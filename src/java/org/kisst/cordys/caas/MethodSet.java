package org.kisst.cordys.caas;

import org.jdom.Element;

public class MethodSet extends CordysObject {

	protected MethodSet(CordysObject parent, String dn) {
		super(parent, dn);
	}
	
	public NamedObjectList<Method> getMethods() {
		Element method=new Element("GetChildren", nsldap);
		method.addContent(new Element("dn").setText(dn));
		return createObjects(call(method));
	}

}
