package com.casualsuperman.portent.util;

import lombok.experimental.UtilityClass;

@UtilityClass
public class FilenameUtils {
	public static String getExtension(String name) {
		int dotIndex = name.lastIndexOf('.');
		if (dotIndex == -1) {
			return "";
		}
		return name.substring(dotIndex + 1);
	}

	public static String removeExtension(String name) {
		int dotIndex = name.lastIndexOf('.');
		if (dotIndex == -1) {
			return name;
		}
		return name.substring(0, dotIndex);
	}
}
