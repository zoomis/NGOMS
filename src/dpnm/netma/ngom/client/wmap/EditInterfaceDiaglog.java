package dpnm.netma.ngom.client.wmap;

import dpnm.netma.ngom.client.comp.Util;
import dpnm.netma.ngom.data.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;

class EditInterfaceDiaglog extends JDialog {
	static final String ADD_TITLE = "New interface";
	static final String EDIT_TITLE = "Edit interface data";
	static final String DEFAULT_SAMPLING_INTERVAL = "60";
	static final int MAX_SAMPLING_INTERVAL = 1800;
	static final int MIN_SAMPLING_INTERVAL = 10;

	// result comes here
	private InterfaceInfo[] interfaceInfo;

	private String[] ifDescrs;
    private String[] ifAlias;
	private DeviceInfo deviceInfo;
	boolean insertMode = false;

	// labels
	private JLabel routerLabel = Util.standardLabel("Router: ");
	private JLabel linksListLabel = Util.standardLabel("Interface: ");
	private JLabel descrLabel = Util.standardLabel("Description: ");
	private JLabel samplingLabel = Util.standardLabel("Sampling interval: ");
	private JLabel activeLabel = Util.standardLabel("Active: ");

	// values
	private JLabel routerValueLabel = new JLabel();
	private JList linksList = new JList();
	private JTextField descrField = Util.standardTextField();
	private JTextField samplingField = Util.standardTextField();
	private JCheckBox activeBox = new JCheckBox("", true);
	private JButton okButton = Util.standardButton("OK");
	private JButton cancelButton = Util.standardButton("Cancel");
    
	EditInterfaceDiaglog(Frame parent, DeviceInfo deviceInfo, String[] ifDescrs) {
        this(parent, deviceInfo, ifDescrs, null);
    }
	EditInterfaceDiaglog(Frame parent, DeviceInfo deviceInfo, String[] ifDescrs, String[] ifAlias) {
		// add link
		super(parent, ADD_TITLE, true);
		this.insertMode = true;
		this.deviceInfo = deviceInfo;
		this.ifDescrs = ifDescrs;
		this.ifAlias = ifAlias;
		constructUserInterface();
		pack();
		setVisible(true);
	}

	EditInterfaceDiaglog(Frame parent, DeviceInfo deviceInfo, InterfaceInfo interfaceInfo) {
        this(parent, deviceInfo, interfaceInfo, null);
	}

	EditInterfaceDiaglog(Frame parent, DeviceInfo deviceInfo, InterfaceInfo interfaceInfo, String[] ifAlias) {
		// edit link
		super(parent, EDIT_TITLE, true);
		this.insertMode = false;
		this.deviceInfo = deviceInfo;
		this.interfaceInfo = new InterfaceInfo[] { interfaceInfo };
        this.ifAlias = ifAlias;
		constructUserInterface();
		pack();
		setVisible(true);
	}

	private void constructUserInterface() {
		JPanel content = (JPanel) getContentPane();
		Box box = Box.createVerticalBox();
		box.add(Util.getPanelFor(routerLabel, routerValueLabel));
		box.add(Util.getPanelFor(linksListLabel, Util.standardScrollPane(linksList)));
		box.add(Util.getPanelFor(descrLabel, descrField));
		box.add(Util.getPanelFor(samplingLabel, samplingField));
		box.add(Util.getPanelFor(activeLabel, activeBox));
		box.add(Util.getPanelFor(Util.standardLabel(""), okButton, cancelButton));
		okButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) { ok(); }
		});
		cancelButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) { cancel(); }
		});
		content.add(box);
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		// populate controls
        routerValueLabel.setText(deviceInfo.getHost());
		if(insertMode) {
			String[] names = new String[ifDescrs.length];
			for (int i = 0; i < names.length; i++) {
				names[i] = ifDescrs[i];
				if (ifAlias.length != 0) {
    				names[i] += "|" + ifAlias[i];
				}
			}
			linksList.setModel(new DefaultComboBoxModel(names));
			linksList.setSelectedIndex(0);
			samplingField.setText(DEFAULT_SAMPLING_INTERVAL);
			activeBox.setSelected(true);
		}
		else {
			linksList.setModel(new DefaultComboBoxModel(deviceInfo.getInterfaces()));
			linksList.setSelectedValue(interfaceInfo[0].getIfDescr(), true);
			linksList.setEnabled(false);
			descrField.setText(interfaceInfo[0].getDescr());
			samplingField.setText("" + interfaceInfo[0].getSamplingInterval());
			activeBox.setSelected(interfaceInfo[0].isActive());
		}
		okButton.setMnemonic(KeyEvent.VK_O);
		cancelButton.setMnemonic(KeyEvent.VK_C);
		getRootPane().setDefaultButton(okButton);
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		Util.centerOnScreen(this);
	}

	private void close() {
		dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
	}

	private void ok() {
		int samplingInterval;
		try {
			samplingInterval = Integer.parseInt(samplingField.getText());
			if(samplingInterval < MIN_SAMPLING_INTERVAL ||
				samplingInterval > MAX_SAMPLING_INTERVAL) {
                throw new NumberFormatException();
			}
		} catch(NumberFormatException nfe) {
			Util.error(this, "Sampling interval must be a number between " +
				MIN_SAMPLING_INTERVAL + " and " + MAX_SAMPLING_INTERVAL);
			return;
		}
		if(insertMode) {
			// new link
			Object[] selectedLinks = linksList.getSelectedValues();
			int[] selectedLinksIndex = linksList.getSelectedIndices();

			int count = selectedLinks.length;
			if(count == 0) {
				Util.error(this, "Select at least one interface to add");
				return;
			}
			interfaceInfo = new InterfaceInfo[count];
			for(int i = 0; i < count; i++) {
				interfaceInfo[i] = new InterfaceInfo();
				interfaceInfo[i].setIfDescr(ifDescrs[selectedLinksIndex[i]]);
				if (ifAlias.length != 0) {
    				interfaceInfo[i].setIfAlias(ifAlias[selectedLinksIndex[i]]);
				}
				interfaceInfo[i].setActive(activeBox.isSelected());
				interfaceInfo[i].setDescr(descrField.getText());
				interfaceInfo[i].setSamplingInterval(samplingInterval);
			}
		}
		else {
			// update link
			interfaceInfo[0].setActive(activeBox.isSelected());
			interfaceInfo[0].setDescr(descrField.getText());
			interfaceInfo[0].setSamplingInterval(samplingInterval);
		}
		close();
	}

	private void cancel() {
		interfaceInfo = null;
		close();
	}

	InterfaceInfo[] getInterfaceInfo() {
		return interfaceInfo;
	}
}
