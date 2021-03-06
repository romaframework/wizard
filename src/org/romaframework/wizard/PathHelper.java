package org.romaframework.wizard;

import java.io.File;
import java.io.IOException;

public class PathHelper {

	private static final String	ROMA_HOME	= "ROMA_HOME";

	public static String getWizardPath() {
		String home = System.getenv(ROMA_HOME);
		if (home == null || home.isEmpty()) {
			try {
				home = new File(".").getCanonicalPath();
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}else {
			try {
				home = new File(home).getCanonicalPath();
			} catch (IOException e) {
				throw new RuntimeException(e);
			}			
		}
		if (home.charAt(home.length() - 1) != '/') {
			home += "/";
		}
		return home;
	}

	public static File getWizardFilePath() {
		return new File(getWizardPath());
	}
}
