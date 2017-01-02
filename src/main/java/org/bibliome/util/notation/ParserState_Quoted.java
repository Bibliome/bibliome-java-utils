package org.bibliome.util.notation;



class ParserState_Quoted extends AbstractStringParserState {
	private final char quote;
	
	private ParserState_Quoted(char quote, String key) {
		super(key);
		this.quote = quote;
	}

	@Override
	public void consumeEOF(NotationParser parser) throws NotationParseException {
		throw parser.parseError(String.format("unfinished '%c'-quoted string", quote));
	}

	@Override
	protected boolean allowNL() {
		return true;
	}

	@Override
	protected void consumeCharacterWithoutEscape(NotationParser parser, char c) throws NotationParseException {
		if (c == quote) {
			String s = contents.toString();
			if (key == null) {
				parser.setParserState(ParserState_Key.getInstance(s));
				return;
			}
			parser.addStringValue(parser, s);
			parser.setParserState(ParserState_Finish.getInstance());
			return;
		}
		contents.append(c);
	}

	static ParserState_Quoted getInstance(char quote, String key) {
		return new ParserState_Quoted(quote, key);
	}
}
