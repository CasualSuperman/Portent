package com.casualsuperman.portent;

import com.casualsuperman.portent.exceptions.EnvironmentException;
import com.casualsuperman.portent.util.FilenameUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class InstanceLocator {
	private final File root;
	private final Set<String> templateTypes;

	private Map<String, List<Instance>> instances = null;

	public InstanceLocator(File root, Set<String> templateTypes) {
		this.root = root;
		this.templateTypes = templateTypes;
	}

	public synchronized void discover() {
		if (instances == null) {
			this.instances = new ConcurrentHashMap<>();
			try {
				Files.walkFileTree(root.toPath(), new SimpleFileVisitor<Path>() {
					@Override
					public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {
						String name = file.getFileName().toString();
						String extension = FilenameUtils.getExtension(name);
						if (templateTypes.contains(extension)) {
							instances.computeIfAbsent(extension, t -> new ArrayList<>())
									.add(new Instance(root, root.toPath().relativize(file)));
						}
						return FileVisitResult.CONTINUE;
					}
				});
			} catch (IOException ex) {
				throw new EnvironmentException("failed to scan for instances", ex);
			}
		}
	}

	public Map<String, List<Instance>> getTemplateInstances() {
		if (instances == null) {
			throw new IllegalStateException("must call discover() before getting instances");
		}
		return Collections.unmodifiableMap(instances);
	}

}
