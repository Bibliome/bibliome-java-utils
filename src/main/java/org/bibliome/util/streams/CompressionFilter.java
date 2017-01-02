package org.bibliome.util.streams;

import java.io.IOException;
import java.io.InputStream;
import java.util.zip.GZIPInputStream;

public enum CompressionFilter {
	NONE {
		@Override
		public InputStream getInputStream(InputStream is) throws IOException {
			return is;
		}
	},
	
	GZIP {
		@Override
		public InputStream getInputStream(InputStream is) throws IOException {
			return new GZIPInputStream(is);
		}
	};
	
	public abstract InputStream getInputStream(InputStream is) throws IOException;
}
