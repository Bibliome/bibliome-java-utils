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

package fr.inra.maiage.bibliome.util.count;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.xpath.XPathExpressionException;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import fr.inra.maiage.bibliome.util.clio.CLIOConversionException;
import fr.inra.maiage.bibliome.util.clio.CLIOException;
import fr.inra.maiage.bibliome.util.clio.CLIOParser;
import fr.inra.maiage.bibliome.util.clio.CLIOption;
import fr.inra.maiage.bibliome.util.xml.XMLUtils;

/**
 * Reads text files and computes words occurrence, frequency and TF-IDF.
 * @author rbossy
 */
public class FrequentWords extends CLIOParser {
	private static final Pattern WORD_BOUNDARY = Pattern.compile("\\b");
	
	private final List<File> files = new ArrayList<File>();
	private final Set<String> exclude = new HashSet<String>();
	private String encoding = "UTF-8";
	private int minLength = 1;
	private int n = 10;
	private double threshold = 0;
	private boolean xmlFiles = false;
	private boolean tfidf = false;
	
	public FrequentWords() {
		super();
	}
	
	@Override
	public boolean processArgument(String arg) throws CLIOConversionException {
		files.add((File) convertArgument(File.class, arg));
		return true;
	}
	
	@CLIOption("-encoding")
	public void setEncoding(String encoding) {
		this.encoding = encoding;
	}
	
	@CLIOption("-excludeFile")
	public void setExcludeFile(File file) throws IOException {
		BufferedReader br = getBufferedReader(file);
		while (true) {
			String line = br.readLine();
			if (line == null)
				break;
			exclude.add(line.trim());
		}
		br.close();
	}
	
	@CLIOption("-minLength")
	public void setMinLength(int minLength) {
		this.minLength = minLength;
	}
	
	@CLIOption("-n")
	public void setN(int n) {
		this.n = n > 0 ? n : Integer.MAX_VALUE;
	}
	
	@CLIOption("-threshold")
	public void setThreshold(double threshold) {
		this.threshold = threshold;
	} 
	
	@CLIOption("-xml")
	public void xmlFiles() {
		xmlFiles = true;
	}
	
	@CLIOption("-tfidf")
	public void tfidf() {
		tfidf = true;
	}
	
	@CLIOption(value = "-help", stop = true)
	public void help() {
		System.err.print(usage());
	}
	
	@Override
	public String getResourceBundleName() {
		return FrequentWords.class.getCanonicalName() + "Help";
	}
	
	private BufferedReader getBufferedReader(File f) throws FileNotFoundException, UnsupportedEncodingException {
		InputStream is = new FileInputStream(f);
		InputStreamReader isr = new InputStreamReader(is, encoding);
		return new BufferedReader(isr);
	}
	
	private void readFile(File f, TfIdfDocuments<File> docs) throws XPathExpressionException, SAXException, IOException {
		if (xmlFiles)
			readXMLFile(f, docs);
		else
			readTextFile(f, docs);
	}
	
	private void readTextFile(File f, TfIdfDocuments<File> docs) throws IOException {
		BufferedReader br = getBufferedReader(f);
		while (true) {
			String line = br.readLine();
			if (line == null)
				break;
			readString(line, f, docs);
		}
		br.close();
	}
	
	private void readXMLFile(File f, TfIdfDocuments<File> docs) throws SAXException, IOException, XPathExpressionException {
		Document doc = XMLUtils.docBuilder.parse(f);
		String contents = XMLUtils.evaluateString("/", doc);
		readString(contents, f, docs);
	}

	private void readString(String s, File f, TfIdfDocuments<File> docs) {
		Matcher m = WORD_BOUNDARY.matcher(s);
		int start = 0;
		while (m.find()) {
			int end = m.start();
			String word = s.substring(start, end).trim();
			start = end;
			if (word.length() < minLength)
				continue;
			if (exclude.contains(word))
				continue;
			docs.incr(f, word);
		}
	}
	
	public static void main(String[] args) throws IOException, XPathExpressionException, SAXException, CLIOException {
		FrequentWords frequentWords = new FrequentWords();
		if (frequentWords.parse(args))
			return;
		TfIdfDocuments<File> docs = new TfIdfDocuments<File>(new HashMap<File,TfIdfStats>());
		for (File f : frequentWords.files)
			frequentWords.readFile(f, docs);
		if (frequentWords.tfidf) {
			docs.compute();
			for (File f : frequentWords.files) {
				System.out.println(f);
				TfIdfStats stats = docs.get(f);
				List<Map.Entry<String,TfIdf>> terms = stats.entryList(Collections.reverseOrder(new TfIdf.TfIdfComparator<TfIdf>()));
				for (Map.Entry<String,TfIdf> t : terms.subList(0, Math.min(frequentWords.n, terms.size()))) {
					double d = t.getValue().getTfIdf();
					if (d < frequentWords.threshold)
						break;
					System.out.println(t.getKey() + '\t' + d);
				}
			}
		}
		else {
			List<Map.Entry<String,Count>> terms = docs.getTotal().entryList(true);
			for (Map.Entry<String,Count> e : terms.subList(0, Math.min(frequentWords.n, terms.size()))) {
				long c = e.getValue().get();
				if (c < frequentWords.threshold)
					break;
				System.out.println(e.getKey() + '\t' + c);
			}
		}
	}
}
