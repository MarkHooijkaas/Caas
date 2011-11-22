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

import java.util.Random;

import org.kisst.cordys.caas.exception.CaasRuntimeException;
import org.kisst.cordys.caas.support.ChildList;
import org.kisst.cordys.caas.support.LdapObject;
import org.kisst.cordys.caas.support.LdapObjectBase;
import org.kisst.cordys.caas.util.StringUtil;
import org.kisst.cordys.caas.util.XmlNode;

public class SoapProcessor extends LdapObjectBase {
	private static final String START = "Start";
	private static final String STOP = "Stop";
	private static final String RESTART = "Restart";
	private static final String LIST = "List";
	
	public final ChildList<ConnectionPoint> connectionPoints = new ChildList<ConnectionPoint>(this, ConnectionPoint.class);
	public final ChildList<ConnectionPoint> cp = connectionPoints;
	public final StringProperty computer = new StringProperty("computer");
	public final StringProperty host = new StringProperty("busoshost");
	public final BooleanProperty automatic = new BooleanProperty("automaticstart");
	public final XmlProperty config = new XmlProperty("bussoapprocessorconfiguration");
	public final XmlSubProperty ui_algorithm = new XmlSubProperty(config, "routing/@ui_algorithm");  
	public final XmlSubProperty ui_type = new XmlSubProperty(config, "routing/@ui_type");  
	public final XmlSubProperty preference = new XmlSubProperty(config, "routing/preference");  
	public final XmlSubProperty gracefulCompleteTime = new XmlSubProperty(config, "gracefulCompleteTime");  
	public final XmlSubProperty abortTime = new XmlSubProperty(config, "abortTime");  
	public final XmlSubProperty requestTimeout = new XmlSubProperty(config, "cancelReplyInterval");  
	public final XmlSubProperty implementation = new XmlSubProperty(config, "configuration/@implementation");  
	public final XmlBoolProperty useSystemLogPolicy = new XmlBoolProperty(config, "loggerconfiguration/systempolicy",true);
	private XmlNode workerprocess;
	private static Random random=new Random();
	
	protected SoapProcessor(LdapObject parent, String dn) {
		super(parent, dn);
	}
	@Override protected String prefix() { return "sp"; }

	@Override public void myclear() { super.myclear(); workerprocess=null; } 

	public String call(String input) { return getSystem().call(input, null, getDn()); }
	
	public void setWorkerprocess(XmlNode workerprocess) {
		this.workerprocess=workerprocess;
	}
	private static XmlNode inactiveWorkerProcess=new XmlNode("<dummy><status></status></dummy>");
	public XmlNode getWorkerprocess() {
		if (workerprocess!=null && useCache())
			return this.workerprocess;
		XmlNode method=new XmlNode(LIST, xmlns_monitor);
		XmlNode response=call(method);
		for (XmlNode s: response.getChildren("tuple")) {
			XmlNode workerprocess=s.getChild("old/workerprocess");
			String dn=workerprocess.getChildText("name");
			if (dn.equals(getDn())) {
				this.workerprocess=workerprocess;
				return workerprocess;
			}
		}
		this.workerprocess= inactiveWorkerProcess;
		return workerprocess;
		//throw new RuntimeException("Could not find processor details for "+this.dn);
	}
	
	private int getIntChild(XmlNode x, String name) {
		String result=x.getChildText(name);
		if (result==null || result.length()==0)
			return -1;
		else
			return Integer.parseInt(result);
	}
	public String getStatus()      { return getWorkerprocess().getChildText("status"); } 
	public String getCpuTime()     { return getWorkerprocess().getChildText("totalCpuTime"); }
	public int getPid()            { return getIntChild(getWorkerprocess(), "process-id"); } 
	public int getNomMemory()      { return getIntChild(getWorkerprocess(), "totalNOMMemory"); } 
	public int getNomNodesMemory() { return getIntChild(getWorkerprocess(), "totalNOMNodesMemory"); } 
	public int getVirtualMemory()  { return getIntChild(getWorkerprocess(), "virtualMemoryUsage"); } 
	public int getResidentMemory() { return getIntChild(getWorkerprocess(), "residentMemoryUsage"); } 
	public int getSequenceNumber() { return getIntChild(getWorkerprocess(), "sequence-number"); } 
	public int getPreference()     { return getIntChild(getWorkerprocess(), "preference"); } 
	public int getBusdocs()        { return getIntChild(getWorkerprocess(), "busdocs"); } 
	public int getProcessingTime() { return getIntChild(getWorkerprocess(), "processing-time"); } 
	public int getLastTime()       { return getIntChild(getWorkerprocess(), "last-time"); } 

