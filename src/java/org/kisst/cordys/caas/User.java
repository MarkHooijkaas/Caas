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

public class User extends CordysLdapObject {
	public final RefProperty<AuthenticatedUser> authenticatedUser = new RefProperty<AuthenticatedUser>("authenticationuser/string");
	
	protected User(LdapObject parent, String dn) {
		super(parent, dn);
	}

	public EntryObjectList<Role> getRole() { return getRoles(); }
	public EntryObjectList<Role> getRoles() {
		return new EntryObjectList<Role>(this, "role");
	}
	
	public void addRole(Role r) { addLdapString("role", r.dn); }
	public void removeRole(Role r) { removeLdapString("role", r.dn); }
	
	public AuthenticatedUser getAuser() { return authenticatedUser.get(); }
	public AuthenticatedUser getAuthenticatedUser() {
		String dn=getEntry().getChildText("authenticationuser/string");
		return (AuthenticatedUser) getSystem().getObject(dn);
	}

	public void diff(LdapObject other, int depth) {
		if (this==other)
			return;
		User otherUser = (User) other;
		String auser1=getAuthenticatedUser().getName();
		String auser2=otherUser.getAuthenticatedUser().getName();
		if (! auser1.equals(auser2)) {
			System.out.println("< "+this+".authenticatedUser="+auser1);
			System.out.println("> "+this+".authenticatedUser="+auser2);
		}
		getRoles().diff(this+" has role: ",otherUser.getRoles(), depth);
	}
}
