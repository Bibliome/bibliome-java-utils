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

package fr.inra.maiage.bibliome.util.bionlpst;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import fr.inra.maiage.bibliome.util.Strings;
import fr.inra.maiage.bibliome.util.files.InputDirectory;
import fr.inra.maiage.bibliome.util.files.InputFile;
import fr.inra.maiage.bibliome.util.fragments.SimpleMutableFragment;
import fr.inra.maiage.bibliome.util.streams.FileSourceStream;
import fr.inra.maiage.bibliome.util.streams.SourceStream;

public class BioNLPSTDocument {
	private final String id;
	private final String text;
	private final Map<String,BioNLPSTAnnotation> annotations = new HashMap<String,BioNLPSTAnnotation>();
	private final Collection<Equivalence> equivalences = new LinkedHashSet<Equivalence>();

	public BioNLPSTDocument(String id, String text) {
		super();
		this.id = id;
		this.text = text;
	}
	
	public BioNLPSTDocument(String id, Reader reader) throws IOException {
		this(id, readWhole(reader));
	}
	
	public BioNLPSTDocument(SourceStream source) throws IOException {
		try (Reader r = source.getBufferedReader()) {
			this.id = getIdFromFilename(source.getStreamName(r));
			this.text = readWhole(r);
		}
	}
	
	public BioNLPSTDocument(String charset, File file) throws IOException {
		this(new FileSourceStream(charset, file.getAbsolutePath()));
	}
	
	private static final String readWhole(Reader r) throws IOException {
		StringBuilder result = new StringBuilder();
		char[] buf = new char[1024];
		while (true) {
			int n = r.read(buf);
			if (n == -1)
				break;
			result.append(buf, 0, n);
		}
		return result.toString();
	}
	
	public static String getIdFromFilename(String fullName) {
		String name;
		int slash = fullName.lastIndexOf(File.separatorChar);
		if (slash == -1) {
			name = fullName;
		}
		else {
			name = fullName.substring(slash + 1);
		}
		if (!name.endsWith(".txt"))
			throw new RuntimeException("expected a .txt file, got " + name);
		return name.substring(0, name.length() - 4);
	}
	
	public String getId() {
		return id;
	}

	public String getText() {
		return text;
	}
	
	public Collection<Equivalence> getEquivalences() {
		return Collections.unmodifiableCollection(equivalences);
	}
	
	void addEquivalence(Equivalence equiv) {
		equivalences.add(equiv);
	}

	BioNLPSTAnnotation resolveId(Sourced source, String id) throws BioNLPSTException {
		if (annotations.containsKey(id))
			return annotations.get(id);
		return source.error("could not resolve id " + id);
	}
	
	void addAnnotation(BioNLPSTAnnotation a) throws BioNLPSTException {
		String id = a.getId();
		if (annotations.containsKey(id)) {
			a.error("id clash: " + id + " (see " + annotations.get(id).message());
		}
		annotations.put(id, a);
	}
	
	public void resolveAllIds() throws BioNLPSTException {
		for (BioNLPSTAnnotation a : annotations.values())
			a.resolveIds();
	}
	
	public Collection<BioNLPSTAnnotation> getAnnotations() {
		return Collections.unmodifiableCollection(annotations.values());
	}
	
	public void parseAFile(InputDirectory dir, Visibility visibility, String charset) throws IOException, BioNLPSTException {
		InputFile file = new InputFile(dir, id + visibility.fileSuffix);
		String source = file.getAbsolutePath();
		SourceStream stream = new FileSourceStream(charset, file);
		try (BufferedReader r = stream.getBufferedReader()) {
			int lineno = 0;
			while (true) {
				String line = r.readLine();
				if (line == null)
					break;
				if (line.isEmpty())
					continue;
				lineno++;
				parseLine(source, lineno, visibility, line);
			}
		}
	}
	
	private void parseLine(String source, int lineno, Visibility visibility, String line) throws BioNLPSTException {
		for (LineType lineType : LineType.values()) {
			Matcher m = lineType.pattern.matcher(line);
			if (m.matches()) {
				lineType.handleMatch(source, lineno, this, visibility, m);
				return;
			}
		}
	}

