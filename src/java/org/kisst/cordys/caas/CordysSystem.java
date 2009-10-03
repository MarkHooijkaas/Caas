package org.kisst.cordys.caas;

import org.jdom.Element;
import org.kisst.cordys.caas.soap.HttpClientCaller;
import org.kisst.cordys.caas.soap.SoapCaller;
import org.kisst.cordys.caas.util.JdomUtil;


public class CordysSystem {
	private final SoapCaller caller;
	final ObjectRegistry registry;
	public final CordysRoot root;
	public boolean debug=false;
	
	public static CordysRoot connect(String filename) {
		System.setProperty("org.apache.commons.logging.simplelog.log.org.apache.commons.httpclient", "error");
		HttpClientCaller caller = new HttpClientCaller(filename);
		String rootdn= caller.props.getProperty("cordys.rootdn");
		return new CordysSystem(rootdn, caller).root;
	}

	protected CordysSystem(String dn, SoapCaller caller) {
		this.caller=caller;
		this.root=new CordysRoot(this, dn);
		this.registry=new ObjectRegistry(this, root);
	}

	public LdapObject getObject(Element elm) { return registry.getObject(elm); }
	public LdapObject getObject(String dn)   { return registry.getObject(dn); }

	public String call(String input) {
		String soap="<SOAP:Envelope xmlns:SOAP=\"http://schemas.xmlsoap.org/soap/envelope/\"><SOAP:Body>"
			+ input
			+ "</SOAP:Body></SOAP:Envelope>";
		if (debug)
			System.out.println(soap);
		String response = caller.call(soap);
		if (debug || response.indexOf("SOAP:Fault")>0)
			System.out.println(response);
		return response;
	}
	public Element call(Element method) { 
		String xml = JdomUtil.toString(method);
		String response= call(xml);
		Element output=JdomUtil.fromString(response);
		if (output.getName().equals("Envelope"))
			output=output.getChild("Body",null).getChild(null,null);
		return output;
	}

}
