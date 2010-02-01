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

import org.kisst.cordys.caas.support.ChildList;
import org.kisst.cordys.caas.support.LdapObject;
import org.kisst.cordys.caas.support.LdapObjectBase;
import org.kisst.cordys.caas.util.XmlNode;

public class Isvp extends LdapObjectBase {
	public final ChildList<User> users= new ChildList<User>(this, User.class);
	public final ChildList<User> user = users;
	public final ChildList<User> u    = users;

	public final ChildList<Role> roles= new ChildList<Role>(this, Role.class);
	public final ChildList<Role> role= roles;
	public final ChildList<Role> r   = roles;

	public final ChildList<MethodSet> methodSets= new ChildList<MethodSet>(this, MethodSet.class);
	public final ChildList<MethodSet> ms = methodSets;

	public final StringProperty filename = new StringProperty("member", 3);
	public final StringProperty owner = new StringProperty("owner", 3);

	private XmlNode definition=null;


	
	protected Isvp(LdapObject parent, String dn) {
		super(parent, dn);
	}
	@Override protected String prefix() { return "isvp"; }
	@Override public void myclear() { super.myclear(); definition=null; }

	@Override protected void preDeleteHook() {
		throw new RuntimeException("It is not allowed to delete an Isvp, please use unload instead");
	}

	public String getBasename() {
		String result=filename.get();
		if (result.endsWith(".isvp"))
			result=result.substring(0,result.length()-5);
		return result;
	}
		
	public void unload(boolean deletereferences) {
		for (Machine m: getSystem().machines)
			// TODO: check if machine has the ISVP loaded
			m.unloadIsvp(this, deletereferences);
		getSystem().removeLdap(getDn());
		getSystem().isvp.clear();
	}
	
	public XmlNode getDefinition() {
		if (definition!=null)
			return definition;
		XmlNode method = new XmlNode("GetISVPackageDefinition", xmlns_isv);
		XmlNode file=method.add("file");
		file.setText(getBasename());
		file.setAttribute("type", "isvpackage");
		file.setAttribute("onlyxml", "true");
		definition=call(method).getChild("ISVPackage").detach();
		return definition;
	}
	
	public XmlNode getDescription() { return getDefinition().getChild("description"); }
	public XmlNode getContent() { return getDefinition().getChild("content"); }
	public String getOwner2() { return getDescription().getChildText("owner"); }
	public String getName2() { return getDescription().getChildText("name"); }
	public String getVersion() { return getDescription().getChildText("version"); }
	public String getWcpversion() { return getDescription().getChildText("wcpversion"); }
	public String getEula() { return getDescription().getChildText("eula"); }
	public String getSidebar() { return getDescription().getChildText("sidebar"); }
			
}
