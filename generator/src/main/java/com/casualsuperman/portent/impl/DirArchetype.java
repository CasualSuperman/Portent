package com.casualsuperman.portent.impl;

import com.casualsuperman.portent.Archetype;
import com.casualsuperman.portent.Artifact;
import lombok.Data;
import java.util.List;

@Data
public class DirArchetype implements Archetype {
	private final String name;
	private final List<Artifact> artifacts;
}
