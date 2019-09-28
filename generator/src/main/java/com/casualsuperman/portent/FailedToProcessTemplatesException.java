package com.casualsuperman.portent;

import lombok.Getter;

import java.util.Collections;
import java.util.Map;

@Getter
public class FailedToProcessTemplatesException extends RuntimeException {
	private final Map<Artifact, Exception> failures;

	public FailedToProcessTemplatesException(Map<Artifact, Exception> failures) {
		super("failed to process template results", failures.values().iterator().next());
		this.failures = Collections.unmodifiableMap(failures);
	}
}
