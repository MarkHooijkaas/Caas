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

package org.kisst.cordys.caas.main;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import org.kisst.cordys.caas.util.ReflectionUtil;

public class GroovyCaasShell implements Command {
	public void run(String[] args) {
		if (args.length>0 && args[0].equals("--download")) {
			downloadAll();
			return;
		}
		runAny(args);
	}

	private static void downloadAll() {
		String libdir = getLibDir();
		System.out.println("Downloading necessary jar files to "+libdir);
		File d=new File(libdir);
		if (!d.exists()) {
			System.out.println("Making new directory "+libdir);
			d.mkdir();
		}
		// httpclient and necessary files
		download(libdir+"commons-httpclient-3.1.jar", "http://repo2.maven.org/maven2/commons-httpclient/commons-httpclient/3.1/commons-httpclient-3.1.jar");
		download(libdir+"commons-logging-1.0.4.jar", "http://repo2.maven.org/maven2/commons-logging/commons-logging/1.0.4/commons-logging-1.0.4.jar");
		download(libdir+"commons-codec-1.2.jar", "http://repo2.maven.org/maven2/commons-codec/commons-codec/1.2/commons-codec-1.2.jar");
		
		// log4j and jdom
		download(libdir+"log4j-1.2.13.jar", "http://repo2.maven.org/maven2/log4j/log4j/1.2.13/log4j-1.2.13.jar");
		download(libdir+"jdom-1.0.jar", "http://repo2.maven.org/maven2/jdom/jdom/1.0/jdom-1.0.jar");

		// groovy and files for interactive shell
		download(libdir+"groovy-all-1.6.5.jar", "http://repo1.maven.org/maven2/org/codehaus/groovy/groovy-all/1.6.5/groovy-all-1.6.5.jar");
		download(libdir+"commons-cli-1.2.jar", "http://repo1.maven.org/maven2/commons-cli/commons-cli/1.2/commons-cli-1.2.jar");
		download(libdir+"jline-0.9.94.jar", "http://repo1.maven.org/maven2/jline/jline/0.9.94/jline-0.9.94.jar");
	}
	
	private static String getLibDir() {
		// TODO: is this a safe way to determine the directory of the jar file?
		String dir=System.getProperty("java.class.path");
		dir=dir.replace('\\','/');
		int pos=dir.lastIndexOf("/");
		if (pos>=0)
			 return dir.substring(0,pos+1)+"lib/";
		else
			return "lib/";
	}

	private static void download(String filename, String url) {
		File f=new File(filename);
		if (f.exists()) {
			System.out.println("File "+filename+" allready exists, will not download this");
			return;
		}
		System.out.println("Downloading "+filename+" from "+url);
		InputStream in=null;
		FileOutputStream out=null;
		try {
			URL u = new URL(url);
			in = u.openStream();         // throws an IOException
			out = new FileOutputStream(f);
			//DataInputStream dis = new DataInputStream(new BufferedInputStream(is));
		    byte[] buf = new byte[4 * 1024]; // 4K buffer
		    int bytesRead;
		    while ((bytesRead = in.read(buf)) != -1)
		      out.write(buf, 0, bytesRead);

		} 
		catch (Exception e) { e.printStackTrace();	} 
		finally {
			if ( in!=null) try {  in.close(); } catch (IOException e) {	/* ignore */ }
			if (out!=null) try { out.close(); } catch (IOException e) {	/* ignore */ }
		}
	}

	private static void runAny(String[] args) {
		int i=0;
		boolean debug=false;
		String filename=null;
		while (i<args.length) {
			if (args[i].equals("--debug"))
				debug=true;
			else
				filename=args[i];
			i++;
		}
		if (filename==null)
			runInteractive(args, debug);
		else
			runFile(args, debug);
	}
	
	private static void runInteractive(String[] args, boolean debug) {
		int code=0;
		SecurityManager psm = null;
		try {
			psm = System.getSecurityManager();
			Object shell = ReflectionUtil.createObject("org.codehaus.groovy.tools.shell.Groovysh");
			System.setSecurityManager((SecurityManager) ReflectionUtil.createObject("org.codehaus.groovy.tools.shell.util.NoExitSecurityManager"));
			ReflectionUtil.invoke(shell, "executeCommand", new Object[]{"import org.kisst.cordys.caas.Caas"});
			ReflectionUtil.invoke(shell, "run", new Object[]{args});
		}
		catch (Exception e) {
			if (debug)
				e.printStackTrace();
			askTodDownload();
		}
		finally {
			System.setSecurityManager(psm);
		}
		System.exit(code);
	}

	private static void runFile(String[] args, boolean debug) {
		System.out.println("Running "+args[0]);
		int code=0;
		try {
			Class clz = ReflectionUtil.findClass("groovy.ui.GroovyMain");
			ReflectionUtil.invoke(clz, null, "main", new Object[]{args});
		}
		catch (Exception e) {
			if (debug)
				e.printStackTrace();
			askTodDownload();
		}
		System.exit(code);
	}

	private static void askTodDownload() {
		System.out.println("Some kind of error occured");
		System.out.println("This might be due to not having downloaded the necessary jar files");
		System.out.println("In order to do this one should execute the following command");
		System.out.println("\tjava -jar "+System.getProperty("java.class.path")+" --download");
		System.out.println("Or, if you behind a firewall or proxyserver, try the following command");
		System.out.println("\tjava -autoproxy -jar "+System.getProperty("java.class.path")+" --download");
	}
}
