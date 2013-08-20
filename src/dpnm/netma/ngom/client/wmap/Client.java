package dpnm.netma.ngom.client.wmap;

import org.apache.xmlrpc.XmlRpcException;

import dpnm.netma.ngom.frontend.*;
import dpnm.netma.ngom.client.comp.HostDialog;
import dpnm.netma.ngom.client.comp.Util;
import dpnm.netma.ngom.data.*;

import javax.swing.*;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.*;

import java.awt.*;
import java.awt.event.*;
import java.io.IOException;

class Client extends JFrame {
	static final String TITLE = "JRobin-MRTG client";
	static final String SUBTITLE = "http://www.jrobin.org";
	static final String COPYRIGHT = "Copyright \u00A9 2004 Sasa Markovic";
	static final String ICON = "icon.png";

	static Dimension MAIN_TREE_SIZE = new Dimension(320, 400);
	static Dimension INFO_PANE_SIZE = new Dimension(320, 400);
	static String DEFAULT_HOST = "localhost";
	static final int HGAP = 1, VGAP = 1, DIVIDER_SIZE = 3;

	// controls
	JTree mainTree = new JTree();
	JTextPane infoTextPane = new JTextPane();
	// menubar
	JMenuBar menuBar = new JMenuBar();
	// MRTG menu
	JMenu mrtgMenu = new JMenu("MRTG");
	JMenuItem newHostMenuItem = new JMenuItem("Connect to JRobin-MRTG server...", KeyEvent.VK_C);
	JMenuItem reloadMenuItem = new JMenuItem("Reload data from JRobin-MRTG server", KeyEvent.VK_L);
	JMenuItem exitMenuItem = new JMenuItem("Exit", KeyEvent.VK_X);
	// Routers menu
	JMenu routersMenu = new JMenu("Router actions");
	JMenuItem newRouterItem = new JMenuItem("Add router...", KeyEvent.VK_A);
	JMenuItem editRouterItem = new JMenuItem("Edit router data...", KeyEvent.VK_E);
	JMenuItem deleteRouterItem = new JMenuItem("Remove router", KeyEvent.VK_V);
	// Interfaces menu
	JMenu linksMenu = new JMenu("Interface actions");
	JMenuItem newLinkItem = new JMenuItem("Add interface(s)...", KeyEvent.VK_N);
	JMenuItem editLinkItem = new JMenuItem("Edit interface data...", KeyEvent.VK_E);
	JMenuItem deleteLinkItem = new JMenuItem("Remove interface", KeyEvent.VK_V);
	JMenuItem quickGraphItem = new JMenuItem("Quick graph (last 24hr)...", KeyEvent.VK_Q);
	JMenuItem dailyGraphItem = new JMenuItem("Daily graph...", KeyEvent.VK_D);
	JMenuItem weeklyGraphItem = new JMenuItem("Weekly graph...", KeyEvent.VK_W);
	JMenuItem monthlyGraphItem = new JMenuItem("Monthly graph...", KeyEvent.VK_O);
	JMenuItem yearlyGraphItem = new JMenuItem("Yearly graph...", KeyEvent.VK_Y);
	JMenuItem customGraphItem = new JMenuItem("Custom graph...", KeyEvent.VK_C);
	// Help menu
	JMenu helpMenu = new JMenu("Help");
	JMenuItem helpItem = new JMenuItem("Help...", KeyEvent.VK_H);
	JMenuItem aboutItem = new JMenuItem("About...", KeyEvent.VK_A);
	// MRTG popup menu
	JPopupMenu mrtgPopupMenu = new JPopupMenu();
	JMenuItem mrtgPopupReloadMenuItem = new JMenuItem("Reload data from JRobin-MRTG server");
	JMenuItem mrtgPopupAddRouterMenuItem = new JMenuItem("Add router...");
	JMenuItem mrtgPopupExitMenuItem = new JMenuItem("Exit");
	// ROUTER popup menu
	JPopupMenu routerPopupMenu = new JPopupMenu();
	JMenuItem routerPopupEditRouterMenuItem = new JMenuItem("Edit router data...");
	JMenuItem routerPopupAddLinksMenuItem = new JMenuItem("Add interface(s)...");
	JMenuItem routerPopupRemoveRouterMenuItem = new JMenuItem("Remove router");
	// INTERFACES popup menu
	JPopupMenu linksPopupMenu = new JPopupMenu();
	JMenuItem linksPopupEditLinkMenuItem = new JMenuItem("Edit interface data...");
	JMenuItem linksPopupRemoveLinkMenuItem = new JMenuItem("Remove interface");
	JMenuItem linksPopupQuickGraphMenuItem = new JMenuItem("Quick graph (last 24hr)...");
	JMenuItem linksPopupDailyGraphMenuItem = new JMenuItem("Daily graph...");
	JMenuItem linksPopupWeeklyGraphMenuItem = new JMenuItem("Weekly graph...");
	JMenuItem linksPopupMonthlyGraphMenuItem = new JMenuItem("Monthly graph...");
	JMenuItem linksPopupYearlyGraphMenuItem = new JMenuItem("Yearly graph...");
	JMenuItem linksPopupCustomGraphMenuItem = new JMenuItem("Custom graph...");

