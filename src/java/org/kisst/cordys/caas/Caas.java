package org.kisst.cordys.caas;

import org.kisst.cordys.caas.soap.HttpClientCaller;

public class Caas {
	public static CordysSystem connect(String filename, String name) {
		//System.setProperty("org.apache.commons.logging.simplelog.log.org.apache.commons.httpclient", "error");
		HttpClientCaller caller = new HttpClientCaller(filename);
		String rootdn= caller.props.getProperty("cordys.rootdn");
		return new CordysSystem(name, caller, rootdn);
	}
}
