package org.bibliome.util.files;

import java.io.File;

public interface FileFactory<T extends File> {
	T createFile(String path);
	T createFile(String parent, String path);
}
