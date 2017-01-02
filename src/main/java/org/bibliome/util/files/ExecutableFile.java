package org.bibliome.util.files;

import java.io.File;
import java.util.logging.Logger;


public class ExecutableFile extends AbstractFile {
	private static final long serialVersionUID = 1L;

	public ExecutableFile(String pathname) {
		super(pathname);
	}

	public ExecutableFile(File parent, String name) {
		super(parent, name);
	}

	public ExecutableFile(String parent, String name) {
		super(parent, name);
	}

	@Override
	public boolean check(Logger logger) {
		return checkExists(logger) && checkRegular(logger) && checkExecutable(logger);
	}
}
