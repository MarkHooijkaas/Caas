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

import org.kisst.cordys.caas.util.XmlNode;

public class XmlObjectList<T extends CordysXmlObject> extends CordysObjectList<T>  {
	private final String key;
	private final Class<? extends CordysLdapObject> clz;

	protected XmlObjectList(CordysObject parent, String key, Class<? extends CordysLdapObject> clz) {
		super(parent.getSystem());
		this.key=key;
		this.clz=clz;
	}

	@SuppressWarnings("unchecked")
	protected void retrieveList() {
		XmlNode method = new XmlNode("GetCollection", xmlns_xmlstore);
		method.add("folder").setText(key); // TODO: attribute version=isv, organization, user 
		XmlNode response=system.call(method);
		//if (response.getName().equals("Envelope"))
		//	response=response.getChild("Body").getChildren().get(0);
		for (XmlNode tuple : response.getChildren("tuple")) {
			Connector obj=new Connector(getSystem(), tuple.getAttribute("key"));
			if (clz==null || obj.getClass()==clz)
				this.grow((T) obj);
			//System.out.println(dn);
		}
	}
}
