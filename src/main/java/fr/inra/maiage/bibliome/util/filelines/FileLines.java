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

package fr.inra.maiage.bibliome.util.filelines;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.logging.Logger;

import fr.inra.maiage.bibliome.util.Strings;
import fr.inra.maiage.bibliome.util.streams.SourceStream;

/**
 * Reads column-based files.
 * @author rbossy
 *
 * @param <D>
 */
public abstract class FileLines<D> {
    private final TabularFormat format;
    private Logger logger = null;

    public FileLines() {
    	this(new TabularFormat());
    }
    
    public FileLines(TabularFormat format) {
		super();
		this.format = format;
	}
    
    public FileLines(Logger logger) {
    	this();
    	this.logger = logger;
    }
    
    public FileLines(TabularFormat format, Logger logger) {
		this(format);
		this.logger = logger;
	}

	/**
     * Issue a warning for the specified entry.
     * @param entry entry pointed by the warning
     * @param lineno line number of the entry
     * @param msg warning message
     * @param strict either to throw an exception
     * @throws InvalidFileLineEntry
     */
    protected void warning(List<String> entry, int lineno, String msg, boolean strict) throws InvalidFileLineEntry {
        msg = "at line " + lineno + " " + entry + ": " + msg;
        if (strict)
            throw new InvalidFileLineEntry(msg, entry, lineno);
        if (logger == null)
            return;
        logger.warning(msg);
    }
    
    /**
     * Processes the specified line.
     * @param line line to process
     * @param data data object
     * @param lineno line number of the line
     * @throws InvalidFileLineEntry
     */
    public void process(String line, D data, int lineno) throws InvalidFileLineEntry {
        if (format.isSkipEmpty() && line.isEmpty())
            return;
        if (format.isSkipBlank() && line.trim().isEmpty())
            return;
        List<String> entry = Strings.split(line, format.getSeparator(), format.getColumnLimit());
        if (entry.size() < format.getMinColumns()) {
            String msg = String.format("expected " + format.getMinColumns() + " columns or more, got " + entry.size());
            warning(entry, lineno, msg, format.isStrictColumnNumber());
        }
        if (entry.size() > format.getMaxColumns()) {
            String msg = String.format("expected " + format.getMaxColumns() + " columns or less, got " + entry.size());
            warning(entry, lineno, msg, format.isStrictColumnNumber());
        }
        if (format.isTrimColumns())
        	for (int i = 0; i < entry.size(); ++i)
        		entry.set(i, entry.get(i).trim());
        if (format.isNullifyEmpty())
        	for (int i = 0; i < entry.size(); ++i)
        		if (entry.get(i).isEmpty())
        			entry.set(i, null);
        processEntry(data, lineno, entry);
    }
    
    /**
     * Processes the specified reader line by line.
     * @param reader
     * @param data
     * @param lineno
     * @throws IOException
     * @throws InvalidFileLineEntry
     */
    public void process(BufferedReader reader, D data, int lineno) throws IOException, InvalidFileLineEntry {
    	while (true) {
    		String line = reader.readLine();
    		if (line == null)
    			break;
            process(line, data, ++lineno);    		
    	}
    }
    
    /**
     * Processes the specified entry.
     * @param data
     * @param lineno
     * @param entry
     * @throws InvalidFileLineEntry
     */
    public abstract void processEntry(D data, int lineno, List<String> entry) throws InvalidFileLineEntry;
    
    /**
     * Processes the specified reader line by line.
     * @param reader
     * @param data
     * @throws IOException
     * @throws InvalidFileLineEntry
     */
    public void process(BufferedReader reader, D data) throws IOException, InvalidFileLineEntry {
        process(reader, data, 0);
    }

    /**
     * Processes the specified file.
     * @param file
     * @param data
     * @throws IOException
     * @throws InvalidFileLineEntry
     */
    public void process(SourceStream file, D data) throws IOException, InvalidFileLineEntry {
        BufferedReader reader = file.getBufferedReader();
        process(reader, data);
        reader.close();
    }
    
    /**
     * Processes the specified file.
     * @param file
     * @param data
     * @throws IOException
     * @throws InvalidFileLineEntry
     */
    public void process(File file, String charset, D data) throws IOException, InvalidFileLineEntry {
    	BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), charset));
        process(reader, data);
        reader.close();
    }

    /**
     * Returns the current logger.
     */
    public Logger getLogger() {
        return logger;
    }

    /**
	 * Sets the logger.
	 * @param logger
	 */
	public void setLogger(Logger logger) {
        this.logger = logger;
    }

	/**
	 * Returns the tabular format.
	 */
	public TabularFormat getFormat() {
		return format;
	}
}
