package org.kisst.cordys.caas.support;

import org.kisst.cordys.caas.CordysSystem;
import org.kisst.cordys.caas.util.XmlNode;

public class CordysXmlObject extends CordysObject {
	private final CordysSystem system;
	private final CordysObject parent; 
	private final String path;
	private XmlNode data=null;

	protected CordysXmlObject(CordysObject parent, String key) {
		this.system=parent.getSystem();
		this.parent=parent;
		this.path=key;
	}
	@Override public String getKey() { return "xmlstore:"+getSystem().getDn()+":"+path; }
	@Override public CordysSystem getSystem() { return system; }
	public String getPath() { return path; }
	public XmlNode getData() {
		if (data==null)
			data=system.getXml(path).getChildren().get(0);
		return data;
	}
	public String toString() {
		String c=this.getClass().getSimpleName()+"("+getName()+")";
		if (parent!=null && (parent instanceof LdapObject))
			c=parent.toString()+"."+c;
		return c; 
	}
}
