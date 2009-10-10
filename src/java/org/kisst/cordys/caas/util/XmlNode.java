package org.kisst.cordys.caas.util;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.Namespace;
import org.jdom.input.SAXBuilder;
import org.jdom.output.XMLOutputter;

public class XmlNode {
	private final Element element;
	
	public XmlNode(Element element) { this.element = element; }
	public XmlNode(String name, Namespace ns) {	this.element=new Element(name, ns);	}
	public XmlNode(String name, String namespace) {
		this.element=new Element(name, Namespace.getNamespace(namespace));
	}
	public XmlNode(String xml) {
		if (! xml.trim().startsWith("<")) {
			//xml is just an element name
			this.element=new Element(xml);
			return;
		}
		SAXBuilder builder = new SAXBuilder();
		Document doc;
		try {
			doc = builder.build(new StringReader(xml));
		}
		catch (JDOMException e) { throw new RuntimeException(e); }
		catch (IOException e) { throw new RuntimeException(e); }
		this.element =doc.getRootElement();

	}
	
	public XmlNode clone() { return new XmlNode((Element) this.element.clone()); }
	public String getName() {  return element.getName(); }
	public String getNamespace() {  return element.getNamespace().getURI(); }
	public String getText() { return element.getText(); }
	public String getAttribute(String name) { return element.getAttribute(name).getValue(); }
	public XmlNode getChild(String path) { return (XmlNode) get(path); }
	public XmlNode getParent() { return new XmlNode(element.getParentElement()); }
	public Object  propertyMissing(String name) { return get(name); }
	public Element getJdomElement() { return element; }
	public String getChildText(String path) {
		XmlNode child=getChild(path);
		if (child==null)
			return null;
		return child.getText();
	}

	public List<XmlNode> getChildren() {
		List l = element.getChildren();
		ArrayList<XmlNode> result=new ArrayList<XmlNode>(l.size());
		for (Object o: l)
			result.add(new XmlNode((Element) o));
		return result;
	}
	public List<XmlNode> getChildren(String name) {
		List l = element.getChildren(name, null);
		ArrayList<XmlNode> result=new ArrayList<XmlNode>(l.size());
		for (Object o: l)
			result.add(new XmlNode((Element) o));
		return result;
	}
	
	public Object get(String path) {
		String[] parts=path.split("/");
		Element e=element;
		for (String part:parts) {
			if (part.equals(".."))
				e=e.getParentElement();
			else if (part.equals("text()"))
				return e.getText();
			else if (part.startsWith("@"))
				return e.getAttribute(part.substring(1)).getValue();
			else
				e=e.getChild(part,null);
			if (e==null)
				return null;
		}
		return new XmlNode(e);
	}
	
	public String toString() {
		XMLOutputter out=new XMLOutputter();
		String xml= out.outputString(element);
		return xml;
	}
	public XmlNode setAttribute(String name, String value) {
		element.setAttribute(name, value);
		return this;
	}
	public XmlNode createChild(String name, String namespace) {
		Element child=new Element(name, element.getNamespace());
		element.addContent(child);
		return new XmlNode(child);
	}
	public XmlNode add(String name) { return createChild(name); }

	public XmlNode createChild(String name) {
		Element child=new Element(name, element.getNamespace());
		element.addContent(child);
		return new XmlNode(child);
	}
	public XmlNode detach() { this.element.detach(); return this; }
	public void setText(String text) { element.setText(text); }
	public void add(XmlNode node) { element.addContent(node.element); }
	public void remove(XmlNode e) { element.getChildren().remove(e.element); }
}