	private DataManager mrtgData = DataManager.getInstance();

	Client() throws IOException, XmlRpcException {
		super(TITLE);
		constructUI();
		pack();
	}

	private void constructUI() {
		JPanel content = (JPanel) getContentPane();
		content.setLayout(new BorderLayout(HGAP, VGAP));

		// tree pane
		JPanel leftPanel = new JPanel();
		leftPanel.setLayout(new BorderLayout());
		JScrollPane treePane = new JScrollPane(mainTree);
        leftPanel.add(treePane);
		leftPanel.setPreferredSize(MAIN_TREE_SIZE);
		mainTree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
		mainTree.addTreeSelectionListener(new TreeSelectionListener() {
			public void valueChanged(TreeSelectionEvent e) { nodeChangedAction(); }
		});
        DefaultTreeCellRenderer renderer = new TreeRenderer();
		mainTree.setCellRenderer(renderer);

		// info pane
		JPanel rightPanel = new JPanel();
		rightPanel.setLayout(new BorderLayout());
		JScrollPane infoPane = new JScrollPane(infoTextPane);
		rightPanel.add(infoPane);
		infoPane.setPreferredSize(INFO_PANE_SIZE);
		infoTextPane.setEditable(false);
		JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, true,
			leftPanel, rightPanel);
		splitPane.setDividerSize(DIVIDER_SIZE);

		// add split pane to content
		content.add(splitPane, BorderLayout.CENTER);

