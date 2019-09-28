package com.casualsuperman.portent.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class FilenameUtilsTest {

	@Test
	public void getExtension() {
		assertEquals("", FilenameUtils.getExtension("abba"));
		assertEquals("dat", FilenameUtils.getExtension("abba.dat"));
		assertEquals("dat", FilenameUtils.getExtension("abba.bat.dat"));
	}

	@Test
	public void removeExtension() {
		assertEquals("abba", FilenameUtils.removeExtension("abba"));
		assertEquals("abba", FilenameUtils.removeExtension("abba.dat"));
		assertEquals("abba.bat", FilenameUtils.removeExtension("abba.bat.dat"));
	}
}
