/**
 * 
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