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

import java.util.LinkedList;

import org.kisst.cordys.caas.CordysSystem;
import org.kisst.cordys.caas.Organization;
import org.kisst.cordys.caas.util.FileUtil;
import org.kisst.cordys.caas.util.XmlNode;

public class CaasPackage {
	private final LinkedList<Objective> objectives = new LinkedList<Objective>();
	private final String orgName;

	public CaasPackage(String pmfile) {
		XmlNode pm=new XmlNode(FileUtil.loadString(pmfile));
		orgName=pm.getAttribute("org");
		
		for (XmlNode child: pm.getChildren()) {
			if ("soapnode".equals(child.getName()))
				objectives.add(new SoapNodeObjective(child));
			else if ("user".equals(child.getName()))
				objectives.add(new UserObjective(child));
			else if ("isvp".equals(child.getName()))
				objectives.add(new IsvpObjective(child));

		}
	}
	public String getDefaultOrgName() { return orgName; }
	
	public boolean check(CordysSystem system) { return check(system.org.getByName(orgName)); }
	public void configure(CordysSystem system)   { configure(system.org.getByName(orgName)); }
	public void purge(CordysSystem system)       { purge(system.org.getByName(orgName)); }
	
	public boolean check(Organization org) {
		boolean result=true;
		for (Objective o: objectives) 
			result= o.check(org) && result; 
		return result;
	}
	
	public void configure(Organization org) {
		for (Objective o: objectives)
			o.configure(org);
	}

	public void purge(Organization org) {
		for (Objective o: objectives)
			o.remove(org);
	}

}
