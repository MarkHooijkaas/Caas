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

package org.kisst.cordys.caas.pm;

import org.kisst.cordys.caas.CordysSystem;

public class PackageManager {
	private final CordysSystem system;
	public PackageManager(CordysSystem system) {
		this.system=system;
	}
	
	public CaasPackage p(String filename) { return new CaasPackage(system,filename, null); }
    public Messages validate(String pmfile) { return validate(pmfile, null); }
    public Messages validate(String pmfile, String org) {
    	return new CaasPackage(system, pmfile, org).validate(); 
	}
}
