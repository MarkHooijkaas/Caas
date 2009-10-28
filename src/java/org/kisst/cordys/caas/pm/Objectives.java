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

package org.kisst.cordys.caas.pm;

import java.util.Iterator;
import java.util.LinkedList;

import org.kisst.cordys.caas.support.EntryObjectList;
import org.kisst.cordys.caas.support.LdapObject;

public class Objectives implements Iterable<Objective> {

	private LinkedList<Objective> list=new LinkedList<Objective>();
	
	public Iterator<Objective> iterator() { return list.iterator();}
	public int size() { return list.size(); }
	public boolean empty() { return list.size()==0; } 

	public void add(EntryObjectList<?> target, LdapObject entry) {
		list.add(new Objective(target, entry));
	}
	public void add(String targetName, LdapObject entry) {
		list.add(new Objective(targetName, entry));
	}
	public String toString() {
		StringBuilder result=new StringBuilder();
		//for (String s:list)
		//	result.append(s).append("\n");
		return result.toString();
	}
}
