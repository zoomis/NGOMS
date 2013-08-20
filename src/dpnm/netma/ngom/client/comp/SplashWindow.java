/*
 * @(#)SplashWindow.java
 * 
 * Created on 2005. 5. 29
 *
 *	This software is the confidential and proprietary informatioon of
 *	POSTECH DP&NM. ("Confidential Information"). You shall not
 *	disclose such Confidential Information and shall use it only in
 *	accordance with the terms of the license agreement you entered into
 *	with Eric Kang.
 *
 *	Contact: Eric Kang at eliot@postech.edu
 */
package dpnm.netma.ngom.client.comp;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import dpnm.netma.ngom.client.wmap.Resources;
import dpnm.netma.ngom.client.wmap.WeatherMapClient;

/**
 *	A window class that shows the splash window
 *
 *	@author Jun Kang
 *	@since	2001/09/03
 *	@version $Id: SplashWindow.java,v 1.1 2006/07/20 00:56:26 eliot Exp $
 */
public class SplashWindow extends JWindow {
	public SplashWindow(ImageIcon icon) {
		super();

		JLabel l;

		l = new JLabel(icon);

		Dimension labelSize = l.getPreferredSize();
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();

		l.setBounds(0, 0, labelSize.width, labelSize.height);
		getContentPane().setLayout(null);
		getContentPane().add(l);
		setBounds(screenSize.width/2 - (labelSize.width/2),
				screenSize.height/2 - (labelSize.height/2),
				labelSize.width, labelSize.height);
		setVisible(true);
	}
}
