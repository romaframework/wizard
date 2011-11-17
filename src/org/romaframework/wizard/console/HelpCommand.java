package org.romaframework.wizard.console;

import org.romaframework.aspect.console.ConsoleAspect;
import org.romaframework.aspect.console.annotation.ConsoleClass;
import org.romaframework.core.Roma;

@ConsoleClass(name = "help", defaultAction = "help")
public class HelpCommand {

	public void help() {
		System.out.println(Roma.component(ConsoleAspect.class).buildHelp());
	}

	public void help(String helpClass) {

	}

	public void help(String helpClass, String helpAction) {

	}

}
