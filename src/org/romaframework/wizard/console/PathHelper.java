package org.romaframework.wizard.console;

import java.io.File;

public class PathHelper {

	private static final String	JAVA_HOME	= "JAVA_HOME";

	public static String getWizardPath() {
		String home = System.getenv(JAVA_HOME);
		if (home == null || home.isEmpty()) {
			home = "./";
		} else if (home.charAt(home.length() - 1) != '/') {
			home += "/";
		}
		return home;
	}

	public static File getWizardFilePath() {
		return new File(getWizardPath());
	}
}
