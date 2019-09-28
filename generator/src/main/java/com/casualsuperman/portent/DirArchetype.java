package com.casualsuperman.portent;

import lombok.Data;
import java.util.List;

@Data
public class DirArchetype implements Archetype {
	private final String name;
	private final List<Artifact> artifacts;
}
