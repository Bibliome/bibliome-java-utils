package fr.inra.maiage.bibliome.util.alvisae;

import java.io.PrintStream;

import fr.inra.maiage.bibliome.util.fragments.Fragment;

public class PrintAnnotation implements AnnotationVisitor<Void,String> {
	private final PrintStream out;
	private final int formWindowSize;

	public PrintAnnotation(PrintStream out, int formWindowSize) {
		super();
		this.out = out;
		this.formWindowSize = formWindowSize;
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
			int start = frag.getStart();
			int end = frag.getEnd();
			out.format("%s%d-%d (%.2f-%.2f) \"%s\"\n", indent, start, end, (start / len), (end / len), contents.substring(start, end));
			if (formWindowSize > 0) {
				int before = Math.max(0, start - formWindowSize);
				int after = Math.min((int) len, end + formWindowSize);
				out.format("%s%s\u001B[31m%s\u001B[0m%s\n", indent, contents.substring(before, start), contents.substring(start, end), contents.substring(end, after));
			}
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
}
