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

public class CaasMain extends CompositeCommand {

	public static void main(String[] args) {
		CaasMain caas=new CaasMain();
		caas.run(new Environment(), args);
	}

	private final PmCommand pm=new PmCommand();
	private final GroovyCaasShell shell = new GroovyCaasShell();

	private CaasMain() {
		super("caas [options] [cmd] [suboptions]");
		commands.put("pm", pm);
		commands.put("shell", shell);
		commands.put("run", shell);
		
		options.addOption("q", "quiet", false, "don't output anything unless errors happen");
		options.addOption("v", "verbose", false, "be verbose about what you are doing");
		options.addOption("d", "debug", false, "if this option is set debug logging will be shown");
		options.addOption("c", "cop", true, "location of a .cop file with connection properties");
	}
	
	@Override public void execute(Environment env, String[] args) {
		env.setSystem(env.getOptionValue("cop"));

		if (env.hasOption("debug"))
			env.debug=true;
		if (env.hasOption("quiet"))
			env.quiet=true;
		
		if (! env.quiet)
			System.out.println("caas: Cordys Administration Automation Scripting, version "+Caas.getVersion());

		defaultCommand="shell";
		super.execute(env, args);
	}
}
