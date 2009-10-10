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

import org.jdom.Element;
import org.jdom.Namespace;
import org.kisst.cordys.caas.util.JdomUtil;

public class SoapProcessor extends CordysLdapObject {

	private static final Namespace nsmonitor=Namespace.getNamespace("http://schemas.cordys.com/1.0/monitor");

	private Element workerprocess;
	protected SoapProcessor(LdapObject parent, String dn) {
		super(parent, dn);
	}

	public void setWorkerprocess(Element workerprocess) {
		this.workerprocess=workerprocess;
	}
	public Element getWorkerprocess() {
		if (workerprocess!=null && getSystem().getCache())
			return this.workerprocess;
		Element method=new Element("List", CordysSystem.xmlns_monitor);
		Element response=soapCall(method);
		for (Object s: response.getChildren("tuple", null)) {
			Element workerprocess=((Element) s).getChild("old", null).getChild("workerprocess",null);
			String dn=workerprocess.getChildText("name",null);
			if (dn.equals(this.dn)) {
				this.workerprocess=workerprocess;
				return workerprocess;
			}
		}
		throw new RuntimeException("Could not find processor details for"+this.dn);
	}

	private int getIntChild(Element x, String name) {
		String result=x.getChildText(name,null);
		if (result==null)
			return -1;
		else
			return Integer.parseInt(result);
	}
	public String getStatus(){ return getWorkerprocess().getChildText("status",null); } 
	public int getPid()            { return getIntChild(getWorkerprocess(), "process-id"); } 
	public int getNomMemory()      { return getIntChild(getWorkerprocess(), "totalNOMMemory"); } 
	public int getNomNodesMemory() { return getIntChild(getWorkerprocess(), "totalNOMNodesMemory"); } 
	public int getCpuTime()        { return getIntChild(getWorkerprocess(), "totalCpuTime"); } 
	public int getVirtualMemory()  { return getIntChild(getWorkerprocess(), "virtualMemoryUsage"); } 
	public int getResidentMemory() { return getIntChild(getWorkerprocess(), "residentMemoryUsage"); } 
	public int getSequenceNumber() { return getIntChild(getWorkerprocess(), "sequence-number"); } 
	public int getPreference()     { return getIntChild(getWorkerprocess(), "preference"); } 
	public int getBusdocs()        { return getIntChild(getWorkerprocess(), "busdocs"); } 
	public int getProcessingTime() { return getIntChild(getWorkerprocess(), "processing-time"); } 
	public int getLastTime()       { return getIntChild(getWorkerprocess(), "last-time"); } 

	public void start() {
		Element method=new Element("Start", nsmonitor);
		method.addContent(new Element("dn").setText(dn));
		soapCall(method);
	}
	public void stop() {
		Element method=new Element("Stop", nsmonitor);
		method.addContent(new Element("dn").setText(dn));
		soapCall(method);
	}
	public void restart() {
		Element method=new Element("Restart", nsmonitor);
		method.addContent(new Element("dn").setText(dn));
		soapCall(method);
	}
	public String getComputer() {
		return getEntry().getChild("computer",null).getChildText("string",null);
	}
	public boolean getAutomatic() {
		String s=getEntry().getChild("automaticstart",null).getChildText("string",null);
		return s.equals("true");
	}
	public Element getConfig() {
		String s=getEntry().getChild("bussoapprocessorconfiguration",null).getChildText("string",null);
		return JdomUtil.fromString(s);
	}
	public boolean getUseSystemLogPolicy() {
		Element e=getConfig().getChild("loggerconfiguration",null);
		if (e==null)
			return true; // defautl is true
		return e.getChildText("systempolicy",null).equals("true");
	}
}
