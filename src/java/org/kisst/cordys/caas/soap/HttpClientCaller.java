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

public class HttpClientCaller implements SoapCaller {
	private final HttpClient client = new HttpClient();
	private final String url;
	private final String username;
	private final String password;
	private final String ntlmhost;
	private final String ntlmdomain;
	public final Properties props=new Properties();

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

	public String call(String input) {
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

}
