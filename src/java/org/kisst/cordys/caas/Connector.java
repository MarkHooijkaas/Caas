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

import org.kisst.cordys.caas.support.CordysObject;
import org.kisst.cordys.caas.support.CordysXmlObject;

public class Connector extends CordysXmlObject {

	public Connector(CordysObject parent, String key) { super(parent, key); }

	@Override public String getName() { return getData().getChildText("step/description"); }
	public String getImplementation() { return getData().getChildText("step/implementation"); }
	@Override protected String prefix() { return "conn"; }

	@Override public String toString() { return getVarName(); }
	@Override public String getVarName() {
		String name=getName();
		if (name.indexOf(" ")>=0 || name.indexOf('.')>=0)
			return getSystem().getVarName()+".conn.\""+getName()+"\"";
		else
			return getSystem().getVarName()+".conn."+getName();
	}
}
