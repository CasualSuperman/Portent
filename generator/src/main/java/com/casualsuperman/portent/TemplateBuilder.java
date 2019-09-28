package com.casualsuperman.portent;

import lombok.RequiredArgsConstructor;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
public class TemplateBuilder {
	private final Path basePath;
	private final String name;
	private final List<Path> files = new ArrayList<>();

	public synchronized void addFile(Path file) {
		this.files.add(file);
	}

	public Template build() {
		List<Templatable> templates = new ArrayList<>(files.size());
		for (Path f : files) {
			templates.add(new FileTemplatable(basePath.resolve(name).toFile(), f));
		}
		return new DirTemplate(name, templates);
	}
}
