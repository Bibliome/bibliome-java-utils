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

package fr.inra.maiage.bibliome.util.genia;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import fr.inra.maiage.bibliome.util.PatternFilenameFilter;

/**
 * Genia corpus.
 * @author rbossy
 *
 */
public class Corpus {
	private static final FilenameFilter TXT_FILE_FILTER = new PatternFilenameFilter("\\.txt$");
	private static final FilenameFilter A1_FILE_FILTER = new PatternFilenameFilter("\\.a1$");
	private static final FilenameFilter A2_FILE_FILTER = new PatternFilenameFilter("\\.a2$");

	private final Map<String,Document> documents = new HashMap<String,Document>();
	
	/**
	 * Creates a new genia corpus.
	 */
	public Corpus() {
		super();
	}
	
	/**
	 * Adds the specified document to this corpus.
	 * @param doc
	 */
	void addDocument(Document doc) {
		if (documents.containsKey(doc.getId()))
			throw new RuntimeException("duplicate document id " + doc.getId());
		documents.put(doc.getId(), doc);
	}
	
	/**
	 * Returns the document with the specified identifier.
	 * @param id
	 * @throws RuntimeException if the corpus does not contain a document with the specified identifier
	 */
	public Document getDocument(String id) {
		if (!documents.containsKey(id))
			throw new RuntimeException("no document with id " + id);
		return documents.get(id);
	}

	/**
	 * Returns all documents in this corpus.
	 */
	public Collection<Document> getDocuments() {
		return Collections.unmodifiableCollection(documents.values());
	}
	
	/**
	 * Parses all files in the specified directory.
	 * @param dir
	 * @throws IOException
	 * @throws RuntimeException if the specified file is not a directory
	 */
	public void parse(File dir, boolean removeRW) throws IOException {
		if (!dir.isDirectory())
			throw new RuntimeException(dir.toString() + " is not a directory");
		for (File file : dir.listFiles(TXT_FILE_FILTER)) {
			String id = file.getName().replace(".txt", "");
			Document doc = new Document(id);
			addDocument(doc);
		}
		parseA1(dir, removeRW);
		parseA2(dir, removeRW);
	}
	
	/**
	 * Parses all .a1 files in the specified directory.
	 * @param dir
	 * @throws IOException
	 * @throws RuntimeException if the specified file is not a directory, or if a .a1 file is read and there is no document with the corresponding identifier
	 */
	public void parseA1(File dir, boolean removeRW) throws IOException {
		if (!dir.isDirectory())
			throw new RuntimeException(dir.toString() + " is not a directory");
		for (File file : dir.listFiles(A1_FILE_FILTER)) {
			String id = file.getName().replace(".a1", "");
			Document doc = getDocument(id);
			doc.parse(file, true, removeRW);
		}
	}

	/**
	 * Parses all .a2 files in the specified directory.
	 * @param dir
	 * @throws IOException
	 * @throws RuntimeException if the specified file is not a directory, or if a .a2 file is read and there is no document with the corresponding identifier
	 */	
	public void parseA2(File dir, boolean removeRW) throws IOException {
		if (!dir.isDirectory())
			throw new RuntimeException(dir.toString() + " is not a directory");
		for (File file : dir.listFiles(A2_FILE_FILTER)) {
			String id = file.getName().replace(".a2", "");
			Document doc = getDocument(id);
			doc.parse(file, false, removeRW);
		}
	}

	/**
	 * Returns a copy of this corpus with only the data read form files (not entities or events added after).
	 */
	public Corpus copyInput() {
		Corpus result = new Corpus();
		for (Document doc : documents.values())
			result.addDocument(doc.copyInput());
		return result;
	}

	/**
	 * Returns either this corpus contains a document with the specified identifier.
	 * @param id
	 */
	public boolean hasDocument(String id) {
		return documents.containsKey(id);
	}
	
	/**
	 * Removes all documents in this corpus.
	 */
	public void clear() {
		documents.clear();
	}
}
