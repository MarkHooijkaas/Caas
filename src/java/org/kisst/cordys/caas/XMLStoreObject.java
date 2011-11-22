package org.kisst.cordys.caas;

import java.util.ArrayList;

import org.kisst.cordys.caas.exception.CaasRuntimeException;
import org.kisst.cordys.caas.support.CordysObject;
import org.kisst.cordys.caas.util.XmlNode;

/**
 *  Class to represent an item in Cordys XMLStore
 *  It provides operations to read, create and update the XML
 *  
 *  @author galoori
 */

public class XMLStoreObject extends CordysObject{

	//Location of the XMLStoreObject content in Cordys XMLStore
	private String key;
	//Version of the XMLStoreObject. Either "organization" or "user"
	private String version;
	//XML content of the XMLStoreObject
	private XmlNode xml;
	//Represents the organization to which this XMLStore object belongs
	private Organization org;
	//Contains the list of XMLStore versions
	public static ArrayList<String> versionList;
	
	@Override
	//Returns the Path of the XMLStoreObject
	public String getKey() {
		return key;
	}
	@Override
	//Returns the name of the XMLStoreObject
	public String getName() {
		int pos = key.lastIndexOf("/");
		if(pos>0)
			return key.substring(pos);
		return key;
	}
	@Override
	public String getVarName() {
		return getName();
	}
	
	@Override
	/**
	 * Returns the CordysSystem object of XMLStoreObject
	 */
	public CordysSystem getSystem() {
		
		CordysSystem system = org.getSystem();
		if(system==null)
			throw new CaasRuntimeException("Cordys system is null for organization '"+org.getName()+"'");
		return system;
	}
	
	/**
	 *  Sets the "organization" the as default version
	 *  
	 * @param key  Full path of XMLStoreObject in Cordys XMLStore
	 * @param org Organization object to which the XMLStoteObject belongs
	 */
	public XMLStoreObject(String key,Organization org)
	{
		this(key, "organization", org);
	}
	
	/**
	 *  Sets the key, version and XML of the XMLStoreObject
	 *  
	 * @param key  Complete path to the XMLStoreObject
	 * @param version Version of the XMLStoreObject
	 */
	public XMLStoreObject(String key,String version, Organization org)
	{
		this.key = key;
		this.version = version;
		this.org = org;
		this.xml = readXMLStoreObject(key, version);
	}
	
	/**
	 *  Fetches the XML content of the XMLStoreObject from Cordys
	 *  XMLStore using GetXMLObject service
	 *  
	 * @param key  Complete path to the XMLStoreObject
	 * @param version Version of the XMLStoreObject
	 * @return XML content
	 */
	public XmlNode readXMLStoreObject(String key, String version) 
	{	
		//Check if the key or version is null 
		if(key==null || version==null)
			throw new CaasRuntimeException("key or version of the XMLStoreObject is null. key:: "+key+" version:: "+version);
		
		key = key.trim();
		version = version.trim();
		//Check if the key or version is empty 
		if(key.length()==0 || version.length()==0)
			throw new CaasRuntimeException("key or version of the XMLStoreObject is empty. key:: "+key+" version:: "+version);
		//Check for invalid version type
		if(!versionList.contains(version))
			throw new CaasRuntimeException("Invalid XMLStore version '"+version+"'. Please change it to either 'organization' or 'user'");
		
		XmlNode request = new XmlNode("GetXMLObject",xmlns_xmlstore);
		request.add("key").setAttribute("version", version).setText(key);
		XmlNode response = call(request);
		return response.getChild("tuple/old");
	}
	
	/**
	 * Gets the XML content of the XMLStoreObject
	 * 
	 * @return XML content of the XMLStoreObject 
	 */
	public XmlNode getXML()
	{	
		return xml;
	}
	
	public void appendXML(XmlNode node)
	{
		XmlNode request=new XmlNode("AppendXMLObject", xmlns_xmlstore);
		XmlNode tuple=request.add("tuple");
		tuple.setAttribute("key", key);
		tuple.setAttribute("version", version);		
		if (node!=null)
			tuple.add("new").add(node);
		call(request);
		//Refresh the XML content of the XMLStoreObject after append operation	
		this.xml = readXMLStoreObject(key, version);
	}
	
	/**
	 * Updates the XML content of the XMLStoreObject using UpdateXMLObject service
	 * If 'update' flag is set to true, then the XML content is overwritten unconditionally
	 * If 'update' flag is set to false and the XMLStore object is already existing in
	 * Cordys XMLStore then exception is thrown
	 * 
	 * @param newXml - XML content to be updated
	 * @param update - true/false
	 */
	public void overwriteXML(XmlNode newXml)
	{
		XmlNode request=new XmlNode("UpdateXMLObject", xmlns_xmlstore);
		XmlNode tuple=request.add("tuple");
		tuple.setAttribute("key", key);
		tuple.setAttribute("version", version);
		//Set 'unconditional' flag to true to overwrite the existing XML 
		tuple.setAttribute("unconditional", "true");
		if (newXml!=null)
			tuple.add("new").add(newXml);
		call(request);
		//Refresh the XML content of the XMLStoreObject after the update operation
		this.xml = newXml;
	}
	

	/**
	 * Executes the XMLStore web services. It specifies the organization name under which context
	 * the web services will be executed
	 * 
	 * @param method Web service request XML
	 * @return XmlNode representing the web service response
	 */
	public XmlNode call(XmlNode method) { 
		return getSystem().call(method,org.getDn(),null); 
	}

	//Initialize and load XMLStore versions
	static{
		 versionList = new ArrayList<String>();
		 versionList.add("organization");
		 versionList.add("user");
	}
}
