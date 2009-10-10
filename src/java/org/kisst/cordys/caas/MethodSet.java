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

import java.util.List;

import org.kisst.cordys.caas.util.XmlNode;

public class MethodSet extends CordysLdapObject {

	protected MethodSet(LdapObject parent, String dn) {
		super(parent, dn);
	}
	
	public NamedObjectList<Method> getMethod() {
		return new NamedObjectList<Method>(getMethods()); 
	}
	public List<Method> getMethods() {
		XmlNode method=new XmlNode("GetChildren", xmlns_ldap);
		method.add("dn").setText(dn);
		return getObjectsFromEntries(soapCall(method));
	}

}
