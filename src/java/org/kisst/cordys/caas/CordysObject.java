package org.kisst.cordys.caas;

import org.jdom.Element;
import org.kisst.cordys.caas.util.JdomUtil;

public class CordysObject {
	private final CordysSystem system;

	protected CordysObject(LdapObject parent) {
		if (parent==null)
			this.system=(CordysSystem) this; // workaround to be used by constructor of CordysSytem
		else
			this.system=parent.getSystem();
	}
	public CordysSystem getSystem() { return system; }

	public String call(String input) { return getSystem().call(input); }
	public Element call(Element method) { 
		String xml = JdomUtil.toString(method);
		String response= getSystem().call(xml);
		Element output=JdomUtil.fromString(response);
		if (output.getName().equals("Envelope"))
			output=output.getChild("Body",null).getChild(null,null);
		return output;
	}


}
