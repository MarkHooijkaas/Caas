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

public class Isvp extends CordysLdapObject {
	public final ChildList<User> users= new ChildList<User>(this, User.class);
	public final ChildList<User> user = users;

	public final ChildList<Role> roles= new ChildList<Role>(this, Role.class);
	public final ChildList<Role> role= roles;

	public final ChildList<MethodSet> methodSets= new ChildList<MethodSet>(this, MethodSet.class);
	public final ChildList<MethodSet> ms = methodSets;


	
	protected Isvp(LdapObject parent, String dn) {
		super(parent, dn);
	}

	public void unload(boolean deletereferences) {
		String filename=getFilename();
		if (filename.endsWith(".isvp"))
			filename=filename.substring(0,filename.length()-5);

		XmlNode method=new XmlNode("UnloadISVPackage", xmlns_isv);
		XmlNode file=method.add("file");
		file.setText(filename);
		if (deletereferences)
			file.setAttribute("deletereference", "true");
		else
			file.setAttribute("deletereference", "false");
		call(method);
		getSystem().remove(dn);
	}
	
	public String getFilename() {
		String result=getEntry().getChildText("member/string");
		if (result.startsWith("cn="))
			return result.substring(3);
		else
			return result;
	}
	public void diff(LdapObject other, int depth) {
		if (this==other)
			return;
		Isvp otherIsvp=(Isvp) other;
		methodSets.diff(otherIsvp.methodSets, depth);
		roles.diff(otherIsvp.roles,depth);
	}
}
