package com.casualsuperman.portent;

import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Arrays;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.Matchers.hasEntry;

public class InstanceTest {
	@Test
	public void testLoadContext() throws IOException {
		Instance i = new Instance(new File("src/test/example/src/main/java"),
		                          Paths.get("mydir", "Instance.search"));
		assertThat(i.loadInstanceVars(), allOf(
				aMapWithSize(1),
				hasEntry("key", Arrays.asList(1, 2, 3))
		));
		assertThat(i.getPackageName(), equalTo("mydir"));
		assertThat(i.getInstanceName(), equalTo("Instance"));
		assertThat(i.getTargetDirectory(new File("a")), equalTo(new File("a/mydir")));
	}

	@Test
	public void testNestedPackage() {
		Instance i = new Instance(new File("."), Paths.get("mydir", "subdir", "otherdir", "Instance.search"));
		assertThat(i.getPackageName(), equalTo("mydir.subdir.otherdir"));
		assertThat(i.getTargetDirectory(new File("a")), equalTo(new File("a/mydir/subdir/otherdir")));
	}
}
