/*
 * @(#)ListEditor.java
 * 
 * Created on 2006. 07. 15
 *
 *	This software is the confidential and proprietary informatioon of
 *	POSTECH DP&NM. ("Confidential Information"). You shall not
 *	disclose such Confidential Information and shall use it only in
 *	accordance with the terms of the license agreement you entered into
 *	with Eric Kang.
 *
 *	Contact: Eric Kang at eliot@postech.edu
 */
package dpnm.netma.ngom.client.wmap;

import org.apache.xmlrpc.XmlRpcException;

import javax.swing.*;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.*;

import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.util.Enumeration;

import dpnm.netma.ngom.Conf;
import dpnm.netma.ngom.util.Logger;

import dpnm.netma.ngom.client.comp.Util;
import dpnm.netma.ngom.data.*;

import dpnm.netma.ngom.frontend.DataManager;

/**
 * List Editor class
 * This class is for add/remove/modify Devices and Interfaces.
 *
 * @author Eric Kang
 */
public class ListEditor extends JInternalFrame {
	/**
	* The offset pixels of the internal windows.
	*/
	private static final int _windowOffset = 30 ;

    DataManager dataManager = null;

	static Dimension MAIN_TREE_SIZE = new Dimension(320, 500);
	static Dimension INFO_PANE_SIZE = new Dimension(320, 500);
	static String DEFAULT_HOST = "localhost";
	static final int HGAP = 1, VGAP = 1, DIVIDER_SIZE = 3;

	// controls
	JTree mainTree = new JTree();
	JTextPane infoTextPane = new JTextPane();
	// menubar
	JMenuBar menuBar = new JMenuBar();
	// MRTG menu
	JMenu mrtgMenu = new JMenu("LIST");
	JMenuItem reloadMenuItem = new JMenuItem("Reload data from NWS server", KeyEvent.VK_L);
	JMenuItem exitMenuItem = new JMenuItem("Exit", KeyEvent.VK_X);
	// Devices menu
	JMenu devicesMenu = new JMenu("Device actions");
	JMenuItem newDeviceItem = new JMenuItem("Add device...", KeyEvent.VK_A);
	JMenuItem editDeviceItem = new JMenuItem("Edit device data...", KeyEvent.VK_E);
	JMenuItem deleteDeviceItem = new JMenuItem("Remove device", KeyEvent.VK_V);
	JMenuItem dquickGraphItem = new JMenuItem("Quick graph (last 24hr)...", KeyEvent.VK_Q);
	JMenuItem ddailyGraphItem = new JMenuItem("Daily graph...", KeyEvent.VK_D);
	JMenuItem dweeklyGraphItem = new JMenuItem("Weekly graph...", KeyEvent.VK_W);
	JMenuItem dmonthlyGraphItem = new JMenuItem("Monthly graph...", KeyEvent.VK_O);
	JMenuItem dyearlyGraphItem = new JMenuItem("Yearly graph...", KeyEvent.VK_Y);
	JMenuItem dcustomGraphItem = new JMenuItem("Custom graph...", KeyEvent.VK_C);
	
	// Interfaces menu
	JMenu linksMenu = new JMenu("Interface actions");
	JMenuItem newLinkItem = new JMenuItem("Add interface(s)...", KeyEvent.VK_N);
	JMenuItem editLinkItem = new JMenuItem("Edit interface data...", KeyEvent.VK_E);
	JMenuItem editSamplingItem = new JMenuItem("Edit Sampling Interval...", KeyEvent.VK_S);
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
	JMenuItem mrtgPopupAddDeviceMenuItem = new JMenuItem("Add device...");
	JMenuItem mrtgPopupExitMenuItem = new JMenuItem("Exit");	
	
