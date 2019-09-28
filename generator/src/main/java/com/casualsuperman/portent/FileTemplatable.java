package com.casualsuperman.portent;

import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.io.*;
import java.nio.file.Path;

@ToString
@RequiredArgsConstructor
public class FileTemplatable implements Templatable {
	private final File root;
	private final Path path;

	@Override
	public Path getPath() {
		return path;
	}

	@Override
	public InputStream getContents() throws FileNotFoundException {
		return new BufferedInputStream(new FileInputStream(root.toPath().resolve(path).toFile()));
	}
}
