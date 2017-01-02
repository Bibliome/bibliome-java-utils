package org.bibliome.util.notation;


abstract class AbstractStringParserState implements ParserState {
	protected final StringBuilder contents = new StringBuilder();
	protected boolean escape = false;
	protected final String key;
	private StringBuilder variable = null;

	protected AbstractStringParserState(String key) {
		super();
		this.key = key;
	}

	@Override
	public void consumeCharacter(NotationParser parser, char c) throws NotationParseException {
		Syntax syntax = parser.getSyntax();
		if (variable != null) {
			if (escape) {
				variable.append(c);
				escape = false;
				return;
			}
			if (c == Syntax.escape()) {
				escape = true;
				return;
			}
			if (c == syntax.getVariable()) {
				String var = variable.toString();
				contents.append(parser.getVariableValue(var));
				variable = null;
				return;
			}
			variable.append(c);
			return;
		}
		if (escape) {
			consumeEscapedCharacter(parser, c);
			return;
		}
		if (c == Syntax.escape()) {
			escape = true;
			return;
		}
		if (c == syntax.getVariable()) {
			variable = new StringBuilder();
			return;
		}
		consumeCharacterWithoutEscape(parser, c);
	}
	
	private void consumeEscapedCharacter(NotationParser parser, char c) throws NotationParseException {
		if (c == Syntax.escapeNL()) {
			if (!allowNL()) {
				throw parser.unexpectedCharacter(Syntax.escape());
			}
			contents.append(Syntax.getNl());
		}
		else if (c == Syntax.escapeTab()) {
			contents.append(Syntax.getTab());
		}
		else {
			contents.append(c);
		}
		escape = false;
	}
	
	protected abstract boolean allowNL();
	
	protected abstract void consumeCharacterWithoutEscape(NotationParser parser, char c) throws NotationParseException;
}
