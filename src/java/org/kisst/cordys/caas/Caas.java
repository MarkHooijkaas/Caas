package org.kisst.cordys.caas;

import java.io.IOException;
import java.io.InputStream;
import java.net.ConnectException;
import java.util.Properties;

import org.kisst.cordys.caas.soap.DummyCaller;
import org.kisst.cordys.caas.soap.HttpClientCaller;
import org.kisst.cordys.caas.util.FileUtil;
import org.kisst.cordys.caas.util.XmlNode;

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
			if (! (e.getCause() instanceof ConnectException))
				e.printStackTrace();
			System.out.println("FAILED");
			return null;
		}
	}
	public static CordysSystem loadFromDump(String filename) {
		String name=filename.substring(0,filename.indexOf("."));
		int pos=name.lastIndexOf("/");
		if (pos>=0)
			name=name.substring(pos+1);
		return loadFromDump(filename, name);
	}
	public static CordysSystem loadFromDump(String filename, String name) {
		XmlNode xml=new XmlNode(FileUtil.loadString(filename));
		DummyCaller caller=new DummyCaller(xml);
		if (name==null)
			name=caller.getName();
		return new CordysSystem(name, caller);
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
