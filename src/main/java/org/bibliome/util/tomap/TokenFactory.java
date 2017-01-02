package org.bibliome.util.tomap;

import java.util.HashMap;

import org.bibliome.util.defaultmap.DefaultMap;

public class TokenFactory  {
	private final DefaultMap<Token,Token> map = new DefaultMap<Token,Token>(true, new HashMap<Token,Token>()) {
		@Override
		protected Token defaultValue(Token key) {
			return key;
		}
	};

	public Token getToken(Token token) {
		return map.safeGet(token);
	}
}
