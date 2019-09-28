package com.casualsuperman.portent;

import java.io.Reader;
import java.io.Writer;

public interface TemplateEngine {
	void writeTo(Reader reader, Context context, Writer writer);
}
