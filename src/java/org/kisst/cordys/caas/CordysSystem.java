package org.kisst.cordys.caas;

import java.util.List;

import org.kisst.cordys.caas.soap.HttpClientCaller;
import org.kisst.cordys.caas.soap.SoapCaller;


public class CordysSystem  extends LdapObject{
	private final SoapCaller caller;

	public static CordysSystem connect() {
		System.setProperty("org.apache.commons.logging.simplelog.log.org.apache.commons.httpclient", "error");
		HttpClientCaller caller = new HttpClientCaller("user.properties");
		String rootdn= caller.props.getProperty("cordys.rootdn");
		return new CordysSystem(rootdn, caller);
	}

	public CordysSystem(String dn, SoapCaller caller) {
		super(dn);
		this.caller=caller;
	}
	public CordysSystem getSystem() { return this; }
	
	public String call(String input) {
		String soap="<SOAP:Envelope xmlns:SOAP=\"http://schemas.xmlsoap.org/soap/envelope/\"><SOAP:Body>"
			+ input
			+ "</SOAP:Body></SOAP:Envelope>";
		//System.out.println(soap);
		String response = caller.call(soap);
		//System.out.println(response);
		return response;
	}
	
	public List<Organization> getOrganizations() {
		return getChildren(this, "GetOrganizations", Organization.class);
	}
}
