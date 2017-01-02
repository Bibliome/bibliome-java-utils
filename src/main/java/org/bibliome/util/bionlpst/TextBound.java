package org.bibliome.util.bionlpst;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.bibliome.util.fragments.Fragment;
import org.bibliome.util.fragments.SimpleFragment;

public class TextBound extends BioNLPSTAnnotation {
	private final List<Fragment> fragments = new ArrayList<Fragment>();

	public TextBound(String source, int lineno, BioNLPSTDocument document, Visibility visibility, String id, String type) throws BioNLPSTException {
		super(source, lineno, document, visibility, id, type);
	}
	
	public List<Fragment> getFragments() {
		return Collections.unmodifiableList(fragments);
	}

	@Override
	public void resolveIds() {
	}
	
	void addFragment(Fragment frag) throws BioNLPSTException {
		if (!fragments.isEmpty()) {
			Fragment last = fragments.get(fragments.size() - 1);
			if (frag.getStart() < last.getEnd())
				error("overlapping fragments");
		}
		fragments.add(new SimpleFragment(frag.getStart(), frag.getEnd()));
	}

	@Override
	public <R,P> R accept(BioNLPSTAnnotationVisitor<R,P> visitor, P param) {
		return visitor.visit(this, param);
	}

	@Override
	public AnnotationKind getKind() {
		return AnnotationKind.TEXT_BOUND;
	}
}
