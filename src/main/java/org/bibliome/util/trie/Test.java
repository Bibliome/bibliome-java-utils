package org.bibliome.util.trie;

import java.io.IOException;

import org.bibliome.util.marshall.StringCodec;

public class Test {
	private static final String FILE_PATH = "test.trie";
	private static final String QUERY = "There's a fo ol tràïn in the foul by a single Toto";
	
	private static void lookup(Trie<String> trie, String key) {
		System.out.println(key + ": " + trie.getValues(key));		
	}
	
	private static void search(Matcher<String> matcher) {
		matcher.init();
		matcher.search(QUERY);
		System.out.println();
		System.out.println(matcher.getMatchControl());		
		for (Match<String> m : matcher.finish(QUERY.length())) {
			System.out.println(
					QUERY.substring(m.getStart(), m.getEnd()) + " " +
							m.getStart() + "-" + m.getEnd() + " " +
							m.getValues()
					);
		}		
	}
	
	private static void testTrie(Trie<String> trie) {
		System.out.println();
		System.out.println();
		System.out.println();
		lookup(trie, "foo");
		lookup(trie, "foul");
		lookup(trie, "train");
		lookup(trie, "toto");
		lookup(trie, "qux");
		System.out.println();
		trie.tree(System.out);
		StandardMatchControl control = new StandardMatchControl();
		Matcher<String> matcher = new Matcher<String>(trie, control);

		control.setWordStartCaseInsensitive(true);
		control.setIgnoreDiacritics(true);
		control.setSkipWhitespace(true);
		search(matcher);
	}
	
	private static Trie<String> buildTrie() {
		Trie<String> result = new Trie<String>();
		result.addEntry("foo", "bar");
		result.addEntry("fool", "mad");
		result.addEntry("toto", "titi");
		result.addEntry("train", "wagon");
		return result;
	}
	
	private static Trie<String> loadTrie(String file) throws IOException {
		return new Trie<String>(file, StringCodec.INSTANCE);
	}
	
	public static void main(String[] args) throws IOException {
		Trie<String> trie = buildTrie();
		testTrie(trie);
		
		trie.save(FILE_PATH, StringCodec.INSTANCE);
		testTrie(loadTrie(FILE_PATH));
	}
}
