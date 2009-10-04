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

package org.kisst.cordys.caas.soap;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.NTCredentials;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.httpclient.auth.AuthScope;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.jdom.Element;
import org.kisst.cordys.caas.util.JdomUtil;

public class HttpClientCaller implements SoapCaller {
	private final HttpClient client = new HttpClient();
	private final String url;
	private final String username;
	private final String password;
	private final String ntlmhost;
	private final String ntlmdomain;
	public final Properties props=new Properties();
	private boolean debug=false;

	public HttpClientCaller(String filename)
	{
		props.clear();
		FileInputStream inp = null;
		try {
			inp =new FileInputStream(filename);
			props.load(inp);
		} 
		catch (java.io.IOException e) { throw new RuntimeException(e);  }
		finally {
			try {
				if (inp!=null) 
					inp.close();
			}
			catch (java.io.IOException e) { throw new RuntimeException(e);  }
		}

		url =(String) props.get("cordys.gateway.url");
		username=(String) props.get("cordys.gateway.username");
		password=(String) props.get("cordys.gateway.password");
		ntlmhost=(String) props.get("cordys.gateway.ntlmhost");
		ntlmdomain=(String) props.get("cordys.gateway.ntlmdomain");
		if (ntlmdomain==null)
			client.getState().setCredentials(AuthScope.ANY,	new UsernamePasswordCredentials(username, password));
		else
			client.getState().setCredentials(AuthScope.ANY,	new NTCredentials(username, password, ntlmhost, ntlmdomain));

	}

	public String httpCall(String input) {
		PostMethod method=new PostMethod(url);
		method.setDoAuthentication(true);
		int statusCode;
		String response;
		try {
			method.setRequestEntity(new StringRequestEntity(input, "text/xml", "UTF-8"));
			statusCode = client.executeMethod(method);
			response=method.getResponseBodyAsString();
		}
		catch (HttpException e) { throw new RuntimeException(e);}
		catch (IOException e) { throw new RuntimeException(e);}
		if (statusCode != HttpStatus.SC_OK) {
			throw new RuntimeException("Method failed: " + method.getStatusLine()+"\n"+response);
		}
		return response;
	}

	public String soapCall(String input) {
		String soap="<SOAP:Envelope xmlns:SOAP=\"http://schemas.xmlsoap.org/soap/envelope/\"><SOAP:Body>"
			+ input
			+ "</SOAP:Body></SOAP:Envelope>";
		if (debug)
			System.out.println(soap);
		String response = httpCall(soap);
		if (debug || response.indexOf("SOAP:Fault")>0)
			System.out.println(response);
		return response;
	}
	public Element soapCall(Element method) { 
		String xml = JdomUtil.toString(method);
		String response= soapCall(xml);
		Element output=JdomUtil.fromString(response);
		if (output.getName().equals("Envelope"))
			output=output.getChild("Body",null).getChild(null,null);
		return output;
	}
	

}
