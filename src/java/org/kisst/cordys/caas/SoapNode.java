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

import java.util.LinkedHashMap;

import org.kisst.cordys.caas.util.XmlNode;




public class SoapNode extends CordysLdapObject {
	public final ChildList<SoapProcessor> soapProcessors= new ChildList<SoapProcessor>(this, SoapProcessor.class);
	public final ChildList<SoapProcessor> sp = soapProcessors;

	public final EntryObjectList<MethodSet> methodSets = new EntryObjectList<MethodSet>(this, "busmethodsets");
	public final EntryObjectList<MethodSet> ms = methodSets;
	
	public final StringList namespaces= new StringList("labeleduri"); 
	public final StringList ns = namespaces;
	
	public final XmlProperty config = new XmlProperty("bussoapnodeconfiguration");
	
	public final XmlSubProperty ui_algorithm = new XmlSubProperty(config, "routing/@ui_algorithm");  
	public final XmlSubProperty ui_type = new XmlSubProperty(config, "routing/@ui_type");  
	public final XmlSubProperty numprocessors = new XmlSubProperty(config, "routing/numprocessors");  
	public final XmlSubProperty algorithm= new XmlSubProperty(config, "routing/algorithm");  

	
	protected SoapNode(LdapObject parent, String dn) {
		super(parent, dn);
	}
	
	public void recalcNamespaces() {
		LinkedHashMap<String, String> all=new LinkedHashMap<String, String>();
		for (MethodSet ms : methodSets) {
			if (ms!=null) {
				for (String s : ms.namespaces.get())
					all.put(s,s);
			}
		}
		XmlNode newEntry=getEntry().clone();
		XmlNode msNode=newEntry.getChild("labeleduri");
		for (XmlNode child: msNode.getChildren())
			msNode.remove(child);
		for (String s: all.keySet())
			msNode.add("string").setText(s);
		updateLdap(newEntry);
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
