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



abstract public class CommandBase implements Command {
	private final String usage;
	private final String summary;
	
	public CommandBase(String usage, String summary) { 
		this.usage=usage;
		this.summary=summary;
	}
	abstract public void run(String[] args);
	
	public String getSyntax() { return usage; }
	public String getHelp()  { return null;}
	public String getSummary(){ return summary;}

	public void checkHelp(String prefix, String[] args) {
		if (args.length==0)
			return;
		if(args[0].equals("help") || args[0].equals("--help"))
			System.out.println(getHelp());
	}
}