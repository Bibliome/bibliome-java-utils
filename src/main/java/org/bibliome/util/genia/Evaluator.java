package org.bibliome.util.genia;

import java.io.File;
import java.io.IOException;
import java.util.logging.Logger;

/**
 * An evaluator compares two genia corpora.
 * The two corpora must follow the same schema.
 * The first corpus is called 'hypothesis', and the second 'reference'.
 * An evaluator will typically compute a score indicating how close the hypothesis is from the reference.
 * @author rbossy
 *
 */
public abstract class Evaluator {
	/**
	 * The reference corpus.
	 */
	protected final Corpus reference = new Corpus();

	/**
	 * Computes the difference between two documents.
	 * @param logger
	 * @param reference
	 * @param hypothesis
	 */
	protected abstract void difference(Logger logger, Document reference, Document hypothesis);
	
	/**
	 * Computes the difference, when an hypothesis document is missing.
	 * @param logger
	 * @param reference
	 */
	protected abstract void missing(Logger logger, Document reference);
	
	/**
	 * Computes the difference, when there's an extra hypothesis document.
	 * @param logger
	 * @param hypothesis
	 */
	protected abstract void extra(Logger logger, Document hypothesis);
	
	/**
	 * Clears the scores.
	 */
	protected abstract void clear();
	
	/**
	 * Creates a new evaluator.
	 */
	public Evaluator() {
		super();
	}

	/**
	 * Creates a new evaluator, reading the reference in the specified directory.
	 * @param dir
	 * @throws IOException
	 */
	public Evaluator(File dir, boolean removeRW) throws IOException {
		super();
		parseReference(dir, removeRW);
	}

	/**
	 * Loads the reference from the specified directory.
	 * @param dir
	 * @throws IOException
	 */
	public final void parseReference(File dir, boolean removeRW) throws IOException {
		reference.clear();
		reference.parse(dir, removeRW);
	}
	
	/**
	 * Evaluates the specified hypothesis corpus against the currently loaded reference.
	 * @param logger
	 * @param hypothesis
	 */
	private void evaluate(Logger logger, Corpus hypothesis) {
		clear();
		logger.info("evaluating predictions");
		for (Document doc : reference.getDocuments()) {
			if (hypothesis.hasDocument(doc.getId()))
				difference(logger, doc, hypothesis.getDocument(doc.getId()));
			else
				missing(logger, doc);
		}
		for (Document doc : hypothesis.getDocuments()) {
			if (!reference.hasDocument(doc.getId()))
				extra(logger, doc);
		}
	}

	/**
	 * Loads the hypothesis from the specified directory, checks it against the specified schema and evaluates it against the currently loaded reference.
	 * @param logger
	 * @param hypDir
	 * @param schema
	 * @throws IOException
	 */
	public void evaluate(Logger logger, File hypDir, boolean removeRW, Schema schema) throws IOException {
		Corpus hypothesis = reference.copyInput();
		logger.info("parsing prediction directory");
		hypothesis.parseA2(hypDir, removeRW);
		if (schema != null) {
			logger.info("checking prediction contents");
			if (!schema.check(logger, hypothesis))
				return;
		}
		evaluate(logger, hypothesis);
	}

	/**
	 * Returns the currently loaded reference corpus.
	 */
	public Corpus getReference() {
		return reference;
	}
}
