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
import org.kisst.cordys.caas.Isvp;
import org.kisst.cordys.caas.MethodSet;
import org.kisst.cordys.caas.Organization;
import org.kisst.cordys.caas.Role;
import org.kisst.cordys.caas.SoapNode;
import org.kisst.cordys.caas.User;
import org.kisst.cordys.caas.util.FileUtil;
import org.kisst.cordys.caas.util.XmlNode;

public class PackageManager {
	private final CordysSystem system;
	public PackageManager(CordysSystem system) {
		this.system=system;
	}
	
	public Messages validate(String pmfile) {
		Messages warnings = new Messages();
		
		XmlNode pm=new XmlNode(FileUtil.loadString(pmfile));
		for (XmlNode child: pm.getChildren()) {
			if ("soapnode".equals(child.getName()))
				validateSoapNode(warnings, child);
			else if ("user".equals(child.getName()))
				validateUser(warnings, child);
			else if ("isvp".equals(child.getName()))
				validateIsvp(warnings, child);

		}
		return warnings;
	}

	private void validateIsvp(Messages warnings, XmlNode node) {
		String name=node.getAttribute("name");
		Isvp isvp=system.isvp.getByName(name);
		if (isvp==null) {
			warnings.add("ISVP "+name+" not installed");
			return;
		}
		// TODO: check if loaded on all necessary machines
		boolean foundMatchingVersion=false;
		for (XmlNode child: node.getChildren()) {
			if ("version".equals(child.getName())) {
				if (isvp.filename.get().equals(child.getAttribute("filename"))) {
					foundMatchingVersion=true;
					String tested=child.getAttribute("tested");
					if ("OK".equals(tested))
						continue;
					else
						warnings.addWarnings("isvp version "+isvp.filename+" not tested OK",child);
				}
			}
			else
				//throw new RuntimeException("Unknown element in isvp section "+name+":\n"+child.getPretty());
				warnings.add("Unknown element in isvp section "+name+":\n"+child.getPretty());
		}
		if (! foundMatchingVersion)
			warnings.add("No matching version found for isvp ["+isvp.getName()+"], with filename:"+isvp.filename.get());
	}

	private void validateUser(Messages warnings, XmlNode node) {
		String orgName=node.getAttribute("org");
		String name=node.getAttribute("name");
		Organization org=system.org.getByName(orgName);
		if (org==null) {
			warnings.add("Organization "+orgName+" does not exist");
			return;
		}
		User user=org.users.getByName(name);
		if (user==null) {
			warnings.add("User "+name+" in organization "+orgName+" does not exist");
			return;
		}
		// TODO: check if loaded on all necessary machines
		for (XmlNode child: node.getChildren()) {
			if ("role".equals(child.getName())) {
				Role role;
				if (child.getAttribute("isvp").length()==0) {
					role=org.roles.getByName(child.getAttribute("name"));
				}
				else {
					Isvp isvp=system.isvp.getByName(child.getAttribute("isvp"));
					role=isvp.roles.getByName(child.getAttribute("name"));
					if (role==null)
						warnings.add("Unknown role "+child.getAttribute("name"));
				}
				if (! user.roles.contains(role))
					warnings.addWarnings("user "+user+" should have role "+role,child);
			}
			else
				//throw new RuntimeException("Unknown element in user section "+name+":\n"+child.getPretty());
				warnings.add("Unknown element in user section "+name+":\n"+child.getPretty());
		}
	}

	private void validateSoapNode(Messages warnings, XmlNode node) {
		String orgName=node.getAttribute("org");
		String name=node.getAttribute("name");
		Organization org=system.org.getByName(orgName);
		if (org==null) {
			warnings.add("Organization "+orgName+" does not exist");
			return;
		}
		SoapNode sn=org.sn.getByName(name);
		if (sn==null) {
			warnings.add("User "+name+" in organization "+orgName+" does not exist");
			return;
		}
		// TODO: check if loaded on all necessary machines
		for (XmlNode child: node.getChildren()) {
			if ("ms".equals(child.getName())) {
				MethodSet ms;
				if (child.getAttribute("isvp").length()==0) {
					ms=org.ms.getByName(child.getAttribute("name"));
				}
				else {
					Isvp isvp=system.isvp.getByName(child.getAttribute("isvp"));
					ms=isvp.ms.getByName(child.getAttribute("name"));
				}
				if (! sn.ms.contains(ms))
					warnings.addWarnings("soapnode "+sn+" should have methodset "+ms,child);
			}
			else
				//throw new RuntimeException("Unknown element in user section "+name+":\n"+child.getPretty());
				warnings.add("Unknown element in soapnode section "+name+":\n"+child.getPretty());
		}
	}	
}
