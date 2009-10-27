package org.kisst.cordys.caas.pm;

import java.util.Iterator;
import java.util.LinkedList;

import org.kisst.cordys.caas.util.XmlNode;

public class Messages implements Iterable<String> {
	private LinkedList<String> list=new LinkedList<String>();
	
	public Iterator<String> iterator() { return list.iterator();}
	public int size() { return list.size(); }
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
				add(child.getAttribute("message"));
	}
	public String toString() {
		StringBuilder result=new StringBuilder();
		for (String s:list)
			result.append(s).append("\n");
		return result.toString();
	}
}
