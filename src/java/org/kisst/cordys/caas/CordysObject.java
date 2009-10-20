package org.kisst.cordys.caas;

import java.lang.reflect.Field;
import java.util.LinkedHashMap;
import java.util.Map;


public abstract class CordysObject {
	public boolean useCache=true;
	abstract public CordysSystem getSystem();
	abstract public void clearCache();
	
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
