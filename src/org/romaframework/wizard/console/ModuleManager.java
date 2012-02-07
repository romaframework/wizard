package org.romaframework.wizard.console;

import static org.romaframework.wizard.console.ProjectManager.PROJECT_FILE_NAME;
import static org.romaframework.wizard.console.ProjectManager.PROJECT_PACKAGE;

import java.io.File;
import java.io.FileInputStream;
import java.io.PrintStream;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.ivy.Ivy;
import org.apache.ivy.core.module.descriptor.Artifact;
import org.apache.ivy.core.module.descriptor.DefaultDependencyDescriptor;
import org.apache.ivy.core.module.descriptor.DefaultModuleDescriptor;
import org.apache.ivy.core.module.descriptor.ModuleDescriptor;
import org.apache.ivy.core.module.id.ModuleId;
import org.apache.ivy.core.module.id.ModuleRevisionId;
import org.apache.ivy.core.report.ResolveReport;
import org.apache.ivy.core.resolve.IvyNode;
import org.apache.ivy.core.resolve.ResolveOptions;
import org.apache.ivy.core.retrieve.RetrieveOptions;
import org.apache.ivy.plugins.parser.xml.XmlModuleDescriptorWriter;
import org.apache.ivy.plugins.version.VersionMatcher;
import org.apache.tools.ant.DefaultLogger;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.ProjectHelper;
import org.apache.tools.ant.taskdefs.Copy;
import org.apache.tools.ant.types.FileSet;
import org.apache.tools.ant.types.FilterSet;
import org.romaframework.aspect.console.annotation.ConsoleAction;
import org.romaframework.aspect.console.annotation.ConsoleClass;
import org.romaframework.aspect.console.annotation.ConsoleParameter;
import org.romaframework.core.util.FileUtils;
import org.romaframework.wizard.ModuleData;
import org.romaframework.wizard.PathHelper;
import org.romaframework.wizard.RomaWizardArtifactFilter;

@ConsoleClass(name = "module", description = "Module management")
public class ModuleManager {

	public static final String			ROMA_ORGANIZATION_NAME	= "org.romaframework";
	public static final String[]		TXT_FILES								= { "**/*.css", "**/*.jsp", "**/*.java", "**/*.xml", "**/*.js", "**/*.classpath", "**/*.project", "**/*.sh", "**/*.bat" };
	private Ivy											ivy;
	private static final Log				log											= LogFactory.getLog(ModuleManager.class);
	private File										projectFile;
	private Properties							projectInfo;
	private DefaultModuleDescriptor	projectDescriptor;
	private ResolveReport						report;

	protected Ivy getIvy() {
		if (ivy == null) {
			ivy = Ivy.newInstance();
			try {
				ivy.configure(new File(PathHelper.getWizardPath() + "project-install/ivysettings.xml"));
			} catch (Exception e) {
				log.error("Error on ivy loading");
				throw new RuntimeException("Error on ivy loading", e);
			}
		}
		return ivy;
	}

	public ResolveReport getReport() {
		if (report == null) {
			getProjectDescriptor();
		}
		return report;
	}

	@ConsoleAction(description = "add a module to a project")
	public void add(@ConsoleParameter(name = "module", description = "the name of module to add") String module,
			@ConsoleParameter(name = "project", description = "path of project where add the module") String project) {
		log.error("Error on reading installing module: " + module);
		Properties properties = new Properties();
		try {
			properties.loadFromXML(new FileInputStream(project + "/" + PROJECT_FILE_NAME));
			if (add(project, module, properties, "latest.integration")) {
				log.error("Error on reading installing module: " + module);
			}
		} catch (Exception e) {
			log.error("Error on reading file: " + project + "/" + PROJECT_FILE_NAME, e);
		}
	}

	@ConsoleAction(description = "add a module of a version to a project")
	public void add(@ConsoleParameter(name = "module", description = "the name of module to add") String module,
			@ConsoleParameter(name = "version", description = "the version of module to add") String version,
			@ConsoleParameter(name = "project", description = "path of project where add the module") String project) {
		log.error("Error on reading installing module: " + module);
		Properties properties = new Properties();
		try {
			properties.loadFromXML(new FileInputStream(project + "/" + PROJECT_FILE_NAME));
			if (add(project, module, properties, version)) {
				log.error("Error on reading installing module: " + module);
			}
		} catch (Exception e) {
			log.error("Error on reading file: " + project + "/" + PROJECT_FILE_NAME, e);
		}
	}

