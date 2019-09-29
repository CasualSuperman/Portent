package com.casualsuperman.portent.exceptions;

/**
 * Exception caused by the execution environment not behaving as expected. File permissions, etc.
 */
public class EnvironmentException extends RuntimeException {
	public EnvironmentException(String message) {
		super(message);
	}

	public EnvironmentException(String message, Exception cause) {
		super(message, cause);
	}
}
