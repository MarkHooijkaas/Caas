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

package org.kisst.cordys.caas.support;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.kisst.cordys.caas.CordysSystem;
import org.kisst.cordys.caas.Isvp;
import org.kisst.cordys.caas.util.XmlNode;


public abstract class LdapObject extends CordysObject {
	public abstract class AbstractProperty {
		abstract public Object get();
		@Override public String toString() { return ""+get(); }
	}
	public class StringProperty extends AbstractProperty {
		private final String path;
		private final int startPos;
		public StringProperty(String path) { this(path,0);}
		public StringProperty(String path, int startPos) {
			this.startPos=startPos;
			this.path=path+"/string";
		}
		@Override public String get() { 
			String s= getEntry().getChildText(path);
			if (s!=null && startPos>0)
				return s.substring(startPos);
			return s;
		}
		public void set(String value) {
			checkIfMayBeModified(); 
			XmlNode newEntry=getEntry().clone();
			newEntry.getChild(path).setText(value);
			updateLdap(newEntry);
		}
	}
	public class XmlProperty extends StringProperty {
		public XmlProperty(String path) {super(path);}
		public XmlNode getXml() { return new XmlNode(get()); }
		public void set(XmlNode value) { set(value.toString()); }
	}
	public class XmlSubProperty extends AbstractProperty {
		private final XmlProperty xml;
		private final String path;
		public XmlSubProperty(XmlProperty xml, String path) {
			this.xml=xml;
			this.path=path;
		}
		@Override public String get() {return xml.getXml().getChildText(path); }
		public void set(String value) {
			XmlNode newnode=xml.getXml().clone();
			newnode.setChildText(path, value);
			xml.set(newnode);
		}
	}
	public class XmlBoolProperty extends XmlSubProperty {
		private final boolean defaultValue;
		public XmlBoolProperty(XmlProperty xml, String path, boolean defaultValue) { 
			super(xml, path);
			this.defaultValue=defaultValue;
		}
		public boolean getBool() {
			String value=get();
			if (value==null)
				return defaultValue;
			return "true".equals(value);			
		}
	}

	public class BooleanProperty extends StringProperty {
		public BooleanProperty(String path) {super(path);}
		public Boolean getBool() { return "true".equals(get()); }
		public void set(boolean value) { set(""+value);}
	}
	public class RefProperty<T extends LdapObject> extends StringProperty {
		public RefProperty(String path) {super(path);}
		@SuppressWarnings("unchecked")
		public T getRef() { return (T) getSystem().getLdap(get()); }
		public void set(T value) { set(value.getDn()); }
	}
	public class StringList extends AbstractProperty implements Iterable<String> {
		// TODO: cache this?
		private final String path;
		public StringList(String path) {this.path=path;}
		@Override public List<String> get() { 
			ArrayList<String> result=new ArrayList<String>();
			XmlNode start=getEntry().getChild(path);
			if (start!=null)
				for (XmlNode child: start.getChildren("string")) 
					result.add(child.getText());
			return result;
		}
		public Iterator<String> iterator() { return get().iterator(); }
		public String getAt(int index) { return get().get(index); }
		public void add(String value) {
			checkIfMayBeModified(); 
			XmlNode newEntry=getEntry().clone();
			XmlNode n=newEntry.getChild(path);
			if (n==null)
				n=newEntry.add(path);
			n.add("string").setText(value);
			updateLdap(newEntry);
		}
		public void remove(String value) {
			checkIfMayBeModified(); 
			XmlNode newEntry= getEntry().clone();
			XmlNode list=newEntry.getChild(path);
			for(XmlNode e: list.getChildren()) {
				if (value.equals(e.getText())) {
					list.remove(e);
				}
			}
			updateLdap(newEntry);
		}
		
		/**
		 * update the existing entry with the new list of values
		 * This will clear the existing child elements and add 
		 * the new entries to the parent. 
		 * 
		 * @param values
		 */
		public void update(List<String> values)
		{
			checkIfMayBeModified(); 
			XmlNode newEntry=getEntry().clone();
			XmlNode n=newEntry.getChild(path);
			if (n==null)
				n=newEntry.add(path);
			
			List<XmlNode> children = n.getChildren();
			for (XmlNode xmlNode : children) {
				n.remove(xmlNode);
			}
			for (String value : values) {
				n.add("string").setText(value);	
			}			
			updateLdap(newEntry);
		}
	}

	public final StringProperty description = new StringProperty("description");
	public final StringProperty desc = description;

	private final LdapObject parent; 
	private XmlNode entry;

	// This constructor is meant to be used by CordysSystem, which has no parent
	protected LdapObject() {
		this.parent=null;
	}
	
	protected LdapObject(LdapObject parent) {
		this.parent=parent; 
	}
	abstract public String getDn();
	@Override public void myclear() { super.myclear(); entry=null; }
	public void debug(String msg) { getSystem().getEnv().debug(msg); } 
	public void info(String msg)  { getSystem().getEnv().info(msg); } 
	public void warn(String msg)  { getSystem().getEnv().warn(msg); } 
	public void error(String msg) { getSystem().getEnv().error(msg); } 

