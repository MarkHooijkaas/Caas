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

package org.kisst.cordys.caas;

import org.jdom.Element;
import org.kisst.cordys.caas.soap.HttpClientCaller;
import org.kisst.cordys.caas.soap.SoapCaller;
import org.kisst.cordys.caas.util.JdomUtil;


public class CordysSystem implements LdapObject {
	private final SoapCaller caller;
	final ObjectRegistry registry;
	public final String dn; 
	public boolean debug=false;
	
	public static CordysSystem connect(String filename) {
		System.setProperty("org.apache.commons.logging.simplelog.log.org.apache.commons.httpclient", "error");
		HttpClientCaller caller = new HttpClientCaller(filename);
		String rootdn= caller.props.getProperty("cordys.rootdn");
		return new CordysSystem(rootdn, caller);
	}

	protected CordysSystem(String dn, SoapCaller caller) {
		this.caller=caller;
		this.dn=dn;
		//this.root=new CordysRoot(this, dn);
		this.registry=new ObjectRegistry(this);
	}
	public CordysSystem getSystem() { return this; }
	public String getDn() { return dn;	}
	public String getName() { return "Cordys";}
	public LdapObject getParent() {return null;	}

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

	

	public NamedObjectList<Organization> getOrg() { return getOrganizations(); }
	public NamedObjectList<Organization> getOrganizations() {
		Element method=new Element("GetOrganizations", CordysLdapObject.nsldap);
		method.addContent(new Element("dn").setText(dn));
		return createObjectsFromEntries(call(method));
	}
	public NamedObjectList<AuthenticatedUser> getAuthenticatedUsers() {
		Element method=new Element("GetAuthenticatedUsers", CordysLdapObject.nsldap);
		method.addContent(new Element("dn").setText(dn));
		method.addContent(new Element("filter").setText("*"));
		return createObjectsFromEntries(call(method));
	}
	
	public NamedObjectList<Isvp> getIsvps() {
		Element method=new Element("GetSoftwarePackages", CordysLdapObject.nsldap);
		method.addContent(new Element("dn").setText(dn));
		return createObjectsFromEntries(call(method));
	}	
	
	@SuppressWarnings("unchecked")
	public <T extends LdapObject> NamedObjectList<T> createObjectsFromEntries(Element response) {
		NamedObjectList<T> result=new NamedObjectList<T>();

		if (response.getName().equals("Envelope"))
			response=response.getChild("Body",null).getChild(null,null);
		for (Object tuple : response.getChildren("tuple", null)) {
			Element elm=((Element) tuple).getChild("old", null).getChild("entry", null);
			LdapObject obj=getObject(elm);
			result.put(obj.getName(),(T) obj);
			//System.out.println(dn);
		}
		return result;
	}
}
