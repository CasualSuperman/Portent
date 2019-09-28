package com.casualsuperman.portent;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class ContextLoaderTest {
	@Test
	public void testContextLoader() throws IOException {
		ContextLoader loader = new ContextLoader(new File("src/test/example/src/main/java"));
		Map<String, Object> context = loader.getContext(Paths.get("mydir", "Instance.search"));
		assertThat(context, Matchers.allOf(
			aMapWithSize(3),
			hasEntry("filename", "Instance"),
			hasEntry("package", "mydir"),
			Matchers.<String, Object>hasEntry("key", Arrays.asList(1, 2, 3))
		));
	}
}
