package org.kisst.cordys.caas;

import java.util.List;

import org.jdom.Element;
import org.jdom.Namespace;


public class LdapObject extends CordysObject {
	public final static Namespace nsldap=Namespace.getNamespace("http://schemas.cordys.com/1.0/ldap");

	private final LdapObject parent; 
	protected final String dn;
	private Element entry;

	protected LdapObject(LdapObject parent, String dn) {
		super(parent);
		this.parent=parent;
		this.dn=dn;
	}
	public LdapObject getParent() { return parent; }
	
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

	public NamedObjectList<LdapObject> getChildren() {
		Element method=new Element("GetChildren", nsldap);
		method.addContent(new Element("dn").setText(dn));
		return createObjects(call(method));
	}
	
	protected <T extends LdapObject> NamedObjectList<T> createObjects(Element element) {
		return getSystem().registry.createObjectsFromEntries(element);
	}
	
	public void refresh() {
		Element method=new Element("GetLDAPObject", nsldap);
		method.addContent(new Element("dn").setText(dn));
		Element response = call(method);
		setEntry(response.getChild("tuple",null).getChild("old",null).getChild("entry",null));
	}
	public void setEntry(Element entry) {
		this.entry=entry;
		entry.detach();
	}
	public Element getEntry() {
		if (entry==null)
			refresh();
		return entry;
	}
	protected void addLdapString(String group, String value) {
		getEntry();
		Element newEntry=(Element) entry.clone();
		newEntry.getChild(group, null).addContent(new Element("string",nsldap).setText(value));
		updateLdap(newEntry);
	}
	protected void removeLdapString(String group, String value) {
		getEntry();
		Element newEntry=(Element) entry.clone();
		List<?> children=newEntry.getChild(group, null).getChildren(); 
		Element toRemove=null;
		for(Object o: children) {
			Element e= (Element) o;
			if (e.getText().equals(value))
				toRemove=e;
		}
		if (toRemove!=null)
			children.remove(toRemove);
		updateLdap(newEntry);
	}

	protected void updateLdap(Element newEntry) {
		Element tuple=new Element("tuple", nsldap);
		tuple.addContent(new Element("old", nsldap).addContent(entry));
		tuple.addContent(new Element("new", nsldap).addContent(newEntry));
		Element method=new Element("Update", nsldap).addContent(tuple);
		call(method);
		setEntry(newEntry);
	}
	

}
