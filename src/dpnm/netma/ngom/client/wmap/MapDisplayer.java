/*
 * @(#)MapDsplayer.java
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
import dpnm.netma.ngom.client.comp.DesComponent;
import dpnm.netma.ngom.client.comp.DeviceComponent;
import dpnm.netma.ngom.client.comp.HoverButton;
import dpnm.netma.ngom.client.comp.LinkComponent;
import dpnm.netma.ngom.data.*;
import dpnm.netma.ngom.frontend.DataManager;
import dpnm.netma.ngom.util.Logger;

import java.io.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;

import javax.swing.*;
import javax.swing.event.InternalFrameAdapter;
import javax.swing.event.InternalFrameEvent;

import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;
import java.util.Date;

import javax.imageio.*;

import org.jrobin.graph.RrdGraph;

/**
 * Map Displayer Class
 * This class is based on Model-View-Controller(MVC) pattern
 *
 * @author Eric Kang
 */
public class MapDisplayer extends JInternalFrame implements MouseListener, 
    MouseMotionListener {
    private static final Color DES_COLOR_ROUTER = new Color(224,224,255,224);
    private static final Color DES_COLOR_LINK   = new Color(255,224,224,224);
    

    private long timeOut = 10000L;

	private NetworkMapInfo mapInfo;
    private Vector<LinkComponent> links;
    private Vector<DeviceComponent> devices;
    private NetworkMapLinkInfo selectedLinkInfo = null;
    private DeviceInfo selectedDeviceInfo = null;
    private Image deviceImg[] = new Image[6];
	/**
	* The offset pixels of the internal windows.
	*/
	private static final int _windowOffset = 30 ;
	
	// This model is based on MVC structure

    JPanel view;
	ColorBar colorBar = new ColorBar();
    DateComponent dateComp = new DateComponent();
    DesComponent desComp = new DesComponent();
    Timer timer = null;

    //Link Popup menu
    JPopupMenu linksPopupMenu = new JPopupMenu();
	JMenuItem linksPopupQuickGraphMenuItem = new JMenuItem("Quick graph (last 24hr)...");
	JMenuItem linksPopupDailyGraphMenuItem = new JMenuItem("Daily graph...");
	JMenuItem linksPopupWeeklyGraphMenuItem = new JMenuItem("Weekly graph...");
	JMenuItem linksPopupMonthlyGraphMenuItem = new JMenuItem("Monthly graph...");
	JMenuItem linksPopupYearlyGraphMenuItem = new JMenuItem("Yearly graph...");
	JMenuItem linksPopupCustomGraphMenuItem = new JMenuItem("Custom graph...");


    public MapDisplayer(NetworkMapInfo info, long timeOut) {
        this(info.getName() + " - " + info.getDescr() + " ["+(timeOut/1000)+" sec]", true, true, true, true, 
        		info.getWidth(), info.getHeight(), info, timeOut);
    }
 
    /**
     * Constructor
     */
    public MapDisplayer(String title, boolean resizable, boolean closable, boolean maximizable, boolean iconifiable, 
    		int width, int height, NetworkMapInfo info, long timeOut) {
        super(title, resizable, closable, maximizable, iconifiable);
        this.mapInfo = info;
        timer = new Timer();
        links = new Vector<LinkComponent>();
        devices = new Vector<DeviceComponent>();
        this.timeOut = timeOut;
        
        setSize(width, height);
        
        for (int i = 0; i < 6; i++) {
            deviceImg[i] = WeatherMapClient.rf.getImage(Resources.DEVICE_ICONS[i]);
        }
        //	decide location of internal frame
		setLocation(WeatherMapClient.getNumWindow() * _windowOffset, WeatherMapClient.getNumWindow() * _windowOffset) ;
        WeatherMapClient.increaseNumWindow();
		
		createUI();

        loadNetworkMap();
        
        addDesComponent();
        
        addInternalFrameListener(new InternalFrameAdapter()  //종료 버튼을 눌렀을 경우에 대한 리스너 등록
        {
            public void internalFrameClosing(InternalFrameEvent e) 
            {
                exit();     //종료 버튼을 눌렀을 경우 수행
            }
        });
        
        timer.scheduleAtFixedRate(new MyTimerTask(),0, timeOut);
    }
    
    void addDesComponent() {
        desComp = new DesComponent();
        desComp.setLocation(200,100);
        desComp.setVisible(false);
        view.add(desComp,0);
    }

    void setNetworkMapInfo(NetworkMapInfo info) {
        timer.cancel();
        resetView();
        this.mapInfo = info;
        setTitle(info.getName() + " - " + info.getDescr() + " ["+(timeOut/1000)+" sec]");
        //view.setSize(info.getWidth(), info.getHeight());
        view.setPreferredSize(new Dimension(info.getWidth(), info.getHeight()));
        setSize(info.getWidth(), info.getHeight());
        loadNetworkMap();
        timer.scheduleAtFixedRate(new MyTimerTask(),0, timeOut);
        repaint();
    }
    
    NetworkMapInfo getNetworkMapInfo() {
        return mapInfo;
    }
    
    private void resetView() {
        view.removeMouseListener(this);
        view.removeMouseMotionListener(this);
        view.removeAll();
        devices.removeAllElements();
        links.removeAllElements();
        mapInfo = null;
        dateComp.update();
        dateComp.setLocation(10,10);
        view.add(dateComp);
        colorBar.setLocation(10,30);
        view.add(colorBar);
        desComp = new DesComponent();
        desComp.setLocation(200,100);
        desComp.setVisible(false);
        view.add(desComp);
        getContentPane().add(view, BorderLayout.CENTER);
        
        view.addMouseMotionListener(this);
        view.addMouseListener(this);
    }
    private void createUI() {
        JPanel contentPane = new JPanel();
        contentPane.setLayout(new BorderLayout());
        contentPane.add(createToolBar(), BorderLayout.NORTH) ;
        setContentPane(contentPane);
        view = new JPanel();
        view.setBackground(Color.white);
        view.setLayout(null);
        dateComp.update();
        dateComp.setLocation(10,10);
        view.add(dateComp);
        colorBar.setLocation(10,30);
        view.add(colorBar);
        
       
    	if (mapInfo !=null && mapInfo.getBackground() != null && 
         		!mapInfo.getBackground().equalsIgnoreCase("")) {
            JLabel label = new JLabel();
            label.setLocation(0,0);
            label.setIcon(getBackgroundImage());
            
            label.setSize(new Dimension(mapInfo.getWidth(), mapInfo.getHeight()));
            view.add(label);
    	}
        
        view.setPreferredSize(new Dimension(mapInfo.getWidth(), mapInfo.getHeight()));
        JScrollPane js = new JScrollPane(view, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, 
                JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        //getContentPane().add(view, BorderLayout.CENTER);
        getContentPane().add(js, BorderLayout.CENTER);

        view.addMouseMotionListener(this);
        view.addMouseListener(this);
        //  graph popup menu

		linksPopupQuickGraphMenuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) { graph(GraphFrame.TYPE_QUICK); }
		});
        linksPopupMenu.add(linksPopupQuickGraphMenuItem);
		linksPopupDailyGraphMenuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) { graph(GraphFrame.TYPE_DAILY); }
		});
        linksPopupMenu.add(linksPopupDailyGraphMenuItem);
		linksPopupWeeklyGraphMenuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) { graph(GraphFrame.TYPE_WEEKLY); }
		});
        linksPopupMenu.add(linksPopupWeeklyGraphMenuItem);
		linksPopupMonthlyGraphMenuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) { graph(GraphFrame.TYPE_MONTHLY); }
		});
        linksPopupMenu.add(linksPopupMonthlyGraphMenuItem);
		linksPopupYearlyGraphMenuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) { graph(GraphFrame.TYPE_YEARLY); }
		});
        linksPopupMenu.add(linksPopupYearlyGraphMenuItem);

        linksPopupCustomGraphMenuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) { graph(GraphFrame.TYPE_CUSTOM); }
		});
        linksPopupMenu.add(linksPopupCustomGraphMenuItem);

    }
    protected JToolBar createToolBar()
    {
        JToolBar toolBar = new JToolBar() ;
        JButton button ;

        button = new HoverButton("Export PNG image", 
                WeatherMapClient.rf.getIcon(Resources.EXPORT_IMG_STR));

        button.setToolTipText("Save to PNG image");
        button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ev) {
                exportPNGImage();
            }
        });
        toolBar.add(button);
    
        return toolBar ;
    }
    
    private void loadNetworkMap() {
    	NetworkMapDeviceInfo[] info = mapInfo.getDevices();
    	for (int i = 0; i < info.length; i++) {
    		DeviceComponent comp = new DeviceComponentImpl(info[i].getXpos(), info[i].getYpos());
    		comp.setDeviceInfo(info[i].getDeviceInfo());
            comp.setIcon(deviceImg[info[i].getIcon()], info[i].getIcon());
            comp.setDescr(info[i].getDescr());
    		view.add(comp,0);
            devices.add(comp);
    	}
    	NetworkMapLinkInfo[] linkInfo = mapInfo.getLinks();
    	for (int i = 0; i < linkInfo.length; i++) {
          LinkComponent comp2 = new LinkComponentImpl();
          comp2.setLinkInfo(linkInfo[i]);
          comp2.setLocation(0,0);
          comp2.setSize(getWidth(), getHeight());
          links.add(comp2);
          view.add(comp2,0);
    	}
        repaint();
    }
    
    private NetworkMapDeviceInfo getNetworkMapDeviceInfo(String host) {
        NetworkMapDeviceInfo[] info = mapInfo.getDevices();
        for (int i = 0; i < info.length; i++) {
            if (info[i].getDeviceInfo().getHost().equalsIgnoreCase(host)) {
                return info[i];
            }
        }
        return null;
    }
      
    /**
     * exit map displayer
     */
    public void exit() {
    	if (Conf.DEBUG) {
    		Logger.getInstance().logAppClient("MapDisplayer", 
    				"EXIT");
    	}
        timer.cancel();
    }
    
    /**
     * print the model
     *
     */
    public void print() {
        
    }
    
    /**
     * export the model to the file
     *
     */
    public void export() {
        
    }

 	private void graph(int type) {
        if (selectedLinkInfo != null) {
            GraphInFrame f = new GraphInFrame(selectedLinkInfo.getSrc().getDeviceInfo(), 
                    selectedLinkInfo.getInterfaceInfo(), type);
            WeatherMapClient.getInstance().insertFrame(f);
            f.setVisible(true);
	    }
    }

 	
  	void exportGraph(String name, Date start, Date end) {
        for (int i = 0; i < links.size(); i++) {
            LinkComponent comp = (LinkComponent)links.get(i);
            selectedLinkInfo = comp.getInterfaceInfo();
            try {
            	byte[] graphBytes = DataManager.getInstance().getPngGraph(
	                    selectedLinkInfo.getSrc().getDeviceInfo(),
	                    selectedLinkInfo.getInterfaceInfo(),
	                    start,
	                    end);
        		String fileName = name+ "_"+ 
        			selectedLinkInfo.getInterfaceInfo().getIfDescr().replaceAll("[^0-9a-zA-Z]", "_") +
    			"@" + selectedLinkInfo.getSrc().getDeviceInfo().getHost().replaceFirst(":", "_") + ".png";
				BufferedOutputStream out = new BufferedOutputStream(
						new FileOutputStream(new File(fileName)));

				out.write(graphBytes);
				out.close();
            } catch (Exception ex) {
            	ex.printStackTrace();
            }
        }      
        for (int i = 0; i < devices.size(); i++) {
            DeviceComponent comp = (DeviceComponent)devices.get(i);
            try {
            	byte[] graphBytes = DataManager.getInstance().getPngGraph(
            			comp.getDeviceInfo(),
	                    null,
	                    start,
	                    end);
        		String fileName = name+ "_"+ 
    				comp.getDeviceInfo().getHost().replaceFirst(":", "_") + ".png";
				BufferedOutputStream out = new BufferedOutputStream(
						new FileOutputStream(new File(fileName)));

				out.write(graphBytes);
				out.close();
            } catch (Exception ex) {
            	ex.printStackTrace();
            }
        }      
    }
  
    public class MyTimerTask extends TimerTask {
        public void run() {
            try {
                DataManager.getInstance().reloadData();
                for (int i = 0; i < mapInfo.getLinks().length; i++) {
                    NetworkMapLinkInfo info = mapInfo.getLinks()[i];
                    info.setInterfaceInfo(DataManager.getInstance().getInterfaceInfoByIfDescr(
                            DataManager.getInstance().getDeviceIndexByHost(
                                    info.getSrc().getDeviceInfo().getHost()), 
                            info.getInterfaceInfo().getIfDescr()));
                    LinkComponent comp = (LinkComponent)links.elementAt(i);
                    comp.setLinkInfo(info);
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            dateComp.update();
            view.repaint();
        }
    }

    public void mouseClicked(MouseEvent e) {
        // TODO Auto-generated method stub
        
    }

    public void mousePressed(MouseEvent e) {
        // TODO Auto-generated method stub
        if (e.getModifiers() == MouseEvent.BUTTON3_MASK) {
            for (int i = 0; i < links.size(); i++) {
                LinkComponent comp = (LinkComponent)links.elementAt(i);
                if (comp.isIn(e.getX(), e.getY())) {
                    desComp.setVisible(false);
                    selectedLinkInfo = comp.getInterfaceInfo();
                    linksPopupMenu.show(view, e.getX(), e.getY()); 
                    return;
                }
            }
        }
    }

    public void mouseReleased(MouseEvent e) {
        // TODO Auto-generated method stub
        
    }

    public void mouseEntered(MouseEvent e) {
        // TODO Auto-generated method stub
        
    }

    public void mouseExited(MouseEvent e) {
        // TODO Auto-generated method stub
        
    }

    public void mouseDragged(MouseEvent e) {
        // TODO Auto-generated method stub
        
    }

    public void mouseMoved(MouseEvent e) {
        // TODO Auto-generated method stub
        for (int i = 0; i < devices.size(); i++) {
            DeviceComponent comp = (DeviceComponent)devices.elementAt(i);
            if (comp.isIn(e.getX(), e.getY())) {
                desComp.setLocation(e.getX(), e.getY());
                desComp.setText(comp.getDeviceInfo().getComment());
                desComp.setBackground(DES_COLOR_ROUTER);
                desComp.setVisible(true);
                view.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                return;
            }
        }
        for (int i = 0; i < links.size(); i++) {
            LinkComponent comp = (LinkComponent)links.elementAt(i);
            if (comp.isIn(e.getX(), e.getY())) {
                desComp.setLocation(e.getX(), e.getY());
                desComp.setText(comp.getInterfaceInfo().getInterfaceInfo().getComment());
                desComp.setBackground(DES_COLOR_LINK);
                desComp.setVisible(true);
                view.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                return;
            }
        }
        view.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
        desComp.setVisible(false);

    }

    private void exportPNGImage() {
    	File f = new File("png");
    	if (!f.exists()) {
    		f.mkdir();
    	}
        JFileChooser fc = new JFileChooser(f);
        fc.setDialogTitle("Save to PNG file");
        if (fc.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            File pngFile = fc.getSelectedFile();
            if (!pngFile.getPath().endsWith(".png")) {
            	pngFile = new File(pngFile.getPath()+".png");
            }
            exportPNGImage(pngFile);
        }
    }
   
    
    public byte[] exportPNGImageAsBytes() {
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        BufferedImage bi = new BufferedImage(getWidth(), getHeight()+25,
            BufferedImage.TYPE_INT_RGB);

        Graphics g = bi.getGraphics();
        g.setColor(Color.white);
        g.fillRect(0,0, getWidth(), getHeight()+25);
        g.setFont(Resources.DIALOG_12B);
        g.setColor(Color.black);
        g.drawString("MAP TITLE : " + mapInfo.getName() + " ( " + mapInfo.getDescr()+ " ) - " +
                mapInfo.getWidth() + " x " + mapInfo.getHeight() + " ( Update period : "+ (int)(timeOut/1000)+" seconds )", 5, 16);

        g.translate(0, 25);
        
        /*  test */
    	if (mapInfo !=null && mapInfo.getBackground() != null && 
         		!mapInfo.getBackground().equalsIgnoreCase("")) {
            BufferedImage back = WeatherMapClient.rf.getBufferedImage(
            		Resources.MAP_DIR+mapInfo.getBackground());
    		g.drawImage(back, 0, 0, mapInfo.getWidth(), mapInfo.getHeight(), null); 
    	}
        /*
        JLabel label = new JLabel();
        label.setIcon(WeatherMapClient.rf.getIcon(mapInfo.getBackground()));
        label.setSize(new Dimension(mapInfo.getWidth(), mapInfo.getHeight()));
        label.paint(g);
        */
 
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
    void exportPNGImage(File pngFile) {
        BufferedImage bi = new BufferedImage(getWidth(), getHeight()+25,
                BufferedImage.TYPE_INT_RGB);
        Graphics g = bi.getGraphics();
        g.setColor(Color.white);
        g.fillRect(0,0, getWidth(), getHeight()+25);
        g.translate(0, 25);
        view.paint(g);
        g.setFont(Resources.DIALOG_12B);
        g.setColor(Color.black);
        g.translate(0, -25);
        g.drawString("MAP TITLE : " + mapInfo.getName() + " ( " + mapInfo.getDescr()+ " ) - " +
                mapInfo.getWidth() + " x " + mapInfo.getHeight(), 5, 16);

        try {
            ImageIO.write(bi, "png", pngFile);
        } catch ( Exception ex) {
            
        }
    }

    void exportPNGImageGraphics(File pngFile, File pngFile2) {
        BufferedImage bi = new BufferedImage(getWidth(), getHeight()+25,
                BufferedImage.TYPE_INT_RGB);

        Graphics g = bi.getGraphics();
        g.setColor(Color.white);
        g.fillRect(0,0, getWidth(), getHeight()+25);
        g.setFont(Resources.DIALOG_12B);
        g.setColor(Color.black);
        g.drawString("MAP TITLE : " + mapInfo.getName() + " ( " + mapInfo.getDescr()+ " ) - " +
                mapInfo.getWidth() + " x " + mapInfo.getHeight() + " ( Update period : "+ (int)(timeOut/1000)+" seconds )", 5, 16);

        g.translate(0, 25);
        
        /*  test */
    	if (mapInfo !=null && mapInfo.getBackground() != null && 
         		!mapInfo.getBackground().equalsIgnoreCase("")) {
            BufferedImage back = WeatherMapClient.rf.getBufferedImage(
            		Resources.MAP_DIR+mapInfo.getBackground());
    		g.drawImage(back, 0, 0, mapInfo.getWidth(), mapInfo.getHeight(), null); 
    	}
		/*
        JLabel label = new JLabel();
        label.setIcon(WeatherMapClient.rf.getIcon(Resources.MAP_DIR+Resources.MAP[2]));
        label.setSize(new Dimension(mapInfo.getWidth(), mapInfo.getHeight()));
        label.paint(g);
        */
 
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
    	
        /*  test */
    	BufferedImage bi2 = new BufferedImage(getWidth(), getHeight()+25,
                BufferedImage.TYPE_INT_RGB);
        BufferedImage bi3 = new BufferedImage(getWidth()/2, (getHeight()+25)/2,
                BufferedImage.TYPE_INT_RGB);
        
    	Graphics g2 = bi2.getGraphics();
    	g2.setColor(Color.white);
        g2.fillRect(0,0, getWidth(), getHeight()+25);
        g2.setFont(Resources.DIALOG_12B);
        g2.setColor(Color.black);
        //g2.drawString("MAP TITLE : " + mapInfo.getName() + " ( " + mapInfo.getDescr()+ " ) - " +
        //        mapInfo.getWidth() + " x " + mapInfo.getHeight() + " ( Update period : "+ (int)(timeOut/1000)+" seconds )", 5, 16);

        g2.translate(0, 25);
        /*  test */
        //g.translate(10,10); dateComp.paint(g); g.translate(-10,-10);
        //g.translate(10,30); colorBar.paint(g); g.translate(-10,-30);

        for (int i = 0; i < devices.size(); i++) {
            DeviceComponent comp = (DeviceComponent)devices.get(i);
            g2.translate(comp.getX(), comp.getY());
            comp.setIsStringVisible(false);
            comp.paint(g2);
            comp.setIsStringVisible(true);
            g2.translate(-comp.getX(), -comp.getY());
        }
    	for (int i = 0; i < links.size(); i++) {
            LinkComponent comp2 = (LinkComponent)links.get(i);
            g2.translate(comp2.getX(), comp2.getY()); 
            comp2.paint(g2);
            g2.translate(-comp2.getX(), -comp2.getY());
    	}
    	
    	Graphics g3 = bi3.getGraphics();
    	g3.drawImage(bi2, 0,0, getWidth()/2, (getHeight()+25)/2, 0, 0, getWidth(), getHeight()+25, this);
    	
        try {
        	ImageIO.write(bi, "png", pngFile);
            ImageIO.write(bi3, "png", pngFile2);
        } catch ( Exception ex) {
            
        }
    }
    
    public String exportNetworkMapInfoForJSP(double scale) {
    	StringBuffer sb = new StringBuffer();
    	for (int i = 0; i < links.size(); i++) {
            LinkComponent comp = (LinkComponent)links.get(i);
            selectedLinkInfo = comp.getInterfaceInfo();
            String h = selectedLinkInfo.getSrc().getDeviceInfo().getHost();
            String in = selectedLinkInfo.getInterfaceInfo().getIfDescr();
            
            sb.append("<AREA shape=\"poly\" coords=\"");
            sb.append(comp.toHTMLMAP(scale));
            sb.append("\" href=\"interface.jsp?back=map&host="+h+"&interface="+in+"\"");
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
            sb.append(comp.toHTMLMAP(scale));
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
    String exportNetworkMapInfo(double scale) {
    	StringBuffer sb = new StringBuffer();
    	for (int i = 0; i < links.size(); i++) {
            LinkComponent comp = (LinkComponent)links.get(i);
            selectedLinkInfo = comp.getInterfaceInfo();
            String name = selectedLinkInfo.getInterfaceInfo().getIfDescr().replaceAll("[^0-9a-zA-Z]", "_") +
    		"@" + selectedLinkInfo.getSrc().getDeviceInfo().getHost().replaceFirst(":", "_");
            
            sb.append("<AREA shape=\"poly\" coords=\"");
            sb.append(comp.toHTMLMAP(scale));
            sb.append("\" href=\"graph.php?map="+mapInfo.getName()+"&link=");
            sb.append(name+"\"");
            sb.append(" onMouseOver=\"this.style.cursor='pointer'; return overlib('");
            sb.append(name);
            sb.append("', DELAY, 250, CAPTION, '");
            sb.append(selectedLinkInfo.getInterfaceInfo().getHTMLComment());
            sb.append("');\" onMouseOut=\"return nd();\">\n");
    	}
    	for (int i = 0; i < devices.size(); i++) {
            DeviceComponent comp = (DeviceComponent)devices.get(i);
            selectedDeviceInfo = comp.getDeviceInfo();
            String name = comp.getDeviceInfo().getHost();
            sb.append("<AREA shape=\"poly\" coords=\"");
            sb.append(comp.toHTMLMAP(scale));
            sb.append("\" href=\"graph.php?map="+mapInfo.getName()+"&link=");
            sb.append(name+"\"");
            sb.append(" onMouseOver=\"this.style.cursor='pointer'; return overlib('");
            sb.append(name);
            sb.append("', DELAY, 250, CAPTION, '");
            sb.append(selectedDeviceInfo.getHTMLComment());
            sb.append("');\" onMouseOut=\"return nd();\">\n");
    	}
    	return sb.toString();
    }


    /**
     * @return Returns the timeOut.
     */
    public long getTimeOut() {
        return timeOut;
    }

    /**
     * @param timeOut The timeOut to set.
     */
    public void setTimeOut(long timeOut) {
        this.timeOut = timeOut;
    }
    
    private ImageIcon getBackgroundImage() {
    	ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    	int width = mapInfo.getWidth();
    	int height = mapInfo.getHeight();
    	try {
    		BufferedImage bi =  WeatherMapClient.rf.getBufferedImage(
    				Resources.MAP_DIR+mapInfo.getBackground());
    		BufferedImage bi2 = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
    		Graphics2D g = bi2.createGraphics();
    		g.drawImage(bi, 0, 0, width, height, null);
    		ImageIO.write(bi2, "png", outputStream);
    	} catch (Exception ex) {
    		ex.printStackTrace();
    	}
    	return new ImageIcon(outputStream.toByteArray());
    }
}
