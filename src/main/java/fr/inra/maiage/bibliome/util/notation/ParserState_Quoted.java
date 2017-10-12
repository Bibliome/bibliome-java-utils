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
