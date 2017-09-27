package org.bibliome.util.pubmed;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.logging.Logger;
import java.util.regex.Matcher;

import org.bibliome.util.filters.Filter;
import org.bibliome.util.filters.Filters;
import org.bibliome.util.mappers.Mapper;
import org.bibliome.util.mappers.Mappers;
import org.bibliome.util.mappers.ToStringMapper;
import org.bibliome.util.streams.AbstractMultipleSourceStream;
import org.bibliome.util.streams.CompressionFilter;
import org.bibliome.util.streams.SourceStream;
import org.bibliome.util.streams.URLSourceStream;

class PubMedListingSourceStream extends AbstractMultipleSourceStream {
	private final URL baseURL;
	private final Collection<URL> files = new LinkedHashSet<URL>();
	private final FileFilter fileFilter;

	PubMedListingSourceStream(String baseLocation, FileFilter fileFilter) throws MalformedURLException, IOException {
		this(new URL(baseLocation), fileFilter);
	}
	
	private PubMedListingSourceStream(URL baseURL, FileFilter fileFilter) throws IOException {
		super("UTF-8");
		this.baseURL = baseURL;
		this.fileFilter = fileFilter;
		SourceStream source = new URLSourceStream("UTF-8", CompressionFilter.NONE, baseURL);
		try (BufferedReader r = source.getBufferedReader()) {
			while (true) {
				String line = r.readLine();
				if (line == null) {
					break;
				}
				int space = line.lastIndexOf(' ');
				String filename = line.substring(space + 1);
				Matcher m = PubMedIndexUpdater.PUBMED_FILENAME_PATTERN.matcher(filename);
				if (m.matches()) {
					URL url = new URL(baseURL, filename);
					files.add(url);
				}
			}
		}
	}

	@Override
	public Collection<String> getStreamNames() {
		return Mappers.apply(new ToStringMapper<URL>(), files, new ArrayList<String>());
	}
	
	private final Mapper<URL,InputStream> URL_TO_INPUT_STREAM = new Mapper<URL,InputStream>() {
		@Override
		public InputStream map(URL x) {
			try {
				String streamName = x.toString();
				InputStream result = CompressionFilter.FILE_EXTENSION.getInputStream(x.openStream(), streamName);
				setStreamName(result, streamName);
				return result;
			}
			catch (IOException e) {
				throw new RuntimeException(e);
			}
		}
	};
	
	private final Filter<URL> URL_FILTER = new Filter<URL>() {
		@Override
		public boolean accept(URL x) {
			String fn = x.getFile();
			System.err.println("x = " + x);
			return fileFilter.accept(new File(fn));
		}
	};
	
	@Override
	public Iterator<InputStream> getInputStreams() throws IOException {
		return Mappers.apply(URL_TO_INPUT_STREAM, Filters.apply(URL_FILTER, files.iterator()));
	}

	@Override
	public boolean check(Logger logger) {
		return true;
	}

	@Override
	protected String getCollectiveName() {
		return baseURL.toString();
	}
}
