package com.casualsuperman.portent;

import com.casualsuperman.portent.util.FilenameUtils;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;

import java.io.*;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public class Instance {
	private static final TypeReference<HashMap<String, Object>> TYPE = new TypeReference<HashMap<String, Object>>() {};
	private static final ObjectMapper mapper = new ObjectMapper();

	/** The absolute location of the file containing the instance variables to load. */
	private final File absLocation;
	/** The location of the file relative to the source root. */
	private final Path relLocation;

	/** The effective package name, based on {@link #relLocation}. */
	@Getter
	private final String packageName;
	/** The filename of the instance, with the archetype name removed. */
	@Getter
	private final String instanceName;

	public Instance(File root, Path location) {
		this.absLocation = root.toPath().resolve(location).toFile();
		this.relLocation = location;

		this.packageName = joinPath(location.getParent());
		this.instanceName = FilenameUtils.removeExtension(location.getFileName().toString());
	}

	public File getTargetDirectory(File targetRoot) {
		return targetRoot.toPath().resolve(relLocation.getParent()).toFile();
	}

	public Map<String, Object> loadInstanceVars() throws IOException {
		return getContents(new BufferedInputStream(new FileInputStream(absLocation)));
	}

	protected static Map<String, Object> getContents(InputStream is) throws IOException {
		return mapper.readValue(is, TYPE);
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
