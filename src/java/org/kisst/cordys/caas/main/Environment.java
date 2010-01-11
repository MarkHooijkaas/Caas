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

import java.util.Properties;

import org.kisst.cordys.caas.util.FileUtil;

public class Environment {
	private final static Environment singleton=new Environment();
	public static Environment get() { return singleton; }
	private Environment() {}
	
	public boolean debug=false;
	public boolean quiet=false;
	public boolean verbose=false;
	private Properties props=new Properties();
	
	private void log(String type, String msg){ System.out.println(type+" "+msg);}
	public void debug(String msg) { if (debug   && ! quiet) log("DEBUG",msg); }
	public void info(String msg)  { if (verbose && ! quiet) log("INFO ", msg); }
	public void warn(String msg)  { if (! quiet) log("WARN ", msg); }
	public void error(String msg)  { log("ERROR", msg); }
	
	public String getProp(String key, String defaultValue) { return props.getProperty(key, defaultValue); }
	public void loadProperties(String filename) {
		props.clear();
		FileUtil.load(props, filename);
	}
}
