package com.casualsuperman.portent;

import java.io.IOException;
import java.io.Reader;
import java.nio.file.Path;

public interface Artifact {
	Path getPath();

	Reader getContents() throws IOException;

	String getArchetypeName();
}
