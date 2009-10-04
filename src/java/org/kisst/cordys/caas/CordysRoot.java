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


public class CordysRoot  extends CordysLdapObject {

	protected CordysRoot(CordysSystem system, String dn) {
		super(system, dn);
	}

	public NamedObjectList<Organization> getOrg() { return getOrganizations(); }
	public NamedObjectList<Organization> getOrganizations() {
		Element method=new Element("GetOrganizations", nsldap);
		method.addContent(new Element("dn").setText(dn));
		return createObjectsFromEntries(call(method));
	}
	public NamedObjectList<AuthenticatedUser> getAuthenticatedUsers() {
		Element method=new Element("GetAuthenticatedUsers", nsldap);
		method.addContent(new Element("dn").setText(dn));
		method.addContent(new Element("filter").setText("*"));
		return createObjectsFromEntries(call(method));
	}
	
	public NamedObjectList<Isvp> getIsvps() {
		Element method=new Element("GetSoftwarePackages", nsldap);
		method.addContent(new Element("dn").setText(dn));
		return createObjectsFromEntries(call(method));
	}
}
