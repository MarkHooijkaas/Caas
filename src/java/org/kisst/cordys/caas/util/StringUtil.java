package org.kisst.cordys.caas.util;

import java.util.Map;

public class StringUtil {

	public static String quotedName(String name) {
		if (name.indexOf(' ')>=0 || name.indexOf('.')>=0)
			return '"'+name+'"';
		else
			return name;
	}

	public static String substitute(String str, Map<String, String> vars) {
		// TODO: more efficient algorithm
		for (String key:vars.keySet()) 
			str=str.replace("${"+key+"}", vars.get(key));
		return str;
	}
}
