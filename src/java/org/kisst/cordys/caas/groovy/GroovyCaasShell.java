package org.kisst.cordys.caas.groovy;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import org.kisst.cordys.caas.util.ReflectionUtil;

public class GroovyCaasShell {
	public static void main(String[] args) {
		System.out.println("Caas: Cordys Administration Automation Scripting");
		if (args.length>0 && args[0].equals("--download"))
			downloadAll();
		else
			run(args);
	}

	private static void downloadAll() {
		String jardir = getJarDir();
		System.out.println("Downloading necessary jar files to "+jardir);
		File d=new File(jardir);
		if (!d.exists()) {
			System.out.println("Making new directory "+jardir);
			d.mkdir();
		}
		// httpclient and necessary files
		download(jardir+"commons-httpclient-3.1.jar", "http://repo2.maven.org/maven2/commons-httpclient/commons-httpclient/3.1/commons-httpclient-3.1.jar");
		download(jardir+"commons-logging-1.0.4.jar", "http://repo2.maven.org/maven2/commons-logging/commons-logging/1.0.4/commons-logging-1.0.4.jar");
		download(jardir+"commons-codec-1.2.jar", "http://repo2.maven.org/maven2/commons-codec/commons-codec/1.2/commons-codec-1.2.jar");
		
		// log4j and jdom
		download(jardir+"log4j-1.2.13.jar", "http://repo2.maven.org/maven2/log4j/log4j/1.2.13/log4j-1.2.13.jar");
		download(jardir+"jdom-1.0.jar", "http://repo2.maven.org/maven2/jdom/jdom/1.0/jdom-1.0.jar");

		// groovy and files for interactive shell
		download(jardir+"groovy-all-1.6.5.jar", "http://repo1.maven.org/maven2/org/codehaus/groovy/groovy-all/1.6.5/groovy-all-1.6.5.jar");
		download(jardir+"commons-cli-1.2.jar", "http://repo1.maven.org/maven2/commons-cli/commons-cli/1.2/commons-cli-1.2.jar");
		download(jardir+"jline-0.9.94.jar", "http://repo1.maven.org/maven2/jline/jline/0.9.94/jline-0.9.94.jar");
	}
	
	private static String getJarDir() {
		// TODO: is this a safe way to determine the directory of the jar file?
		String dir=System.getProperty("java.class.path");
		dir=dir.substring(0,dir.indexOf("caas.jar"))+"lib/";
		return dir;
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

	private static void run(String[] args) {
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
			askTodDownload();
		}
		finally {
			System.setSecurityManager(psm);
		}
		System.exit(code);
	}
	
	private static void askTodDownload() {
		System.out.println("Some kind of error occured");
		System.out.println("This might be due to not having donwloaded necessary jar files");
		System.out.println("In order to do this one should execute the following command");
		System.out.println("\tjava -jar caas.jar --download");
		System.out.println("Or, if you behind a firewall or proxyserver, try the following command");
		System.out.println("\tjava -autoproxy -jar caas.jar --download");
	}
}