	protected void initProjectDescriptor(File projectFile, String name, String organization) {
		try {
			DefaultModuleDescriptor projectDescriptor = new DefaultModuleDescriptor(new ModuleRevisionId(new ModuleId(organization, name), "1.0"), "", null);
			projectDescriptor.toIvyFile(new File(projectFile.getAbsolutePath() + "/ivy.xml"));
		} catch (Exception e) {
			log.error("Error on reading ivy file: ", e);
		}
	}

	protected DefaultModuleDescriptor getProjectDescriptor() {
		if (projectDescriptor == null) {
			try {
				report = getIvy().resolve(new File(projectFile.getAbsolutePath() + "/ivy.xml"));
				projectDescriptor = (DefaultModuleDescriptor) report.getModuleDescriptor();
			} catch (Exception e) {
				log.error("Error on reading ivy file: ", e);
			}

		}
		return projectDescriptor;
	}

	protected void addDependency(ModuleRevisionId moduleId) {
		try {
			DefaultDependencyDescriptor ddd = new DefaultDependencyDescriptor(moduleId, false);
			ddd.addDependencyConfiguration("*", "default");
			getProjectDescriptor().addDependency(ddd);
		} catch (Exception e) {
			log.error("Error on loading project dependencies ", e);
		}
	}

	/**
	 * Install a module to a project.
	 * 
	 * @param project
	 *          where install.
	 * @param module
	 *          to install.
	 * @param projectInfo
	 *          generic project information.
	 * @return true if module is correctly installed.
	 */
	protected boolean add(String project, String module, Properties projectInfo, String version) {
		return add(project, new ModuleData(module, ROMA_ORGANIZATION_NAME, version, true), projectInfo);
	}

	/**
	 * Install a module to a project.
	 * 
	 * @param project
	 *          where install.
	 * @param module
	 *          to install.
	 * @param organization
	 *          the organization of module to install.
	 * @param version
	 *          the version of module to add.
	 * @param projectInfo
	 *          generic project information.
	 * @return true if module is correctly installed.
	 */
	protected boolean add(String project, ModuleData module, Properties projectInfo) {
		this.projectInfo = projectInfo;
		projectFile = new File(project);
		try {

			ModuleRevisionId mri = new ModuleRevisionId(new ModuleId(module.getOrganization(), module.getArtifact()), module.getVersion());
			if (module.isInstall()) {
				ResolveOptions ro = new ResolveOptions();
				ro.setConfs(new String[] { "default", "wizard" });
				ResolveReport repo = getIvy().resolve(mri, ro, true);
				if (repo.hasError()) {
					log.error("Error on module resolve dependencies");
				}
				mri = repo.getModuleDescriptor().getModuleRevisionId();
				RetrieveOptions options = new RetrieveOptions();
				options.setConfs(new String[] { "wizard" });
				options.setArtifactFilter(new RomaWizardArtifactFilter());
				getIvy().retrieve(mri, PathHelper.getWizardPath() + "temp/libs/[artifact].[ext]", options);
				List<?> dependencies = repo.getDependencies();
				Collections.reverse(dependencies);
				for (Object o : dependencies) {
					IvyNode node = (IvyNode) o;
					if (node.isLoaded()) {
						installArtifacts(node.getAllArtifacts(), node.getDescriptor(), node.getData().getSettings().getVersionMatcher());
					}
				}

				installArtifacts(repo.getModuleDescriptor().getAllArtifacts(), repo.getModuleDescriptor(), getIvy().getSettings().getVersionMatcher());

				try {
					org.apache.commons.io.FileUtils.deleteDirectory(new File(PathHelper.getWizardPath() + "temp"));
				} catch (Exception e) {
					log.warn("Internal error on tmp directory clear", e);
				}
				addDependency(repo.getModuleDescriptor().getDependencies()[0].getDependencyRevisionId());
			} else {
				addDependency(mri);
			}

			XmlModuleDescriptorWriter.write(getProjectDescriptor(), new File(projectFile.getAbsolutePath() + "/ivy.xml"));
			projectDescriptor = null;
			report = null;
			return true;
		} catch (Exception e) {
			log.error("error on module resolve", e);
			return false;
		}
	}

	/**
	 * Check if install and Install all wizard artifact of a module.
	 * 
	 * @param artifacts
	 *          where find to install.
	 * @param descriptor
	 *          the module to install
	 * @param versionMatcher
	 *          the version matcher for match if install module.
	 */
	protected void installArtifacts(Artifact[] artifacts, ModuleDescriptor descriptor, VersionMatcher versionMatcher) {
		for (Artifact art : artifacts) {
			File artifactFile = new File(PathHelper.getWizardPath() + "temp/libs/" + art.getName() + "." + art.getExt());
			if (artifactFile.exists()) {

				boolean conains = false;
				for (Object o : getReport().getDependencies()) {
					IvyNode node = (IvyNode) o;
					if (node.getModuleRevision() != null && descriptor.equals(node.getModuleRevision().getDescriptor())) {
						conains = true;
						break;
					}

				}
				if (!conains) {
					installArtifact(artifactFile);
				} else {
					extractLocal(artifactFile);
				}
			}
		}
	}

