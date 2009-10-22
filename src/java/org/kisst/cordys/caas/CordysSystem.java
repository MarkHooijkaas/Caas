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


public class CordysSystem extends CordysObject {
	private final SoapCaller caller;
	private final CordysObjectCache cache;
	private final String name;
	private final String dn; 

	public final String version;
	public final String build;
	public boolean debug=false;
	public boolean useCache=true;
	//public int displayFormat=0;
	
	public final ChildList<Organization> organizations= new ChildList<Organization>(this, Organization.class);
	public final ChildList<Organization> org = organizations;
	public final ChildList<Organization> o   = organizations;

	public final ChildList<Isvp> isvps= new ChildList<Isvp>(this, Isvp.class);
	public final ChildList<Isvp> isvp = isvps;
	public final ChildList<Isvp> i    = isvps;

	public final ChildList<AuthenticatedUser> authenticatedUsers= new ChildList<AuthenticatedUser>(this, "cn=authenticated users,", AuthenticatedUser.class);
	public final ChildList<AuthenticatedUser> auser = authenticatedUsers;
	public final ChildList<AuthenticatedUser> au    = authenticatedUsers;
	
	@SuppressWarnings("unchecked")
	public final CordysObjectList<SoapProcessor> soapProcessors = new CordysObjectList(this) {
		protected void retrieveList() {
			for (Organization o: organizations) {
				for (SoapProcessor sp: o.soapProcessors)
					grow(sp);
			}
		}
	}; 
	public final CordysObjectList<SoapProcessor> sp = soapProcessors; 
	
	public CordysSystem(String name, SoapCaller caller) {
		this.name=name;
		this.caller=caller;
		XmlNode response=call(new XmlNode("GetVersion",xmlns_monitor));
		XmlNode header=response.getChild("../../Header/header");
		String tmp=header.getChildText("sender/component");
		String key="cn=soap nodes,o=system,";
		this.dn=tmp.substring(tmp.indexOf(key)+key.length());
		this.version=response.getChildText("version");
		this.build=response.getChildText("build");
		this.cache=new CordysObjectCache(this);
	}
	public String toString() { return "CordysSystem("+name+")"; }
	public CordysSystem getSystem() { return this; }
	public String getDn()   { return dn; }
	public String getKey()  { return "ldap:"+dn; }
	public String getName() { return name; }
	public void refresh() {cache.clear(); }
	public boolean useCache() { return useCache; }

	public CordysObject getObject(XmlNode entry) { 
		String newdn=entry.getAttribute("dn");
		String key="ldap:"+newdn;
		//System.out.println("get ["+newdn+"]");
		CordysObject result=cache.findObject(key);
		if (result==null) {
			result=CordysLdapObject.createObject(this, entry);
			cache.remember(result);
		}
		return result;
	}
	//public CordysObject findObject(String key)   { return cache.getObject(key); }
	public CordysObject getObject(String key)   { return cache.getObject(key); }
	public void remove(String dn)   { cache.remove(dn); }

	public String call(String input, String org, String processor) {
		return caller.call(input, debug, org, processor); 
	}
	public String call(String soap) { return caller.call(soap, debug); }
	public XmlNode call(XmlNode method) { return caller.call(method, debug); }

	public void refreshSoapProcessors() {
		XmlNode method=new XmlNode("List", xmlns_monitor);
		XmlNode response=call(method);
		for (XmlNode s: response.getChildren("tuple")) {
			XmlNode workerprocess=s.getChild("old/workerprocess");
			String dn=workerprocess.getChildText("name");
			SoapProcessor obj= (SoapProcessor) getObject("ldap:"+dn);
			obj.setWorkerprocess(workerprocess);
		}
	}

	public Isvp loadIsvp(String filename) {
		filename=filename.trim();
		if (filename.endsWith(".isvp"))
			filename=filename.substring(0,filename.length()-5);
		XmlNode method=new XmlNode("GetISVPackageDefinition", xmlns_isv);
		XmlNode file=method.add("file");
		file.setText(filename);
		file.setAttribute("type", "isvpackage");
		file.setAttribute("detail", "false");
		file.setAttribute("wizardsteps", "true");
		XmlNode details=call(method);
		
		method=new XmlNode("LoadISVPackage", xmlns_isv);
		method.add("url").setText("http://CORDYS42/cordys/wcp/isvcontent/packages/"+filename+".isvp");
		method.add(details.getChild("ISVPackage").detach());
		call(method);
		return null;
	}
	public int compareTo(CordysObject o) { return dn.compareTo(o.getKey()); }
	
	public void diff(CordysObject other, int depth) {
		if (this==other)
			return;
		CordysSystem otherSystem = (CordysSystem) other;
		organizations.diff(otherSystem.organizations, depth);
		isvps.diff(otherSystem.isvps,depth);
	}
	public XmlNode getXml(String key) { return getXml(key, "isv", null); }
	public XmlNode getXml(String key, String version, String organization) {
		XmlNode method=new XmlNode("GetXMLObject", xmlns_xmlstore);
		XmlNode keynode=method.add("key");
		keynode.setText(key);
		if (version !=null)
			keynode.setAttribute("version", version);
		XmlNode response = caller.call(method, debug, organization, null);
		return response.getChild("tuple/old");
	}
}
