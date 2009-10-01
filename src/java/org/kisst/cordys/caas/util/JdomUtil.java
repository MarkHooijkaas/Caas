package org.kisst.cordys.caas.util;

import java.io.IOException;
import java.io.StringReader;

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
}
