package org.kisst.cordys.caas;

import org.kisst.cordys.caas.support.LdapObject;
import org.kisst.cordys.caas.support.LdapObjectBase;

public class OsProcess extends LdapObjectBase {
	public final StringProperty executable = new StringProperty("busosexecutable");
	public final StringList arguments= new StringList("busosprocessargument");
	
	protected OsProcess(LdapObject parent, String dn) {
		super(parent, dn);
	}
	@Override protected String prefix() { return "osprocess"; }
}