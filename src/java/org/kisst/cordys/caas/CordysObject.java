package org.kisst.cordys.caas;

import java.lang.reflect.Field;
import java.util.LinkedHashMap;
import java.util.Map;


public abstract class CordysObject {
	public final static String xmlns_monitor= "http://schemas.cordys.com/1.0/monitor";
	public final static String xmlns_ldap   = "http://schemas.cordys.com/1.0/ldap";
	public final static String xmlns_isv    = "http://schemas.cordys.com/1.0/isvpackage";

	abstract public CordysSystem getSystem();
	abstract public void clearCache();
	
	public boolean useCache() { return getSystem().useCache();}
	
	public Map<String, CordysObject> getProps() {
		Map<String, CordysObject> result= new LinkedHashMap<String, CordysObject>();
		for (Field f: this.getClass().getFields()) {
			if (CordysObject.class.isAssignableFrom(f.getType())) {
				try {
					result.put(f.getName(), (CordysObject) f.get(this));
				} 
				catch (IllegalAccessException e) { throw new RuntimeException(e);}
			}
		}
		return result;
	}
}
