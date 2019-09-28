package com.casualsuperman.portent;

import lombok.RequiredArgsConstructor;

import java.nio.file.FileVisitResult;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@RequiredArgsConstructor
public class TemplateLocator extends SimpleFileVisitor<Path> {
	private final Path baseDir;

	private final Map<String, TemplateBuilder> templates = new ConcurrentHashMap<>();

	@Override
	public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {
		Path rel = baseDir.relativize(file);
		String templateName = rel.getName(0).toString();
		TemplateBuilder builder = templates.computeIfAbsent(templateName,  n -> new TemplateBuilder(baseDir, n));
		if (rel.getNameCount() > 1) {
			builder.addFile(rel.subpath(1, rel.getNameCount()));
		}
		return FileVisitResult.CONTINUE;
	}

	public Map<String, Template> getTemplates() {
		Map<String, Template> results = new HashMap<>();
		for (Map.Entry<String, TemplateBuilder> templ : templates.entrySet()) {
			results.put(templ.getKey(), templ.getValue().build());
		}
		return results;
	}
}
