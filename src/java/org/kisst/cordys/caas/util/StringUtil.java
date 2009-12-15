package org.kisst.cordys.caas.util;

public class StringUtil {

	public static String quotedName(String name) {
		if (name.indexOf(' ')>=0 || name.indexOf('.')>=0)
			return '"'+name+'"';
		else
			return name;
	}
}
