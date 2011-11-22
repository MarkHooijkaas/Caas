package org.kisst.cordys.caas.cm.gui;

import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.HashMap;
import java.util.List;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTree;
import javax.swing.ToolTipManager;
import javax.swing.UIManager;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import org.kisst.cordys.caas.cm.Objective;

public class CcmGui extends JPanel implements TreeSelectionListener, ActionListener, Objective.Ui {
	private static final long serialVersionUID = 1L;
	private JEditorPane htmlPane;
	JTree tree;
	private JPopupMenu popup;
	private final HashMap<Objective, DefaultMutableTreeNode> nodeMap=new HashMap<Objective, DefaultMutableTreeNode> ();
	private final HashMap<Objective, Boolean> objectiveProps=new HashMap<Objective, Boolean> ();
	
	public CcmGui(Objective target) {
		super(new GridLayout(1,0));
		
		//Create the nodes.
		DefaultMutableTreeNode root = new DefaultMutableTreeNode(target);
		nodeMap.put(target,root);
		createNodes(root, target);

		tree = new JTree(root);
		tree.getSelectionModel().setSelectionMode
		(TreeSelectionModel.SINGLE_TREE_SELECTION);

		tree.setCellRenderer(new IconRenderer());
		
		tree.addTreeSelectionListener(this);
		ToolTipManager.sharedInstance().registerComponent(tree);
		createPopup();

		JScrollPane treeView = new JScrollPane(tree);

		htmlPane = new JEditorPane();
		htmlPane.setEditable(false);
		JScrollPane htmlView = new JScrollPane(htmlPane);

		JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
		splitPane.setTopComponent(treeView);
		splitPane.setBottomComponent(htmlView);

		Dimension minimumSize = new Dimension(100, 20);
		htmlView.setMinimumSize(minimumSize);
		treeView.setMinimumSize(minimumSize);
		splitPane.setDividerLocation(700); 
		splitPane.setPreferredSize(new Dimension(1000, 800));

		add(splitPane);
	}

	private void createPopup() {
		popup = new JPopupMenu();
		JMenuItem mi = new JMenuItem("check");
		mi.addActionListener(this);
		mi.setActionCommand("check");
		popup.add(mi);
		mi = new JMenuItem("configure");
		mi.addActionListener(this);
		mi.setActionCommand("configure");
		popup.add(mi);
		popup.setOpaque(true);
		popup.setLightWeightPopupEnabled(true);

		tree.addMouseListener(
				new MouseAdapter() {
					@Override public void mouseReleased( MouseEvent e ) {
						if ( e.isPopupTrigger()) {
							popup.show( (JComponent)e.getSource(), e.getX(), e.getY() );
						}
					}
					@Override public void mousePressed( MouseEvent e ) {
						if ( e.isPopupTrigger()) {
							popup.show( (JComponent)e.getSource(), e.getX(), e.getY() );
						}
					}
				}
		);

	}
	public void actionPerformed(ActionEvent ae) {
		TreePath path = tree.getSelectionPath();
		if (path==null)
			return;
		DefaultMutableTreeNode node = (DefaultMutableTreeNode) path.getLastPathComponent();
		if (node==null)
			return;
		Object o= node.getUserObject();
		if (!( o instanceof Objective)) {
			return;
		}
		Objective info =  (Objective) node.getUserObject();
		String action = ae.getActionCommand(); 
		if (action.equals("check")) {
			tree.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
			new Thread(new Worker(this, action, info)).start();
			tree.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
		}
		if (action.equals("configure")) {
			tree.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
			new Thread(new Worker(this, action, info)).start();
			//((DefaultTreeModel )tree.getModel()).nodeStructureChanged(node);
			tree.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
		}

	}



	public void valueChanged(TreeSelectionEvent e) {
		DefaultMutableTreeNode node = (DefaultMutableTreeNode)
		tree.getLastSelectedPathComponent();

		if (node == null) return;

		Object nodeInfo = node.getUserObject();
		if (nodeInfo instanceof Objective) {
			Objective info = (Objective)nodeInfo;
			showMessage(info.getMessage());
		} else {
			showMessage(null); 
		}
	}

	private void showMessage(String message) {
		if (message==null)
			message="";
		htmlPane.setText(message);
	}

	private void createNodes(DefaultMutableTreeNode top, Objective objective) {
		List<Objective> list = objective.getChildren();
		if (list!=null) {
			for (Objective child:list) {
				DefaultMutableTreeNode node = new DefaultMutableTreeNode(child);
				nodeMap.put(child, node);
				top.add(node);
				createNodes(node, child);
			}
		}
	}

	private  void showFrame() {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) { /* ignore, use default look and feel */ }

		JFrame frame = new JFrame("CcmGui");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.add(this);
		frame.pack();
		frame.setVisible(true);
	}

	public  void run() {
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			public void run() {	showFrame(); }
		});
	}

	private class IconRenderer extends DefaultTreeCellRenderer {
		private static final long serialVersionUID = 1L;
		private final Icon warnIcon;
		private final Icon errorIcon;
		private final Icon activeIcon;

		public IconRenderer() {
			warnIcon = loadIcon("images/warning.gif");
			errorIcon = loadIcon("images/error.gif");
			activeIcon = loadIcon("images/middle.gif");
		}

		public Component getTreeCellRendererComponent(
				JTree tree,
				Object value,
				boolean sel,
				boolean expanded,
				boolean leaf,
				int row,
				boolean hasFocus) {

			super.getTreeCellRendererComponent(
					tree, value, sel,
					expanded, leaf, row,
					hasFocus);
			if (value instanceof DefaultMutableTreeNode) {
				DefaultMutableTreeNode node=(DefaultMutableTreeNode) value;
				if (node.getUserObject() instanceof Objective) {
					Objective target= (Objective) node.getUserObject() ;
					if (target.getStatus()==1)
						setIcon(warnIcon);
					if (target.getStatus()==2)
						setIcon(errorIcon);
					setToolTipText(target.getMessage());
					Boolean highlight = objectiveProps.get(target);
					if (highlight!=null && highlight) {
						setIcon(activeIcon);
					}
				}
			} else {
				setToolTipText(null); //no tool tip
			}

			return this;
		}
		private ImageIcon loadIcon(String path) {
			java.net.URL imgURL = CcmGui.class.getResource(path);
			if (imgURL != null)
				return new ImageIcon(imgURL);
			else
				throw new RuntimeException("Couldn't find icon file: " + path);
		}

	}
	

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
		highlight(obj);
	}

	public void configuring(Objective obj) {
		System.out.println("TEXTUI configuring:"+obj);
		highlight(obj);
	}

	public void readyWith(Objective obj) {
		lowlight(obj);
	}

	private void highlight(Objective obj) {
		objectiveProps.put(obj, true);
		tree.repaint();
	}
	private void lowlight(Objective obj) {
		objectiveProps.put(obj, false);
		tree.repaint();
	}


}
