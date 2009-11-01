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
import org.kisst.cordys.caas.Isvp;
import org.kisst.cordys.caas.Organization;
import org.kisst.cordys.caas.support.ChildList;
import org.kisst.cordys.caas.support.LdapObject;
import org.kisst.cordys.caas.support.LdapObjectBase;
import org.kisst.cordys.caas.util.FileUtil;
import org.kisst.cordys.caas.util.XmlNode;

public class CaasPackage {
	public static class GhostObject extends LdapObjectBase {
		private GhostObject(LdapObject parent, String isvpName, String type, String entry) {
			super(parent, parent.getSystem().getName()+".isvp.\""+isvpName+"\"."+type+".\""+entry+"\"");
		}
		@Override public String getVarName() { return getDn(); }
	}
	
	private final CordysSystem system;
	private final LinkedList<Objective> objectives = new LinkedList<Objective>();
	private final Messages warnings=new Messages();
	private final Organization org;

	public CaasPackage(CordysSystem system, String pmfile, String org) {
		String orgName;
		this.system=system;
		XmlNode pm=new XmlNode(FileUtil.loadString(pmfile));
		if (org==null)
			orgName=pm.getAttribute("org");
		else
			orgName=org;
		this.org=system.org.getByName(orgName);
		
		for (XmlNode child: pm.getChildren()) {
			if ("soapnode".equals(child.getName()))
				parseSoapNode(child);
			else if ("user".equals(child.getName()))
				parseUser(child);
			else if ("isvp".equals(child.getName()))
				parseIsvp(child);

		}
	}

	public Messages validate() {
		Messages warnings=this.warnings.clone();
		for (Objective o: objectives) {
			if (o.target==null)
				warnings.add("unknown target "+o.target+" should have entry "+o.entry.getVarName());
			else if (o.entry instanceof GhostObject)
				warnings.add("target "+o.target+" should have unknown entry "+o.entry.getVarName());
			else if (!o.isSatisfied(org))
				warnings.add("target "+o.target+" should have entry "+o.entry.getVarName());
		}
		return warnings;
	}
	
	public Messages configure() {
		Messages warnings=this.warnings.clone();
		for (Objective o: objectives) {
			if (o.target==null)
				warnings.add("unknown target "+o.target+" should have entry "+o.entry.getVarName());
			else if (o.entry instanceof GhostObject)
				warnings.add("target "+o.target+" should have unknown entry "+o.entry.getVarName());
			else if (o.isSatisfied(org))
				warnings.add("target "+o.target+" already has entry "+o.entry.getVarName());
			else
				o.satisfy(org);
		}
		return warnings;
	}

	private void parseIsvp(XmlNode node) {
		String name=node.getAttribute("name");
		Isvp isvp=system.isvp.getByName(name);
		if (isvp==null) {
			warnings.add("required isvp "+name+" is not installed");
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
						warnings.addWarnings("required isvp "+isvp.getVarName()+" has version "+isvp.filename+" that tested "+tested,child);
				}
			}
			else
				throw new RuntimeException("Unknown element in isvp section "+name+":\n"+child.getPretty());
		}
		if (! foundMatchingVersion)
			warnings.add("required isvp "+isvp.getVarName()+", has version "+isvp.filename.get()+" that was not mentioned in the known versions");
	}

	private void parseSoapNode(XmlNode node) {
		Target target=new Target.SoapNode(node);
		for (XmlNode child: node.getChildren()) {
				LdapObject entry=findEntry(child,"ms");
				objectives.add(new Objective(target, entry));
		}
	}

	private void parseUser(XmlNode node) {
		Target target = new Target.User(node);
		for (XmlNode child: node.getChildren()) {
				LdapObject entry=findEntry(child,"role");
				objectives.add(new Objective(target, entry));
		}
	}

	private LdapObject findEntry(XmlNode node, String propName) {
		if (! propName.equals(node.getName()))
			throw new RuntimeException("Unknown element in "+node.getParent().getName()+" section "+node.getParent().getAttribute("name")+":\n"+node.getPretty());
			
		String entryName=node.getAttribute("name");
		String isvpName=node.getAttribute("isvp");
		LdapObject entry=null;
		if (isvpName.length()==0)
			entry=((ChildList<?>) org.getProp(propName)).getByName(entryName);
		else {
			Isvp isvp=system.isvp.getByName(isvpName);
			if (isvp!=null)
				entry=((ChildList<?>) isvp.getProp(propName)).getByName(entryName);
		}
		if (entry==null)
			return new GhostObject(system, isvpName,propName,entryName);
		else
			return entry;
	}
}
