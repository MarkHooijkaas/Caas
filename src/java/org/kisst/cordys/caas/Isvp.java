package org.kisst.cordys.caas;

import java.util.List;

import org.jdom.Element;

public class Isvp extends CordysObject {

	public Isvp(CordysSystem system, String dn) {
		super(system, dn);
	}

	public List<MethodSet> getMethodSets() {	
		Element method=new Element("GetMethodSets", nsldap10);
		method.addContent(new Element("dn").setText(dn));
		method.addContent(new Element("labeleduri").setText("*"));
		return createObjects(call(method), MethodSet.class);
	}
}
