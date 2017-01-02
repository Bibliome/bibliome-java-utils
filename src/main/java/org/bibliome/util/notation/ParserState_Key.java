package org.bibliome.util.notation;


class ParserState_Key extends AbstractTokenConsumer {
	private final String key;
	
	private ParserState_Key(String key) {
		super();
		this.key = key;
	}

	@Override
	protected void finishLine(NotationParser parser) throws NotationParseException {
		parser.addStringValue(parser, key);
	}

	@Override
	protected void consumeMap(NotationParser parser, char c) throws NotationParseException {
		parser.openMappedList(parser, key);
		parser.setParserState(ParserState_Map.getInstance(key));
	}

	@Override
	protected void consumeQuote(NotationParser parser, char c) throws NotationParseException {
		throw parser.unexpectedCharacter(c);
	}

	@Override
	protected void consumeEscape(NotationParser parser) throws NotationParseException {
		throw parser.unexpectedCharacter(Syntax.escape());
	}

	@Override
	protected void consumeWhitespace(NotationParser parser, char c) throws NotationParseException {
	}

	@Override
	protected void consumeOther(NotationParser parser, char c) throws NotationParseException {
		throw parser.unexpectedCharacter(c);
	}

	static ParserState_Key getInstance(String key) {
		return new ParserState_Key(key);
	}
}
