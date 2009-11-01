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
import org.kisst.cordys.caas.support.LdapObject;

public class Objective {
	public Objective(Target target, LdapObject entry) {
		this.target=target;
		this.entry=entry;
	}
	public final Target target;
	public final LdapObject entry;
	
	public boolean isSatisfied(Organization org) { return target.links(org, entry); }
	public void satisfy(Organization org) { target.link(org, entry); }
	public void remove(Organization org) { target.unlink(org, entry); }
}