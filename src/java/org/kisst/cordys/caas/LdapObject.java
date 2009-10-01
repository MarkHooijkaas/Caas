package org.kisst.cordys.caas;

import java.io.IOException;
import java.io.StringReader;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.Namespace;
import org.jdom.input.SAXBuilder;
import org.jdom.output.XMLOutputter;
import org.kisst.cordys.caas.util.ReflectionUtil;

public abstract class LdapObject {
	public final Namespace nsldap=Namespace.getNamespace("http://schemas.cordys.com/1.1/ldap");
	
	protected final String dn;
	protected LdapObject(String dn) {
		this.dn=dn;
	}
	public String toString() {
		String c=this.getClass().getSimpleName();
		return c+"("+getName()+")"; 
	}
	public String getDn() { return dn; }
	public String getName() {
		int pos=dn.indexOf("=");
		int pos2=dn.indexOf(",",pos);
		return dn.substring(pos+1,pos2);
	}
	
	abstract public CordysSystem getSystem(); 
	public String call(String input) { return getSystem().call(input); }
	public Element call(Element method) { 
		XMLOutputter out=new XMLOutputter();
		String xml= out.outputString(method);
		String response= getSystem().call(xml);
		SAXBuilder builder = new SAXBuilder();
		Document doc;
		try {
			doc = builder.build(new StringReader(response));
		}
		catch (JDOMException e) { throw new RuntimeException(e); }
		catch (IOException e) { throw new RuntimeException(e); }
		return doc.getRootElement();
	}

	public List<CordysObject> getChildren() { return getChildren("entry dn=\""); }
	public List<CordysObject> getChildren(String key) {
		String msg="<GetChildren xmlns=\"http://schemas.cordys.com/1.0/ldap\">"
			+"<dn>${dn}</dn>"
		    +"</GetChildren>";
		msg = msg.replaceAll("\\$\\{dn}",dn);
		String response=call(msg);
		ArrayList<CordysObject> result=new ArrayList<CordysObject>();
		int pos=0;
		while ((pos=response.indexOf(key, pos))>0) {
			pos=pos+key.length();
			String dn2=response.substring(pos,response.indexOf("\"", pos));
			result.add(new CordysObject(getSystem(), dn2));
		}
		return result;
	}
	
	public String getDetails() {
		String msg="<GetLDAPObject xmlns=\"http://schemas.cordys.com/1.0/ldap\">"
			+"<dn>${dn}</dn>"
		    +"</GetLDAPObject>";
		msg = msg.replaceAll("\\$\\{dn}",dn);
		return call(msg);
	}
	
	public <T> List<T> getChildren(CordysSystem system, String method, Class resultClass) { 
		return getChildren(system, method, resultClass, "entry dn=\""); 
	}
	@SuppressWarnings("unchecked")
	public <T> List<T> getChildren(CordysSystem system, String method, Class resultClass, String key) {
		String msg="<${method} xmlns=\"http://schemas.cordys.com/1.0/ldap\">"
			+"<dn>${dn}</dn>"
		    +"</${method}>";
		msg = msg.replaceAll("\\$\\{dn}",dn);
		msg = msg.replaceAll("\\$\\{method}",method);
		String response=call(msg);
		ArrayList<T> result=new ArrayList<T>();
		int pos=0;
		Constructor cons=ReflectionUtil.getConstructor(resultClass, new Class[] {CordysSystem.class, String.class});
		while ((pos=response.indexOf(key, pos))>0) {
			pos=pos+key.length();
			String dn2=response.substring(pos,response.indexOf("\"", pos));
			Object obj;
			try {
				obj = cons.newInstance(new Object[]{system, dn2});
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

	@SuppressWarnings("unchecked")
	public <T> List<T> createObjects(Element response, Class resultClass, String key) {
		ArrayList<T> result=new ArrayList<T>();
		Constructor cons=ReflectionUtil.getConstructor(resultClass, new Class[] {CordysSystem.class, String.class});

		if (response.getName().equals("Envelope"))
			response=response.getChild("Body",null).getChild(null,null);
		for (Object tuple : response.getChildren("tuple", null)) {
			Element elm=((Element) tuple).getChild("old", null).getChild("entry", null);
			String dn2=elm.getAttributeValue("dn");
			Object obj;
			try {
				obj = cons.newInstance(new Object[]{getSystem(), dn2});
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
