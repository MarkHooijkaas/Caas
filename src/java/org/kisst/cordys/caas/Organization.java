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

import org.jdom.Element;

public class Organization extends CordysLdapObject {

	protected Organization(LdapObject parent, String dn) {
		super(parent, dn);
	}

	public NamedObjectList<User> getUsers() {	
		Element method=new Element("GetOrganizationalUsers", xmlns_ldap);
		method.addContent(new Element("dn").setText(dn));
		return getObjectsFromEntries(soapCall(method));
	}


	public NamedObjectList<MethodSet> getMs() { return getMethodSets(); }
	public NamedObjectList<MethodSet> getMethodSets() {	
		Element method=new Element("GetMethodSets", xmlns_ldap);
		method.addContent(new Element("dn").setText(dn));
		method.addContent(new Element("labeleduri").setText("*"));
		return getObjectsFromEntries(soapCall(method));
	}
	
	public NamedObjectList<Role> getRoles() {	
		Element method=new Element("GetRolesForOrganization", xmlns_ldap);
		method.addContent(new Element("dn").setText(dn));
		return getObjectsFromEntries(soapCall(method));
	}

	public NamedObjectList<SoapNode> getSn() { return getSoapNodes(); }	
	public NamedObjectList<SoapNode> getSoapNodes() {	
		Element method=new Element("GetSoapNodes", xmlns_ldap);
		method.addContent(new Element("dn").setText(dn));
		method.addContent(new Element("namespace").setText("*"));
		return getObjectsFromEntries(soapCall(method));
	}
}
