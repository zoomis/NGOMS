/*
 * @(#)DateComponent.java
 * 
 * Created on 2005. 12. 17
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

import java.awt.Color;
import java.awt.Component;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import dpnm.netma.ngom.client.wmap.Resources;

public class DateComponent extends Component {
	/**
	 * 
	 */
	private static final long serialVersionUID = 2941707080522354075L;
	
	FontMetrics metrics = null;
    SimpleDateFormat dateFormat;
    Calendar cal = null;
    public DateComponent() {
        dateFormat = new SimpleDateFormat("EEE MMM d HH:mm:ss z yyyy",
                Locale.US);
        cal = Calendar.getInstance();
        setSize(250,20);
    }
    
    public void update() {
        Date date = new Date(System.currentTimeMillis());
        cal.setTime(date);
    }

    public void paint(Graphics g) {
        setForeground(Color.black);
        g.setFont(Resources.DIALOG_12B);
        if (metrics == null) {
            metrics = g.getFontMetrics();
        }
        String str = dateFormat.format(cal.getTime());
        
        g.drawString(str, 0, metrics.getMaxAscent());
    }
}
