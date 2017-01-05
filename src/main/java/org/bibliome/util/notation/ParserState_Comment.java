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
