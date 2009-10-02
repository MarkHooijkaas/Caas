package org.kisst.cordys.caas;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;

import org.jdom.Element;
import org.kisst.cordys.caas.util.ReflectionUtil;

public class ObjectRegistry {
	private final static HashMap<String,Class> ldapObjectTypes=new HashMap<String,Class>();
	static {
		ldapObjectTypes.put("busauthenticationuser", AuthenticatedUser.class);
		ldapObjectTypes.put("groupOfNames", Isvp.class); // ???
		ldapObjectTypes.put("busmethod", Method.class);
		ldapObjectTypes.put("busmethodset", MethodSet.class);
		ldapObjectTypes.put("organization", Organization.class);
		ldapObjectTypes.put("busorganizationalrole", Role.class);
		ldapObjectTypes.put("bussoapnode", SoapNode.class);
		ldapObjectTypes.put("bussoapprocessor", SoapProcessor.class);
		ldapObjectTypes.put("busorganizationaluser", User.class);
	}
	
	
	private final HashMap<String, CordysObject> tree=new HashMap<String, CordysObject>();
	private final CordysSystem system;
	
	public ObjectRegistry(CordysSystem system) {
		this.system=system;
		tree.put(system.dn, system);
	}
	
	public synchronized CordysObject getObject(String dn) {
		CordysObject result=tree.get(dn);
		if (result==null) {
			result=createObject(dn);
			tree.put(dn, result);
		}
		return result;
	}
	public synchronized CordysObject getObject(Element entry) {
		String dn=entry.getAttributeValue("dn");
		CordysObject result=tree.get(dn);
		if (result==null) {
			result=createObject(entry);
			tree.put(dn, result);
		}
		return result;
	}
	private CordysObject createObject(String dn) {
		Element method=new Element("GetLDAPObject", CordysObject.nsldap10);
		method.addContent(new Element("dn").setText(dn));
		Element response = system.call(method);
		Element entry=response.getChild("tuple",null).getChild("old",null).getChild("entry",null);
		return createObject(entry);
	}
	
	private CordysObject createObject(Element entry) {
		String dn=entry.getAttributeValue("dn");
		CordysObject parent = getParent(entry);
		Class resultClass = determineClass(entry);
		if (resultClass==null)
			return null;
		CordysObject result;
		Constructor cons=ReflectionUtil.getConstructor(resultClass, new Class[] {CordysObject.class, String.class});
		try {
			result = (CordysObject) cons.newInstance(new Object[]{parent, entry});
			//result.entry=elm;
		}
		catch (IllegalArgumentException e) { throw new RuntimeException(e); }
		catch (InstantiationException e) { throw new RuntimeException(e); }
		catch (IllegalAccessException e) { throw new RuntimeException(e); }
		catch (InvocationTargetException e) { throw new RuntimeException(e); }
		tree.put(dn, result);
		return result;
	}

	private CordysObject getParent(Element entry) {
		String dn=entry.getAttributeValue("dn");
		do {
			dn=dn.substring(dn.indexOf(",")+1);
			CordysObject parent=getObject(entry);
			if (parent!=null)
				return parent;
		} while (dn.length()>0);
		throw new RuntimeException("Could not find a parent for "+dn);
	}

	private Class determineClass(Element entry) {
		Element objectclass=entry.getChild("objectclass",null);
		for(Object o:objectclass.getChildren("string",null)) {
			Class c=ldapObjectTypes.get(((Element) o).getText());
			if (c!=null)
				return c;
		}
		return null;
	}

}
