package org.kisst.cordys.caas;

import java.util.HashMap;
import java.util.Iterator;

public class NamedObjectList<T extends CordysObject> extends HashMap<String,T>{
	private static final long serialVersionUID = 1L;
	
	public Iterator<T> iterator() { return values().iterator(); }
	public T getAt(int index) {
		for(T obj: values()) {
			if (index--<=0)
				return obj;
		}
		throw new IndexOutOfBoundsException("Index out of bounds");	
	}
	public T propertyMissing(String name) { return get(name); }

	public NamedObjectList <T> like(String expr) {
		NamedObjectList <T> result=new NamedObjectList <T>();
		for(T obj: values()) {
			if (obj.getName().indexOf(expr)>=0)
				result.put(obj.getName(), obj);
		}
		return result;	
	}

}
