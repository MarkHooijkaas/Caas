package org.kisst.cordys.caas;

import java.util.List;

import org.jdom.Element;
import org.kisst.cordys.caas.util.DynamicProperty;

public class Organization extends CordysContainer {
	public final DynamicProperty<User> user;
	public final DynamicProperty<MethodSet> methodSet;

	protected Organization(CordysObject parent, String dn) {
		super(parent, dn);
		user=new DynamicProperty<User>(getSystem(), User.class, "cn=", "cn=organizational users,"+dn);
		methodSet=new DynamicProperty<MethodSet>(getSystem(), MethodSet.class, "cn=", "cn=method sets,"+dn);
	}

	public List<Role> getRoles() {	
		Element method=new Element("GetRolesForOrganization", nsldap10);
		method.addContent(new Element("dn").setText(dn));
		return createObjects(call(method), Role.class);
	}

	public List<SoapNode> getSoapNodes() {	
		Element method=new Element("GetSoapNodes", nsldap10);
		method.addContent(new Element("dn").setText(dn));
		method.addContent(new Element("namespace").setText("*"));
		return createObjects(call(method), SoapNode.class);
	}
}
