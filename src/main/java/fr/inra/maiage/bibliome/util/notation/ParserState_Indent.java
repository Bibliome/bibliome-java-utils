/*
Copyright 2016, 2017 Institut National de la Recherche Agronomique

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

        http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
*/

package fr.inra.maiage.bibliome.util.notation;



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