		// mrtg menu
		newHostMenuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) { selectNewHost(); }
		});
		mrtgMenu.add(newHostMenuItem);
		reloadMenuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) { reloadDataFromHost(); }
		});
		mrtgMenu.add(reloadMenuItem);
		mrtgMenu.addSeparator();
		exitMenuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) { System.exit(0); }
		});
		mrtgMenu.add(exitMenuItem);
		mrtgMenu.setMnemonic(KeyEvent.VK_M);
		menuBar.add(mrtgMenu);

		// routers menu
		newRouterItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) { addRouter(); }
		});
		routersMenu.add(newRouterItem);
		editRouterItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) { editRouter(); }
		});
		routersMenu.add(editRouterItem);
		routersMenu.addSeparator();
		deleteRouterItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) { removeRouter(); }
		});
		routersMenu.add(deleteRouterItem);
		routersMenu.setMnemonic(KeyEvent.VK_R);
		menuBar.add(routersMenu);

		// interfaces menu
		newLinkItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) { addLink(); }
		});
		linksMenu.add(newLinkItem);
		editLinkItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) { editLink(); }
		});
		linksMenu.add(editLinkItem);
		linksMenu.addSeparator();
		deleteLinkItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) { removeLink(); }
		});
		linksMenu.add(deleteLinkItem);
		linksMenu.addSeparator();
		quickGraphItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) { graph(GraphFrame.TYPE_QUICK); }
		});
		linksMenu.add(quickGraphItem);
		dailyGraphItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) { graph(GraphFrame.TYPE_DAILY); }
		});
		linksMenu.add(dailyGraphItem);
		weeklyGraphItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) { graph(GraphFrame.TYPE_WEEKLY); }
		});
		linksMenu.add(weeklyGraphItem);
		monthlyGraphItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) { graph(GraphFrame.TYPE_MONTHLY); }
		});
		linksMenu.add(monthlyGraphItem);
		yearlyGraphItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) { graph(GraphFrame.TYPE_YEARLY); }
		});
		linksMenu.add(yearlyGraphItem);
		linksMenu.addSeparator();
		customGraphItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) { graph(GraphFrame.TYPE_CUSTOM); }
		});
		linksMenu.add(customGraphItem);
		linksMenu.setMnemonic(KeyEvent.VK_I);
		menuBar.add(linksMenu);

		// MRTG popup menu
		mrtgPopupReloadMenuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) { reloadDataFromHost(); }
		});
        mrtgPopupMenu.add(mrtgPopupReloadMenuItem);
		mrtgPopupAddRouterMenuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) { addRouter(); }
		});
        mrtgPopupMenu.add(mrtgPopupAddRouterMenuItem);
		mrtgPopupMenu.addSeparator();
		mrtgPopupExitMenuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) { System.exit(0); }
		});
        mrtgPopupMenu.add(mrtgPopupExitMenuItem);

		// ROUTER popup menu
		routerPopupEditRouterMenuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) { editRouter(); }
		});
        routerPopupMenu.add(routerPopupEditRouterMenuItem);
		routerPopupAddLinksMenuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) { addLink(); }
		});
        routerPopupMenu.add(routerPopupAddLinksMenuItem);
		routerPopupMenu.addSeparator();
		routerPopupRemoveRouterMenuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) { removeRouter(); }
		});
        routerPopupMenu.add(routerPopupRemoveRouterMenuItem);

		// LINKS popup menu
		linksPopupEditLinkMenuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) { editLink(); }
		});
        linksPopupMenu.add(linksPopupEditLinkMenuItem);
        linksPopupMenu.addSeparator();
		linksPopupQuickGraphMenuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) { graph(GraphFrame.TYPE_QUICK); }
		});
        linksPopupMenu.add(linksPopupQuickGraphMenuItem);
		linksPopupDailyGraphMenuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) { graph(GraphFrame.TYPE_DAILY); }
		});
        linksPopupMenu.add(linksPopupDailyGraphMenuItem);
		linksPopupWeeklyGraphMenuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) { graph(GraphFrame.TYPE_WEEKLY); }
		});
        linksPopupMenu.add(linksPopupWeeklyGraphMenuItem);
		linksPopupMonthlyGraphMenuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) { graph(GraphFrame.TYPE_MONTHLY); }
		});
        linksPopupMenu.add(linksPopupMonthlyGraphMenuItem);
		linksPopupYearlyGraphMenuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) { graph(GraphFrame.TYPE_YEARLY); }
		});
        linksPopupMenu.add(linksPopupYearlyGraphMenuItem);
		linksPopupMenu.addSeparator();
		linksPopupCustomGraphMenuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) { graph(GraphFrame.TYPE_CUSTOM); }
		});
		linksPopupMenu.add(linksPopupCustomGraphMenuItem);
		linksPopupMenu.addSeparator();
		linksPopupRemoveLinkMenuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) { removeLink(); }
		});
        linksPopupMenu.add(linksPopupRemoveLinkMenuItem);

		MouseAdapter adapter = new MouseAdapter() {
			public void mousePressed(MouseEvent e) { showPopup(e);	}
			public void mouseReleased(MouseEvent e) { showPopup(e); }
			private void showPopup(MouseEvent e) {
				if (e.isPopupTrigger()) {
					int selRow = mainTree.getRowForLocation(e.getX(), e.getY());
                    TreePath treePath = mainTree.getPathForLocation(e.getX(), e.getY());
					if(selRow != -1) {
						mainTree.setSelectionPath(treePath);
						DefaultMutableTreeNode inf =
							(DefaultMutableTreeNode) treePath.getLastPathComponent();
                        Object obj = inf.getUserObject();
						if(obj instanceof BackendInfo) {
							mrtgPopupMenu.show(e.getComponent(), e.getX(), e.getY());
						}
						else if(obj instanceof DeviceInfo) {
							routerPopupMenu.show(e.getComponent(), e.getX(), e.getY());
						}
						else if(obj instanceof InterfaceInfo) {
							linksPopupMenu.show(e.getComponent(), e.getX(), e.getY());
						}
					}
				}
			}
		};
		mainTree.addMouseListener(adapter);

		// finalize UI
		setIconImage(WeatherMapClient.rf.getImage(ICON));
		setJMenuBar(menuBar);
		clearUI();
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) { System.exit(1); }
			public void windowOpened(WindowEvent e) { selectNewHost(); }
		});
		Util.centerOnScreen(this);
		mainTree.requestFocus();
	}

	private void clearUI() {
		mainTree.setModel(new DefaultTreeModel(new DefaultMutableTreeNode("Not connected")));
		infoTextPane.setText("No info available");
	}

	private void selectNewHost() {
		try {
			HostDialog hostDialog = new HostDialog(this, mrtgData.getHost());
			String newHost = hostDialog.getHost();
			if(newHost != null) {
				mrtgData.connect(newHost);
				reloadData();
			}
		} catch (Exception e) {
			clearUI();
			handleException("Host is not responding", e);
		}
	}

	private void nodeChangedAction() {
		DefaultMutableTreeNode node = (DefaultMutableTreeNode)
			mainTree.getLastSelectedPathComponent();
		if (node != null) {
			Object nodeObj = node.getUserObject();
			if(nodeObj instanceof TreeElementInfo) {
				TreeElementInfo treeElement = (TreeElementInfo) nodeObj;
				infoTextPane.setText(treeElement.getInfo());
			}
		}
	}

	private void reloadDataFromHost() {
		try {
			mrtgData.reload();
			reloadData();
		} catch (Exception e) {
			clearUI();
			handleException("Could not reload data from host", e);
		}
	}

	private void reloadData() {
		try {
			TreeModel treeModel = getTreeModel();
			mainTree.setModel(treeModel);
			mainTree.setSelectionRow(0);
			for(int i = mainTree.getRowCount() - 1; i > 0; i--) {
				mainTree.expandRow(i);
			}
		} catch (Exception e) {
			clearUI();
			handleException("Could not reload data from host", e);
		}
	}

	private boolean isConnected() {
		DefaultMutableTreeNode node = (DefaultMutableTreeNode) mainTree.getModel().getRoot();
		return node.getUserObject() instanceof BackendInfo;
	}

	private void addRouter() {
		if(!isConnected()) {
			Util.error(this, "Connect to MRTG server first");
			return;
		}
        EditDeviceDialog newDeviceDiaglog = new EditDeviceDialog(this);
		DeviceInfo deviceInfo = newDeviceDiaglog.getDeviceInfo();
		if(deviceInfo != null) {
			try {
				if(mrtgData.addDevice(deviceInfo) == 0) {
					Util.info(this, "Router " + deviceInfo.getHost() + " added succesfully");
					reloadData();
				}
				else {
					Util.error(this, "Router " + deviceInfo.getHost() + " not added");
				}
			}
			catch(Exception e) {
				handleException("Could not add router", e);
			}
		}
	}

	private void editRouter() {
		DeviceInfo deviceInfo = findSelectedRouter();
		if(deviceInfo == null) {
			Util.warn(this, "Please, select router first");
			return;
		}
		EditDeviceDialog newDeviceDiaglog = new EditDeviceDialog(this, deviceInfo);
		deviceInfo = newDeviceDiaglog.getDeviceInfo();
		if(deviceInfo != null) {
			try {
				if(mrtgData.updateDevice(deviceInfo) == 0) {
					Util.info(this, "Router " + deviceInfo.getHost() + " updated succesfully");
					reloadData();
				}
				else {
					Util.error(this, "Router " + deviceInfo.getHost() + " not updated");
				}
			}
			catch(Exception e) {
				handleException("Could not edit router data", e);
			}
		}
	}

	private void removeRouter() {
		DeviceInfo deviceInfo = findSelectedRouter();
		if(deviceInfo == null) {
			Util.warn(this, "Please, select router first");
			return;
		}
		int linkCount = deviceInfo.getInterfaceInfo().length;
		if(linkCount > 0) {
			Util.error(this, "To remove router " + deviceInfo.getHost() +
				" please delete associated interfaces first (" +
				linkCount + " found)");
			return;
		}
		try {
			if(mrtgData.removeDevice(deviceInfo) == 0) {
				Util.info(this, "Router " + deviceInfo.getHost() + " deleted succesfully");
				reloadData();
			}
			else {
				Util.error(this, "Router " + deviceInfo.getHost() + " not deleted");
			}
		} catch (Exception e) {
			handleException("Could not delete router", e);
		}
	}

	private void addLink() {
		DeviceInfo deviceInfo = findSelectedRouter();
		if(deviceInfo == null) {
			Util.warn(this, "Please, select router first");
			return;
		}
		try {
			String[] links = mrtgData.getAvailableLinks(deviceInfo);
			if(links == null || links.length == 0) {
				// no links available
				Util.error(this, "No interfaces are available on this router");
				return;
			}
			EditInterfaceDiaglog newLinkDialog = new EditInterfaceDiaglog(this, deviceInfo, links);
			InterfaceInfo[] linkInfo = newLinkDialog.getInterfaceInfo();
			if(linkInfo != null) {
				int ok = 0, bad = 0;
				String failedInterfaces = "";
				for(int i = 0; i < linkInfo.length; i++) {
					if(mrtgData.addInterface(deviceInfo, linkInfo[i]) == 0) {
						ok++;
					}
					else {
						bad++;
						failedInterfaces += linkInfo[i].getIfDescr() + "@" +
							deviceInfo.getHost() + "\n";
					}
				}
				String message = ok + " interface(s) added successfully\n";
				if(bad != 0) {
					message += bad + " interface(s) not added:\n" + failedInterfaces;
					Util.error(this, message);
				}
				else {
					Util.info(this, message);
				}
				reloadData();
			}
		} catch (Exception e) {
			handleException("Could not add new link", e);
		}
	}

	private void editLink() {
		DeviceInfo deviceInfo = findSelectedRouter();
		InterfaceInfo linkInfo = findSelectedLink();
		if(deviceInfo == null || linkInfo == null) {
			Util.warn(this, "Please, select interface first");
			return;
		}
		try {
			EditInterfaceDiaglog newLinkDialog = new EditInterfaceDiaglog(this, deviceInfo, linkInfo);
			InterfaceInfo[] linkInfoUpdated = newLinkDialog.getInterfaceInfo();
			if(linkInfoUpdated != null) {
				if(mrtgData.updateInterface(deviceInfo, linkInfoUpdated[0]) == 0) {
                    Util.info(this, "Interface " + linkInfoUpdated[0].getIfDescr() + "@" +
						deviceInfo.getHost() + " updated successfully");
					reloadData();
				}
				else {
					Util.error(this, "Interface " + linkInfoUpdated[0].getIfDescr() + "@" +
						deviceInfo.getHost() + " NOT updated");
				}
			}
		} catch (Exception e) {
			handleException("Could not edit link data", e);
		}
	}

	private void removeLink() {
		DeviceInfo deviceInfo = findSelectedRouter();
		InterfaceInfo linkInfo = findSelectedLink();
		if(deviceInfo == null || linkInfo == null) {
			Util.warn(this, "Please, select interface first");
			return;
		}
		try {
			if(mrtgData.removeInterface(deviceInfo, linkInfo) == 0) {
            	Util.info(this, "Interface " + linkInfo.getIfDescr() + "@" +
					deviceInfo.getHost() + " removed successfully");
				reloadData();
			}
			else {
				Util.error(this, "Interface " + linkInfo.getIfDescr() + "@" +
					deviceInfo.getHost() + " NOT removed");
			}

		} catch (Exception e) {
			handleException("Could not remove link", e);
		}
	}

	private void graph(int type) {
		DeviceInfo deviceInfo = findSelectedRouter();
		InterfaceInfo linkInfo = findSelectedLink();
		if(deviceInfo == null || linkInfo == null) {
			Util.warn(this, "Please, select interface first");
			return;
		}
		new GraphFrame(this, deviceInfo, linkInfo, type);
	}

	private DeviceInfo findSelectedRouter() {
		TreePath path = mainTree.getSelectionPath();
		if(path == null || path.getPathCount() < 2) {
			return null;
		}
		else {
			DefaultMutableTreeNode node = (DefaultMutableTreeNode) path.getPathComponent(1);
			return (DeviceInfo) node.getUserObject();
		}
	}

	private InterfaceInfo findSelectedLink() {
		TreePath path = mainTree.getSelectionPath();
		if(path == null || path.getPathCount() < 3) {
			return null;
		}
		else {
			DefaultMutableTreeNode node = (DefaultMutableTreeNode) path.getLastPathComponent();
			return (InterfaceInfo) node.getUserObject();
		}
	}

	private void handleException(String msg, Exception e) {
		Util.error(this, msg + ":\n" + e);
	}

    public TreeModel getTreeModel() {
		BackendInfo backendInfo = mrtgData.getBackendInfo();
		DefaultMutableTreeNode root = new DefaultMutableTreeNode(backendInfo);
		DeviceInfo[] deviceInfo = mrtgData.getDevices();
		for(int deviceIndex = 0; deviceIndex < deviceInfo.length; deviceIndex++) {
			DefaultMutableTreeNode routerNode =
				new DefaultMutableTreeNode(deviceInfo[deviceIndex]);
			root.add(routerNode);
			InterfaceInfo[] interfaceInfo = deviceInfo[deviceIndex].getInterfaceInfo();
			for(int linkIndex = 0; linkIndex < interfaceInfo.length; linkIndex++) {
				DefaultMutableTreeNode linkNode =
					new DefaultMutableTreeNode(interfaceInfo[linkIndex]);
				routerNode.add(linkNode);
			}
		}
		return new DefaultTreeModel(root);
	}
    
	public static void main(String[] args) throws IOException, XmlRpcException, InterruptedException {
		Client client = new Client();
		client.setVisible(true);
	}

}
