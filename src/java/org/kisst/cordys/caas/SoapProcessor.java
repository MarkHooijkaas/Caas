package org.kisst.cordys.caas;

import org.jdom.Element;
import org.jdom.Namespace;

public class SoapProcessor extends CordysObject {

	private static final Namespace nsmonitor=Namespace.getNamespace("http://schemas.cordys.com/1.0/monitor");

	protected SoapProcessor(CordysObject parent, String dn) {
		super(parent, dn);
	}

	public void start() {
		Element method=new Element("Start", nsmonitor);
		method.addContent(new Element("dn").setText(dn));
		call(method);
	}
	public void stop() {
		Element method=new Element("Stop", nsmonitor);
		method.addContent(new Element("dn").setText(dn));
		call(method);
	}
	public void restart() {
		Element method=new Element("Restart", nsmonitor);
		method.addContent(new Element("dn").setText(dn));
		call(method);
	}
}
