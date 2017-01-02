package org.bibliome.util.bionlpst;

import java.io.PrintStream;
import java.util.Map;

import org.bibliome.util.filters.Filter;
import org.bibliome.util.fragments.Fragment;



public enum BioNLPSTSerializer implements BioNLPSTAnnotationVisitor<Void,PrintStream> {
	INSTANCE;
	
	private static void prelude(PrintStream out, BioNLPSTAnnotation annotation) {
		out.print(annotation.getId());
		out.print('\t');
		out.print(annotation.getType());
	}
	
	@Override
	public Void visit(TextBound textBound, PrintStream param) {
		prelude(param, textBound);
		param.print(' ');
		boolean notFirst = false;
		for (Fragment frag : textBound.getFragments()) {
			if (notFirst)
				param.print(';');
			else
				notFirst = true;
			param.print(frag.getStart());
			param.print(' ');
			param.print(frag.getEnd());
		}
		param.print('\t');
		String text = textBound.getDocument().getText();
		notFirst = false;
		for (Fragment frag : textBound.getFragments()) {
			if (notFirst)
				param.print(' ');
			else
				notFirst = true;
			param.print(text.substring(frag.getStart(), frag.getEnd()));
		}
		return null;
	}

	@Override
	public Void visit(BioNLPSTRelation relation, PrintStream param) {
		prelude(param, relation);
		printArgs(param, relation);
		return null;
	}
	
	private static void printArgs(PrintStream out, AnnotationWithArgs annotation) {
		Map<String,String> argIds = annotation.getArgumentIds();
		for (Map.Entry<String,String> e : argIds.entrySet()) {
			out.print(' ');
			out.print(e.getKey());
			out.print(':');
			out.print(e.getValue());
		}
	}

	@Override
	public Void visit(Event event, PrintStream param) {
		prelude(param, event);
		param.print(':');
		param.print(event.getTriggerId());
		printArgs(param, event);
		return null;
	}

	@Override
	public Void visit(Normalization normalization, PrintStream param) {
		prelude(param, normalization);
		param.print(" Annotation:");
		param.print(normalization.getAnnotationId());
		param.print(" Referent:");
		param.print(normalization.getReferent());
		return null;
	}

	@Override
	public Void visit(Modification modification, PrintStream param) {
		prelude(param, modification);
		param.print(' ');
		param.print(modification.getAnnotationId());
		return null;
	}
	
	public static void print(PrintStream out, Equivalence equiv) {
		out.print("*\tEquiv");
		for (String id : equiv.getAnnotationIds()) {
			out.print(' ');
			out.print(id);
		}
	}
	
	public static void print(PrintStream out, BioNLPSTDocument doc, Filter<BioNLPSTAnnotation> annotationFilter, Filter<Equivalence> equivalenceFilter) {
		for (BioNLPSTAnnotation annotation : doc.getAnnotations()) {
			if (annotationFilter.accept(annotation)) {
				annotation.accept(INSTANCE, out);
				out.println();
			}
		}
		for (Equivalence equiv : doc.getEquivalences()) {
			if (equivalenceFilter.accept(equiv)) {
				print(out, equiv);
				out.println();
			}
		}
	}
}
