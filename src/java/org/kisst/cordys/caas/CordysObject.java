package org.kisst.cordys.caas;

import org.jdom.Element;
import org.jdom.Namespace;
import org.kisst.cordys.caas.util.JdomUtil;


public class CordysObject {
	public final static Namespace nsldap10=Namespace.getNamespace("http://schemas.cordys.com/1.0/ldap");

	private final CordysObject parent; 
	private final CordysSystem system;
	protected final String dn;
	protected Element entry;

	protected CordysObject(CordysObject parent, String dn) {
		this.parent=parent;
		this.dn=dn;
		if (parent==null)
			this.system=(CordysSystem) this; // workaround to be used by constructor of CordysSytem
		else
			this.system=parent.getSystem();
	}
	public CordysObject getParent() { return parent; }
	public CordysSystem getSystem() { return system; }
	//public final Namespace nsldap11=Namespace.getNamespace("http://schemas.cordys.com/1.1/ldap");
	
	public String getDn() { return dn; }
	public String getName() {
		int pos=dn.indexOf("=");
		int pos2=dn.indexOf(",",pos);
		return dn.substring(pos+1,pos2);
	}
	public String toString() {
		String c=this.getClass().getSimpleName()+"("+getName()+")";
		if (parent!=null && ! (parent instanceof CordysSystem))
			c="("+parent.toString()+","+c+")";
		return c; 
	}

	public String call(String input) { return getSystem().call(input); }
	public Element call(Element method) { 
		String xml = JdomUtil.toString(method);
		String response= getSystem().call(xml);
		Element output=JdomUtil.fromString(response);
		if (output.getName().equals("Envelope"))
			output=output.getChild("Body",null).getChild(null,null);
		return output;
	}


	public NamedObjectList<CordysObject> getChildren() {
		Element method=new Element("GetChildren", nsldap10);
		method.addContent(new Element("dn").setText(dn));
		return createObjects(call(method));
	}
	
	protected <T extends CordysObject> NamedObjectList<T> createObjects(Element element) {
		return getSystem().registry.createObjects(element);
	}
	@SuppressWarnings("unchecked")
	public <T extends CordysObject> NamedObjectList<T> createChildren(Element response) {
		NamedObjectList<T> result=new NamedObjectList<T>();

		if (response.getName().equals("Envelope"))
			response=response.getChild("Body",null).getChild(null,null);
		for (Object tuple : response.getChildren("tuple", null)) {
			Element elm=((Element) tuple).getChild("old", null).getChild("entry", null);
			CordysObject obj=system.getObject(elm);
			result.put(obj.getName(),(T) obj);
			//System.out.println(dn);
		}
		return result;
	}
	
	public void refresh() {
		Element method=new Element("GetLDAPObject", nsldap10);
		method.addContent(new Element("dn").setText(dn));
		Element response = system.call(method);
		entry=response.getChild("tuple",null).getChild("old",null).getChild("entry",null);
	}
	public Element getEntry() {
		if (entry==null)
			refresh();
		return entry;
	}
}