	private static enum LineType {
		EQUIVALENCE("\\*\tEquiv(?<annotations>(?: [A-Z]\\d+)+)") {
			@Override
			protected void handleMatch(String source, int lineno, BioNLPSTDocument document, Visibility visibility, Matcher m) {
				Equivalence equiv = new Equivalence(source, lineno, document, visibility);
				String annotationsGroup = m.group("annotations");
				for (String id : Strings.split(annotationsGroup, ' ', 0)) {
					if (!id.isEmpty()) {
						equiv.addAnnotationId(id);
					}
				}
			}
		},
		
		TEXT_BOUND("(?<id>[A-Z]\\d+)\t(?<type>.+) (?<boundaries>\\d+ \\d+(?:;\\d+ \\d+)*)\t(?<form>.*)") {
			@Override
			protected void handleMatch(String source, int lineno, BioNLPSTDocument document, Visibility visibility, Matcher m) throws BioNLPSTException {
				String id = m.group("id");
				String type = m.group("type");
				TextBound tb = new TextBound(source, lineno, document, visibility, id, type);
				String boundariesGroup = m.group("boundaries");
				SimpleMutableFragment frag = new SimpleMutableFragment(0, 0);
				for (String b : Strings.split(boundariesGroup, ';', 0)) {
					int spc = b.indexOf(' ');
					frag.setStart(Integer.parseInt(b.substring(0, spc)));
					frag.setEnd(Integer.parseInt(b.substring(spc + 1)));
					tb.addFragment(frag);
				}
			}
		},
		
		NORMALIZATION("(?<id>[A-Z]\\d+)\t(?<type>.+) Annotation:(?<annotation>[A-Z]\\d+) Referent:(?<referent>.*)") {
			@Override
			protected void handleMatch(String source, int lineno, BioNLPSTDocument document, Visibility visibility, Matcher m) throws BioNLPSTException {
				String id = m.group("id");
				String type = m.group("type");
				String annotationId = m.group("annotation");
				String referent = m.group("referent");
				new Normalization(source, lineno, document, visibility, id, type, annotationId, referent);
			}
		},
		
		MODIFICATION("(?<id>[A-Z]\\d+)\t(?<type>.+) (?<annotation>[A-Z]\\d+)") {
			@Override
			protected void handleMatch(String source, int lineno, BioNLPSTDocument document, Visibility visibility, Matcher m) throws BioNLPSTException {
				String id = m.group("id");
				String type = m.group("type");
				String annotationId = m.group("annotation");
				new Modification(source, lineno, document, visibility, id, type, annotationId);
			}
		},
		
		EVENT("(?<id>[A-Z]\\d+)\t(?<type>[^:\\s]+):(?<trigger>[A-Z]\\d+)[ ]*(?<args>(?: .*:[A-Z]\\d+)*)") {
			@Override
			protected void handleMatch(String source, int lineno, BioNLPSTDocument document, Visibility visibility, Matcher m) throws BioNLPSTException {
				String id = m.group("id");
				String type = m.group("type");
				String triggerId = m.group("trigger");
				Event event = new Event(source, lineno, document, visibility, id, type, triggerId);
				String argsGroup = m.group("args");
				parseArgs(event, argsGroup);
			}
		},
		
		RELATION("(?<id>[A-Z]\\d+)\t(?<type>\\S+)(?<args>(?: .*:[A-Z]\\d+)*)") {
			@Override
			protected void handleMatch(String source, int lineno, BioNLPSTDocument document, Visibility visibility, Matcher m) throws BioNLPSTException {
				String id = m.group("id");
				String type = m.group("type");
				BioNLPSTRelation rel = new BioNLPSTRelation(source, lineno, document, visibility, id, type);
				String argsGroup = m.group("args");
				parseArgs(rel, argsGroup);
			}
		};
		
		private final Pattern pattern;
		
		private LineType(String re) {
			this.pattern = Pattern.compile(re);
		}
		
		protected abstract void handleMatch(String source, int lineno, BioNLPSTDocument document, Visibility visibility, Matcher m) throws BioNLPSTException;

		private static void parseArgs(AnnotationWithArgs annotation, String group) throws BioNLPSTException {
			for (String s : Strings.split(group, ' ', 0)) {
				if (s.isEmpty())
					continue;
				int col = s.lastIndexOf(':');
				String role = s.substring(0, col);
				String a = s.substring(col + 1);
				annotation.addArgumentId(role, a);
			}
		}
	}
}