	public void start() {
		XmlNode method=new XmlNode (START, xmlns_monitor);
		method.add("dn").setText(getDn());
		call(method);
	}
	public void stop() {
		XmlNode method=new XmlNode (STOP, xmlns_monitor);
		method.add("dn").setText(getDn());
		call(method);
	}
	public void restart() {
		XmlNode method=new XmlNode (RESTART, xmlns_monitor);
		method.add("dn").setText(getDn());
		call(method);
	}

	public void createConnectionPoint(String name) {
		createConnectionPoint(name, "socket", getMachine().getName());
	}
	//Following 2 methods are changed to pass the machineName as a parameter, which is needed in case of clustered installation
	public void createConnectionPoint(String name, String machineName) {
		createConnectionPoint(name, "socket", machineName);
	}
	public void createConnectionPoint(String name, String type, String machineName) {
		String uri=type+"://"+machineName+":"+getAvailablePort();
		XmlNode newEntry=newEntryXml("", name,"busconnectionpoint");
		newEntry.add("description").add("string").setText(name);
		newEntry.add("labeleduri").add("string").setText(uri); // TODO
		createInLdap(newEntry);
		connectionPoints.clear();
	}
	public void updateEntry(XmlNode newEntry){
		updateLdap(newEntry);
		myclear();
		getSystem().removeLdap(getDn());
	}
	private Machine getMachine() {
		// TODO This hack only works on single machine installs
		return getSystem().machines.get(0);
	}
	public String getClassPath(){
		XmlNode oldEntry=getEntry().clone();
		XmlNode config = new XmlNode(oldEntry.getChildText("bussoapprocessorconfiguration/string"));
		XmlNode cpNode = getCPNode(config);
		if(cpNode==null) return null;
		String attrValue = cpNode.getAttribute("value");
		return attrValue.substring("-cp".length()).trim();
	}
	private XmlNode getCPNode(XmlNode config){
		XmlNode jreConfigNode = config.getChild("jreconfig");
		if(jreConfigNode==null) return null;
		for(XmlNode paramNode:jreConfigNode.getChildren())
			if(paramNode.getAttribute("value").contains("-cp"))
				return paramNode;
		return null;
	}
	public String setClassPath(String newCP){
		//By default, append the newCP to the existing class path of the SC 
		return setClassPath(newCP, false);
	}
	public String setClassPath(String newCP, boolean overwrite){
		if(StringUtil.isEmpty(newCP))
			throw new CaasRuntimeException("Provide classpath value to set");
		
		XmlNode newEntry=getEntry().clone();
		XmlNode config = new XmlNode(newEntry.getChildText("bussoapprocessorconfiguration/string"));
		XmlNode jreConfigNode = config.getChild("jreconfig");
		String existingCP = getClassPath();
		XmlNode cpNode = getCPNode(config);
		newCP = StringUtil.getUnixStyleFilePath(newCP);
		existingCP = StringUtil.getUnixStyleFilePath(existingCP);
		String seperator = (getSystem().os.equalsIgnoreCase("linux")) ? ":" : ";";
		if(jreConfigNode==null)
			jreConfigNode = config.add("jreconfig");
		if(StringUtil.isEmpty(existingCP))
			jreConfigNode.add("param").setAttribute("value", "-cp "+newCP);
		else{
			if(overwrite)
				cpNode.setAttribute("value", "-cp "+newCP);
			else
				cpNode.setAttribute("value", "-cp "+existingCP+seperator+newCP);
		}
		newEntry.setChildText("bussoapprocessorconfiguration/string", config.compact());
		updateEntry(newEntry);
		return getClassPath();
	}
	private int getAvailablePort() {
		// TODO: check all know connection points to avoid some clashes
		// Note that the official Cordys wizard does not seem to check this either
		int port=random.nextInt(64*1024-10000)+10000;
		return port; 
	}
}
