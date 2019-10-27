package com.casualsuperman.portent;

import com.casualsuperman.portent.exceptions.EnvironmentException;
import com.casualsuperman.portent.exceptions.FailedToMoveTemplateResultsException;
import com.casualsuperman.portent.exceptions.FailedToProcessTemplatesException;
import com.casualsuperman.portent.util.FilenameUtils;
import lombok.Getter;
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

	private final Charset targetCharset;

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

		private final Map<Artifact, TargetImpl> targets = new HashMap<>();

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
						Path tempFile = Files.createTempFile(path.getFileName().toString(), ".tmp");
						String templateName =
								archetype.getName() + ":" + artifact.getPath() + "@" + instance.getInstanceName();
						targets.put(artifact, new TargetImpl(tempFile, target, templateName));
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
			for (Map.Entry<Artifact, TargetImpl> artifact : targets.entrySet()) {
				try {
					templateEngine.writeTo(artifact.getKey(), context, artifact.getValue());
				} catch (final Exception e) {
					failures.put(artifact.getKey(), e);
				}
			}
			reportTemplatingFailures(failures);
		}

		public void moveTempFiles() {
			Map<Artifact, Exception> failures = new HashMap<>();
			for (Map.Entry<Artifact, TargetImpl> artifact : targets.entrySet()) {
				try {
					artifact.getValue().moveFile();
				} catch (IOException e) {
					failures.put(artifact.getKey(), e);
				}
			}
			reportMoveFailures(failures);
		}

		private void deleteTempFiles() {
			for (TargetImpl p : targets.values()) {
				p.deleteTempFile();
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

	@RequiredArgsConstructor
	private class TargetImpl implements Target {
		private final Path tempFile;
		private final Path destFile;
		@Getter
		private final String targetId;

		@Override
		public Path getTargetPath() {
			return tempFile;
		}

		@Override
		public Charset getCharset() {
			return targetCharset;
		}

		public void moveFile() throws IOException {
			Files.createDirectories(destFile.toFile().getParentFile().toPath());
			Files.move(tempFile, destFile, StandardCopyOption.REPLACE_EXISTING);
		}

		public void deleteTempFile() {
			try {
				Files.deleteIfExists(tempFile);
			} catch (IOException e) {
				log.warn("Failed to delete temporary file {}", tempFile, e);
			}
		}
	}
}
