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
