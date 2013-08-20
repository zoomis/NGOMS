/*
 * @(#)DeviceComponent.java
 * 
 * Created on 2005. 12. 14
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

import dpnm.netma.ngom.client.wmap.Resources;
import dpnm.netma.ngom.client.comp.DeviceComponent;
import dpnm.netma.ngom.data.*;

import java.awt.*;
import dpnm.netma.ngom.client.comp.TextComponent;

/**
 * Class Instruction
 *
 * @author Joon-Myung Kang
 */
public class DeviceComponentImpl extends DeviceComponent {
	
	private TextComponent s_comp = null;
    
    public DeviceComponentImpl(int x, int y) {
        super(x,y);
        s_comp = new TextComponent();
        s_comp.setSize(WIDTH, HEIGHT);
        s_comp.setLocation(-20,35);
        s_comp.setAlignment(TextComponent.ALIGN_CENTER);
        s_comp.setForeground(getForeground());
    }

    //  display icon
    public void paint(Graphics g) {
        super.paint(g);
        
        if (isStringVisible) {
        	g.drawImage(iconImage, (WIDTH-30)/2, 0,20,15, this);
        }
        if (comp != null && isStringVisible) {
            comp.setText(getDescr());
            comp.setForeground(getForeground());

            comp.draw((Graphics2D)g);
        }        else {
    		s_comp.setFont(TextComponent.ARIAL_50);
    		String d = info.getDescr();
        	if (d.equals("Core1")) {
        		s_comp.setText("C1");
        	}
        	else if (d.equals("Core2")) {
        		s_comp.setText("C2");
        	}
        	else if (d.equals("BORANET 라우터")) {
        		s_comp.setText("BN");
        	}
        	else if (d.equals("KORNET 라우터")) {
        		s_comp.setText("KN");
        	}
        	else if (d.equals("기숙사")) {
        		s_comp.setText("DM");
        	}
        	else if (d.equals("낙원아파트")) {
        		s_comp.setText("NW");
        	}
        	else {
        		StringBuffer sb = new StringBuffer(info.getHost());
        		int i = sb.indexOf(".");
        		i = sb.indexOf(".", i+1);
        		
        		s_comp.setFont(TextComponent.ARIAL_23);
        		s_comp.setText(sb.substring(i+1).toString());
        	}
        	s_comp.draw((Graphics2D)g);
        }
       
        if (isSelected()) {
            g.setColor(Color.blue);
            g.drawRect(0,0, getWidth() - 1, getHeight() - 1);
        }
    }
    
	public String toHTMLMAP(double scale) {
    	StringBuffer sb = new StringBuffer();
    	
    	sb.append((int)(x*scale) + "," + (int)((y+25)*scale) + ",");
    	sb.append((int)((x+WIDTH)*scale) + "," + (int)((y+25)*scale) + ",");
		sb.append((int)((x+WIDTH)*scale) + "," + (int)((y+HEIGHT+25)*scale) + ",");
		sb.append((int)(x*scale) + "," + (int)((y+HEIGHT+25)*scale));
		
    	return sb.toString();
    }
}
