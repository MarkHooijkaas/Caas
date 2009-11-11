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

import java.util.LinkedList;

public class Differences {
	private final static class AttributeDiff { String name; Object v1; Object v2;} 
	private final static class ChildDiff { @SuppressWarnings("unused") String name; Differences diffs;}
	//private final Differences parent;
	private final String name1;
	private final String name2;
	//private final CordysObject o1;
	//private final CordysObject o2;
	private final LinkedList<Object> onlyIn1= new LinkedList<Object>();
	private final LinkedList<Object> onlyIn2= new LinkedList<Object>();
	private final LinkedList<AttributeDiff> attribs= new LinkedList<AttributeDiff>();
	private final LinkedList<ChildDiff> childs = new LinkedList<ChildDiff>();

	public Differences(CordysObject o1, CordysObject o2) { this(null, "", o1,o2);}
	public Differences(Differences parent, String name, CordysObject o1, CordysObject o2) {
		//this.parent=parent;
		this.name1=o1.getVarName();
		this.name2=o2.getVarName();
		//this.o1=o1;
		//this.o2=o2;
	}
	
	public boolean emtpy() {
		return onlyIn1.size()==0 && onlyIn2.size()==0 && attribs.size()==0 && childs.size()==0;
	}
	
	public void onlyIn1(Object only) { onlyIn1.add(only); }
	public void onlyIn2(Object only) { onlyIn2.add(only); }
	
	public void attributeDiffers(String name, Object v1, Object v2) {
		AttributeDiff d=new AttributeDiff();
		d.name=name;
		d.v1=v1;
		d.v2=v2;
		attribs.add(d);
	}
	public void addChildDiffs(Differences diffs) {
		if (diffs.emtpy())
			return;
		ChildDiff d=new ChildDiff();
		d.name=null;
		d.diffs=diffs;
		childs.add(d);
	}
	
	@Override public String toString() { return toString(""); }
	public String toString(String prefix) {
		StringBuilder result=new StringBuilder();
		toString(result,prefix);
		return result.toString();
	}
	public void toString(StringBuilder result, String prefix) {
		if (onlyIn1.size()>0) {
			result.append(prefix+"Only in "+name1+":\n");
			for (Object o: onlyIn1)
				result.append(prefix+"\t< "+o+"\n");
		}
		if (onlyIn2.size()>0) {
			result.append(prefix+"Only in "+name2+":\n");
			for (Object o: onlyIn2)
				result.append(prefix+"\t> "+o+"\n");
		}
		for (AttributeDiff d: attribs) {
			result.append(prefix+"DIFF field "+d.name+" differs between "+name1+" and "+name2+"\n");
			result.append(prefix+"\t< "+name1+"."+d.name+"="+d.v1+"\n");
			result.append(prefix+"\t> "+name2+"."+d.name+"="+d.v2+"\n");
		}
		for (ChildDiff c: childs)
			c.diffs.toString(result, prefix+"\t");
	}
}
