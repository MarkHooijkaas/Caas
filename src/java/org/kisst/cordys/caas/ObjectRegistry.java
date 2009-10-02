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
		//ldapObjectTypes.put("groupOfNames", Isvp.class); 
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
	
	ObjectRegistry(CordysSystem system) {
		this.system=system;
		remember(system);
	}
	
	
	public synchronized CordysObject getObject(String newdn) {
		//System.out.println("get "+newdn);
		CordysObject result=tree.get(newdn);
		if (result==null) {
			result=createObject(newdn);
			if (result!=null)
				remember(result);
		}
		return result;
	}
	public synchronized CordysObject getObject(Element entry) {
		//System.out.println("get "+JdomUtil.toString(entry));
		String newdn=entry.getAttributeValue("dn");
		CordysObject result=tree.get(newdn);
		if (result==null) {
			result=createObject(entry);
			remember(result);
		}
		return result;
	}
	private void remember(CordysObject obj) {
		tree.put(obj.dn, obj);
		if (system.debug)
			System.out.println("remembering "+obj);
	}
	
	private CordysObject createObject(String newdn) {
		//System.out.println("create "+newdn);
		Element method=new Element("GetLDAPObject", CordysObject.nsldap);
		method.addContent(new Element("dn").setText(newdn));
		Element response = system.call(method);
		Element entry=response.getChild("tuple",null).getChild("old",null).getChild("entry",null);
		return createObject(entry);
	}
	
	private CordysObject createObject(Element entry) {
		//System.out.println("create "+JdomUtil.toString(entry));
		String newdn=entry.getAttributeValue("dn");
		CordysObject parent = getParent(entry);
		Class resultClass = determineClass(entry);
		if (resultClass==null)
			return null;
		CordysObject result;
		//System.out.println(resultClass+","+parent+","+newdn);
		Constructor cons=ReflectionUtil.getConstructor(resultClass, new Class[] {CordysObject.class, String.class});
		cons.setAccessible(true);
		try {
			result = (CordysObject) cons.newInstance(new Object[]{parent, newdn});
			result.setEntry(entry);
		}
		catch (IllegalArgumentException e) { throw new RuntimeException(e); }
		catch (InstantiationException e) { throw new RuntimeException(e); }
		catch (IllegalAccessException e) { throw new RuntimeException(e); }
		catch (InvocationTargetException e) { throw new RuntimeException(e); }
		tree.put(newdn, result);
		return result;
	}

	private CordysObject getParent(Element entry) {
		String dn=entry.getAttributeValue("dn");
		//System.out.println("getParent "+dn);
		do {
			dn=dn.substring(dn.indexOf(",")+1);
			CordysObject parent=getObject(dn);
			if (parent!=null)
				return parent;
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
		if (dn.substring(dn.indexOf(",")+1).equals(system.dn) && dn.startsWith("cn="))
			return Isvp.class;
		return null;
	}
	@SuppressWarnings("unchecked")
	public <T extends CordysObject> NamedObjectList<T> createObjectsFromEntries(Element response) {
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
	@SuppressWarnings("unchecked")
	public <T extends CordysObject> NamedObjectList<T> createObjectsFromStrings(Element start, String group) {
		NamedObjectList<T> result=new NamedObjectList<T>();
		start=start.getChild(group,null);
		for (Object s: start.getChildren("string", null)) {
			String dn=((Element) s).getText();
			CordysObject obj=system.getObject(dn);
			result.put(obj.getName(),(T) obj);
			//System.out.println(dn);
		}
		return result;
	}

}
