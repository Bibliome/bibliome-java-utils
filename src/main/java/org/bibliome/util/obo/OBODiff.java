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

package org.bibliome.util.obo;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;

import org.bibliome.util.clio.CLIOException;
import org.bibliome.util.clio.CLIOParser;
import org.bibliome.util.clio.CLIOption;
import org.bibliome.util.streams.FileSourceStream;
import org.bibliome.util.streams.SourceStream;

public class OBODiff extends CLIOParser {
	private String beforePath;
	private String afterPath;
	private String encoding = "UTF-8";

	@Override
	protected boolean processArgument(String arg) throws CLIOException {
		if (beforePath == null) {
			beforePath = arg;
			return false;
		}
		if (afterPath == null)
			afterPath = arg;
		return true;
	}

	@Override
	public String getResourceBundleName() {
		return OBODiff.class.getCanonicalName() + "Help";
	}
	
	@CLIOption("-encoding")
	public void setEncoding(String encoding) {
		this.encoding = encoding;
	} 
	
	private SourceStream getSourceStream(String path) {
		return new FileSourceStream(encoding, path);
	}

	public static void main(String args[]) throws CLIOException, UnsupportedEncodingException, IOException {
		OBODiff inst = new OBODiff();
		if (!inst.parse(args))
			return;
		Logger logger = Logger.getLogger("obo");
		Ontology before = new Ontology(logger, inst.getSourceStream(inst.beforePath));
		Ontology after = new Ontology(logger, inst.getSourceStream(inst.afterPath));
		for (Term beforeTerm : before.getAllTerms()) {
			String id = beforeTerm.getId();
			Term afterTerm = after.getTerm(id);
			if (afterTerm == null) {
				System.out.println("REMOVED TERM\t" + beforeTerm.getName());
				continue;
			}

			Set<String> beforeParents = getParentIds(beforeTerm);
			Set<String> afterParents = getParentIds(afterTerm);
			for (String beforeParent : beforeParents)
				if (!afterParents.contains(beforeParent))
					System.out.println("REMOVED PARENT\t" + beforeTerm.getName() + "\t" + before.getTerm(beforeParent).getName());
			for (String afterParent : afterParents)
				if (!beforeParents.contains(afterParent))
					System.out.println("ADDED PARENT\t" + beforeTerm.getName() + "\t" + after.getTerm(afterParent).getName());

			Set<String> beforeSynonyms = new HashSet<String>(beforeTerm.getSynonyms());
			Set<String> afterSynonyms = new HashSet<String>(afterTerm.getSynonyms());
			afterSynonyms.removeAll(beforeSynonyms);
			if (!afterSynonyms.isEmpty()) {
				System.out.print("ADDED SYNONYMS\t" + beforeTerm.getName());
				for (String syn : afterSynonyms)
					System.out.print("\t" + syn);
				System.out.println();
			}
		}
		for (Term afterTerm : after.getAllTerms()) {
			String id = afterTerm.getId();
			Term beforeTerm = before.getTerm(id);
			if (beforeTerm == null)
				System.out.println("ADDED TERM\t" + afterTerm.getName());
		}
	}
	
	private static Set<String> getParentIds(Term term) {
		Set<String> result = new HashSet<String>();
		for (Term beforeParent : term.getParents())
			result.add(beforeParent.getId());
		return result;
	}
}
