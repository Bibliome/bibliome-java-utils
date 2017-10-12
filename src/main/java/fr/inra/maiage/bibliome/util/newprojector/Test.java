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

package fr.inra.maiage.bibliome.util.newprojector;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.List;

import fr.inra.maiage.bibliome.util.filelines.InvalidFileLineEntry;
import fr.inra.maiage.bibliome.util.newprojector.chars.Filters;
import fr.inra.maiage.bibliome.util.newprojector.chars.Mappers;
import fr.inra.maiage.bibliome.util.newprojector.states.FirstValueState;
import fr.inra.maiage.bibliome.util.streams.FileSourceStream;
import fr.inra.maiage.bibliome.util.streams.SourceStream;

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
