package org.kisst.cordys.caas;

import org.jdom.Element;

public class User extends CordysObject {

	protected User(CordysObject parent, String dn) {
		super(parent, dn);
	}
	// does not work in C2
	public NamedObjectList<Role> getRoles() {
		Element method=new Element("GetRoles", nsldap10);
		method.addContent(new Element("dn").setText(dn));
		return createObjects(call(method));
	}
	
	
}
