package org.romaframework.wizard;

public class ModuleData {

	private String	artifact;
	private String	organization;
	private String	version;
	private boolean	install;

	public ModuleData() {
	}

	public ModuleData(String module, String organization, String version, boolean install) {
		this.artifact = module;
		this.organization = organization;
		this.version = version;
		this.install = install;
	}

	public String getArtifact() {
		return artifact;
	}

	public void setArtifact(String name) {
		this.artifact = name;
	}

	public String getOrganization() {
		return organization;
	}

	public void setOrganization(String organization) {
		this.organization = organization;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public boolean isInstall() {
		return install;
	}

	public void setInstall(boolean install) {
		this.install = install;
	}

}
