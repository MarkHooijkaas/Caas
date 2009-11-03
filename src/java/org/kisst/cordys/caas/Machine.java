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

import java.util.LinkedList;
import java.util.List;

import org.kisst.cordys.caas.support.CordysObject;
import org.kisst.cordys.caas.util.XmlNode;

public class Machine extends CordysObject {
	private final SoapProcessor monitor;
	private final String hostname;
	
	protected Machine(SoapProcessor monitor) {
		this.monitor=monitor;
		String tmp=monitor.getName();
		this.hostname=tmp.substring(tmp.indexOf("monitor@")+8);
	}

	@Override public String toString() { return getVarName();}
	@Override public String getName() { return hostname;}
	@Override public String getKey() { return "machine:"+getName();	}
	@Override public CordysSystem getSystem() { return monitor.getSystem();	}
	@Override public String getVarName() { return getSystem().getVarName()+".machine."+getName();}

	public SoapProcessor getMonitor() { return monitor; }
	
	public void refreshSoapProcessors() {
		XmlNode method=new XmlNode("List", xmlns_monitor);
		XmlNode response=monitor.call(method);
		for (XmlNode s: response.getChildren("tuple")) {
			XmlNode workerprocess=s.getChild("old/workerprocess");
			String dn=workerprocess.getChildText("name");
			SoapProcessor obj= (SoapProcessor) getSystem().getLdap(dn);
			obj.setWorkerprocess(workerprocess);
		}
	}

	public void loadIsvp(String filename) {
		filename=filename.trim();
		if (filename.endsWith(".isvp"))
			filename=filename.substring(0,filename.length()-5);
		XmlNode method=new XmlNode("GetISVPackageDefinition", xmlns_isv);
		XmlNode file=method.add("file");
		file.setText(filename);
		file.setAttribute("type", "isvpackage");
		file.setAttribute("detail", "false");
		file.setAttribute("wizardsteps", "true");
		XmlNode details=monitor.call(method);
		
		method=new XmlNode("LoadISVPackage", xmlns_isv);
		method.add("url").setText("http://"+hostname+"/cordys/wcp/isvcontent/packages/"+filename+".isvp");
		method.add(details.getChild("ISVPackage").detach());
		monitor.call(method);
	}

	public void unloadIsvp(Isvp isvp, boolean deletereferences) {
		XmlNode method=new XmlNode("UnloadISVPackage", xmlns_isv);
		XmlNode file=method.add("file");
		file.setText(isvp.getBasename());
		if (deletereferences)
			file.setAttribute("deletereference", "true");
		else
			file.setAttribute("deletereference", "false");
		monitor.call(method);
		//TODO: do this only for last machine??? getSystem().removeLdap(isvp.getDn());
	}
	
	public List<String> getIsvpFiles() {
		LinkedList<String> result=new LinkedList<String>();
		XmlNode method=new XmlNode("ListISVPackages", xmlns_isv);
		method.add("type").setText("ISVPackage");
		XmlNode response=monitor.call(method);
		for (XmlNode child: response.getChildren()) {
			String url=child.getText();
			result.add(url.substring(url.lastIndexOf("/")+1));
		}
		return result;
	}
}
