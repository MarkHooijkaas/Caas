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
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;
import org.kisst.cordys.caas.Caas;
import org.kisst.cordys.caas.CordysSystem;

public class Environment {
	public boolean debug=false;
	public boolean quiet=false;
	public boolean verbose=false;
	private CordysSystem system;
	private CommandLine cmdline;
	private String copfile;
	
	public void setSystem(String copfile) { this.copfile=copfile; }
	public CordysSystem getSystem() {
		if (system!=null)
			return system;
		if (copfile==null)
			copfile=System.getProperty("user.home")+"/config/caas/default.cop";
		log(copfile);
		system=Caas.connect(copfile);
		return system;
	}
	
	public void log(String msg){ System.out.println(msg);}
	public boolean hasOption(String name) { return cmdline.hasOption(name); }
	public String getOptionValue(String name) { return cmdline.getOptionValue(name); }
	
	public String[] parse(Options options, String[] args) {
		try {
			cmdline = new PosixParser().parse( options, args, true);
		} catch (ParseException e) { throw new RuntimeException(e); }
		return cmdline.getArgs();
	}
}
