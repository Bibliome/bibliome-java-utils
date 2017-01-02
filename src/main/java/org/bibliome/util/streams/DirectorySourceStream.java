package org.bibliome.util.streams;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.logging.Logger;

import org.bibliome.util.files.AbstractFile;
import org.bibliome.util.mappers.FileAbsolutePathMapper;
import org.bibliome.util.mappers.Mapper;
import org.bibliome.util.mappers.Mappers;

public class DirectorySourceStream extends AbstractMultipleSourceStream {
	private final CompressionFilter compressionFilter;
	private final AbstractFile dir;
	private final boolean recursive;
	private final FileFilter filter;

	public DirectorySourceStream(String charset, CompressionFilter compressionFilter, AbstractFile dir, boolean recursive, FileFilter filter) {
		super(charset);
		this.compressionFilter = compressionFilter;
		this.dir = dir;
		this.recursive = recursive;
		this.filter = filter;
	}
	
	private final Mapper<File,InputStream> fileToInputStream = new Mapper<File,InputStream>() {
		@Override
		public InputStream map(File x) {
			try {
				InputStream result = compressionFilter.getInputStream(new FileInputStream(x));
				setStreamName(result, x.getAbsolutePath());
				return result;
			}
			catch (IOException e) {
				throw new RuntimeException(e);
			}
		}
	};
	
	private void collectFiles(Collection<File> collection, boolean children, File... files) {
		for (File file : files) {
			if (file.isDirectory()) {
				if (children)
					collectFiles(collection, recursive, file.listFiles(filter));
			}
			else if (file.isFile()) {
				collection.add(file);
			}
		}
	}
	
	@Override
	public Iterator<InputStream> getInputStreams() throws IOException {
		Collection<File> files = new ArrayList<File>();
		collectFiles(files, true, dir);
		Iterator<File> filesIt = files.iterator();
		return Mappers.apply(fileToInputStream, filesIt);
	}

	@Override
	public boolean check(Logger logger) {
		return dir.check(logger);
	}

	@Override
	protected String getCollectiveName() {
		return dir.getAbsolutePath();
	}

	@Override
	public String toString() {
		return dir.toString();
	}

	@Override
	public Collection<String> getStreamNames() {
		Collection<File> files = new ArrayList<File>();
		collectFiles(files, true, dir);
		return Mappers.mappedCollection(new FileAbsolutePathMapper<File>(), files);
	}
}
