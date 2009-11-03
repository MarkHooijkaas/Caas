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


abstract public class CompositeCommand extends CommandBase {
	protected final LinkedHashMap<String, Command> commands=new LinkedHashMap<String,Command>();
	private final String defaultCommand;
	protected final String usage;

	private Command help=new Command() { 
		public void run(String[] args) { printHelp(args); }
	};
	
	
	public String getCommandNames() {
		String commandNames="";
		for (String c: commands.keySet())
			commandNames+=", "+c;
		return commandNames.substring(2);
	}


	public CompositeCommand(String usage, String defaultCommand) {
		this.usage=usage;
		this.defaultCommand=defaultCommand;
		commands.put("help", help);
	}
	
	@Override public void run(String[] args) {
		String cmd=defaultCommand;
		if (args.length>0) {
			cmd=args[0];
			args=subArgs(args,1);
		}
		Command command=commands.get(cmd);
		if (command==null)
			System.out.println("Unknown subcommand "+cmd);
		else
			command.run(args);
	}
	
	protected static String[] subArgs(String[] args, int pos) {
		String result[]= new String[args.length-pos];
		for (int i=pos; i<args.length; i++)
			result[i-pos]=args[i];
		return result;
	}

}