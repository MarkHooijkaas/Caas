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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import org.kisst.cordys.caas.Caas;
import org.kisst.cordys.caas.CordysSystem;
import org.kisst.cordys.caas.Isvp;
import org.kisst.cordys.caas.Organization;
import org.kisst.cordys.caas.User;
import org.kisst.cordys.caas.cm.CaasPackage;
import org.kisst.cordys.caas.cm.CcmFilesObjective;
import org.kisst.cordys.caas.cm.Objective;
import org.kisst.cordys.caas.cm.Template;
import org.kisst.cordys.caas.cm.gui.CcmGui;
import org.kisst.cordys.caas.exception.CaasRuntimeException;
import org.kisst.cordys.caas.util.FileUtil;
import org.kisst.cordys.caas.util.StringUtil;


public class PmCommand extends CompositeCommand {
	
	private abstract class HostCommand extends CommandBase {
		public HostCommand(String usage, String summary) { super(usage, summary); }
		protected final Cli cli=new Cli();
		private final Cli.StringOption systemOption= cli.stringOption("s", "system", "the system to use", null);
		private final Cli.StringOption orgOption= cli.stringOption("o", "organization", "the organization to use", null);
				
		protected CordysSystem getSystem() { return Caas.getSystem(Caas.defaultSystem); }
		protected Organization getOrg(String defaultOrg) {
			if (orgOption.isSet())				
				return getSystem().org.getByName(orgOption.get());
			else
				return getSystem().org.getByName(defaultOrg);
		}
		protected String[] checkArgs(String[] args) {
			args=cli.parse(args);
			if (systemOption.isSet())
				Caas.defaultSystem=systemOption.get();
			return args;
		}
		@Override public String getHelp() {
			return "\nOPTIONS\n"+cli.getSyntax("\t");
		}
	}
	
	private ArrayList<String> getFiles(String[] args) {
		ArrayList<String> result = new ArrayList<String>();
		for (String path: args) {
			File f=new File(path);
			if (f.isFile())
				result.add(path);
			else if (f.isDirectory()) {
				for (String p2: f.list()) {
					File f2=new File(f,p2);
					if (f2.isFile())
						result.add(f2.getPath());
				}
			}
			else throw new RuntimeException("Unknown path "+path);
		}
		return result;
	}

	private TextUi ui = new TextUi();

	private Command check=new HostCommand("[options] <ccm file>|<dir> ...", "validates the given install info") {
		@Override public void run(String[] args) {
			args=checkArgs(args);
			Organization org=null;
			for (String path: getFiles(args)) {
				CaasPackage p=new CaasPackage(path,getSystem());
				if (org==null || ! org.getName().equals(p.getDefaultOrgName()))
					org = getOrg(p.getDefaultOrgName());
				boolean result=p.check(ui)==0;
				System.out.println(path+"\t"+result);
			}
		}
	};
	
	private Command gui=new HostCommand("[options] <ccm file>|<dir> ...", "shows gui for the given install info") {
		@Override public void run(String[] args) {
			args=checkArgs(args);
			Objective target=new CcmFilesObjective(args, getSystem());
			CcmGui gui=new CcmGui(target);
			gui.run();
		}
	};

	private Command configure=new HostCommand("[options] <ccm file>|<dir> ...", "installs the given isvp") {
		@Override public void run(String[] args) { 
			args=checkArgs(args);
			Organization org=null;
			for (String path: getFiles(args)) {
				CaasPackage p=new CaasPackage(path,getSystem());
				if (org==null || ! org.getName().equals(p.getDefaultOrgName()))
					org = getOrg(p.getDefaultOrgName());
				p.configure(ui);
			}
		}
	};
	private Command purge=new HostCommand("[options] <ccm file>|<dir> ...", "removes the given isvp") {
		@Override public void run(String[] args) { 
			args=checkArgs(args);
			Organization org=null;
			for (String path: getFiles(args)) {
				CaasPackage p=new CaasPackage(path,getSystem());
				if (org==null || ! org.getName().equals(p.getDefaultOrgName()))
					org = getOrg(p.getDefaultOrgName());
				p.purge(ui);
			}
		}
	};
	private Command deductUserCcmFiles=new HostCommand("[options]", "create a ccm files for each user of the given organization") {
		@Override public void run(String[] args) { 
			args=checkArgs(args);
			Organization org= getOrg(null);
			for (User u: org.users) {
				String filename="user-"+u.getName()+".ccm";
				File f= new File(filename);
				if (f.exists()) {
					System.out.println("skipping file "+filename+", already exists");
					continue;
				}
				Template tpl=new Template(org, null, null, u);
				tpl.save(filename);
			}
		}
	};

