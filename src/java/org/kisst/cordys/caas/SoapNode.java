package org.kisst.cordys.caas;

import java.util.ArrayList;
import java.util.List;

import org.jdom.Element;


public class SoapNode extends CordysObject {

	protected SoapNode(CordysObject parent, String dn) {
		super(parent, dn);
	}
	
	public NamedObjectList<SoapProcessor> getSoapProcessors() {
		Element method=new Element("GetChildren", nsldap10);
		method.addContent(new Element("dn").setText(dn));
		return createObjects(call(method));
	}
	
	public List<String> getNamespaces() {
		ArrayList<String> result=new ArrayList<String>();
		Element ms=entry.getChild("labeleduri", null);
		for (Object o: ms.getChildren("string", null)) 
			result.add(((Element)o).getText());
		return result;
	}
	public NamedObjectList<MethodSet> getMethodSets() {
		NamedObjectList<MethodSet> result=new NamedObjectList<MethodSet>();
		Element ms=entry.getChild("busmethodsets", null);
		for (Object o: ms.getChildren("string", null)) {
			String dn=((Element)o).getText();
			MethodSet obj=(MethodSet)getSystem().getObject(dn);
			result.put(obj.getName(), obj);
		}
		return result;
	}

}
