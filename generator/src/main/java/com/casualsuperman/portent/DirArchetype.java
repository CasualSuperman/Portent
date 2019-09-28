package com.casualsuperman.portent;

import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.util.List;

@ToString
@RequiredArgsConstructor
public class DirArchetype implements Archetype {
	private final String name;
	private final List<Artifact> files;

	@Override
	public String getName() {
		return name;
	}

	@Override
	public List<Artifact> getTemplates() {
		return files;
	}
}