	// device popup menu
	JPopupMenu devicePopupMenu = new JPopupMenu();
	JMenuItem devicePopupEditDeviceMenuItem = new JMenuItem("Edit device data...");
	JMenuItem devicePopupAddLinksMenuItem = new JMenuItem("Add interface(s)...");
	JMenuItem devicePopupRemoveDeviceMenuItem = new JMenuItem("Remove device");
	JMenuItem devicePopupQuickGraphMenuItem = new JMenuItem("Quick graph (last 24hr)...");
	JMenuItem devicePopupDailyGraphMenuItem = new JMenuItem("Daily graph...");
	JMenuItem devicePopupWeeklyGraphMenuItem = new JMenuItem("Weekly graph...");
	JMenuItem devicePopupMonthlyGraphMenuItem = new JMenuItem("Monthly graph...");
	JMenuItem devicePopupYearlyGraphMenuItem = new JMenuItem("Yearly graph...");
	JMenuItem devicePopupCustomGraphMenuItem = new JMenuItem("Custom graph...");
	
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

    public ListEditor() {
        this("List Editor", true, true, true, true, 640, 480);
    }

    public ListEditor(String title, int width, int height) {
        this(title, true, true, true, true, width, height);
    }
 
    /**
     * Constructor
     */
    public ListEditor(String title, boolean resizable, boolean closable, boolean maximizable, boolean iconifiable, 
    		int width, int height) {
        super(title, resizable, closable, maximizable, iconifiable);
        
        setSize(width, height);

        dataManager = DataManager.getInstance();

        setLocation(WeatherMapClient.getNumWindow() * _windowOffset, WeatherMapClient.getNumWindow() * _windowOffset) ;
        WeatherMapClient.increaseNumWindow();
        
		createUI();
//        pack();
    }

    private void createUI() {
		JPanel content = (JPanel) getContentPane();
		content.setLayout(new BorderLayout(HGAP, VGAP));

		// tree pane
		JPanel leftPanel = new JPanel();
		leftPanel.setLayout(new BorderLayout());
		JScrollPane treePane = new JScrollPane(mainTree);
        leftPanel.add(treePane);
//		leftPanel.setPreferredSize(MAIN_TREE_SIZE);
		leftPanel.setSize(MAIN_TREE_SIZE);
		mainTree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
		mainTree.addTreeSelectionListener(new TreeSelectionListener() {
			public void valueChanged(TreeSelectionEvent e) { nodeChangedAction(); }
		});
        DefaultTreeCellRenderer renderer = new TreeRenderer();
		mainTree.setCellRenderer(renderer);

		// info pane
		JPanel rightPanel = new JPanel();
		rightPanel.setPreferredSize(INFO_PANE_SIZE);
		rightPanel.setLayout(new BorderLayout());
		JScrollPane infoPane = new JScrollPane(infoTextPane);
		rightPanel.add(infoPane);
		infoPane.setPreferredSize(INFO_PANE_SIZE);
		infoTextPane.setEditable(false);
		JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, true,
			leftPanel, rightPanel);
		splitPane.setDividerSize(DIVIDER_SIZE);
		splitPane.setDividerLocation(320);

		// add split pane to content
		content.add(splitPane, BorderLayout.CENTER);

		// mrtg menu
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

