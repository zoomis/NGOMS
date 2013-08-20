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
package dpnm.netma.ngom.client.comp;

import dpnm.netma.ngom.data.*;

import java.awt.*;

public abstract class LinkComponent extends Component {
	protected int x1, y1, x2, y2; 
	protected Polygon p;
	protected NetworkMapLinkInfo linkInfo;
    protected boolean isEdit = false;
    protected boolean isSelected = false;
    
    protected int status = 0;
	
	public LinkComponent() {
	}
	
	public void setLinkInfo(NetworkMapLinkInfo info) {
		this.linkInfo = info;
    }

    public NetworkMapLinkInfo getInterfaceInfo() {
        return this.linkInfo;
    }
	
	public void setSrc(int x, int y) {
		x1 = x;
		y1 = y;
	}
	
	public void setDst(int x, int y) {
		x2 = x;
		y2 = y;
	}

    /**
     * @return Returns the isEdit.
     */
    public boolean isEdit() {
        return isEdit;
    }

    /**
     * @param isEdit The isEdit to set.
     */
    public void setEdit(boolean isEdit) {
        this.isEdit = isEdit;
    }
 
	public void paint(Graphics g) {
        super.paint(g);
	}
    
    public boolean isIn(int x, int y) {
        return p != null ? p.contains(x, y) : false;
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
    	return new String();
    }
}
