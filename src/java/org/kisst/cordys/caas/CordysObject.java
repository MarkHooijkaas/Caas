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

public class CordysObject {
	private final CordysSystem system;

	protected CordysObject(CordysSystem system) {
		this.system=system;
	}
	public CordysSystem getSystem() { return system; }

	public Element soapCall(Element method) { return getSystem().soapCall(method); }

	@SuppressWarnings("unchecked")
	protected <T extends LdapObject> NamedObjectList<T> getObjectsFromEntries(Element response) {
		NamedObjectList<T> result=new NamedObjectList<T>();
		if (response.getName().equals("Envelope"))
			response=response.getChild("Body",null).getChild(null,null);
		for (Object tuple : response.getChildren("tuple", null)) {
			Element elm=((Element) tuple).getChild("old", null).getChild("entry", null);
			LdapObject obj=system.getObject(elm);
			result.put(obj.getName(),(T) obj);
			//System.out.println(dn);
		}
		return result;
	}
	@SuppressWarnings("unchecked")
	protected <T extends LdapObject> NamedObjectList<T> getObjectsFromStrings(Element start, String group) {
		NamedObjectList<T> result=new NamedObjectList<T>();
		start=start.getChild(group,null);
		for (Object s: start.getChildren("string", null)) {
			String dn=((Element) s).getText();
			LdapObject obj=system.getObject(dn);
			result.put(obj.getName(),(T) obj);
			//System.out.println(dn);
		}
		return result;
	}

}
