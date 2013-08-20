/*
 * @(#)NetworkMapCreator.java
 * 
 * Created on 2005. 12. 16
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

import dpnm.netma.ngom.Conf;
import dpnm.netma.ngom.NGOMException;
import dpnm.netma.ngom.client.comp.ColorBar;
import dpnm.netma.ngom.client.comp.DateComponent;
import dpnm.netma.ngom.client.comp.DeviceComponent;
import dpnm.netma.ngom.client.comp.LinkComponent;
import dpnm.netma.ngom.data.*;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import javax.imageio.*;

/**
 * Map Displayer Class
 * This class is based on Model-View-Controller(MVC) pattern
 *
 * @author Eric Kang
 */
public class NetworkMapCreator {
	private NetworkMapInfo mapInfo;
    private ArrayList<LinkComponent> links;
    private ArrayList<DeviceComponent> devices;
    private NetworkMapLinkInfo selectedLinkInfo = null;
    private DeviceInfo selectedDeviceInfo = null;
    private Image deviceImg[] = new Image[6];

	ColorBar colorBar = new ColorBar();
    DateComponent dateComp = new DateComponent();

    public NetworkMapCreator(NetworkMapInfo info) {
        this.mapInfo = info;
        links = new ArrayList<LinkComponent>();
        devices = new ArrayList<DeviceComponent>();
        
        for (int i = 0; i < 6; i++) {
            deviceImg[i] = WeatherMapClient.rf.getImage(Resources.DEVICE_ICONS[i]);
        }
        loadNetworkMap();
    }
    
    private void loadNetworkMap() {
    	NetworkMapDeviceInfo[] info = mapInfo.getDevices();
    	for (int i = 0; i < info.length; i++) {
    		DeviceComponent comp = new DeviceComponentImpl(info[i].getXpos(), info[i].getYpos());
    		comp.setDeviceInfo(info[i].getDeviceInfo());
            comp.setIcon(deviceImg[info[i].getIcon()], info[i].getIcon());
            comp.setDescr(info[i].getDescr());
            devices.add(comp);
    	}
    	NetworkMapLinkInfo[] linkInfo = mapInfo.getLinks();
    	for (int i = 0; i < linkInfo.length; i++) {
          LinkComponent comp2 = new LinkComponentImpl();
          comp2.setLinkInfo(linkInfo[i]);
          comp2.setLocation(0,0);
          comp2.setSize(mapInfo.getWidth(), mapInfo.getHeight());
          links.add(comp2);
    	}
    }
    
    public byte[] exportPNGImageAsBytes() {
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        BufferedImage bi = new BufferedImage(mapInfo.getWidth(), mapInfo.getHeight()+25,
            BufferedImage.TYPE_INT_RGB);

        Graphics g = bi.getGraphics();
        g.setColor(Color.white);
        g.fillRect(0,0, mapInfo.getWidth(), mapInfo.getHeight()+25);
        g.setFont(Resources.DIALOG_12B);
        g.setColor(Color.black);
        g.drawString("MAP TITLE : " + mapInfo.getName() + " ( " + mapInfo.getDescr()+ " ) - " +
                mapInfo.getWidth() + " x " + mapInfo.getHeight(), 5, 16);
        
        g.translate(0, 25);
        
        /*  test */
    	if (mapInfo !=null && mapInfo.getBackground() != null && 
         		!mapInfo.getBackground().equalsIgnoreCase("")) {
            BufferedImage back = WeatherMapClient.rf.getBufferedImage(
            		Resources.MAP_DIR+mapInfo.getBackground());
    		g.drawImage(back, 0, 0, mapInfo.getWidth(), mapInfo.getHeight(), null); 
    	}
    
        g.translate(10,10); dateComp.paint(g); g.translate(-10,-10);
        g.translate(10,30); colorBar.paint(g); g.translate(-10,-30);


        for (int i = 0; i < devices.size(); i++) {
            DeviceComponent comp = (DeviceComponent)devices.get(i);
            g.translate(comp.getX(), comp.getY()); 
            comp.paint(g);
            g.translate(-comp.getX(), -comp.getY());
        }
    	for (int i = 0; i < links.size(); i++) {
            LinkComponent comp2 = (LinkComponent)links.get(i);
            g.translate(comp2.getX(), comp2.getY()); 
            comp2.paint(g);
            g.translate(-comp2.getX(), -comp2.getY());
    	}
    	
        try {
        	ImageIO.write(bi, "png", outputStream);
        } catch ( Exception ex) {
            
        }
        return outputStream.toByteArray();
    }

    public String exportNetworkMapInfoForJSP() {
    	StringBuffer sb = new StringBuffer();
    	for (int i = 0; i < links.size(); i++) {
            LinkComponent comp = (LinkComponent)links.get(i);
            selectedLinkInfo = comp.getInterfaceInfo();
            String h = selectedLinkInfo.getSrc().getDeviceInfo().getHost();
            String in = selectedLinkInfo.getInterfaceInfo().getIfDescr();
            
            sb.append("<AREA shape=\"poly\" coords=\"");
            sb.append(comp.toHTMLMAP(1.0));
            sb.append("\" href=\"interface.jsp?back=map&host="+h+"&interface="+in+"&map="+mapInfo.getName()+"\"");
            sb.append(" onMouseOver=\"this.style.cursor='pointer'; return overlib('");
            sb.append("<b>"+in+"@"+h+"</b>");
            sb.append("', DELAY, 250, CAPTION, '");
            sb.append(selectedLinkInfo.getInterfaceInfo().getHTMLComment());
            sb.append("');\" onMouseOut=\"return nd();\">\n");
    	}
    	for (int i = 0; i < devices.size(); i++) {
            DeviceComponent comp = (DeviceComponent)devices.get(i);
            selectedDeviceInfo = comp.getDeviceInfo();
            String name = comp.getDeviceInfo().getHost();
            sb.append("<AREA shape=\"poly\" coords=\"");
            sb.append(comp.toHTMLMAP(1.0));
            sb.append("\" href=\"device_util.jsp?back=map&map="+mapInfo.getName()+"&device=");
            sb.append(name+"\"");
            sb.append(" onMouseOver=\"this.style.cursor='pointer'; return overlib('");
            sb.append("<b>"+name+"</b>");
            sb.append("', DELAY, 250, CAPTION, '");
            sb.append(selectedDeviceInfo.getHTMLComment());
            sb.append("');\" onMouseOut=\"return nd();\">\n");
    	}
    	return sb.toString();
    }

 
}
