package com.casualsuperman.portent;

import lombok.Getter;

import java.util.Collections;
import java.util.Map;

@Getter
public class FailedToMoveTemplateResultsException extends RuntimeException {
	private final Map<Artifact, Exception> failures;

	public FailedToMoveTemplateResultsException(Map<Artifact, Exception> failures) {
		super("failed to move results to their target location", failures.values().iterator().next());
		this.failures = Collections.unmodifiableMap(failures);
	}
}
