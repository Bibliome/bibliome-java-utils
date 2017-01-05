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

package org.bibliome.util.notation;


abstract class AbstractTokenConsumer implements ParserState {
	@Override
	public void consumeCharacter(NotationParser parser, char c) throws NotationParseException {
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
			consumeMap(parser, c);
			return;
		}
		if (c == syntax.getQuote1() || c == syntax.getQuote2()) {
			consumeQuote(parser, c);
			return;
		}
		if (c == Syntax.escape()) {
			consumeEscape(parser);
			return;
		}
		if (Syntax.isWhitespace(c)) {
			consumeWhitespace(parser, c);
			return;
		}
		consumeOther(parser, c);
	}

	@Override
	public final void consumeEOF(NotationParser parser) throws NotationParseException {
		finishLine(parser);
	}

	protected abstract void finishLine(NotationParser parser) throws NotationParseException;
	protected abstract void consumeMap(NotationParser parser, char c) throws NotationParseException;
	protected abstract void consumeQuote(NotationParser parser, char c) throws NotationParseException;
	protected abstract void consumeEscape(NotationParser parser) throws NotationParseException;
	protected abstract void consumeWhitespace(NotationParser parser, char c) throws NotationParseException;
	protected abstract void consumeOther(NotationParser parser, char c) throws NotationParseException;
}
