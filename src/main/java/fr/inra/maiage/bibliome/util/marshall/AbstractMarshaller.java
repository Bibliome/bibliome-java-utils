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

package fr.inra.maiage.bibliome.util.marshall;

import java.io.IOException;
import java.nio.channels.FileChannel;

public abstract class AbstractMarshaller<T> {
	protected final FileChannel channel;
	
	protected AbstractMarshaller(FileChannel channel) {
		super();
		this.channel = channel;
	}
	
	public static int getPosition(FileChannel channel) throws IOException {
		long result = channel.position();
		if (result > Integer.MAX_VALUE)
			throw new IOException("position exceeds 32-bit value");
		return (int) result;
	}
	
	protected int getPosition() throws IOException {
		return getPosition(channel);
	}

	public FileChannel getChannel() {
		return channel;
	}
}
