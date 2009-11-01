package org.kisst.cordys.caas.pm;

import org.kisst.cordys.caas.Organization;
import org.kisst.cordys.caas.support.EntryObjectList;
import org.kisst.cordys.caas.support.LdapObject;
import org.kisst.cordys.caas.util.XmlNode;

public abstract class Target {
	protected final String name;
	protected Target(XmlNode node) {this.name=node.getAttribute("name"); }
	abstract boolean exists(Organization org);
	abstract EntryObjectList<?> getList(Organization org);
	
	boolean links(Organization org, LdapObject part) { return getList(org).contains(part); }
	void     link(Organization org, LdapObject part) { getList(org).add(part); }
	void   unlink(Organization org, LdapObject part) { getList(org).remove(part); }
	
	public static class User extends Target {
		public User(XmlNode node) { super(node); }
		@Override boolean exists(Organization org) { return org.users.getByName(name)!=null; }
		@Override EntryObjectList<?> getList(Organization org) { return org.users.getByName(name).roles; }
	}

	public static class SoapNode extends Target {
		SoapNode(XmlNode node) { super(node); }
		@Override boolean exists(Organization org) { return org.sn.getByName(name)!=null; }
		@Override EntryObjectList<?> getList(Organization org) { return org.sn.getByName(name).ms; }
	}
}
