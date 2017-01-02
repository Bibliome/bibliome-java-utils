package org.bibliome.util.streams;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.bibliome.util.Strings;
import org.bibliome.util.files.AbstractFile;
import org.bibliome.util.files.FileFactory;
import org.bibliome.util.files.InputDirectory;
import org.bibliome.util.files.InputFile;

public class StreamFactory {
	private String charset = "UTF-8";
	private CompressionFilter compressionFilter = CompressionFilter.NONE;
	private boolean recursive = false;
	private FileFilter filter = null;
	private List<String> inputDirs = null;

	public StreamFactory() {
	}

	public String getCharset() {
		return charset;
	}

	public CompressionFilter getCompressionFilter() {
		return compressionFilter;
	}

	public boolean isRecursive() {
		return recursive;
	}

	public FileFilter getFilter() {
		return filter;
	}

	public List<String> getInputDirs() {
		return inputDirs;
	}

	public void setInputDirs(List<String> inputDirs) {
		this.inputDirs = inputDirs;
	}

	public void setCharset(String charset) {
		this.charset = charset;
	}

	public void setCompressionFilter(CompressionFilter compressionFilter) {
		this.compressionFilter = compressionFilter;
	}

	public void setRecursive(boolean recursive) {
		this.recursive = recursive;
	}

	public void setFilter(FileFilter filter) {
		this.filter = filter;
	}
	
	private static final FileFactory<InputFile> INPUT_FILE_FACTORY = new FileFactory<InputFile>() {
		@Override
		public InputFile createFile(String path) {
			return new InputFile(path);
		}

		@Override
		public InputFile createFile(String parent, String path) {
			return new InputFile(parent, path);
		}
	};
	
	private static final FileFactory<AbstractFile> INPUT_FILE_OR_DIRECTORY_FACTORY = new FileFactory<AbstractFile>() {
		@Override
		public AbstractFile createFile(String path) {
			return createFile(new File(path));
		}

		@Override
		public AbstractFile createFile(String parent, String path) {
			return createFile(new File(parent, path));
		}
		
		private AbstractFile createFile(File file) {
			if (file.isDirectory()) {
				return new InputDirectory(file.getPath());
			}
			return new InputFile(file.getPath());
		}
	};
	
	public DirectorySourceStream getDirectorySourceStream(String path) {
		AbstractFile dir = AbstractFile.getInputFile(INPUT_FILE_OR_DIRECTORY_FACTORY, inputDirs, path);
		return new DirectorySourceStream(charset, compressionFilter, dir, recursive, filter);
	}
	
	public FileSourceStream getFileSourceStream(String path) {
		InputFile file = AbstractFile.getInputFile(INPUT_FILE_FACTORY, inputDirs, path);
		return new FileSourceStream(charset, compressionFilter, file);
	}

	private static String uriToResourceName(URI uri) {
		return uri.getHost().replace('.', '/') + uri.getPath();
	}
	
	public ResourceSourceStream getResourceSourceStream(String name) {
		return new ResourceSourceStream(charset, compressionFilter, name);
	}

	public CollectionSourceStream getSourceStreamList(String path) throws IOException, URISyntaxException {
		Collection<SourceStream> streams = new ArrayList<SourceStream>();
		SourceStream listSource = getFileSourceStream(path);
		try (BufferedReader r = listSource.getBufferedReader()) {
			while (true) {
				String f = r.readLine();
				if (f == null)
					break;
				f = f.trim();
				if (f.isEmpty())
					continue;
				streams.add(getSourceStream(f));
			}
			return new CollectionSourceStream(charset, streams);
		}
	}

	public InputStreamSourceStream getInputStreamSourceStream(InputStream is, String name) {
		return new InputStreamSourceStream(charset, compressionFilter, is, name);
	}

	public PipeSourceStream getPipeSourceStream(String[] commandArray) {
		return new PipeSourceStream(charset, compressionFilter, commandArray);
	}

	private static String[] uriToCommandArray(URI uri) {
		String command = uri.getPath();
		List<String> args = Strings.split(uri.getQuery(), '&', -1);
		String[] commandArray = new String[args.size() + 1];
		commandArray[0] = command;
		for (int i = 1; i < commandArray.length; ++i)
			commandArray[i] = args.get(i - 1);
		return commandArray;
	}

	public URLSourceStream getURLSourceStream(URL url) {
		return new URLSourceStream(charset, compressionFilter, url);
	}

	public SourceStream getSourceStream(String s) throws IOException, URISyntaxException {
		URI uri = null;
		try {
			uri = new URI(s);
		}
		catch (URISyntaxException e) {
			return getFileSourceStream(s);
		}
		String scheme = uri.getScheme();
		if (scheme == null) {
			return getDirectorySourceStream(uri.getPath());
		}
		switch (uri.getScheme()) {
			case "file":
				return getFileSourceStream(uri.getPath());
			case "dir":
				return getDirectorySourceStream(uri.getPath());
			case "resource":
			case "res":
				return getResourceSourceStream(uriToResourceName(uri));
			case "list":
				return getSourceStreamList(uri.getPath());
			case "stream":
				if ("stdin".equals(uri.getHost())) {
					return getInputStreamSourceStream(System.in, "<<stdin>>");
				}
				throw new URISyntaxException(s, "unknown authority: " + uri.getHost());
			case "pipe":
				String[] commandArray = uriToCommandArray(uri);
				return getPipeSourceStream(commandArray);
		}
		return getURLSourceStream(uri.toURL());
	}
}
