package org.kisst.cordys.caas.support;

import java.util.Map;

import org.kisst.cordys.caas.CordysSystem;


public abstract class CordysObject implements Comparable<CordysObject> {
	public final static String xmlns_monitor= "http://schemas.cordys.com/1.0/monitor";
	public final static String xmlns_ldap   = "http://schemas.cordys.com/1.0/ldap";
	public final static String xmlns_isv    = "http://schemas.cordys.com/1.0/isvpackage";
	public final static String xmlns_xmlstore="http://schemas.cordys.com/1.0/xmlstore";

	abstract public CordysSystem getSystem();
	public String getName() { return null; } 
	public String getKey()  { return null; } 

	public int compareTo(CordysObject other) {
		if (other==null)
			return -1;
		return getKey().compareTo(other.getKey());
	}

	public boolean useCache() { return getSystem().useCache();}

	public Props getProps() { return new Props(this,null);}

	public void log(String msg) { System.out.println(msg); }
	public void myclear() {}
	public void clear() {
		int mykeylen=0;
		if (getKey()!=null)
			mykeylen=getKey().length();
		for (Map.Entry e: getProps().entrySet()) {
			if (e.getValue() instanceof CordysObject) {
				//System.out.println(e.getKey()+" ");
				CordysObject o= (CordysObject) e.getValue();
				String childkey=o.getKey();
				if (childkey!=null && childkey.length()>mykeylen) {
					// Only clear properties with longer keys, to prevent loops
					//System.out.println("clearing "+childkey);
					o.clear();
				}
			}
		}
		myclear();
	}
	
	public void deepdiff(CordysObject other) { diff(other,100); }
	public void diff(CordysObject other) { diff(other,0); }
	public void diff(CordysObject other, int depth) { diff("", other, depth); }
	public void diff(String prefix,CordysObject other, int depth) {
		if (this.getClass()!=other.getClass())
			throw new RuntimeException("Cannot diff two different classes "
					+this.getClass().getName()+" and "+other.getClass().getName());
		mydiff(prefix, other, depth);
	}
	protected void mydiff(String prefix, CordysObject other, int depth) {}

}
