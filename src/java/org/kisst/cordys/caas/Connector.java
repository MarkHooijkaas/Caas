package org.kisst.cordys.caas;

public class Connector extends CordysXmlObject {

	protected Connector(CordysObject parent, String key) { super(parent, key); }

	public String getName() { return getData().getChildText("step/description"); }
	public String getImplementation() { return getData().getChildText("step/implementation"); }
}
