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

package org.kisst.cordys.caas.support;

import java.util.LinkedList;
import java.util.List;

import org.kisst.cordys.caas.util.XmlNode;

/**
 * This class works like a list, without being a real java.util.List
 * This hack is necessary, because in groovy the propertyMissing method is never used
 * on objects that inherit from a List.
 * 
 */
public class EntryObjectList<T extends LdapObject> extends CordysObjectList<T>  {
	private final LdapObject parent;
	private final String group;
	private final String type;
	private final LinkedList<String> dangling=new LinkedList<String>();

	public EntryObjectList(LdapObject parent, String group, String type) {
		super(parent.getSystem());
		this.parent=parent;
		this.group=group;
		this.type=type;
	}
	public EntryObjectList(LdapObject parent, String group) {
		this(parent,group, group);
	}
	
	@Override public String getVarName() { return parent.getVarName()+"."+type;}
	@Override public String getKey() { return parent.getKey()+":Entries:"+group;}
	@SuppressWarnings("unchecked")
	@Override protected void retrieveList() {
		dangling.clear();
		XmlNode method=new XmlNode("GetLDAPObject", xmlns_ldap);
		method.add("dn").setText(parent.getDn());
		XmlNode response=system.call(method);
		if (response.getName().equals("Envelope"))
			response=response.getChild("Body").getChildren().get(0);

		//System.out.println(response.getIndented());
		XmlNode start=response.getChild("tuple/old/entry/"+group);
		//System.out.println(start.getIndented());
		if (start==null)
			return;
		for (XmlNode s: start.getChildren("string")) {
			String dn=s.getText();
			CordysObject obj=system.getLdap(dn);
			if (obj==null)
				dangling.add(dn);
			else
				this.grow((T) obj);
		}
	}
	@SuppressWarnings("unchecked")
	public List<String> getDangling() { fetchList(); return (List<String>) dangling.clone(); }
	public void add(LdapObject obj) { add(obj.getDn()); }
	public void remove(LdapObject obj) { remove(obj.getDn()); }
	public void add(String value) {
		XmlNode newEntry=parent.getEntry().clone();
		XmlNode g=newEntry.getChild(group);
		if (g==null)
			g=newEntry.add(group);
		g.add("string").setText(value);
		parent.updateLdap(newEntry);
		parent.clear();
	}
	public void add(String[] values) {
		XmlNode newEntry=parent.getEntry().clone();
		XmlNode g=newEntry.getChild(group);
		if (g==null)
			g=newEntry.add(group);
		for (String s:values)
			g.add("string").setText(s);
		parent.updateLdap(newEntry);
		parent.clear();
	}

	public void remove(String value) {
		XmlNode newEntry= parent.getEntry().clone();
		XmlNode list=newEntry.getChild(group);
		for(XmlNode e: list.getChildren()) {
			if (e.getText().equals(value))
				list.remove(e);
		}
		parent.updateLdap(newEntry);
		parent.clear();
	}
}
