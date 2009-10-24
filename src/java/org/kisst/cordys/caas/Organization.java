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

import org.kisst.cordys.caas.support.ChildList;
import org.kisst.cordys.caas.support.LdapObjectBase;
import org.kisst.cordys.caas.support.CordysObject;
import org.kisst.cordys.caas.support.CordysObjectList;
import org.kisst.cordys.caas.support.LdapObject;
import org.kisst.cordys.caas.util.XmlNode;



public class Organization extends LdapObjectBase {
	public final ChildList<User> users= new ChildList<User>(this, "cn=organizational users,", User.class);
	public final ChildList<User> user = users;
	public final ChildList<User> u    = users;

	public final ChildList<Role> roles= new ChildList<Role>(this, "cn=organizational roles,", Role.class);
	public final ChildList<Role> role= roles;
	public final ChildList<Role> r   = roles;

	public final ChildList<MethodSet> methodSets= new ChildList<MethodSet>(this, "cn=method sets,", MethodSet.class);
	public final ChildList<MethodSet> ms = methodSets;

	public final ChildList<SoapNode> soapNodes= new ChildList<SoapNode>(this, "cn=soap nodes,", SoapNode.class);
	public final ChildList<SoapNode> sn = soapNodes;
	
	// These fields must be initialized in constructor because system is only known there
	public final CordysObjectList<SoapProcessor> soapProcessors; 
	public final CordysObjectList<SoapProcessor> sp; 
	

	@SuppressWarnings("unchecked")
	protected Organization(LdapObject parent, String dn) {
		super(parent, dn);
		soapProcessors = new CordysObjectList(parent.getSystem()) {
			protected void retrieveList() {
				for (SoapNode sn: soapNodes) {
					for (SoapProcessor sp: sn.soapProcessors)
						grow(sp);
				}
			}
		}; 
		sp = soapProcessors;
	}

	public String call(String input) { return getSystem().call(input, getDn(), null); }

	@Override
	protected void preDeleteHook() {
		for (SoapProcessor sp: soapProcessors)
			sp.stop();
	}

	public void createMethodSet(String name, String namespace, String implementationclass) {
		XmlNode newEntry=newEntryXml("cn=method sets,", name,"busmethodset");
		newEntry.add("labeleduri").add("string").setText(namespace);
		newEntry.add("implementationclass").add("string").setText(implementationclass);
		createInLdap(newEntry);
		methodSets.clear();
	}

	public void createUser(String name, AuthenticatedUser au) {
		XmlNode newEntry=newEntryXml("cn=organizational users,", name,"busorganizationaluser","busorganizationalobject");
		newEntry.add("authenticationuser").add("string").setText(au.getDn());
		newEntry.add("menu");
		newEntry.add("toolbar");
		newEntry.add("role").add("string").setText("cn=everyoneIn"+getName()+",cn=organizational roles,"+getDn());
		createInLdap(newEntry);
		users.clear();
	}

	public void createRole(String name) {
		XmlNode newEntry=newEntryXml("cn=organizational roles,", name,"busorganizationalrole","busorganizationalobject");
		newEntry.add("description").add("string").setText(name);
		newEntry.add("menu");
		newEntry.add("toolbar");
		newEntry.add("role").add("string").setText("cn=everyoneIn"+getName()+",cn=organizational roles,"+getDn());
		createInLdap(newEntry);
		roles.clear();
	}

	
	public void diff(CordysObject other, int depth) {
		if (this==other)
			return;
		Organization otherOrg = (Organization) other;
		soapNodes.diff(otherOrg.soapNodes,depth);
		users.diff(otherOrg.users, depth);
		roles.diff(otherOrg.roles, depth);
	}
	
	public XmlNode getXml(String key, String version) { return getSystem().getXml(key, version, getDn()); }
	public XmlNode getXml(String key) { return getSystem().getXml(key, "organization", getDn()); }
}
