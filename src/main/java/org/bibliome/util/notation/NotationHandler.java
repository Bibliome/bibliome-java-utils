package org.bibliome.util.notation;


public interface NotationHandler {
	void closeList(NotationParser parser) throws NotationParseException;
	void openUnmappedList(NotationParser parser) throws NotationParseException;
	void openMappedList(NotationParser parser, String key) throws NotationParseException;
	void addStringValue(NotationParser parser, String value) throws NotationParseException;
	void directive(NotationParser parser, String directive) throws NotationParseException;
}
