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
import org.kisst.cordys.caas.main.Environment;
import org.kisst.cordys.caas.pm.CaasPackage.GhostObject;
import org.kisst.cordys.caas.support.LdapObject;

public class Objective {
	public Objective(Target target, LdapObject entry) {
		this.target=target;
		this.entry=entry;
	}
	public final Target target;
	public final LdapObject entry;
	
	public boolean isSatisfied(Organization org) {
		boolean result=target.links(org, entry);
		Environment env=Environment.get();
		if (! target.exists(org))
			env.error("unknown target "+target+" should have entry "+entry.getVarName());
		else if (entry instanceof GhostObject)
			env.error("target "+target+" should have unknown entry "+entry.getVarName());
		else if (!result)
			env.error("target "+target+" should have entry "+entry.getVarName());
		return result;
	}
	public void satisfy(Organization org) { 
		Environment env=Environment.get();
		if (target==null)
			env.warn("unknown target "+target+" should have entry "+entry.getVarName());
		else if (entry instanceof GhostObject)
			env.warn("target "+target+" should have unknown entry "+entry.getVarName());
		else if (target.links(org, entry))
			env.warn("target "+target+" already has entry "+entry.getVarName());
		else
			target.link(org, entry); 
	}
	public void remove(Organization org) { target.unlink(org, entry); }
}