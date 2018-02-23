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


package fr.inra.maiage.bibliome.util;

import java.io.IOException;
import java.lang.Character.UnicodeBlock;
import java.text.Normalizer;
import java.text.Normalizer.Form;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import fr.inra.maiage.bibliome.util.mappers.Mapper;
import fr.inra.maiage.bibliome.util.mappers.Mappers;
import fr.inra.maiage.bibliome.util.mappers.ToStringMapper;

/**
 * Miscellaneous string utilities.
 */
public abstract class Strings {
	public static final char ZWSP = '\u200B';
	
    private static final Map<Character,String> xmlSpecial = new HashMap<Character,String>();
    private static final Map<Character,String> javaSpecial = new HashMap<Character,String>();
    private static final Map<Character,String> latexSpecial = new HashMap<Character,String>();
    private static final Map<Character,String> jsonSpecial = new HashMap<Character,String>();

    static {
        xmlSpecial.put('&', "&amp;");
        xmlSpecial.put('<', "&lt;");
        xmlSpecial.put('"', "&quot;");
        
        javaSpecial.put('\n', "\\n");
        javaSpecial.put('\t', "\\t");
        javaSpecial.put('\r', "\\r");
        javaSpecial.put('\b', "\\b");
        javaSpecial.put('\f', "\\f");
        javaSpecial.put('\'', "\\\'");
        javaSpecial.put('\"', "\\\"");
        javaSpecial.put('\\', "\\\\");
        
        latexSpecial.put('#', "\\#");
        latexSpecial.put('$', "\\$");
        latexSpecial.put('%', "\\%");
        latexSpecial.put('&', "\\&");
        latexSpecial.put('~', "\\~{}");
        latexSpecial.put('_', "\\_");
        latexSpecial.put('^', "\\^{}");
        latexSpecial.put('\\', "\\ensuremath{\\backslash}");
        latexSpecial.put('{', "\\{");
        latexSpecial.put('}', "\\}");
        
        jsonSpecial.put('\n', "\\n");
        jsonSpecial.put('\t', "\\t");
        jsonSpecial.put('\r', "\\r");
        jsonSpecial.put('\b', "\\b");
        jsonSpecial.put('\f', "\\f");
        jsonSpecial.put('\'', "\\\'");
        jsonSpecial.put('\"', "\\\"");
        jsonSpecial.put('\\', "\\\\");
        
    }

    /**
     * Replaces all occurrences of <code>&amp;</code>, <code>&lt;</code> and <code>&quot;</code> with their respective XML entities.
     * @param src
     * @return the converted string
     */
    public static final String escapeXML(String src) {
        return escape(src, xmlSpecial).replace("]]", "))");
    }

    /**
     * Escapes all characters for a java string litteral.
     * @param src
     */
    public static final String escapeJava(String src) {
        return escape(src, javaSpecial);
    }
   
    /**
     * Escapes all characters for a json string litteral.
     * @param src
     */
    public static final String escapeJSON(CharSequence src) {
        return escape(src, jsonSpecial);
    }

    /**
     * Escapes all latex speciel characters.
     * @param src
     */
    public static final String escapeLatex(String src) {
    	return escape(src, latexSpecial);
    }

    /**
     * Replaces all occurrences of characters in the specified mapping keys with the corresponding values.
     * @param src
     * @param special
     * @return the string
     */
    public static final String escape(CharSequence src, Map<Character,String> special) {
        StringBuilder sb = null;
        for (int i = 0; i < src.length(); i++) {
            char c = src.charAt(i);
            if (special.containsKey(c)) {
                if (sb == null)
                    sb = new StringBuilder(src.subSequence(0, i));
                sb.append(special.get(c));
            }
            else {
                if (sb != null)
                    sb.append(c);
            }
        }
        if (sb == null)
            return src.toString();
        return sb.toString();
    }
    
    public static String removeZWSP(String s) {
    	StringBuilder sb = new StringBuilder();
    	boolean foundOne = false;
    	for (int i = 0; i < s.length(); ++i) {
    		char c = s.charAt(i);
    		if (c == ZWSP) {
    			if (!foundOne) {
    				sb.append(s.substring(0, i));
    				foundOne = true;
    			}
    			continue;
    		}
    		if (foundOne) {
    			sb.append(c);
    		}
    	}
    	if (foundOne) {
    		return sb.toString();
    	}
    	return s;
    }

