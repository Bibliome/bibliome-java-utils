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

package fr.inra.maiage.bibliome.util.streams;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.logging.Logger;

import fr.inra.maiage.bibliome.util.files.OutputFile;

public class FileTargetStream extends AbstractTargetStream {
	private final OutputFile file;
	private final boolean append;

	public FileTargetStream(String charset, OutputFile file, boolean append) {
		super(charset);
		this.file = file;
		this.append = append;
	}
	
	public FileTargetStream(String charset, OutputFile file) {
		this(charset, file, false);
	}

	public FileTargetStream(String charset, String file, boolean append) {
		this(charset, new OutputFile(file), append);
	}

	public FileTargetStream(String charset, String file) {
		this(charset, new OutputFile(file));
	}

	@Override
	public String getName() {
		return file.getAbsolutePath();
	}

	@Override
	public OutputStream getOutputStream() throws IOException {
		File dir = file.getParentFile();
		if (dir != null)
			dir.mkdirs();
		return new FileOutputStream(file, append);
	}

	@Override
	public boolean check(Logger logger) {
		return file.check(logger);
	}

	@Override
	public String toString() {
		return file.toString();
	}
}
