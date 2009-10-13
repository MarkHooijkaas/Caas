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
	public int displayFormat=0;
	
	private boolean cache=true;
	private LdapObjectList<Organization> cachedOrganizations; 
	
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
	public void clear() {
		cachedOrganizations=null;
		ldapcache.clear();
	}

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


	public LdapObjectList<Organization> getOrg() { 	return getOrganizations();	}
	public LdapObjectList<Organization> getOrganizations() {
		if (cachedOrganizations==null || ! getCache()) {
			XmlNode method=new XmlNode("GetOrganizations", CordysLdapObject.xmlns_ldap);
			method.add("dn").setText(dn);
			cachedOrganizations = new LdapObjectList<Organization>(this,method);
		}
		return cachedOrganizations;
	}

	public LdapObjectList<AuthenticatedUser> getAuthuser() {
		return getAuthenticatedUsers();
	}
	public LdapObjectList<AuthenticatedUser> getAuthenticatedUsers() {
		XmlNode method=new XmlNode("GetAuthenticatedUsers", CordysLdapObject.xmlns_ldap);
		method.add("dn").setText(dn);
		method.add("filter").setText("*");
		return new LdapObjectList<AuthenticatedUser>(this,method);
	}
	
	public LdapObjectList<Isvp> getIsvp() {
		return getIsvps();
	}
	public LdapObjectList<Isvp> getIsvps() {
		XmlNode method=new XmlNode("GetSoftwarePackages", CordysLdapObject.xmlns_ldap);
		method.add("dn").setText(dn);
		return new LdapObjectList<Isvp>(this,method);
	}

	public LdapObjectList<SoapProcessor> getSp() { 
		return getSoapProcessors(); 
	}
	public LdapObjectList<SoapProcessor> getSoapProcessors() {
		LdapObjectList<SoapProcessor> result=new LdapObjectList<SoapProcessor>();
		for (Organization o: getOrganizations()) {
			for (SoapProcessor sp: o.getSoapProcessors())
				result.add(sp);
		}
		return result;
	}
	public LdapObjectList<SoapProcessor> refreshSoapProcessors() {
		XmlNode method=new XmlNode("List", xmlns_monitor);
		LdapObjectList<SoapProcessor> result=new LdapObjectList<SoapProcessor>();
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
	
	public void deepdiff(LdapObject other) { diff(other,100); }
	public void diff(LdapObject other) { diff(other,0); }
	public void diff(LdapObject other, int depth) {
		if (this==other)
			return;
		CordysSystem otherSystem = (CordysSystem) other;
		getOrganizations().diff(otherSystem.getOrganizations(), depth);
		getIsvps().diff(otherSystem.getIsvps(),depth);
	}

}
