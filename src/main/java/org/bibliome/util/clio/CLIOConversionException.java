package org.bibliome.util.clio;

/**
 * Exception thrown when a command line argument could not be converted to the required type.
 * @author rbossy
 *
 */
public class CLIOConversionException extends CLIOException {
	private static final long serialVersionUID = 1L;

	public CLIOConversionException() {
		super();
	}

	public CLIOConversionException(String message, Throwable cause) {
		super(message, cause);
	}

	public CLIOConversionException(String message) {
		super(message);
	}

	public CLIOConversionException(Throwable cause) {
		super(cause);
	}
}
