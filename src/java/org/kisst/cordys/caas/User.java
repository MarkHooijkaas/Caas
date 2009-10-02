package org.kisst.cordys.caas;


public class User extends LdapObject {

	protected User(LdapObject parent, String dn) {
		super(parent, dn);
	}

	public NamedObjectList<Role> getRoles() {
		return getSystem().registry.createObjectsFromStrings(getEntry(),"role");
	}
	
	public void addRole(Role r) { addLdapString("role", r.dn); }
	public void removeRole(Role r) { removeLdapString("role", r.dn); }
	
	public AuthenticatedUser getAuthenticatedUser() {
		String dn=getEntry().getChild("authenticationuser",null).getChildText("string",null);
		return (AuthenticatedUser) getSystem().getObject(dn);
	}
}
