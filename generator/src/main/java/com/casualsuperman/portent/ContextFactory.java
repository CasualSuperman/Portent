package com.casualsuperman.portent;

import com.casualsuperman.portent.exceptions.ContextLoadException;
import com.casualsuperman.portent.impl.MapBackedContext;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
public class ContextFactory {
	/**
	 * Variables common to all instances. May include built-in Maven variables, variables configured in the Maven
	 * plugin, or variables specified while using Portent as a library.
	 */
	@NonNull
	private final Map<String, Object> globalVars;

	/**
	 * Puts in global variables, followed by instance meta-variables, followed by variables loaded by the instance.
	 * @param instance The instance to compute context for.
	 * @return The context for the provided instance.
	 */
	@NonNull
	public Context getContext(Instance instance) {
		Map<String, Object> vars = new HashMap<>();
		vars.putAll(globalVars);
		vars.putAll(getDefaultInstanceVars(instance));
		try {
			vars.putAll(instance.loadInstanceVars());
		} catch (IOException e) {
			throw new ContextLoadException(e);
		}
		return new MapBackedContext(vars);
	}

	@NonNull
	private Map<String, Object> getDefaultInstanceVars(Instance instance) {
		Map<String, Object> vars = new HashMap<>();
		vars.put("package", instance.getPackageName());
		vars.put("name", instance.getInstanceName());
		return vars;
	}
}
