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

package org.kisst.cordys.caas.util;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.jdom.output.XMLOutputter;

public class JdomUtil {
	public static Element fromString(String response) {
		SAXBuilder builder = new SAXBuilder();
		Document doc;
		try {
			doc = builder.build(new StringReader(response));
		}
		catch (JDOMException e) { throw new RuntimeException(e); }
		catch (IOException e) { throw new RuntimeException(e); }
		return doc.getRootElement();
	}
	public static String toString(Element method) {
		XMLOutputter out=new XMLOutputter();
		String xml= out.outputString(method);
		return xml;
	}
	/**
	 * returns a nicely typed list of all children elements with a certain name.
	 * This method is a convenience method to be able to nicely Iterate.
	 * Furthermore it ignores namespace
	 */
	public static List<Element> getChildren(Element e, String tag) {
		ArrayList<Element> result=new ArrayList<Element>();
		for (Object o: e.getChildren(tag,null))
			result.add((Element) o);
		return result;
	}
}
