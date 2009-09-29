package org.kisst.cordys.caas;

public class CordysObject {
	protected final String dn;
	protected CordysObject(String dn) {
		this.dn=dn;
	}
	public String toString() { return dn; }
	public String getShortName() {
		int pos=dn.indexOf("=");
		int pos2=dn.indexOf(",",pos);
		return dn.substring(pos+1,pos2);
	}
}
