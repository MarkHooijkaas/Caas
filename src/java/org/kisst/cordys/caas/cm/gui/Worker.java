package org.kisst.cordys.caas.cm.gui;

import java.awt.Cursor;

import org.kisst.cordys.caas.cm.Objective;

public class Worker implements Runnable {
	private final String action;
	private final Objective target;
	private final CcmGui gui;

	public Worker(CcmGui gui, String action, Objective target) {
		this.action=action;
		this.target=target;
		this.gui=gui;
	}
	
	public void run() {
		gui.tree.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
		target.getSystem().clear();
		if (action.equals("check")) {
			target.check(gui);
		}
		if (action.equals("configure")) {
			target.configure(gui);
			target.check(gui);
		}
		gui.tree.repaint();
		gui.tree.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
	}

}
