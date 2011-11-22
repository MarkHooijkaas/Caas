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
import org.kisst.cordys.caas.Isvp;
import org.kisst.cordys.caas.main.Environment;
import org.kisst.cordys.caas.util.XmlNode;

public class IsvpObjective extends AbstractObjective {
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
	
	public IsvpObjective(CordysSystem system, XmlNode node) {
		super(system);
		name=node.getAttribute("name");
		for (XmlNode child: node.getChildren()) {
			if ("version".equals(child.getName()))
				versions.add(new Version(child));
			else
				throw new RuntimeException("Unknown element in isvp section "+name+":\n"+child.getPretty());
		}
	}

	public String toString() { return "IsvpObjective("+name+")"; }
	
	protected void myCheck(Ui ui) {
		Environment env=Environment.get();
		
		Isvp isvp=getSystem().isvp.getByName(name);
		if (isvp==null) {
			message="required isvp "+name+" is not installed";
			ui.error(this,message);
			status=ERROR;
			return;
		}
		// TODO: check if loaded on all necessary machines
		boolean foundMatchingVersion=false;
		for (Version v: versions) {
			env.debug("Checking "+isvp.filename+" against version "+v.filename);
			if (isvp.filename.get().equals(v.filename)) {
				ui.info(this, v.filename+ " matches");
				foundMatchingVersion=true;
				if ("OK".equals(v.tested)) {
					status=OK;
					return;
				}
				else {
					message="required isvp "+isvp.getVarName()+" has version "+isvp.filename+" that tested "+v.tested;
					ui.error(this,message);
					for (String s: v.warnings)
						env.warn("\t"+s);
				}
			}
		}
		if (! foundMatchingVersion) {
			message="required isvp "+isvp.getVarName()+", has version "+isvp.filename.get()+" that was not mentioned in the known versions";
			ui.error(this,message);
		}
		status=ERROR;
		return;
	}
	
	protected void myConfigure(Ui ui) { /* do nothing, automatically loading  not supported */	}
	protected void myPurge(Ui ui) {    /* do nothing, automatically unloading  not supported */}

}