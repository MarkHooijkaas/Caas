package org.kisst.cordys.caas.support;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import org.kisst.cordys.caas.util.ReflectionUtil;
import org.kisst.cordys.caas.util.XmlNode;

public class Props implements Iterable<Object> {
	private final Map<String, Object> list= new LinkedHashMap<String, Object>();
	private String prevName=null;
	private Object prevValue=null;

	public static class Alias {
		public final String origName;  
		public final Object value;
		public Alias(String origName, Object value) {
			this.origName=origName;
			this.value=value;
		}
		public String toString() { return value.toString(); }
	}

	public Props(Object target, Class<?> clz) {
		//System.out.println("getting props for "+this.getClass().getName());
		for (Field f: target.getClass().getFields()) {
			if (! Modifier.isStatic(f.getModifiers())) {
				try {
					if (Props.class.isAssignableFrom(f.getType())) 
						continue; // prevent endless recursion
					if (clz==null || clz.isAssignableFrom(f.getType()))
						add(f.getName(), f.get(target));
				} 
				catch (IllegalAccessException e) { throw new RuntimeException(e);}
			}
		}
		for (java.lang.reflect.Method m: target.getClass().getMethods()) {
			if (m.getName().startsWith("get") 
					&& m.getParameterTypes().length==0 
					&& ! Modifier.isStatic(m.getModifiers())) 
			{
				if (clz==null || clz.isAssignableFrom(m.getReturnType())) {
					if (Props.class.isAssignableFrom(m.getReturnType())) 
						continue; // prevent endless recursion
					String name=m.getName().substring(3);
					name=name.substring(0,1).toLowerCase()+name.substring(1);
					add(name, ReflectionUtil.invoke(target, m, null));
				}
			}
		}
	}

	public Iterator<Object> iterator() { return list.values().iterator();}
	public Set<Map.Entry<String,Object>> entrySet() { return list.entrySet(); } 

	private void add(String name, Object value) { 
		if (prevValue!=null && prevValue==value)
			value=new Alias(prevName,prevValue);
		else {
			prevValue=value;
			prevName=name;
		}
		list.put(name, value);
	} 

	public String toString() {
		if (list.size()==0)
			return "{}";
		StringBuilder result=new StringBuilder();
		result.append("{\n");
		boolean first=true;
		for (Map.Entry<String, Object> entry : list.entrySet()) {
			if (first)
				first=false;
			else
				result.append(",\n");
			result.append(entry.getKey()+"=");
			Object value=entry.getValue();
			if (value instanceof Alias)
				result.append("alias("+((Alias)value).origName+")");
			else if (value instanceof XmlNode) 
				result.append(((XmlNode)value).shortString(40));
			else if (value instanceof LdapObject.XmlProperty) 
				result.append(((LdapObject.XmlProperty)value).getXml().shortString(40));
			else
				result.append(value);
		}
		result.append("\n}");
		return result.toString();
	}
}