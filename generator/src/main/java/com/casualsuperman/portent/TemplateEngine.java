package com.casualsuperman.portent;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;

public interface TemplateEngine {
	void writeTo(String templateName, Reader reader, Context context, Writer writer) throws IOException;
}
