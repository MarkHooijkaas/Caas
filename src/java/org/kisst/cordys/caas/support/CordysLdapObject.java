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



public abstract class CordysLdapObject extends LdapObject {
	private final CordysSystem system;
	private final String dn;

	protected CordysLdapObject(CordysObject parent, String dn) {
		super(parent);
		this.system=parent.getSystem();
		this.dn=dn;
	}

	public CordysSystem getSystem() { return system; }
	public String getDn() { return dn; }
}
