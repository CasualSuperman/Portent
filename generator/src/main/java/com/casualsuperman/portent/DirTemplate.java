package com.casualsuperman.portent;

import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.util.List;

@ToString
@RequiredArgsConstructor
public class DirTemplate implements Template {
	private final String name;
	private final List<Templatable> files;

	@Override
	public String getName() {
		return name;
	}

	@Override
	public List<Templatable> getTemplates() {
		return files;
	}
}
