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

package org.kisst.cordys.caas.support;

import org.kisst.cordys.caas.CordysSystem;
import org.kisst.cordys.caas.util.XmlNode;

public class ChildList<T extends CordysLdapObject> extends CordysObjectList<T>  {
	private static final long serialVersionUID = 1L;
	private final CordysObject parent;
	private final String prefix;
	private final Class<? extends CordysLdapObject> clz;

	public ChildList(CordysObject parent, Class<? extends CordysLdapObject> clz) {
		this(parent,"",clz);
	}
	public ChildList(CordysObject parent, String prefix, Class<? extends CordysLdapObject> clz) {
		super(parent.getSystem());
		this.parent=parent;
		this.prefix=prefix;
		this.clz=clz;
		// We have to delay the use of the dn, because the dn is not known in CordysSystem
		// at construction time
	}
	
	@SuppressWarnings("unchecked")
	protected void retrieveList() {
		XmlNode method = new XmlNode("GetChildren", xmlns_ldap);
		//method.add("dn").setText(prefix+((CordysLdapObject) parent).getDn());
		String dn;
		if (parent instanceof CordysSystem)
			dn=((CordysSystem) parent).getDn();
		else if (parent instanceof CordysLdapObject)
			dn=((CordysLdapObject) parent).getDn();
		else 
			throw new RuntimeException("parent "+parent+"of wrong type");
		method.add("dn").setText(prefix+dn); 
		XmlNode response=system.call(method);
		if (response.getName().equals("Envelope"))
			response=response.getChild("Body").getChildren().get(0);
		for (XmlNode tuple : response.getChildren("tuple")) {
			XmlNode elm=tuple.getChild("old/entry");
			CordysObject obj=system.getObject(elm);
			if (clz==null || obj.getClass()==clz)
				this.grow((T) obj);
			//System.out.println(dn);
		}
	}
}
