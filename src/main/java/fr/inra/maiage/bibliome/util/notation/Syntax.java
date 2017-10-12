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

public class Syntax {
	private static final char TAB = '\t';
	private static final char NL = '\n';
	private static final char ESCAPE = '\\';
	private static final char ESCAPE_NL = 'n';
	private static final char ESCAPE_TAB = 't';

	private int tabWidth = 8;
	private char map1 = ':';
	private char map2 = '=';
	private char quote1 = '"';
	private char quote2 = '\'';
	private char variable = '~';
	private char comment = '#';
	private char directive = '%';
	
	public Syntax() {
		super();
	}
	
	private Syntax(int tabWidth, char map1, char map2, char quote1, char quote2, char variable, char comment, char directive) {
		super();
		this.tabWidth = tabWidth;
		this.map1 = map1;
		this.map2 = map2;
		this.quote1 = quote1;
		this.quote2 = quote2;
		this.variable = variable;
		this.comment = comment;
		this.directive = directive;
	}

	public Syntax(Syntax syntax) {
		this(syntax.tabWidth, syntax.map1, syntax.map2, syntax.quote1, syntax.quote2, syntax.variable, syntax.comment, syntax.directive);
	}

	public int getTabWidth() {
		return tabWidth;
	}

	public char getMap1() {
		return map1;
	}

	public char getMap2() {
		return map2;
	}

	public char getQuote1() {
		return quote1;
	}

	public char getQuote2() {
		return quote2;
	}

	public char getVariable() {
		return variable;
	}

	public char getComment() {
		return comment;
	}

	public char getDirective() {
		return directive;
	}

	public void setDirective(char directive) {
		this.directive = directive;
	}

	public void setMap1(char map1) {
		this.map1 = map1;
	}

	public void setMap2(char map2) {
		this.map2 = map2;
	}

	public void setQuote1(char quote1) {
		this.quote1 = quote1;
	}

	public void setQuote2(char quote2) {
		this.quote2 = quote2;
	}

	public void setVariable(char variable) {
		this.variable = variable;
	}

	public void setComment(char comment) {
		this.comment = comment;
	}

	public void setTabWidth(int tabWidth) {
		this.tabWidth = tabWidth;
	}

	public static char getTab() {
		return TAB;
	}

	public static char getNl() {
		return NL;
	}
	
	public static char escape() {
		return ESCAPE;
	}
	
	public static char escapeNL() {
		return ESCAPE_NL;
	}
	
	public static char escapeTab() {
		return ESCAPE_TAB;
	}
	
	public static boolean isWhitespace(char c) {
		return Character.isWhitespace(c);
	}

	public String printable(String s, boolean key) {
		if (s.isEmpty()) {
			StringBuilder sb = new StringBuilder(2);
			sb.append(getQuote1());
			sb.append(getQuote1());
			return sb.toString();
		}
		int nl = s.indexOf(getNl());
		boolean trim = isWhitespace(s.charAt(0)) || isWhitespace(s.charAt(s.length() - 1));
		if (nl != -1 || trim) {
			return quotedString(s);
		}
		return unquotedString(s, key);
	}
	
	private char choseQuote(String s) {
		final char q1 = getQuote1();
		final char q2 = getQuote2();
		int n1 = 0;
		int n2 = 0;
		final int len = s.length();
		for (int i = 0; i < len; ++i) {
			char c = s.charAt(i);
			if (c == q1) {
				++n1;
			}
			else if (c == q2) {
				++n2;
			}
		}
		if (n1 <= n2) {
			return q1;
		}
		return q2;
	}

	private String quotedString(String s) {
		StringBuilder sb = new StringBuilder();
		final char quote = choseQuote(s);
		sb.append(quote);
		final int len = s.length();
		for (int i = 0; i < len; ++i) {
			char c = s.charAt(i);
			if (c == escape()) {
				sb.append(escape());
				sb.append(escape());
				continue;
			}
			if (c == getNl()) {
				sb.append(escape());
				sb.append(escapeNL());
				continue;
			}
			if (c == getTab()) {
				sb.append(escape());
				sb.append(escapeTab());
				continue;
			}
			if (c == quote) {
				sb.append(quote);
			}
			sb.append(c);
		}
		sb.append(quote);
		return sb.toString();
	}

	private String unquotedString(String s, boolean key) {
		StringBuilder sb = new StringBuilder();
		final int len = s.length();
		for (int i = 0; i < len; ++i) {
			char c = s.charAt(i);
			if (c == getComment()) {
				sb.append(escape());
				sb.append(getComment());
				continue;
			}
			if (c == escape()) {
				sb.append(escape());
				sb.append(escape());
				continue;
			}
			if (c == getMap1() || c == getMap2()) {
				if (key) {
					sb.append(escape());
				}
				sb.append(c);
				continue;
			}
			if (c == getNl()) {
				sb.append(escape());
				sb.append(escapeNL());
				continue;
			}
			if (c == getTab()) {
				sb.append(escape());
				sb.append(escapeTab());
				continue;
			}
			if (c == getQuote1() || c == getQuote2()) {
				if (i == 0) {
					sb.append(escape());
				}
				sb.append(c);
				continue;
			}
			sb.append(c);
		}
		return sb.toString();
	}
}
