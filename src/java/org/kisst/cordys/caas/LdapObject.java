package org.kisst.cordys.caas;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import org.kisst.cordys.caas.util.ReflectionUtil;

public abstract class LdapObject {
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
	
	abstract protected CordysSystem getSystem(); 
	protected String call(String input) { return getSystem().call(input); }

	public List<CordysObject> getChildren() {
		String msg="<GetChildren xmlns=\"http://schemas.cordys.com/1.0/ldap\">"
			+"<dn>${dn}</dn>"
		    +"</GetChildren>";
		msg = msg.replaceAll("\\$\\{dn}",dn);
		String response=call(msg);
		ArrayList<CordysObject> result=new ArrayList<CordysObject>();
		int pos=0;
		String key="entry dn=\"";
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
	
	@SuppressWarnings("unchecked")
	public <T> List<T> getChildren(CordysSystem system, String method, Class resultClass) {
		String msg="<${method} xmlns=\"http://schemas.cordys.com/1.0/ldap\">"
			+"<dn>${dn}</dn>"
		    +"</${method}>";
		msg = msg.replaceAll("\\$\\{dn}",dn);
		msg = msg.replaceAll("\\$\\{method}",method);
		String response=call(msg);
		ArrayList<T> result=new ArrayList<T>();
		int pos=0;
		String key="entry dn=\"";
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

}
