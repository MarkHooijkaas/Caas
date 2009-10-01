package org.kisst.cordys.caas;

import java.util.List;

import org.jdom.Element;

public class Organization extends CordysObject {
	public Organization(CordysSystem system, String dn) {
		super(system, dn);
	}

	public List<User> getUsers() {	
		Element method=new Element("GetOrganizationalUsers", nsldap);
		method.addContent(new Element("dn").setText(dn));
		return createObjects(call(method), User.class);
	}
	
	public List<SoapNode> getSoapNodes() {	
		Element method=new Element("GetSoapNodes", nsldap);
		method.addContent(new Element("dn").setText(dn));
		return createObjects(call(method), SoapNode.class);
	}
}
