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

import java.util.HashSet;

import org.kisst.cordys.caas.CordysSystem;
import org.kisst.cordys.caas.Organization;
import org.kisst.cordys.caas.Role;
import org.kisst.cordys.caas.main.Environment;
import org.kisst.cordys.caas.support.EntryObjectList;
import org.kisst.cordys.caas.support.LdapObject;
import org.kisst.cordys.caas.util.XmlNode;

public abstract class ObjectiveBase extends CompositeObjective {
	private static final Environment env=Environment.get();
	
	private final boolean otherEntriesAllowed;

	protected final Organization org;
	
	public ObjectiveBase(Organization org, String propName, XmlNode node, boolean defaultOtherEntriesAllowed) {
		super(node.getAttribute("name"));
		this.org=org;
		this.otherEntriesAllowed="true".equals(node.getAttribute("otherEntriesAllowed", ""+defaultOtherEntriesAllowed)); 
		for (XmlNode child: node.getChildren())
			entries.add(new EntryObjective(org, this, propName, child));
	}

	abstract public String getVarName();
	abstract boolean exists();
	abstract void create();
	abstract EntryObjectList<?> getList();
	
	boolean contains(LdapObject part) { return getList().contains(part); }
	void         add(LdapObject part) { getList().add(part); }
	void      remove(LdapObject part) { getList().remove(part); }
	public CordysSystem getSystem() { return org.getSystem(); }


	public int check(Ui ui) {
		ui.checking(this);
		message=null;
		status=OK;
		if (! exists()) {
			message="unknown target "+this;
			ui.error(this,message);
			status=ERROR;
			return status;
		}
		super.check(ui);
		if (containsInvalidEntries())
			status=Math.max(status,WARN);
		ui.readyWith(this);
		return status;
	}
	
	private boolean containsInvalidEntries() {
		if (otherEntriesAllowed)
			return false;
		boolean otherEntries=false;
		EntryObjectList<?> lst = getList();

		HashSet<LdapObject> set=new HashSet<LdapObject>();
		for (Objective e:entries) {
			if (e instanceof EntryObjective)
				set.add(((EntryObjective)e).findEntry());
		}
		for (LdapObject obj: lst)  {
			if (obj instanceof Role && obj.getName().equals("everyoneIn"+org.getName())) // TODO: Dirty hack
				continue;
			if (! set.contains(obj)) {
				otherEntries=true;
				String msg="Unknown existing entry "+obj+" in target "+this;
				env.error(msg);
				if (message==null)
					message=msg;
				else
					message+="\n"+message;
			}
		}
		return otherEntries;
	}

	
	public void configure(Ui ui) { 
		ui.configuring(this);
		if (! exists()) {
			create();
		}
		super.configure(ui);
		containsInvalidEntries();
		ui.readyWith(this);
	}

	public void purge(Ui ui) { 
		if (! exists()) {
			env.error("unknown target "+this);
			return;
		}
		super.purge(ui);
	}
}