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

import java.util.ArrayList;
import java.util.List;

import org.kisst.cordys.caas.util.XmlNode;


public abstract class CordysLdapObject extends CordysObject implements LdapObject {
	protected abstract class AbstractProperty extends CordysObject {
		public void refresh() {}
		public CordysSystem getSystem() { return CordysLdapObject.this.getSystem();	}
		abstract public Object get();
		public String toString() { return ""+get(); }
	}
	protected class StringProperty extends AbstractProperty {
		private final String path;
		private final int startPos;
		protected StringProperty(String path) { this(path,0);}
		public StringProperty(String path, int startPos) {
			this.startPos=startPos;
			this.path=path+"/string";
		}
		public String get() { 
			String s= getEntry().getChildText(path);
			if (s!=null && startPos>0)
				return s.substring(startPos);
			return s;
		}
		public void set(String value) { 
			XmlNode newEntry=getEntry().clone();
			newEntry.getChild(path).setText(value);
			updateLdap(newEntry);
		}
	}
	protected class XmlProperty extends StringProperty {
		protected XmlProperty(String path) {super(path);}
		public XmlNode getXml() { return new XmlNode(get()); }
		public void set(XmlNode value) { set(value.toString()); }
	}
	protected class XmlSubProperty {
		private final XmlProperty xml;
		private final String path;
		protected XmlSubProperty(XmlProperty xml, String path) {
			this.xml=xml;
			this.path=path;
		}
		public String get() {return xml.getXml().getChildText(path); }
		public void set(String value) {
			XmlNode newnode=xml.getXml().clone();
			newnode.setChildText(path, value);
			xml.set(newnode);
		}
		public String toString() { return ""+get(); }
	}

	protected class BooleanProperty extends StringProperty {
		protected BooleanProperty(String path) {super(path);}
		public Boolean getBool() { return "true".equals(get()); }
		public void set(boolean value) { set(""+value);}
	}
	protected class RefProperty<T extends LdapObject> extends StringProperty {
		protected RefProperty(String path) {super(path);}
		@SuppressWarnings("unchecked")
		public T getRef() { return (T) getSystem().getObject(get()); }
		public void set(T value) { set(value.getDn()); }
	}
	protected class StringList extends AbstractProperty {
		// TODO: cache this?
		private final String path;
		protected StringList(String path) {this.path=path;}
		public List<String> get() { 
			ArrayList<String> result=new ArrayList<String>();
			XmlNode start=getEntry().getChild(path);
			if (start!=null)
				for (XmlNode child: start.getChildren("string")) 
					result.add(child.getText());
			return result;
		}
		public void add(String value) {
			XmlNode newEntry=getEntry().clone();
			newEntry.getChild(path).add("string").setText(value);
			updateLdap(newEntry);
		}
		public void remove(String value) {
			XmlNode newEntry= getEntry().clone();
			XmlNode list=newEntry.getChild(path);
			for(XmlNode e: list.getChildren()) {
				if (value.equals(e.getText())) {
					list.remove(e);
				}
			}
			updateLdap(newEntry);
		}

	}

	public final StringProperty description = new StringProperty("description");

	private final CordysSystem system;
	private final LdapObject parent; 
	protected final String dn;
	private XmlNode entry;

	protected CordysLdapObject(CordysSystem system, String dn) {
		this.system=system;
		this.parent=null;
		this.dn=dn;
	}
	
	protected CordysLdapObject(LdapObject parent, String dn) {
		this.system=parent.getSystem();
		this.parent=parent;
		this.dn=dn;
	}
	public void refresh() { 
		entry=null;
		for (Object o: getProps().values()) {
			if (o instanceof CordysObject)
				((CordysObject) o).refresh();
		}
	}
	public LdapObject getParent() { return parent; }
	public CordysSystem getSystem() { return system; }
	public XmlNode call(XmlNode method) { return getSystem().call(method); }
	
	public String getKey() { return dn; }
	public String getDn() { return dn; }
	public String getName() {
		int pos=dn.indexOf("=");
		int pos2=dn.indexOf(",",pos);
		return dn.substring(pos+1,pos2);
	}
	public String toString() {
		//if (getSystem().displayFormat==0) {
		if (true) {
			String c=this.getClass().getSimpleName()+"("+getName()+")";
			if (parent!=null && (parent instanceof CordysLdapObject))
				c=parent.toString()+"."+c;
			return c; 
		}
		else {
			if (parent!=null && (parent instanceof CordysLdapObject))
				return parent.toString()+"."+getName();
			else
				return getName();
		}
	}
	public boolean equals(Object o) {
		if (o instanceof LdapObject)
			return dn.equals(((LdapObject)o).getDn());
		return false;
	}

	void setEntry(XmlNode entry) {
		this.entry=entry;
		entry.detach();
	}
	public XmlNode getEntry() {
		if (entry!=null && useCache())
			return entry;
		XmlNode  method=new XmlNode("GetLDAPObject", xmlns_ldap);
		method.add("dn").setText(dn);
		XmlNode response = call(method);
		setEntry(response.getChild("tuple/old/entry"));
		return entry;
	}

	protected void updateLdap(XmlNode newEntry) {
		XmlNode method=new XmlNode("Update", xmlns_ldap);
		XmlNode tuple=method.add("tuple");
		tuple.add("old").add(entry.clone());
		tuple.add("new").add(newEntry);
		call(method);
		setEntry(newEntry);
	}
}
