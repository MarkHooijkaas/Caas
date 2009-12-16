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

import org.kisst.cordys.caas.CordysSystem;
import org.kisst.cordys.caas.util.XmlNode;

public class CordysXmlObject extends CordysObject {
	private final CordysSystem system;
	private final CordysObject parent; 
	private final String path;
	private XmlNode data=null;

	protected CordysXmlObject(CordysObject parent, String key) {
		this.system=parent.getSystem();
		this.parent=parent;
		this.path=key;
	}
	@Override public String getName() { return null;} // TODO
	@Override public String getVarName() { return parent.getVarName()+".xml("+path+")";}
	@Override public String getKey() { return "xmlstore:"+getSystem().getDn()+":entry:"+path; }
	@Override public CordysSystem getSystem() { return system; }
	public String getPath() { return path; }
	public XmlNode getData() {
		if (data==null)
			data=system.getXml(path).getChildren().get(0);
		return data;
	}
	@Override public String toString() {
		String c=this.getClass().getSimpleName()+"("+getName()+")";
		if (parent!=null && (parent instanceof LdapObject))
			c=parent.toString()+"."+c;
		return c; 
	}
}
