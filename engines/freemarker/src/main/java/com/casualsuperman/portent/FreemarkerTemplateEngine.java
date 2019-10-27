package com.casualsuperman.portent;

import com.casualsuperman.portent.exceptions.ProcessingException;
import freemarker.cache.StringTemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import freemarker.template.Version;
import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.util.Map;

@Slf4j
public class FreemarkerTemplateEngine implements TemplateEngine {
	private static final int BUF_SIZE = 1024;

	private final Configuration freemarker;
	private final StringTemplateLoader templateLoader = new StringTemplateLoader();

	public FreemarkerTemplateEngine() {
		freemarker = new Configuration(Configuration.VERSION_2_3_29);
		freemarker.setTemplateLoader(templateLoader);
	}

	public void setVersion(String version) {
		Version ver = new Version(version);
		log.debug("Set version to {}", ver);
		freemarker.setIncompatibleImprovements(ver);
	}

	@Override
	public void writeTo(Artifact artifact, Context context, Target target) throws IOException {
		Map<String, Object> fContext = context.getVariables();
		String templateName = artifact.getArchetypeName() + ":" + artifact.getPath();
		templateLoader.putTemplate(templateName, readFully(artifact));
		Template template = freemarker.getTemplate(templateName);
		try {
			template.process(fContext, target.getWriter());
		} catch (TemplateException e) {
			throw new ProcessingException("failed to render template", e);
		}
	}

	private String readFully(Artifact artifact) throws IOException {
		StringBuilder sb = new StringBuilder();
		char[] buf = new char[BUF_SIZE];
		try (Reader r = artifact.getContents()) {
			int read;
			while ((read = r.read(buf)) >= 0) {
				sb.append(buf, 0, read);
			}
		}
		return sb.toString();
	}
}
