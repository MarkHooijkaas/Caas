package org.kisst.cordys.caas;

import java.util.List;

public class Organization extends CordysObject {
	public Organization(CordysSystem system, String dn) {
		super(system, dn);
	}

	public List<User> getUsers() {
		return getChildren(system, "GetOrganizationalUsers", User.class);
	}
}
