package fr.inra.maiage.bibliome.util.alvisae;

import java.io.PrintStream;

import fr.inra.maiage.bibliome.util.fragments.Fragment;

public class PrintAnnotation implements AnnotationVisitor<Void,String> {
	private final PrintStream out;

	public PrintAnnotation(PrintStream out) {
		super();
		this.out = out;
	}
	
	private String preamble(AlvisAEAnnotation ann, String indent, String kind) {
		out.format("%s%s %s (%s)\n", indent, kind, ann.getType(), ann.getId());
		return indent + "    ";
	}
	
	@Override
	public Void visit(TextBound tb, String indent) {
		indent = preamble(tb, indent, "Text-Bound");
		String contents = tb.getDocument().getContents();
		float len = contents.length();
		for (Fragment frag : tb.getFragments()) {
			out.format("%s%d-%d (%.2f-%.2f) \"%s\"\n", indent, frag.getStart(), frag.getEnd(), (frag.getStart() / len), (frag.getEnd() / len), contents.substring(frag.getStart(), frag.getEnd()));
		}
		return null;
	}

	@Override
	public Void visit(Group grp, String indent) {
		indent = preamble(grp, indent, "Group");
		for (AlvisAEAnnotation item : grp.getItems()) {
			item.accept(this, indent);
		}
		return null;
	}

	@Override
	public Void visit(Relation rel, String indent) {
		indent = preamble(rel, indent, "Relation");
		for (String role : rel.getRoles()) {
			out.format("%s%s\n", indent, role);
			rel.getArgument(role).accept(this, indent);
		}
		return null;
	}
	
	public static void print(PrintStream out, AlvisAEAnnotation ann, String indent) {
		ann.accept(new PrintAnnotation(out), indent);
	}
	
	public static void print(PrintStream out, AlvisAEAnnotation ann) {
		print(out, ann, "");
	}
	
	public static void print(AlvisAEAnnotation ann, String indent) {
		print(System.out, ann, indent);
	}
	
	public static void print(AlvisAEAnnotation ann) {
		print(System.out, ann, "");
	}
}
