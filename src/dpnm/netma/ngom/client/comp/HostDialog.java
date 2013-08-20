package dpnm.netma.ngom.client.comp;

import dpnm.netma.ngom.frontend.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;
import java.io.*;

public class HostDialog extends JDialog {
	private static final String HOST_FILENAME = System.getProperty("user.home") +
		System.getProperty("file.separator") + ".last-nws-host";
	private static final String DEFAULT_HOST = "localhost";
	private static final String TITLE = "Select NGOM Backend Manager";

	private String host;

	private JLabel hostLabel = Util.standardLabel("Host address:");
	private JTextField hostField = Util.standardTextField();
	private JButton okButton = Util.standardButton("OK");
	private JButton cancelButton = Util.standardButton("Cancel");

	public HostDialog(Frame parent, String host) {
		super(parent, TITLE, true);
		constructUserInterface();
		pack();
		this.host = host;
		setVisible(true);
	}

	private void constructUserInterface() {
		JPanel content = (JPanel) getContentPane();
		Box box = Box.createVerticalBox();
		box.add(Util.getPanelFor(hostLabel, hostField));
		box.add(Util.getPanelFor(Util.standardLabel(), okButton, cancelButton));
		content.add(box);

		if(host != null) {
			hostField.setText(host);
		}
		else {
			String savedHost = getHostFromFile();
			hostField.setText(savedHost == null? DEFAULT_HOST: savedHost);
		}
		hostField.selectAll();
		okButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) { ok();	}
		});
		cancelButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) { cancel(); }
		});
		okButton.setMnemonic(KeyEvent.VK_O);
		cancelButton.setMnemonic(KeyEvent.VK_C);
		getRootPane().setDefaultButton(okButton);

		// finalzie
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		Util.centerOnScreen(this);
	}

	public String getHost() {
		return host;
	}

	private void ok() {
        String hostEntered = hostField.getText();
		if(hostEntered.length() == 0) {
			Util.warn(this, "Please enter host address");
		}
		else {
			host = hostEntered;
			saveHostToFile();
			close();
		}
	}

	private void close() {
		dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
	}

	private void cancel() {
		close();
	}

	private void saveHostToFile() {
		PrintWriter pw = null;
		try {
			pw = new PrintWriter(new FileWriter(HOST_FILENAME, false));
			pw.println(host);
		}
		catch (IOException e) {
		}
		finally {
			if(pw != null) {
				pw.close();
			}
		}
	}

	private String getHostFromFile() {
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new FileReader(HOST_FILENAME));
			return reader.readLine();
		}
		catch (IOException e) {
			return null;
		}
		finally {
			if(reader != null) {
				try {
					reader.close();
				}
				catch(IOException e) { }
			}
		}
	}
}
