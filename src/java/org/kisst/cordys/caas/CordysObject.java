package org.kisst.cordys.caas;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.LinkedHashMap;
import java.util.Map;


public abstract class CordysObject implements Comparable<CordysObject> {
	public final static String xmlns_monitor= "http://schemas.cordys.com/1.0/monitor";
	public final static String xmlns_ldap   = "http://schemas.cordys.com/1.0/ldap";
	public final static String xmlns_isv    = "http://schemas.cordys.com/1.0/isvpackage";
	public final static String xmlns_xmlstore="http://schemas.cordys.com/1.0/xmlstore";
	
	abstract public CordysSystem getSystem();
	abstract public void refresh();
	public String getName() { return null; } 
	public String getKey()  { return null; } 
	
	public boolean useCache() { return getSystem().useCache();}
	
	public Map<String, Object> getProps() {
		Map<String, Object> result= new LinkedHashMap<String, Object>() {
			private static final long serialVersionUID = 1L;
			public String toString() {
				if (size()==0)
					return "{}";
				StringBuilder result=new StringBuilder();
				result.append("{");
				boolean first=true;
				for (Entry<String, Object> entry : this.entrySet()) {
					result.append("\n"+entry.getKey()+"="+entry.getValue());
					if (first)
						first=false;
					else
						result.append(",");
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
		return result;
	}

	public int compareTo(CordysObject o) {
		if (o==this)
			return 0;
		String[] d1=getKey().split(",");
		String[] d2=o.getKey().split(",");
		int p1=d1.length-1;
		int p2=d2.length-1;
		while (p1>=0 && p2>=0 ) {
			int comp=d1[p1--].compareTo(d2[p2--]);
			if (comp!=0)
				return comp;
		}
		return 0;
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
