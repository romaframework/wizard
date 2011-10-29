package org.romaframework.wizard;

import java.util.List;
import java.util.Map;

public class ProjectTypes {

	private Map<String, List<ModuleData>>	projectTypes;

	public Map<String, List<ModuleData>> getProjectTypes() {
		return projectTypes;
	}

	public void setProjectTypes(Map<String, List<ModuleData>> modules) {
		this.projectTypes = modules;
	}

	public List<ModuleData> getModulesOfProject(String type) {
		if (projectTypes == null)
			return null;
		return projectTypes.get(type);
	}

}
