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

import org.kisst.cordys.caas.util.XmlNode;

public class Messages implements Iterable<String> {
	private final LinkedList<String> list;
	
	public Messages() {
		this.list=new LinkedList<String>();
	}
	@SuppressWarnings("unchecked")
	public Messages(LinkedList<String> list) {
		this.list=(LinkedList<String>) list.clone();
	}
	public Iterator<String> iterator() { return list.iterator();}
	public int size() { return list.size(); }
	public Messages clone() { return new Messages(list); }
	public boolean empty() { return list.size()==0; } 
	public void add(String msg) {list.add(msg); }
	public void addWarnings(String msg, XmlNode node) {add(msg ,node,"warning"); }
	public void addWarnings(XmlNode node) {add(null,node,"warning"); }
	private void add(String warning, XmlNode node, String type) {
		if (warning!=null)
			add(warning);
		if (node.getAttribute(type)!=null)
			add(node.getAttribute(type));
		for (XmlNode child: node.getChildren())
			if (type.equals(child.getName()))
				add("\t"+child.getAttribute("message"));
	}
	public String toString() {
		StringBuilder result=new StringBuilder("\n");
		for (String s:list)
			result.append(s).append("\n");
		return result.toString();
	}
}
