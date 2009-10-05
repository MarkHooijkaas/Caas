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
import org.jdom.Namespace;
import org.kisst.cordys.caas.soap.HttpClientCaller;
import org.kisst.cordys.caas.soap.SoapCaller;


public class CordysSystem implements LdapObject {
	public final static Namespace nsldap=Namespace.getNamespace("http://schemas.cordys.com/1.0/ldap");
	
	public static CordysSystem connect(String filename) {
		//System.setProperty("org.apache.commons.logging.simplelog.log.org.apache.commons.httpclient", "error");
		HttpClientCaller caller = new HttpClientCaller(filename);
		String rootdn= caller.props.getProperty("cordys.rootdn");
		return new CordysSystem(rootdn, caller);
	}

	
	private final SoapCaller caller;
	final LdapCache ldapcache;
	public final String dn; 
	public boolean debug=false;

	protected CordysSystem(String dn, SoapCaller caller) {
		this.caller=caller;
		this.dn=dn;
		this.ldapcache=new LdapCache(this);
	}
	public CordysSystem getSystem() { return this; }
	public String getDn() { return dn;	}
	public String getName() { return "Cordys";}
	public LdapObject getParent() {return null;	}

	public LdapObject getObject(Element elm) { return ldapcache.getObject(elm); }
	public LdapObject getObject(String dn)   { return ldapcache.getObject(dn); }
	public void remove(String dn)   { ldapcache.remove(dn); }

	public Element soapCall(Element method) { return caller.soapCall(method, debug); }
	

	public NamedObjectList<Organization> getOrg() { return getOrganizations(); }
	public NamedObjectList<Organization> getOrganizations() {
		Element method=new Element("GetOrganizations", CordysSystem.nsldap);
		method.addContent(new Element("dn").setText(dn));
		return getObjectsFromEntries(soapCall(method));
	}
	public NamedObjectList<AuthenticatedUser> getAuthenticatedUsers() {
		Element method=new Element("GetAuthenticatedUsers", CordysSystem.nsldap);
		method.addContent(new Element("dn").setText(dn));
		method.addContent(new Element("filter").setText("*"));
		return getObjectsFromEntries(soapCall(method));
	}
	
	public NamedObjectList<Isvp> getIsvps() {
		Element method=new Element("GetSoftwarePackages", CordysSystem.nsldap);
		method.addContent(new Element("dn").setText(dn));
		return getObjectsFromEntries(soapCall(method));
	}

	public NamedObjectList<SoapProcessor> getSp() { return getSoapProcessors(); }
	public NamedObjectList<SoapProcessor> getSoapProcessors() {
		NamedObjectList<SoapProcessor> result= new NamedObjectList<SoapProcessor>();
		for (Object o: getOrganizations() ) {
			for (Object sn : ((Organization) o).getSoapNodes()) {
				for (Object sp : ((SoapNode) sn).getSoapProcessors()) {
					result.put(((SoapProcessor)sp).getDn(), (SoapProcessor) sp); // using dn, to prevent duplicate names over organizations
				}
			}
		}
		return result;
	}

	// TODO: this function is the same as found in CordysObject, but is tricky to reuse
	@SuppressWarnings("unchecked")
	protected <T extends LdapObject> NamedObjectList<T> getObjectsFromEntries(Element response) {
		NamedObjectList<T> result=new NamedObjectList<T>();
		if (response.getName().equals("Envelope"))
			response=response.getChild("Body",null).getChild(null,null);
		for (Object tuple : response.getChildren("tuple", null)) {
			Element elm=((Element) tuple).getChild("old", null).getChild("entry", null);
			LdapObject obj=getObject(elm);
			result.put(obj.getName(),(T) obj);
		}
		return result;
	}
	
	public Isvp loadIsvp(String filename) {
		filename=filename.trim();
		if (filename.endsWith(".isvp"))
			filename=filename.substring(0,filename.length()-5);
		Element method=new Element("GetISVPackageDefinition", Isvp.xmlns_isv);
		Element file=new Element("file", Isvp.xmlns_isv).setText(filename);
		file.setAttribute("type", "isvpackage");
		file.setAttribute("detail", "false");
		file.setAttribute("wizardsteps", "true");
		method.addContent(file);
		Element details=soapCall(method);
		
		method=new Element("LoadISVPackage", Isvp.xmlns_isv);
		method.addContent(new Element("url", Isvp.xmlns_isv).setText("http://CORDYS42/cordys/wcp/isvcontent/packages/"+filename+".isvp"));
		method.addContent(details.getChild("ISVPackage", null).detach());
		soapCall(method);
		return null;
	}
}
