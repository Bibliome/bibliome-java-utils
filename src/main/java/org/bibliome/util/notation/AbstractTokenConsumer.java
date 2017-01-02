package org.bibliome.util.notation;


abstract class AbstractTokenConsumer implements ParserState {
	@Override
	public void consumeCharacter(NotationParser parser, char c) throws NotationParseException {
		Syntax syntax = parser.getSyntax();
		if (c == Syntax.getNl()) {
			finishLine(parser);
			parser.setParserState(ParserState_Indent.getInstance());
			return;
		}
		if (c == syntax.getComment()) {
			finishLine(parser);
			parser.setParserState(ParserState_Comment.getInstance());
			return;
		}
		if (c == syntax.getMap1() || c == syntax.getMap2()) {
			consumeMap(parser, c);
			return;
		}
		if (c == syntax.getQuote1() || c == syntax.getQuote2()) {
			consumeQuote(parser, c);
			return;
		}
		if (c == Syntax.escape()) {
			consumeEscape(parser);
			return;
		}
		if (Syntax.isWhitespace(c)) {
			consumeWhitespace(parser, c);
			return;
		}
		consumeOther(parser, c);
	}

	@Override
	public final void consumeEOF(NotationParser parser) throws NotationParseException {
		finishLine(parser);
	}

	protected abstract void finishLine(NotationParser parser) throws NotationParseException;
	protected abstract void consumeMap(NotationParser parser, char c) throws NotationParseException;
	protected abstract void consumeQuote(NotationParser parser, char c) throws NotationParseException;
	protected abstract void consumeEscape(NotationParser parser) throws NotationParseException;
	protected abstract void consumeWhitespace(NotationParser parser, char c) throws NotationParseException;
	protected abstract void consumeOther(NotationParser parser, char c) throws NotationParseException;
}
