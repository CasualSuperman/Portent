package com.casualsuperman.portent.impl;

import com.casualsuperman.portent.Context;
import lombok.Data;

import java.util.Map;

@Data
public class MapBackedContext implements Context {
	private final Map<String, Object> variables;
}
