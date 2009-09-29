package org.kisst.cordys.caas;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.kisst.cordys.caas.soap.SoapCaller;


public class CordysSystem extends CordysObject {
	private final String rootdn;
	private final SoapCaller caller;

	public CordysSystem(String dn, SoapCaller caller) {
		super(dn);
		this.caller=caller;
		this.rootdn=dn;
	}
	private String call(String input) {
		String soap="<SOAP:Envelope xmlns:SOAP=\"http://schemas.xmlsoap.org/soap/envelope/\"><SOAP:Body>"
			+ input
			+ "</SOAP:Body></SOAP:Envelope>";
		return caller.call(soap);
	}
	
	public List<Organization> getOrganizations() {
		String msg="<GetAllOrganizations xmlns=\"http://schemas.cordys.com/1.0/ldap\">"
			+"<dn>${rootdn}</dn>"
		    +"</GetAllOrganizations>";
		msg = msg.replaceAll("\\$\\{rootdn}",rootdn);
		String response=call(msg);
		ArrayList<Organization> result=new ArrayList<Organization>();
		int pos=0;
		String key="entry dn=\"";
		while ((pos=response.indexOf(key, pos))>0) {
			pos=pos+key.length()+1;
			String dn=response.substring(pos,response.indexOf("\"", pos));
			result.add(new Organization(dn));
			System.out.println(dn);
		}
		return result;
	}

	private String loadTemplate(String name) {
		InputStream instream=CordysSystem.class.getClassLoader().getResourceAsStream("org/kisst/cordys/sbf/templates/"+name);
		BufferedReader inp = new BufferedReader(new InputStreamReader(instream));
		String result="";
		String line;
		try {
			while ((line=inp.readLine()) != null)
				result+=line+"\n";
		} catch (IOException e) { throw new RuntimeException(e); }
		return result;
	}

	public void createCustomRole(String org, String name ) {
		String template=loadTemplate("CreateCustomRole.template");
		template = template.replaceAll("\\$\\{dn}",dn);
		template = template.replaceAll("\\$\\{name}",name);
		template = template.replaceAll("\\$\\{org}",org);
		System.out.println(call(template));
	}

	public void createAuthUser(String login, String name, String fullname) {
		String template=loadTemplate("CreateAuthUser.template");
		template = template.replaceAll("\\$\\{dn}",dn);
		template = template.replaceAll("\\$\\{name}",name);
		template = template.replaceAll("\\$\\{fullname}",fullname);
		template = template.replaceAll("\\$\\{login}",login);
		System.out.println(call(template));
	}

	public void createOrgUser(String org, String authname, String name, String fullname, String role) {
		String template=loadTemplate("CreateOrgUser.template");
		template = template.replaceAll("\\$\\{dn}",dn);
		template = template.replaceAll("\\$\\{org}",org);
		template = template.replaceAll("\\$\\{name}",name);
		template = template.replaceAll("\\$\\{authname}",authname);
		template = template.replaceAll("\\$\\{fullname}",fullname);
		template = template.replaceAll("\\$\\{role}",role);
		System.out.println(call(template));
	}

	
	public void createMethodSet(String conntype, String name, String namespace) {
		String template=loadTemplate("CreateMethodSet.template");
		template = template.replaceAll("\\$\\{org}",rootdn);
		template = template.replaceAll("\\$\\{name}",name);
		template = template.replaceAll("\\$\\{namespace\\}",namespace);
		template = template.replaceAll("\\$\\{type\\}",conntype);
		System.out.println(call(template));
	}
	public void deleteMethodSet(String conntype, String name, String namespace) {
		String template=loadTemplate("DeleteMethodSet.template");
		template = template.replaceAll("\\$\\{org}",rootdn);
		template = template.replaceAll("\\$\\{name}",name);
		template = template.replaceAll("\\$\\{namespace\\}",namespace);
		template = template.replaceAll("\\$\\{type\\}",conntype);
		System.out.println(template);
		System.out.println(call(template));
	}	
	public void createMethod(String methodset, String name, String impl, String wsdl) {
		String template=loadTemplate("CreateMethod.template");
		template = template.replaceAll("\\$\\{org}",rootdn);
		template = template.replaceAll("\\$\\{methodset}",methodset);
		template = template.replaceAll("\\$\\{name\\}",name);
		template = template.replaceAll("\\$\\{impl\\}",xmlEscape(impl));
		template = template.replaceAll("\\$\\{wsdl\\}",xmlEscape(wsdl));
		System.out.println(template);
		System.out.println(call(template));
	}
	private String xmlEscape(String str) {
		return str.replaceAll("&", "&amp;").replaceAll("<", "&lt;").replaceAll(">", "&gt;");
	}

}
