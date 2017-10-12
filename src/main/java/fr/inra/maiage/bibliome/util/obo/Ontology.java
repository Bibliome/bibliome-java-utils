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

package fr.inra.maiage.bibliome.util.obo;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import fr.inra.maiage.bibliome.util.Pair;
import fr.inra.maiage.bibliome.util.defaultmap.DefaultMap;
import fr.inra.maiage.bibliome.util.streams.SourceStream;

public class Ontology {
	private final Map<String,Term> terms = new HashMap<String,Term>();
	private final Map<String,Subset> subsets = new HashMap<String,Subset>();
	private final Map<String,String> info = new HashMap<String,String>();

	private static final Pattern CLAUSE = Pattern.compile("([^:]+):\\s+(.*)");

	public Ontology(Logger logger, SourceStream file) throws IOException {
		super();
		BufferedReader r = file.getBufferedReader();
		parse(logger, r);
		r.close();
	}
	
	public Term getTerm(String id) {
		return terms.get(id);
	}
	
	public Collection<Term> getAllTerms() {
		return Collections.unmodifiableCollection(terms.values());
	}
	
	public Subset getSubset(String id) {
		return subsets.get(id);
	}
	
	public Collection<Subset> getAllSubsets() {
		return Collections.unmodifiableCollection(subsets.values());
	}
	
	public String getInfo(String key) {
		return info.get(key);
	}
	
	public Map<String,String> getAllInfo() {
		return Collections.unmodifiableMap(info);
	}
	
	public Collection<Term> getRoots() {
		Collection<Term> result = new ArrayList<Term>();
		for (Term term : terms.values())
			if (term.isRoot())
				result.add(term);
		return result;
	}
	
	private static interface GlobalClauseHandler {
		void handle(int lineno, String clause, String arg);
	}
	
	private final GlobalClauseHandler setInfo = new GlobalClauseHandler() {
		@Override
		public void handle(int lineno, String clause, String arg) {
			info.put(clause, arg);	
		}
	};
	
	private final GlobalClauseHandler subsetDef = new GlobalClauseHandler() {
		private final Pattern SUBSETDEF = Pattern.compile("(.+?)\\s+\"(.*)\"");

		@Override
		public void handle(int lineno, String clause, String arg) {
			Matcher m2 = SUBSETDEF.matcher(arg);
			if (!m2.matches())
				error(lineno, "malformed subsetdef: '" + arg + "'");
			String id = m2.group(1);
			if (subsets.containsKey(id))
				error(lineno, "duplicate subset");
			String name = m2.group(2);
			subsets.put(id, new Subset(id, name));
		}
	};
	
	private static interface TermClauseHandler {
		void handle(int lineno, String arg, Term term);
	}
	
	private static final TermClauseHandler doNothing = new TermClauseHandler() {
		@Override
		public void handle(int lineno, String arg, Term term) {
		}
	};
	
	private static final TermClauseHandler setId = new TermClauseHandler() {
		@Override
		public void handle(int lineno, String arg, Term term) {
			if (term.getId() != null)
				error(lineno, "second term id");
			term.setId(arg);
		}
	};
	
	private static final TermClauseHandler setName = new TermClauseHandler() {
		@Override
		public void handle(int lineno, String arg, Term term) {
			if (term.getName() != null)
				error(lineno, "second term name");
			term.setName(arg);
		}
	};
	
	private final TermClauseHandler setSubset = new TermClauseHandler() {
		@Override
		public void handle(int lineno, String arg, Term term) {
			if (!subsets.containsKey(arg))
				error(lineno, "unknown subset");
			Subset subset = subsets.get(arg);
			subset.add(term);
			term.add(subset);
		}
	};
	
	private final class HandleIsa implements TermClauseHandler {
		private final Collection<Pair<Term,String>> pending = new ArrayList<Pair<Term,String>>();

		@Override
		public void handle(int lineno, String arg, Term term) {
			pending.add(new Pair<Term, String>(term, arg));
		}
		
