package org.kisst.cordys.caas;

import java.util.List;

import org.jdom.Element;
import org.kisst.cordys.caas.soap.HttpClientCaller;
import org.kisst.cordys.caas.soap.SoapCaller;


public class CordysSystem  extends Organization {
	private final SoapCaller caller;

	public static CordysSystem connect() {
		System.setProperty("org.apache.commons.logging.simplelog.log.org.apache.commons.httpclient", "error");
		HttpClientCaller caller = new HttpClientCaller("user.properties");
		String rootdn= caller.props.getProperty("cordys.rootdn");
		return new CordysSystem(rootdn, caller);
	}

	public CordysSystem(String dn, SoapCaller caller) {
		super(null,dn);
		this.caller=caller;
	}
	public CordysSystem getSystem() { return this; }
	
	public String call(String input) {
		String soap="<SOAP:Envelope xmlns:SOAP=\"http://schemas.xmlsoap.org/soap/envelope/\"><SOAP:Body>"
			+ input
			+ "</SOAP:Body></SOAP:Envelope>";
		//System.out.println(soap);
		String response = caller.call(soap);
		if (response.indexOf("SOAP:Fault")>0)
			System.out.println(response);
		return response;
	}
	
	public List<Organization> getOrganizations() {
		Element method=new Element("GetOrganizations", nsldap);
		method.addContent(new Element("dn").setText(dn));
		return createObjects(call(method), Organization.class);
	}
	public List<AuthenticatedUser> getAuthenticatedUsers() {
		Element method=new Element("GetAuthenticatedUsers", nsldap);
		method.addContent(new Element("dn").setText(dn));
		method.addContent(new Element("filter").setText("*"));
		return createObjects(call(method), AuthenticatedUser.class);
	}
}
