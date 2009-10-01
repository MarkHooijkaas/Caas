package org.kisst.cordys.caas;

import java.util.List;

import org.jdom.Element;
import org.kisst.cordys.caas.util.DynamicProperty;

public class Organization extends CordysObject {
	public final DynamicProperty<User> user;

	public Organization(CordysSystem system, String dn) {
		super(system, dn);
		user=new DynamicProperty<User>(getSystem(), User.class, "cn=", "cn=organizational users,"+dn);
	}

	public List<User> getUsers() {	
		Element method=new Element("GetOrganizationalUsers", nsldap10);
		method.addContent(new Element("dn").setText(dn));
		return createObjects(call(method), User.class);
	}
	
	public List<SoapNode> getSoapNodes() {	
		Element method=new Element("GetSoapNodes", nsldap10);
		method.addContent(new Element("dn").setText(dn));
		return createObjects(call(method), SoapNode.class);
	}
}
