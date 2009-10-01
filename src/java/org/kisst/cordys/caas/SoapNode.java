package org.kisst.cordys.caas;

import java.util.List;

import org.jdom.Element;


public class SoapNode extends CordysObject {

	public SoapNode(CordysObject parent, String dn) {
		super(parent, dn);
	}
	
	public List<SoapProcessor> getSoapProcessors() {
		Element method=new Element("GetChildren", nsldap10);
		method.addContent(new Element("dn").setText(dn));
		return createObjects(call(method), SoapProcessor.class);
	}
}
