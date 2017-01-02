package org.bibliome.util.tomap.readers;

import java.io.File;
import java.io.InputStream;
import java.util.logging.Logger;

import org.bibliome.util.streams.SourceStream;
import org.bibliome.util.tomap.StringNormalization;
import org.bibliome.util.tomap.Token;
import org.bibliome.util.tomap.TokenFactory;
import org.bibliome.util.tomap.TokenNormalization;
import org.bibliome.util.tomap.readers.AbstractReader.ReaderResult;

public abstract class AbstractReader<T extends ReaderResult> {
	protected final Logger logger;
	private final TokenFactory tokenFactory = new TokenFactory();
	private final TokenNormalization tokenNormalization;
	private final StringNormalization stringNormalization;
	
	protected AbstractReader(Logger logger, TokenNormalization tokenNormalization, StringNormalization stringNormalization) {
		super();
		this.logger = logger;
		this.tokenNormalization = tokenNormalization;
		this.stringNormalization = stringNormalization;
	}

	protected Token getToken(String form, String lemma, String pos) {
		Token t = new Token(form, lemma, pos);
		tokenNormalization.normalize(stringNormalization, t);
		return tokenFactory.getToken(t);
	}
	
	public static abstract class ReaderResult {}
	
	public abstract T parseFile(File file) throws Exception;
	
	public abstract T parseStream(InputStream stream) throws Exception;
	
	public T parseSource(SourceStream source) throws Exception {
		return parseStream(source.getInputStream());
	}
}
