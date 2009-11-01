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

import java.util.List;
import java.util.LinkedList;

import org.kisst.cordys.caas.CordysSystem;
import org.kisst.cordys.caas.Isvp;
import org.kisst.cordys.caas.Organization;
import org.kisst.cordys.caas.main.Environment;
import org.kisst.cordys.caas.util.XmlNode;

public class IsvpObjective implements Objective {
	public static class Version {
		public final String filename;
		public final String tested;
		public final String[] warnings;
		public Version(XmlNode node) {
			this.filename=node.getAttribute("filename");
			this.tested=node.getAttribute("tested");
			List<XmlNode> children=node.getChildren();
			warnings=new String[children.size()];
			int i=0;
			for (XmlNode child: children)
				warnings[i]=child.getAttribute("message");
		}
	}
	
	private final String name;
	private final LinkedList<Version> versions=new LinkedList<Version>();
	
	public IsvpObjective(XmlNode node) {
		name=node.getAttribute("name");
		for (XmlNode child: node.getChildren()) {
			if ("version".equals(child.getName()))
				versions.add(new Version(child));
			else
				throw new RuntimeException("Unknown element in isvp section "+name+":\n"+child.getPretty());
		}
	}
	
	public boolean check(Organization org) {
		CordysSystem system=org.getSystem();
		Environment env=Environment.get();
		
		Isvp isvp=system.isvp.getByName(name);
		if (isvp==null) {
			env.error("required isvp "+name+" is not installed");
			return false;
		}
		// TODO: check if loaded on all necessary machines
		boolean foundMatchingVersion=false;
		for (Version v: versions) {
			env.debug("Checking "+isvp.filename+" against version "+v.filename);
			if (isvp.filename.get().equals(v.filename)) {
				env.info(v.filename+ " matches");
				foundMatchingVersion=true;
				if ("OK".equals(v.tested))
					return true;
				else {
					env.error("required isvp "+isvp.getVarName()+" has version "+isvp.filename+" that tested "+v.tested);
					for (String s: v.warnings)
						env.warn("\t"+s);
				}
			}
		}
		if (! foundMatchingVersion)
			env.error("required isvp "+isvp.getVarName()+", has version "+isvp.filename.get()+" that was not mentioned in the known versions");
		return false;
	}
	
	public void configure(Organization org) { /* do nothing, automatically loading  not supported */	}
	public void remove(Organization org) {    /* do nothing, automatically unloading  not supported */}
}