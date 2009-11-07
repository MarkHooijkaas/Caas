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
import java.util.Map.Entry;


abstract public class CompositeCommand implements Command {
	private class HelpCommand extends CommandBase {
		HelpCommand() {super("[<subcmd>]"); }
		public void run(String[] args) { 
			Command cmd=CompositeCommand.this;
			String prefix=CompositeCommand.this.prefix;
			if (args.length>0) {
				Command cmd2=commands.get(args[0]);
				if (cmd2!=null) {
					prefix+=" "+args[0];
					cmd=cmd2;
				}
			}
			System.out.println("Usage: "+prefix+" "+cmd.getUsage()); 
			System.out.println(cmd.getHelp()); 
		}
	}
	
	protected final LinkedHashMap<String, Command> commands=new LinkedHashMap<String,Command>();
	private final String defaultCommand;
	//private final CompositeCommand parent;
	private final String prefix;
	protected final Command help=new HelpCommand();
	
	public CompositeCommand(String prefix) { this(prefix,"help"); }
	public CompositeCommand(String prefix,String defaultCommand) {
		this.defaultCommand=defaultCommand;
		this.prefix=prefix;
		commands.put("help", help);
	}

	public String getUsage() {
		String commandNames="";
		for (String c: commands.keySet())
			commandNames+="|"+c;
		return "["+commandNames.substring(1)+"] [arg ...]";

	}

	public String getHelp() {
		StringBuilder result=new StringBuilder("\nUse any of the following commands:\n");
		for (Entry<String,Command> entry: commands.entrySet()) {
			Command cmd=entry.getValue();
			result.append("\t"+prefix+" "+entry.getKey()+" "+cmd.getUsage()+"\n");
		}
		return result.toString();
	}
	
	public String getCommandNames() {
		String commandNames="";
		for (String c: commands.keySet())
			commandNames+=", "+c;
		return commandNames.substring(2);
	}

	public void run(String[] args) {
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