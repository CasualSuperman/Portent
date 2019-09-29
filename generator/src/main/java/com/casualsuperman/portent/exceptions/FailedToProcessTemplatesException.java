package com.casualsuperman.portent.exceptions;

import com.casualsuperman.portent.Artifact;
import lombok.Getter;

import java.util.Collections;
import java.util.Map;

@Getter
@SuppressWarnings("squid:S1948")
public class FailedToProcessTemplatesException extends ProcessingException {
	private final Map<Artifact, Exception> failures;

	public FailedToProcessTemplatesException(Map<Artifact, Exception> failures) {
		super("failed to process template results", failures.values().iterator().next());
		this.failures = Collections.unmodifiableMap(failures);
	}
}
