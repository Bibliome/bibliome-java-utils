package org.bibliome.util.notation;


class ParserState_Finish extends AbstractTokenConsumer {
	private static final ParserState_Finish INSTANCE = new ParserState_Finish();
	
	private ParserState_Finish() {
		super();
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

	static ParserState_Finish getInstance() {
		return INSTANCE;
	}
}
