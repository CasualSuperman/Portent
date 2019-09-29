package com.casualsuperman.portent.util;

import com.casualsuperman.portent.Context;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@UtilityClass
public class FilenameUtils {
	private static final Pattern VARIABLE_PATTERN = Pattern.compile("__((?:(?!__).)++)__");

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

	public static String templateFilename(String name, Context context) {
		StringBuffer sb = new StringBuffer();
		Matcher m = VARIABLE_PATTERN.matcher(name);

		while (m.find()) {
			String varName = m.group(1);
			String value = getValue(context.getVariables(), varName);
			if (value != null) {
				m.appendReplacement(sb, value);
			}
		}
		m.appendTail(sb);

		return sb.toString();
	}

	private static String getValue(Map<?, ?> context, String varName) {
		Object value = context.get(varName);
		if (value == null) {
			// See if we have a multi-part identifier, and try to recurse into it.
			String[] parts = varName.split("\\.", 2);
			if (parts.length > 1) {
				value = context.get(parts[0]);
				if (value instanceof Map) {
					//noinspection rawtypes
					return getValue((Map) value, parts[1]);
				}
			}
		}
		try {
			return (String) value;
		} catch (ClassCastException ex) {
			log.warn("Property '{}' is not a String, calling toString() instead", varName);
			return value.toString(); //NOSONAR: If the value is null, the cast to String will succeed.
		}
	}
}
