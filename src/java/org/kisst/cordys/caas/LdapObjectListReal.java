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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

public class LdapObjectListReal<T extends LdapObject> extends ArrayList<T> implements LdapObjectList<T> {
	private static final long serialVersionUID = 1L;
	private final HashMap<String,T> dnIndex=new HashMap<String,T>(); 
	private final HashMap<String,T> nameIndex=new HashMap<String,T>(); 
	
	public LdapObjectListReal() {}
	public LdapObjectListReal(LdapObjectList<T> l) {
		for(T obj : l)
			add(obj);
	}
	public boolean add(T obj) {
		super.add(obj);
		dnIndex.put(obj.getDn(), obj);
		nameIndex.put(obj.getName(), obj);
		return true;
	}
	
	public int getSize() { return size(); }
	public String toString() { return toString("[\t",",\n\t","]"); }
	public String toString(String begin, String middle, String end) {
		StringBuffer result=new StringBuffer(begin);
		boolean first=true;
		for(Object o: this) {
			if (! first)
				result.append(middle);
			else
				first=false;
			result.append(o.toString());
		}
		result.append(end);
		return result.toString();
	}
	
	public T getAt(int index) { return get(index); } 
	
	public LdapObjectListReal <T> like(String expr) {
		expr=expr.toLowerCase();
		LdapObjectListReal <T> result=new LdapObjectListReal <T>();
		for(T obj: this) {
			if (obj.getName().toLowerCase().indexOf(expr)>=0)
				result.add(obj);
		}
		return result;	
	}
	public T get(String key) {
		T result=dnIndex.get(key);
		if (result!=null)
			return result;
		result=nameIndex.get(key);
		if (result!=null)
			return result;
		key=key.toLowerCase();
		if (result==null) {
			for(T obj: this) {
				if (obj.getName().toLowerCase().indexOf(key)>=0)
					return obj;
			}
		}
		return null;
	}
	
	public LdapObjectList<T> sort() {
		LdapObjectListReal<T> newList=new LdapObjectListReal<T>(this);
		Collections.sort(newList);
		return newList;
	}

	public void diff(LdapObjectList<T> other) {
		LdapObjectList<T> l1=this.sort();
		LdapObjectList<T> l2=other.sort();
		int pos1=0;
		int pos2=0;
		while (pos1<l1.getSize() || pos2<l2.getSize()) {
			if (pos1>=l1.getSize())
				System.out.println("> "+l2.get(pos2++));
			else if (pos2>=l2.getSize())
				System.out.println("< "+l1.get(pos1++));
			else {
				int comp=l1.get(pos1).getName().compareTo(l2.get(pos2).getName());
				if (comp==0) {
					pos1++;
					pos2++;
				}
				else if (comp<0)
					System.out.println("< "+l1.get(pos1++));
				else
					System.out.println("> "+l2.get(pos2++));
			}
		}
	}
	public Object __getattr__(String name) { return get(name); }
	public Object __getitem__(String key)  { return get(key); }
	public Object __getitem__(int index)   { return get(index); }
}
