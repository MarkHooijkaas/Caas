package org.kisst.cordys.caas;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import org.jdom.Element;
import org.jdom.Namespace;
import org.kisst.cordys.caas.util.JdomUtil;
import org.kisst.cordys.caas.util.ReflectionUtil;


public class CordysObject {
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
	public final Namespace nsldap10=Namespace.getNamespace("http://schemas.cordys.com/1.0/ldap");
	//public final Namespace nsldap11=Namespace.getNamespace("http://schemas.cordys.com/1.1/ldap");
	
	public String getDn() { return dn; }
	public String getName() {
		int pos=dn.indexOf("=");
		int pos2=dn.indexOf(",",pos);
		return dn.substring(pos+1,pos2);
	}
	public String toString() {
		String c=this.getClass().getSimpleName()+"(";
		if (parent!=null && ! (parent instanceof CordysSystem))
			c+=parent.toString()+",";
		return c+getName()+")"; 
	}

	public String call(String input) { return getSystem().call(input); }
	public Element call(Element method) { 
		String xml = JdomUtil.toString(method);
		String response= getSystem().call(xml);
		return JdomUtil.fromString(response);
	}


	public List<CordysObject> getChildren() {
		Element method=new Element("GetChildren", nsldap10);
		method.addContent(new Element("dn").setText(dn));
		return createObjects(call(method), CordysObject.class);
	}
	
	public Element getDetails() {
		Element method=new Element("GetLDAPObject", nsldap10);
		method.addContent(new Element("dn").setText(dn));
		return call(method);
	}
	

	@SuppressWarnings("unchecked")
	public <T> List<T> createObjects(Element response, Class resultClass) {
		ArrayList<T> result=new ArrayList<T>();
		Constructor cons=ReflectionUtil.getConstructor(resultClass, new Class[] {CordysObject.class, String.class});

		if (response.getName().equals("Envelope"))
			response=response.getChild("Body",null).getChild(null,null);
		for (Object tuple : response.getChildren("tuple", null)) {
			Element elm=((Element) tuple).getChild("old", null).getChild("entry", null);
			String dn2=elm.getAttributeValue("dn");
			CordysObject obj;
			try {
				obj = (CordysObject) cons.newInstance(new Object[]{this, dn2});
				obj.entry=elm;
			}
			catch (IllegalArgumentException e) { throw new RuntimeException(e); }
			catch (InstantiationException e) { throw new RuntimeException(e); }
			catch (IllegalAccessException e) { throw new RuntimeException(e); }
			catch (InvocationTargetException e) { throw new RuntimeException(e); }
			result.add((T) obj);
			//System.out.println(dn);
		}
		return result;
	}
}
