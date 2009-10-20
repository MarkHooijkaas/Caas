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



public class SoapNode extends CordysLdapObject {
	public final ChildList<SoapProcessor> soapProcessors= new ChildList<SoapProcessor>(this, SoapProcessor.class);
	public final ChildList<SoapProcessor> sp = soapProcessors;

	public final EntryObjectList<MethodSet> methodSets = new EntryObjectList<MethodSet>(this, "busmethodsets");
	public final EntryObjectList<MethodSet> ms = methodSets;
	
	public final StringList namespaces= new StringList("labeleduri"); 
	public final StringList ns = namespaces;
	
	protected SoapNode(LdapObject parent, String dn) {
		super(parent, dn);
	}

	
	public void addMethodSet(MethodSet m) { 
		addLdapString("busmethodsets", m.dn); 
		refresh();
	}
	public void removeMethodSet(MethodSet m) { 
		removeLdapString("busmethodsets", m.dn);
		refresh();
	}
	
	public void diff(LdapObject other, int depth) {
		if (this==other)
			return;
		SoapNode otherSn = (SoapNode) other;
		soapProcessors.diff(otherSn.soapProcessors, depth);
		methodSets.diff(otherSn.methodSets, depth);
		// TODO: diff namespaces
	}
}
