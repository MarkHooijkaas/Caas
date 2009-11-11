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
import org.kisst.cordys.caas.main.Environment;
import org.kisst.cordys.caas.support.ChildList;
import org.kisst.cordys.caas.support.LdapObject;
import org.kisst.cordys.caas.util.XmlNode;

public class ObjectiveBase implements Objective {
	private static final Environment env=Environment.get();
	private class Entry {
		public final String propName;
		public final String isvpName;
		public final String name;
		public Entry(String propName, XmlNode node) {
			this.propName=propName;
			this.isvpName=node.getAttribute("isvp");
			this.name=node.getAttribute("name");
		}
		@Override public String toString() { return "isvp.\""+isvpName+"\"."+propName+"."+name; } 
		public LdapObject findEntry(Organization org) {
			CordysSystem system=org.getSystem();
			LdapObject result=null;
			if (isvpName.length()==0)
				result = ((ChildList<?>) org.getProp(propName)).getByName(name);
			else {
				Isvp isvp=system.isvp.getByName(isvpName);
				if (isvp==null) {
					env.error("target "+target.getVarName(org)+" refers to UNKNOWN isvp \""+isvpName+"\" in entry "+this);
					return null;
				}
				if (isvp!=null)
					result=((ChildList<?>) isvp.getProp(propName)).getByName(name);
			}
			if (result==null)
				env.error("target "+target.getVarName(org)+" refers to UNKNOWN entry "+this);
			return result;
		}
	}
	
	public final Target target;
	private final LinkedList<Entry> entries=new LinkedList<Entry>();
	public ObjectiveBase(String propName, Target target, XmlNode node) {
		this.target=target;
		for (XmlNode child: node.getChildren())
			entries.add(new Entry(propName, child));
	}
	
	public boolean check(Organization org) {
		if (! target.exists(org)) {
			env.error("unknown target "+target);
			return false;
		}
		boolean ok=true;
		for (Entry e:entries)
			ok=check(org,e.findEntry(org)) && ok;
		return ok;
	}
	
	private boolean check(Organization org, LdapObject entry) {
		if (entry==null)
			return false;
		else if (target.contains(org, entry)) {
			env.info("target "+target+" has entry "+entry.getVarName());
			return true;
		}
		env.error("target "+target+" should have entry "+entry.getVarName());
		return false;
	}

	public void configure(Organization org) { 
		if (! target.exists(org)) {
			env.error("unknown target "+target);
			return;
		}
		for (Entry e:entries)
			configure(org,e.findEntry(org));
	}
	private void configure(Organization org, LdapObject entry) { 
		if (entry==null)
			env.warn("target "+target+" should have unknown entry "); // TODO: +entry.getVarName());
		else if (target.contains(org, entry))
			env.warn("target "+target+" already has entry "+entry.getVarName());
		else
			target.add(org, entry); 
	}

	public void remove(Organization org) { 
		if (! target.exists(org)) {
			env.error("unknown target "+target);
			return;
		}
		for (Entry e:entries)
			remove(org,e.findEntry(org));
	}
	private void remove(Organization org, LdapObject entry) { 
		if (entry==null)
			env.warn("target "+target+" should have unknown entry "); // TODO: +entry.getVarName());
		else if (! target.contains(org, entry))
			env.warn("target "+target+" does not have entry "+entry.getVarName());
		else
			target.remove(org, entry); 
	}
}