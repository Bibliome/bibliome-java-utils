package org.bibliome.util.notation;


interface ParserState {
	void consumeCharacter(NotationParser parser, char c) throws NotationParseException;
	void consumeEOF(NotationParser parser) throws NotationParseException;
}
