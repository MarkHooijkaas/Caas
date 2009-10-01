package org.kisst.cordys.caas;

import java.util.List;

import org.jdom.Element;

public class CordysContainer extends CordysObject {

	public CordysContainer(CordysSystem system, String dn) {
		super(system, dn);
	}

	public List<User> getUsers() {	
		Element method=new Element("GetOrganizationalUsers", nsldap10);
		method.addContent(new Element("dn").setText(dn));
		return createObjects(call(method), User.class);
	}


	public List<MethodSet> getMethodSets() {	
		Element method=new Element("GetMethodSets", nsldap10);
		method.addContent(new Element("dn").setText(dn));
		method.addContent(new Element("labeleduri").setText("*"));
		return createObjects(call(method), MethodSet.class);
	}
}
