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
package dpnm.netma.ngom.client.comp;

import dpnm.netma.ngom.client.wmap.Resources;
import dpnm.netma.ngom.data.*;

import java.awt.*;

/**
 * Class Instruction
 *
 * @author Joon-Myung Kang
 */
public class DeviceComponent extends Component {
/**
	 * 
	 */
	private static final long serialVersionUID = 7988004704156632180L;
	//    public static final int WIDTH = 100;
//    public static final int HEIGHT = 72;
    public static final int WIDTH = 70;
    public static final int HEIGHT = 36;
    //  device information
    protected DeviceInfo info;
    protected int icon = 0;
    protected Image iconImage = null;
    protected TextComponent comp = new TextComponent();
    protected boolean isSelected = false;
    protected String descr;
    
    protected int width = WIDTH;
    protected int height = HEIGHT;
    
    protected int x, y;
    protected boolean isStringVisible = true;
    public void setIsStringVisible(boolean is) {
    	isStringVisible = is;
    }
    
    public DeviceComponent(int x, int y) {
        super.setLocation(x,y);
        this.x = x; this.y = y;
        setSize(WIDTH,HEIGHT);
        comp.setSize(WIDTH, 40);
        comp.setLocation(0,15);
        comp.setInsets(new Insets(0,2,0,2));
        comp.setFont(Resources.DIALOG_10);
        comp.setAlignment(TextComponent.ALIGN_CENTER);
    }

    public void setIcon(Image image, int icon) {
        this.icon = icon;
        this.iconImage = image;
    }
    
    public void setDeviceInfo(DeviceInfo info) {
    	this.info = info;
    }
    
    public DeviceInfo getDeviceInfo() {
        return info;
    }
    
    public int getIcon() {
        return icon;
    }

    //  display icon
    public void paint(Graphics g) {
        super.paint(g);
    }
    
    public boolean isIn(int x, int y) {
        Rectangle r = new Rectangle(getX(), getY(), getWidth(), getHeight());
        return r.contains(x, y);
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

	public String getDescr() {
		return descr;
	}

	public void setDescr(String descr) {
		this.descr = descr;
	}
    
	public String toHTMLMAP(double scale) {
    	return new String();
    }
}
