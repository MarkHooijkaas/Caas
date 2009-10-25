package org.kisst.cordys.caas;

import org.kisst.cordys.caas.support.CordysObject;
import org.kisst.cordys.caas.support.CordysXmlObject;

public class Connector extends CordysXmlObject {

	public Connector(CordysObject parent, String key) { super(parent, key); }

	@Override public String getName() { return getData().getChildText("step/description"); }
	public String getImplementation() { return getData().getChildText("step/implementation"); }
	@Override protected String prefix() { return "conn"; }
}
