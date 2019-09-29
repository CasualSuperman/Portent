package com.casualsuperman.portent;

import com.casualsuperman.portent.exceptions.EnvironmentException;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ArchetypeLocator {
	private final Path baseDir;

	private Map<String, ArchetypeBuilder> templates = null;

	public ArchetypeLocator(Path baseDir) {
		this.baseDir = baseDir;
	}

	public synchronized void discover() {
		if (templates == null) {
			templates = new ConcurrentHashMap<>();
			try {
				Files.walkFileTree(baseDir, new SimpleFileVisitor<Path>() {
					@Override
					public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {
						Path rel = baseDir.relativize(file);
						String templateName = rel.getName(0).toString();
						ArchetypeBuilder builder = templates.computeIfAbsent(templateName, n -> new ArchetypeBuilder(baseDir, n));
						if (rel.getNameCount() > 1) {
							builder.addFile(rel.subpath(1, rel.getNameCount()));
						}
						return FileVisitResult.CONTINUE;
					}
				});
			} catch (IOException ex) {
				throw new EnvironmentException("failed to discover template archetypes", ex);
			}
		}
	}

	public Map<String, Archetype> getTemplates(Charset charset) {
		if (templates == null) {
			throw new IllegalStateException("must call discover() before getting templates");
		}
		Map<String, Archetype> results = new HashMap<>();
		for (Map.Entry<String, ArchetypeBuilder> templ : templates.entrySet()) {
			results.put(templ.getKey(), templ.getValue().build(charset));
		}
		return results;
	}
}
