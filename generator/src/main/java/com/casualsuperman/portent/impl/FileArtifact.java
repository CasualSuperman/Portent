package com.casualsuperman.portent.impl;

import com.casualsuperman.portent.Artifact;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;

@ToString
@RequiredArgsConstructor
public class FileArtifact implements Artifact {
	@Getter
	private final String archetypeName;
	private final File root;
	private final Path path;

	private final Charset charset;

	@Override
	public Path getPath() {
		return path;
	}

	@Override
	public Reader getContents() throws IOException {
		return Files.newBufferedReader(root.toPath().resolve(path), charset);
	}
}
