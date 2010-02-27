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

public class ChildList<T extends LdapObject> extends CordysObjectList<T>  {
	private static final long serialVersionUID = 1L;
	private final LdapObject parent;
	private final String prefix;
	private final Class<? extends LdapObject> clz;

	public ChildList(LdapObject parent, Class<? extends LdapObject> clz) {
		this(parent,"",clz);
	}
	public ChildList(LdapObject parent, String prefix, Class<? extends LdapObject> clz) {
		super(parent.getSystem());
		this.parent=parent;
		this.prefix=prefix;
		this.clz=clz;
		// We have to delay the use of the dn, because the dn is not known in CordysSystem
		// at construction time
	}

	@Override public String getKey() { return parent.getKey()+":"+clz.getSimpleName()+"s"; }
	@Override public String getName() {
		String name=clz.getSimpleName();
		return name.substring(0,1).toLowerCase()+name.substring(1)+"s"; 
	}
	@Override public String getVarName() { return parent.getVarName()+"."+getName();}

	@SuppressWarnings("unchecked")
	@Override protected void retrieveList() {
		XmlNode method = new XmlNode("GetChildren", xmlns_ldap);
		//method.add("dn").setText(prefix+((CordysLdapObject) parent).getDn());
		String dn;
		if (parent instanceof CordysSystem)
			dn=((CordysSystem) parent).getDn();
		else 
			dn=parent.getDn();
		method.add("dn").setText(prefix+dn); 
		XmlNode response=system.call(method);
		if (response.getName().equals("Envelope"))
			response=response.getChild("Body").getChildren().get(0);
		for (XmlNode tuple : response.getChildren("tuple")) {
			XmlNode elm=tuple.getChild("old/entry");
			CordysObject obj=system.getLdap(elm);
			if (obj==null)
				continue;
			if (clz==null || obj.getClass()==clz)
				this.grow((T) obj);
			//System.out.println(dn);
		}
	}
}
