package org.bibliome.util.notation;


class ParserState_Unquoted extends AbstractStringParserState {
	private final boolean directive;
	
	private ParserState_Unquoted(String key, NotationParser parser, char c) throws NotationParseException {
		super(key);
		directive = key == null && c == parser.getSyntax().getDirective();
		if (!directive) {
			consumeCharacter(parser, c);
		}
	}

	@Override
	protected boolean allowNL() {
		return false;
	}

	private void finishLine(NotationParser parser) throws NotationParseException {
		String s = contents.toString().trim();
		parser.addStringValue(parser, s);
	}

	@Override
	public void consumeEOF(NotationParser parser) throws NotationParseException {
		finishLine(parser);
	}

	@Override
	protected void consumeCharacterWithoutEscape(NotationParser parser, char c) throws NotationParseException {
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
			if (key == null) {
				String s = contents.toString().trim();
				if (directive) {
					parser.directive(parser, s);
				}
				else {
					parser.openMappedList(parser, s);
				}
				parser.setParserState(ParserState_Map.getInstance(s));
				return;
			}
			contents.append(c);
			return;
		}
		contents.append(c);
	}

	static ParserState_Unquoted getInstance(String key, NotationParser parser, char c) throws NotationParseException {
		return new ParserState_Unquoted(key, parser, c);
	}
}
