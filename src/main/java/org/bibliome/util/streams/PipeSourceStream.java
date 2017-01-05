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

package org.bibliome.util.streams;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.Collections;
import java.util.logging.Logger;

import org.bibliome.util.files.ExecutableFile;

public class PipeSourceStream extends AbstractSingleSourceStream {
	private final String[] commandArray;
	
	public PipeSourceStream(String charset, CompressionFilter compressionFilter, String[] commandArray) {
		super(charset, compressionFilter);
		this.commandArray = commandArray;
	}

	public PipeSourceStream(String charset, String[] commandArray) {
		super(charset);
		this.commandArray = commandArray;
	}

	@Override
	public InputStream getInputStream() throws IOException {
		Runtime runtime = Runtime.getRuntime();
		Process process = runtime.exec(commandArray);
		try (InputStream result = process.getInputStream()) {
			setStreamName(result, commandArray[0]);
			int retval = process.waitFor();
			if (retval != 0)
				throw new IOException("command " + commandArray[0] + " failed, return value = " + retval);
			return result;
		}
		catch (InterruptedException e) {
			throw new IOException(e);
		}
	}

	@Override
	public boolean check(Logger logger) {
		ExecutableFile command = new ExecutableFile(commandArray[0]);
		return command.check(logger);
	}

	@Override
	public Collection<String> getStreamNames() {
		return Collections.singleton(commandArray[0]);
	}
}
