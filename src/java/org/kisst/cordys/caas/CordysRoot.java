package org.kisst.cordys.caas;

import org.jdom.Element;


public class CordysRoot  extends LdapObject {

	protected CordysRoot(CordysSystem system, String dn) {
		super(system, dn);
	}

	public NamedObjectList<Organization> getOrg() { return getOrganizations(); }
	public NamedObjectList<Organization> getOrganizations() {
		Element method=new Element("GetOrganizations", nsldap);
		method.addContent(new Element("dn").setText(dn));
		return createObjects(call(method));
	}
	public NamedObjectList<AuthenticatedUser> getAuthenticatedUsers() {
		Element method=new Element("GetAuthenticatedUsers", nsldap);
		method.addContent(new Element("dn").setText(dn));
		method.addContent(new Element("filter").setText("*"));
		return createObjects(call(method));
	}
	
	public NamedObjectList<Isvp> getIsvps() {
		Element method=new Element("GetSoftwarePackages", nsldap);
		method.addContent(new Element("dn").setText(dn));
		return createObjects(call(method));
	}
}
