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
	public final static String xmlns_isv="http://schemas.cordys.com/1.0/isvpackage";

	protected Isvp(LdapObject parent, String dn) {
		super(parent, dn);
	}

	public LdapObjectList<MethodSet> getMs() { 
		return getMethodSets(); 
	}
	public LdapObjectList<MethodSet> getMethodSets() {	
		XmlNode method=new XmlNode("GetMethodSets", xmlns_ldap);
		method.add("dn").setText(dn);
		method.add("labeleduri").setText("*");
		return new LdapObjectList<MethodSet>(system, method);
	}
	
	
	public LdapObjectList<Role> getRole() {
		return getRoles();
	}
	public LdapObjectList<Role> getRoles() {	
		XmlNode method=new XmlNode("GetRolesForSoftwarePackage", xmlns_ldap);
		method.add("dn").setText(dn);
		return new LdapObjectList<Role>(system, method);
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
		soapCall(method);
		getSystem().remove(dn);
	}
	
	public String getFilename() {
		String result=getEntry().getChildText("member/string");
		if (result.startsWith("cn="))
			return result.substring(3);
		else
			return result;
	}
}
