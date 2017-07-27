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

package org.bibliome.util.streams;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
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
				if (children) {
					File[] childrenFiles = file.listFiles(filter);
					Arrays.sort(childrenFiles);
					collectFiles(collection, recursive, childrenFiles);
				}
			}
			else if (file.isFile()) {
				collection.add(file);
			}
		}
	}
	
	@Override
	public Iterator<InputStream> getInputStreams() throws IOException {
		List<File> files = new ArrayList<File>();
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
