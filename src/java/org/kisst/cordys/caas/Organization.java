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

public class Organization extends CordysLdapObject {
	private LdapObjectList<SoapNode> cachedSoapNodes=null;

	protected Organization(LdapObject parent, String dn) {
		super(parent, dn);
	}

	public void clear() {
		super.clear();
		cachedSoapNodes=null;
	}
	
	public LdapObjectList<User> getUser() { return getUsers(); }
	public LdapObjectList<User> getUsers() {	
		XmlNode method=new XmlNode("GetOrganizationalUsers", xmlns_ldap);
		method.add("dn").setText(dn);
		return new LdapObjectList<User>(system, method);
	}


	public LdapObjectList<MethodSet> getMs() { return getMethodSets(); }
	public LdapObjectList<MethodSet> getMethodSets() {	
		XmlNode method=new XmlNode("GetMethodSets", xmlns_ldap);
		method.add("dn").setText(dn);
		method.add("labeleduri").setText("*");
		return new LdapObjectList<MethodSet>(system, method);
	}
	
	public LdapObjectList<Role> getRole() { return getRoles(); }
	public LdapObjectList<Role> getRoles() {	
		XmlNode method=new XmlNode("GetRolesForOrganization", xmlns_ldap);
		method.add("dn").setText(dn);
		return new LdapObjectList<Role>(system, method);
	}

	public LdapObjectList<SoapNode> getSn() { return getSoapNodes(); }	
	public LdapObjectList<SoapNode> getSoapNodes() {	
		if (cachedSoapNodes==null || ! system.getCache()) {
			XmlNode method=new XmlNode("GetSoapNodes", xmlns_ldap);
			method.add("dn").setText(dn);
			method.add("namespace").setText("*");
			cachedSoapNodes=new LdapObjectList<SoapNode>(system, method);
		}
		return cachedSoapNodes;
	}

	public LdapObjectList<SoapProcessor> getSp() { 
		return getSoapProcessors();
	}

	public LdapObjectList<SoapProcessor> getSoapProcessors() {
		LdapObjectList<SoapProcessor> result= new LdapObjectList<SoapProcessor>();
		for (Object sn : getSoapNodes()) {
			for (Object sp : ((SoapNode) sn).getSoapProcessors()) {
				result.add((SoapProcessor) sp); // using dn, to prevent duplicate names over organizations
			}
		}
		return result;
	}
	
	public void diff(LdapObject other, int depth) {
		if (this==other)
			return;
		Organization otherOrg = (Organization) other;
		getSoapNodes().diff(otherOrg.getSoapNodes(),depth);
		getUsers().diff(otherOrg.getUsers(), depth);
		getRoles().diff(otherOrg.getRoles(), depth);
	}

}
