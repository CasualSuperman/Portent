package com.casualsuperman.portent;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;

@Mojo(name = "portent")
public class PortentMojo extends AbstractMojo {
	public void execute() throws MojoFailureException {
		throw new MojoFailureException("not implemented yet");
	}
}
