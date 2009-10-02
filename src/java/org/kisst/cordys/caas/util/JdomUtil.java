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
