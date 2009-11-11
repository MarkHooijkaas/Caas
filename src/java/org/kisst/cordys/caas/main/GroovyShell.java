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

import org.kisst.cordys.caas.util.ReflectionUtil;

public class GroovyShell extends CommandBase {
	public GroovyShell() { super("", "start a groovy shell"); }

	private Cli cli=new Cli();
	private Cli.StringOption terminal = cli.stringOption("t", "terminal", "choose the terminal type out of unix|win|none", null);
	
	@Override public void run(String[] args) {
		int code=0;
		args=cli.parse(args);
		if (terminal.isSet())
			setTerminal(terminal.get());

		SecurityManager psm = null;
		try {
			psm = System.getSecurityManager();
			Object shell = ReflectionUtil.createObject("org.codehaus.groovy.tools.shell.Groovysh");
			System.setSecurityManager((SecurityManager) ReflectionUtil.createObject("org.codehaus.groovy.tools.shell.util.NoExitSecurityManager"));
			ReflectionUtil.invoke(shell, "executeCommand", new Object[]{"import org.kisst.cordys.caas.Caas"});
			ReflectionUtil.invoke(shell, "run", new Object[]{args});
		}
		finally {
			System.setSecurityManager(psm);
		}
		System.exit(code);
	}

	@Override public String getHelp() { return "\nOPTIONS\n"+cli.getSyntax("\t"); } 
	
	private void setTerminal(String type) {
		if (type==null)
			return;

		if ("auto".equals(type))
			type = null;
		else if ("unix".equals(type))
			type = "jline.UnixTerminal";
		else if ("win".equals(type))
			type = "jline.WindowsTerminal";
		else if ("none".equals(type))
			type = "jline.UnsupportedTerminal";
		else
			throw new RuntimeException("unknown terminal type "+type);

        if (type != null)
            System.setProperty("jline.terminal", type);
    }
}
