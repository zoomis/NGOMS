package dpnm.netma.ngom.client.wmap;

import dpnm.netma.ngom.client.comp.Util;
import dpnm.netma.ngom.data.*;

import javax.swing.*;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;

class InterfaceInfoDialog extends JDialog {
	static final String ADD_TITLE = "New interface";
	static final String EDIT_TITLE = "Edit interface data";
	static final String DEFAULT_SAMPLING_INTERVAL = "60";
	static final int MAX_SAMPLING_INTERVAL = 1800;
	static final int MIN_SAMPLING_INTERVAL = 10;

	// result comes here
	private InterfaceInfo interfaceInfo;

	private String[] ifDescrs;
    private String[] ifAlias;
	private DeviceInfo deviceInfo;
	boolean insertMode = false;

	// labels
	private JLabel routerLabel = Util.standardLabel("Router: ");
	private JLabel linksListLabel = Util.standardLabel("Interface: ");
	private JLabel descrLabel = Util.standardLabel("Description: ");
	private JLabel samplingLabel = Util.standardLabel("Sampling interval: ");
    private JLabel kindLabel = Util.standardLabel("Target:");
	private JLabel activeLabel = Util.standardLabel("Active: ");

	// values
	private JLabel routerValueLabel = new JLabel();
    private JComboBox interfaceCombo = new JComboBox();
	private JTextField descrField = Util.standardTextField();
	private JTextField samplingField = Util.standardTextField();
    private JComboBox kindCombo = new JComboBox();
	private JCheckBox activeBox = new JCheckBox("", true);
	private JButton okButton = Util.standardButton("OK");
	private JButton cancelButton = Util.standardButton("Cancel");
    
    private int kind = 0;

    InterfaceInfoDialog(Frame parent, DeviceInfo deviceInfo, String[] ifDescrs, String[] ifAlias) {
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
    
    InterfaceInfoDialog(Frame parent, DeviceInfo deviceInfo, InterfaceInfo interfaceInfo, int kind) {
        // edit link
        super(parent, EDIT_TITLE, true);
        this.insertMode = false;
        this.deviceInfo = deviceInfo;
        this.interfaceInfo = interfaceInfo;
        this.kind = kind;
        constructUserInterface();
        pack();
        setVisible(true);
    }
    
	private void constructUserInterface() {
		JPanel content = (JPanel) getContentPane();
		Box box = Box.createVerticalBox();
		box.add(Util.getPanelFor(routerLabel, routerValueLabel));
//		box.add(Util.getPanelFor(linksListLabel, Util.standardScrollPane(linksList)));
        box.add(Util.getPanelFor(linksListLabel, interfaceCombo));
		box.add(Util.getPanelFor(descrLabel, descrField));
		box.add(Util.getPanelFor(samplingLabel, samplingField));
        box.add(Util.getPanelFor(kindLabel, kindCombo));
		box.add(Util.getPanelFor(activeLabel, activeBox));
		box.add(Util.getPanelFor(Util.standardLabel(""), okButton, cancelButton));
		okButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) { ok(); }
		});
		cancelButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) { cancel(); }
		});
		content.add(box);
        kindCombo.addItem("Output Utilization");
        kindCombo.addItem("Input Utilization");
        kindCombo.addItem("Total Utilization");
        kindCombo.setSelectedIndex(kind);
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		// populate controls
        routerValueLabel.setText(deviceInfo.getHost());

        if(insertMode) {
            for (int i = 0; i < ifDescrs.length; i++) {
                interfaceCombo.addItem(ifDescrs[i] + "|"+ifAlias[i]);
            }
            interfaceCombo.setSelectedIndex(0);
            samplingField.setText(DEFAULT_SAMPLING_INTERVAL);
            activeBox.setSelected(true);
        }
        else {
            String descrs[] = deviceInfo.getInterfaces();
            String alias[] = deviceInfo.getAlias();
            for (int i = 0; i < descrs.length; i++) {
                interfaceCombo.addItem(descrs[i] + "|"+alias[i]);
            }
            ifDescrs = descrs;
            ifAlias = alias;
            interfaceCombo.setEnabled(false);
            descrField.setText(interfaceInfo.getDescr());
            samplingField.setText("" + interfaceInfo.getSamplingInterval());
            kindCombo.setSelectedIndex(kind);
            activeBox.setSelected(interfaceInfo.isActive());
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
            interfaceInfo = new InterfaceInfo();
            interfaceInfo.setIfDescr(ifDescrs[interfaceCombo.getSelectedIndex()]);
            interfaceInfo.setIfAlias(ifAlias[interfaceCombo.getSelectedIndex()]);
            interfaceInfo.setActive(activeBox.isSelected());
            interfaceInfo.setDescr(descrField.getText());
            interfaceInfo.setSamplingInterval(samplingInterval);
            kind = kindCombo.getSelectedIndex();
        }
        else {
            // update link
            interfaceInfo.setActive(activeBox.isSelected());
            interfaceInfo.setDescr(descrField.getText());
            interfaceInfo.setSamplingInterval(samplingInterval);
            kind = kindCombo.getSelectedIndex();
        }
		close();
	}

	private void cancel() {
		interfaceInfo = null;
		close();
	}

	InterfaceInfo getInterfaceInfo() {
		return interfaceInfo;
	}

    /**
     * @return Returns the kind.
     */
    public int getKind() {
        return kind;
    }

    /**
     * @param kind The kind to set.
     */
    public void setKind(int kind) {
        this.kind = kind;
    }
}
