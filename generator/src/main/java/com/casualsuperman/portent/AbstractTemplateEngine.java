package com.casualsuperman.portent;

import lombok.extern.slf4j.Slf4j;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
public abstract class AbstractTemplateEngine implements TemplateEngine {
	private static final Pattern VARIABLE_PATTERN = Pattern.compile("__((?:(?!__).)++)__");

	protected static String templateFilename(String name, Context context) {
		StringBuffer sb = new StringBuffer();
		Matcher m = VARIABLE_PATTERN.matcher(name);

		while (m.find()) {
			String varName = m.group(1);
			String value = getValue(context, varName);
			if (value != null) {
				m.appendReplacement(sb, value);
			}
		}
		m.appendTail(sb);

		return sb.toString();
	}

	private static String getValue(Context context, String varName) {
		Object value = context.getVariables().get(varName);
		try {
			return (String) value;
		} catch (ClassCastException ex) {
			log.warn("Property '{}' is not a String, calling toString() instead", varName);
			return value.toString();
		}
	}
}
