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

import java.io.IOException;
import java.io.InputStream;
import java.net.ConnectException;
import java.util.LinkedHashMap;
import java.util.Properties;

import org.kisst.cordys.caas.main.Environment;
import org.kisst.cordys.caas.pm.PackageManager;
import org.kisst.cordys.caas.pm.Template;
import org.kisst.cordys.caas.soap.DummyCaller;
import org.kisst.cordys.caas.soap.HttpClientCaller;
import org.kisst.cordys.caas.soap.NativeCaller;
import org.kisst.cordys.caas.soap.SoapCaller;
import org.kisst.cordys.caas.util.FileUtil;
import org.kisst.cordys.caas.util.XmlNode;

public class Caas {
	public static Configuration config(String filename) { return new Configuration(filename); }
	public static Template template(String filename) { return new Template(FileUtil.loadString(filename)); }
	
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

	private static LinkedHashMap<String, CordysSystem> systemCache = new LinkedHashMap<String, CordysSystem>();
	public static CordysSystem getSystem(String name) {
		CordysSystem result=systemCache.get(name);
		if (result!=null)
			return result;
		Environment env=Environment.get();
		String classname=env.getProp("system."+name+".gateway.class", null);
		try {
			System.out.print("Connecting to system "+name+" ... ");
			SoapCaller caller;
			if (classname==null || classname.equals("HttpClientCaller"))
				caller = new HttpClientCaller(name);
			else if (classname.equals("NativeCaller"))
				caller=new NativeCaller(name);
			else
				throw new RuntimeException("Unknown SoapCaller class "+classname);
			result = new CordysSystem(name, caller);
			System.out.println("OK");
			return result;
		}
		catch (Exception e) {
			System.out.println("FAILED");
			// Catch any exceptions so it won't be a problem if anything fails in the Startup script
			//if (! (e.getCause() instanceof ConnectException))
			if (Environment.get().debug)
				e.printStackTrace();
			Environment.get().error(e.getMessage());
			return null;
		}		
	}
	public static String defaultSystem=null;
	public static CordysSystem getDefaultSystem() {
		if (defaultSystem==null)
			defaultSystem=Environment.get().getProp("caas.defaultSystem", "default");
		return getSystem(defaultSystem);
	}

	public final static PackageManager pm=new PackageManager();
}
