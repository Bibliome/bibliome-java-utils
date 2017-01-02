package org.bibliome.util.files;

import java.io.File;
import java.util.logging.Logger;

public class OutputFile extends AbstractFile {
	private static final long serialVersionUID = 1L;

	public OutputFile(String pathname) {
		super(pathname);
	}

	public OutputFile(File parent, String name) {
		super(parent, name);
	}

	public OutputFile(String parent, String name) {
		super(parent, name);
	}

	@Override
	public boolean check(Logger logger) {
		if (exists())
			return checkRegular(logger) && checkWritable(logger);
		for (File dir = getAbsoluteFile().getParentFile(); dir != null; dir = dir.getParentFile())
			if (dir.exists())
				return checkDirectory(logger, dir) && checkWritable(logger, dir);
		throw new RuntimeException("none of the parents of " + this + " exists!");
	}
}
