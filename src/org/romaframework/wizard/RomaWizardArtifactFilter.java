package org.romaframework.wizard;

import org.apache.ivy.core.module.descriptor.Artifact;
import org.apache.ivy.util.filter.Filter;
import org.romaframework.wizard.console.ModuleManager;

public class RomaWizardArtifactFilter implements Filter {

	@Override
	public boolean accept(Object o) {
		if (!(o instanceof Artifact)) {
			return false;
		}
		Artifact art = (Artifact) o;
		if (!art.getModuleRevisionId().getOrganisation().startsWith(ModuleManager.ROMA_ORGANIZATION_NAME))
			return false;
		return art.getName().endsWith("-wizard") && "zip".equals(art.getType());
	}

}
