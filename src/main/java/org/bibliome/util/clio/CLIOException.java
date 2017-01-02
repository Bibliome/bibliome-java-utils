package org.bibliome.util.clio;

/**
 * Exception thrown when a command line argument parser.
 * @author rbossy
 *
 */
public class CLIOException extends Exception {
	private static final long serialVersionUID = 1L;

	public CLIOException() {
		super();
	}

	public CLIOException(String message, Throwable cause) {
		super(message, cause);
	}

	public CLIOException(String message) {
		super(message);
	}

	public CLIOException(Throwable cause) {
		super(cause);
	}
}
