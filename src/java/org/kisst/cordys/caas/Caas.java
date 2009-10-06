package org.kisst.cordys.caas;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.kisst.cordys.caas.soap.HttpClientCaller;

public class Caas {
	public static CordysSystem connect(String filename) {
		String name=filename.substring(0,filename.indexOf("."));
		int pos=name.lastIndexOf("/");
		if (pos>=0)
			name=name.substring(pos+1);
		return connect(filename, name);
	}
	public static CordysSystem connect(String filename, String name) {
		try {
			System.out.print("Connecting to system "+name+" ("+filename+") ... ");
			HttpClientCaller caller = new HttpClientCaller(filename);
			CordysSystem result = new CordysSystem(name, caller);
			System.out.println("OK");
			return result;
		}
		catch (Exception e) {
			// Catch any exceptions so it won't be a problem if anything fails in the Startup script
			//e.printStackTrace();
			System.out.println("FAILED");
			return null;
		}
	}
	public static String getVersion() {
		InputStream in = Caas.class.getResourceAsStream("/version.properties");
		if (in==null)
			return "unknown-version";
		Properties props=new Properties();
		try {
			props.load(in);
		} catch (IOException e) { throw new RuntimeException(e);}
		return props.getProperty("project.version");
	}
}
