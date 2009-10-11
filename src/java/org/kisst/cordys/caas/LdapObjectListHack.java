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

import java.util.Iterator;

/**
 * This class works like a list, without being a real java.util.List
 * This hack is necessary, because in groovy the propertyMissing method is never used
 * on objects that inherit from a List.
 * 
 */
public class LdapObjectListHack<T extends LdapObject> implements LdapObjectList<T> {
	private static final long serialVersionUID = 1L;
	private final LdapObjectListReal<T> list;
	
	public LdapObjectListHack() {
		list=new LdapObjectListReal<T>();
	}

	public LdapObjectListHack(LdapObjectList<T> l) {
		list=new LdapObjectListReal<T>(l);
	}
	
	public boolean add(T obj) { return list.add(obj); }
	
	public String toString() { return list.toString(); }
	public T getAt(int index) { return list.getAt(index); }
	public T propertyMissing(String name) {	return get(name); }

	public LdapObjectListHack <T> like(String expr) { return new LdapObjectListHack<T>(list.like(expr)); }

	public T get(int index)  { return list.get(index); }
	public T get(String key) { return list.get(key); }
	
	public LdapObjectList<T> sort() { return new LdapObjectListHack<T>(list.sort()); }
	public void diff(LdapObjectList<T> other) { list.diff(other); }
	
	// methods to be iterable without being a real List
	public int getSize() { return list.size(); }
	public Iterator<T> iterator() { return list.iterator(); }
	
	public Object __getattr__(String name) { return get(name); }
	public Object __getitem__(String key)  { return get(key); }
	public Object __getitem__(int index)   { return get(index); }
}
