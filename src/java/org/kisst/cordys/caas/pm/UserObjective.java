package org.kisst.cordys.caas.pm;

import org.kisst.cordys.caas.Organization;
import org.kisst.cordys.caas.support.EntryObjectList;
import org.kisst.cordys.caas.util.XmlNode;

public class UserObjective extends ObjectiveBase {
	public static class Roles extends Target {
		public Roles(XmlNode node) { super(node); }
		@Override boolean exists(Organization org) { return org.users.getByName(name)!=null; }
		@Override EntryObjectList<?> getList(Organization org) { return org.users.getByName(name).roles; }
		@Override public String getVarName(Organization org) { return org.getVarName()+".user."+name; }

	}

	public UserObjective(XmlNode node) {
		super("roles", new Roles(node), node);
	}
}
