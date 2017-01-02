package org.bibliome.util.bionlpst;

@SuppressWarnings("serial")
public class BioNLPSTException extends Exception {
	public BioNLPSTException() {
		super();
	}

	public BioNLPSTException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public BioNLPSTException(String message, Throwable cause) {
		super(message, cause);
	}

	public BioNLPSTException(String message) {
		super(message);
	}

	public BioNLPSTException(Throwable cause) {
		super(cause);
	}
}
