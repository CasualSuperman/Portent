package com.casualsuperman.portent;

import org.hamcrest.Matcher;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collections;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class ContextLoaderTest {
	@Test
	@SuppressWarnings({"unchecked", "rawtypes"})
	public void testContextLoader() throws IOException {
		ContextLoader loader = new ContextLoader(new File("src/test/example/src/main/java"),
				Collections.singletonMap("wax", Collections.singletonMap("quail", "multi-jump")));
		Map<String, Object> context = loader.getContext(Paths.get("mydir", "Instance.search"));
		assertThat(context, allOf(
				aMapWithSize(4),
				(Matcher) hasEntry(equalTo("wax"), allOf(aMapWithSize(1), hasEntry("quail", "multi-jump"))),
				hasEntry("filename", "Instance"),
				(Matcher) hasEntry("key", Arrays.asList(1, 2, 3)),
				hasEntry("package", "mydir")
		));
	}
}
