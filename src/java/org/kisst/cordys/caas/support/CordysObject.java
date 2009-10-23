package org.kisst.cordys.caas.support;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.LinkedHashMap;
import java.util.Map;

import org.kisst.cordys.caas.CordysSystem;
import org.kisst.cordys.caas.util.ReflectionUtil;
import org.kisst.cordys.caas.util.XmlNode;


public abstract class CordysObject implements Comparable<CordysObject> {
	public final static String xmlns_monitor= "http://schemas.cordys.com/1.0/monitor";
	public final static String xmlns_ldap   = "http://schemas.cordys.com/1.0/ldap";
	public final static String xmlns_isv    = "http://schemas.cordys.com/1.0/isvpackage";
	public final static String xmlns_xmlstore="http://schemas.cordys.com/1.0/xmlstore";

	abstract public CordysSystem getSystem();
	abstract public void refresh();
	public String getName() { return null; } 
	public String getKey()  { return null; } 

	public int compareTo(CordysObject other) {
		if (other==null)
			return -1;
		return getKey().compareTo(other.getKey());
	}

	public boolean useCache() { return getSystem().useCache();}

	public Map<String, Object> getProps() {
		Map<String, Object> result= new LinkedHashMap<String, Object>() {
			private static final long serialVersionUID = 1L;
			public String toString() {
				if (size()==0)
					return "{}";
				StringBuilder result=new StringBuilder();
				result.append("{\n");
				boolean first=true;
				Map.Entry<String, Object> previous=null;
				for (Map.Entry<String, Object> entry : this.entrySet()) {
					if (first)
						first=false;
					else
						result.append(",\n");
					result.append(entry.getKey()+"=");
					if (previous!=null && previous.getValue()==entry.getValue() && entry.getValue()!=null)
						result.append("alias("+previous.getKey()+")");
					else {
						previous=entry; 
						if (entry.getValue() instanceof XmlNode) 
							result.append(((XmlNode)entry.getValue()).shortString(40));
						else if (entry.getValue() instanceof CordysLdapObject.XmlProperty) 
							result.append(((CordysLdapObject.XmlProperty)entry.getValue()).getXml().shortString(40));
						else
							result.append(entry.getValue());
					}
				}
				result.append("\n}");
				return result.toString();
			}
		};

		for (Field f: this.getClass().getFields()) {
			if (! Modifier.isStatic(f.getModifiers())) {
				try {
					result.put(f.getName(),  f.get(this));
				} 
				catch (IllegalAccessException e) { throw new RuntimeException(e);}
			}
		}
		for (java.lang.reflect.Method m: this.getClass().getMethods()) {
			if (m.getName().startsWith("get") 
					&& m.getParameterTypes().length==0 
					&& ! java.lang.reflect.Modifier.isStatic(m.getModifiers())) 
			{
				String name=m.getName().substring(3);
				name=name.substring(0,1).toLowerCase()+name.substring(1);
				if (name.equals("props"))
					continue;
				result.put(name, ReflectionUtil.invoke(this, m, null));
			}
		}
		return result;
	}

	public void deepdiff(CordysObject other) { diff(other,100); }
	public void diff(CordysObject other) { diff(other,0); }
	public void diff(CordysObject other, int depth) { diff("", other, depth); }
	public void diff(String prefix,CordysObject other, int depth) {
		if (this.getClass()!=other.getClass())
			throw new RuntimeException("Cannot diff two different classes "
					+this.getClass().getName()+" and "+other.getClass().getName());
		mydiff(prefix, other, depth);
	}
	protected void mydiff(String prefix, CordysObject other, int depth) {}

}
