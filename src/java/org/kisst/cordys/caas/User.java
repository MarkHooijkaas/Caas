package org.kisst.cordys.caas;

import java.util.List;

public class User extends CordysObject {

	public User(CordysSystem system, String dn) {
		super(system, dn);
	}
	public List<Role> getRoles() {	return getChildren(system, "GetRoles", Role.class, "role id=\""); }
	
}
