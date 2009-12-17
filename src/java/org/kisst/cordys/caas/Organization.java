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

import org.kisst.cordys.caas.main.Environment;
import org.kisst.cordys.caas.support.ChildList;
import org.kisst.cordys.caas.support.CordysObjectList;
import org.kisst.cordys.caas.support.LdapObject;
import org.kisst.cordys.caas.support.LdapObjectBase;
import org.kisst.cordys.caas.util.XmlNode;



public class Organization extends LdapObjectBase {
	private static final Environment env=Environment.get();
	
	public final ChildList<User> users= new ChildList<User>(this, "cn=organizational users,", User.class);
	public final ChildList<User> user = users;
	public final ChildList<User> u    = users;

	public final ChildList<Role> roles= new ChildList<Role>(this, "cn=organizational roles,", Role.class);
	public final ChildList<Role> role= roles;
	public final ChildList<Role> r   = roles;

	public final ChildList<MethodSet> methodSets= new ChildList<MethodSet>(this, "cn=method sets,", MethodSet.class);
	public final ChildList<MethodSet> ms = methodSets;

	public final ChildList<SoapNode> soapNodes= new ChildList<SoapNode>(this, "cn=soap nodes,", SoapNode.class);
	public final ChildList<SoapNode> sn = soapNodes;
	
	// These fields must be initialized in constructor because system is only known there
	public final CordysObjectList<SoapProcessor> soapProcessors; 
	public final CordysObjectList<SoapProcessor> sp; 
	
	public final ProcessModel.List processes=new ProcessModel.List(this); 
	public final ProcessModel.List proc=processes; 

	
	@SuppressWarnings("unchecked")
	protected Organization(LdapObject parent, String dn) {
		super(parent, dn);
		soapProcessors = new CordysObjectList(parent.getSystem()) {
			@Override protected void retrieveList() {
				for (SoapNode sn: soapNodes) {
					for (SoapProcessor sp: sn.soapProcessors)
						grow(sp);
				}
			}
			@Override public String getKey() { return "SoapProcessors:"+getDn(); }
		}; 
		sp = soapProcessors;
	}

	@Override protected String prefix() { return "org"; }

	public String call(String input) { return getSystem().call(input, getDn(), null); }

	@Override protected void preDeleteHook() {
		for (SoapProcessor sp: soapProcessors)
			sp.stop();
	}

	public void createMethodSet(String name, String namespace, String implementationclass) {
		XmlNode newEntry=newEntryXml("cn=method sets,", name,"busmethodset");
		newEntry.add("labeleduri").add("string").setText(namespace);
		newEntry.add("implementationclass").add("string").setText(implementationclass);
		createInLdap(newEntry);
		methodSets.clear();
	}

	public void createUser(String name, AuthenticatedUser au) {
		XmlNode newEntry=newEntryXml("cn=organizational users,", name,"busorganizationaluser","busorganizationalobject");
		newEntry.add("authenticationuser").add("string").setText(au.getDn());
		newEntry.add("menu");
		newEntry.add("toolbar");
		newEntry.add("role").add("string").setText("cn=everyoneIn"+getName()+",cn=organizational roles,"+getDn());
		createInLdap(newEntry);
		users.clear();
	}

	public void createRole(String name) {
		XmlNode newEntry=newEntryXml("cn=organizational roles,", name,"busorganizationalrole","busorganizationalobject");
		newEntry.add("description").add("string").setText(name);
		newEntry.add("menu");
		newEntry.add("toolbar");
		newEntry.add("role").add("string").setText("cn=everyoneIn"+getName()+",cn=organizational roles,"+getDn());
		createInLdap(newEntry);
		roles.clear();
	}
	
	public void createSoapNode(String name, XmlNode config, MethodSet ... allms ) {
		XmlNode newEntry=newEntryXml("cn=soap nodes,", name,"bussoapnode");
		newEntry.add("description").add("string").setText(name);
		XmlNode bms = newEntry.add("busmethodsets");
		XmlNode luri= newEntry.add("labeleduri");
		for (MethodSet ms: allms) {
			bms.add("string").setText(ms.getDn());
			for (String ns:ms.namespaces.get())
				luri.add(ns);
		}
		newEntry.add("bussoapnodeconfiguration").add("string").setText(config.toString());
		createInLdap(newEntry);
		soapNodes.clear();
	}
	
	public void createSoapNode(String name, MethodSet ... allms ) {		
		XmlNode routing=new XmlNode("routing");
		routing.setAttribute("ui_algorithm", "failover");
		routing.setAttribute("ui_type", "loadbalancing");
		routing.add("numprocessors").setText("100000");
		routing.add("algorithm").setText("algorithm");
		createSoapNode(name, routing, allms);
	}	
	
