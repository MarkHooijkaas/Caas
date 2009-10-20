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

/**
 * This class works like a list, without being a real java.util.List
 * This hack is necessary, because in groovy the propertyMissing method is never used
 * on objects that inherit from a List.
 * 
 */
public class EntryObjectList<T extends LdapObject> extends LdapObjectList<T>  {
	private final CordysLdapObject parent;
	private final String group;

	protected EntryObjectList(CordysLdapObject parent, String group) {
		super(parent.getSystem());
		this.parent=parent;
		this.group=group;
	}
	
	@SuppressWarnings("unchecked")
	protected void retrieveList() {
		XmlNode method=new XmlNode("GetChildren", xmlns_ldap);
		method.add("dn").setText(parent.getDn());
		XmlNode response=system.call(method);
		if (response.getName().equals("Envelope"))
			response=response.getChild("Body").getChildren().get(0);

		XmlNode start=response.getChild(group);
		for (XmlNode s: start.getChildren("string")) {
			String dn=s.getText();
			LdapObject obj=system.getObject(dn);
			this.add((T) obj);
		}
	}
}
