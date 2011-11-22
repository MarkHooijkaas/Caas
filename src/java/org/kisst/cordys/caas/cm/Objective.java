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

package org.kisst.cordys.caas.cm;

import java.util.List;

import org.kisst.cordys.caas.CordysSystem;


public interface Objective {
	public interface Ui {
		public void info(Objective obj, String msg);
		public void warn(Objective obj, String msg);
		public void error(Objective obj, String msg);
		public void checking(Objective obj);
		public void configuring(Objective obj);
		public void readyWith(Objective obj);
	}
	public static final int OK=0;
	public static final int WARN=1;
	public static final int ERROR=2;
	
	public int check(Ui ui);
	public void configure(Ui ui);
	public void purge(Ui ui);

	public List<Objective> getChildren();
	public int getStatus();
	public String getMessage();
	public CordysSystem getSystem();
}