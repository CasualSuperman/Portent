package com.casualsuperman.portent;

import java.io.IOException;
import java.io.Writer;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;

public interface Target {
	String getTargetId();

	Path getTargetPath();

	Charset getCharset();

	default Writer getWriter() throws IOException {
		return Files.newBufferedWriter(getTargetPath(), getCharset());
	}
}
