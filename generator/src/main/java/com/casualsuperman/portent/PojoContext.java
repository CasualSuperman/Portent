package com.casualsuperman.portent;

import lombok.Data;

import java.util.Map;

@Data
public class PojoContext implements Context {
	private final Map<String, Object> variables;
}
