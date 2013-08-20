/*
 * @(#)MapEditor.java
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
import dpnm.netma.ngom.frontend.DataManager;
import dpnm.netma.ngom.client.comp.ColorBar;
import dpnm.netma.ngom.client.comp.DateComponent;
import dpnm.netma.ngom.client.comp.DeviceComponent;
import dpnm.netma.ngom.client.comp.HoverButton;
import dpnm.netma.ngom.client.comp.LinkComponent;
import dpnm.netma.ngom.client.comp.StatusBar;
import dpnm.netma.ngom.client.comp.Util;
import dpnm.netma.ngom.client.wmap.MapDisplayer.MyTimerTask;
import dpnm.netma.ngom.data.*;
import dpnm.netma.ngom.util.Logger;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.Iterator;
import java.util.Vector;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.KeyStroke;


/**
 * Map Editor Class
 * This class is based on Model-View-Controller(MVC) pattern
 *
 * @author Eric Kang
 */
public class MapEditor extends JInternalFrame implements ActionListener, 
    MouseListener, MouseMotionListener {

    //  DEFINE state
    private static final int NONE = -1;
    private static final int SELECT = 0x00;
    private static final int CREATE_ROUTER = 0x01;
    private static final int MODIFY_ROUTER = 0x02;
    private static final int CREATE_LINK = 0x03;
    private static final int MODIFY_LINK = 0x04;
    private static final int MOVE_ROUTER = 0x05;
    
    private int state = NONE;
	/**
	* The offset pixels of the internal windows.
	*/
	private static final int _windowOffset = 30 ;
    
    DataManager dataManager = null;
    NetworkMapInfo mapInfo = null;
    JPanel view;
    JScrollPane viewPane;
    ColorBar colorBar = new ColorBar();
    DateComponent dateComp = new DateComponent();
    
    JLabel backgroundLabel = new JLabel();
    
    Vector routers;
    Vector links;
    private Image deviceImg[] = new Image[6];
    
    Component selectedComp = null;
    LineDrawer line = new LineDrawer();
    
    //Link Popup menu
    JPopupMenu popupMenu = new JPopupMenu();
    JMenuItem editMenu = new JMenuItem("Edit Information");
    JMenuItem deleteMenu = new JMenuItem("Delete...");
	// This model is based on MVC structure

	StatusBar statusBar = new StatusBar();
	int moving_x = 0;
    int moving_y = 0;
    int original_x = 0;
    int original_y = 0;
    int comp_x = 0;
    int comp_y = 0;
    DeviceComponent moving_comp = null;
    
    public MapEditor() {
        this("NoName", true, true, true, true, 640, 480);
    }

    public MapEditor(String title, int width, int height) {
        this(title, true, true, true, true, width, height);
    }
 
    /**
     * Constructor
     */
    public MapEditor(String title, boolean resizable, boolean closable, boolean maximizable, boolean iconifiable, 
    		int width, int height) {
        super(title, resizable, closable, maximizable, iconifiable);
        
        setSize(width, height);
        dataManager = DataManager.getInstance();
        routers = new Vector();
        links = new Vector();
        
        for (int i = 0; i < 6; i++) {
            deviceImg[i] = WeatherMapClient.rf.getImage(Resources.DEVICE_ICONS[i]);
        }
        
        //  decide location of internal frame
        setLocation(WeatherMapClient.getNumWindow() * _windowOffset, WeatherMapClient.getNumWindow() * _windowOffset) ;
        WeatherMapClient.increaseNumWindow();
        
		
		createUI();
        
		setState(NONE);
    }

    
    private void createUI() {
        JPanel contentPane = new JPanel();
		setJMenuBar(createMenuBar()) ;
        contentPane.setLayout(new BorderLayout());
        contentPane.add(createToolBar(), BorderLayout.NORTH);
        view = new JPanel();
        view.setBackground(Color.gray);
        view.setLayout(null);
//		viewPane = new JScrollPane(view, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
//				JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);

//        dateComp.update();
//        dateComp.setLocation(10,10);
//        view.add(dateComp);
//        colorBar.setLocation(10,30);
//        view.add(colorBar);

        JScrollPane pane = new JScrollPane(view, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, 
                JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED) ;
//        contentPane.add(viewPane, BorderLayout.CENTER);
        contentPane.add(pane, BorderLayout.CENTER);

        contentPane.add(statusBar, BorderLayout.SOUTH);

        setContentPane(contentPane);
        
        view.addMouseListener(this);
        view.addMouseMotionListener(this);
        
        editMenu.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) { 
                editInfo();
            }
        });
        popupMenu.add(editMenu);
        deleteMenu.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) { delete(); }
        });
        popupMenu.add(deleteMenu);
    }
    
    private void resetView() {
        view.removeAll();
        view.setBackground(Color.white);
        routers.removeAllElements();
        links.removeAllElements();
        mapInfo = null;
        line.setVisible(false);
        line.setBounds(0,0,view.getWidth(),view.getHeight());
        view.add(line,0);
        dateComp.update();
        dateComp.setLocation(10,10);
        view.add(dateComp,1);
        colorBar.setLocation(10,30);
        view.add(colorBar,2);
        backgroundLabel.setLocation(0,0);

        view.add(backgroundLabel,3); 
    }
    
    void setNetworkMapInfo(NetworkMapInfo info) {
        resetView();
        this.mapInfo = info;
        view.setPreferredSize(new Dimension(info.getWidth(), info.getHeight()));
        
//        viewPane.setSize(info.getWidth(), info.getHeight());
        loadNetworkMap();
        repaint();
    }
    NetworkMapInfo getNetworkMapInfo() {
        return mapInfo;
    }
    
	private JMenuBar createMenuBar()
	{
		JMenuBar sMenuBar ;

		sMenuBar = new JMenuBar();
		sMenuBar.setOpaque(true);
		
		sMenuBar.add(buildFileMenu()) ;

		return sMenuBar ;
	}

	protected JMenu buildFileMenu()
	{
		JMenu file = new JMenu("File") ;
		JMenuItem menuItem ;

        menuItem = new JMenuItem("New", 
                WeatherMapClient.rf.getIcon(Resources.NEW_IMG_STR)); 
        menuItem.setMnemonic(KeyEvent.VK_N);
        menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, 
				ActionEvent.ALT_MASK));
        menuItem.addActionListener(this);
        file.add(menuItem);

        menuItem = new JMenuItem("Open", 
                WeatherMapClient.rf.getIcon(Resources.OPEN_IMG_STR)); 
        menuItem.setMnemonic(KeyEvent.VK_O);
        menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, 
				ActionEvent.ALT_MASK));
        menuItem.addActionListener(this);
        file.add(menuItem);
        
        menuItem = new JMenuItem("Delete", 
                WeatherMapClient.rf.getIcon(Resources.DISCONNECT_IMG_STR)); 
        menuItem.setMnemonic(KeyEvent.VK_D);
        menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_D, 
                ActionEvent.ALT_MASK));
        menuItem.setActionCommand("DeleteMap");
        menuItem.addActionListener(this);
        file.add(menuItem);
        file.addSeparator();
        
        menuItem = new JMenuItem("Preference", 
                WeatherMapClient.rf.getIcon(Resources.CREATEMAP_IMG_STR)); 
        menuItem.setMnemonic(KeyEvent.VK_P);
        menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_P, 
                ActionEvent.ALT_MASK));
        menuItem.addActionListener(this);
        file.add(menuItem);
 
        menuItem = new JMenuItem("Background", 
                WeatherMapClient.rf.getIcon(Resources.CREATEMAP_IMG_STR)); 
        menuItem.setMnemonic(KeyEvent.VK_B);
        menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_B, 
                ActionEvent.ALT_MASK));
        menuItem.addActionListener(this);
        file.add(menuItem);
        
