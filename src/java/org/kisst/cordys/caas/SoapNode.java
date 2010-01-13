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

import java.util.LinkedHashMap;

import org.kisst.cordys.caas.support.ChildList;
import org.kisst.cordys.caas.support.EntryObjectList;
import org.kisst.cordys.caas.support.LdapObject;
import org.kisst.cordys.caas.support.LdapObjectBase;
import org.kisst.cordys.caas.util.XmlNode;




public class SoapNode extends LdapObjectBase {
	public final ChildList<SoapProcessor> soapProcessors= new ChildList<SoapProcessor>(this, SoapProcessor.class);
	public final ChildList<SoapProcessor> sp = soapProcessors;

	public final EntryObjectList<MethodSet> methodSets = new EntryObjectList<MethodSet>(this, "busmethodsets","ms");
	public final EntryObjectList<MethodSet> ms = methodSets;
	
	public final StringList namespaces= new StringList("labeleduri"); 
	public final StringList ns = namespaces;
	
	public final XmlProperty config = new XmlProperty("bussoapnodeconfiguration");
	
	public final XmlSubProperty ui_algorithm = new XmlSubProperty(config, "routing/@ui_algorithm");  
	public final XmlSubProperty ui_type = new XmlSubProperty(config, "routing/@ui_type");  
	public final XmlSubProperty numprocessors = new XmlSubProperty(config, "routing/numprocessors");  
	public final XmlSubProperty algorithm= new XmlSubProperty(config, "routing/algorithm");  

	
	protected SoapNode(LdapObject parent, String dn) {
		super(parent, dn);
	}
	@Override protected String prefix() { return "sn"; }

