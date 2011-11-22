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

package org.kisst.cordys.caas.cm;

import java.util.LinkedList;
import java.util.List;

import org.kisst.cordys.caas.CordysSystem;
import org.kisst.cordys.caas.Organization;
import org.kisst.cordys.caas.util.FileUtil;
import org.kisst.cordys.caas.util.XmlNode;

public class CaasPackage implements Objective {
	private final LinkedList<Objective> objectives = new LinkedList<Objective>();
	private final String orgName;
	private int status;
	private final String name;
	private final CordysSystem system;
	
	public CaasPackage(String pmfile, CordysSystem system) {
		this.name=pmfile;
		this.system = system;
		XmlNode pm=new XmlNode(FileUtil.loadString(pmfile));
		orgName=pm.getAttribute("org");
		Organization org = system.organizations.get(orgName);
		for (XmlNode child: pm.getChildren()) {
			if ("soapnode".equals(child.getName()))
				objectives.add(new SoapNodeObjective(org, child));
			else if ("user".equals(child.getName()))
				objectives.add(new UserObjective(org, child));
			else if ("xmlstore".equals(child.getName()))
				objectives.add(new XmlStoreObjective(org, child));
			else if ("isvp".equals(child.getName()))
				objectives.add(new IsvpObjective(system, child));

		}
	}
	public String getName() { return name; }
	public String toString() { return "CaasPackage("+name+")"; }
	public List<Objective> getChildren() { return objectives; }
	public String getMessage() { return "see Children";	}
	public int getStatus() { return status; }

	public String getDefaultOrgName() { return orgName; }
	public CordysSystem getSystem() { return system; }

	
	public int check(Ui ui) {
		ui.checking(this);
		status=OK;
		for (Objective o: objectives) 
			status = Math.max(status, o.check(ui)); 
		ui.readyWith(this);

		return status;
	}
	
	public void configure(Ui ui) {
		ui.configuring(this);
		for (Objective o: objectives)
			o.configure(ui);
		ui.readyWith(this);
	}

	public void purge(Ui ui) {
		for (Objective o: objectives)
			o.purge(ui);
	}
}
