package dpnm.netma.ngom.client.comp;

import java.awt.*;

import dpnm.netma.ngom.client.wmap.Resources;

public class ColorBar extends Component {
    private static final Color BACKCOLOR = new Color(245,229,245);
    private static final int WIDTH = 100;
	private static Color linkColor[] = {
		new Color(0,235,235),
		new Color(0,163,247),
		new Color(0,0,247),
		new Color(0,255,0),
		new Color(0,199,0),
		new Color(0,143,0),
		new Color(150,150,0),
		new Color(255,255,0),
		new Color(255,143,0),
        new Color(255,0,0),
        new Color(215,0,0),
        new Color(191,0,0),
        new Color(255,0,255),
        new Color(155,87,203),
		new Color(0,0,0)
	};
    private static double range[] = {
        0.005,
        0.01,
        0.05,
        0.1,
        0.5,
        1.0,
        2.0,
        5.0,
        10.0,
        30.0,
        60.0,
        70.0,
        80.0,
        90.0,
        100.0
    };
    private static String rangeStr[] = {
        "0.005",
        "0.01",
        "0.05",
        "0.1",
        "0.5",
        "1",
        "2",
        "5",
        "10",
        "30",
        "60",
        "70",
        "80",
        "90",
        "100"
    };


	public ColorBar() {
		setSize(WIDTH, range.length*10+8+15);
	}
	
	public static Color getColorByUtilization(double utilization) {
        double util = utilization * 100.0; //   for 100 percentage
        int cValue = 255- ((int)(200.0 * utilization)+55);
        return new Color(cValue, cValue, cValue);
	}
	public static Color getColorByMbps(double m) {
        int cValue = ((int)(m/10))*10;
        cValue = cValue > 240 ? 240 : cValue;
        cValue = 240 - cValue;
        return new Color(cValue, cValue, cValue);
	}
	
	/*
	public static Color getColorByUtilization(double utilization) {
        double util = utilization * 100.0; //   for 100 percentage
	    for (int i = 0; i < range.length; i++) {
	        if (util < range[i]) {
	            return linkColor[i];
            }
        }
        return linkColor[0];
	}
	*/
/*	
	public void paint(Graphics g) {
		super.paint(g);
		g.setColor(Color.black);
		g.fillRect(0,0,WIDTH,10*linkColor.length+8);
        g.setColor(BACKCOLOR);
        g.fillRect(1,1,WIDTH-2,10*linkColor.length+6);
        
        g.setFont(Resources.DIALOG_10);
        int i = 0;
		for (i = 0; i < linkColor.length; i++) {
			g.setColor(linkColor[i]);
			g.fillRect(4,4+10*i,30,10);

			g.setColor(Color.black);
			g.drawString("< "+rangeStr[i]+"%", 38, 4+10*i+8);
		}
        g.setFont(Resources.DIALOG_12B);
        g.drawString("Line Utilization", 10, 10+10*i+8);
    }
*/	
	public void paint(Graphics g) {
		super.paint(g);
		g.setColor(Color.black);
		g.fillRect(0,0,WIDTH,10*linkColor.length+8);
        g.setColor(BACKCOLOR);
        g.fillRect(1,1,WIDTH-2,10*linkColor.length+6);
        
        g.setFont(Resources.DIALOG_10);
        int i = 0;
		for (i = 0; i < linkColor.length; i++) {
            int cValue = 255 - (int)((double)i * (255.0/linkColor.length));
			g.setColor(new Color(cValue, cValue, cValue));
			g.fillRect(4,4+10*i,30,10);

			g.setColor(Color.black);
			g.drawString("< "+((i+1)*10)+" M", 38, 4+10*i+8);
		}
        g.setFont(Resources.DIALOG_12B);
        g.drawString("Line Mbps", 10, 10+10*i+8);
    }}
