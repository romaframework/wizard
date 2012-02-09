package org.romaframework.wizard.console;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.List;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.romaframework.aspect.console.ConsoleAspect;
import org.romaframework.aspect.console.annotation.ConsoleAction;
import org.romaframework.aspect.console.annotation.ConsoleClass;
import org.romaframework.aspect.console.annotation.ConsoleParameter;
import org.romaframework.core.Roma;
import org.romaframework.wizard.ModuleData;
import org.romaframework.wizard.PathHelper;
import org.romaframework.wizard.ProjectTypes;

@ConsoleClass(name = "project", defaultAction = "help", description = "Project management")
public class ProjectManager {

	public static final String	BASE_SOURCE_FOLDER	= "/src/main/java/";
	public static final String	BASE_DOMAIN_FOLDER	= "domain";
	public static final String	BASE_IOC_FOLDER			= BASE_SOURCE_FOLDER + "META-INF/components";
	public static final String	BASE_IOC_FILE				= "applicationContext.xml";

	public static final String	PROJECT_PACKAGE			= "package";
	public static final String	PROJECT_NAME				= "name";
	public static final String	PROJECT_SOURCE			= "src";
	public static final String	PROJECT_IOC_PATH		= "ioc-path";
	public static final String	PROJECT_IOC_FILE		= "ioc-file";
	public static final String	PROJECT_FILE_NAME		= "roma-project.xml";

	private static final Log		log									= LogFactory.getLog(ProjectManager.class);

	private Properties					properties					= new Properties();

	@ConsoleAction(description = "Create a new project")
	public void create(@ConsoleParameter(name = "type", description = "the type of project example: web,webready,console") String type,
			@ConsoleParameter(name = "name", description = "the name of the project") String name,
			@ConsoleParameter(name = "package", description = "the package of new project") String pack,
			@ConsoleParameter(name = "path", description = " the folder where the was created.") String path) {

		properties.put(PROJECT_PACKAGE, pack);
		properties.put(PROJECT_NAME, name);
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
		ProjectTypes projectTypes = Roma.component(ProjectTypes.class);
		List<ModuleData> modules = null;
		if (projectTypes != null) {
			modules = projectTypes.getModulesOfProject(type);
		}
		if (modules == null) {
			log.error("Project Type selected not exist");
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
		m.initProjectDescriptor(projectDir, name, pack);
		for (ModuleData module : modules) {
			m.add(projectDir.getAbsolutePath(), module, properties);
		}

		File projectInstall = new File(PathHelper.getWizardPath() + "project-install/" + type);
		if (projectInstall.exists()) {
			ModuleManager.install(projectInstall, projectDir, properties);
		}
	}

	@ConsoleAction(description = "Create a crud for a class in a project")
	public void crud(@ConsoleParameter(name = "class", description = "domain class that will be generate the crud ") String claz,
			@ConsoleParameter(name = "project-folder", description = "the path of the project") String projectfolder) {

		if (!projectfolder.endsWith("/")) {
			projectfolder = projectfolder.concat("/");
		}

		File dirclass = new File(projectfolder);
		if (!dirclass.exists()) {
			System.out.println("The project folder not exits:" + projectfolder);
			return;
		}

		String realpath = claz.replaceAll("\\.", "/");
		;
		realpath = projectfolder.concat("src/main/java/").concat(realpath).concat(".java");
		File rpath = new File(realpath);

		if (!rpath.exists()) {
			System.out.println("Entity class not exist:" + realpath);
			return;
		}
		Properties properties = new Properties();
		try {
			properties.loadFromXML(new FileInputStream(projectfolder + "/" + PROJECT_FILE_NAME));

		} catch (Exception e) {
			log.error("Error on reading file: " + projectfolder + "/" + PROJECT_FILE_NAME, e);
			return;
		}
		String crudClass = realpath.substring(realpath.lastIndexOf('/') + 1, realpath.length() - 5);
		properties.put("crud.class", crudClass);
		properties.put("crud.package.path", realpath.substring(0, realpath.lastIndexOf('/')));
		properties.put("domain.package", claz.substring(0, claz.lastIndexOf('.')));
		properties.put("crud.path", crudClass.toLowerCase());

		File projectcrud = new File(PathHelper.getWizardPath() + "crud/");

		if (projectcrud.exists()) {
			ModuleManager.install(projectcrud, new File(projectfolder), properties);

		}
	}

	@ConsoleAction(description = "Display the project management help")
	public void help(@ConsoleParameter(name = "commands...") String... args) {
		String name = "project";// Roma.getFeature(this, ConsoleClassFeatures.NAME);
		if (args.length == 0)
			System.out.println(Roma.component(ConsoleAspect.class).buildHelpCommandGroup(name));
		else if (args.length > 1) {
			System.out.println("Wrong project parameters");
			System.out.println(Roma.component(ConsoleAspect.class).buildHelpCommand(name, args[0]));
		}
	}

}
