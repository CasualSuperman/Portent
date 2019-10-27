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
	public void writeTo(Artifact artifact, Context context, Target target) throws IOException {
		VelocityContext vContext = new VelocityContext(context.getVariables());
		try (Reader reader = artifact.getContents(); Writer writer = target.getWriter()) {
			velocity.evaluate(vContext, writer, target.getTargetId(), reader);
		}
	}
}
