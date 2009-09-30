package org.kisst.cordys.caas;


public class CordysObject extends LdapObject {
	protected final CordysSystem system;
	protected CordysObject(CordysSystem system, String dn) {
		super(dn);
		this.system=system;
	}
	public CordysSystem getSystem() { return system; }
}
