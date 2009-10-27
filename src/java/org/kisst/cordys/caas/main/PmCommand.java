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

import org.apache.commons.cli.Options;
import org.kisst.cordys.caas.pm.PackageManager;

public class PmCommand extends Command {
	@Override public void run(Environment env, String[] args) {
		PackageManager pm=new PackageManager(env.system);
		if ("validate".equals(args[0]))
			System.out.println(pm.validate(args[1]));
		else
			throw new RuntimeException("Unknown pm subcommand "+args[0]);
	}

	@Override public Options getOptions() {
		return null;
	}	
}
