package org.kisst.cordys.caas.soap;


import java.util.HashMap;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.kisst.cordys.caas.exception.CaasRuntimeException;
import org.kisst.cordys.caas.support.SamlClient;
import org.kisst.cordys.caas.util.StringUtil;

/**
 * Responsible for executing the SOAP requests when Cordys is running in SSO mode
 *  
 * @author galoori
 */

public class SamlClientCaller extends BaseCaller {

	//A constant containing the SAML artifact name that need to be set in the query string
	private final String SAML_ARTIFACT_NAME= "SAMLart";
	//Contains the Cordys system name which is in turn passed to the SamlClient class
	private final String systemName;
	
	public SamlClientCaller(String systemName){
		super(systemName);
		this.systemName = systemName;
	}

	@Override 
	/**
	 * Delegates the incoming SOAP request to sendHttpRequest
	 */
	public String httpCall(String url, String inputSoapRequest) {
		return sendHttpRequest(url,inputSoapRequest,null);
	}

	/**
	 * Sends the input SOAP request to the Cordys Gateway after adding SAML ArtifactID. 
	 * It also has a support to send query string parameters like organization, timeout etc.
	 * 
	 * @param url - Cordys BaseGateway URL
	 * @param inputSoapRequest - SOAP Request XML string
	 * @param map - Query string parameters that need to added to the BaseGateway URL.
	 * @return response - SOAP Response XML string
	 */
	public String sendHttpRequest(String url, String inputSoapRequest, HashMap<String, String> map){
				
		int statusCode;
		String response,baseURL,queryString, aString=null;
		
		//Get the SamlClient instance for systemName and get its ArtifactID 
		String artifactID= SamlClient.getInstance(systemName).getArtifactID();		
		//Check if the artifactId is null
		if(artifactID==null) 
			throw new CaasRuntimeException("Unable to get the SAML ArtifactID for system '"+systemName+"'");
		
		//Check if the url already contains any query string parameters
		baseURL = url;
		int pos=url.indexOf("?");
		if (pos>0){
			baseURL = url.substring(0, pos);
			aString = url.substring(pos+1);
		}
		if(map==null) 
			map = new HashMap<String, String>();
		
		map.put(SAML_ARTIFACT_NAME, artifactID);

		if(aString==null)
			queryString = StringUtil.mapToString(map);
		else
			queryString = StringUtil.mapToString(map)+"&"+aString;
		
		//Create a PostMethod by passing the Cordys Gateway URL to its constructor
		PostMethod method=new PostMethod(baseURL);
		method.setDoAuthentication(true);
		//Set the Query String
		method.setQueryString(queryString);
		
		try {
			//Environment.get().debug("URL:: "+method.getURI().getURI().toString());
			method.setRequestEntity(new StringRequestEntity(inputSoapRequest, "text/xml", "UTF-8"));
			HttpClient client = new HttpClient();
			statusCode = client.executeMethod(method);
			response=method.getResponseBodyAsString();
		}
		catch (Exception e) { throw new CaasRuntimeException(e);}
		if (statusCode != HttpStatus.SC_OK) {
			throw new CaasRuntimeException("Method failed: " + method.getStatusLine()+"\n"+response);
		}
		return response;
	}
}