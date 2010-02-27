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

import java.lang.reflect.Constructor;
import java.util.HashMap;

import org.kisst.cordys.caas.AuthenticatedUser;
import org.kisst.cordys.caas.ConnectionPoint;
import org.kisst.cordys.caas.CordysSystem;
import org.kisst.cordys.caas.Isvp;
import org.kisst.cordys.caas.Method;
import org.kisst.cordys.caas.MethodSet;
import org.kisst.cordys.caas.Organization;
import org.kisst.cordys.caas.OsProcess;
import org.kisst.cordys.caas.Role;
import org.kisst.cordys.caas.SoapNode;
import org.kisst.cordys.caas.SoapProcessor;
import org.kisst.cordys.caas.User;
import org.kisst.cordys.caas.Xsd;
import org.kisst.cordys.caas.util.ReflectionUtil;
import org.kisst.cordys.caas.util.XmlNode;


/**
 * This is the base class for all kinds of Ldap Objects, except for CordysSystem, which is special
 * This basically is just a convenience class provding the getDn() and getSystem() method
 * so that not all sublcasses need to implement these again.
 * It is separate from the LdapObject class, because CordysSystem also is a LdapObject, but does
 * can't use the dn and system (itself) at construction time.
 */
public abstract class LdapObjectBase extends LdapObject {
	private final CordysSystem system;
	private final String dn;

	protected LdapObjectBase(LdapObject parent, String dn) {
		super(parent);
		this.system=parent.getSystem();
		this.dn=dn;
	}

	@Override public CordysSystem getSystem() { return system; }
	@Override public String getDn() { return dn; }
	


	public static LdapObject createObject(CordysSystem system, String dn) {
		XmlNode method=new XmlNode("GetLDAPObject", CordysObject.xmlns_ldap);
		method.add("dn").setText(dn);
		XmlNode response = system.call(method);
		XmlNode entry=response.getChild("tuple/old/entry");
		return createObject(system, entry);
	}

	public static LdapObject createObject(CordysSystem system, XmlNode entry) {
		if (entry==null)
			return null;
		String newdn=entry.getAttribute("dn");
		//System.out.println("createObject ["+newdn+"]");
		LdapObject parent = calcParent(system, entry.getAttribute("dn"));
		Class<?> resultClass = determineClass(system, entry);
		if (resultClass==Isvp.class) {
			if (newdn.startsWith("cn=licinfo,") || newdn.startsWith("cn=authenticated users,") || newdn.startsWith("cn=consortia,"))
				return null;
		}
		if (resultClass==null)
			throw new RuntimeException("could not determine class for entry "+entry);
		Constructor<?> cons=ReflectionUtil.getConstructor(resultClass, new Class[]{LdapObject.class, String.class} );
		LdapObject result = (LdapObject) ReflectionUtil.createObject(cons, new Object[]{parent, newdn});
		result.setEntry(entry);
		return result;
	}
	
	private final static HashMap<String,Class<?>> ldapObjectTypes=new HashMap<String,Class<?>>();
	static {
		ldapObjectTypes.put("busauthenticationuser", AuthenticatedUser.class);
		//ldapObjectTypes.put("groupOfNames", Isvp.class); this one is not unique
		ldapObjectTypes.put("busmethod", Method.class);
		ldapObjectTypes.put("busmethodset", MethodSet.class);
		ldapObjectTypes.put("busmethodtype", Xsd.class);
		ldapObjectTypes.put("organization", Organization.class);
		ldapObjectTypes.put("busorganizationalrole", Role.class);
		ldapObjectTypes.put("bussoapnode", SoapNode.class);
		ldapObjectTypes.put("bussoapprocessor", SoapProcessor.class);
		ldapObjectTypes.put("busorganizationaluser", User.class);
		ldapObjectTypes.put("busconnectionpoint", ConnectionPoint.class);
		ldapObjectTypes.put("busosprocess", OsProcess.class);
	}
	static private Class<?> determineClass(CordysSystem system, XmlNode entry) {
		if (entry==null)
			return null;
		XmlNode objectclass=entry.getChild("objectclass");
		for(XmlNode o:objectclass.getChildren("string")) {
			Class<?> c=ldapObjectTypes.get(o.getText());
			if (c!=null)
				return c;
		}
		String dn=entry.getAttribute("dn");
		if (dn.substring(dn.indexOf(",")+1).equals(system.getDn()) && dn.startsWith("cn="))
			return Isvp.class;
		return null;
	}
	static private LdapObject calcParent(CordysSystem system, String dn) {
		//System.out.println("calcParent ["+dn+"]");
		String origdn=dn;
		int pos;
		while ((pos=dn.indexOf(","))>=0) {
			dn=dn.substring(pos+1);
			LdapObject parent=system.seekLdap(dn);
			if (parent!=null)
				return parent;
			XmlNode entry=retrieveEntry(system, dn);
			if (entry==null) // could happen when restoring from a dump
				continue;
			Class<?> resultClass = determineClass(system, entry);
			if (resultClass!=null)
				return createObject(system, entry);
		}
		throw new RuntimeException("Could not find a parent for "+origdn);
	}

}
