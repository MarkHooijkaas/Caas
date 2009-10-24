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

import org.kisst.cordys.caas.support.ChildList;
import org.kisst.cordys.caas.support.LdapObjectBase;
import org.kisst.cordys.caas.support.CordysObject;
import org.kisst.cordys.caas.support.LdapObject;

public class MethodSet extends LdapObjectBase {
	public final ChildList<Method> methods= new ChildList<Method>(this, Method.class);
	public final ChildList<Method> method = methods;
	public final ChildList<Method> m      = methods;
	
	public final StringList namespaces= new StringList("labeleduri"); 
	public final StringList ns = namespaces;

	public final StringProperty implementationclass=new StringProperty("implementationclass");
	
	protected MethodSet(LdapObject parent, String dn) {
		super(parent, dn);
	}

	public void createMethod(String name) {
		createInLdap(newEntryXml("", name,"busmethod"));
		methods.clear();
	}

	public void diff(CordysObject other, int depth) {
		if (this==other)
			return;
		MethodSet otherMs=(MethodSet) other;
		methods.diff(otherMs.methods,depth);
	}
}
