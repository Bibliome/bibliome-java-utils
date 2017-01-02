package org.bibliome.util.tydi;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import org.bibliome.util.CartesianProduct;
import org.bibliome.util.EquivalenceHashSets;
import org.bibliome.util.EquivalenceSets;
import org.bibliome.util.filelines.EquivFileLines;
import org.bibliome.util.filelines.FileLines;
import org.bibliome.util.filelines.InvalidFileLineEntry;
import org.bibliome.util.filelines.TabularFormat;
import org.bibliome.util.streams.SourceStream;
import org.bibliome.util.trie.Match;
import org.bibliome.util.trie.Matcher;
import org.bibliome.util.trie.StandardMatchControl;
import org.bibliome.util.trie.Trie;

/**
 * Resolves a tabular TyDI export as a single mapping to canonical terms.
 * This includes typographic variants and acronyms saturation.
 * @author rbossy
 *
 */
public class ResolveExport {
    private static class LemmaLines extends FileLines<Integer> {
        /** The lemma to term. */
        private final Map<String,String> lemmaToTerm = new HashMap<String,String>();

        /** The term to lemma. */
        private final Map<String,String> termToLemma = new HashMap<String,String>();

        /**
         * Instantiates a new lemma lines.
         * @param ctx 
         * 
         * @throws FileNotFoundException
         *             the file not found exception
         * @throws UnsupportedEncodingException
         *             the unsupported encoding exception
         * @throws IOException
         *             Signals that an I/O exception has occurred.
         */
        private LemmaLines(Logger logger) {
            super(logger);
            getFormat().setNumColumns(2);
        }

        @Override
        public void processEntry(Integer foo, int lineno, List<String> entry) {
            String lemma = entry.get(0);
            String term = entry.get(1);

            if (lemmaToTerm.containsKey(lemma)) {
                String prevTerm = lemmaToTerm.get(lemma);
                if (!prevTerm.equals(term)) {
                    getLogger().warning(String.format("ambiguous lemma %s: %s / %s", lemma, prevTerm, term));
                }
            }
            lemmaToTerm.put(lemma, term);

            if (termToLemma.containsKey(term)) {
                String prevLemma = termToLemma.get(term);
                if (!prevLemma.equals(lemma)) {
                    getLogger().warning(String.format("several lemmas for %s: %s / %s", term, prevLemma, lemma));
                }
            }
            termToLemma.put(term, lemma);
        }
    }

    private static final FileLines<Map<String,String>> synonymLines = new FileLines<Map<String,String>>() {
        @Override
        public void processEntry(Map<String,String> lemmaToTerm, int lineno, List<String> entry) {
            String lemma = entry.get(0);
            String canonical = entry.get(1);
            lemmaToTerm.put(lemma, canonical);
        }
    };
	
    private static void saturateMerged(Logger logger, SourceStream mergeFile, Map<String,String> map) throws FileNotFoundException, UnsupportedEncodingException, IOException, InvalidFileLineEntry {
        EquivalenceSets<String> merged = loadEquivalenceFile(logger, mergeFile, null);
        Map<String,String> toAdd = new HashMap<String,String>();
        for (Map.Entry<String,String> e : map.entrySet()) {
            String lemma = e.getKey();
            if (merged.getMap().containsKey(lemma)) {
                String term = e.getValue();
                for (String m : merged.getMap().get(lemma))
                    toAdd.put(m, term);
            }
        }
        map.putAll(toAdd);
    }
    
    private static EquivalenceSets<String> loadEquivalenceFile(Logger logger, SourceStream file, EquivalenceSets<String> eqSets) throws FileNotFoundException, UnsupportedEncodingException, IOException, InvalidFileLineEntry {
        if (eqSets == null)
            eqSets = new EquivalenceHashSets<String>();
        if (file == null)
            return eqSets;
        TabularFormat format = new TabularFormat();
        format.setMinColumns(2);
        format.setMaxColumns(2);
        format.setSkipBlank(true);
        format.setSkipEmpty(true);
		EquivFileLines efl = new EquivFileLines(format, logger);
        efl.process(file, eqSets);
        return eqSets;
    }
    
    private static final List<Match<Set<String>>> searchVariants(Matcher<Set<String>> variantMatcher, String lemma) {
    	variantMatcher.init();
    	variantMatcher.search(lemma);
    	return variantMatcher.finish(lemma.length());
    }

