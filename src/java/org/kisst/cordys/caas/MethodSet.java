package org.kisst.cordys.caas;

import java.util.List;

import org.jdom.Element;

public class MethodSet extends CordysObject {

	public MethodSet(CordysObject parent, String dn) {
		super(parent, dn);
	}
	
	public List<Method> getMethods() {
		Element method=new Element("GetChildren", nsldap10);
		method.addContent(new Element("dn").setText(dn));
		return createObjects(call(method), Method.class);
	}

}
