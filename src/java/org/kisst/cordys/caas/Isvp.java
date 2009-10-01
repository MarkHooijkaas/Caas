package org.kisst.cordys.caas;

import java.util.List;

import org.jdom.Element;

public class Isvp extends CordysObject {

	public Isvp(CordysObject parent, String dn) {
		super(parent, dn);
	}

	public List<MethodSet> getMethodSets() {	
		Element method=new Element("GetMethodSets", nsldap10);
		method.addContent(new Element("dn").setText(dn));
		method.addContent(new Element("labeleduri").setText("*"));
		return createObjects(call(method), MethodSet.class);
	}
	
	public List<Role> getRoles() {	
		Element method=new Element("GetRolesForSoftwarePackage", nsldap10);
		method.addContent(new Element("dn").setText(dn));
		return createObjects(call(method), Role.class);
	}
}
