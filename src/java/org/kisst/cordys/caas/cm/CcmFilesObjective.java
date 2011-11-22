package org.kisst.cordys.caas.cm;

import java.io.File;

import org.kisst.cordys.caas.CordysSystem;

public class CcmFilesObjective extends CompositeObjective {

	private final CordysSystem system;

	public CcmFilesObjective(String[] files, CordysSystem system) {
		super("ccm");
		this.system=system;
		for (String path: files) {
			File f=new File(path);
			if (f.isFile())
				entries.add(new CaasPackage(path, system));
			else if (f.isDirectory()) {
				if (files.length==1) // prevents one layer
					addFlatDirectory(f);
				else
					entries.add(new CcmFilesObjective(f, system));
			}
		}
	}

	private CcmFilesObjective(File dir, CordysSystem system) {
		super(dir.getPath());
		this.system=system;
		addFlatDirectory(dir);
	}

	private void addFlatDirectory(File dir) {
		if (!dir.isDirectory())
			throw new RuntimeException("bug");
		for (String p: dir.list()) {
			File f=new File(dir,p);
			if (f.isFile())
				entries.add(new CaasPackage(f.getPath(), system));
		}
	}


	public CordysSystem getSystem() { return system; }
}
