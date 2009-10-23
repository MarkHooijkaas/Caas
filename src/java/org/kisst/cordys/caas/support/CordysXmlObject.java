package org.kisst.cordys.caas.support;

import org.kisst.cordys.caas.CordysSystem;
import org.kisst.cordys.caas.util.XmlNode;

public class CordysXmlObject extends CordysObject {
	private final CordysSystem system;
	private final CordysObject parent; 
	private final String key;
	private XmlNode data=null;

	protected CordysXmlObject(CordysObject parent, String key) {
		this.system=parent.getSystem();
		this.parent=parent;
		this.key=key;
	}
	@Override public String getKey() { return key; }
	@Override public CordysSystem getSystem() { return system; }
	@Override public void refresh() { data=null;}
	public XmlNode getData() {
		if (data==null)
			data=system.getXml(key).getChildren().get(0);
		return data;
	}
	public String toString() {
		String c=this.getClass().getSimpleName()+"("+getName()+")";
		if (parent!=null && (parent instanceof CordysLdapObject))
			c=parent.toString()+"."+c;
		return c; 
	}
}
