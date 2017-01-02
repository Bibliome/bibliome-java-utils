package org.bibliome.util.notation;



class ParserState_Indent extends AbstractTokenConsumer {
	private int indent = 0;

	private ParserState_Indent() {
		super();
	}

	@Override
	protected void finishLine(NotationParser parser) throws NotationParseException {
	}

	@Override
	protected void consumeMap(NotationParser parser, char c) throws NotationParseException {
		parser.checkIndent(indent);
		parser.openUnmappedList(parser);
		parser.setParserState(ParserState_Finish.getInstance());
	}

	@Override
	protected void consumeQuote(NotationParser parser, char c) throws NotationParseException {
		parser.checkIndent(indent);
		parser.setParserState(ParserState_Quoted.getInstance(c, null));
	}

	@Override
	protected void consumeEscape(NotationParser parser) throws NotationParseException {
		parser.checkIndent(indent);
		parser.setParserState(ParserState_Unquoted.getInstance(null, parser, Syntax.escape()));
	}
	
	private static int getWhitespaceWidth(NotationParser parser, char c) {
		if (c == Syntax.getTab()) {
			return parser.getSyntax().getTabWidth();
		}
		return 1;
	}

	@Override
	protected void consumeWhitespace(NotationParser parser, char c) throws NotationParseException {
		indent += getWhitespaceWidth(parser, c);
	}

	@Override
	protected void consumeOther(NotationParser parser, char c) throws NotationParseException {
		parser.checkIndent(indent);
		parser.setParserState(ParserState_Unquoted.getInstance(null, parser, c));
	}

	static ParserState getInstance() {
		return new ParserState_Indent();
	}
}