    /**
     * Returns the specified string with leading and trailing white spaces removed and consecutive white spaces replaced with a single space.
     * @param src
     */
    public static final String normalizeSpace(String src, char space) {
        StringBuilder sb = null;
        boolean normalizing = false;
        src = src.trim();
        for (int i = 0; i < src.length(); i++) {
            char c = src.charAt(i);
            if (Character.isWhitespace(c)) {
                if (normalizing)
                    continue;
                if (sb == null)
                    sb = new StringBuilder(src.substring(0, i));
                sb.append(space);
                normalizing = true;
                continue;
            }
            normalizing = false;
            if (sb != null)
                sb.append(c);
        }
        if (sb == null)
            return src;
        return sb.toString();
    }
    
    public static final String normalizeSpace(String src) {
    	return normalizeSpace(src, ' ');
    }
    
    /**
     * Splits the specified string by the specified character.
     * @param s
     * @param sep
     */
    public static final List<String> split(String s, char sep, int limit, List<String> values) {
    	if (values == null)
    		values = new ArrayList<String>();
    	else
    		if (!values.isEmpty())
    			values.clear();
        int last = 0;
        while (true) {
        	if ((limit > 0) && (values.size() == limit - 1))
        		break;
        	int sepIndex = s.indexOf(sep, last);
        	if (sepIndex < 0)
        		break;
        	values.add(new String(s.substring(last, sepIndex)));
        	last = sepIndex + 1;
        }
        values.add(new String(s.substring(last)));
        return values;
    }

    public static final List<String> split(String s, char sep, int limit) {
    	return split(s, sep, limit, new ArrayList<String>());
    }
    
    /**
     * Splits the specified string by the specified character, then trims each piece.
     * @param s
     * @param sep
     */
    public static final List<String> splitAndTrim(String s, char sep, int limit) {
        List<String> result = split(s, sep, limit);
        for (int i = 0; i < result.size(); i++)
            result.set(i, result.get(i).trim());
        return result;
    }

    /**
     * Joins the strings with the specified character.
     * @param str
     * @param sep
     */
    public static final String join(Collection<String> str, char sep) {
        return join(str, Character.toString(sep));
    }

    /**
     * Joins the object string representations with the specified character.
     * @param str
     * @param sep
     */
    public static final <T> String joinStrings(Collection<T> str, char sep) {
        Mapper<T,String> mapper = new ToStringMapper<T>();
        return join(Mappers.apply(mapper, str, new ArrayList<String>()), sep);
    }

    /**
     * Joins the object string representations with the specified character.
     * @param str
     * @param sep
     */
    public static final <T> String joinStrings(T[] str, char sep) {
        Mapper<T,String> mapper = new ToStringMapper<T>();
        return join(Mappers.apply(mapper, Arrays.asList(str), new ArrayList<String>()), sep);
    }
    
    /**
     * Joins the object string representations with the specified separator.
     * @param str
     * @param sep
     */
    public static final <T> String joinStrings(T[] str, String sep) {
        Mapper<T,String> mapper = new ToStringMapper<T>();
        return join(Mappers.apply(mapper, Arrays.asList(str), new ArrayList<String>()), sep);    	
    }
    
    public static final <T> void joinStrings(Appendable a, T[] str, String sep) throws IOException {
        Mapper<T,String> mapper = new ToStringMapper<T>();
        join(a, Mappers.apply(mapper, Arrays.asList(str), new ArrayList<String>()), sep);    	    	
    }
    
    public static final <T> void joinStrings(Appendable a, Collection<T> str, String sep) throws IOException {
        Mapper<T,String> mapper = new ToStringMapper<T>();
        join(a, Mappers.apply(mapper, str, new ArrayList<String>()), sep);    	    	
    }

    /**
     * Joins the strings with the specified string.
     * @param str
     * @param sep
     */
    public static final String join(Collection<String> str, String sep) {
    	StringCat strcat = new StringCat();
        boolean notFirst = false;
        for (String s : str) {
            if (notFirst)
                strcat.append(sep);
            else
                notFirst = true;
            strcat.append(s);
        }
        return strcat.toString();
    }

    /**
     * Joins the object string representations with the specified string.
     * @param str
     * @param sep
     */
    public static final <T> String joinStrings(Collection<T> str, String sep) {
        Mapper<T,String> mapper = new ToStringMapper<T>();
        return join(Mappers.apply(mapper, str, new ArrayList<String>()), sep);
    }
    
    /**
     * Joins the integer string representations with the specified string.
     * @param str
     * @param sep
     */
    public static final <T> String joinStrings(int[] str, String sep) {
    	StringCat strcat = new StringCat();
        boolean notFirst = false;
        for (int i : str) {
            if (notFirst)
                strcat.append(sep);
            else
                notFirst = true;
            strcat.append(Integer.toString(i));
        }
        return strcat.toString();
   }

