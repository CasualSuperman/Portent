package com.casualsuperman.portent;

public class TemplatingFailedException extends RuntimeException {
	public TemplatingFailedException(String message, Exception e) {
		super(message, e);
	}
}
