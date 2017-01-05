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

package org.bibliome.util.filelines;

import java.util.List;

/**
 * Exception thrown when a line is malformed.
 * @author rbossy
 *
 */
public class InvalidFileLineEntry extends RuntimeException {
    private static final long serialVersionUID = 1L;
    
    private final List<String> entry;
    private final int lineno;

    public InvalidFileLineEntry(List<String> entry, int lineno) {
        super();
        this.entry = entry;
        this.lineno = lineno;
    }

    public InvalidFileLineEntry(String msg, List<String> entry, int lineno) {
        super(msg);
        this.entry = entry;
        this.lineno = lineno;
    }

    /**
     * Returns the faulty entry.
     */
    public List<String> getEntry() {
        return entry;
    }

    /**
     * Returns the line number of the faulty line.
     */
    public int getLineno() {
        return lineno;
    }
}