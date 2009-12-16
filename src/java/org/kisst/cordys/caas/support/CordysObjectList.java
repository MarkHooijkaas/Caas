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

package org.kisst.cordys.caas.support;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;

import org.kisst.cordys.caas.CordysSystem;


/**
 * This class works like a list, without being a real java.util.List
 * This hack is necessary, because in groovy the propertyMissing method is never used
 * on objects that inherit from a List.
 * 
 */
public abstract class CordysObjectList<T extends CordysObject> extends CordysObject implements  Iterable<T> {
	private static final long serialVersionUID = 1L;
	protected final CordysSystem system;
	private final ArrayList<T> list=new ArrayList<T>();
	private boolean listAvailable=false;
	private final HashMap<String,T> keyIndex=new HashMap<String,T>(); 
	private final HashMap<String,T> nameIndex=new HashMap<String,T>(); 


	protected CordysObjectList(CordysSystem system) {
		this.system=system;
	}
	@Override public String getVarName() { return null; }
	@Override public String getName() { return null; } // TODO

	protected ArrayList<T> fetchList() {
		if (useCache() && listAvailable)
			return list;
		list.clear();
		retrieveList();
		listAvailable=true;
		return list;
	}
	@Override public void myclear() { 
		//log("clearing list "+getKey()+" content "+list);
		if (isListAvailable())
			for (CordysObject o: this)
				o.clear();
		listAvailable=false;
		list.clear(); 
		keyIndex.clear();
		nameIndex.clear();
		super.myclear();
	}
	
	protected abstract void retrieveList();
	protected boolean isListAvailable() { return listAvailable; }
	@Override public CordysSystem getSystem() {return system; }

	protected void grow(T obj) {
		list.add(obj);
		if (obj!=null) {
			keyIndex.put(obj.getKey(), obj);
			nameIndex.put(obj.getName(), obj);
		}
	}
	public Iterator<T> iterator() { fetchList(); return list.iterator(); }

	public T getByName(String name) {
		fetchList();
		return nameIndex.get(name);
	}
	
	public T get(String key) {
		fetchList();
		T result=keyIndex.get(key);
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

	public boolean contains(LdapObject obj) { fetchList(); return list.contains(obj); }
	public T get(int index)  { fetchList(); return list.get(index); }
	public T getAt(int index) { return get(index); } 
	
	
	public int getSize() { fetchList(); return list.size(); }
	@Override public String toString() { return toString("[\n\t",",\n\t","\n]"); }
	public String toString(String begin, String middle, String end) {
		fetchList(); 
		if (this.list.size()==0)
			return "[]";
		if (this.list.size()==1)
			return "["+get(0)+"]";
		StringBuffer result=new StringBuffer(begin);
		boolean first=true;
		for(Object o: this) {
			if (! first)
				result.append(middle);
			else
				first=false;
			result.append(o);
		}
		result.append(end);
		return result.toString();
	}
	
	@SuppressWarnings("unchecked")
	public CordysObjectList <T> like(final String filter) {
		return new CordysObjectList(system) {
			final String expr=filter.toLowerCase();
			@Override protected void retrieveList() {
				for(T obj: CordysObjectList.this) {
					if (obj.getName().toLowerCase().indexOf(expr)>=0)
						grow(obj);
				}
			}
			@Override public String getKey() { return CordysObjectList.this.getKey()+":like("+filter+")";}
		};
	}
	
	@SuppressWarnings("unchecked")
	public CordysObjectList<T> sort() {
		return new CordysObjectList(system) {
			@Override protected void retrieveList() {
				ArrayList<T> tmp=new ArrayList<T>();
				for (T obj : CordysObjectList.this)
					if (obj!=null)
						tmp.add(obj);
				Collections.sort(tmp);
				for (T obj : tmp)
					grow(obj);
				
			}
			@Override public String getKey() { return CordysObjectList.this.getKey()+":sorted";}
		};
	}
	@SuppressWarnings("unchecked")
	@Override public Differences diff(Differences parent, CordysObject other, int depth) {
		Differences diffs=new Differences(parent, getName(), this, other);
		CordysObjectList<T> l1=this.sort();
		CordysObjectList<T> l2=((CordysObjectList<T> )other).sort();
		int pos1=0;
		int pos2=0;
		while (pos1<l1.getSize() || pos2<l2.getSize()) {
			if (pos1>=l1.getSize())
				diffs.onlyIn2(l2.get(pos2++));
			else if (pos2>=l2.getSize())
				diffs.onlyIn1(l1.get(pos1++));
			else {
				int comp=l1.get(pos1).getName().compareTo(l2.get(pos2).getName());
				if (comp==0) {
					if (depth>0)
						diffs.addChildDiffs(l1.get(pos1).diff(diffs,l2.get(pos2), depth-1));
					pos1++;
					pos2++;
				}
				else if (comp<0)
					diffs.onlyIn1(l1.get(pos1++));
				else
					diffs.onlyIn2(l2.get(pos2++));
			}
		}
		return diffs;
	}

	// Groovy specific methods
	public T propertyMissing(String name) {	return get(name); }

	// Jython specific methods
	public Object __getattr__(String name) { return get(name); }
	public Object __getitem__(String key)  { return get(key); }
	public Object __getitem__(int index)   { return get(index); }
}
