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
	private Command check=new CommandBase("<file.caasii|isvpname>", "validates the given install info") {
		public void run(String[] args) {
			Environment env=Environment.get();
			boolean result=Caas.pm.p(args[0]).check(env.getSystem());
			System.out.println(result);
		}
	};
	private Command configure=new CommandBase("<file.caasii|isvpname>", "installs the given isvp") {
		public void run(String[] args) {
			Environment env=Environment.get();
			Caas.pm.p(args[0]).configure(env.getSystem());
		}
	};
	private Command purge=new CommandBase("<file.caasii|isvpname>", "removes the given isvp") {
		public void run(String[] args) {
			Environment env=Environment.get();
			Caas.pm.p(args[0]).purge(env.getSystem());
		}
	};
	
	public PmCommand() {
		super("caas pm","run a caas package manager command"); 
		//options.addOption("o", "org", true, "override the default organization");
		commands.put("check", check);
		commands.put("configure", configure);
		commands.put("purge", purge);
	}
}
