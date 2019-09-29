package com.casualsuperman.portent;

import com.casualsuperman.portent.InstanceLocator;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.Set;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class InstanceLocatorTest {
	@Test
	public void testInstanceDiscovery() throws IOException {
		Set<String> templateTypes = Collections.singleton("search");
		File root = new File("src/test/example/src/main/java");
		InstanceLocator locator = new InstanceLocator(root, templateTypes);
		locator.discover();
		assertThat(locator.getTemplateInstances(), allOf(
				aMapWithSize(1),
				hasEntry(equalTo("search"), contains(
						hasProperty("relLocation", equalTo(Paths.get("mydir/Instance.search")))))
		));
	}
}
