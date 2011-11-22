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

package org.kisst.cordys.caas.cm;

import org.kisst.cordys.caas.CordysSystem;
import org.kisst.cordys.caas.Organization;
import org.kisst.cordys.caas.util.XmlNode;

public class XmlStoreObjective extends CompositeObjective {
	private class TextObjective extends AbstractObjective {
		private final String string;
		public TextObjective(XmlNode node) {
			super(org.getSystem());
			this.string=node.getAttribute("string");
		}
		public String toString() { return "TextObjective("+string+")"; } 

		protected void myCheck(Ui ui) {
			XmlNode xml = org.getXml(key);
			if (xml==null) {
				message="required xmlstore key "+key+" is not available";
				ui.error(this,message);
				status=ERROR;
				return;
			}
			status=OK;
			//System.out.println(xml.getPretty());
			String text=xml.getText();
			//System.out.println(text);
			message="";
			if (text.indexOf(string)<0) {
				status=ERROR;
				ui.error(this, "xmlstore "+key+" is missing text "+string);
				message+="xmlstore "+key+" is missing text "+string+"\n";
			}
		}
		protected void myConfigure(Ui ui) { /* do nothing, automatically editing not supported */	}
		protected void myPurge(Ui ui) {    /* do nothing, automatically editing  not supported */}

	}
	private final String key;
	private final Organization org;

	public XmlStoreObjective(Organization org, XmlNode node) {
		super("XmlStore("+node.getAttribute("key")+")");
		this.org=org;
		key=node.getAttribute("key");
		for (XmlNode child: node.getChildren()) {
			if ("contains".equals(child.getName()))
				entries.add(new TextObjective(child));
			else
				throw new RuntimeException("Unknown element in xmlstore section "+key+":\n"+child.getPretty());
		}
	}

	public CordysSystem getSystem() { return org.getSystem(); }
}