	@Override public String getVarName() {
		String name= getName();
		if (name.indexOf(" ")>=0 || name.indexOf('.')>=0)
			return getParent().getVarName()+"."+prefix()+".\""+name+"\"";
		else
			return getParent().getVarName()+"."+prefix()+"."+name;
	}

	public CordysObject getParent() { return parent; }
	public XmlNode call(XmlNode method) { return getSystem().call(method); }
	
	@Override public String getKey() { return "ldap:"+getDn(); }
	@Override public String getName() {
		String dn=getDn();
		int pos=dn.indexOf("=");
		int pos2=dn.indexOf(",",pos);
		return dn.substring(pos+1,pos2);
	}
	@Override public String toString() { return getVarName(); }
	@Override public boolean equals(Object o) {
		if (o instanceof LdapObject)
			return getDn().equals(((LdapObject)o).getDn());
		return false;
	}
	@Override public int compareTo(CordysObject o) {
		if (o==this)
			return 0;
		if (o==null)
			return -1;
		String[] d1=getKey().split(",");
		String[] d2=o.getKey().split(",");
		int p1=d1.length-1;
		int p2=d2.length-1;
		while (p1>=0 && p2>=0 ) {
			int comp=d1[p1--].compareTo(d2[p2--]);
			if (comp!=0)
				return comp;
		}
		return 0;
	}


	void setEntry(XmlNode entry) {
		this.entry=entry;
		if (entry!=null)
			entry.detach();
	}
	public XmlNode getEntry() {
		if (entry==null || ! useCache())
			setEntry(retrieveEntry(getSystem(),getDn()));
		return entry;
	}
	
	static public XmlNode retrieveEntry(CordysSystem system, String dn) {
		//log("getting dn "+dn);
		XmlNode  method=new XmlNode("GetLDAPObject", xmlns_ldap);
		method.add("dn").setText(dn);
		XmlNode response = system.call(method);
		return response.getChild("tuple/old/entry");
	}

	protected XmlNode newEntryXml(String prefix, String name, String ... types) {
		XmlNode newEntry = new XmlNode("entry",xmlns_ldap);
		newEntry.setAttribute("dn", "cn="+name+","+prefix+getDn());
		XmlNode child = newEntry.add("objectclass");
		child.add("string").setText("top");
		for (String t: types)
			child.add("string").setText(t);
		newEntry.add("cn").add("string").setText(name);
		return newEntry;
	}
	//Added to create Authenticated User XML
	protected XmlNode newAuthenticatedUserEntryXml(String prefix, String name, String ... types) {
		XmlNode newEntry = new XmlNode("entry",xmlns_ldap);
		newEntry.setAttribute("dn", "cn="+name+","+prefix+getSystem().getDn());
		XmlNode child = newEntry.add("objectclass");
		child.add("string").setText("top");
		for (String t: types)
			child.add("string").setText(t);
		newEntry.add("cn").add("string").setText(name);
		return newEntry;
	}
	
	protected void createInLdap(XmlNode newEntry) { updateLdap(null, newEntry); }
	protected void updateLdap(XmlNode newEntry) { updateLdap(entry.clone(), newEntry); }
	protected void updateLdap(XmlNode oldEntry, XmlNode newEntry) {
		XmlNode method=new XmlNode("Update", xmlns_ldap);
		XmlNode tuple=method.add("tuple");
		if (oldEntry!=null)
			tuple.add("old").add(oldEntry);
		if (newEntry!=null)
			tuple.add("new").add(newEntry);
		call(method);
		setEntry(newEntry);
	}
	protected void preDeleteHook() {}
	public void checkIfMayBeModified() {
		CordysObject obj=this;
		while (obj!=null && (obj instanceof LdapObject)) {
			if (obj instanceof Isvp)
				throw new RuntimeException("It is not allowed to delete any part of an ISVP");
			obj=((LdapObject)obj).getParent();
		}
	}
	public void delete() {
		checkIfMayBeModified(); 
		preDeleteHook();
		XmlNode method=new XmlNode("DeleteRecursive", xmlns_ldap);
		XmlNode tuple=method.add("tuple");
		tuple.add("old").add(entry.clone());
		call(method);
		getParent().clear();
		getSystem().removeLdap(getDn());
	}

	public XmlNode dumpXml(){
		XmlNode result=new XmlNode("dump");
		result.setAttribute("name", getSystem().getName());
		result.setAttribute("version", getSystem().version);
		result.setAttribute("build", getSystem().build);
		dumpXml(result);
		return result;
	}

	private void dumpXml(XmlNode result) {
		XmlNode my=result.add("ldap");
		my.setAttribute("name", getName());
		my.setAttribute("dn", getDn());
		my.add(getEntry().clone());
		XmlNode children=my.add("children");
		for (ChildList<?> clist: new Props<ChildList<?>>(this, ChildList.class)) {
			for (LdapObject o: clist)
				o.dumpXml(children);
		}
	}
}
