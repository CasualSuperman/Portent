package com.casualsuperman.portent;

import lombok.RequiredArgsConstructor;
import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class ArchetypeLocatorTest {
	@Test
	public void testTemplateDetection() throws IOException {
		Path startingFolder = Paths.get("src", "test", "example");
		Path templateFolder = startingFolder.resolve(Paths.get("src", "main", "templates"));
		ArchetypeLocator locator = new ArchetypeLocator(templateFolder);
		Files.walkFileTree(templateFolder, locator);
		assertThat(locator.getTemplates(StandardCharsets.UTF_8), allOf(
				aMapWithSize(2),
				hasEntry(equalTo("endpoint"), allOf(
						hasName("endpoint"), Matchers.<Archetype>hasProperty("artifacts", containsInAnyOrder(
								hasProperty("path", equalToObject(Paths.get("__filename__.java")))
						))
				)),
				hasEntry(equalTo("search"), allOf(
						hasName("search"), hasProperty("artifacts", containsInAnyOrder(
								hasProperty("path", equalToObject(Paths.get("Demo.java"))),
								hasProperty("path", equalToObject(Paths.get("subdir/SecondDemo.java")))
						))
				))
		));
	}

	public static Matcher<? super Archetype> hasName(String name) {
		return new TemplateNameMatcher(name);
	}

	@RequiredArgsConstructor
	private static class TemplateNameMatcher extends BaseMatcher<Archetype> {
		private final String name;

		@Override
		public boolean matches(Object actual) {
			return actual instanceof Archetype && ((Archetype) actual).getName().equals(name);
		}

		@Override
		public void describeTo(Description description) {
			description.appendText("Has name ").appendValue(name);
		}
	}
}
