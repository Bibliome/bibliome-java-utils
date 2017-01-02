package org.bibliome.util.files;

import java.io.File;


public class WorkingDirectory extends OutputDirectory {
	private static final long serialVersionUID = 1L;

	public WorkingDirectory(String pathname) {
		super(pathname);
	}

	public WorkingDirectory(File parent, String name) {
		super(parent, name);
	}

	public WorkingDirectory(String parent, String name) {
		super(parent, name);
	}
}
