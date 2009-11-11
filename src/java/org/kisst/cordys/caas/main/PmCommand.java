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

import org.kisst.cordys.caas.Caas;


public class PmCommand extends CompositeCommand {
	private abstract class HostCommand extends CommandBase {
		public HostCommand(String usage, String summary) { super(usage, summary); }
		Cli cli=new Cli();
		Cli.StringOption system= cli.stringOption("s", "system", "the system to use", null);
		protected String[] checkArgs(String[] args) {
			args=cli.parse(args);
			if (system.isSet())
				Caas.defaultSystem=system.get();
			return args;
		}
		@Override public String getHelp() {
			return "\nOPTIONS\n"+cli.getSyntax("\t");
		}
	}
	
	private Command check=new HostCommand("[options] <file.caasii|isvpname>", "validates the given install info") {
		@Override public void run(String[] args) {
			args=checkArgs(args);
			boolean result=Caas.pm.p(args[0]).check(Caas.getDefaultSystem());
			System.out.println(result);
		}
	};
	private Command configure=new HostCommand("[options] <file.caasii|isvpname>", "installs the given isvp") {
		@Override public void run(String[] args) { Caas.pm.p(args[0]).configure(Caas.getDefaultSystem());	}
	};
	private Command purge=new HostCommand("[options] <file.caasii|isvpname>", "removes the given isvp") {
		@Override public void run(String[] args) { Caas.pm.p(args[0]).purge(Caas.getDefaultSystem()); 	}
	};
	
	public PmCommand() {
		super("caas pm","run a caas package manager command"); 
		//options.addOption("o", "org", true, "override the default organization");
		commands.put("check", check);
		commands.put("configure", configure);
		commands.put("purge", purge);
	}
}
