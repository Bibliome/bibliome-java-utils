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