    /**
     * Joins the strings with the specified string.
     * @param str
     * @param sep
     */
    public static final String join(String[] str, String sep) {
        return join(Arrays.asList(str), sep);
    }

    /**
     * Joins the strings with the specified character.
     * @param str
     * @param sep
     */
    public static final String join(String[] str, char sep) {
        return join(Arrays.asList(str), sep);
    }

    /**
     * Joins the strings with the specified string, prints the result into the specified stream.
     * @param out
     * @param str
     * @param sep
     * @throws IOException 
     */
    public static final void join(Appendable out, Collection<String> str, String sep) throws IOException {
        boolean notFirst = false;
        for (String s : str) {
            if (notFirst)
                out.append(sep);
            else
                notFirst = true;
            out.append(s);
        }
    }

    /**
     * Joins the strings with the specified character, prints the result into the specified stream.
     * @param out
     * @param str
     * @param sep
     * @throws IOException 
     */
    public static final void join(Appendable out, Collection<String> str, char sep) throws IOException {
        join(out, str, Character.toString(sep));
    }

    /**
     * Joins the strings with the specified string, prints the result into the specified stream.
     * @param out
     * @param str
     * @param sep
     * @throws IOException 
     */
    public static final void join(Appendable out, String[] str, String sep) throws IOException {
        join(out, Arrays.asList(str), sep);
    }

    /**
     * Joins the strings with the specified character, prints the result into the specified stream.
     * @param out
     * @param str
     * @param sep
     * @throws IOException 
     */
    public static final void join(Appendable out, String[] str, char sep) throws IOException {
        join(out, Arrays.asList(str), sep);
    }

    /**
     * Replace the portion between start and end in s by r.
     * @param s
     * @param start
     * @param end
     * @param r
     */
    public static final String replace(String s, int start, int end, String r) {
        StringBuilder sb = new StringBuilder(s);
        sb.replace(start, end, r);
        return sb.toString();
    }

    /**
     * Replace occurrences of variable references by their value.
     * @param src
     * @param vars
     * @param vp
     */
    public static final String replaceVars(String src, Map<String,String> vars, Pattern vp) {
        Matcher m = vp.matcher(src);
        StringBuffer sb = new StringBuffer();
        while (m.find()) {
            String v = m.group(1);
            String r = vars.containsKey(v) ? vars.get(v) : m.group();
            m.appendReplacement(sb, r);
        }
        m.appendTail(sb);
        return sb.toString();
    }

    /**
     * Replace occurrences of variable references by their value.
     * @param src
     * @param vars
     * @param vp
     */
    public static final String replaceVars(String src, Map<String,String> vars, String vp) {
        return replaceVars(src, vars, Pattern.compile(vp));
    }
    
    private static final Map<String,Boolean> booleanMap = new HashMap<String,Boolean>();

    static {
        booleanMap.put("true", true);
        booleanMap.put("yes", true);
        booleanMap.put("false", false);
        booleanMap.put("no", false);
    }
    
    /**
     * Converts the specified string into a boolean.
     * "yes" or "true" -> true.
     * "no" or "false" -> false.
     * @param s
     * @throws IllegalArgumentException
     */
    public static final boolean getBoolean(String s) throws IllegalArgumentException {
        if (booleanMap.containsKey(s))
            return booleanMap.get(s);
        throw new IllegalArgumentException();
    }

    /**
     * Converts the specified string into a boolean.
     * If the specified string is not a valid boolean value, then returns the specified default value.
     * @param s
     * @param defaultValue
     * @return true if the specified string equals "true" or "yes", false if the specified string equals "false" or "no", otherwise defaultValue.
     */
    public static final boolean getBoolean(String s, boolean defaultValue) {
        if (booleanMap.containsKey(s))
            return booleanMap.get(s);
        return defaultValue;
    }

    /**
     * Returns true if all characters in the specified string are digits.
     * @param s
     */
	public static boolean allDigits(String s) {
		for (int i = 0; i < s.length(); ++i)
			if (!Character.isDigit(s.charAt(i)))
				return false;
		return true;
	}

	/**
	 * Converts the specified string into an integer, or returns the specified default value if it could not be converted.
	 * @param s
	 * @param defaultInteger
	 */
	public static final int getInteger(String s, int defaultInteger) {
		if (s == null)
			return defaultInteger;
		try {
			return Integer.parseInt(s);
		}
		catch (NumberFormatException nfe) {
			return defaultInteger;
		}
	}

