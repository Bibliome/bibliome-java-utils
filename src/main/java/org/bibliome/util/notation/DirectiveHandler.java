package org.bibliome.util.notation;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DirectiveHandler implements NotationHandler {
	private final NotationHandler delegate;
	
	public DirectiveHandler(NotationHandler delegate) {
		super();
		this.delegate = delegate;
	}

	@Override
	public void closeList(NotationParser parser) throws NotationParseException {
		delegate.closeList(parser);
	}

	@Override
	public void openUnmappedList(NotationParser parser) throws NotationParseException {
		delegate.openUnmappedList(parser);
	}

	@Override
	public void openMappedList(NotationParser parser, String key) throws NotationParseException {
		delegate.openMappedList(parser, key);
	}

	@Override
	public void addStringValue(NotationParser parser, String value) throws NotationParseException {
		delegate.addStringValue(parser, value);
	}
	
	private static final Pattern NORMALIZE_DIRECTIVE = Pattern.compile("[-_ ]");
	
	private static String normalizeDirective(String directive) {
		Matcher m = NORMALIZE_DIRECTIVE.matcher(directive.toLowerCase());
		return m.replaceAll("");
	}

	@Override
	public void directive(NotationParser parser, String directive) throws NotationParseException {
		switch (normalizeDirective(directive)) {
			case "variable":
			case "variables":
			case "var":
			case "vars": {
				VariablesDirective var = new VariablesDirective(parser.getVariables());
				parser.push();
				parser.setHandler(var);
				break;
			}
			case "syntax": {
				SyntaxDirective syn = new SyntaxDirective(parser.getSyntax());
				parser.push();
				parser.setHandler(syn);
				break;
			}
			default: {
				throw parser.unknownDirective(directive);
			}
		}
	}
	
	private static class VariablesDirective implements NotationHandler {
		private final Map<String,String> variables;
		private String key = null;

		private VariablesDirective(Map<String,String> variables) {
			super();
			this.variables = variables;
		}

		@Override
		public void closeList(NotationParser parser) {
			key = null;
		}

		@Override
		public void openUnmappedList(NotationParser parser) throws NotationParseException {
			throw parser.parseError("what variable?");
		}

		@Override
		public void openMappedList(NotationParser parser, String key) throws NotationParseException {
			if (this.key != null) {
				throw parser.parseError("misplaced variable " + key + " (was expecting value for " + this.key + ")");
			}
			this.key = key;
		}

		@Override
		public void addStringValue(NotationParser parser, String value) {
			if (!variables.containsKey(key)) {
				if (key == null) {
					variables.put(value, "");
				}
				else {
					variables.put(key, value);
				}
			}
		}

		@Override
		public void directive(NotationParser parser, String directive) throws NotationParseException {
			throw misplacedDirective(parser, directive);
		}
	}
	
	private static class SyntaxDirective implements NotationHandler {
		private final Syntax syntax;
		private String key;
		
		private SyntaxDirective(Syntax syntax) {
			super();
			this.syntax = syntax;
		}

		@Override
		public void closeList(NotationParser parser) {
			key = null;
		}

		@Override
		public void openUnmappedList(NotationParser parser) throws NotationParseException {
			throw parser.parseError("what syntax?");
		}

		@Override
		public void openMappedList(NotationParser parser, String key) throws NotationParseException {
			if (this.key != null) {
				throw parser.parseError("what syntax?");
			}
			this.key = key;			
		}
		
		private static char getChar(NotationParser parser, String value) throws NotationParseException {
			if (value.isEmpty()) {
				throw parser.parseError("empty string");
			}
			return value.charAt(0);
		}

		@Override
		public void addStringValue(NotationParser parser, String value) throws NotationParseException {
			if (key == null) {
				throw parser.parseError("what syntax?");
			}
			switch (normalizeDirective(key)) {
				case "map":
				case "map1": {
					syntax.setMap1(getChar(parser, value));
					break;
				}
				case "map2":
				case "altmap": {
					syntax.setMap2(getChar(parser, value));
					break;
				}
				case "quote":
				case "quote1": {
					syntax.setQuote1(getChar(parser, value));
					break;
				}
				case "quote2":
				case "altquote": {
					syntax.setQuote2(getChar(parser, value));
					break;
				}
				case "var":
				case "variable": {
					syntax.setVariable(getChar(parser, value));
					break;
				}
				case "comment": {
					syntax.setComment(getChar(parser, value));
					break;
				}
				case "directive": {
					syntax.setDirective(getChar(parser, value));
					break;
				}
				case "tab":
				case "tabwidth": {
					try {
						syntax.setTabWidth(Integer.parseInt(value));
					}
					catch (NumberFormatException e) {
						throw parser.parseError(e.getMessage());
					}
					break;
				}
				default:
					throw parser.parseError("unkown syntax element " + key);
			}
		}

		@Override
		public void directive(NotationParser parser, String directive) throws NotationParseException {
			throw misplacedDirective(parser, directive);
		}
	}
	
	private static NotationParseException misplacedDirective(NotationParser parser, String directive) {
		return parser.parseError("misplaced directive " + directive);
	}
}
