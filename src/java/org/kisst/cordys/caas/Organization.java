package org.kisst.cordys.caas;

import org.jdom.Element;

public class Organization extends CordysContainer {

	protected Organization(LdapObject parent, String dn) {
		super(parent, dn);
	}

	public NamedObjectList<Role> getRoles() {	
		Element method=new Element("GetRolesForOrganization", nsldap);
		method.addContent(new Element("dn").setText(dn));
		return createObjects(call(method));
	}

	public NamedObjectList<SoapNode> getSn() { return getSoapNodes(); }	
	public NamedObjectList<SoapNode> getSoapNodes() {	
		Element method=new Element("GetSoapNodes", nsldap);
		method.addContent(new Element("dn").setText(dn));
		method.addContent(new Element("namespace").setText("*"));
		return createObjects(call(method));
	}
}
