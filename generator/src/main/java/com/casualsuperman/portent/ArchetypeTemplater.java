package com.casualsuperman.portent;

import com.casualsuperman.portent.exceptions.EnvironmentException;
import com.casualsuperman.portent.exceptions.FailedToMoveTemplateResultsException;
import com.casualsuperman.portent.exceptions.FailedToProcessTemplatesException;
import com.casualsuperman.portent.util.FilenameUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
public class ArchetypeTemplater {
	private final Archetype      archetype;
	private final TemplateEngine templateEngine;
	private final ContextFactory contextFactory;

	private final Charset charset;

	public void constructArchetype(File root, Instance i, boolean overwriteExisting) {
		InstanceTemplater templater = new InstanceTemplater(i, overwriteExisting);
		
		try {
			templater.createTempFiles(root);
			templater.performArtifactTemplating();
			templater.moveTempFiles();
		} finally {
			templater.deleteTempFiles();
		}
	}

	// TODO: Warn or error if target filenames will overlap after templating.
	private class InstanceTemplater {
		private final Instance instance;
		private final Context context;
		private final boolean overwriteExisting;

		private final Map<Artifact, Path> tempFiles = new HashMap<>();
		private final Map<Artifact, Path> targetFiles = new HashMap<>();

		public InstanceTemplater(Instance instance, boolean overwriteExisting) {
			this.instance = instance;
			this.context = contextFactory.getContext(instance);
			this.overwriteExisting = overwriteExisting;
		}

		public void createTempFiles(File root) {
			try {
				File relRoot = instance.getTargetDirectory(root);
				for (Artifact artifact : archetype.getArtifacts()) {
					Path path = artifact.getPath();
					Path target = getArtifactTarget(relRoot, path, context);
					if (overwriteExisting || !target.toFile().exists()) {
						tempFiles.put(artifact, Files.createTempFile(path.getFileName().toString(), ".tmp"));
						targetFiles.put(artifact, target);
					} else {
						log.debug("Skipping existing file {}", target);
					}
				}
			} catch (IOException ex) {
				throw new EnvironmentException("failed to create temp files", ex);
			}
		}

		public void performArtifactTemplating() {
			Map<Artifact, Exception> failures = new HashMap<>();
			for (Map.Entry<Artifact, Path> artifact : tempFiles.entrySet()) {
				String templateName =
						archetype.getName() + ":" + artifact.getKey().getPath() + "@" + instance.getInstanceName();
				try (Writer writer = Files.newBufferedWriter(artifact.getValue(), charset)) {
					templateEngine.writeTo(templateName, artifact.getKey().getContents(), context, writer);
				} catch (final Exception e) {
					failures.put(artifact.getKey(), e);
				}
			}
			reportTemplatingFailures(failures);
		}

		public void moveTempFiles() {
			Map<Artifact, Exception> failures = new HashMap<>();
			for (Map.Entry<Artifact, Path> artifact : tempFiles.entrySet()) {
				try {
					Path target = targetFiles.get(artifact.getKey());
					Files.createDirectories(target.toFile().getParentFile().toPath());
					Files.move(artifact.getValue(), target, StandardCopyOption.REPLACE_EXISTING);
				} catch (IOException e) {
					failures.put(artifact.getKey(), e);
				}
			}
			reportMoveFailures(failures);
		}

		private void deleteTempFiles() {
			for (Path p : tempFiles.values()) {
				try {
					Files.deleteIfExists(p);
				} catch (IOException e) {
					log.warn("Failed to delete temporary file {}", p, e);
				}
			}
		}

		private void reportMoveFailures(Map<Artifact, Exception> failures) {
			if (!failures.isEmpty()) {
				throw new FailedToMoveTemplateResultsException(failures);
			}
		}

		private void reportTemplatingFailures(Map<Artifact, Exception> failures) {
			if (!failures.isEmpty()) {
				throw new FailedToProcessTemplatesException(failures);
			}
		}

		private Path getArtifactTarget(File root, Path path, Context context) {
			String fileName = FilenameUtils.templateFilename(path.getFileName().toString(), context);
			return root.toPath().resolve(path.resolveSibling(fileName));
		}
	}
}
