package org.romaframework.wizard.console;

import org.romaframework.aspect.console.ConsoleAspect;
import org.romaframework.aspect.console.annotation.ConsoleAction;
import org.romaframework.aspect.console.annotation.ConsoleClass;
import org.romaframework.aspect.console.annotation.ConsoleParameter;
import org.romaframework.core.Roma;

@ConsoleClass(name = "help", defaultAction = "help", description = "Help Method")
public class HelpCommand {

	public final static String	ASCII_ART	= "" + "\n                      _______" + "\n                     |_   __ \\"
																						+ "\n                       | |__) |   .--.   _ .--..--.   ,--." + "\n                       |  __ /  / .'`\\ \\[ `.-. .-. | `'_\\ :"
																						+ "\n                      _| |  \\ \\_| \\__. | | | | | | | // | |,"
																						+ "\n                     |____| |___|'.__.' [___||__||__]\\'-;__/" + "\n"
																						+ "\n ________                                                              __"
																						+ "\n|_   __  |                                                            [  |  _"
																						+ "\n  | |_ \\_|_ .--.  ,--.   _ .--..--.  .---.  _   _   __   .--.   _ .--. | | / ]"
																						+ "\n  |  _|  [ `/'`\\]`'_\\ : [ `.-. .-. |/ /__\\\\[ \\ [ \\ [  ]/ .'`\\ \\[ `/'`\\]| '' |"
																						+ "\n _| |_    | |    // | |, | | | | | || \\__., \\ \\/\\ \\/ / | \\__. | | |    | |`\\ \\"
																						+ "\n|_____|  [___]   \\'-;__/[___||__||__]'.__.'  \\__/\\__/   '.__.' [___]  [__|  \\_]\n\n";

	@ConsoleAction(description = "Show help for a command")
	public void help(@ConsoleParameter(name = "commands...") String... args) {
		System.out.println(ASCII_ART);
		if (args.length == 0)
			System.out.println(Roma.component(ConsoleAspect.class).buildHelp());
		else if (args.length == 1)
			System.out.println(Roma.component(ConsoleAspect.class).buildHelpCommandGroup(args[0]));
		else if (args.length == 2)
			System.out.println(Roma.component(ConsoleAspect.class).buildHelpCommand(args[0], args[1]));
	}
}
