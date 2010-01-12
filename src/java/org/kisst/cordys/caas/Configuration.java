package org.kisst.cordys.caas;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;

import org.kisst.cordys.caas.pm.Template;
import org.kisst.cordys.caas.util.FileUtil;

public class Configuration {
	private final Organization org;
	private final LinkedHashMap<String,String>props=new LinkedHashMap<String,String>();
	
	public Configuration(String filename) {
		Properties p=new Properties();
		FileUtil.load(p, filename);
		for (Object key: p.keySet())
			props.put((String)key, (String)p.get(key)); 
		// TODO: use properties from file? although this might create multiple CordysSystem objects pointing to same system
		org=Caas.getSystem(props.get("system")).organizations.get(props.get("org"));
	}

	public Organization getOrganization() {	return org;	}
	public String get(String key) { return props.get(key); }
	public void put(String key, String value) { props.put(key, value); }
	public void apply(Template templ) { templ.apply(org, props); }
	@SuppressWarnings("unchecked")
	public Map<String,String> getProps() { return (Map<String, String>) props.clone(); } // T
}
