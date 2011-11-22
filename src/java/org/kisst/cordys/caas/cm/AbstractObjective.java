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

import java.util.LinkedList;
import java.util.List;

import org.kisst.cordys.caas.CordysSystem;

public abstract class AbstractObjective implements Objective {
	private static final LinkedList<Objective> emptyChildren=new LinkedList<Objective>();
	
	private final CordysSystem system;
	protected int status=OK;
	protected String message="";
	
	public AbstractObjective(CordysSystem system) {
		this.system=system;
	}
	abstract protected void myCheck(Ui ui);
	abstract protected void myConfigure(Ui ui);
	abstract protected void myPurge(Ui ui);
	
	
	public List<Objective> getChildren() { return emptyChildren;	}
	public String getMessage() { return message;}
	public int getStatus() { return status;}
	public CordysSystem getSystem() { return system; }


	public int check(Ui ui) {
		ui.checking(this);
		myCheck(ui);
		ui.readyWith(this);
		return status;
	}
	
	public void configure(Ui ui) {
		ui.checking(this);
		myConfigure(ui);
		ui.readyWith(this);
	}
	public void purge(Ui ui) {
		myPurge(ui);
	}
}