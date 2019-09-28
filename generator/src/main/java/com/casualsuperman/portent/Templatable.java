package com.casualsuperman.portent;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;

public interface Templatable {
	Path getPath();

	InputStream getContents() throws IOException;
}
