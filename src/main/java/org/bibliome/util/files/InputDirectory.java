package org.bibliome.util.files;

import java.io.File;
import java.util.logging.Logger;


public class InputDirectory extends AbstractFile {
	private static final long serialVersionUID = 1L;

	public InputDirectory(String pathname) {
		super(pathname);
	}

	public InputDirectory(File parent, String name) {
		super(parent, name);
	}

	public InputDirectory(String parent, String name) {
		super(parent, name);
	}

	@Override
	public boolean check(Logger logger) {
		return checkExists(logger) && checkDirectory(logger) && checkReadable(logger);
	}
}
