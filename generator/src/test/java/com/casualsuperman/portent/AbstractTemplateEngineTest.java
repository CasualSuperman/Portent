package com.casualsuperman.portent;

import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static com.casualsuperman.portent.AbstractTemplateEngine.templateFilename;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class AbstractTemplateEngineTest {
	@Test
	public void testMissingTemplateFilename() {
		String filename = "I own a __company__ branded microSD card!";
		assertEquals(filename, templateFilename(filename, ctx()));
	}

	@Test
	public void testTemplateFilename() {
		String filename = "I own a __company__ branded microSD card!";
		Context someContext = ctx("company", "SanDisk");
		assertEquals("I own a SanDisk branded microSD card!", templateFilename(filename, someContext));
	}

	@Test
	public void testMultipleTemplates() {
		String filename = "I own a __company__ branded __company__ company!";
		Context someContext = ctx("company", "SanDisk");
		assertEquals("I own a SanDisk branded SanDisk company!", templateFilename(filename, someContext));
	}

	@Test
	public void testMultipleVariables() {
		String filename = "I own a __company__ branded __food__ company!";
		Context someContext = ctx("company", "SubWay", "food", "sandwich");
		assertEquals("I own a SubWay branded sandwich company!", templateFilename(filename, someContext));
	}

	@Test
	public void testUnderscoreInVariableName() {
		String filename = "I own a __comp_any__ branded __food__ company!";
		Context someContext = ctx("comp_any", "SubWay", "food", "sandwich");
		assertEquals("I own a SubWay branded sandwich company!", templateFilename(filename, someContext));
	}

	@Test
	public void testBrokenVariableName() {
		String filename = "I own a __comp__any__ branded __food__ company!";
		Context someContext = ctx("comp_any", "SubWay", "food", "sandwich");
		assertEquals("I own a __comp__any__ branded __food__ company!", templateFilename(filename, someContext));
	}

	@Test
	public void testNonStringVariable() {
		String filename = "I own a __company__!";
		Object o = mock(Object.class);
		when(o.toString()).thenReturn("Nintendo Switch");
		Context someContext = ctx(Collections.singletonMap("company", o));
		assertEquals("I own a Nintendo Switch!", templateFilename(filename, someContext));
	}

	private static Context ctx(String... pairs) {
		if (pairs.length % 2 != 0) {
			throw new IllegalStateException("must have an even number of arguments");
		}
		Map<String, Object> context = new HashMap<>();
		for (int i = 0, l = pairs.length; i < l; i += 2) {
			context.put(pairs[i], pairs[i + 1]);
		}
		return ctx(context);
	}

	private static Context ctx(Map<String, Object> map) {
		return new MapBackedContext(map);
	}
}
