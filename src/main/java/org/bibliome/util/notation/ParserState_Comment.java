package org.bibliome.util.notation;


class ParserState_Comment implements ParserState {
	private static final ParserState_Comment INSTANCE = new ParserState_Comment();
	
	private ParserState_Comment() {
		super();
	}
	
	@Override
	public void consumeCharacter(NotationParser parser, char c) throws NotationParseException {
		if (c == Syntax.getNl()) {
			parser.setParserState(ParserState_Indent.getInstance());
		}
	}

	@Override
	public void consumeEOF(NotationParser parser) throws NotationParseException {
	}

	static ParserState_Comment getInstance() {
		return INSTANCE;
	}
}
