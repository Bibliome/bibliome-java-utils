package org.bibliome.util.streams;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.logging.Logger;

import org.bibliome.util.Iterators;
import org.bibliome.util.Strings;
import org.bibliome.util.mappers.Mapper;
import org.bibliome.util.mappers.Mappers;

public class CollectionSourceStream extends AbstractMultipleSourceStream {
	private final Collection<SourceStream> collection;

	public CollectionSourceStream(String charset, Collection<SourceStream> collection) {
		super(charset);
		this.collection = new ArrayList<SourceStream>(collection);
	}

	public CollectionSourceStream(String charset, SourceStream... resources) {
		this(charset, Arrays.asList(resources));
	}

	private static final Mapper<SourceStream,Iterator<InputStream>> sourceStreamToInputStream = new Mapper<SourceStream,Iterator<InputStream>>() {
		@Override
		public Iterator<InputStream> map(SourceStream x) {
			try {
				return x.getInputStreams();
			}
			catch (IOException ioe) {
				throw new RuntimeException(ioe);
			}
		}
	};

	@Override
	public Iterator<InputStream> getInputStreams() throws IOException {
		Iterator<SourceStream> resourcesIt = collection.iterator();
		Iterator<Iterator<InputStream>> isIt = Mappers.apply(sourceStreamToInputStream, resourcesIt);
		return Iterators.flatten(isIt);
	}

	@Override
	public boolean check(Logger logger) {
		boolean result = true;
		for (SourceStream res : collection)
			result = res.check(logger) && result;
		return result;
	}

	@Override
	protected String getCollectiveName() {
		return "<<collection>>";
	}

	@Override
	public String toString() {
		return "collection: " + Strings.joinStrings(collection, ", ");
	}

	@Override
	public Collection<String> getStreamNames() {
		Collection<String> result = new ArrayList<String>(collection.size());
		for (SourceStream stream : collection)
			result.addAll(stream.getStreamNames());
		return result;
	}
}
