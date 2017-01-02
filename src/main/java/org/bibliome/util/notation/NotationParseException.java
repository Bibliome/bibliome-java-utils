package org.bibliome.util.notation;

public class NotationParseException extends Exception {
	private static final long serialVersionUID = -924012011563294463L;

	public NotationParseException() {
		super();
	}

	public NotationParseException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public NotationParseException(String message, Throwable cause) {
		super(message, cause);
	}

	public NotationParseException(String message) {
		super(message);
	}

	public NotationParseException(Throwable cause) {
		super(cause);
	}
}