    /**
     * Resolves export
     * @param logger
     * @param lemmaFile
     * @param synonymsFile
     * @param quasiSynonymsFile
     * @param mergeFile
     * @param typographicVariationsFile
     * @param acronymsFile
     * @return a map from surface form to canonical form.
     * @throws UnsupportedEncodingException
     * @throws IOException
     * @throws InvalidFileLineEntry
     */
    public static Map<String,String> resolveExport(Logger logger, SourceStream lemmaFile, SourceStream synonymsFile, SourceStream quasiSynonymsFile, SourceStream mergeFile, SourceStream typographicVariationsFile, SourceStream acronymsFile) throws UnsupportedEncodingException, IOException, InvalidFileLineEntry {
		logger.info("reading lemma");
		LemmaLines lemmaLines = new LemmaLines(logger);
		lemmaLines.process(lemmaFile, 0);
		logger.fine(Integer.toString(lemmaLines.lemmaToTerm.size()) + " entries");
		logger.info("reading synonyms");
		synonymLines.setLogger(logger);
		synonymLines.process(synonymsFile, lemmaLines.lemmaToTerm);
		logger.fine(Integer.toString(lemmaLines.lemmaToTerm.size()) + " entries");
		logger.info("reading quasi-synonyms");
		synonymLines.process(quasiSynonymsFile, lemmaLines.lemmaToTerm);
		logger.fine(Integer.toString(lemmaLines.lemmaToTerm.size()) + " entries");
		logger.info("saturating with merged");
		saturateMerged(logger, mergeFile, lemmaLines.lemmaToTerm);
		logger.fine(Integer.toString(lemmaLines.lemmaToTerm.size()) + " entries");
		logger.info("reading typographic variants");
		EquivalenceSets<String> variants = loadEquivalenceFile(logger, typographicVariationsFile, null);
		logger.info("reading acronyms");
		loadEquivalenceFile(logger, acronymsFile, variants);

		Trie<Set<String>> variantTrie = new Trie<Set<String>>();
		variantTrie.addEntries(variants.getMap());
		StandardMatchControl matchControl = new StandardMatchControl();
		matchControl.setStartWordBoundary(true);
		matchControl.setEndWordBoundary(true);
		Matcher<Set<String>> variantMatcher = new Matcher<Set<String>>(variantTrie, matchControl);
		logger.info("saturating with typographic variants");
		Map<String,String> map = new HashMap<String,String>();
		for (Map.Entry<String,String> e : lemmaLines.lemmaToTerm.entrySet()) {
			String lemma = e.getKey();
			List<Match<Set<String>>> matches = searchVariants(variantMatcher, lemma);
			if (matches.isEmpty()) {
				map.put(lemma, e.getValue());
				continue;
			}
			StringBuilder variant = new StringBuilder();
			List<String> suffixes = new ArrayList<String>(matches.size());
			List<Collection<String>> variations = new ArrayList<Collection<String>>(matches.size());
			int lastPos = 0;
			String lastVariation = null;
			for (int i = 0; i < matches.size(); ++i) {
				Match<Set<String>> w = matches.get(i);
				String currentVariation = lemma.substring(w.getStart(), w.getEnd());
				if (i == 0) {
					variant.append(lemma.substring(0, w.getStart()));
					lastPos = w.getEnd();
				}
				else {
					if (w.getStart() < lastPos) {
						logger.warning(String.format("overlapping variations: '%s' / '%s'", lastVariation, currentVariation));
						continue;
					}
					suffixes.add(lemma.substring(lastPos, w.getStart()));
					lastPos = w.getEnd();
				}
				lastVariation = currentVariation;
				variations.add(w.getValues().get(0));
			}
			suffixes.add(lemma.substring(lastPos));
			int prefixLength = variant.length();
			CartesianProduct<String> cp = new CartesianProduct<String>(variations);
			List<String> v = cp.getElements();
			String canonical = e.getValue();
			while (cp.next()) {
				variant.setLength(prefixLength);
				for (int i = 0; i < v.size(); ++i) {
					variant.append(v.get(i));
					variant.append(suffixes.get(i));
				}
				String sv = variant.toString();
				if (lemmaLines.lemmaToTerm.containsKey(sv)) {
					String cv = lemmaLines.lemmaToTerm.get(sv);
					if (!cv.equals(canonical)) {
						logger.warning(String.format("%s has canonical %s, but variant %s has canonical %s", lemma, canonical, sv, cv));
					}
				}
				map.put(sv, canonical);
			}
		}
		logger.info(String.format("%d entries", map.size()));
		/*if (commaKludge) {
                logger.info("kludging commas");
                Map<String,String[]> toAdd = new HashMap<String,String[]>();
                for (Map.Entry<String,String[]> e : map.entrySet()) {
                    String form = e.getKey();
                    if (form.indexOf(',') == -1)
                        continue;
                    String commaFreeForm = form.replace(",", "");
                    String[] entry = e.getValue();
                    String[] commaFreeEntry = Arrays.copyOf(entry, entry.length);
                    commaFreeEntry[0] = commaFreeForm;
                    toAdd.put(commaFreeForm, commaFreeEntry);
                }
                map.putAll(toAdd);
            }*/
		return map;
	}
}
