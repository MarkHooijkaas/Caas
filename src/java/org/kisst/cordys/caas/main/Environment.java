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
import org.kisst.cordys.caas.CordysSystem;

public class Environment {
	public boolean debug=false;
	public boolean quiet=false;
	public boolean verbose=false;
	public final CordysSystem system;
	public Environment(String copfile) {
		if (copfile==null)
			copfile=System.getProperty("user.home")+"/config/caas/default.cop";
		log(copfile);
		this.system=Caas.connect(copfile);
	}
	
	public void log(String msg){ System.out.println(msg);}
}
