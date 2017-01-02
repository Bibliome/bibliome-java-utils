package org.bibliome.util.files;

import java.io.File;
import java.util.List;
import java.util.logging.Logger;

import org.bibliome.util.Checkable;

public abstract class AbstractFile extends File implements Checkable {
	private static final long serialVersionUID = 1L;

	public AbstractFile(String pathname) {
		super(pathname);
	}

	public AbstractFile(File parent, String name) {
		super(parent, name);
	}

	public AbstractFile(String parent, String name) {
		super(parent, name);
	}
	
	protected boolean checkExists(Logger logger) {
		return checkExists(logger, this);
	}
	
	protected boolean checkDirectory(Logger logger) {
		return checkDirectory(logger, this);
	}
	
	protected boolean checkRegular(Logger logger) {
		return checkRegular(logger, this);
	}
	
	protected boolean checkReadable(Logger logger) {
		return checkReadable(logger, this);
	}
	
	protected boolean checkWritable(Logger logger) {
		return checkWritable(logger, this);
	}
	
	public static boolean checkExecutable(Logger logger, File f) {
		if (f.canExecute())
			return true;
		logger.severe(f.getAbsolutePath() + " is not executable");
		return false;
	}

	protected static boolean checkExists(Logger logger, File f) {
		if (f.exists())
			return true;
		logger.severe(f.getAbsolutePath() + " does not exist");
		return false;
	}
	
	protected static boolean checkDirectory(Logger logger, File f) {
		if (f.isDirectory())
			return true;
		logger.severe(f.getAbsolutePath() + " is not a directory");
		return false;
	}
	
	protected static boolean checkRegular(Logger logger, File f) {
		if (f.isFile())
			return true;
		logger.severe(f.getAbsolutePath() + " is not a regular file");
		return false;
	}
	
	protected static boolean checkReadable(Logger logger, File f) {
		if (f.canRead())
			return true;
		logger.severe(f.getAbsolutePath() + " cannot be read");
		return false;
	}
	
	protected static boolean checkWritable(Logger logger, File f) {
		if (f.canWrite())
			return true;
		logger.severe(f.getAbsolutePath() + " cannot be written");
		return false;
	}
	
	protected boolean checkExecutable(Logger logger) {
		if (canExecute())
			return true;
		logger.severe(getAbsolutePath() + " is not executable");
		return false;
	}
	
	public static <T extends File> T getInputFile(FileFactory<T> ff, List<String> inputDirs, String path) {
		T file = ff.createFile(path);
		if (file.isAbsolute()) {
			return file;
		}
		if (inputDirs == null || inputDirs.isEmpty()) {
			return file;
		}
		for (String dir : inputDirs) {
			T f = ff.createFile(dir, path);
			if (f.exists()) {
				return f;
			}
		}
		return file;
	}
	
	public static <T extends File> T getOutputFile(FileFactory<T> ff, String outputDir, String path) {
		T file = ff.createFile(path);
		if (file.isAbsolute()) {
			return file;
		}
		if (outputDir == null) {
			return file;
		}
		return ff.createFile(outputDir, path);
	}
}
