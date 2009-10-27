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

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;
import org.kisst.cordys.caas.Caas;

public class CaasMain {

	public static void main(String[] args) {
		PmCommand pm=new PmCommand();
		Options options = new Options();
		options.addOption("h", "help", false, "print this help message");
		options.addOption("q", "quiet", false, "don't output anything unless errors happen");
		//options.addOption("v", "verbose", false, "be verbose about what you are doing");
		//options.addOption("d", "debug", false, "if this option is set debug logging will be shown");
		options.addOption("c", "cop", true, "location of a .cop file with connection properties");
		
		CommandLine cmdline;
		try {
			cmdline = new PosixParser().parse( options, args, true);
		} catch (ParseException e) { throw new RuntimeException(e); }
		Environment env;
		//if (cmdline.hasOption("cop"))
			env=new Environment(cmdline.getOptionValue("cop"));
		//boolean debug=false;
		//if (cmdline.hasOption("debug"))
		//	debug=true;
		if (cmdline.hasOption("help")) {
			new HelpFormatter().printHelp(80,  
					"Usage: caas [options] [cmd] [suboptions]"
					+"\twhere <cmd> one of pm, shell, run",
					"Options:",
					options, null );
			return;
		}
		if (! cmdline.hasOption("quiet"))
			System.out.println("caas: Cordys Administration Automation Scripting, version "+Caas.getVersion());
			
		
		
		args=cmdline.getArgs();
		if (args.length==0)
			GroovyCaasShell.main(args);
		else if ("pm".equals(args[0]))
			pm.run(env, subArgs(args,1));
		else if ("shell".equals(args[0]))
			GroovyCaasShell.main(subArgs(args,1));
		else if ("run".equals(args[0]))
			GroovyCaasShell.main(subArgs(args,1));
		else 
			System.out.println("Unknown command "+args[0]);
	}
	private static String[] subArgs(String[] args, int pos) {
		String result[]= new String[args.length-pos];
		for (int i=pos; i<args.length; i++)
			result[i-pos]=args[i];
		return result;
	}

}