	public static final double getDouble(String s, double defaultDouble) {
		if (s == null)
			return defaultDouble;
		try {
			return Double.parseDouble(s);
		}
		catch (NumberFormatException nfe) {
			return defaultDouble;
		}
	}

	/**
	 * Computes the Levenshtein edit distance between the specified strings.
	 * @param a
	 * @param b
	 */
	public static final int levenshtein(CharSequence a, CharSequence b) {
		final int aLen = a.length();
		final int bLen = b.length();
		final int[][] m = new int[aLen + 1][bLen + 1];
		for (int i = 0; i <= aLen; ++i)
			m[i][0] = i;
		for (int j = 0; j <= bLen; ++j)
			m[0][j] = j;
		for (int i = 1; i <= aLen; ++i)
			for (int j = 1; j <= bLen; ++j)
				if (a.charAt(i - 1) == b.charAt(j - 1))
					m[i][j] = m[i - 1][j - 1];
				else
					m[i][j] = 1 + Math.min(m[i - 1][j - 1], Math.min(m[i][j - 1], m[i - 1][j]));
		return m[aLen][bLen];
	}
	
	public static final boolean levenshteinSimilar(CharSequence a, CharSequence b, double threshold) {
		final int aLen = a.length();
		final int bLen = b.length();
		final double maxLen = Math.max(aLen, bLen);
		final int lenDiff = Math.abs(aLen - bLen);
		if ((lenDiff / maxLen) > threshold) {
			return false;
		}
		final int[][] m = new int[aLen + 1][bLen + 1];
		for (int i = 0; i <= aLen; ++i)
			m[i][0] = i;
		for (int j = 0; j <= bLen; ++j)
			m[0][j] = j;
		for (int i = 1; i <= aLen; ++i)
			for (int j = 1; j <= bLen; ++j)
				if (a.charAt(i - 1) == b.charAt(j - 1))
					m[i][j] = m[i - 1][j - 1];
				else
					m[i][j] = 1 + Math.min(m[i - 1][j - 1], Math.min(m[i][j - 1], m[i - 1][j]));
		return (m[aLen][bLen] / maxLen) <= threshold;
	}
	
	/**
	 * Replaces whitespaces in the specified string.
	 * @param sb
	 * @param s
	 * @param more
	 * @param rep
	 */
	public static final void escapeWhitespaces(StringBuilder sb, CharSequence s, char more, char rep) {
		for (int i = 0; i < s.length(); ++i) {
			char c = s.charAt(i);
			sb.append(Character.isWhitespace(c) || c == more ? rep : c);
		}
	}
	
	/**
	 * Replaces whitespaces with underscores in the specified string.
	 * @param sb
	 * @param s
	 * @param more
	 */
	public static final void escapeWhitespaces(StringBuilder sb, CharSequence s, char more) {
		escapeWhitespaces(sb, s, more, '_');
	}
	
	/**
	 * Replaces whitespaces in the specified string.
	 * @param sb
	 * @param s
	 */
	public static final void escapeWhitespaces(StringBuilder sb, CharSequence s) {
		escapeWhitespaces(sb, s, '_');
	}
	
	public static final String NEWLINE = String.format("%n");

	/**
	 * Remove diacritics from the specified string.
	 * @param s
	 * @return a copy of the specified string with diacritics removed.
	 */
	public static final String removeDiacritics(String s) {
		String n = Normalizer.normalize(s, Form.NFD);
		StringBuilder sb = null;
		for (int i = 0; i < n.length(); ++i) {
			char c = n.charAt(i);
			UnicodeBlock b = UnicodeBlock.of(c);
			if (UnicodeBlock.COMBINING_DIACRITICAL_MARKS.equals(b) || UnicodeBlock.COMBINING_DIACRITICAL_MARKS_SUPPLEMENT.equals(b)) {
				if (sb == null) {
					sb = new StringBuilder(n.length());
					sb.append(n.substring(0, i));
				}
				continue;
			}
			if (sb != null)
				sb.append(c);
		}
		if (sb == null)
			return n;
		return sb.toString();
	}
	
	public static int count(CharSequence s, char c) {
		int result = 0;
		for (int i = 0; i < s.length(); ++i)
			if (c == s.charAt(i))
				result++;
		return result;
	}
	
	public static final String clip(String s, int from, int to) {
		return s.substring(Math.max(0, from), Math.min(s.length(), to));
	}
}
