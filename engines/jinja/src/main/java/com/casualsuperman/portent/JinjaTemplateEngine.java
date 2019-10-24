package com.casualsuperman.portent;

import com.google.common.io.CharStreams;
import com.hubspot.jinjava.Jinjava;

import java.io.*;
import java.util.Map;

public class JinjaTemplateEngine implements TemplateEngine {
	private final Jinjava jinjava = new Jinjava();

	@Override
	public void writeTo(String templateName, Reader reader, Context context, Writer writer) throws IOException {
		Map<String, Object> jContext = context.getVariables();
		writer.write(jinjava.render(CharStreams.toString(reader), jContext));
	}
}
