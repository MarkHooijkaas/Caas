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

package org.kisst.cordys.caas.support;

import org.kisst.cordys.caas.Connector;
import org.kisst.cordys.caas.util.XmlNode;

public class XmlObjectList<T extends CordysXmlObject> extends CordysObjectList<T>  {
	private final String path;
	private final Class<? extends LdapObject> clz;

	public XmlObjectList(CordysObject parent, String path, Class<? extends LdapObject> clz) {
		super(parent.getSystem());
		this.path=path;
		this.clz=clz;
	}

	@SuppressWarnings("unchecked")
	@Override protected void retrieveList() {
		XmlNode method = new XmlNode("GetCollection", xmlns_xmlstore);
		method.add("folder").setText(path); // TODO: attribute version=isv, organization, user 
		XmlNode response=system.call(method);
		//if (response.getName().equals("Envelope"))
		//	response=response.getChild("Body").getChildren().get(0);
		for (XmlNode tuple : response.getChildren("tuple")) {
			Connector obj=new Connector(getSystem(), tuple.getAttribute("key"));
			if (clz==null || obj.getClass()== (Class) clz)
				this.grow((T) obj);
			//System.out.println(dn);
		}
	}
	@Override public String getKey() { return "xmlstore:"+getSystem().getDn()+":children:"+path; }
}
