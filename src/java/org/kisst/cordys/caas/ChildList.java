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

public class ChildList<T extends LdapObject> extends LdapObjectList<T>  {
	private static final long serialVersionUID = 1L;
	private final LdapObject parent;
	private final String prefix;
	private final Class<? extends LdapObject> clz;

	protected ChildList(LdapObject parent, String prefix, Class<? extends LdapObject> clz) {
		super(parent.getSystem());
		this.parent=parent;
		this.prefix=prefix;
		this.clz=clz;
		// We have to delay the use of the dn, because the dn is not known in CordysSystem
		// at construction time
	}
	
	@SuppressWarnings("unchecked")
	protected void retrieveList() {
		XmlNode method = new XmlNode("GetChildren", CordysLdapObject.xmlns_ldap);
		method.add("dn").setText(prefix+parent.getDn());
		XmlNode response=system.call(method);
		if (response.getName().equals("Envelope"))
			response=response.getChild("Body").getChildren().get(0);
		for (XmlNode tuple : response.getChildren("tuple")) {
			XmlNode elm=tuple.getChild("old/entry");
			LdapObject obj=system.getObject(elm);
			if (clz==null || obj.getClass()==clz)
				this.add((T) obj);
			//System.out.println(dn);
		}
	}
}
