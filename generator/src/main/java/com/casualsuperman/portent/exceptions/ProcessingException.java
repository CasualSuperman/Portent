package com.casualsuperman.portent.exceptions;

/**
 * Exception caused by some form of processing. Invalid templates, bad directory layout, etc.
 */
public class ProcessingException extends RuntimeException {
	public ProcessingException(String message) {
		super(message);
	}

	public ProcessingException(String message, Throwable cause) {
		super(message, cause);
	}
}
