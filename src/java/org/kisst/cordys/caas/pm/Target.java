package org.kisst.cordys.caas.pm;

import org.kisst.cordys.caas.Organization;
import org.kisst.cordys.caas.support.EntryObjectList;
import org.kisst.cordys.caas.support.LdapObject;
import org.kisst.cordys.caas.util.XmlNode;

public abstract class Target {
	protected final String name;
	protected Target(XmlNode node) {this.name=node.getAttribute("name"); }
	abstract public String getVarName(Organization org);
	abstract boolean exists(Organization org);
	abstract EntryObjectList<?> getList(Organization org);
	
	public String toString() { return name; }
	
	boolean contains(Organization org, LdapObject part) { return getList(org).contains(part); }
	void         add(Organization org, LdapObject part) { getList(org).add(part); }
	void      remove(Organization org, LdapObject part) { getList(org).remove(part); }
	
}
