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


public class PmCommand extends CompositeCommand{
	private Command validate=new Command() {
		public void run(String[] args) {
			Environment env=Environment.get();
			System.out.println(env.getSystem().pm.validate(args[0],null));
		}
	};
	private Command configure=new Command() {
		public void run(String[] args) {
			Environment env=Environment.get();
			System.out.println(env.getSystem().pm.p(args[0]).configure());
		}
	};
	private Command purge=new Command() {
		public void run(String[] args) {
			System.out.println("purge command not implemented yet");
		}
	};
	
	public PmCommand() {
		super("caas [options] pm <cmd> [suboptions]");
		options.addOption("o", "org", true, "override the default organization");
		commands.put("validate", validate);
		commands.put("configure", configure);
		commands.put("purge", purge);
	}
}
