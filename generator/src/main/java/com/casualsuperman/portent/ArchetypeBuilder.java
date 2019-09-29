package com.casualsuperman.portent;

import com.casualsuperman.portent.impl.DirArchetype;
import com.casualsuperman.portent.impl.FileArtifact;
import lombok.RequiredArgsConstructor;

import java.nio.charset.Charset;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
public class ArchetypeBuilder {
	private final Path basePath;
	private final String name;

	private final List<Path> files = new ArrayList<>();

	public synchronized void addFile(Path file) {
		this.files.add(file);
	}

	public Archetype build(Charset charset) {
		List<Artifact> templates = new ArrayList<>(files.size());
		for (Path f : files) {
			templates.add(new FileArtifact(basePath.resolve(name).toFile(), f, charset));
		}
		return new DirArchetype(name, templates);
	}
}
