package org.kisst.cordys.caas;

import java.util.List;

import org.jdom.Element;

public class User extends CordysObject {

	public User(CordysSystem system, String dn) {
		super(system, dn);
	}
	public List<Role> getRoles() {
		Element method=new Element("GetRoles", nsldap);
		method.addContent(new Element("dn").setText(dn));
		return createObjects(call(method), Role.class);
	}
	
}