	public void recalcNamespaces() {
		LinkedHashMap<String, String> all=new LinkedHashMap<String, String>();
		for (MethodSet ms : methodSets) {
			if (ms!=null) {
				for (String s : ms.namespaces.get())
					all.put(s,s);
			}
		}
		XmlNode newEntry=getEntry().clone();
		XmlNode msNode=newEntry.getChild("labeleduri");
		if (msNode==null)
			msNode=newEntry.add("labeleduri");
		for (XmlNode child: msNode.getChildren())
			msNode.remove(child);
		for (String s: all.keySet())
			msNode.add("string").setText(s);
		updateLdap(newEntry);
	}
	
	
	/*
	 &lt;configurations&gt;
	   &lt;cancelReplyInterval&gt;30000&lt;/cancelReplyInterval&gt;
	   &lt;gracefulCompleteTime&gt;15&lt;/gracefulCompleteTime&gt;
	   &lt;abortTime&gt;5&lt;/abortTime&gt;
  	   &lt;jreconfig&gt;&lt;param value="-Xmx64M"/&gt;&lt;/jreconfig&gt;
	   &lt;routing ui_type="loadbalancing" ui_algorithm="failover"&gt;
	     &lt;preference&gt;1&lt;/preference&gt;
	   &lt;/routing&gt;
	   &lt;loggerconfiguration&gt;&lt;systempolicy&gt;true&lt;/systempolicy&gt;&lt;log4j:configuration xmlns:log4j="http://jakarta.apache.org/log4j"&gt;&lt;renderer renderedClass="com.eibus.util.logger.internal.LocalizableLogMessage" renderingClass="com.eibus.util.logger.internal.TextRenderer"/&gt;&lt;renderer renderedClass="com.eibus.util.logger.internal.LogMessage" renderingClass="com.eibus.util.logger.internal.TextRenderer"/&gt;&lt;root&gt;&lt;priority value="error"/&gt;&lt;appender-ref ref="DailyRollingFileAppender"/&gt;&lt;/root&gt;&lt;category name="com.eibus.security.acl"&gt;&lt;priority value="error"/&gt;&lt;/category&gt;&lt;category name="com.eibus.license"&gt;&lt;priority value="error"/&gt;&lt;/category&gt;&lt;category name="com.eibus.directory"&gt;&lt;priority value="error"/&gt;&lt;/category&gt;&lt;category name="com.eibus.soap"&gt;&lt;priority value="error"/&gt;&lt;/category&gt;&lt;category name="com.eibus.transport.SOAPMessage"&gt;&lt;priority value="error"/&gt;&lt;/category&gt;&lt;category name="com.eibus.transport"&gt;&lt;priority value="error"/&gt;&lt;/category&gt;&lt;category name="com.eibus.tools"&gt;&lt;priority value="error"/&gt;&lt;/category&gt;&lt;category name="com.eibus.util"&gt;&lt;priority value="error"/&gt;&lt;/category&gt;&lt;category name="org.kisst.cordys.relay.RelayTrace"&gt;&lt;priority value="error"/&gt;&lt;/category&gt;&lt;category name="org.kisst.cordys.relay.RelayTimer"&gt;&lt;priority value="error"/&gt;&lt;/category&gt;&lt;category name="httpclient.wire"&gt;&lt;priority value="error"/&gt;&lt;/category&gt;&lt;appender name="DailyRollingFileAppender" class="org.apache.log4j.DailyRollingFileAppender"&gt;&lt;param name="File" value="esb#relay_service2#relayconnector_processor.xml"/&gt;&lt;param name="DatePattern" value=".yyyy-MM-dd"/&gt;&lt;layout class="org.apache.log4j.xml.XMLLayout"&gt;&lt;param name="locationInfo" value="true"/&gt;&lt;/layout&gt;&lt;/appender&gt;&lt;/log4j:configuration&gt;&lt;/loggerconfiguration&gt;
	   &lt;spyPublish&gt;false&lt;/spyPublish&gt;&lt;spyFile&gt;&lt;/spyFile&gt;&lt;spyLogger&gt;&lt;/spyLogger&gt;&lt;spyLevels&gt;&lt;/spyLevels&gt;&lt;spyCategories&gt;&lt;/spyCategories&gt;
	   &lt;configuration implementation="org.kisst.cordys.relay.RelayConnector" htmfile="/cordys/kisst.org/RelayConnector-1.0/config.html"&gt;
	     &lt;classpath xmlns="http://schemas.cordys.com/1.0/xmlstore"&gt;
	       &lt;location&gt;/kisst.org/RelayConnector-1.0/commons-logging-1.0.4.jar&lt;/location&gt;
	       &lt;location&gt;/kisst.org/RelayConnector-1.0/backport-util-concurrent.jar&lt;/location&gt;
	       &lt;location&gt;/kisst.org/RelayConnector-1.0/ehcache-1.5.0.jar&lt;/location&gt;
	       &lt;location&gt;/kisst.org/RelayConnector-1.0/groovy-all-1.6.2.jar&lt;/location&gt;
	       &lt;location&gt;/kisst.org/RelayConnector-1.0/RelayConnector-1.0.jar&lt;/location&gt;
	     &lt;/classpath&gt;
	     &lt;ConfigLocation&gt;D:/config/RelayConnector.properties&lt;/ConfigLocation&gt;
	   &lt;/configuration&gt;
	 &lt;/configurations&gt;
	 */
	public void createSoapProcessor(String name, String machine, boolean automatic, XmlNode config) {
		XmlNode newEntry=newEntryXml("", name,"bussoapprocessor");
		newEntry.add("description").add("string").setText(name);
		newEntry.add("computer").add("string").setText(machine); // TODO
		newEntry.add("busosprocesshost");
		newEntry.add("automaticstart").add("string").setText(""+automatic);
		newEntry.add("bussoapprocessorconfiguration").add("string").setText(config.compact());
		createInLdap(newEntry);
		soapProcessors.clear();
	}
	public void createSoapProcessor(String name, Connector conn) {
		XmlNode config=new XmlNode("configurations"); 
		config.add("cancelReplyInterval").setText("30000");
		config.add("gracefulCompleteTime").setText("15");
		config.add("abortTime").setText("5");
		config.add("jreconfig").add("param").setAttribute("value","-Xmx64M");
		config.add("loggerconfiguration");
		XmlNode config2=config.add("configuration");
		config2.setAttribute("implementation", conn.getData().getChildText(("step/implementation")));
		config2.setAttribute("htmfile", conn.getData().getChildText(("step/url")));
		config2.add(conn.getData().getChild("step/classpath").clone());
		createSoapProcessor(name, getSystem().machines.get(0).getName(), false, config);
	}	
}
