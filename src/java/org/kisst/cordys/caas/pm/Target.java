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

package org.kisst.cordys.caas.pm;

import org.kisst.cordys.caas.Organization;
import org.kisst.cordys.caas.support.EntryObjectList;
import org.kisst.cordys.caas.support.LdapObject;
import org.kisst.cordys.caas.util.XmlNode;

public abstract class Target {
	protected final String name;
	protected Target(XmlNode node) {this.name=node.getAttribute("name"); }
	abstract public String getVarName(Organization org);
	abstract boolean exists(Organization org);
	abstract EntryObjectList<?> getList(Organization org);
	
	@Override public String toString() { return name; }
	
	boolean contains(Organization org, LdapObject part) { return getList(org).contains(part); }
	void         add(Organization org, LdapObject part) { getList(org).add(part); }
	void      remove(Organization org, LdapObject part) { getList(org).remove(part); }
	
}
