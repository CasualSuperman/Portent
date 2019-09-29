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
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Mojo(name = "render", defaultPhase = LifecyclePhase.GENERATE_SOURCES, requiresProject = true)
public class PortentMojo extends AbstractMojo {
	@Parameter(defaultValue = "${project.compileSourceRoots}", readonly = true, required = true)
	private List<String> compileSourceRoots;

	@Parameter(property = "templateDirectory", defaultValue = "src/main/templates")
	private File templateDir;

	@Parameter(defaultValue = "${project.build.directory}/generated-sources/portent")
	private File generatedSourcesDirectory;

	@Parameter(property = "sourceEncoding", defaultValue = "${project.build.sourceEncoding}")
	private String encoding;

	@Parameter(property = "failIfNoTemplates", defaultValue = "true", readonly = true)
	private boolean failIfNoTemplates;

	@Parameter(property = "failIfNoInstances", defaultValue = "true", readonly = true)
	private boolean failIfNoInstances;

	@Parameter(property = "overwriteExisting", defaultValue = "false", readonly = true)
	private boolean overwriteExisting;

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

		TemplateEngine engine = getTemplateEngine();
		ContextFactory contextFactory = new ContextFactory(getGlobalVars());
		instances.entrySet().parallelStream().forEach(instanceGroup -> {
			Archetype archetype = archetypes.get(instanceGroup.getKey());
			ArchetypeTemplater templater = new ArchetypeTemplater(archetype, engine, contextFactory,
			                                                      charset, overwriteExisting);
			instanceGroup.getValue().parallelStream().forEach(instance ->
					templater.constructArchetype(generatedSourcesDirectory, instance));
		});
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
