package com.casualsuperman.portent;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assumptions.assumeTrue;
import static org.mockito.Mockito.*;

public class ArchetypeTemplaterTest {
	@TempDir
	public File testFolder;

	@Test
	public void testArchetypeGeneration() throws IOException {
		Archetype archetype = mock(Archetype.class);
		when(archetype.getName()).thenReturn("demo");
		List<Artifact> artifacts = getMockedArtifacts();
		when(archetype.getArtifacts()).thenReturn(artifacts);
		TemplateEngine engine = new BasicTemplateEngine();
		ContextFactory factory = new ContextFactory(Collections.emptyMap());
		ArchetypeTemplater templater = new ArchetypeTemplater(archetype, engine, factory, StandardCharsets.UTF_8);

		Instance i = getMockInstance();

		File existingFile = new File(testFolder, "com/test/File.java");
		assumeTrue(existingFile.getParentFile().mkdirs());
		assumeTrue(existingFile.createNewFile());

		templater.constructArchetype(testFolder, i, true);

		Path testPath = testFolder.toPath();
		File application = testPath.resolve(Paths.get("com", "test", "Application.java")).toFile();
		File applicationImpl = testPath.resolve(Paths.get("com", "test", "ApplicationImpl.java")).toFile();
		assertTrue(application.exists());
		assertTrue(applicationImpl.exists());
		verify(artifacts.get(2), times(1)).getContents();

		assertEquals(readAll(getResource("Application.java")), readAll(new FileReader(application)));
		assertEquals(readAll(getResource("ApplicationImpl.java")), readAll(new FileReader(applicationImpl)));
		assertEquals(readAll(getResource("File.java")), readAll(new FileReader(existingFile)));
	}

	@Test
	public void testArchetypeGenerationNoOverwrite() throws IOException {
		Archetype archetype = mock(Archetype.class);
		when(archetype.getName()).thenReturn("demo");
		List<Artifact> artifacts = getMockedArtifacts();
		when(archetype.getArtifacts()).thenReturn(artifacts);
		TemplateEngine engine = new BasicTemplateEngine();
		ContextFactory factory = new ContextFactory(Collections.emptyMap());
		ArchetypeTemplater templater = new ArchetypeTemplater(archetype, engine, factory, StandardCharsets.UTF_8);

		Instance i = getMockInstance();

		File existingFile = new File(testFolder, "com/test/File.java");
		assumeTrue(existingFile.getParentFile().mkdirs());
		assumeTrue(existingFile.createNewFile());

		templater.constructArchetype(testFolder, i, false);

		Path testPath = testFolder.toPath();
		File application = testPath.resolve(Paths.get("com", "test", "Application.java")).toFile();
		File applicationImpl = testPath.resolve(Paths.get("com", "test", "ApplicationImpl.java")).toFile();
		assertTrue(application.exists());
		assertTrue(applicationImpl.exists());
		verify(artifacts.get(2), never()).getContents();

		assertEquals(readAll(getResource("Application.java")), readAll(new FileReader(application)));
		assertEquals(readAll(getResource("ApplicationImpl.java")), readAll(new FileReader(applicationImpl)));
		assertEquals("", readAll(new FileReader(existingFile)), "Existing file should not be overwritten");
	}

	private Reader getResource(String resource) {
		return new InputStreamReader(ArchetypeTemplaterTest.class.getResourceAsStream(resource));
	}

	@SneakyThrows
	private Instance getMockInstance() {
		Instance i = spy(new Instance(new File(""), Paths.get("com", "test", "Application.demo")));
		doReturn(Collections.singletonMap("value", "Hello, world!")).when(i).loadInstanceVars();
		return i;
	}

	@SneakyThrows
	private List<Artifact> getMockedArtifacts() {
		Artifact one = mock(Artifact.class);
		when(one.getPath()).thenReturn(Paths.get("__name__Impl.java"));
		when(one.getContents()).thenReturn(new StringReader("package ${package};\n" +
				                                            "\n" +
				                                            "public class ${name}Impl implements ${name} {\n" +
				                                            "\tprivate final String value = \"${value}\";\n" +
				                                            "}"));
		Artifact two = mock(Artifact.class);
		when(two.getPath()).thenReturn(Paths.get("__name__.java"));
		when(two.getContents()).thenReturn(new StringReader("package ${package};\n" +
				                                            "\n" +
				                                            "public interface ${name} {}"));
		Artifact three = mock(Artifact.class);
		when(three.getPath()).thenReturn(Paths.get("File.java"));
		when(three.getContents()).thenReturn(new StringReader("package ${package};"));
		return Arrays.asList(one, two, three);
	}

	private static class BasicTemplateEngine implements TemplateEngine {
		private static final Pattern INTERPOLATION = Pattern.compile("\\$\\{([^}]++)}");

		@Override
		public void writeTo(Artifact artifact, Context context, Target target) throws IOException {
			String data;
			try (Reader contents = artifact.getContents()) {
				data = readAll(contents);
			}
			StringBuffer result = new StringBuffer();
			Matcher m = INTERPOLATION.matcher(data);
			while (m.find()) {
				m.appendReplacement(result, context.getVariables().get(m.group(1)).toString());
			}
			m.appendTail(result);
			try (Writer writer = target.getWriter()) {
				writer.write(result.toString());
			} catch (IOException e) {
				throw new RuntimeException("failed to write template results", e);
			}
		}
	}

	private static String readAll(Reader reader) {
		try (BufferedReader bufferedReader = new BufferedReader(reader)) {
			return bufferedReader.lines().collect(Collectors.joining("\n"));
		} catch (IOException e) {
			throw new RuntimeException("failed to close reader", e);
		}
	}
}
