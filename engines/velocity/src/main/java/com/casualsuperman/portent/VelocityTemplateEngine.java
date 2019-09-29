package com.casualsuperman.portent;

import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;

import java.io.*;

public class VelocityTemplateEngine implements TemplateEngine {
	private final VelocityEngine velocity = new VelocityEngine();

	public VelocityTemplateEngine() {
		velocity.init();
	}

	@Override
	public void writeTo(String templateName, Reader reader, Context context, Writer writer) {
		VelocityContext vContext = new VelocityContext(context.getVariables());
		velocity.evaluate(vContext, writer, templateName, reader);
	}
}
