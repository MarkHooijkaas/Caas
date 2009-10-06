package org.kisst.cordys.caas;

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
}
