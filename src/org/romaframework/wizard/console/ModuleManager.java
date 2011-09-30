package org.romaframework.wizard.console;

import static org.romaframework.wizard.console.ProjectManager.PROJECT_FILE_NAME;
import static org.romaframework.wizard.console.ProjectManager.PROJECT_PACKAGE;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Collections;
import java.util.Date;
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
import org.apache.ivy.plugins.version.VersionMatcher;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.ProjectHelper;
import org.romaframework.aspect.console.annotation.ConsoleClass;
import org.romaframework.core.util.FileUtils;
import org.romaframework.wizard.RomaWizardArtifactFilter;

@ConsoleClass(name = "module")
public class ModuleManager {

	public static final String			ROMA_ORGANIZATION_NAME	= "org.romaframework";
	private Ivy											ivy;
	private static final Log				log											= LogFactory.getLog(ModuleManager.class);
	private File										projectFile;
	private Properties							projectInfo;
	private DefaultModuleDescriptor	projectDescriptor;

	protected Ivy getIvy() {
		if (ivy == null) {
			ivy = Ivy.newInstance();
			try {
				ivy.configureDefault();
			} catch (Exception e) {
				log.error("Error on ivy loading");
				throw new RuntimeException("Error on ivy loading", e);
			}
		}
		return ivy;
	}

	public void add(String project, String module) {

		Properties properties = new Properties();
		try {
			properties.loadFromXML(new FileInputStream(project + "/" + PROJECT_FILE_NAME));
			if (add(project, module, properties)) {
				log.error("Error on reading installing module: " + module);
			}
		} catch (Exception e) {
			log.error("Error on reading file: " + project + "/" + PROJECT_FILE_NAME, e);
		}
	}

	protected void initProjectDescriptor(String name,String organization){
		projectDescriptor = new DefaultModuleDescriptor(new ModuleRevisionId(new ModuleId(organization, name), "1.0"), "", null);
	}
	
	protected DefaultModuleDescriptor getProjectDescriptor() {
		if (projectDescriptor == null) {
			try {
				if (!new File(projectFile.getAbsolutePath() + "/ivy.xml").exists()) {
					projectDescriptor = new DefaultModuleDescriptor(new ModuleRevisionId(new ModuleId("prova", "prova"), "1.0"), "status", new Date());
				} else {
					ResolveReport report = getIvy().resolve(new File(projectFile.getAbsolutePath() + "/ivy.xml"));
					projectDescriptor = (DefaultModuleDescriptor) report.getModuleDescriptor();
				}
			} catch (Exception e) {
				log.error("Error on reading ivy file: ", e);
			}

		}
		return projectDescriptor;
	}

	protected void addDependency(ModuleRevisionId mri) {
		try {
			getProjectDescriptor().addDependency(new DefaultDependencyDescriptor(mri, false));
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
	protected boolean add(String project, String module, Properties projectInfo) {
		this.projectInfo = projectInfo;
		projectFile = new File(project);
		try {

			ModuleRevisionId mri = new ModuleRevisionId(new ModuleId(ROMA_ORGANIZATION_NAME, module), "latest.integration");
			ResolveOptions ro = new ResolveOptions();
			ResolveReport repo = getIvy().resolve(mri, ro, true);
			if (repo.hasError()) {
				log.error("Error on module resolve dependencies");
			}
			addDependency(mri);
			mri = repo.getModuleDescriptor().getModuleRevisionId();
			RetrieveOptions options = new RetrieveOptions();
			options.setArtifactFilter(new RomaWizardArtifactFilter());
			getIvy().retrieve(mri, "libs/[artifact].[ext]", options);
			List<?> dependencies = repo.getDependencies();
			Collections.reverse(dependencies);
			for (Object o : dependencies) {
				IvyNode node = (IvyNode) o;
				if (node.isLoaded()) {
					installArtifacts(node.getAllArtifacts(), node.getDescriptor(), node.getData().getSettings().getVersionMatcher());
				}
			}

			installArtifacts(repo.getModuleDescriptor().getAllArtifacts(), repo.getModuleDescriptor(), getIvy().getSettings().getVersionMatcher());

			org.apache.commons.io.FileUtils.deleteDirectory(new File("libs"));
			org.apache.commons.io.FileUtils.deleteDirectory(new File("export"));
			getProjectDescriptor().toIvyFile(new File(projectFile.getAbsolutePath() + "/ivy.xml"));
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
			File artifactFile = new File("libs/" + art.getName() + "." + art.getExt());
			if (artifactFile.exists()) {
				if (!getProjectDescriptor().dependsOn(versionMatcher, descriptor)) {
					installArtifact(artifactFile);
				}
			}
		}
	}

	/**
	 * Extract an artifact on project structure.
	 * 
	 * @param moduleFile
	 *          the file to install.
	 */
	protected void installArtifact(File moduleFile) {
		String name = moduleFile.getName().substring(0, moduleFile.getName().lastIndexOf('.'));
		File ext = new File("export/" + name);
		FileUtils.unzipArchive(moduleFile, ext);
		File scaffolding = new File(ext.getAbsolutePath() + "/scaffolding");
		if (scaffolding.exists()) {
			try {
				org.apache.commons.io.FileUtils.copyDirectory(scaffolding, projectFile);
			} catch (IOException ioe) {
				log.error("Unable to copy file \"" + scaffolding.getName() + "\" to \"" + projectFile.getName() + " cause: " + ioe, ioe);
			}
		}
		File file = new File(ext.getAbsolutePath() + "/wizard/wizard.xml");
		if (file.exists()) {
			executeAntScript(file, projectInfo, "add-module");
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
	protected void executeAntScript(File buildFile, Properties projectInfo, String... targets) {
		Project project = new Project();

		for (Object key : projectInfo.keySet()) {
			project.setUserProperty("project." + (String) key, (String) projectInfo.get(key));
		}

		project.setUserProperty("project.path", projectFile.getAbsolutePath());
		project.setUserProperty("project.package-path", ((String) projectInfo.get(PROJECT_PACKAGE)).replace('.', '/'));
		project.setUserProperty("ant.file", buildFile.getAbsolutePath());
		project.init();
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
