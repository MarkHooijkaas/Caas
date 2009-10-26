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

import org.kisst.cordys.caas.support.LdapObjectBase;
import org.kisst.cordys.caas.support.LdapObject;

public class Method extends LdapObjectBase {
	public final XmlProperty implementation = new XmlProperty("busmethodimplementation");
	public final XmlProperty impl           = implementation;
	public final XmlProperty signature      = new XmlProperty("busmethodsignature");
	public final XmlProperty sig            = signature;
	public final XmlProperty wsdl           = new XmlProperty("busmethodwsdl");
	public final XmlProperty iface          = new XmlProperty("busmethodinterface");
	
	protected Method(LdapObject parent, String dn) {
		super(parent, dn);
	}
	@Override protected String prefix() { return "methods"; }
}
