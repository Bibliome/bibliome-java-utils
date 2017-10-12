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

import java.io.IOException;
import java.io.OutputStream;
import java.util.logging.Logger;

public class OutputStreamTargetStream extends AbstractTargetStream {
	private final OutputStream os;
	private final String name;
	
	public OutputStreamTargetStream(String charset, OutputStream os, String name) {
		super(charset);
		this.os = os;
		this.name = name;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public OutputStream getOutputStream() throws IOException {
		return os;
	}

	@Override
	public boolean check(Logger logger) {
		return true;
	}

	@Override
	public String toString() {
		return name;
	}
}