		// devices menu
		newDeviceItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) { addDevice(); }
		});
		devicesMenu.add(newDeviceItem);
		editDeviceItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) { editDevice(); }
		});
		devicesMenu.add(editDeviceItem);
		devicesMenu.addSeparator();
		deleteDeviceItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) { removeDevice(); }
		});
		devicesMenu.add(deleteDeviceItem);
		devicesMenu.addSeparator();
		dquickGraphItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) { graphDevice(GraphFrame.TYPE_QUICK); }
		});
		devicesMenu.add(dquickGraphItem);
		ddailyGraphItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) { graphDevice(GraphFrame.TYPE_DAILY); }
		});
		devicesMenu.add(ddailyGraphItem);
		dweeklyGraphItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) { graphDevice(GraphFrame.TYPE_WEEKLY); }
		});
		devicesMenu.add(dweeklyGraphItem);
		dmonthlyGraphItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) { graphDevice(GraphFrame.TYPE_MONTHLY); }
		});
		devicesMenu.add(dmonthlyGraphItem);
		dyearlyGraphItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) { graphDevice(GraphFrame.TYPE_YEARLY); }
		});
		devicesMenu.add(dyearlyGraphItem);
		devicesMenu.addSeparator();
		dcustomGraphItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) { graphDevice(GraphFrame.TYPE_CUSTOM); }
		});
		devicesMenu.add(dcustomGraphItem);
		devicesMenu.setMnemonic(KeyEvent.VK_R);
		menuBar.add(devicesMenu);

		// interfaces menu
		newLinkItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) { addLink(); }
		});
		linksMenu.add(newLinkItem);
		editLinkItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) { editLink(); }
		});
		linksMenu.add(editLinkItem);
		editSamplingItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) { editSampling(); }
		});
		linksMenu.add(editSamplingItem);
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
		mrtgPopupAddDeviceMenuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) { addDevice(); }
		});
        mrtgPopupMenu.add(mrtgPopupAddDeviceMenuItem);
		mrtgPopupMenu.addSeparator();
		mrtgPopupExitMenuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) { System.exit(0); }
		});
        mrtgPopupMenu.add(mrtgPopupExitMenuItem);

		// device popup menu
		devicePopupEditDeviceMenuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) { editDevice(); }
		});
        devicePopupMenu.add(devicePopupEditDeviceMenuItem);
		devicePopupAddLinksMenuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) { addLink(); }
		});
        devicePopupMenu.add(devicePopupAddLinksMenuItem);
		devicePopupMenu.addSeparator();
		devicePopupRemoveDeviceMenuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) { removeDevice(); }
		});
        devicePopupMenu.add(devicePopupRemoveDeviceMenuItem);
				// DEVICES popup menu
        devicePopupMenu.addSeparator();
        
		devicePopupQuickGraphMenuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) { graphDevice(GraphFrame.TYPE_QUICK); }
		});
        devicePopupMenu.add(devicePopupQuickGraphMenuItem);
		devicePopupDailyGraphMenuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) { graphDevice(GraphFrame.TYPE_DAILY); }
		});
        devicePopupMenu.add(devicePopupDailyGraphMenuItem);
		devicePopupWeeklyGraphMenuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) { graphDevice(GraphFrame.TYPE_WEEKLY); }
		});
        devicePopupMenu.add(devicePopupWeeklyGraphMenuItem);
		devicePopupMonthlyGraphMenuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) { graphDevice(GraphFrame.TYPE_MONTHLY); }
		});
        devicePopupMenu.add(devicePopupMonthlyGraphMenuItem);
		devicePopupYearlyGraphMenuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) { graphDevice(GraphFrame.TYPE_YEARLY); }
		});
        devicePopupMenu.add(devicePopupYearlyGraphMenuItem);
		devicePopupMenu.addSeparator();
		devicePopupCustomGraphMenuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) { graphDevice(GraphFrame.TYPE_CUSTOM); }
		});
		devicePopupMenu.add(devicePopupCustomGraphMenuItem);
	

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
							devicePopupMenu.show(e.getComponent(), e.getX(), e.getY());
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
		setJMenuBar(menuBar);
		clearUI();
		mainTree.requestFocus();
    }

	private void clearUI() {
		mainTree.setModel(new DefaultTreeModel(new DefaultMutableTreeNode("Not connected")));
		infoTextPane.setText("No info available");
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

	void reloadDataFromHost() {
		try {
			dataManager.reload();
			reloadData();
		} catch (Exception e) {
			clearUI();
			handleException("Could not reload data from host", e);
		}
	}

	private void reloadData() {
		try {
//			boolean row[] = new boolean[mainTree.getRowCount()];
//			for(int i = mainTree.getRowCount() - 1; i > 0; i--) {
//				row[i] = mainTree.isExpanded(i); 
//			}
			
					
			TreeModel treeModel = getTreeModel();
			mainTree.setModel(treeModel);
			mainTree.setSelectionRow(0);
			for(int i = mainTree.getRowCount() - 1; i > 0; i--) {
//				if (row.length != 1 && row[i]) {
					mainTree.expandRow(i);
//				}
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

	private void addDevice() {
		if(!isConnected()) {
			Util.error(this, "Connect to MRTG server first");
			return;
		}
        EditDeviceDialog newDeviceDialog = new EditDeviceDialog(null);
		DeviceInfo deviceInfo = newDeviceDialog.getDeviceInfo();
		if(deviceInfo != null) {
			try {
				if(dataManager.addDevice(deviceInfo) == 0) {
					Util.info(this, "Device " + deviceInfo.getHost() + " added succesfully");
					reloadData();
				}
				else {
					Util.error(this, "Device " + deviceInfo.getHost() + " not added");
				}
			}
			catch(Exception e) {
				handleException("Could not add device", e);
			}
		}
	}

	private void editDevice() {
		DeviceInfo deviceInfo = findSelectedDevice();
		if(deviceInfo == null) {
			Util.warn(this, "Please, select device first");
			return;
		}
		EditDeviceDialog newDeviceDialog = new EditDeviceDialog(null, deviceInfo);
		deviceInfo = newDeviceDialog.getDeviceInfo();
		if(deviceInfo != null) {
			try {
				if(dataManager.updateDevice(deviceInfo) == 0) {
					Util.info(this, "Device " + deviceInfo.getHost() + " updated succesfully");
					reloadData();
				}
				else {
					Util.error(this, "Device " + deviceInfo.getHost() + " not updated");
				}
			}
			catch(Exception e) {
				handleException("Could not edit device data", e);
			}
		}
	}

	private void removeDevice() {
		DeviceInfo deviceInfo = findSelectedDevice();
		if(deviceInfo == null) {
			Util.warn(this, "Please, select device first");
			return;
		}
		int linkCount = deviceInfo.getInterfaceInfo().length;
		if(linkCount > 0) {
			Util.error(this, "To remove device " + deviceInfo.getHost() +
				" please delete associated interfaces first (" +
				linkCount + " found)");
			return;
		}
		try {
			if(dataManager.removeDevice(deviceInfo) == 0) {
				Util.info(this, "Device " + deviceInfo.getHost() + " deleted succesfully");
				reloadData();
			}
			else {
				Util.error(this, "Device " + deviceInfo.getHost() + " not deleted");
			}
		} catch (Exception e) {
			handleException("Could not delete device", e);
		}
	}

	private void addLink() {
		DeviceInfo deviceInfo = findSelectedDevice();
		if(deviceInfo == null) {
			Util.warn(this, "Please, select device first");
			return;
		}
		try {
			String[] links = dataManager.getAvailableLinks(deviceInfo);
			if(links == null || links.length == 0) {
				// no links available
				Util.error(this, "No interfaces are available on this device");
				return;
			}
            String alias[] = dataManager.getAvailableLinksAlias(deviceInfo);
            
			EditInterfaceDiaglog newLinkDialog = new EditInterfaceDiaglog(
                    WeatherMapClient.getInstance().getFrame(), 
                    deviceInfo, links, alias);
			InterfaceInfo[] interfaceInfo = newLinkDialog.getInterfaceInfo();
			if(interfaceInfo != null) {
				int ok = 0, bad = 0;
				String failedInterfaces = "";
				for(int i = 0; i < interfaceInfo.length; i++) {
					if(dataManager.addInterface(deviceInfo, interfaceInfo[i]) == 0) {
						ok++;
					}
					else {
						bad++;
						failedInterfaces += interfaceInfo[i].getIfDescr() + "@" +
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
		DeviceInfo deviceInfo = findSelectedDevice();
		InterfaceInfo interfaceInfo = findSelectedLink();
		if(deviceInfo == null || interfaceInfo == null) {
			Util.warn(this, "Please, select interface first");
			return;
		}
		try {
            String alias[] = dataManager.getAvailableLinksAlias(deviceInfo);
			EditInterfaceDiaglog newLinkDialog = new EditInterfaceDiaglog(
                    WeatherMapClient.getInstance().getFrame(), deviceInfo, interfaceInfo, alias);
			InterfaceInfo[] interfaceInfoUpdated = newLinkDialog.getInterfaceInfo();
			if(interfaceInfoUpdated != null) {
				if(dataManager.updateInterface(deviceInfo, interfaceInfoUpdated[0]) == 0) {
                    Util.info(this, "Interface " + interfaceInfoUpdated[0].getIfDescr() + "@" +
						deviceInfo.getHost() + " updated successfully");
					reloadData();
				}
				else {
					Util.error(this, "Interface " + interfaceInfoUpdated[0].getIfDescr() + "@" +
						deviceInfo.getHost() + " NOT updated");
				}
			}
		} catch (Exception e) {
			handleException("Could not edit link data", e);
		}
	}
	
	private void editSampling() {
		String sampling = JOptionPane.showInputDialog("Set the sampling interval...(sec)");
		try {
			if(dataManager.updateSamplingInterval(Integer.parseInt(sampling)) == 0) {
	        	Util.info(this, "Sampling Interval was changed successfully");
	        	reloadDataFromHost();
			}
			else {
				Util.error(this, "Updating sampling interval error");
			}


		} catch (Exception e) {
			handleException("Cound not edit sampling interval", e);
		}
	}

	private void removeLink() {
		DeviceInfo deviceInfo = findSelectedDevice();
		InterfaceInfo interfaceInfo = findSelectedLink();
		if(deviceInfo == null || interfaceInfo == null) {
			Util.warn(this, "Please, select interface first");
			return;
		}
		try {
			if(dataManager.removeInterface(deviceInfo, interfaceInfo) == 0) {
            	Util.info(this, "Interface " + interfaceInfo.getIfDescr() + "@" +
					deviceInfo.getHost() + " removed successfully");
				reloadData();
			}
			else {
				Util.error(this, "Interface " + interfaceInfo.getIfDescr() + "@" +
					deviceInfo.getHost() + " NOT removed");
			}

		} catch (Exception e) {
			handleException("Could not remove link", e);
		}
	}

	
	private void graphDevice(int type) {
		DeviceInfo deviceInfo = findSelectedDevice();
		if(deviceInfo == null) { 
			Util.warn(this, "Please, select interface first");
			return;
		}
        GraphInFrame f = new GraphInFrame(deviceInfo, type);
        WeatherMapClient.getInstance().insertFrame(f);
        f.setVisible(true);
	}
	private void graph(int type) {
		DeviceInfo deviceInfo = findSelectedDevice();
		InterfaceInfo interfaceInfo = findSelectedLink();
		if(deviceInfo == null || interfaceInfo == null) {
			Util.warn(this, "Please, select interface first");
			return;
		}
        GraphInFrame f = new GraphInFrame(deviceInfo, interfaceInfo, type);
        WeatherMapClient.getInstance().insertFrame(f);
        f.setVisible(true);
	}

	private DeviceInfo findSelectedDevice() {
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
		if (Conf.DEBUG) {
			e.printStackTrace();
		}
		Util.error(this, msg + ":\n" + e);
	}
	


    public TreeModel getTreeModel() {
		BackendInfo backendInfo = dataManager.getBackendInfo();
		DefaultMutableTreeNode root = new DefaultMutableTreeNode(backendInfo);
		DeviceInfo[] deviceInfo = dataManager.getDevices();
		for(int deviceIndex = 0; deviceIndex < deviceInfo.length; deviceIndex++) {
			DefaultMutableTreeNode deviceNode =
				new DefaultMutableTreeNode(deviceInfo[deviceIndex]);
			root.add(deviceNode);
			InterfaceInfo[] interfaceInfo = deviceInfo[deviceIndex].getInterfaceInfo();
			for(int linkIndex = 0; linkIndex < interfaceInfo.length; linkIndex++) {
				DefaultMutableTreeNode linkNode =
					new DefaultMutableTreeNode(interfaceInfo[linkIndex]);
				deviceNode.add(linkNode);
			}
		}
		return new DefaultTreeModel(root);
	}
}