//        menuItem = new JMenuItem("Save", 
//                NWSClient.rf.getIcon(Resources.SAVE_IMG_STR)); 
//        menuItem.setMnemonic(KeyEvent.VK_S);
//		menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, 
//				ActionEvent.ALT_MASK));
//        menuItem.addActionListener(this);
//        file.add(menuItem);

        file.addSeparator();
        
        menuItem = new JMenuItem("Close", 
                WeatherMapClient.rf.getIcon(Resources.EXIT_IMG_STR)); 
        menuItem.setMnemonic(KeyEvent.VK_X);
		menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_X, 
				ActionEvent.ALT_MASK));
        menuItem.addActionListener(this);
        file.add(menuItem);

		return file ;
	}

    private JToolBar createToolBar() {
        JToolBar toolBar = new JToolBar(JToolBar.HORIZONTAL);
        toolBar.setFloatable(false);
		JButton button = new HoverButton(WeatherMapClient.rf.getIcon(Resources.DEVICE_IMG_STR));
		button.setToolTipText("Create Device");
		button.setActionCommand("Device");
		button.addActionListener(this);
		toolBar.add(button);
		
		button = new HoverButton(WeatherMapClient.rf.getIcon(Resources.LINK_IMG_STR));
		button.setToolTipText("Create Link");
		button.setActionCommand("Link");
		button.addActionListener(this);
		toolBar.add(button);

        return toolBar;
    } 
   
   
    /**
     * exit map editor
     */
    public void exit() {
        
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
    /* (non-Javadoc)
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    public void actionPerformed(ActionEvent e) {
		String sMenuItemString ;
		sMenuItemString = e.getActionCommand() ;
		if(sMenuItemString.intern() == "New".intern()) {
		    Object[] message = new Object[2];
		    JPanel p = new JPanel();
	        GridBagLayout gridbag = new GridBagLayout();
	        GridBagConstraints c = new GridBagConstraints();
		    p.setLayout(gridbag);

	        JLabel label = new JLabel("Name");
	        c.gridwidth = GridBagConstraints.RELATIVE;
	        gridbag.setConstraints(label,c);
	        p.add(label);
	            
	        JTextField title = new JTextField(30);
	        title.setForeground(Color.blue);
	        c.gridwidth = GridBagConstraints.REMAINDER;
	        gridbag.setConstraints(title,c);

	        p.add(title);

	        label = new JLabel("Description");
	        c.gridwidth = GridBagConstraints.RELATIVE;
	        gridbag.setConstraints(label,c);
	        p.add(label);
	            
	        JTextField description = new JTextField(30);
	        description.setForeground(Color.blue);
	        c.gridwidth = GridBagConstraints.REMAINDER;
	        gridbag.setConstraints(description,c);

	        p.add(description);
	        
	        label = new JLabel("Width");
	        c.gridwidth = GridBagConstraints.RELATIVE;
	        gridbag.setConstraints(label,c);
	        p.add(label);
	            
	        JTextField width = new JTextField(10);
	        width.setText("640");
	        width.setForeground(Color.blue);
	        c.gridwidth = GridBagConstraints.REMAINDER;
	        gridbag.setConstraints(width,c);

	        p.add(width);
	        
	        label = new JLabel("Height");
	        c.gridwidth = GridBagConstraints.RELATIVE;
	        gridbag.setConstraints(label,c);
	        p.add(label);
	            
	        JTextField height = new JTextField(10);
	        height.setText("480");
	        height.setForeground(Color.blue);
	        c.gridwidth = GridBagConstraints.REMAINDER;
	        gridbag.setConstraints(height,c);

	        p.add(height);
	        

	        
		    message[0] = "Create Network Map";
		    message[1] = p;
		    
		    int result;

			result = JOptionPane.showOptionDialog(null, message, "Create Network Map",
					JOptionPane.YES_NO_CANCEL_OPTION,
					JOptionPane.INFORMATION_MESSAGE, null, null, null);

			if ( result == JOptionPane.YES_OPTION ) {
	            if (title.getText() == null || title.getText().intern() == "".intern()) {
	                JOptionPane.showMessageDialog(this, "Please, insert map name",
	                        "Error", JOptionPane.ERROR_MESSAGE);
	            	return;
	            }
	            createMap(title.getText(), description.getText(), Integer.parseInt(width.getText()),
	            		Integer.parseInt(height.getText()));
                setState(SELECT);
			}
		}
		else if(sMenuItemString.intern() == "Open".intern()) {
            Object[] message = new Object[2];
            JPanel p = new JPanel();
            GridBagLayout gridbag = new GridBagLayout();
            GridBagConstraints c = new GridBagConstraints();
            p.setLayout(gridbag);

            String[] mapNames = dataManager.getMapNames();
            JComboBox comboBox = null;
            if (mapNames.length == 0) {
                JLabel label = new JLabel("There is no map.");
                c.gridwidth = GridBagConstraints.REMAINDER;
                gridbag.setConstraints(label,c);
                p.add(label);
            } else {
                comboBox = new JComboBox(mapNames);
                c.gridwidth = GridBagConstraints.RELATIVE;
                gridbag.setConstraints(comboBox,c);
                p.add(comboBox);
            }
            message[0] = "Select Network Map";
            message[1] = p;
            
            int result = 0;

            if (mapNames.length == 0) {
                JOptionPane.showMessageDialog(null, "There is no map",
                        "Error", JOptionPane.ERROR_MESSAGE);  
                return;
            } else {
                result = JOptionPane.showOptionDialog(null, message, "Open Network Map",
                        JOptionPane.YES_NO_CANCEL_OPTION,
                        JOptionPane.INFORMATION_MESSAGE, null, null, null);
                if ( result == JOptionPane.YES_OPTION ) {
                    int selected = comboBox.getSelectedIndex();
                    resetView();
                    mapInfo = dataManager.getNetworkMapInfo(selected);
//                    System.out.println(mapInfo.getInfo());
                    setTitle(mapInfo.getName(), mapInfo.getDescr());
                    setSize(mapInfo.getWidth(), mapInfo.getHeight()+50);
                    view.setPreferredSize(
                    		new Dimension(mapInfo.getWidth(), mapInfo.getHeight()));
//                    viewPane.setSize(mapInfo.getWidth(), mapInfo.getHeight());
                    
                    if (mapInfo !=null && mapInfo.getBackground() != null && 
                    		!mapInfo.getBackground().equalsIgnoreCase("")) {
                    	backgroundLabel.setIcon(getBackgroundImage());
                        backgroundLabel.setSize(new Dimension(mapInfo.getWidth(), mapInfo.getHeight()));
                    }
                    loadNetworkMap();
                    setState(SELECT);
                }
            }

		}
		else if (sMenuItemString.intern() == "Preference".intern()) {
			if (mapInfo == null)
				return;
            Object[] message = new Object[2];
            JPanel p = new JPanel();
            GridBagLayout gridbag = new GridBagLayout();
            GridBagConstraints c = new GridBagConstraints();
            p.setLayout(gridbag);

            JLabel label = new JLabel("Name");
            c.gridwidth = GridBagConstraints.RELATIVE;
            gridbag.setConstraints(label,c);
            p.add(label);
                
            JTextField title = new JTextField(30);
            title.setText(mapInfo.getName());
            title.setForeground(Color.blue);
            c.gridwidth = GridBagConstraints.REMAINDER;
            gridbag.setConstraints(title,c);
            title.setEnabled(false);

            p.add(title);

            label = new JLabel("Description");
            c.gridwidth = GridBagConstraints.RELATIVE;
            gridbag.setConstraints(label,c);
            p.add(label);
                
            JTextField description = new JTextField(30);
            description.setText(mapInfo.getDescr());
            description.setForeground(Color.blue);
            c.gridwidth = GridBagConstraints.REMAINDER;
            gridbag.setConstraints(description,c);

            p.add(description);
            
            label = new JLabel("Width");
            c.gridwidth = GridBagConstraints.RELATIVE;
            gridbag.setConstraints(label,c);
            p.add(label);
                
            JTextField width = new JTextField(10);
            width.setText(String.valueOf(mapInfo.getWidth()));
            width.setForeground(Color.blue);
            c.gridwidth = GridBagConstraints.REMAINDER;
            gridbag.setConstraints(width,c);

            p.add(width);
            
            label = new JLabel("Height");
            c.gridwidth = GridBagConstraints.RELATIVE;
            gridbag.setConstraints(label,c);
            p.add(label);
                
            JTextField height = new JTextField(10);
            height.setText(String.valueOf(mapInfo.getHeight()));
            height.setForeground(Color.blue);
            c.gridwidth = GridBagConstraints.REMAINDER;
            gridbag.setConstraints(height,c);

            p.add(height);
            message[0] = "Modify Network Map";
            message[1] = p;
            
            int result;

            result = JOptionPane.showOptionDialog(null, message, "Create Network Map",
                    JOptionPane.YES_NO_CANCEL_OPTION,
                    JOptionPane.INFORMATION_MESSAGE, null, null, null);

            if ( result == JOptionPane.YES_OPTION ) {
                if (title.getText() == null || title.getText().intern() == "".intern()) {
                    JOptionPane.showMessageDialog(this, "Please, insert map name",
                            "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                modifyMap(title.getText(), description.getText(), Integer.parseInt(width.getText()),
                        Integer.parseInt(height.getText()));
            }
		} else if (sMenuItemString.intern() == "DeleteMap".intern()) {
            Object[] message = new Object[2];
            JPanel p = new JPanel();
            GridBagLayout gridbag = new GridBagLayout();
            GridBagConstraints c = new GridBagConstraints();
            p.setLayout(gridbag);

            String[] mapNames = dataManager.getMapNames();
            JComboBox comboBox = null;
            if (mapNames.length == 0) {
                JLabel label = new JLabel("There is no map.");
                c.gridwidth = GridBagConstraints.REMAINDER;
                gridbag.setConstraints(label,c);
                p.add(label);
            } else {
                comboBox = new JComboBox(mapNames);
                c.gridwidth = GridBagConstraints.RELATIVE;
                gridbag.setConstraints(comboBox,c);
                p.add(comboBox);
            }
            message[0] = "Select Network Map to delete";
            message[1] = p;
            
            int result = 0;

            if (mapNames.length == 0) {
                JOptionPane.showMessageDialog(null, "There is no map",
                        "Error", JOptionPane.ERROR_MESSAGE);  
                return;
            } else {
                result = JOptionPane.showOptionDialog(null, message, "Open Network Map",
                        JOptionPane.YES_NO_CANCEL_OPTION,
                        JOptionPane.INFORMATION_MESSAGE, null, null, null);
                if ( result == JOptionPane.YES_OPTION ) {
                    int selected = comboBox.getSelectedIndex();
                    
                    NetworkMapInfo mInfo = dataManager.getNetworkMapInfo(selected);
                    if (mapInfo != null &&
                            mInfo.getName().equalsIgnoreCase(mapInfo.getName())) {
                        resetView();
                    }
                    removeMap(mapInfo);
                }
            }
      
        }
		else if (sMenuItemString.intern() == "Close".intern()) {
			this.dispose();
		} 
        else if (sMenuItemString.intern() == "Device".intern()) {
            setState(CREATE_ROUTER);
            view.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        } 
        else if (sMenuItemString.intern() == "Link".intern()) {
            setState(CREATE_LINK);
            view.setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
        }
		else if (sMenuItemString.intern() == "Background".intern()) {
			if (mapInfo == null) {
				return;
			}
    		JFileChooser chooser = new JFileChooser(Resources.HOME+File.separator+Resources.MAP_DIR);
    		int ret = chooser.showOpenDialog(null);
    		if (ret == JFileChooser.APPROVE_OPTION) {
    			mapInfo.setBackground(chooser.getSelectedFile().getName());
                //  notify to create map
                try {
                    dataManager.updateMapBackground(mapInfo);
                    WeatherMapClient.getInstance().reloadData();
                } catch ( Exception ex ) {
                    ex.printStackTrace();
                }
            	backgroundLabel.setIcon(getBackgroundImage());
                backgroundLabel.setSize(new Dimension(mapInfo.getWidth(), mapInfo.getHeight()));
                
    			view.repaint();
    		}
		}
    }
    
    private void createMap(String name, String descr, int width, int height) {
    	setTitle(name,descr);
    	this.setSize(new Dimension(width, height+50));
        view.setPreferredSize(new Dimension(width, height));
//        viewPane.setSize(new Dimension(width, height));
        resetView();
        mapInfo = new NetworkMapInfo();
        mapInfo.setName(name);
        mapInfo.setDescr(descr);
        mapInfo.setWidth(width);
        mapInfo.setHeight(height);
        //  notify to create map
        try {
            dataManager.addMap(mapInfo);
        } catch ( Exception ex ) {
            
        }
    }
    
    private void modifyMap(String name, String descr, int width, int height) {
        setTitle(name,descr);
        this.setSize(new Dimension(width, height+50));
        view.setPreferredSize(new Dimension(width, height));
//        viewPane.setSize(new Dimension(width, height));

        mapInfo.setDescr(descr);
        mapInfo.setWidth(width);
        mapInfo.setHeight(height);
        //  notify to create map
        try {
            dataManager.updateMap(mapInfo);
            WeatherMapClient.getInstance().reloadData();
        } catch ( Exception ex ) {
            ex.printStackTrace();
        }
    }
    
    private void removeMap(NetworkMapInfo mapInfo) {
        setTitle("NoName");
        //  notify to create map
        try {
            if (dataManager.removeMap(mapInfo) == 0) {
                Util.info(this, mapInfo.getName() + " removed sucessfully");
            }
            
        } catch ( Exception ex ) {
            
        }
    }
    
    private void loadNetworkMap() {
        NetworkMapDeviceInfo[] info = mapInfo.getDevices();
        for (int i = 0; i < info.length; i++) {
            DeviceComponent comp = new DeviceComponentImpl(info[i].getXpos(), info[i].getYpos());
            comp.setDeviceInfo(info[i].getDeviceInfo());
            comp.setIcon(deviceImg[info[i].getIcon()], info[i].getIcon());
            comp.setDescr(info[i].getDescr());
            routers.add(comp);
            view.add(comp,3);
        }
        NetworkMapLinkInfo[] linkInfo = mapInfo.getLinks();
        for (int i = 0; i < linkInfo.length; i++) {
          LinkComponent comp2 = new LinkComponentImpl();
          comp2.setLinkInfo(linkInfo[i]);
          comp2.setLocation(0,0);
          comp2.setSize(getWidth(), getHeight());
          comp2.setEdit(true);
          links.add(comp2);
          view.add(comp2,3);
        }
        repaint();
    }
    
    public void setTitle(String name, String descr) {
        super.setTitle(name+" - " + descr);
    }

    private void editInfo() {
        if (selectedComp instanceof DeviceComponent) {
            editDevice((DeviceComponent)selectedComp);
        } else {
            editLink((LinkComponent)selectedComp);
        }
    }
    
    private void addDevice(int x, int y) {
        DeviceInfoDialog newDeviceDialog = new DeviceInfoDialog(WeatherMapClient.getInstance().getFrame(),
                dataManager.getDevices(), null, 0);
        DeviceInfo routerInfo = newDeviceDialog.getDeviceInfo();
        if(routerInfo != null) {
            int icon = newDeviceDialog.getIconNum();

            DeviceComponent comp = new DeviceComponentImpl(x, y);
            comp.setDeviceInfo(routerInfo);
            comp.setIcon(deviceImg[icon], icon);
            comp.setDescr(routerInfo.getDescr());


//            try {
//                if(dataManager.addDevice(routerInfo) == 0) {
//                } else {
//                   Debug.print("router already added");
//                   dataManager.updateDevice(routerInfo);
//                }
//            }
//            catch(Exception e) {
//                e.printStackTrace();
//            }
            NetworkMapDeviceInfo mInfo = new NetworkMapDeviceInfo();
            mInfo.setDeviceInfo(routerInfo);
            mInfo.setIcon(icon);
            mInfo.setXpos(x);
            mInfo.setYpos(y);
            mInfo.setDescr(routerInfo.getDescr());
            
            view.repaint();
            //  LINK updated
            try {
                if(dataManager.addDeviceMap(mapInfo.getName(), mInfo) == 0) {
                    Util.info(this, "Device " + routerInfo.getHost() + " added succesfully");
                    WeatherMapClient.getInstance().reloadData();
//                    view.add(comp);
//                    routers.add(comp);
                } else {
                	if (Conf.DEBUG) {
                		Logger.getInstance().logAppClient("MapEditor", 
                            "update router error");
                	}
                    
                    Util.error(this, "Device " + routerInfo.getHost() + " not added");
                }
            }
            catch(Exception e) {
                e.printStackTrace();
            }
            view.repaint();
        }
        setState(SELECT);
    }
    
    private void editDevice(DeviceComponent comp) {

        DeviceInfo routerInfo = comp.getDeviceInfo();
        routerInfo.setDescr(comp.getDescr());
        DeviceInfoDialog newDeviceDialog = new DeviceInfoDialog(WeatherMapClient.getInstance().getFrame(),
                dataManager.getDevices(), routerInfo, comp.getIcon());
        routerInfo = newDeviceDialog.getDeviceInfo();
        if(routerInfo != null) {
            comp.setIcon(deviceImg[newDeviceDialog.getIconNum()], newDeviceDialog.getIconNum());

        	//  처음에 router info를 먼저 업데이트하고 그 다음에는 map router info를 업데이트해야 한다.
//            try {
//                if(dataManager.updateDevice(routerInfo) == 0) {
//                } else {
//                   Util.error(this, "Device " + routerInfo.getHost() + " not updated");
//                }
//            }
//            catch(Exception e) {
//            }
            NetworkMapDeviceInfo mInfo = new NetworkMapDeviceInfo();
            mInfo.setDeviceInfo(routerInfo);
            mInfo.setIcon(newDeviceDialog.getIconNum());
            mInfo.setXpos(comp.getX());
            mInfo.setYpos(comp.getY());
            mInfo.setDescr(routerInfo.getDescr());
            view.repaint();
            //  LINK updated
            try {
                if(dataManager.updateDeviceMap(mapInfo.getName(), mInfo) == 0) {
                    Util.info(this, "Device " + routerInfo.getHost() + " updated succesfully");
                    WeatherMapClient.getInstance().reloadData();
                } else {
                	if (Conf.DEBUG) {
                		Logger.getInstance().logAppClient("MapEditor", 
                            "update router error");
                	}
                    Util.error(this, "Device " + routerInfo.getHost() + " not updated");
                }
            }
            catch(Exception e) {
            }
            view.repaint();
        }
    }
    
    private void addLink(DeviceComponent src, DeviceComponent dst) {

        try {
            String link[] = dataManager.getAvailableLinks(src.getDeviceInfo());
            if(link == null || link.length == 0) {
                // no links available
                Util.error(this, "No interfaces are available on this router");
                return;
            }
            String alias[] = dataManager.getAvailableLinksAlias(src.getDeviceInfo());
            
            InterfaceInfoDialog newLinkDialog = new InterfaceInfoDialog(WeatherMapClient.getInstance().getFrame(), 
                    src.getDeviceInfo(), link, alias);
            InterfaceInfo linkInfoUpdated = newLinkDialog.getInterfaceInfo();
            int kind = newLinkDialog.getKind();
            if(linkInfoUpdated != null) {
                if(dataManager.addInterface(src.getDeviceInfo(),linkInfoUpdated) == 0) {
                } else {
                    dataManager.updateInterface(src.getDeviceInfo(), 
                            linkInfoUpdated);
                }
                NetworkMapLinkInfo lInfo = new NetworkMapLinkInfo();
                lInfo.setInterfaceInfo(linkInfoUpdated);
                lInfo.setKind(kind);
                
                NetworkMapDeviceInfo[] mri = mapInfo.getDevices();
                for (int i = 0; i < mri.length; i++) {
                    if (mri[i].getDeviceInfo().getHost().equalsIgnoreCase(
                            src.getDeviceInfo().getHost())) {
                        lInfo.setSrc(mri[i]);
                    }
                    if (mri[i].getDeviceInfo().getHost().equalsIgnoreCase(
                            dst.getDeviceInfo().getHost())) {
                        lInfo.setDst(mri[i]);
                    }
                }
//                System.out.println("LinkInfo\n"+lInfo);
                view.repaint();
                //  LINK updated
                try {
                    if(dataManager.addLinkMap(mapInfo.getName(), lInfo) == 0) {
                        Util.info(this, "Interface " + linkInfoUpdated.getIfDescr() + "@" +
                                src.getDeviceInfo().getHost() + " updated successfully");
//                        LinkComponent comp2 = new LinkComponent();
//                        comp2.setLinkInfo(lInfo);
//                        comp2.setLocation(0,0);
//                        comp2.setSize(getWidth(), getHeight());
//                        comp2.setEdit(true);
//                        links.add(comp2);
//                        view.add(comp2);
                        WeatherMapClient.getInstance().reloadData();
                    } else {
                        Util.error(this, "Interface " + linkInfoUpdated.getIfDescr() + "@" +
                                src.getDeviceInfo().getHost() + " NOT updated");
                    }
                }
                catch(Exception e) {
                    e.printStackTrace();
                }
                view.repaint();
            }
            
        } catch ( Exception ex) {
            ex.printStackTrace();
        }
    }
    
    private void editLink(LinkComponent comp) {

        try {
//            String links[] = dataManager.getAvailableLinks(comp.getLinkInfo().getSrcDevice().getDeviceInfo());
//            String alias[] = dataManager.getAvailableLinksAlias(comp.getLinkInfo().getSrcDevice().getDeviceInfo());
            InterfaceInfoDialog newLinkDialog = 
            	new InterfaceInfoDialog(WeatherMapClient.getInstance().getFrame(), 
                    comp.getInterfaceInfo().getSrc().getDeviceInfo(), 
                    comp.getInterfaceInfo().getInterfaceInfo(),
                    comp.getInterfaceInfo().getKind());
            InterfaceInfo linkInfoUpdated = newLinkDialog.getInterfaceInfo();
            int kind = newLinkDialog.getKind();
            if(linkInfoUpdated != null) {
                if(dataManager.updateInterface(comp.getInterfaceInfo().getSrc().getDeviceInfo(), 
                        linkInfoUpdated) == 0) {

                }
                else {
                    Util.error(this, "Interface " + linkInfoUpdated.getIfDescr() + "@" +
                            comp.getInterfaceInfo().getSrc().getDeviceInfo().getHost() + " NOT updated");
                }
                NetworkMapLinkInfo lInfo = new NetworkMapLinkInfo();
                lInfo.setInterfaceInfo(linkInfoUpdated);
                lInfo.setKind(kind);
                lInfo.setSrc(comp.getInterfaceInfo().getSrc());
                lInfo.setDst(comp.getInterfaceInfo().getDst());
                comp.setLinkInfo(lInfo);
                view.repaint();
                //  LINK updated
                try {
                    if(dataManager.updateLinkMap(mapInfo.getName(), lInfo) == 0) {
                        Util.info(this, "Interface " + linkInfoUpdated.getIfDescr() + "@" +
                                comp.getInterfaceInfo().getSrc().getDeviceInfo().getHost() + " updated successfully");
                        WeatherMapClient.getInstance().reloadData();
                    } else {
                        Util.error(this, "Interface " + linkInfoUpdated.getIfDescr() + "@" +
                                comp.getInterfaceInfo().getSrc().getDeviceInfo().getHost() + " NOT updated");
                    }
                }
                catch(Exception e) {
                }
                view.repaint();
            }
            
        } catch ( Exception ex) {
            ex.printStackTrace();
        }
    }
    
    private void delete() {
        if (selectedComp instanceof DeviceComponent) {
            deleteDevice((DeviceComponent)selectedComp);
        } else {
            deleteLink((LinkComponent)selectedComp);
        }
    }
    
    private void deleteDevice(DeviceComponent comp) {
        DeviceInfo routerInfo = comp.getDeviceInfo();
        try {
            if(dataManager.removeDeviceMap(mapInfo.getName(), findNetworkMapDeviceInfo(routerInfo)) == 0) {
                Util.info(this, "Device " + routerInfo.getHost() + " deleted succesfully");
//                view.remove(comp);
                for (int j = 0; j < links.size(); j++) {
                    LinkComponent l = (LinkComponent)links.elementAt(j);
                    if (l.getInterfaceInfo().getSrc().getDeviceInfo().getHost().equalsIgnoreCase(
                            comp.getDeviceInfo().getHost())) {
//                        view.remove(l);
                        dataManager.removeLinkMap(mapInfo.getName(), l.getInterfaceInfo());
                    }                
                    if (l.getInterfaceInfo().getDst().getDeviceInfo().getHost().equalsIgnoreCase(
                            comp.getDeviceInfo().getHost())) {
//                        view.remove(l);
                        dataManager.removeLinkMap(mapInfo.getName(), l.getInterfaceInfo());
                    }
                }
                WeatherMapClient.getInstance().reloadData();
            }
            else {
                Util.error(this, "Device " + routerInfo.getHost() + " not deleted");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        view.repaint();
    }
    
    private void deleteLink(LinkComponent comp) {
        DeviceInfo routerInfo = comp.getInterfaceInfo().getSrc().getDeviceInfo();
        InterfaceInfo linkInfo = comp.getInterfaceInfo().getInterfaceInfo();
//        view.remove(comp);

        try {
            if(dataManager.removeLinkMap(mapInfo.getName(), comp.getInterfaceInfo()) == 0) {
                Util.info(this, "Interface " + linkInfo.getIfDescr() + "@" +
                    routerInfo.getHost() + " removed successfully");
                WeatherMapClient.getInstance().reloadData();
            }
            else {
                Util.error(this, "Interface " + linkInfo.getIfDescr() + "@" +
                    routerInfo.getHost() + " NOT removed");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        view.repaint();
    }
    public void mouseClicked(MouseEvent e) {
        // TODO Auto-generated method stub
        
    }

    public void mousePressed(MouseEvent e) {
        if (getState() == NONE) {
            return;
        }
        if (getState() != SELECT) {
            if (e.getModifiers() == MouseEvent.BUTTON1_MASK && getState() == CREATE_ROUTER) {
                addDevice(e.getX(), e.getY());
            }
            else if (e.getModifiers() == MouseEvent.BUTTON1_MASK && getState() == CREATE_LINK) {
                DeviceComponent comp = findDeviceComponent(e.getX(), e.getY());
                if (comp != null) {
                    original_x = e.getX();
                    original_y = e.getY();
                    moving_x = e.getX();
                    moving_y = e.getY();
                    moving_comp = comp;
                    line.setVisible(true);
                    line.setPoints(original_x, original_y, moving_x, moving_y);
                    comp.setSelected(true);
                } else {
                    Util.error(this, "You should select the specific router");
                    setState(SELECT);
                    moving_comp = null;
                }
            }
            else if (e.getModifiers() == MouseEvent.BUTTON3_MASK) {
                setState(SELECT);
            }
            return;
        }
        // TODO Auto-generated method stub
        if (e.getModifiers() == MouseEvent.BUTTON3_MASK) {
            for (int i = 0; i < links.size(); i++) {
                LinkComponent comp = (LinkComponent)links.elementAt(i);
                if (comp.isIn(e.getX(), e.getY())) {

                    selectedComp = comp;
                    editMenu.setText("Edit Link (" + comp.getInterfaceInfo().getInterfaceInfo().getIfDescr()+"|"+
                            comp.getInterfaceInfo().getInterfaceInfo().getIfAlias()+")");
                    deleteMenu.setText("Delete Link (" + comp.getInterfaceInfo().getInterfaceInfo().getIfDescr()+"|"+
                    comp.getInterfaceInfo().getInterfaceInfo().getIfAlias()+")");
                    popupMenu.show(view, e.getX(), e.getY()); 
                    return;
                }
            }
            for (int i = 0; i < routers.size(); i++) {
                DeviceComponent comp = (DeviceComponent)routers.elementAt(i);
                if (comp.isIn(e.getX(), e.getY())) {

                    selectedComp = comp;
                    editMenu.setText("Edit Device (" + comp.getDeviceInfo().getHost()+")");
                    deleteMenu.setText("Delete Device (" + comp.getDeviceInfo().getHost()+")");
                    popupMenu.show(view, e.getX(), e.getY()); 
                    return;
                }
            }
        } else if (e.getModifiers() == MouseEvent.BUTTON1_MASK) {
            DeviceComponent comp = findDeviceComponent(e.getX(), e.getY());
            if (comp != null) {
                original_x = e.getX();
                original_y = e.getY();
                moving_x = e.getX();
                moving_y = e.getY();
                moving_comp = comp;
                comp_x = comp.getLocation().x;
                comp_y = comp.getLocation().y;
                view.setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
                setState(MOVE_ROUTER);
            } else {
                setState(SELECT);
                moving_comp = null;
            }
        }
    }

    public void mouseReleased(MouseEvent e) {
//        System.out.println(getStateText(state));
        // TODO Auto-generated method stub
        if (getState() == MOVE_ROUTER) {
            NetworkMapDeviceInfo[] mri = mapInfo.getDevices();
            for (int i = 0; i < mri.length; i++) {
                if (mri[i].getDeviceInfo().getHost().equalsIgnoreCase(
                        moving_comp.getDeviceInfo().getHost())) {
                    mri[i].setXpos(moving_comp.getX());
                    mri[i].setYpos(moving_comp.getY());
                    for (int j = 0; j < links.size(); j++) {
                        LinkComponent comp = (LinkComponent)links.elementAt(j);
                        if (comp.getInterfaceInfo().getSrc().getDeviceInfo().getHost().equalsIgnoreCase(
                                moving_comp.getDeviceInfo().getHost())) {
                            ((LinkComponentImpl)comp).updatePoints();
                        }                
                        if (comp.getInterfaceInfo().getDst().getDeviceInfo().getHost().equalsIgnoreCase(
                                moving_comp.getDeviceInfo().getHost())) {
                            ((LinkComponentImpl)comp).updatePoints();
                        }
                    }
                    view.repaint();
                    try {
                        if(dataManager.updateDeviceMap(mapInfo.getName(), mri[i]) == 0) {
//                            Util.info(this, "Device " + mri[i].getDeviceInfo().getHost() + " updated succesfully");
                            WeatherMapClient.getInstance().reloadData();
                        } else {
                        	if (Conf.DEBUG) {
                        		Logger.getInstance().logAppClient("MapEditor", 
                                    "update router error");
                        	}
 
//                            Util.error(this, "Device " + mri[i].getDeviceInfo().getHost() + " not updated");
                        }
                    }
                    catch(Exception ex) {
                    }
                    break;
                }
            }
            //  find all link

            view.repaint();
            //  done move
            moving_comp = null;

            setState(SELECT);
        } else if (getState() == CREATE_LINK) {
            DeviceComponent comp = findDeviceComponent(e.getX(), e.getY());
            if (comp != null) {
                addLink(moving_comp, comp);
            } else {
                Util.error(this, "Target should be a router.");
            }
            line.setVisible(false);
            moving_comp = null;
            setState(SELECT);
            view.repaint();
        }
    }

    public void mouseEntered(MouseEvent e) {
        // TODO Auto-generated method stub
        
    }

    public void mouseExited(MouseEvent e) {
        // TODO Auto-generated method stub
        
    }

    public void mouseDragged(MouseEvent e) {
        // TODO Auto-generated method stub
        if (getState() == MOVE_ROUTER) {
            moving_x = e.getX();
            moving_y = e.getY();
            if (moving_comp != null) {
                moving_comp.setLocation(
                        comp_x + moving_x - original_x,
                        comp_y + moving_y - original_y);
                view.repaint();
            }
        } else if (getState() == CREATE_LINK) {
            moving_x = e.getX();
            moving_y = e.getY();
            line.setPoints(original_x, original_y, moving_x, moving_y);
            for (int i = 0; i < routers.size(); i++) {
                DeviceComponent comp = (DeviceComponent)routers.elementAt(i);
                if (comp.isIn(e.getX(), e.getY())) {
                    comp.setSelected(true);
                    view.repaint();
                    return;
                } else {
                    comp.setSelected(false);
                    view.repaint();
                }
            }
            moving_comp.setSelected(true);
            view.repaint();
        }
    }

    public void mouseMoved(MouseEvent e) {
        // TODO Auto-generated method stub
        if (getState() == SELECT) {
            for (int i = 0; i < routers.size(); i++) {
                DeviceComponent comp = (DeviceComponent)routers.elementAt(i);
                if (comp.isIn(e.getX(), e.getY())) {
                    comp.setSelected(true);
                    view.repaint();
                    return;
                } else {
                    comp.setSelected(false);
                    view.repaint();
                }
            }
            for (int i = 0; i < links.size(); i++) {
                LinkComponent comp = (LinkComponent)links.elementAt(i);
                if (comp.isIn(e.getX(), e.getY())) {
                    comp.setSelected(true);
                    view.repaint();
                    return;
                } else {
                    comp.setSelected(false);
                    view.repaint();
                }
            }
        }
    }

    private DeviceComponent findDeviceComponent(int x, int y) {
        for (int i = 0; i < routers.size(); i++) {
            DeviceComponent comp = (DeviceComponent)routers.elementAt(i);
            if (comp.isIn(x, y)) {
                return comp;
            }
        }
        return null;
    }
    
    private NetworkMapDeviceInfo findNetworkMapDeviceInfo(DeviceInfo info) {
        NetworkMapDeviceInfo mri[] = mapInfo.getDevices();
        for (int i = 0; i < mri.length; i++) {
            if (mri[i].getDeviceInfo().getHost().equalsIgnoreCase(
                    info.getHost())) {
                return mri[i];
            }
        }
        return null;
    }
    /**
     * @return Returns the state.
     */
    public int getState() {
        return state;
    }

    /**
     * @param state The state to set.
     */
    public void setState(int state) {
        this.state = state;
        statusBar.setMessage(getStateText(state));
        if (state == SELECT) {
            view.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
        }
    }
    
    private String getStateText(int state) {
        switch(state) {
        case NONE:
            return "NONE";
        case SELECT:
            return "SELECT";
        case CREATE_ROUTER:
            return "CREATE ROUTER";
        case CREATE_LINK:
            return "CREATE LINK";
        case MODIFY_ROUTER:
            return "MODIFY ROUTER";
        case MODIFY_LINK:
            return "MODIFY LINK";
        case MOVE_ROUTER:
            return "MOVE ROUTER";
        }
        return "NOT DEFINED";
    }
    
    public class LineDrawer extends Component {
        private int x1;
        private int y1;
        private int x2;
        private int y2;
        
        
        public LineDrawer() {
            
        }
        
        public void setPoints(int x1, int y1, int x2, int y2) {
            this.x1 = x1;
            this.y1 = y1;
            this.x2 = x2;
            this.y2 = y2;
        }
        public void paint(Graphics g) {
            super.paint(g);
            Graphics2D g2 = (Graphics2D)g;
            g2.setColor(Color.blue);
            g2.setStroke(new BasicStroke(2.0f, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_BEVEL, 10, 
                    new float[]{5.0f, 5.0f}, 0));
            g2.drawLine(x1, y1, x2, y2);
        }
    }
    private ImageIcon getBackgroundImage() {
    	System.out.println(Resources.MAP_DIR+mapInfo.getBackground());
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
