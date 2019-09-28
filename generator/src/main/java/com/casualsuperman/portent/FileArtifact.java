package com.casualsuperman.portent;

import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.io.*;
import java.nio.file.Path;

@ToString
@RequiredArgsConstructor
public class FileArtifact implements Artifact {
	private final File root;
	private final Path path;

	@Override
	public Path getPath() {
		return path;
	}

	@Override
	public Reader getContents() throws FileNotFoundException {
		return new BufferedReader(new FileReader(root.toPath().resolve(path).toFile()));
	}
}