	private Command deductIsvpCcmFiles=new HostCommand("[options]", "create a ccm files for each isvp of the given organization") {
		@Override public void run(String[] args) { 
			args=checkArgs(args);
			Organization org= getOrg(null);
			for (Isvp isvp: org.getSystem().isvps) {
				String filename="isvp-"+isvp.getName()+".ccm";
				File f= new File(filename);
				if (f.exists()) {
					System.out.println("skipping file "+filename+", already exists");
					continue;
				}
				Template tpl=new Template(org, null, isvp, null);
				if (! tpl.isEmpty())
					tpl.save(filename);
			}
		}
	};
	
	
	private Command template=new HostCommand("[options] <template file>", "create a template based on the given organization") {
		private final Cli.StringOption isvpName= cli.stringOption("i", "isvpName", "the isvpName to use for custom content", null);
		@Override public void run(String[] args) { 
			args=checkArgs(args);
			String orgz = System.getProperty("template.org");
			Template templ = new Template(getOrg(orgz), isvpName.get());
			templ.save(args[0]);
		}
	};
	private Command create=new HostCommand("[options] <template file>", "create elements in an organization based on the given template") {
		@Override public void run(String[] args) { 
			args=checkArgs(args);
			Template templ=new Template(FileUtil.loadString(args[0]));			
			String orgz = System.getProperty("create.org");
			Map<String,String> map = loadSystemProperties(this.getSystem().getName());
			templ.apply(getOrg(orgz), map);
		}
	};
	
	public PmCommand(String name) {
		super("caas "+name,"run a caas package manager command"); 
		//options.addOption("o", "org", true, "override the default organization");
		commands.put("gui", gui);
		commands.put("check", check);
		commands.put("configure", configure);
		commands.put("purge", purge);
		commands.put("template", template);
		commands.put("create", create);
		commands.put("deduct-user-ccm", deductUserCcmFiles);
		commands.put("deduct-isvp-ccm", deductIsvpCcmFiles);
	}
	
	/**
	 * This method looks up for the properties file and loads it after finding it.
	 * It first looks up at the location mentioned in 'system.<<systemName>>.properties.file' property in caas.conf
	 * If not then looks up for the '<<systemName>>.properties' file in the current directory
	 * If not then look up for the '<<systemName>>.properties' in logged in user's home directory
	 * 
	 * @param systemName - Cordys system name as mentioned in the caas.conf file
	 * @return map - A Map object containing all the properties of the given system
	 */
	@SuppressWarnings("unchecked")
	private Map<String, String> loadSystemProperties(String systemName){

		String fileName=null; 
		Map<String, String> map=null;
		if(systemName==null)
			throw new CaasRuntimeException("Unable to load the properties as the Cordys system name is null");
		
		//File name of the properties file mentioned in caas.conf file - Highest Precedence
		String propsFileInConf = Environment.get().getProp("system."+systemName+".properties.file", null);
		//File name of the properties file in current directory - Second Highest Precedence
		String propsFileInPWD = systemName+".properties";
		//File name of the properties file in user's home directory - Lowest Precedence
		String propsFileInHomeDir = System.getProperty("user.home")+"/config/caas/"+systemName+".properties";
		Properties props = new Properties();
		//Convert the file paths to Unix file path format
		propsFileInConf = StringUtil.getUnixStyleFilePath(propsFileInConf);
		propsFileInHomeDir = StringUtil.getUnixStyleFilePath(propsFileInHomeDir);
		
		String[] fileNames = new String[]{propsFileInConf, propsFileInPWD, propsFileInHomeDir};
		//Determine the file that need to be considered for loading
		//To do so, Loop over the files as per their precedence and check for their existence  
		for(String  aFileName:fileNames){ 
			if(FileUtil.isFileExists(aFileName)){ 
				fileName = aFileName;
				break;
			}
		}
		
		//Load the properties file and convert it to a HashMap
		if(fileName!=null){
			FileUtil.load(props, fileName);
			map = new HashMap<String, String>((Map) props);	
		}else{
			//Throw a warning if none of the files in the precedence list are existing
			Environment.get().warn("No file is configured for property 'system."+systemName+".properties.file' in caas.conf. Make sure there are no variables to be resolved in template file.");
		}
		
		return map;
	}

}
