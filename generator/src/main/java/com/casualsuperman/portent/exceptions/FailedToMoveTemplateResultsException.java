package com.casualsuperman.portent.exceptions;

import com.casualsuperman.portent.Artifact;
import lombok.Getter;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Getter
@SuppressWarnings("squid:S1948")
public class FailedToMoveTemplateResultsException extends EnvironmentException {
	private final Map<Artifact, Exception> failures;

	public FailedToMoveTemplateResultsException(Map<Artifact, Exception> failures) {
		super("failed to move results to their target location", failures.values().iterator().next());
		this.failures = Collections.unmodifiableMap(new HashMap<>(failures));
	}
}
