package org.kisst.cordys.caas;

import java.util.ArrayList;
import java.util.List;

import org.jdom.Element;


public class SoapNode extends CordysObject {

	protected SoapNode(CordysObject parent, String dn) {
		super(parent, dn);
	}
	
	public NamedObjectList<SoapProcessor> getSp() { return getSoapProcessors(); }
	public NamedObjectList<SoapProcessor> getSoapProcessors() {
		Element method=new Element("GetChildren", nsldap);
		method.addContent(new Element("dn").setText(dn));
		return createObjects(call(method));
	}
	
	public List<String> getNamespaces() {
		ArrayList<String> result=new ArrayList<String>();
		Element ms=getEntry().getChild("labeleduri", null);
		for (Object o: ms.getChildren("string", null)) 
			result.add(((Element)o).getText());
		return result;
	}

	public NamedObjectList<MethodSet> getMs() { return getMethodSets(); }
	public NamedObjectList<MethodSet> getMethodSetsOld() {
		NamedObjectList<MethodSet> result=new NamedObjectList<MethodSet>();
		Element ms=getEntry().getChild("busmethodsets", null);
		for (Object o: ms.getChildren("string", null)) {
			String dn=((Element)o).getText();
			MethodSet obj=(MethodSet)getSystem().getObject(dn);
			result.put(obj.getName(), obj);
		}
		return result;
	}
	
	public NamedObjectList<MethodSet> getMethodSets() {
		return getSystem().registry.createObjectsFromStrings(getEntry(),"busmethodsets");
	}
	
	public void addMethodSet(MethodSet m) { 
		addLdapString("busmethodsets", m.dn); 
		recalcNamespaces();
	}
	public void removeMethodSet(MethodSet m) { 
		removeLdapString("busmethodsets", m.dn);
		recalcNamespaces();
	}
	public void recalcNamespaces() { 
		// TODO
	}

}
