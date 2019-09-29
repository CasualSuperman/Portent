package com.casualsuperman.portent.exceptions;

public class ContextLoadException extends ProcessingException {
	public ContextLoadException(Exception e) {
		super("failed to load context", e);
	}
}
