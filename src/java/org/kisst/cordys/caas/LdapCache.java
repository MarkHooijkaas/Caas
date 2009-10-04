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

import org.jdom.Element;
import org.kisst.cordys.caas.util.ReflectionUtil;

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
	
	
	public synchronized LdapObject getObject(String newdn) {
		//System.out.println("get "+newdn);
		LdapObject result=tree.get(newdn);
		if (result==null) {
			result=createObject(newdn);
			if (result!=null)
				remember(result);
		}
		return result;
	}
	public synchronized LdapObject getObject(Element entry) {
		//System.out.println("get "+JdomUtil.toString(entry));
		String newdn=entry.getAttributeValue("dn");
		LdapObject result=tree.get(newdn);
		if (result==null) {
			result=createObject(entry);
			remember(result);
		}
		return result;
	}
	public void remove(String dn) { tree.put(dn, null);	}
	private void remember(LdapObject obj) {
		tree.put(obj.getDn(), obj);
		if (system.debug)
			System.out.println("remembering "+obj);
	}
	
	private LdapObject createObject(String newdn) {
		//System.out.println("create "+newdn);
		Element method=new Element("GetLDAPObject", CordysSystem.nsldap);
		method.addContent(new Element("dn").setText(newdn));
		Element response = system.soapCall(method);
		Element entry=response.getChild("tuple",null).getChild("old",null).getChild("entry",null);
		return createObject(entry);
	}
	
	private LdapObject createObject(Element entry) {
		//System.out.println("create "+JdomUtil.toString(entry));
		String newdn=entry.getAttributeValue("dn");
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

	private LdapObject getParent(Element entry) {
		String dn=entry.getAttributeValue("dn");
		//System.out.println("getParent "+dn);
		do {
			dn=dn.substring(dn.indexOf(",")+1);
			LdapObject parent=getObject(dn);
			if (parent!=null)
				return (LdapObject) parent;
		} while (dn.length()>0);
		throw new RuntimeException("Could not find a parent for "+dn);
	}

	private Class determineClass(Element entry) {
		//System.out.println(JdomUtil.toString(entry));
		Element objectclass=entry.getChild("objectclass",null);
		for(Object o:objectclass.getChildren("string",null)) {
			Class c=ldapObjectTypes.get(((Element) o).getText());
			if (c!=null)
				return c;
		}
		String dn=entry.getAttributeValue("dn");
		if (dn.substring(dn.indexOf(",")+1).equals(system.getDn()) && dn.startsWith("cn="))
			return Isvp.class;
		return null;
	}
}