	public XmlNode getXml(String key, String version) { return getSystem().getXml(key, version, getDn()); }
	public XmlNode getXml(String key) { return getSystem().getXml(key, "organization", getDn()); }
	
	
	public XmlNode deduct(Isvp isvp) { return deduct(isvp, isvp.getName());	}
	public XmlNode deduct(String isvpName) { return deduct(this, isvpName);	}

	public XmlNode createTemplate(String targetIsvpName) {
		XmlNode result=new XmlNode("org");
		//result.setAttribute("isvp", isvpName);
		result.setAttribute("org", this.getName());
		for (SoapNode sn : this.soapNodes) {
			XmlNode node=result.add("soapnode");
			node.setAttribute("name", sn.getName());
			node.add("bussoapnodeconfiguration").add(sn.config.getXml().clone());
			for (MethodSet ms: sn.methodSets) {
				XmlNode child=node.add("ms");
				child.setAttribute("name", ms.getName());
				String isvpName=null;
				if (ms.getParent() instanceof Organization)
					isvpName=targetIsvpName;
				else
					isvpName=ms.getParent().getName();
				if (isvpName!=null)
					child.setAttribute("isvp", isvpName);
			}
			for (SoapProcessor sp: sn.soapProcessors) {
				XmlNode child=node.add("sp");
				child.setAttribute("name", sp.getName());
				child.setAttribute("automatic", ""+sp.automatic.getBool());
				child.add("bussoapprocessorconfiguration").add(sp.config.getXml().clone());
			}
		}
		for (User u : this.users) {
			XmlNode node=result.add("user");
			node.setAttribute("name", u.getName());
			node.setAttribute("au", u.au.getRef().getName());
			for (Role r: u.roles) {
				String isvpName=null;
				if (r.getParent() instanceof Organization) {
					if (r.getName().equals("everyoneIn"+getName()))
						continue;
					isvpName=targetIsvpName;
				}
				else
					isvpName=r.getParent().getName();
				XmlNode child=node.add("role");
				child.setAttribute("name", r.getName());
				if (isvpName!=null)
					child.setAttribute("isvp", isvpName);
			}
		}
		for (Role rr : this.roles) {
			XmlNode node=result.add("role");
			node.setAttribute("name", rr.getName());
			for (Role r: rr.roles) {
				String isvpName=null;
				if (r.getParent() instanceof Organization) {
					if (r.getName().equals("everyoneIn"+getName()))
						continue;
					isvpName=targetIsvpName;
				}
				else
					isvpName=r.getParent().getName();
				XmlNode child=node.add("role");
				child.setAttribute("name", r.getName());
				if (isvpName!=null)
					child.setAttribute("isvp", isvpName);
			}
		}
		return result;
	}

