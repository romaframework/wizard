package org.romaframework.wizard;

public enum ProjectTypes {

	WEB(new String[] { "roma-project-web" }), WEBREADY(new String[] { "roma-view-janiculum", "roma-persistence-datanucleus" });

	private String[]	modules;

	private ProjectTypes(String[] modules) {
		this.modules = modules;
	}

	public String[] getModules() {
		return modules;
	}

}
