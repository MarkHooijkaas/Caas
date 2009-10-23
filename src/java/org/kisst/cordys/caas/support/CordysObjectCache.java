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

import java.util.HashMap;

import org.kisst.cordys.caas.CordysSystem;


public class CordysObjectCache {
	private final HashMap<String, CordysObject> cache=new HashMap<String, CordysObject>();
	private final CordysSystem system;
	
	public CordysObjectCache(CordysSystem system) {
		this.system=system;
		remember(system);
	}

	public void clear() {
		cache.clear();
		remember(system);
	}

	public synchronized CordysObject findObject(String key) { return cache.get(key);}
	public synchronized CordysObject getObject(String key) {
		//System.out.println("get key ["+key+"]");
		CordysObject result=cache.get(key);
		if (result==null) {
			result=createObject(key);
			if (result!=null)
				remember(result);
		}
		return result;
	}
	public void remove(String key) { cache.put(key, null);	}
	public void remember(CordysObject obj) {
		if (obj==null)
			return;
		//System.out.println("remembering ["+obj.getKey()+"]");
		cache.put(obj.getKey(), obj);
		if (system.debug)
			System.out.println("remembering "+obj);
	}
	
	private CordysObject createObject(String key) {
		//System.out.println("create "+key);
		if (key.startsWith("ldap:"))
			return CordysLdapObject.createObject(system, key.substring(5));
		throw new RuntimeException("Unknown keytype "+key);
	}
}