	public void createFromTemplate(XmlNode template) {
		for (XmlNode node : template.getChildren()){
			if (node.getName().equals("soapnode")) {
				String name=node.getAttribute("name");
				if (soapNodes.getByName(name)==null) {
					env.info("creating soapnode "+name);
					createSoapNode(name, node.getChild("bussoapnodeconfiguration").getChildren().get(0).clone());
				}
				else
					env.info("configuring soapnode "+name);
				SoapNode sn=soapNodes.getByName(name);
				for (XmlNode child:node.getChildren()) {
					if (child.getName().equals("ms")) {
						MethodSet newms=null;
						String isvpName=child.getAttribute("isvp");
						String msName=child.getAttribute("name");
						env.info("  adding methodset "+msName);
						String dnms=null;
						if (isvpName==null) {
							newms=methodSets.getByName(msName);
							dnms="cn="+msName+",cn=method sets,"+getDn();
						}
						else {
							Isvp isvp=getSystem().isvp.getByName(isvpName);
							if (isvp!=null)
								newms=isvp.methodSets.getByName(msName);
							else
								dnms="cn="+msName+",cn="+isvpName+","+getSystem().getDn();
						}
						if (newms!=null) {
							sn.ms.add(newms);
							for (String ns: newms.namespaces.get())
								sn.namespaces.add(ns);
						}
						else {
							sn.ms.add(dnms);
							sn.namespaces.add("urn:"+msName); // TODO
						}
					}
					else if (child.getName().equals("sp")) {
						String spname=child.getAttribute("name");
						if (sn.soapProcessors.getByName(spname)!=null) {
							env.info("  skipping existing soap processor "+spname);
							continue;
						}
						else
							env.info("  creating soap processor "+spname);

						String machine=getSystem().machines.get(0).getName();
						boolean automatic="true".equals(child.getAttribute("automatic"));
						XmlNode config=child.getChild("bussoapprocessorconfiguration").getChildren().get(0);
						sn.createSoapProcessor(spname, machine, automatic, config.clone());
					}
					else if (child.getName().equals("bussoapnodeconfiguration")) {}
					else
						System.out.println("Unknown soapnode subelement "+child.getPretty());
				}
			}
			else if (node.getName().equals("user")) {
				String name=node.getAttribute("name");
				if (users.getByName(name)==null) {
					env.info("creating user "+name);
					createUser(name, getSystem().authenticatedUsers.getByName(node.getAttribute("au")));
				}
				else
					env.info("configuring user "+name);
				User u=users.getByName(name);
				for (XmlNode child:node.getChildren()) {
					if (child.getName().equals("role")) {
						Role r=null;
						String isvpName=child.getAttribute("isvp");
						String roleName=child.getAttribute("name");
						env.debug("  adding role "+roleName);
						String dnRole=null;
						if (isvpName==null) {
							r=roles.getByName(roleName);
							dnRole="cn="+roleName+",cn=organizational roles,"+getDn();
						}
						else {
							Isvp isvp=getSystem().isvp.getByName(isvpName);
							if (isvp!=null)
								r=isvp.roles.getByName(roleName);
							else
								dnRole="cn="+roleName+",cn="+isvpName+","+getSystem().getDn();
						}
						if (r!=null)
							u.roles.add(r);
						else
							u.roles.add(dnRole);
					}
					else
						System.out.println("Unknown user subelement "+child.getPretty());
				}
			}
			else if (node.getName().equals("role")) {
				String name=node.getAttribute("name");
				if (roles.getByName(name)==null) {
					env.info("creating role "+name);
					createRole(name);
				}
				else
					env.info("configuring role "+name);
				Role rr=roles.getByName(name);
				for (XmlNode child:node.getChildren()) {
					if (child.getName().equals("role")) {
						Role r=null;
						String isvpName=child.getAttribute("isvp");
						String roleName=child.getAttribute("name");
						env.debug("  adding role "+roleName);

						String dnRole=null;
						if (isvpName==null) {
							r=roles.getByName(roleName);
							dnRole="cn="+roleName+",cn=organizational roles,"+getDn();
						}
						else {
							Isvp isvp=getSystem().isvp.getByName(isvpName);
							if (isvp!=null)
								r=isvp.roles.getByName(roleName);
							else
								dnRole="cn="+roleName+",cn="+isvpName+","+getSystem().getDn();
						}
						if (r!=null)
							rr.roles.add(r);
						else
							rr.roles.add(dnRole);
					}
					else
						System.out.println("Unknown role subelement "+child.getPretty());
				}
			}
			else
				System.out.println("Unknown organization element "+node.getPretty());
		}
	}

	
	private XmlNode deduct(LdapObject parent, String isvpName) {
		XmlNode result=new XmlNode("caaspm");
		result.setAttribute("isvp", isvpName);
		result.setAttribute("org", this.getName());
		for (SoapNode sn : this.soapNodes) {
			XmlNode node=null;
			for (MethodSet ms: sn.methodSets) {
				if (ms.getParent()==parent) {
					if (node==null) {
						node=result.add("soapnode");
						node.setAttribute("name", sn.getName());
					}
					XmlNode child=node.add("ms");
					child.setAttribute("name", ms.getName());
					child.setAttribute("isvp", isvpName);
				}
			}
		}
		for (User u : this.users) {
			XmlNode node=null;
			for (Role r: u.roles) {
				if (r.getParent()==parent) {
					if (node==null) {
						node=result.add("user");
						node.setAttribute("name", u.getName());
					}
					XmlNode child=node.add("role");
					child.setAttribute("name", r.getName());
					child.setAttribute("isvp", isvpName);
				}
			}
		}
		for (Role rr : this.roles) {
			XmlNode node=null;
			for (Role r: rr.roles) {
				if (r.getParent()==parent) {
					if (node==null) {
						node=result.add("role");
						node.setAttribute("name", rr.getName());
					}
					XmlNode child=node.add("role");
					child.setAttribute("name", r.getName());
					child.setAttribute("isvp", isvpName);
				}
			}
		}
		return result;
	}
	
	@SuppressWarnings("unchecked")
	public CordysObjectList<LdapObject> seek(final Role target) {
		return new CordysObjectList(getSystem()) {
			@Override protected void retrieveList() {
				for (User u : users) {
					for (Role r: u.roles) {
						if (r==target)
							grow(u);
					}
				}
				for (Role rr : roles) {
					for (Role r: rr.roles) {
						if (r==target) 
							grow(rr);
					}
				}
			}
			@Override public String getKey() { return Organization.this.getKey()+":seek("+target+")";}
		};
	}

	@SuppressWarnings("unchecked")
	public CordysObjectList<SoapNode> seek(final MethodSet target) {
		return new CordysObjectList(getSystem()) {
			@Override protected void retrieveList() {
				for (SoapNode sn : soapNodes) {
					for (MethodSet ms: sn.methodSets) {
						if (ms==target)
							grow(sn);
					}
				}
			}
			@Override public String getKey() { return Organization.this.getKey()+":seek("+target+")";}
		};
	}

}
