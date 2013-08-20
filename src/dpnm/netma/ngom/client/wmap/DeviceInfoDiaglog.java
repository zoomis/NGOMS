package dpnm.netma.ngom.client.wmap;

import dpnm.netma.ngom.client.comp.Util;
import dpnm.netma.ngom.data.*;

import javax.swing.*;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;

class DeviceInfoDialog extends JDialog {
	/**
	 * 
	 */
	private static final long serialVersionUID = -7664798773047441786L;
	static final String ADD_TITLE = "New device";
	static final String EDIT_TITLE = "Edit device data";

	private DeviceInfo deviceInfo;
    private DeviceInfo[] devices;
	private int iconNum = 0;
    
    private ImageIcon deviceImg[] = new ImageIcon[6];
 

	private JLabel hostLabel = Util.standardLabel("Address: ");
	private JLabel communityLabel = Util.standardLabel("Community: ");
	private JLabel descrLabel = Util.standardLabel("Description: ");
	private JLabel activeLabel = Util.standardLabel("Active: ");
    private JLabel iconLabel = Util.standardLabel("Icon: ");
	private JComboBox hostCombo = new JComboBox();
	private JTextField communityField = Util.standardTextField();
	private JTextField descrField = Util.standardTextField();
	private JCheckBox activeBox = new JCheckBox("", true);
	private JButton okButton = Util.standardButton("OK");
	private JButton cancelButton = Util.standardButton("Cancel");
	private JComboBox iconCombo = new JComboBox();
	
	DeviceInfoDialog(Frame parent, DeviceInfo[] devices) {
		this(parent, devices, null,0);
	}

    DeviceInfoDialog(Frame parent, DeviceInfo[] devices, DeviceInfo deviceInfo, int icon) {
		super(parent, deviceInfo == null? ADD_TITLE: EDIT_TITLE, true);
		this.deviceInfo = deviceInfo;
		this.devices = devices;

        for (int i = 0; i < 6; i++) {
            deviceImg[i] = WeatherMapClient.rf.getIcon(Resources.DEVICE_ICONS[i]);
        }

		constructUserInterface();
        iconCombo.setSelectedIndex(icon);
		pack();
		setVisible(true);
	}

	private void constructUserInterface() {
        for (int i = 0; i < 6; i++) {
            iconCombo.addItem(deviceImg[i]);
        }
        JPanel content = (JPanel) getContentPane();
		Box box = Box.createVerticalBox();
		box.add(Util.getPanelFor(hostLabel, hostCombo));
//		box.add(Util.getPanelFor(communityLabel, communityField));
//		communityField.setText("culture");
//		box.add(Util.getPanelFor(descrLabel, descrField));
//		box.add(Util.getPanelFor(activeLabel, activeBox));
		box.add(Util.getPanelFor(iconLabel, iconCombo));
		box.add(Util.getPanelFor(Util.standardLabel(), okButton, cancelButton));
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
		if (devices != null) {
			for (int i = 0; i < devices.length; i++) {
				hostCombo.addItem(devices[i].getHost() +"("+devices[i].getDescr()+")");
			}
		}
		if(deviceInfo != null) {
			hostCombo.setSelectedItem(deviceInfo.getHost());
			hostCombo.setEnabled(false);
			communityField.setText(deviceInfo.getCommunity());
			descrField.setText(deviceInfo.getDescr());
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
//		if(hostField.getText().length() == 0) {
//			Util.warn(this, "Please enter device address");
//		}
//		else if(communityField.getText().length() == 0) {
//			Util.warn(this, "Please eneter device community");
//		}
//		else {

//		    deviceInfo = new DeviceInfo();
//			deviceInfo.setHost(hostCombo.get);
//			deviceInfo.setCommunity(communityField.getText());
//			deviceInfo.setDescr(descrField.getText());
//			deviceInfo.setActive(activeBox.isSelected());
			if (devices != null) {
				deviceInfo = devices[hostCombo.getSelectedIndex()];
				deviceInfo.setDescr(descrField.getText());
			}
			iconNum = iconCombo.getSelectedIndex();
			close();
//		}
	}

	private void cancel() {
		deviceInfo = null;
		close();
	}

	DeviceInfo getDeviceInfo() {
		return deviceInfo;
	}
    
    int getIcon() {
        return iconCombo.getSelectedIndex();
    }

    /**
     * @return Returns the iconNum.
     */
    public int getIconNum() {
        return iconNum;
    }

    /**
     * @param iconNum The iconNum to set.
     */
    public void setIconNum(int iconNum) {
        this.iconNum = iconNum;
    }
}
