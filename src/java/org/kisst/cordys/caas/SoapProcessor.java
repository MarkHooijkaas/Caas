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

import org.jdom.Element;
import org.jdom.Namespace;
import org.kisst.cordys.caas.util.JdomUtil;

public class SoapProcessor extends CordysLdapObject {

	private static final Namespace nsmonitor=Namespace.getNamespace("http://schemas.cordys.com/1.0/monitor");

	protected SoapProcessor(LdapObject parent, String dn) {
		super(parent, dn);
	}

	public void start() {
		Element method=new Element("Start", nsmonitor);
		method.addContent(new Element("dn").setText(dn));
		soapCall(method);
	}
	public void stop() {
		Element method=new Element("Stop", nsmonitor);
		method.addContent(new Element("dn").setText(dn));
		soapCall(method);
	}
	public void restart() {
		Element method=new Element("Restart", nsmonitor);
		method.addContent(new Element("dn").setText(dn));
		soapCall(method);
	}
	public String getComputer() {
		return getEntry().getChild("computer",null).getChildText("string",null);
	}
	public boolean getAutomatic() {
		String s=getEntry().getChild("automaticstart",null).getChildText("string",null);
		return s.equals("true");
	}
	public Element config() {
		String s=getEntry().getChild("bussoapprocessorconfiguration",null).getChildText("string",null);
		return JdomUtil.fromString(s);
	}
	public boolean getUseSystemLogPolicy() {
		Element e=config().getChild("loggerconfiguration",null);
		if (e==null)
			return true; // defautl is true
		return e.getChildText("systempolicy",null).equals("true");
	}
}
