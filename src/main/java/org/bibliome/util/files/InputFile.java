package org.bibliome.util.files;

import java.io.File;
import java.util.logging.Logger;

public class InputFile extends AbstractFile {
	private static final long serialVersionUID = 1L;

	public InputFile(String pathname) {
		super(pathname);
	}

	public InputFile(File parent, String name) {
		super(parent, name);
	}

	public InputFile(String parent, String name) {
		super(parent, name);
	}

	@Override
	public boolean check(Logger logger) {
		return checkExists(logger) && checkRegular(logger) && checkReadable(logger);
	}
}
