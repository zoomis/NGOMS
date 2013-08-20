package dpnm.netma.ngom.client.wmap;

import dpnm.netma.ngom.data.*;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import java.awt.*;

class TreeRenderer extends DefaultTreeCellRenderer {
	private static ImageIcon MRTG_ICON;
	private static ImageIcon ROUTER_ICON;
	private static ImageIcon LINK_ICON;
	private static ImageIcon INACTIVE_ROUTER_ICON;
	private static ImageIcon INACTIVE_LINK_ICON;

	static {
		MRTG_ICON = WeatherMapClient.rf.getIcon("mrtg.png");
		ROUTER_ICON = WeatherMapClient.rf.getIcon("router.png");
		LINK_ICON = WeatherMapClient.rf.getIcon("link.png");
		INACTIVE_ROUTER_ICON = WeatherMapClient.rf.getIcon( "router_inactive.png");
		INACTIVE_LINK_ICON = WeatherMapClient.rf.getIcon("link_inactive.png");
	}

	TreeRenderer() {
    	setLeafIcon(null);
		setClosedIcon(null);
		setOpenIcon(null);
	}

    public Component getTreeCellRendererComponent(
		JTree tree, Object value, boolean sel, boolean expanded,
		boolean leaf, int row, boolean hasFocus) {
		super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);
		DefaultMutableTreeNode node = (DefaultMutableTreeNode)value;
		Object nodeObj = node.getUserObject();
		if(nodeObj instanceof BackendInfo) {
			setFont(getFont().deriveFont(Font.BOLD));
			setIcon(MRTG_ICON);
		}
		else if (nodeObj instanceof DeviceInfo) {
			setFont(getFont().deriveFont(Font.BOLD));
			DeviceInfo routerInfo = (DeviceInfo) nodeObj;
			//setForeground(routerInfo.isActive()? Color.BLACK: Color.RED);
			setIcon(routerInfo.isActive()? ROUTER_ICON: INACTIVE_ROUTER_ICON);
		}
		else if (nodeObj instanceof InterfaceInfo) {
			setFont(getFont().deriveFont(Font.PLAIN));
			InterfaceInfo linkInfo = (InterfaceInfo) nodeObj;
			//setForeground(linkInfo.isActive()? Color.BLACK: Color.RED);
			setIcon(linkInfo.isActive()? LINK_ICON: INACTIVE_LINK_ICON);
		}
		else {
			setFont(getFont().deriveFont(Font.PLAIN));
			//setForeground(Color.BLACK);
		}
		return this;
	}
}
