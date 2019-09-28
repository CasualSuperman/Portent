package com.casualsuperman.portent;

import com.casualsuperman.portent.util.FilenameUtils;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;

import java.io.*;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
public class ContextLoader {
	private static final TypeReference<HashMap<String, Object>> TYPE = new TypeReference<HashMap<String, Object>>() {};

	private final File root;

	private final ObjectMapper mapper = new ObjectMapper();

	protected Map<String, Object> getContents(InputStream is) throws IOException {
		return mapper.readValue(is, TYPE);
	}

	public Map<String, Object> getContext(Path in) throws IOException {
		Map<String, Object> vars = new HashMap<>();
		vars.putAll(getPathVars(in));
		vars.putAll(getContents(new BufferedInputStream(new FileInputStream(root.toPath().resolve(in).toFile()))));
		return vars;
	}

	protected Map<String, String> getPathVars(Path in) {
		Map<String, String> vars = new HashMap<>();
		vars.put("filename", FilenameUtils.removeExtension(in.getFileName().toString()));
		vars.put("package", joinPath(in.getParent()));
		return vars;
	}

	protected String joinPath(Path in) {
		StringBuilder sb = new StringBuilder();
		for (Path elem : in) {
			if (sb.length() > 0) {
				sb.append('.');
			}
			sb.append(elem.toString());
		}
		return sb.toString();
	}
}
