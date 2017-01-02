package org.bibliome.util.yatea.pattern;

public interface SubtermVisitor<R,P> {
	R visit(Chunk chunk, P param);
	R visit(Word word, P param);
}
