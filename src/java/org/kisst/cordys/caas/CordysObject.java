package org.kisst.cordys.caas;

import org.jdom.Element;

public class CordysObject {
	private final CordysSystem system;

	protected CordysObject(CordysSystem system) {
		this.system=system;
	}
	public CordysSystem getSystem() { return system; }

	public String call(String input) { return getSystem().call(input); }
	public Element call(Element method) { return getSystem().call(method); }


}
