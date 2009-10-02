package org.kisst.cordys.caas;

import org.jdom.Element;

public class CordysContainer extends CordysObject {

	protected CordysContainer(CordysObject parent, String dn) {
		super(parent, dn);
	}

	public NamedObjectList<User> getUsers() {	
		Element method=new Element("GetOrganizationalUsers", nsldap10);
		method.addContent(new Element("dn").setText(dn));
		return createObjects(call(method));
	}


	public NamedObjectList<MethodSet> getMethodSets() {	
		Element method=new Element("GetMethodSets", nsldap10);
		method.addContent(new Element("dn").setText(dn));
		method.addContent(new Element("labeleduri").setText("*"));
		return createObjects(call(method));
	}
}
