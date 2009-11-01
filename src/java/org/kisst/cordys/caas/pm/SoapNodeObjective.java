package org.kisst.cordys.caas.pm;

import org.kisst.cordys.caas.Organization;
import org.kisst.cordys.caas.support.EntryObjectList;
import org.kisst.cordys.caas.util.XmlNode;

public class SoapNodeObjective extends ObjectiveBase {
	public static class MethodSets extends Target {
		MethodSets(XmlNode node) { super(node); }
		public String getVarName(Organization org) { return org.getVarName()+".sn."+name; }
		@Override boolean exists(Organization org) { return org.sn.getByName(name)!=null; }
		@Override EntryObjectList<?> getList(Organization org) { return org.sn.getByName(name).ms; }
	}

	public SoapNodeObjective(XmlNode node) {
		super("ms", new MethodSets(node), node);
	}
}
