package org.kisst.cordys.caas.cm;

import java.util.LinkedList;
import java.util.List;


public abstract class CompositeObjective implements Objective {
	protected final String name;
	protected int status;
	protected String message;
	protected final LinkedList<Objective> entries=new LinkedList<Objective>();

	public CompositeObjective(String name) {
		this.name=name; 
	}
	

	public String toString() {
		String type=getClass().getSimpleName();
		return type+"("+name+")";
	}

	public List<Objective> getChildren() { return entries; }
	public String getMessage() { return message; }
	public int getStatus() { return status; }

	
	public int check(Ui ui) {
		ui.checking(this);
		status=OK;
		for (Objective e:entries)
			status=Math.max(status,e.check(ui));
		ui.readyWith(this);
		return status;
	}
	
	public void configure(Ui ui) { 
		ui.configuring(this);
		for (Objective e:entries)
			e.configure(ui);
		ui.readyWith(this);
	}

	public void purge(Ui ui) { 
		for (Objective e:entries)
			e.purge(ui);
	}
}