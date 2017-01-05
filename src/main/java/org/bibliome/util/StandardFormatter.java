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

package org.bibliome.util;

import java.util.logging.Formatter;
import java.util.logging.Level;
import java.util.logging.LogRecord;

/**
 * Standard log formatter.
 * This formatter just outputs the message, the level is prepended if it is equal or higher than WARNING.
 * @author rbossy
 *
 */
public class StandardFormatter extends Formatter {
	@Override
	public String format(LogRecord record) {
		StringCat strcat = new StringCat();
		if (record.getLevel().intValue() >= Level.WARNING.intValue()) {
			strcat.append(record.getLevel().getName());
			strcat.append(": ");
		}
		strcat.append(record.getMessage());
		strcat.append("\n");
		return strcat.toString();
	}
}
