package com.casualsuperman.portent;

import com.casualsuperman.portent.util.FilenameUtils;
import lombok.RequiredArgsConstructor;

import java.nio.file.FileVisitResult;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@RequiredArgsConstructor
public class InstanceLocator extends SimpleFileVisitor<Path> {
	private final Path root;
	private final Set<String> templateTypes;

	private final Map<String, List<Path>> instances = new ConcurrentHashMap<>();

	@Override
	public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {
		String name = file.getFileName().toString();
		String extension = FilenameUtils.getExtension(name);
		if (templateTypes.contains(extension)) {
			instances.computeIfAbsent(extension, t -> new ArrayList<>()).add(root.relativize(file));
		}
		return FileVisitResult.CONTINUE;
	}

	public Map<String, List<Path>> getTemplateInstances() {
		return Collections.unmodifiableMap(instances);
	}

}
