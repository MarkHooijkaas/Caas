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

import org.kisst.cordys.caas.support.EntryObjectList;
import org.kisst.cordys.caas.support.LdapObject;
import org.kisst.cordys.caas.support.LdapObjectBase;



public class User extends LdapObjectBase {
	public final RefProperty<AuthenticatedUser> authenticatedUser = new RefProperty<AuthenticatedUser>("authenticationuser");
	public final RefProperty<AuthenticatedUser> au = authenticatedUser;

	public final EntryObjectList<Role> roles = new EntryObjectList<Role>(this, "role");
	public final EntryObjectList<Role> role = roles;
	public final EntryObjectList<Role> r    = roles;

	public final StringList toolbars= new StringList("toolbar"); 
	public final StringList menus = new StringList("menu"); 

	protected User(LdapObject parent, String dn) {
		super(parent, dn);
	}
	@Override protected String prefix() { return "user"; }
	// It is not yet possible to impersonate another user
	//public XmlNode getXml(String key, String version) { return getSystem().getXml(key, version, getParent().getDn()); }
	//public XmlNode getXml(String key) { return getSystem().getXml(key, "user", getParent().getDn()); }
}
