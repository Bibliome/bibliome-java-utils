package org.bibliome.util.notation;


class ParserState_Map extends AbstractTokenConsumer {
	private final String key;
	
	private ParserState_Map(String key) {
		super();
		this.key = key;
	}

	@Override
	protected void finishLine(NotationParser parser) throws NotationParseException {
	}

	@Override
	protected void consumeMap(NotationParser parser, char c) throws NotationParseException {
		throw parser.unexpectedCharacter(c);
	}

	@Override
	protected void consumeQuote(NotationParser parser, char c) throws NotationParseException {
		parser.setParserState(ParserState_Quoted.getInstance(c, key));
	}

	@Override
	protected void consumeEscape(NotationParser parser) throws NotationParseException {
		parser.setParserState(ParserState_Unquoted.getInstance(key, parser, Syntax.escape()));
	}

	@Override
	protected void consumeWhitespace(NotationParser parser, char c) throws NotationParseException {
	}

	@Override
	protected void consumeOther(NotationParser parser, char c) throws NotationParseException {
		parser.setParserState(ParserState_Unquoted.getInstance(key, parser, c));
	}

	static ParserState_Map getInstance(String key) {
		return new ParserState_Map(key);
	}
}
