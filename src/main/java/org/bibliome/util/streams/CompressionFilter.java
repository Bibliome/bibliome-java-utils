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
import java.util.zip.GZIPInputStream;

public enum CompressionFilter {
	NONE {
		@Override
		public InputStream getInputStream(InputStream is, String streamName) throws IOException {
			return is;
		}
	},
	
	GZIP {
		@Override
		public InputStream getInputStream(InputStream is, String streamName) throws IOException {
			return new GZIPInputStream(is);
		}
	},
	
	FILE_EXTENSION {
		@Override
		public InputStream getInputStream(InputStream is, String streamName) throws IOException {
			if (streamName.endsWith(".gz")) {
				return GZIP.getInputStream(is, streamName);
			}
			return NONE.getInputStream(is, streamName);
		}
	};
	
	public abstract InputStream getInputStream(InputStream is, String streamName) throws IOException;
}
