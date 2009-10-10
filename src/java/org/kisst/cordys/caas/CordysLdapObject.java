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

package org.kisst.cordys.caas;

import org.kisst.cordys.caas.util.XmlNode;


public class CordysLdapObject extends CordysObject implements LdapObject {
	public final static String xmlns_ldap="http://schemas.cordys.com/1.0/ldap";

	private final LdapObject parent; 
	protected final String dn;
	private XmlNode entry;

	protected CordysLdapObject(CordysSystem system, String dn) {
		super(system);
		this.parent=null;
		this.dn=dn;
	}
	
	protected CordysLdapObject(LdapObject parent, String dn) {
		super(parent.getSystem());
		this.parent=parent;
		this.dn=dn;
	}
	public void clear() { entry=null; }
	public LdapObject getParent() { return parent; }
	
	public String getDn() { return dn; }
	public String getName() {
		int pos=dn.indexOf("=");
		int pos2=dn.indexOf(",",pos);
		return dn.substring(pos+1,pos2);
	}
	public String toString() {
		String c=this.getClass().getSimpleName()+"("+getName()+")";
		if (parent!=null && (parent instanceof CordysLdapObject))
			c=parent.toString()+"."+c;
		return c; 
	}
	public boolean equals(Object o) {
		if (o instanceof LdapObject)
			return dn.equals(((LdapObject)o).getDn());
		return false;
	}
	public int compareTo(LdapObject o) {
		if (o==this)
			return 0;
		String[] d1=dn.split(",");
		String[] d2=o.getDn().split(",");
		int p1=d1.length-1;
		int p2=d2.length-1;
		while (p1>=0 && p2>=0 ) {
			int comp=d1[p1--].compareTo(d2[p2--]);
			if (comp!=0)
				return comp;
		}
		return 0;
	}



	public CordysLdapObject refresh() {
		clear();
		return this;  // convenience return value, so you can type obj.refresh().property
	}
	void setEntry(XmlNode entry) {
		this.entry=entry;
		entry.detach();
	}
	public XmlNode getEntry() {
		if (entry!=null && getSystem().getCache())
			return entry;
		XmlNode  method=new XmlNode("GetLDAPObject", xmlns_ldap);
		method.add("dn").setText(dn);
		XmlNode response = soapCall(method);
		setEntry(response.getChild("tuple/old/entry"));
		return entry;
	}

	protected void addLdapString(String group, String value) {
		XmlNode newEntry=getEntry().clone();
		newEntry.getChild(group).add("string").setText(value);
		updateLdap(newEntry);
	}
	protected void removeLdapString(String group, String value) {
		XmlNode newEntry= getEntry().clone();
		for(XmlNode e: newEntry.getChild(group).getChildren()) {
			if (e.getText().equals(value))
				newEntry.remove(e);
		}
		updateLdap(newEntry);
	}

	protected void updateLdap(XmlNode newEntry) {
		XmlNode method=new XmlNode("Update", xmlns_ldap);
		XmlNode tuple=method.add("tuple");
		tuple.add("old").add(entry.clone());
		tuple.add("new").add(newEntry);
		soapCall(method);
		setEntry(newEntry);
	}
}
