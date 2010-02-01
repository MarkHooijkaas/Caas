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

import java.util.HashMap;

import org.kisst.cordys.caas.main.Environment;
import org.kisst.cordys.caas.soap.SoapCaller;
import org.kisst.cordys.caas.support.ChildList;
import org.kisst.cordys.caas.support.CordysObject;
import org.kisst.cordys.caas.support.CordysObjectList;
import org.kisst.cordys.caas.support.LdapObject;
import org.kisst.cordys.caas.support.LdapObjectBase;
import org.kisst.cordys.caas.support.XmlObjectList;
import org.kisst.cordys.caas.util.XmlNode;


public class CordysSystem extends LdapObject {
	private final SoapCaller caller;
	private final HashMap<String, LdapObject> ldapcache=new HashMap<String, LdapObject>();
	private final String name;
	private final String dn; 
	private final Environment env;

	public final String version;
	public final String build;
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
	
	public final XmlObjectList<Connector> connectors= new XmlObjectList<Connector>(this, "/Cordys/WCP/Application Connector", null);
	public final XmlObjectList<Connector> connector = connectors;
	public final XmlObjectList<Connector> conn = connectors;
	
	@SuppressWarnings("unchecked")
	public final CordysObjectList<SoapProcessor> soapProcessors = new CordysObjectList(this) {
		@Override protected void retrieveList() {
			for (Organization o: organizations) {
				for (SoapProcessor sp: o.soapProcessors)
					grow(sp);
			}
		}
		@Override public String getKey() { return getKey()+":SoapProcessors"; }
	}; 
	public final CordysObjectList<SoapProcessor> sp = soapProcessors; 
	@Override public String getVarName() { return name; }

	
	@SuppressWarnings("unchecked")
	public final CordysObjectList<Machine> machines = new CordysObjectList(this) {
		@Override protected void retrieveList() {
			for (SoapProcessor sp: soapProcessors) {
				if (sp.getName().indexOf("monitor")>=0)
					grow(new Machine(sp));
			}
		}
		@Override public String getKey() { return getKey()+":machine"; }
	}; 
	public final CordysObjectList<Machine> machine = machines;
		
	
	public CordysSystem(String name, SoapCaller caller) {
		super();
		this.env=Environment.get();
		this.name=name;
		this.caller=caller;
		XmlNode response=call(new XmlNode("GetInstallationInfo",xmlns_monitor));
		String tmp=response.getChildText("tuple/old/soapprocessorsinfo/processor/dn");
		String key="cn=soap nodes,o=system,";
		this.dn=tmp.substring(tmp.indexOf(key)+key.length());
		this.version=response.getChildText("tuple/old/buildinfo/version");
		this.build=response.getChildText("tuple/old/buildinfo/build");
		rememberLdap(this);
	}

	@Override public String toString() { return "CordysSystem("+name+")"; }
	public Environment getEnv() { return env;}
	@Override public CordysSystem getSystem() { return this; }
	@Override public String getDn()   { return dn; }
	@Override public String getKey()  { return "ldap:"+dn; }
	@Override public String getName() { return name; }
	@Override public void myclear() {
		// It is not necessary to clear the cache, because that is just an index,
		// and guarantees that objects are never created twice.
		// Instead just the content of the objects is cleared.
		//ldapcache.clear(); rememberLdap(this);
	}

	@Override public boolean useCache() { return useCache; }

	public LdapObject seekLdap(String dn) { return ldapcache.get(dn); }
	public synchronized LdapObject getLdap(String dn) {
		//System.out.println("get key ["+key+"]");
		LdapObject result=ldapcache.get(dn);
		if (result!=null)
			return result;
		result=LdapObjectBase.createObject(this, dn);
		rememberLdap(result);
		return result;
		
	}

	public LdapObject getLdap(XmlNode entry) { 
		String dn=entry.getAttribute("dn");
		//System.out.println("get ["+newdn+"]");
		LdapObject result=ldapcache.get(dn);
		if (result!=null)
			return result;
		result=LdapObjectBase.createObject(this, entry);
		rememberLdap(result);
		return result;
	}
	private void rememberLdap(LdapObject obj) {
		if (obj==null)
			return;
		//System.out.println("remembering ["+obj.getKey()+"]");
		ldapcache.put(obj.getDn(), obj);
	}
	public void removeLdap(String dn)   { ldapcache.remove(dn); }

	public String call(String input, String org, String processor) {
		return caller.call(input, org, processor); 
	}
	public String call(String soap) { return caller.call(soap); }
	@Override public XmlNode call(XmlNode method) { return caller.call(method); }

	public void refreshSoapProcessors() {
		for (Machine m: machines)
			m.refreshSoapProcessors();
	}

	public void loadIsvp(String filename) {
		for (Machine m: machines)
			m.loadIsvp(filename);
		isvp.clear();
	}
	
	@Override public int compareTo(CordysObject o) { return dn.compareTo(o.getKey()); }
	
	public XmlNode getXml(String key) { return getXml(key, "isv", null); }
	public XmlNode getXml(String key, String version, String organization) {
		XmlNode method=new XmlNode("GetXMLObject", xmlns_xmlstore);
		XmlNode keynode=method.add("key");
		keynode.setText(key);
		if (version !=null)
			keynode.setAttribute("version", version);
		XmlNode response = caller.call(method, organization, null);
		return response.getChild("tuple/old");
	}
	
	@SuppressWarnings("unchecked")
	public CordysObjectList<LdapObject> seek(final Role target) {
		return new CordysObjectList(getSystem()) {
			@Override protected void retrieveList() {
				for (Organization org: organizations) {
					for (LdapObject obj: org.seek(target))
						grow(obj);
				}
			}
			@Override public String getKey() { return CordysSystem.this.getKey()+":seek("+target+")";}
		};
	}

	@SuppressWarnings("unchecked")
	public CordysObjectList<SoapNode> seek(final MethodSet target) {
		return new CordysObjectList(getSystem()) {
			@Override protected void retrieveList() {
				for (Organization org: organizations) {
					for (LdapObject obj: org.seek(target))
						grow(obj);
				}
			}
			@Override public String getKey() { return CordysSystem.this.getKey()+":seek("+target+")";}
		};
	}
}
