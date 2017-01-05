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

package org.bibliome.util.tomap.readers;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

import org.bibliome.util.Strings;
import org.bibliome.util.files.InputFile;
import org.bibliome.util.streams.CompressionFilter;
import org.bibliome.util.streams.FileSourceStream;
import org.bibliome.util.streams.InputStreamSourceStream;
import org.bibliome.util.streams.SourceStream;
import org.bibliome.util.tomap.StringNormalization;
import org.bibliome.util.tomap.Token;
import org.bibliome.util.tomap.TokenNormalization;
import org.bibliome.util.tomap.readers.AbstractReader.ReaderResult;
import org.bibliome.util.tomap.readers.TreeTaggerReader.TreeTaggerResult;

public class TreeTaggerReader extends AbstractReader<TreeTaggerResult> {
	public TreeTaggerReader(Logger logger, TokenNormalization tokenNormalization, StringNormalization stringNormalization) {
		super(logger, tokenNormalization, stringNormalization);
	}

	public static class TreeTaggerResult extends ReaderResult {
		private final Set<Token> emptyWords = new HashSet<Token>();
		
		public Set<Token> getEmptyWords() {
			return Collections.unmodifiableSet(emptyWords);
		}
	}

	public TreeTaggerResult parseReader(BufferedReader reader) throws IOException {
		TreeTaggerResult result = new TreeTaggerResult();
		int lineno = 0;
		while (true) {
			String line = reader.readLine();
			if (line == null) {
				return result;
			}
			lineno++;
			List<String> cols = Strings.splitAndTrim(line, '\t', 0);
			if (cols.size() != 3) {
				throw new RuntimeException("malformed line #" + lineno + ": " + line);
			}
			String form = cols.get(0);
			String pos = cols.get(1);
			String lemma = cols.get(2);
			Token t = getToken(form, lemma, pos);
			result.emptyWords.add(t);
		}
	}

	@Override
	public TreeTaggerResult parseSource(SourceStream source) throws IOException {
		try (BufferedReader reader = source.getBufferedReader()) {
			return parseReader(reader);
		}
	}
	
	@Override
	public TreeTaggerResult parseFile(File file) throws IOException {
		InputFile inputFile = new InputFile(file.getName());
		SourceStream source = new FileSourceStream("UTF-8", inputFile);
		return parseSource(source);
	}

	@Override
	public TreeTaggerResult parseStream(InputStream stream) throws IOException {
		SourceStream source = new InputStreamSourceStream("UTF-8", CompressionFilter.NONE, stream, "<<stream>>");
		return parseSource(source);
	}
}
