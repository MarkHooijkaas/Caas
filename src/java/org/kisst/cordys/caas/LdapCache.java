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

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;

import org.kisst.cordys.caas.util.ReflectionUtil;
import org.kisst.cordys.caas.util.XmlNode;

public class LdapCache {
	private final static HashMap<String,Class> ldapObjectTypes=new HashMap<String,Class>();
	static {
		ldapObjectTypes.put("busauthenticationuser", AuthenticatedUser.class);
		//ldapObjectTypes.put("groupOfNames", Isvp.class); this one is not unique
		ldapObjectTypes.put("busmethod", Method.class);
		ldapObjectTypes.put("busmethodset", MethodSet.class);
		ldapObjectTypes.put("organization", Organization.class);
		ldapObjectTypes.put("busorganizationalrole", Role.class);
		ldapObjectTypes.put("bussoapnode", SoapNode.class);
		ldapObjectTypes.put("bussoapprocessor", SoapProcessor.class);
		ldapObjectTypes.put("busorganizationaluser", User.class);
	}
	private final HashMap<String, LdapObject> tree=new HashMap<String, LdapObject>();
	private final CordysSystem system;
	
	LdapCache(CordysSystem system) {
		this.system=system;
		remember(system);
	}

	public void clear() {
		for (LdapObject o: tree.values()) {
			if (o instanceof CordysLdapObject)
				((CordysLdapObject) o).clear();
		}
		tree.clear();
		tree.put(system.dn,system);
	}

	public synchronized LdapObject getObject(String newdn) {
		//System.out.println("get ["+newdn+"]");
		LdapObject result=tree.get(newdn);
		if (result==null) {
			result=createObject(newdn);
			if (result!=null)
				remember(result);
		}
		return result;
	}
	public synchronized LdapObject getObject(XmlNode entry) {
		String newdn=entry.getAttribute("dn");
		//System.out.println("get ["+newdn+"]");
		LdapObject result=tree.get(newdn);
		if (result==null) {
			result=createObject(entry);
			remember(result);
		}
		return result;
	}
	public void remove(String dn) { tree.put(dn, null);	}
	private void remember(LdapObject obj) {
		//System.out.println("remembering ["+obj.getDn()+"]");
		tree.put(obj.getDn(), obj);
		if (system.debug)
			System.out.println("remembering "+obj);
	}
	
	private LdapObject createObject(String newdn) {
		//System.out.println("create "+newdn);
		XmlNode method=new XmlNode("GetLDAPObject", CordysLdapObject.xmlns_ldap);
		method.add("dn").setText(newdn);
		XmlNode response = system.call(method);
		XmlNode entry=response.getChild("tuple/old/entry");
		return createObject(entry);
	}
	
	private LdapObject createObject(XmlNode entry) {
		if (entry==null)
			return null;
		String newdn=entry.getAttribute("dn");
		LdapObject parent = getParent(entry);
		Class resultClass = determineClass(entry);
		if (resultClass==null)
			return null;
		//System.out.println(resultClass+","+parent+","+newdn);
		Constructor cons=ReflectionUtil.getConstructor(resultClass, new Class[] {LdapObject.class, String.class});
		cons.setAccessible(true);
		try {
			CordysLdapObject result = (CordysLdapObject) cons.newInstance(new Object[]{parent, newdn});
			result.setEntry(entry);
			tree.put(newdn, result);
			return result;
		}
		catch (IllegalArgumentException e) { throw new RuntimeException(e); }
		catch (InstantiationException e) { throw new RuntimeException(e); }
		catch (IllegalAccessException e) { throw new RuntimeException(e); }
		catch (InvocationTargetException e) { throw new RuntimeException(e); }
	}

	private LdapObject getParent(XmlNode entry) {
		String dn=entry.getAttribute("dn");
		//System.out.println("getParent ["+dn+"]");
		if (dn.length()<=system.dn.length()) // Safeguard
			return null;
		do {
			int pos=dn.indexOf(",");
			dn=dn.substring(pos+1);
			LdapObject parent=getObject(dn);
			if (parent!=null)
				return parent;
		} while (dn.length()>0);
		throw new RuntimeException("Could not find a parent for "+dn);
	}

	private Class determineClass(XmlNode entry) {
		XmlNode objectclass=entry.getChild("objectclass");
		for(XmlNode o:objectclass.getChildren("string")) {
			Class c=ldapObjectTypes.get(o.getText());
			if (c!=null)
				return c;
		}
		String dn=entry.getAttribute("dn");
		if (dn.substring(dn.indexOf(",")+1).equals(system.getDn()) && dn.startsWith("cn="))
			return Isvp.class;
		return null;
	}
}
