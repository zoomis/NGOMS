/*
 * @(#)LinkComponent.java
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

import dpnm.netma.ngom.data.*;
import dpnm.netma.ngom.client.comp.ColorBar;
import dpnm.netma.ngom.client.comp.LinkComponent;
import dpnm.netma.ngom.client.comp.DeviceComponent;
import dpnm.netma.ngom.client.comp.TextComponent;

import java.awt.*;

public class LinkComponentImpl extends LinkComponent {
    private static final int W = 3;

    protected TextComponent textComponent;
    
	public LinkComponentImpl() {
		super();
		textComponent = new TextComponent();
        textComponent.setAlignment(TextComponent.ALIGN_CENTER);
        textComponent.setInsets(new Insets(0,2,0,2));
        textComponent.setForeground(Color.black);
	}
	
	public void setLinkInfo(NetworkMapLinkInfo info) {
		super.setLinkInfo(info);
		updatePoints();
    }

	public void updatePoints() {
        NetworkMapLinkInfo info = linkInfo;
        if (info.getKind() == 1) {
            setPoints(linkInfo.getDst().getXpos(), linkInfo.getDst().getYpos(),
                    linkInfo.getSrc().getXpos(), linkInfo.getSrc().getYpos());    
        } else {
            setPoints(linkInfo.getSrc().getXpos(), linkInfo.getSrc().getYpos(),
                    linkInfo.getDst().getXpos(), linkInfo.getDst().getYpos());
    
        }   
    }
    
    public void setPoints(int sX, int sY, int dX, int dY) {
        int d_width = DeviceComponent.WIDTH;
        int d_height = DeviceComponent.HEIGHT;
        sX = sX + d_width/2;
        sY = sY + d_height/2;
        dX = dX + d_width/2;
        dY = dY + d_height/2;
        if (sX == dX) { //  X의 위치가 서로 같을 때
            if (dY - sY > 0) {
                setSrc(sX, sY+d_height/2);
                setDst(sX, dY-d_height/2);
                status = 0;
            } else {
                setSrc(sX, sY-d_height/2);
                setDst(sX, dY+d_height/2);    
                status = 1;
            }
        } else if (sY == dY) {  //  Y의 위치가 서로 같을 때
            if (dX - sX > 0) {
                setSrc(sX+d_width/2, sY);
                setDst(dX-d_width/2, dY);
                status = 2;
            } else {
                setSrc(sX-d_width/2, sY);
                setDst(dX+d_width/2, dY);   
                status = 3;
            }
        
        } else if ( (double)d_height/d_width 
                < (double)Math.abs(dY-sY)/Math.abs(dX-sX)) { // 가로변
            int part = (int)((double)d_height/2*Math.abs(dX-sX)/Math.abs(dY-sY));
            if (dY - sY > 0) {
                if (dX - sX > 0) {
                    setSrc(sX+part, sY+d_height/2);
                    setDst(dX-part, dY-d_height/2);
                    status = 0;
                } else {
                    setSrc(sX-part, sY+d_height/2);
                    setDst(dX+part, dY-d_height/2);
                    status = 5;
                }
            } else {
                if (dX - sX > 0) {
                    setSrc(sX+part, sY-d_height/2);
                    setDst(dX-part, dY+d_height/2);
                    status = 4;
                } else {
                    setSrc(sX-part, sY-d_height/2);
                    setDst(dX+part, dY+d_height/2);
                    status = 1;
                }
            }
        } else { // 세로변
            int part = (int)((double)d_width/2*Math.abs(dY-sY)/Math.abs(dX-sX));
            if (dY - sY > 0) {
                if (dX - sX > 0) {
                    setSrc(sX+d_width/2, sY+part);
                    setDst(dX-d_width/2, dY-part);
                    status = 6;
                } else {
                    setSrc(sX-d_width/2, sY+part);
                    setDst(dX+d_width/2, dY-part);
                    status = 3;
                }
            } else {
                if (dX - sX > 0) {
                    setSrc(sX+d_width/2, sY-part);
                    setDst(dX-d_width/2, dY+part);
                    status = 2;
                } else {
                    setSrc(sX-d_width/2, sY-part);
                    setDst(dX+d_width/2, dY+part);
                    status = 7;
                }
            }
        }
        
        int x[] = new int[6];
        int y[] = new int[6];

        double cos, sin;

        cos = (double)(x2 - x1) / Math.hypot(x2 - x1, y2 - y1);
        sin = (double)(y2 - y1) / Math.hypot(x2 - x1, y2 - y1);
        
        if (linkInfo.getKind() == 2) {
            x[0] = (int)(x1 + (sin * W));
            y[0] = (int)(y1 - (cos * W));
            x[1] = (int)(x2 + (sin * W));
            y[1] = (int)(y2 - (cos * W));
            x[2] = (int)(x2 - (sin * W));
            y[2] = (int)(y2 + (cos * W));
            x[3] = (int)(x1 - (sin * W));
            y[3] = (int)(y1 + (cos * W));
            x[4] = (int)(x1 + (sin * W));
            y[4] = (int)(y1 - (cos * W));

            p = new Polygon(x, y, 5);            
        } else {
            x[0] = x1;
            y[0] = y1;
            x[1] = (int)(x1 + (sin * W));
            y[1] = (int)(y1 - (cos * W));
            x[2] = (int)(x2 + (sin * W) - (cos * 2 * W));
            y[2] = (int)(y2 - (cos * W) - (sin * 2 * W));
            x[3] = (int)(x[2] + (sin * W));
            y[3] = (int)(y[2] - (cos * W));
            x[4] = x2;
            y[4] = y2;
            x[5] = x1;
            y[5] = y1;
            p = new Polygon(x, y, 6);
        }
    }
	
	public void paint(Graphics g) {
        super.paint(g);
        double value = 0;
        switch (linkInfo.getKind()) {
        case 0:
            value = linkInfo.getInterfaceInfo().getOutKbps()/1024;
            break;
        case 1:
            value = linkInfo.getInterfaceInfo().getInKbps()/1024;
            break;
        case 2:
            value = linkInfo.getInterfaceInfo().getTotalKbps()/1024;
            break;
        }
 
        if (isEdit) {
            if (isSelected()) {
                g.setColor(Color.BLUE);
            } else {
                g.setColor(Color.BLACK);
            }
        } else {
            g.setColor(ColorBar.getColorByMbps(value));
        }
        if (p != null) {
            g.fillPolygon(p);
        }
        if (isEdit) {
            return;
        }
	}
    
    public boolean isIn(int x, int y) {
        return p != null ? p.contains(x, y) : false;
    }
    
    private String getDoubleString(double value) {
        StringBuffer sb = new StringBuffer();
        int v = (int)value;
        sb.append(v);
        sb.append(".");
        value = value - v;
        v = (int)((value+0.005) * 100);
        sb.append(v);
        return sb.toString();
    }

    /**
     * @return Returns the isSelected.
     */
    public boolean isSelected() {
        return isSelected;
    }

    /**
     * @param isSelected The isSelected to set.
     */
    public void setSelected(boolean isSelected) {
        this.isSelected = isSelected;
    }
    
    public String toHTMLMAP(double scale) {
    	StringBuffer sb = new StringBuffer();
    	int x[] = p.xpoints;
    	int y[] = p.ypoints;
    	
    	int i = 0;
    	for (; i < x.length-1; i++) {
    		sb.append((int)(x[i]*scale) +",");
    		sb.append((int)((y[i]+25)*scale) +",");
    	}
		sb.append((int)(x[i]*scale) +",");
		sb.append((int)((y[i]+25)*scale));
    	return sb.toString();
    }
}
