package com.casualsuperman.portent;

import java.util.List;

public interface Template {
	String getName();

	List<Templatable> getTemplates();
}
