package org.bibliome.util.files;

import java.io.File;
import java.util.logging.Logger;


public class OutputDirectory extends AbstractFile {
	private static final long serialVersionUID = 1L;

	public OutputDirectory(String pathname) {
		super(pathname);
	}

	public OutputDirectory(File parent, String name) {
		super(parent, name);
	}

	public OutputDirectory(String parent, String name) {
		super(parent, name);
	}

	@Override
	public boolean check(Logger logger) {
		for (File dir = getAbsoluteFile(); dir != null; dir = dir.getParentFile())
			if (dir.exists())
				return checkDirectory(logger, dir) && checkWritable(logger, dir);
		throw new RuntimeException();
	}
	
	public OutputFile getOutputFile(String name) {
		return new OutputFile(this, name);
	}
}
