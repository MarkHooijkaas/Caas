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

import java.io.PrintWriter;
import java.io.StringWriter;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;
import org.kisst.cordys.caas.Caas;

public class CaasMainCommand extends CompositeCommand {
	private class GroovyCommand extends CompositeCommand {
		public GroovyCommand() {
			super("caas groovy","run either a interactive groovy shell or a groovy script");
			this.commands.put("run", new GroovyRunScript());
			this.commands.put("shell", new GroovyShell());
		}
	}
	
	
	protected final Options options = new Options();

	public CaasMainCommand() {
		super("caas","run any of the caas subcommands"); 
		commands.put("shell", new GroovyShell());
		commands.put("run", new GroovyRunScript());
		commands.put("pm", new PmCommand());
		commands.put("groovy", new GroovyCommand());
		commands.put("setup", new SetupCommand());
		
		options.addOption("q", "quiet", false, "don't output anything unless errors happen");
		options.addOption("v", "verbose", false, "be verbose about what you are doing");
		options.addOption("d", "debug", false, "if this option is set debug logging will be shown");
		options.addOption("c", "cop", true, "location of a .cop file with connection properties");
		options.addOption("h", "help", false, "show this help information");
		options.addOption(null, "version", false, "show the version information");
	}

	public String getSyntax() { return "[options] "+super.getSyntax();}

	protected static String[] subArgs(String[] args, int pos) {
		String result[]= new String[args.length-pos];
		for (int i=pos; i<args.length; i++)
			result[i-pos]=args[i];
		return result;
	}

	public String getOptionHelp() {
		String marker="XXXMARKERXXX";
		StringWriter buffer=new StringWriter();
		new HelpFormatter().printHelp(new PrintWriter(buffer), 80, "dummy", 
				marker,	options, 8, 0, null);
		String result = buffer.toString();
		result = result.substring(result.indexOf(marker)+marker.length());
		return result;
	}
	
	@Override public String getHelp() {
		return super.getHelp()+"\nOPTIONS	"+getOptionHelp();
	}
	

	@Override public void run(String[] args) {
		CommandLine cmdline;
		try {
			cmdline = new PosixParser().parse( options, args, true);
		} catch (ParseException e) { throw new RuntimeException(e); }
		args=cmdline.getArgs();
		Environment env=Environment.get();
		//env.setSystem(cmdline.getOptionValue("cop"));
		initEnvironment();

		if (cmdline.hasOption("debug"))
			env.debug=true;
		if (cmdline.hasOption("verbose"))
			env.verbose=true;
		if (cmdline.hasOption("quiet"))
			env.quiet=true;
		if (cmdline.hasOption("version")) {
			System.out.println(Caas.getVersion());
			return;
		}

		if (! env.quiet)
			System.out.println("caas: Cordys Administration Automation Scripting, version "+Caas.getVersion());

		if (cmdline.hasOption("help")) {
			help.run(args);
			return;
		}

		super.run(args);
	}

	private void initEnvironment() {
		String homedir=System.getProperty("user.home");
		String filename=homedir+"/config/caas/caas.conf";
		Environment.get().loadProperties(filename);
	}
}
