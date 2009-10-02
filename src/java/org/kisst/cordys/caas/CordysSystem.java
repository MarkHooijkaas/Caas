package org.kisst.cordys.caas;

import org.jdom.Element;
import org.kisst.cordys.caas.soap.HttpClientCaller;
import org.kisst.cordys.caas.soap.SoapCaller;
import org.kisst.cordys.caas.util.DynamicProperty;


public class CordysSystem  extends Organization {
	private final SoapCaller caller;
	final ObjectRegistry registry=new ObjectRegistry(this);
	
	public final DynamicProperty<Organization> org;
	
	public static CordysSystem connect(String filename) {
		System.setProperty("org.apache.commons.logging.simplelog.log.org.apache.commons.httpclient", "error");
		HttpClientCaller caller = new HttpClientCaller(filename);
		String rootdn= caller.props.getProperty("cordys.rootdn");
		return new CordysSystem(rootdn, caller);
	}

	protected CordysSystem(String dn, SoapCaller caller) {
		super(null,dn);
		this.caller=caller;
		org=new DynamicProperty<Organization>(this, Organization.class, "o=", dn);
	}

	public CordysObject getObject(Element elm) { return registry.getObject(elm); }
	public CordysObject getObject(String dn)   { return registry.getObject(dn); }

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
	

	public Organization getOrganization(String name) {
		// TODO: validate if it really exists
		return new Organization(this, "o="+name+","+dn);
	}

	public NamedObjectList<Organization> getOrganizations() {
		Element method=new Element("GetOrganizations", nsldap10);
		method.addContent(new Element("dn").setText(dn));
		return createObjects(call(method));
	}
	public NamedObjectList<AuthenticatedUser> getAuthenticatedUsers() {
		Element method=new Element("GetAuthenticatedUsers", nsldap10);
		method.addContent(new Element("dn").setText(dn));
		method.addContent(new Element("filter").setText("*"));
		return createObjects(call(method));
	}
	
	public NamedObjectList<Isvp> getIsvps() {
		Element method=new Element("GetSoftwarePackages", nsldap10);
		method.addContent(new Element("dn").setText(dn));
		return createObjects(call(method));
	}
}
