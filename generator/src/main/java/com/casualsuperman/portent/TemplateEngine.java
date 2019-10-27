package com.casualsuperman.portent;

import java.io.IOException;

public interface TemplateEngine {
	void writeTo(Artifact artifact, Context context, Target target) throws IOException;
}