		private void resolve() {
			for (Pair<Term,String> isa : pending) {
				Term term = isa.first;
				String parentId = isa.second;
				if (!terms.containsKey(parentId))
					error(term.getLineno(), "unknown parent " + parentId);
				Term parent = terms.get(parentId);
				term.addParent(parent);
				parent.addChild(term);
			}
		}
	}
	
	private static final TermClauseHandler addSynonym = new TermClauseHandler() {
		private final Pattern SYNONYM = Pattern.compile("^\"(.*)\"");

		@Override
		public void handle(int lineno, String arg, Term term) {
			Matcher m2 = SYNONYM.matcher(arg);
			if (!m2.find())
				error(lineno, "malformed synonym");
			String synonym = m2.group(1);
			if (term.hasSynonym(synonym))
				error(lineno, "duplicate synonym");
			term.addSynonym(synonym);
		}
	};
	
	private static final TermClauseHandler unsupportedClause = new TermClauseHandler() {
		@Override
		public void handle(int lineno, String arg, Term term) {
			error(lineno, "unhandled clause");
		}
	};
	
	private DefaultMap<String,GlobalClauseHandler> getGlobalClauseHandlers() {
		DefaultMap<String,GlobalClauseHandler> result = new DefaultMap<String,GlobalClauseHandler>(false, new HashMap<String,GlobalClauseHandler>()) {
			@Override
			protected GlobalClauseHandler defaultValue(String key) {
				return setInfo;
			}
		};
		result.put("subsetdef", subsetDef);
		return result;
	}
	
	private DefaultMap<String,TermClauseHandler> getTermClauseHandlers(TermClauseHandler isaHandler) {
		DefaultMap<String,TermClauseHandler> result = new DefaultMap<String,TermClauseHandler>(false, new HashMap<String,TermClauseHandler>()) {
			@Override
			protected TermClauseHandler defaultValue(String key) {
				return unsupportedClause;
			}
		};
		result.put("id", setId);
		result.put("name", setName);
		result.put("subset", setSubset);
		result.put("synonym", addSynonym);
		result.put("exact_synonym", addSynonym);
		result.put("related_synonym", addSynonym);
		if (isaHandler != null)
			result.put("is_a", isaHandler);
		result.put("def", doNothing);
		result.put("comment", doNothing);
		result.put("created_by", doNothing);
		result.put("creation_date", doNothing);
		result.put("xref", doNothing);
		return result;
	}

	private void parse(Logger logger, BufferedReader r) throws IOException {
		DefaultMap<String,GlobalClauseHandler> globalClauseHandlers = getGlobalClauseHandlers();
		HandleIsa handleIsa = new HandleIsa();
		DefaultMap<String,TermClauseHandler> termClauseHandlers = getTermClauseHandlers(handleIsa);
		Term current = null;
		for (int lineno = 1;; ++lineno) {
			String line = r.readLine();
			if (line == null)
				break;
			line = line.trim();
			if (line.isEmpty())
				continue;
			if (line.equals("[Term]")) {
				addTerm(current);
				current = new Term(lineno);
				continue;
			}
			Matcher m = CLAUSE.matcher(line);
			if (!m.matches()) {
				warning(logger, lineno, "malformed line:\n    " + line);
				continue;
			}
			String clause = m.group(1);
			String arg = m.group(2);
			int excl = arg.indexOf('!');
			if (excl >= 0)
				arg = arg.substring(0, excl).trim();
			if (current == null) {
				GlobalClauseHandler handler = globalClauseHandlers.safeGet(clause);
				handler.handle(lineno, clause, arg);
				continue;
			}
			TermClauseHandler handler = termClauseHandlers.safeGet(clause);
			handler.handle(lineno, arg, current);
		}
		addTerm(current);
		handleIsa.resolve();
	}
	
	private void addTerm(Term term) {
		if (term == null)
			return;
		if (terms.containsKey(term.getId()))
			error(term.getLineno(), "duplicate term id " + term.getId());
		terms.put(term.getId(), term);
	}

	private static void error(int lineno, String message) {
		throw new RuntimeException("at line " + lineno + ": " + message);
	}

	private static void warning(Logger logger, int lineno, String message) {
		logger.warning("at line " + lineno + ": " + message);
	}
}
