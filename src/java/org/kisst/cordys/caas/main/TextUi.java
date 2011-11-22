package org.kisst.cordys.caas.main;

import org.kisst.cordys.caas.cm.Objective;

public class TextUi implements Objective.Ui{

	public void error(Objective obj, String msg) {
		System.out.println("TEXTUI ERROR:"+msg);
	}

	public void info(Objective obj, String msg) {
		System.out.println("TEXTUI INFO :"+msg);
	}

	public void warn(Objective obj, String msg) {
		System.out.println("TEXTUI WARN :"+msg);
	}

	public void checking(Objective obj) {
		System.out.println("TEXTUI checking :"+obj);
	}

	public void configuring(Objective obj) {
		System.out.println("TEXTUI configuring:"+obj);
	}

	public void readyWith(Objective obj) {
	}

}
