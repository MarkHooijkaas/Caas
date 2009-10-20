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


public class Organization extends CordysLdapObject {
	public final LdapObjectListProperty<User> users= new LdapObjectListProperty<User>("cn=organizational users,", User.class);
	public final LdapObjectListProperty<User> user = users;

	public final LdapObjectListProperty<Role> roles= new LdapObjectListProperty<Role>("cn=organizational roles,", Role.class);
	public final LdapObjectListProperty<Role> role= roles;

	public final LdapObjectListProperty<MethodSet> methodSets= new LdapObjectListProperty<MethodSet>("cn=method sets,", MethodSet.class);
	public final LdapObjectListProperty<MethodSet> ms = methodSets;

	public final LdapObjectListProperty<SoapNode> soapNodes= new LdapObjectListProperty<SoapNode>("cn=soap nodes,", SoapNode.class);
	public final LdapObjectListProperty<SoapNode> sn = soapNodes;
	
	protected Organization(LdapObject parent, String dn) {
		super(parent, dn);
	}

	public String call(String input) { return getSystem().call(input, dn, null); }

	public LdapObjectList<SoapProcessor> getSp() { 	return getSoapProcessors();	}
	public LdapObjectList<SoapProcessor> getSoapProcessors() {
		LdapObjectList<SoapProcessor> result= new LdapObjectList<SoapProcessor>();
		for (Object sn : soapNodes.get()) {
			for (Object sp : ((SoapNode) sn).soapProcessors.get()) {
				result.add((SoapProcessor) sp); // using dn, to prevent duplicate names over organizations
			}
		}
		return result;
	}
	
	public void diff(LdapObject other, int depth) {
		if (this==other)
			return;
		Organization otherOrg = (Organization) other;
		soapNodes.diff(otherOrg.soapNodes,depth);
		users.diff(otherOrg.users, depth);
		roles.diff(otherOrg.roles, depth);
	}

}
