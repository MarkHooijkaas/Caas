/**
Copyright 2008, 2009 Mark Hooijkaas

This file is part of the Caas tool.

The Caas tool is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

The Caas tool is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with the Caas tool.  If not, see <http://www.gnu.org/licenses/>.
*/

package org.kisst.cordys.caas;

import java.util.HashMap;
import java.util.Iterator;

public class NamedObjectList<T extends LdapObject> extends HashMap<String,T> implements Iterable {
	private static final long serialVersionUID = 1L;
	
	public String toString() {
		StringBuffer result=new StringBuffer("[");
		boolean first=true;
		for(Object o: this) {
			if (! first)
				result.append(", ");
			else
				first=false;
			result.append(o.toString());
		}
		result.append("]");
		return result.toString();
	}
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
		expr=expr.toLowerCase();
		NamedObjectList <T> result=new NamedObjectList <T>();
		for(T obj: values()) {
			if (obj.getName().toLowerCase().indexOf(expr)>=0)
				result.put(obj.getName(), obj);
		}
		return result;	
	}
	@SuppressWarnings("unchecked")
	public T get(Object key) {
		if (! (key instanceof String))
			return super.get(key);
		String name=((String) key).toLowerCase();;
		Object result=super.get(name);
		if (result==null) {
			for(T obj: values()) {
				if (obj.getName().toLowerCase().indexOf(name)>=0)
					return obj;
			}
		}
		return (T) result;
	}

}
