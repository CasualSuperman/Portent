package com.casualsuperman.portent.impl;

import com.casualsuperman.portent.Artifact;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.Path;

@ToString
@RequiredArgsConstructor
public class FileArtifact implements Artifact {
	private final File root;
	private final Path path;

	private final Charset charset;

	@Override
	public Path getPath() {
		return path;
	}

	@Override
	public Reader getContents() throws FileNotFoundException {
		File file = root.toPath().resolve(path).toFile();
		return new InputStreamReader(new BufferedInputStream(new FileInputStream(file)), charset);
	}
}
