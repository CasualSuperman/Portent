package com.casualsuperman.portent;

public enum OverwriteBehavior {
	/** Always overwrite existing files. */
	ALWAYS,
	/** Never overwrite existing files. */
	NEVER,
	/** Overwrite existing files only if they are not in a source root. */
	NON_SOURCE_FILES,
	;
}
