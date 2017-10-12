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
