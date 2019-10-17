package com.casualsuperman.portent;

import lombok.extern.slf4j.Slf4j;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;

import java.io.File;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Mojo(name = "render", defaultPhase = LifecyclePhase.GENERATE_SOURCES, threadSafe = true)
public class PortentMojo extends AbstractMojo {
	@Parameter(defaultValue = "${project.compileSourceRoots}", required = true)
	private List<String> compileSourceRoots;

	@Parameter(property = "templateDirectory", defaultValue = "src/main/templates")
	private File templateDir;

	@Parameter(defaultValue = "${project.build.directory}/generated-sources/portent")
	private File generatedSourcesDirectory;

	@Parameter(property = "sourceEncoding", defaultValue = "${project.build.sourceEncoding}")
	private String encoding;

	/**
	 * Should Portent fail execution if no templates are found. Defaults to <code>true</code>.
	 */
	@Parameter(property = "failIfNoTemplates", defaultValue = "true")
	private boolean failIfNoTemplates;

	/**
	 * Should Portent fail execution if no instances of a template are found. Defaults to <code>true</code>.
	 */
	@Parameter(property = "failIfNoInstances", defaultValue = "true")
	private boolean failIfNoInstances;

	/**
	 * Should Portent overwrite existing files. Possible values are <code>ALWAYS</code>, <code>NEVER</code>,
	 * and <code>NON_SOURCE_FILES</code>. Defaults to <code>NON_SOURCE_FILES</code>.
	 */
	@Parameter(property = "overwriteExisting", defaultValue = "NON_SOURCE_FILES")
	private OverwriteBehavior overwriteExisting;

	@Parameter(readonly = true, required = true, defaultValue = "${project}")
	private MavenProject project;

	public void execute() throws MojoFailureException {
		Charset charset = Charset.forName(encoding);

		Map<String, Archetype> archetypes = getArchetypes();
		if (archetypes.isEmpty()) {
			log.warn("No template definitions found in {}, nothing to do.", templateDir);
			if (failIfNoTemplates) {
				throw new MojoFailureException("No template definitions found");
			}
			return;
		}

		Map<String, List<Instance>> instances = getInstances(archetypes);
		if (instances.isEmpty()) {
			log.warn("No template instances found, nothing to do");
			if (failIfNoInstances) {
				throw new MojoFailureException("no instances to render found");
			}
			return;
		}

		boolean overwrite;
		switch (overwriteExisting) {
			case NEVER:
				overwrite = false;
				break;
			case ALWAYS:
				overwrite = true;
				break;
			case NON_SOURCE_FILES:
				overwrite = !isSourceRoot(generatedSourcesDirectory);
				break;
			default:
				throw new IllegalArgumentException("unable to determine overwrite behavior");
		}

		TemplateEngine engine = getTemplateEngine();
		ContextFactory contextFactory = new ContextFactory(getGlobalVars());
		instances.entrySet().parallelStream().forEach(instanceGroup -> {
			Archetype archetype = archetypes.get(instanceGroup.getKey());
			ArchetypeTemplater templater = new ArchetypeTemplater(archetype, engine, contextFactory,
			                                                      charset);
			instanceGroup.getValue().parallelStream().forEach(instance ->
					templater.constructArchetype(generatedSourcesDirectory, instance, overwrite));
		});
		String relDest = project.getBasedir().toPath().relativize(generatedSourcesDirectory.toPath()).toString();
		project.addCompileSourceRoot(relDest);
	}

	private boolean isSourceRoot(File dir) {
		Path basePath = project.getBasedir().toPath();
		for (String sourceRoot : compileSourceRoots) {
			if (basePath.resolve(sourceRoot).toFile().equals(dir)) {
				return true;
			}
		}
		return false;
	}

	private Map<String, Object> getGlobalVars() {
		return Collections.emptyMap();
	}

	private TemplateEngine getTemplateEngine() {
		return new VelocityTemplateEngine();
	}

	private Map<String, Archetype> getArchetypes() {
		ArchetypeLocator locator = new ArchetypeLocator(templateDir.toPath());
		locator.discover();
		return locator.getTemplates(Charset.forName(encoding));
	}

	private Map<String, List<Instance>> getInstances(Map<String, Archetype> archetypes) {
		return compileSourceRoots.parallelStream()
				.map(f -> project.getBasedir().toPath().resolve(f).toFile())
				.map(root -> new InstanceLocator(root, archetypes.keySet()))
				.map(instanceLocator -> {
					instanceLocator.discover();
					return instanceLocator.getTemplateInstances();
				})
				.map(Map::entrySet)
				.flatMap(Collection::stream)
				.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (a, b) -> {
					a.addAll(b);
					return a;
				}));
	}
}
