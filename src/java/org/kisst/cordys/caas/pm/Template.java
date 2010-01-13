package org.kisst.cordys.caas.pm;

import java.io.File;
import java.util.ArrayList;
import java.util.Map;

import org.kisst.cordys.caas.AuthenticatedUser;
import org.kisst.cordys.caas.Configuration;
import org.kisst.cordys.caas.Isvp;
import org.kisst.cordys.caas.MethodSet;
import org.kisst.cordys.caas.Organization;
import org.kisst.cordys.caas.Role;
import org.kisst.cordys.caas.SoapNode;
import org.kisst.cordys.caas.SoapProcessor;
import org.kisst.cordys.caas.User;
import org.kisst.cordys.caas.main.Environment;
import org.kisst.cordys.caas.util.FileUtil;
import org.kisst.cordys.caas.util.StringUtil;
import org.kisst.cordys.caas.util.XmlNode;

public class Template {
	private static final Environment env=Environment.get();
	private final String template;
	public Template(String template) {this.template=template; }
	
	public Template(Organization org, String targetIsvpName) {
		XmlNode result=new XmlNode("org");
		//result.setAttribute("isvp", isvpName);
		result.setAttribute("org", org.getName());
		for (SoapNode sn : org.soapNodes) {
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
		for (User u : org.users) {
			XmlNode node=result.add("user");
			node.setAttribute("name", u.getName());
			node.setAttribute("au", u.au.getRef().getName());
			for (Role r: u.roles) {
				String isvpName=null;
				if (r.getParent() instanceof Organization) {
					if (r.getName().equals("everyoneIn"+org.getName()))
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
		for (Role rr : org.roles) {
			XmlNode node=result.add("role");
			node.setAttribute("name", rr.getName());
			for (Role r: rr.roles) {
				String isvpName=null;
				if (r.getParent() instanceof Organization) {
					if (r.getName().equals("everyoneIn"+org.getName()))
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
		String str=result.getPretty();
		this.template=str.replace("$", "${dollar}");
	}

	public void save(String filename) { FileUtil.saveString(new File(filename), template);}


	public XmlNode xml( Map<String, String> vars) {
		String str=template;
		if (vars!=null)
			str=StringUtil.substitute(str, vars);
		str=str.replace("${dollar}", "$");
		return new XmlNode(str);
	}
	public void apply(Organization org, Configuration conf) { apply(org, conf.getProps()); }
	public void apply(Organization org, Map<String, String> vars) {
		XmlNode template=xml(vars);
		for (XmlNode node : template.getChildren()){
			if (node.getName().equals("soapnode"))
				processSoapNode(org, node);
			else if (node.getName().equals("user"))
				processUser(org, node);
			else if (node.getName().equals("role"))
				processRole(org, node);
			else
				System.out.println("Unknown organization element "+node.getPretty());
		}
	}

	private void processSoapNode(Organization org, XmlNode node) {
		String name=node.getAttribute("name");
		SoapNode sn=org.soapNodes.getByName(name);
		if (sn==null) {
			env.info("creating soapnode "+name);
			XmlNode config=node.getChild("bussoapnodeconfiguration").getChildren().get(0).clone();
			org.createSoapNode(name, config, getMs(org,node));
			sn=org.soapNodes.getByName(name);
		}
		else {
			env.info("configuring soapnode "+name);
			for (MethodSet ms : getMs(org,node)){
				env.info("  adding methodset "+ms.getName());
				sn.ms.add(ms);
				for (String ns: ms.namespaces.get())
					sn.namespaces.add(ns);
			}
		}
		for (XmlNode child:node.getChildren()) {
			if (child.getName().equals("ms"))
				continue;
			else if (child.getName().equals("sp")) {
				String spname=child.getAttribute("name");
				if (sn.soapProcessors.getByName(spname)!=null) {
					env.info("  skipping existing soap processor "+spname);
					continue;
				}
				else
					env.info("  creating soap processor "+spname);

				String machine=org.getSystem().machines.get(0).getName();
				boolean automatic="true".equals(child.getAttribute("automatic"));
				XmlNode config=child.getChild("bussoapprocessorconfiguration").getChildren().get(0);
				sn.createSoapProcessor(spname, machine, automatic, config.clone());
			}
			else if (child.getName().equals("bussoapnodeconfiguration")) {}
			else
				System.out.println("Unknown soapnode subelement "+child.getPretty());
		}
	}

	private MethodSet[] getMs(Organization org, XmlNode node) {
		ArrayList<MethodSet> result=new ArrayList<MethodSet>();
		for (XmlNode child:node.getChildren()) {
			if (child.getName().equals("ms")) {
				MethodSet newms=null;
				String isvpName=child.getAttribute("isvp");
				String msName=child.getAttribute("name");
				if (isvpName==null) {
					newms=org.methodSets.getByName(msName);
				}
				else {
					Isvp isvp=org.getSystem().isvp.getByName(isvpName);
					if (isvp!=null)
						newms=isvp.methodSets.getByName(msName);
				}
				if (newms!=null) {
					result.add(newms);
				}
				else {
					env.error("  skipping unknownn methodset "+msName);
				}
			}
		}
		return result.toArray(new MethodSet[result.size()]);
	}
	private void processUser(Organization org, XmlNode node) {
		String name=node.getAttribute("name");
		if (org.users.getByName(name)==null) {
			AuthenticatedUser au=org.getSystem().authenticatedUsers.getByName(node.getAttribute("au"));
			if (au==null) {
				env.error("could not create user "+name+" could not find authenticated user "+node.getAttribute("au"));
				//continue;
			}
			else {
				env.info("creating user "+name+" with authenticated user "+au.getName());
				org.createUser(name, au);
			}
		}
		else
			env.info("configuring user "+name);
		User u=org.users.getByName(name);
		for (XmlNode child:node.getChildren()) {
			if (child.getName().equals("role")) {
				Role r=null;
				String isvpName=child.getAttribute("isvp");
				String roleName=child.getAttribute("name");
				env.info("  adding role "+roleName);
				String dnRole=null;
				if (isvpName==null) {
					r=org.roles.getByName(roleName);
					dnRole="cn="+roleName+",cn=organizational roles,"+org.getDn();
				}
				else {
					Isvp isvp=org.getSystem().isvp.getByName(isvpName);
					if (isvp!=null)
						r=isvp.roles.getByName(roleName);
					else
						dnRole="cn="+roleName+",cn="+isvpName+","+org.getSystem().getDn();
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

	private void processRole(Organization org, XmlNode node) {
		String name=node.getAttribute("name");
		if (org.roles.getByName(name)==null) {
			env.info("creating role "+name);
			org.createRole(name);
		}
		else
			env.info("configuring role "+name);
		Role rr=org.roles.getByName(name);
		for (XmlNode child:node.getChildren()) {
			if (child.getName().equals("role")) {
				Role r=null;
				String isvpName=child.getAttribute("isvp");
				String roleName=child.getAttribute("name");
				env.info("  adding role "+roleName);

				String dnRole=null;
				if (isvpName==null) {
					r=org.roles.getByName(roleName);
					dnRole="cn="+roleName+",cn=organizational roles,"+org.getDn();
				}
				else {
					Isvp isvp=org.getSystem().isvp.getByName(isvpName);
					if (isvp!=null)
						r=isvp.roles.getByName(roleName);
					else
						dnRole="cn="+roleName+",cn="+isvpName+","+org.getSystem().getDn();
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
	/*
	private Role[] getRoles(Organization org, XmlNode node) {
		ArrayList<Role> result=new ArrayList<Role>();
		for (XmlNode child:node.getChildren()) {
			if (child.getName().equals("role")) {
				Role r=null;
				String isvpName=child.getAttribute("isvp");
				String roleName=child.getAttribute("name");
				String dnRole=null;
				if (isvpName==null) {
					r=org.roles.getByName(roleName);
					dnRole="cn="+roleName+",cn=organizational roles,"+org.getDn();
				}
				else {
					Isvp isvp=org.getSystem().isvp.getByName(isvpName);
					if (isvp!=null)
						r=isvp.roles.getByName(roleName);
					else
						dnRole="cn="+roleName+",cn="+isvpName+","+org.getSystem().getDn();
				}
				if (r!=null)
					result.add(r);
				else
					env.error("  skipping unknown role "+roleName);
			}
		}
		return result.toArray(new Role[result.size()]);
	}
	*/
}
