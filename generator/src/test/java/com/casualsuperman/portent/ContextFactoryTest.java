package com.casualsuperman.portent;

import org.hamcrest.Matcher;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Collections;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

public class ContextFactoryTest {
	@Test
	@SuppressWarnings({"unchecked", "rawtypes"})
	public void testContextFactory() throws IOException {
		Instance i = mock(Instance.class);
		when(i.getPackageName()).thenReturn("");
		when(i.getInstanceName()).thenReturn("wacky");
		doReturn(Collections.emptyMap()).when(i).loadInstanceVars();

		ContextFactory factory = new ContextFactory(Collections.singletonMap("wax", Collections.singletonMap("quail", "multi-jump")));
		Context context = factory.getContext(i);
		assertThat(context.getVariables(), allOf(
				aMapWithSize(3),
				(Matcher) hasEntry(equalTo("wax"), allOf(aMapWithSize(1), hasEntry("quail", "multi-jump"))),
				hasEntry("filename", "wacky"),
				hasEntry("package", "")
		));
	}

	@Test
	public void testFailingContextLoad() throws IOException {
		Instance i = mock(Instance.class);
		doThrow(IOException.class).when(i).loadInstanceVars();
		ContextFactory factory = new ContextFactory(Collections.emptyMap());
		assertThrows(ContextLoadException.class, () -> factory.getContext(i));
	}
}
