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

import org.kisst.cordys.caas.soap.SoapCaller;
import org.kisst.cordys.caas.util.XmlNode;


public class CordysSystem implements LdapObject {
	public final static String xmlns_monitor="http://schemas.cordys.com/1.0/monitor";
	private final SoapCaller caller;
	final LdapCache ldapcache;
	public final String dn; 
	public boolean debug=false;
	private final String name;
	public final String version;
	public final String build;
	
	private boolean cache=true; 
	
	public CordysSystem(String name, SoapCaller caller) {
		this.name=name;
		this.caller=caller;
		XmlNode response=soapCall(new XmlNode("GetVersion",xmlns_monitor));
		XmlNode header=response.getChild("../../Header/header");
		String tmp=header.getChildText("sender/component");
		String key="cn=soap nodes,o=system,";
		this.dn=tmp.substring(tmp.indexOf(key)+key.length());
		this.version=response.getChildText("version");
		this.build=response.getChildText("build");
		this.ldapcache=new LdapCache(this);
	}
	public String toString() { return "CordysSystem("+name+")"; }
	public CordysSystem getSystem() { return this; }
	public String getDn() { return dn;	}
	public String getName() { return name;}
	public LdapObject getParent() {return null;	}

	public boolean getCache() { return cache; }
	public void setCache(boolean value) {
		this.cache=value;
		if (! cache)
			ldapcache.clear();
	}

	public LdapObject getObject(XmlNode elm) { return ldapcache.getObject(elm); }
	public LdapObject getObject(String dn)   { return ldapcache.getObject(dn); }
	public void remove(String dn)   { ldapcache.remove(dn); }

	//public Element soapCall(Element method) { return caller.soapCall(method, debug); }
	public String soapCall(String soap) { return caller.soapCall(soap, debug); }
	public XmlNode soapCall(XmlNode method) { return caller.soapCall(method, debug); }


	public NamedObjectList<Organization> getOrg() { return getOrganizations(); }
	public NamedObjectList<Organization> getOrganizations() {
		XmlNode method=new XmlNode("GetOrganizations", CordysLdapObject.xmlns_ldap);
		method.add("dn").setText(dn);
		return getObjectsFromEntries(soapCall(method));
	}
	public NamedObjectList<AuthenticatedUser> getAuthenticatedUsers() {
		XmlNode method=new XmlNode("GetAuthenticatedUsers", CordysLdapObject.xmlns_ldap);
		method.add("dn").setText(dn);
		method.add("filter").setText("*");
		return getObjectsFromEntries(soapCall(method));
	}
	
	public NamedObjectList<Isvp> getIsvps() {
		XmlNode method=new XmlNode("GetSoftwarePackages", CordysLdapObject.xmlns_ldap);
		method.add("dn").setText(dn);
		return getObjectsFromEntries(soapCall(method));
	}

	public NamedObjectList<SoapProcessor> getSp() { return getSoapProcessors(); }
	public NamedObjectList<SoapProcessor> getSoapProcessors() {
		XmlNode method=new XmlNode("List", xmlns_monitor);
		NamedObjectList<SoapProcessor> result=new NamedObjectList<SoapProcessor>();
		XmlNode response=soapCall(method);
		for (XmlNode s: response.getChildren("tuple")) {
			XmlNode workerprocess=s.getChild("old/workerprocess");
			String dn=workerprocess.getChildText("name");
			SoapProcessor obj= (SoapProcessor) getObject(dn);
			obj.setWorkerprocess(workerprocess);
			result.add( obj);
		}
		return result;
	}

	// TODO: this function is the same as found in CordysObject, but is tricky to reuse
	@SuppressWarnings("unchecked")
	protected <T extends LdapObject> NamedObjectList<T> getObjectsFromEntries(XmlNode response) {
		NamedObjectList<T> result=new NamedObjectList<T>();
		if (response.getName().equals("Envelope"))
			response=response.getChild("Body").getChildren().get(0);
		for (XmlNode tuple : response.getChildren("tuple")) {
			XmlNode entry=tuple.getChild("old/entry");
			LdapObject obj=getObject(entry);
			result.add((T) obj);
		}
		return result;
	}
	
	public Isvp loadIsvp(String filename) {
		filename=filename.trim();
		if (filename.endsWith(".isvp"))
			filename=filename.substring(0,filename.length()-5);
		XmlNode method=new XmlNode("GetISVPackageDefinition", Isvp.xmlns_isv);
		XmlNode file=method.add("file");
		file.setText(filename);
		file.setAttribute("type", "isvpackage");
		file.setAttribute("detail", "false");
		file.setAttribute("wizardsteps", "true");
		XmlNode details=soapCall(method);
		
		method=new XmlNode("LoadISVPackage", Isvp.xmlns_isv);
		method.add("url").setText("http://CORDYS42/cordys/wcp/isvcontent/packages/"+filename+".isvp");
		method.add(details.getChild("ISVPackage").detach());
		soapCall(method);
		return null;
	}
	public int compareTo(LdapObject o) { return dn.compareTo(o.getDn()); }
}
