package org.kisst.cordys.caas.cm;

import java.util.List;

import org.kisst.cordys.caas.CordysSystem;
import org.kisst.cordys.caas.Isvp;
import org.kisst.cordys.caas.Organization;
import org.kisst.cordys.caas.main.Environment;
import org.kisst.cordys.caas.support.ChildList;
import org.kisst.cordys.caas.support.LdapObject;
import org.kisst.cordys.caas.util.XmlNode;

public class EntryObjective implements Objective {
	private static final Environment env=Environment.get();

	private final ObjectiveBase parent;
	public final String propName;
	public final String isvpName;
	public final String name;
	private final Organization org;

	public EntryObjective(Organization org, ObjectiveBase parent, String propName, XmlNode node) {
		this.org=org;
		this.parent=parent;
		this.propName=propName;
		this.isvpName=node.getAttribute("isvp", null);
		this.name=node.getAttribute("name");
	}
	@Override public String toString() { return "isvp.\""+isvpName+"\"."+propName+"."+name; } 
	public LdapObject findEntry() {
		LdapObject result=null;
		if (isvpName==null || isvpName.length()==0)
			result = ((ChildList<?>) org.getProp(propName)).getByName(name);
		else {
			CordysSystem system=org.getSystem();
			Isvp isvp=system.isvp.getByName(isvpName);
			if (isvp==null) {
				env.error(parent+" should refer to UNKNOWN isvp \""+isvpName+"\" in entry "+this);
				return null;
			}
			if (isvp!=null)
				result=((ChildList<?>) isvp.getProp(propName)).getByName(name);
		}
		if (result==null)
			env.error(parent+" should refer to UNKNOWN entry "+this);
		return result;
	}
	
	private int status=OK;
	private String message=null;
	public String getMessage() { return message; }
	public List<Objective> getChildren() { return null;	}

	public int getStatus() { return status; }
	public CordysSystem getSystem() { return parent.getSystem(); }

	public int check(Ui ui) {
		ui.checking(this);
		LdapObject entry = findEntry();
		if (parent.contains(entry)) {
			ui.info(this,"target "+parent+" has entry "+this);
			message=null;
			status=OK;
		}
		else {
			message="target "+parent+" should have entry "+this;
			ui.error(this, message);
			status = ERROR;
		}
		ui.readyWith(this);
		return status;
	}
	
	public void configure(Ui ui) {
		ui.configuring(this);
		LdapObject entry=findEntry(); 
		if (entry==null)
			env.warn("target "+parent+" should have unknown entry "); // TODO: +entry.getVarName());
		else if (parent.contains(entry))
			env.info("target "+parent+" already has entry "+entry.getVarName());
		else
			parent.add(entry);
		ui.readyWith(this);
	}
	
	public void purge(Ui ui) {
		LdapObject entry=findEntry(); 
		if (entry==null)
			env.warn("target "+this+" should have unknown entry "); // TODO: +entry.getVarName());
		else if (! parent.contains(entry))
			env.warn("target "+this+" does not have entry "+entry.getVarName());
		else
			parent.remove(entry); 
	}
}
