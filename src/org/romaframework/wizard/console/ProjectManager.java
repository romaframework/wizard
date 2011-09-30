package org.romaframework.wizard.console;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.romaframework.aspect.console.annotation.ConsoleClass;
import org.romaframework.wizard.ProjectTypes;

@ConsoleClass(name = "project")
public class ProjectManager {

	public static final String	BASE_SOURCE_FOLDER	= "/src/main/java/";
	public static final String	BASE_DOMAIN_FOLDER	= "domain";
	public static final String	BASE_IOC_FOLDER			= BASE_SOURCE_FOLDER + "META-INF/components";
	public static final String	BASE_IOC_FILE				= "applicationContext.xml";

	public static final String	PROJECT_PACKAGE			= "package";
	public static final String	PROJECT_SOURCE			= "src";
	public static final String	PROJECT_IOC_PATH		= "ioc-path";
	public static final String	PROJECT_IOC_FILE		= "ioc-file";
	public static final String	PROJECT_FILE_NAME		= "roma-project.xml";

	private static final Log		log									= LogFactory.getLog(ProjectManager.class);

	private Properties					properties					= new Properties();

	public void create(ProjectTypes type, String name, String pack, String path) {

		properties.put(PROJECT_PACKAGE, pack);
		properties.put(PROJECT_SOURCE, BASE_SOURCE_FOLDER);
		properties.put(PROJECT_IOC_PATH, BASE_IOC_FOLDER);
		properties.put(PROJECT_IOC_FILE, BASE_IOC_FILE);

		if (path == null || path == "")
			path = ".";
		File workingDir = new File(path);
		if (!workingDir.exists()) {
			log.error("Working path:" + path + " not exist");
			return;
		}

		File projectDir = new File(workingDir.getAbsolutePath() + "/" + name);
		if (projectDir.exists()) {
			log.error("Project directory:" + name + " already exist");
			return;
		}

		projectDir.mkdir();

		try {
			properties.storeToXML(new FileOutputStream(projectDir.getAbsolutePath() + "/" + PROJECT_FILE_NAME), "");
		} catch (Exception e) {
			log.error("Error on write file:" + projectDir.getAbsolutePath() + "/" + PROJECT_FILE_NAME, e);
			return;
		}

		new File(projectDir.getAbsolutePath() + BASE_SOURCE_FOLDER + pack.replace('.', '/') + "/" + BASE_DOMAIN_FOLDER).mkdirs();
		ModuleManager m = new ModuleManager();
		m.initProjectDescriptor(name, pack);
		for (String module : type.getModules()) {
			m.add(projectDir.getAbsolutePath(), module, properties);
		}

	}

}
