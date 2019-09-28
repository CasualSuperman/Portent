package com.casualsuperman.portent;

public class ContextLoadException extends RuntimeException {
	public ContextLoadException(Exception e) {
		super("failed to load context", e);
	}
}
