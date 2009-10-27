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

import java.util.LinkedHashMap;

import org.apache.commons.cli.HelpFormatter;


abstract public class CompositeCommand extends CommandBase {
	protected final LinkedHashMap<String, Command> commands=new LinkedHashMap<String,Command>();
	private final String usage;
	protected String defaultCommand="help";

	private Command help= new Command() {
		public void run(Environment env, String[] args) {
			String commandNames="";
			for (String c: commands.keySet())
				commandNames+=", "+c;
			new HelpFormatter().printHelp(80,  
					"Usage: "+usage
					+"\twhere <cmd> is one of "+commandNames.substring(2),
					"Options:",
					options, null );
		}
	};

	public CompositeCommand(String usage) {
		this.usage=usage;
		commands.put("help", help);
		options.addOption("h", "help", false, "print this help message");
	}
	
	@Override public void execute(Environment env, String[] args) {
		if (env.hasOption("help"))
			help.run(env, args);
		else
			runCommand(env, args);
	}

	protected void runCommand(Environment env, String[] args) {
		String cmd=defaultCommand;
		if (args.length>0) {
			cmd=args[0];
			args=subArgs(args,1);
		}
		Command command=commands.get(cmd);
		if (command==null)
			System.out.println("Unknown subcommand "+cmd);
		else
			command.run(env, args);
	}
}