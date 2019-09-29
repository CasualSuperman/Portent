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
	private final boolean overwriteExisting;

	public void constructArchetype(File root, Instance i) {
		InstanceTemplater templater = new InstanceTemplater(i);
		try {
			templater.createTempFiles();
			templater.performArtifactTemplating();
			templater.moveTempFiles(root);
		} finally {
			templater.deleteTempFiles();
		}
	}

	private class InstanceTemplater {
		private final Instance instance;
		private final Context context;

		private final Map<Artifact, Path> tempFiles = new HashMap<>();

		public InstanceTemplater(Instance instance) {
			this.instance = instance;
			this.context = contextFactory.getContext(instance);
		}

		public void createTempFiles() {
			try {
				for (Artifact artifact : archetype.getArtifacts()) {
					tempFiles.put(artifact, Files.createTempFile(artifact.getPath().getFileName().toString(), ".tmp"));
				}
			} catch (IOException ex) {
				throw new EnvironmentException("failed to create temp files", ex);
			}
		}

		public void performArtifactTemplating() {
			Map<Artifact, Exception> failures = new HashMap<>();
			for (Map.Entry<Artifact, Path> artifact : tempFiles.entrySet()) {
				try (Writer writer = new BufferedWriter(new OutputStreamWriter(
						new FileOutputStream(artifact.getValue().toFile()), charset))) {
					templateEngine.writeTo(artifact.getKey().getContents(), context, writer);
				} catch (final Exception e) {
					failures.put(artifact.getKey(), e);
				}
			}
			reportTemplatingFailures(failures);
		}

		public void moveTempFiles(File root) {
			Map<Artifact, Exception> failures = new HashMap<>();
			File relRoot = instance.getTargetDirectory(root);
			for (Map.Entry<Artifact, Path> artifact : tempFiles.entrySet()) {
				try {
					Path target = getArtifactTarget(relRoot, artifact.getKey().getPath(), context);
					if (overwriteExisting || !target.toFile().exists()) {
						Files.createDirectories(target.toFile().getParentFile().toPath());
						Files.move(artifact.getValue(), target, StandardCopyOption.REPLACE_EXISTING);
					} else {
						log.debug("Skipping existing file {}", target);
					}
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
