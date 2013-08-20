package dpnm.netma.ngom.client.wmap;

import dpnm.netma.ngom.client.comp.Util;
import dpnm.netma.ngom.data.*;
import dpnm.netma.ngom.Conf;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;

class EditDeviceDialog extends JDialog {
	static final String ADD_TITLE = "New device";
	static final String EDIT_TITLE = "Edit device data";

	private DeviceInfo deviceInfo;

	private JLabel hostLabel = Util.standardLabel("Address: ");
	private JLabel netmaskLabel = Util.standardLabel("Netmask: ");
	private JLabel communityLabel = Util.standardLabel("Community: ");
	private JLabel descrLabel = Util.standardLabel("Description: ");
	private JLabel portLabel = Util.standardLabel("SNMP Port: ");
	private JLabel activeLabel = Util.standardLabel("Active: ");
	private JTextField hostField = Util.standardTextField();
	private JTextField netmaskField = Util.standardTextField();
	private JTextField communityField = Util.standardTextField();
	private JTextField descrField = Util.standardTextField();
	private JTextField portField = Util.standardTextField();
	private JCheckBox activeBox = new JCheckBox("", true);
	private JButton okButton = Util.standardButton("OK");
	private JButton cancelButton = Util.standardButton("Cancel");

	EditDeviceDialog(Frame parent) {
		this(parent, null);
	}

	EditDeviceDialog(Frame parent, DeviceInfo deviceInfo) {
		super(parent, deviceInfo == null? ADD_TITLE: EDIT_TITLE, true);
		this.deviceInfo = deviceInfo;
		constructUserInterface();
		pack();
		setVisible(true);
	}

	private void constructUserInterface() {
		JPanel content = (JPanel) getContentPane();
		Box box = Box.createVerticalBox();
		box.add(Util.getPanelFor(hostLabel, hostField));
		box.add(Util.getPanelFor(netmaskLabel, netmaskField));
		netmaskField.setText("255.255.255.0");
		box.add(Util.getPanelFor(communityLabel, communityField));
		communityField.setText("culture");
		box.add(Util.getPanelFor(descrLabel, descrField));
		box.add(Util.getPanelFor(portLabel, portField));
		communityField.setText("161");
		box.add(Util.getPanelFor(activeLabel, activeBox));
		box.add(Util.getPanelFor(Util.standardLabel(), okButton, cancelButton));
		
		hostField.setText(Conf.DEFAULT_HOST);
		
		okButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				ok();
			}
		});
		cancelButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				cancel();
			}
		});
		content.add(box);
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		// populate controls if possible
        if(deviceInfo != null) {
			hostField.setText(deviceInfo.getHost());
			hostField.setEnabled(false);
			netmaskField.setText(deviceInfo.getNetmask());
			communityField.setText(deviceInfo.getCommunity());
			descrField.setText(deviceInfo.getDescr());
			portField.setText(String.valueOf(deviceInfo.getPort()));
            activeBox.setSelected(deviceInfo.isActive());
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
		if(hostField.getText().length() == 0) {
			Util.warn(this, "Please enter device address");
		}
		else if(communityField.getText().length() == 0) {
			Util.warn(this, "Please eneter device community");
		}
		else {
			deviceInfo = new DeviceInfo();
			deviceInfo.setHost(hostField.getText());
			deviceInfo.setNetmask(netmaskField.getText());
			deviceInfo.setCommunity(communityField.getText());
			deviceInfo.setDescr(descrField.getText());
			deviceInfo.setPort(Integer.parseInt(portField.getText()));
			deviceInfo.setActive(activeBox.isSelected());
			close();
		}
	}

	private void cancel() {
		deviceInfo = null;
		close();
	}

	DeviceInfo getDeviceInfo() {
		return deviceInfo;
	}
}
