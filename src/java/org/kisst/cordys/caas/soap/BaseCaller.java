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

import org.kisst.cordys.caas.main.Environment;
import org.kisst.cordys.caas.util.XmlNode;

public abstract class BaseCaller implements SoapCaller {
	protected final String baseurl;
	protected final String username;
	protected final String password;

	public abstract String httpCall(String url, String input);

	public BaseCaller(String name)
	{
		String url =Environment.get().getProp("system."+name+".gateway.url", null);
		if (url==null)
			throw new RuntimeException("No url configured in property system."+name+".gateway.url");
		int pos=url.indexOf("?");
		if (pos>0)
			baseurl=url.substring(0,pos);
		else
			baseurl=url;
		username   = Environment.get().getProp("system."+name+".gateway.username", null);
		password   = Environment.get().getProp("system."+name+".gateway.password", null);
	}

	private String httpCall(String input, String org, String processor) {
		String url=baseurl;
		if (org!=null)
			url += "?organization="+org;
		if (processor!=null) {
			if (org==null)
				url += "?processor="+processor;
			else
				url += "&processor="+processor;
		}
		return httpCall(url, input);
	}


	public String call(String input) {
		return call(input,null, null); // TODO: use other parameters
	}

	public String call(String input, String org, String processor) {
		String soap="<SOAP:Envelope xmlns:SOAP=\"http://schemas.xmlsoap.org/soap/envelope/\"><SOAP:Body>"
			+ input
			+ "</SOAP:Body></SOAP:Envelope>";
		Environment.get().debug(soap);
		String response = httpCall(soap, org, processor);
		Environment.get().debug(response);
		if (response.indexOf("SOAP:Fault")>0)
			throw new RuntimeException(response);
		return response;
	}

	public XmlNode call(XmlNode method) {
		return call(method, null, null);
	}
	public XmlNode call(XmlNode method, String org, String processor) {
		Environment env=Environment.get();
		if (env.debug)
			env.debug(method.getPretty());
		String xml = method.toString();
		String response= call(xml, org, processor);
		XmlNode output=new XmlNode(response);
		if (output.getName().equals("Envelope"))
			output=output.getChild("Body").getChildren().get(0);
		if (env.debug)
			env.debug(output.getPretty());
		return output;
	}
	

}
