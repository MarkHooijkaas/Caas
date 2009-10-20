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

import org.kisst.cordys.caas.util.XmlNode;


public class SoapNode extends CordysLdapObject {
	public final ChildList<SoapProcessor> soapProcessors= new ChildList<SoapProcessor>(this, SoapProcessor.class);
	public final ChildList<SoapProcessor> sp = soapProcessors;

	protected SoapNode(LdapObject parent, String dn) {
		super(parent, dn);
	}

	public List<String> getNamespaces() {
		ArrayList<String> result=new ArrayList<String>();
		XmlNode ms=getEntry().getChild("labeleduri");
		for (XmlNode child: ms.getChildren("string")) 
			result.add(child.getText());
		return result;
	}

	public LdapObjectList<MethodSet> getMs() { return getMethodSets(); }
	public LdapObjectList<MethodSet> getMethodSets() {
		return new EntryObjectList<MethodSet>(this, "busmethodsets");
	}
	
	public void addMethodSet(MethodSet m) { 
		addLdapString("busmethodsets", m.dn); 
		clearCache();
	}
	public void removeMethodSet(MethodSet m) { 
		removeLdapString("busmethodsets", m.dn);
		clearCache();
	}
	
	public void diff(LdapObject other, int depth) {
		if (this==other)
			return;
		SoapNode otherSn = (SoapNode) other;
		soapProcessors.diff(otherSn.soapProcessors, depth);
		getMethodSets().diff(otherSn.getMethodSets(), depth);
		// TODO: diff namespaces
	}
}
