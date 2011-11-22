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

import java.io.File;
import java.util.LinkedList;
import java.util.List;
import org.kisst.cordys.caas.support.CordysObject;
import org.kisst.cordys.caas.util.FileUtil;
import org.kisst.cordys.caas.util.XmlNode;

public class Machine extends CordysObject {
	private final SoapProcessor monitor;
	private final String hostname;
	//Contains the value of CORDYS_INSTALL_DIR variable
	private final String cordysInstallDir;
	private static final String UPLOAD_ISVP = "UploadISVPackage";
	private static final String LOAD_ISVP = "LoadISVPackage";
	private static final String UPGRADE_ISVP = "UpgradeISVPackage";
	private static final String GET_PROPERTY = "GetProperty";
	private static final String LIST_ISVPS = "ListISVPackages";
	private static final String UNLOAD_ISVP = "UnloadISVPackage";
	private static final String GET_ISVP_DEFINITION = "GetISVPackageDefinition";
	private static final String LIST = "List";
	
	protected Machine(SoapProcessor monitor) 
	{
		this.monitor=monitor;
		String tmp=monitor.getName();
		this.hostname=tmp.substring(tmp.indexOf("monitor@")+8);
		//Read the value of CORDYS_INSTALL_DIR variable and assign it to cordysInstallDir
		this.cordysInstallDir=readEIBProperty("CORDYS_INSTALL_DIR");

	}

	@Override public String toString() { return getVarName();}
	@Override public String getName() { return hostname;}
	@Override public String getKey() { return "machine:"+getName();	}
	@Override public CordysSystem getSystem() { return monitor.getSystem();	}
	@Override public String getVarName() { return getSystem().getVarName()+".machine."+getName();}

	public SoapProcessor getMonitor() { return monitor; }
	
	public void refreshSoapProcessors() {
		XmlNode method=new XmlNode(LIST, xmlns_monitor);
		XmlNode response=monitor.call(method);
		for (XmlNode s: response.getChildren("tuple")) {
			XmlNode workerprocess=s.getChild("old/workerprocess");
			String dn=workerprocess.getChildText("name");
			SoapProcessor obj= (SoapProcessor) getSystem().getLdap(dn);
			obj.setWorkerprocess(workerprocess);
		}
	}

	/**
	 * Loads the ISVP
	 * 
	 * @param isvpName
	 * @return
	 */
	public String loadIsvp(String isvpName, long timeOut) 
	{
		//Get the ISVP definition details
		XmlNode details=getIsvpDefinition(isvpName);
		XmlNode request=new XmlNode(LOAD_ISVP, xmlns_isv);
		//Set the timeout
		request.setAttribute("timeOut", String.valueOf(timeOut));
		request.add("url").setText("http://"+hostname+"/cordys/wcp/isvcontent/packages/"+isvpName);
		request.add(details.getChild("ISVPackage").detach());
		//Load the ISVP
		XmlNode response = monitor.call(request);
		//Read the status message		
		return response.getChildText("status");
	}
	
	/**
	 * Upgrades ISVP
	 * 
	 * It needs to consider the rules and other runtime content as well
	 * Currently It overwrites the BPM content in the previous ISVP
	 * 
	 * @param isvpName
	 * @return
	 */	
	public String upgradeIsvp(String isvpName, boolean deleteReferences, long timeOut)
	{
		//Get the provided ISVP details
		XmlNode details=getIsvpDefinition(isvpName);
		XmlNode isvPackageNode = details.getChild("ISVPackage");
		isvPackageNode.setAttribute("canUpgrade", "true");
		isvPackageNode.setAttribute("deleteReferences", String.valueOf(deleteReferences));
		XmlNode promptsetNode = isvPackageNode.add("promptset", xmlns_isv);
		//Add a prompt node and set its value as true to overwrite the BPM content in the previous ISV
		XmlNode promptNode = promptsetNode.add("BusinessProcessEngine").add("prompt");
		promptNode.setText("yes");
		promptNode.setAttribute("id", "overwrite");
		promptNode.setAttribute("value", "yes");
		promptNode.setAttribute("description", "This value indicates whether or not the ISV has overwritten the contents of a previous ISV.");		
		XmlNode request=new XmlNode(UPGRADE_ISVP, xmlns_isv);
		//Set the timeout value
		request.setAttribute("timeOut", String.valueOf(timeOut));
		request.add("url").setText("http://"+hostname+"/cordys/wcp/isvcontent/packages/"+isvpName);
		request.add(isvPackageNode.detach());
		//Upgrade the ISVP		
		XmlNode response = monitor.call(request);
		//Read the status message
		return response.getChildText("status");
	}
	
	/**
	 * Gets the complete details of the given ISVP
	 * 
	 * @param isvpName
	 * @return XmlNode ISVP details XML node
	 */	
	private XmlNode getIsvpDefinition(String isvpName)
	{
		XmlNode method=new XmlNode(GET_ISVP_DEFINITION, xmlns_isv);
		XmlNode file=method.add("file");
		file.setText(isvpName);
		file.setAttribute("type", "isvpackage");
		file.setAttribute("detail", "false");
		file.setAttribute("wizardsteps", "true");
		return monitor.call(method);
	}
	
	/**
	 * Unloads the ISVP
	 * 
	 * @param isvp
	 * @param deletereferences
	 * @return 
	 */	
	public void unloadIsvp(Isvp isvp, boolean deletereferences) 
	{
		XmlNode method=new XmlNode(UNLOAD_ISVP, xmlns_isv);
		XmlNode file=method.add("file");
		file.setText(isvp.getBasename());
		if (deletereferences)
			file.setAttribute("deletereference", "true");
		else
			file.setAttribute("deletereference", "false");
		monitor.call(method);
		//TODO: do this only for last machine??? getSystem().removeLdap(isvp.getDn());
	}
	
	/**
	 * Lists all the installed ISVPs
	 * 
	 * @return List<String> Names of the ISVPs 
	 */	
	public List<String> getIsvpFiles() 
	{
		LinkedList<String> result=new LinkedList<String>();
		XmlNode method=new XmlNode(LIST_ISVPS, xmlns_isv);
		method.add("type").setText("ISVPackage");
		XmlNode response=monitor.call(method);
		for (XmlNode child: response.getChildren()) {
			String url=child.getText();
			result.add(url.substring(url.lastIndexOf("/")+1));
		}
		return result;
	}
	
	public String getCordysInstallDir(){
		return cordysInstallDir;
	}
	
	/**
	 * Reads the value of given EIB property
	 * 
	 * @param propertyName Name of the property
	 * @return String Value of the property
	 */
	public String readEIBProperty(String propertyName)
	{
		XmlNode method=new XmlNode(GET_PROPERTY, xmlns_monitor);
		method.add("property").setAttribute("name", propertyName);
		XmlNode response = monitor.call(method);
		XmlNode propertyNode = response.getChild("tuple/old/property");
		return propertyNode.getAttribute("value", null);
	}
	
	/**
	 * Uploads the given ISVP to the <CORDYS_INSTALL_DIR>/isvcontent/packages location 
	 * 
	 * @param isvpFilePath Full path of the ISVP
	 * @return
	 */
	public void uploadIsvp(String isvpFilePath)
	{	
		File isvpFile = new File(isvpFilePath);
		String isvpName = isvpFile.getName();
		String isvpEncodedContent = FileUtil.encodeFile(isvpFilePath);
		XmlNode request = new XmlNode(UPLOAD_ISVP,xmlns_isv);
		request.add("name").setText(isvpName);
		request.add("content").setText(isvpEncodedContent);
		monitor.call(request);
	}
}
