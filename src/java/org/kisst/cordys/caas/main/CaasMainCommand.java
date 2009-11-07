/**
Copyright 2008, 2009 Mark Hooijkaas

This file is part of the Caas tool.

The Caas tool is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

The Caas tool is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with the Caas tool.  If not, see <http://www.gnu.org/licenses/>.
*/

package org.kisst.cordys.caas.main;

import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.kisst.cordys.caas.Caas;

public class CaasMainCommand extends CompositeCommand {
	protected final Options options = new Options();
	private final PmCommand pm=new PmCommand();
	private final GroovyCaasShell shell = new GroovyCaasShell();
	private final SetupCommand setup = new SetupCommand();

	public CaasMainCommand() {
		super("caas [options] [cmd] ...", "shell"); 
		commands.put("pm", pm);
		commands.put("shell", shell);
		commands.put("run", shell);
		commands.put("setup", setup);
		
		options.addOption("q", "quiet", false, "don't output anything unless errors happen");
		options.addOption("v", "verbose", false, "be verbose about what you are doing");
		options.addOption("d", "debug", false, "if this option is set debug logging will be shown");
		options.addOption("c", "cop", true, "location of a .cop file with connection properties");
		options.addOption("h", "help", false, "show this help information");
		options.addOption(null, "version", false, "show the version information");
	}


	protected static String[] subArgs(String[] args, int pos) {
		String result[]= new String[args.length-pos];
		for (int i=pos; i<args.length; i++)
			result[i-pos]=args[i];
		return result;
	}

	@Override public void printHelp(String[] args) {
		new HelpFormatter().printHelp(80, usage, 
				"[cmd] is one of "+getCommandNames()+"\nOptions:", 
				options, null);
	}
	
	@Override public void run(String[] args) {
		args=Environment.get().parse(options, args);
		Environment env=Environment.get();
		env.setSystem(env.getOptionValue("cop"));

		if (env.hasOption("debug"))
			env.debug=true;
		if (env.hasOption("verbose"))
			env.verbose=true;
		if (env.hasOption("quiet"))
			env.quiet=true;
		if (env.hasOption("version")) {
			System.out.println(Caas.getVersion());
			return;
		}

		if (! env.quiet)
			System.out.println("caas: Cordys Administration Automation Scripting, version "+Caas.getVersion());

		if (env.hasOption("help")) {
			printHelp(args);
			return;
		}

		super.run(args);
	}
}
