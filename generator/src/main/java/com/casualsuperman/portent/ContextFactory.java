package com.casualsuperman.portent;

import lombok.RequiredArgsConstructor;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
public class ContextFactory {
	private final Map<String, Object> globalVars;

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

	private Map<String, Object> getDefaultInstanceVars(Instance instance) {
		Map<String, Object> vars = new HashMap<>();
		vars.put("package", instance.getPackageName());
		vars.put("filename", instance.getInstanceName());
		return vars;
	}
}