	private File extractLocal(File moduleFile) {
		String name = moduleFile.getName().substring(0, moduleFile.getName().lastIndexOf('.'));
		File ext = new File(PathHelper.getWizardPath() + "temp/export/" + name);
		FileUtils.unzipArchive(moduleFile, ext);
		return ext;

	}

	/**
	 * Extract an artifact on project structure.
	 * 
	 * @param moduleFile
	 *          the file to install.
	 */
	protected void installArtifact(File moduleFile) {
		File ext = extractLocal(moduleFile);
		install(ext, projectFile, projectInfo);
	}

	public static void install(File ext, File projectFile, Properties projectInfo) {

		File scaffolding = new File(ext.getAbsolutePath() + "/scaffolding");
		if (scaffolding.exists()) {
			try {
				Project project = new Project();
				// Copy txt file with filter .
				Copy copy = new Copy();
				copy.setProject(project);
				copy.setOverwrite(true);
				FilterSet fs = copy.createFilterSet();
				fs.setBeginToken("#{");
				fs.setEndToken("}");
				for (Object key : projectInfo.keySet()) {
					fs.addFilter(new FilterSet.Filter("project." + (String) key, (String) projectInfo.get(key)));
				}
				fs.addFilter(new FilterSet.Filter("project.path", projectFile.getAbsolutePath()));
				fs.addFilter(new FilterSet.Filter("project.package-path", ((String) projectInfo.get(PROJECT_PACKAGE)).replace('.', '/')));

				FileSet set = new FileSet();
				for (String string : TXT_FILES) {
					set.createInclude().setName(string);
				}
				set.setDir(scaffolding);
				copy.addFileset(set);
				copy.setTodir(projectFile);
				copy.execute();

				// Copy binary file.
				copy = new Copy();
				copy.setOverwrite(true);
				copy.setProject(project);
				set = new FileSet();
				for (String string : TXT_FILES) {
					set.createExclude().setName(string);
				}
				set.setDir(scaffolding);
				copy.addFileset(set);
				copy.setTodir(projectFile);
				copy.execute();

			} catch (Exception ioe) {
				log.error("Unable to copy file \"" + scaffolding.getName() + "\" to \"" + projectFile.getName() + " cause: " + ioe, ioe);
			}
		}
		File file = new File(ext.getAbsolutePath() + "/wizard/wizard.xml");
		if (file.exists()) {
			executeAntScript(file, projectFile, projectInfo, "add-module");
		}

	}

	/**
	 * Execute an ant script.
	 * 
	 * @param buildFile
	 *          the ant file to execute.
	 * @param projectInfo
	 *          the parameter for ant file
	 * @param targets
	 *          the targets to execute.
	 */
	protected static void executeAntScript(File buildFile, File projectFile, Properties projectInfo, String installerPath, String... targets) {
		Project project = new Project();

		for (Object key : projectInfo.keySet()) {
			project.setUserProperty("project." + (String) key, (String) projectInfo.get(key));
		}

		project.setUserProperty("wizard.path", PathHelper.getWizardPath() + "project-install/");
		project.setUserProperty("project.path", projectFile.getAbsolutePath());
		project.setUserProperty("project.package-path", ((String) projectInfo.get(PROJECT_PACKAGE)).replace('.', '/'));
		project.setUserProperty("ant.file", buildFile.getAbsolutePath());
		project.init();

		DefaultLogger consoleLogger = new DefaultLogger() {
			@Override
			protected void printMessage(String message, PrintStream stream, int priority) {
				if (Project.MSG_INFO == priority)
					ModuleManager.log.info(message);
				else if (Project.MSG_ERR == priority)
					ModuleManager.log.error(message);
				else if (Project.MSG_DEBUG == priority)
					ModuleManager.log.debug(message);
				else if (Project.MSG_WARN == priority)
					ModuleManager.log.warn(message);
				else if (Project.MSG_VERBOSE == priority)
					ModuleManager.log.info(message);
			}

		};
		consoleLogger.setMessageOutputLevel(Project.MSG_INFO);
		project.addBuildListener(consoleLogger);

		ProjectHelper helper = ProjectHelper.getProjectHelper();
		project.addReference("ant.projectHelper", helper);
		helper.parse(project, buildFile);
		if (targets == null || targets.length == 0) {
			project.executeTarget(project.getDefaultTarget());
		} else {
			for (String target : targets) {
				project.executeTarget(target);
			}

		}
	}

}
