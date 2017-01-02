package org.bibliome.util.newprojector;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.List;

import org.bibliome.util.filelines.InvalidFileLineEntry;
import org.bibliome.util.newprojector.chars.Filters;
import org.bibliome.util.newprojector.chars.Mappers;
import org.bibliome.util.newprojector.states.FirstValueState;
import org.bibliome.util.streams.FileSourceStream;
import org.bibliome.util.streams.SourceStream;

public class Test {
	public static void main(String[] args) throws IOException, InvalidFileLineEntry {
		Dictionary<String[]> dict = new Dictionary<String[]>(new FirstValueState<String[]>(), Filters.ACCEPT_ALL, Mappers.IDENTITY);
		SourceStream dictFile = new FileSourceStream("UTF-8", args[0]);
		BufferedReader dictReader = dictFile.getBufferedReader();
		int n = 0;
		for (String entry = dictReader.readLine(); entry != null; entry = dictReader.readLine()) {
			System.err.print(Integer.toString(++n) + "\r");
			dict.addEntry(entry, new String[] { entry });
		}
		//dict.tree(System.err);
		Matcher<String[]> matcher = new Matcher<String[]>(dict, Filters.ACCEPT_ALL, Filters.ACCEPT_ALL);
		dict.match(matcher, new StringReader(args[0]));
		List<Match<String[]>> matches = matcher.getMatches();
		System.out.println(args[1]);
		System.out.println("n: " + matches.size());
		for (Match<String[]> m : matches)
			System.out.println(m.getState().getValues().iterator().next()[0] + ": " + m.getStart() + "-" + m.getEnd());
	}
}
