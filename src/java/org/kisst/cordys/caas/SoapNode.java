/**
Copyright 2008, 2009 Mark Hooijkaas

This file is part of the Caas tool.

The Caas tool is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

The Caas tool is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with the Caas tool.  If not, see <http://www.gnu.org/licenses/>.
*/

package org.kisst.cordys.caas;

import java.util.ArrayList;
import java.util.List;

import org.jdom.Element;


public class SoapNode extends CordysLdapObject {

	protected SoapNode(LdapObject parent, String dn) {
		super(parent, dn);
	}
	
	public NamedObjectList<SoapProcessor> getSp() { return getSoapProcessors(); }
	public NamedObjectList<SoapProcessor> getSoapProcessors() {
		Element method=new Element("GetChildren", CordysSystem.nsldap);
		method.addContent(new Element("dn").setText(dn));
		return getObjectsFromEntries(soapCall(method));
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
		return getObjectsFromStrings(getEntry(),"busmethodsets");
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
