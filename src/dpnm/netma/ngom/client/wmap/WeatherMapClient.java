/*
 * @(#)NGOMAppClient.java
 * 
 * Created on 2008. 02. 21
 *
 *	This software is the confidential and proprietary information of
 *	POSTECH DP&NM. ("Confidential Information"). You shall not
 *	disclose such Confidential Information and shall use it only in
 *	accordance with the terms of the license agreement you entered into
 *	with Eric Kang.
 *
 *	Contact: Eric Kang at eliot@postech.edu
 */
package dpnm.netma.ngom.client.wmap;

import dpnm.netma.ngom.frontend.DataManager;
import dpnm.netma.ngom.client.comp.HostDialog;
import dpnm.netma.ngom.client.comp.HoverButton;
import dpnm.netma.ngom.client.comp.ResourceFinder;
import dpnm.netma.ngom.client.comp.SplashWindow;
import dpnm.netma.ngom.data.NetworkMapInfo;

import javax.swing.event.*;
import javax.swing.tree.TreeModel;

import java.awt.*;
import java.awt.event.*;

import javax.swing.* ;

/**
* This is main window. This has many internal windows and uses them.
*
* @author	Eric Kang
* @version	$Revision: 1.2 $
* @since	2005/12/15
*/
public class WeatherMapClient implements ActionListener, InternalFrameListener
{
	/**
	* root frame of the GDT application
	*/
	private static JFrame _frame ;
    /**
    * The number of windows.
    */
    public static int _numWindow = 0 ;
    public static int getNumWindow() {
        return _numWindow;
    }
    public static void increaseNumWindow() {
        _numWindow++;
        if (_numWindow == 10) {
            _numWindow = 0;
        }
    }
	/**
	* DesktopPane of SWindow which manipulates all internal window.
	*/
	private JDesktopPane _sDesktopPane ;

	// Current ui
	public String currentUI = "Metal" ;

	private SplashWindow sp;

	static ResourceFinder rf = new ResourceFinder();
	
	/*	Menu */
	JMenuItem connectMenu;
	JMenuItem disconnectMenu;
	
	JButton connectBtn;
	JButton disconnectBtn;
	
	private static WeatherMapClient _instance = null;

    //  default server
    static String DEFAULT_HOST = "localhost";

	private DataManager dataManager = DataManager.getInstance();
	
	public synchronized static WeatherMapClient getInstance(String title) {
		if (_instance == null) {
			_instance = new WeatherMapClient(title);
		}
		return _instance;
	}
	
	public synchronized static WeatherMapClient getInstance() {
		return _instance;
	}

	/**
	* Initialize window.
	*
	* @param title the window title
	*/
	public WeatherMapClient(String title)
	{
		///////////////////////////////////////////////////////////////////////
		//	Splash wndow showing
		///////////////////////////////////////////////////////////////////////
		sp = new SplashWindow(rf.getIcon(Resources.SPLASH_IMG_STR));
	
//        try {
//            Thread.sleep(2000);
//        } catch (Exception ex) {
//        }
		
		// instance define
		_instance = this ;

		_frame = new JFrame(title) ;
		
		_frame.setIconImage(
                _frame.getToolkit().getImage(rf.getURL(Resources.LOGO_IMG_STR)));
		_sDesktopPane = new JDesktopPane() ;

		// setting menuBar
		_frame.setJMenuBar(createMenuBar()) ;

		JPanel contentPane = new JPanel() ;
		contentPane.setLayout(new BorderLayout()) ;
		contentPane.add(createToolBar(), BorderLayout.NORTH) ;

		int inset = 10;
		//Provide a preferred size for the split pane line by xhiloh on 07. 16.
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();

		contentPane.add(_sDesktopPane, BorderLayout.CENTER) ;

		inset = 50;
		screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		_frame.setBounds ( inset, inset, screenSize.width - inset*4, 
                screenSize.height - inset*4 );
		_frame.setContentPane(contentPane) ;

		_frame.addWindowListener(new WindowAdapter()
			{
				public void windowActivated(WindowEvent e) {
					//	dispose splash window
					sp.setVisible(false);
					sp.dispose();
				}
				public void windowClosing(WindowEvent e)
				{
					System.exit(0) ;
				}
				public void windowOpened(WindowEvent e) {
					selectNewHost();
				}
			}
		);
		
		init();
	}
	
	private void init() {
		connectBtn.setEnabled(true);
		connectMenu.setEnabled(true);
		disconnectBtn.setEnabled(false);
		disconnectMenu.setEnabled(false);
	}
	/**
	* Manipulate the event which is relative with menu.
	*
	* @param e the menu event
	*/
	public void actionPerformed(ActionEvent e)
	{
		String sMenuItemString ;
		sMenuItemString = e.getActionCommand() ;
		if(sMenuItemString.intern() == "Connect".intern()) {
			this.selectNewHost();
		}
		else if(sMenuItemString.intern() == "Disconnect".intern()) {
			this.disconnect();
		}
        else if (sMenuItemString.intern() == "Refresh".intern()) {
            try {
                DataManager.getInstance().reload();
            } catch (Exception ex) {
                
            }
        }
		else if (sMenuItemString.intern() == "ListEditor".intern()) {
			launchListEditor();
		}
		else if (sMenuItemString.intern() == "MapEditor".intern()) {
			launchMapEditor();
		}
		else if (sMenuItemString.intern() == "MapDisplayer".intern()) {
		    launchMapDisplayer();	
		} else if (sMenuItemString.intern() == "ShowTree".intern()) {
            try {
                Client client = new Client();
                client.setVisible(true);      
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
	}

	private JMenuBar createMenuBar()
	{
		JMenuBar sMenuBar ;

		sMenuBar = new JMenuBar() ;

		sMenuBar.add(buildConnectMenu()) ;
		sMenuBar.add(buildHelpMenu()) ;

		return sMenuBar ;
	}

	protected JMenu buildConnectMenu()
	{
		JMenu file = new JMenu("NWS") ;
		JMenuItem menuItem ;

        connectMenu = new JMenuItem("Connect", 
                rf.getIcon(Resources.CONNECT_IMG_STR)); 
        connectMenu.setMnemonic(KeyEvent.VK_C);
        connectMenu.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C, 
				ActionEvent.ALT_MASK));
        connectMenu.setActionCommand("Connect");
        connectMenu.addActionListener(this);
        file.add(connectMenu);

        disconnectMenu = new JMenuItem("Disconnect", 
                rf.getIcon(Resources.DISCONNECT_IMG_STR)); 
        disconnectMenu.setMnemonic(KeyEvent.VK_D);
        disconnectMenu.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_D, 
				ActionEvent.ALT_MASK));
        disconnectMenu.setActionCommand("Disconnect");
        disconnectMenu.addActionListener(this);
        file.add(disconnectMenu);
 
        file.addSeparator();

        menuItem = new JMenuItem("Show Tree", 
                rf.getIcon(Resources.TREE_IMG_STR)); 
        menuItem.setMnemonic(KeyEvent.VK_T);
		menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_T, 
				ActionEvent.ALT_MASK));
        menuItem.setActionCommand("ShowTree");
        menuItem.addActionListener(this);
        file.add(menuItem);

        file.addSeparator();

        menuItem = new JMenuItem("Create List", 
                rf.getIcon(Resources.CREATELIST_IMG_STR)); 
        menuItem.setMnemonic(KeyEvent.VK_L);
		menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_L, 
				ActionEvent.ALT_MASK));
        menuItem.setActionCommand("CreateList");
        menuItem.addActionListener(this);
        file.add(menuItem);
       
        menuItem = new JMenuItem("Create Map", 
                rf.getIcon(Resources.CREATEMAP_IMG_STR)); 
        menuItem.setMnemonic(KeyEvent.VK_M);
		menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_M, 
				ActionEvent.ALT_MASK));
        menuItem.setActionCommand("CreateMap");
        menuItem.addActionListener(this);
        file.add(menuItem);

        menuItem = new JMenuItem("Display Map", 
                rf.getIcon(Resources.DISPLAYMAP_IMG_STR)); 
        menuItem.setMnemonic(KeyEvent.VK_P);
		menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_P, 
				ActionEvent.ALT_MASK));
        menuItem.setActionCommand("DisplayMap");
        menuItem.addActionListener(this);
        file.add(menuItem);
        
        file.addSeparator();

		menuItem = new JMenuItem("Exit", rf.getIcon(
                    Resources.EXIT_IMG_STR));
		menuItem.setMnemonic(KeyEvent.VK_X);
		menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_X, 
				ActionEvent.ALT_MASK));
		menuItem.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
				    exit();
					System.exit(1);;
				}
			}
		);
        file.add(menuItem);

		return file ;
	}

	/**
	* build Help menu
	*
	* @return help menu
	*/
	protected JMenu buildHelpMenu() {
		JMenu help = new JMenu("Help");
		JMenuItem about = new JMenuItem("About POSNWS...");

		about.addActionListener(new ActionListener() {
		    public void actionPerformed(ActionEvent e) {
			showAboutBox();
		    }
		});

		help.add(about);
		return help;
	}

	/**
	* show about dialog
	*/
	public void showAboutBox() {
		JOptionPane.showMessageDialog(null, Env.ABOUT_MSG);
	}

	/**
	* Create Tool Bar
	*
	* @return toolBar
	*/
	protected JToolBar createToolBar()
	{
		JToolBar toolBar = new JToolBar() ;
		JButton button ;

		connectBtn = new HoverButton("Connect", 
                rf.getIcon(Resources.CONNECT_IMG_STR));
		connectBtn.setToolTipText("Connect NWS-Server");
		connectBtn.setActionCommand("Connect");
		connectBtn.addActionListener(this);
		toolBar.add(connectBtn);

		disconnectBtn = new HoverButton("Disconnect", 
                rf.getIcon(Resources.DISCONNECT_IMG_STR));
		disconnectBtn.setToolTipText("Disconnect NWS-Server");
		disconnectBtn.setActionCommand("Disconnect");
		disconnectBtn.addActionListener(this);
		toolBar.add(disconnectBtn);

        button = new HoverButton("Refresh Server", 
                rf.getIcon(Resources.TREE_IMG_STR));
        button.setToolTipText("Refresh Server");
        button.setActionCommand("Refresh");
        button.addActionListener(this);
        toolBar.add(button);
        
		toolBar.addSeparator();

		button = new HoverButton("List Editor", 
                rf.getIcon(Resources.CREATELIST_IMG_STR));
		button.setToolTipText("Launch List Editor");
		button.setActionCommand("ListEditor");
		button.addActionListener(this);
		toolBar.add(button);
	
		button = new HoverButton("Map Editor", 
                rf.getIcon(Resources.CREATEMAP_IMG_STR));
		button.setToolTipText("Launch Map Editor");
		button.setActionCommand("MapEditor");
		button.addActionListener(this);
		toolBar.add(button);
	
		button = new HoverButton("Map Displayer", 
                rf.getIcon(Resources.DISPLAYMAP_IMG_STR));
		button.setToolTipText("Launch Map Displayer");
		button.setActionCommand("MapDisplayer");
		button.addActionListener(this);
		toolBar.add(button);
	
		return toolBar ;
	}
	
	public void exit() {
	
	}

	private void selectNewHost() {
		try {
			HostDialog hostDialog = new HostDialog(_frame,  dataManager.getHost());
			String newHost = hostDialog.getHost();
			if(newHost != null) {
                dataManager.connect(newHost);
                dataManager.reload();
				connectBtn.setEnabled(false);
				connectMenu.setEnabled(false);
				disconnectBtn.setEnabled(true);
				disconnectMenu.setEnabled(true);
			}
		} catch (Exception e) {
			e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Server is not responding.",
                    "Error", JOptionPane.ERROR_MESSAGE);  
		}
	}
	
	private void disconnect() {
		connectBtn.setEnabled(true);
		connectMenu.setEnabled(true);
		disconnectBtn.setEnabled(false);
		disconnectMenu.setEnabled(false);
	}

	private void launchListEditor() {
		ListEditor editor = new ListEditor();
		_sDesktopPane.add(editor);
		editor.setVisible(true);
        editor.reloadDataFromHost();
	}

	private void launchMapEditor() {
		MapEditor editor = new MapEditor();
		_sDesktopPane.add(editor);
		editor.setVisible(true);
	}

	private void launchMapDisplayer() {
        Object[] message = new Object[2];
        JPanel p = new JPanel();
        GridBagLayout gridbag = new GridBagLayout();
        GridBagConstraints c = new GridBagConstraints();
        p.setLayout(gridbag);

        String[] mapNames = dataManager.getMapNames();
        JComboBox comboBox = null;
        JTextField interval = null;
        if (mapNames.length == 0) {
        	JLabel label = new JLabel("There is no map.");
            c.gridwidth = GridBagConstraints.REMAINDER;
        	gridbag.setConstraints(label,c);
        	p.add(label);
        } else {
        	comboBox = new JComboBox(mapNames);
            c.gridwidth = GridBagConstraints.REMAINDER;
        	gridbag.setConstraints(comboBox,c);
        	p.add(comboBox);
            c.weightx = 0.0;
            c.weighty = 1.0;
            JLabel label = new JLabel("Update Interval:");
            c.gridwidth = GridBagConstraints.RELATIVE;
            gridbag.setConstraints(label, c);
            p.add(label);
            interval = new JTextField(5);
            interval.setText("10");
            c.gridwidth = GridBagConstraints.REMAINDER;
            gridbag.setConstraints(interval, c);
            p.add(interval);
        }

        message[0] = "Select Network Map";
        message[1] = p;
        
        int result = 0;

        if (mapNames.length == 0) {
            JOptionPane.showMessageDialog(null, "There is no map",
                    "Error", JOptionPane.ERROR_MESSAGE);  
            return;
        } else {
	        result = JOptionPane.showOptionDialog(null, message, "Create Confererence Window",
	                JOptionPane.YES_NO_CANCEL_OPTION,
	                JOptionPane.INFORMATION_MESSAGE, null, null, null);
	        if ( result == JOptionPane.YES_OPTION ) {
	        	int selected = comboBox.getSelectedIndex();
	        	NetworkMapInfo info = dataManager.getNetworkMapInfo(selected);
	            MapDisplayer displayer = new MapDisplayer(info, 
                        Integer.parseInt(interval.getText())*1000);
	            _sDesktopPane.add(displayer);
	            displayer.setVisible(true);
	        }
        }

	}
    
    public void insertFrame(JInternalFrame frame) {
        _sDesktopPane.add(frame);
    }
    
    JFrame getFrame() {
        return _frame;
    }

	/**
	* Start application.
	*
	* @param args ignored
	*/
	public static void main(String[] args)
	{
		try {
			UIManager.setLookAndFeel(Env.DEFAULT_LOOKANDFEEL);
		} catch (Exception e) {}

        WeatherMapClient.getInstance(Env.TITLE + " " +Env.VERSION);
		_frame.setLocation(100,0);

		_frame.setVisible(true) ;
	}

	/* (non-Javadoc)
	 * @see javax.swing.event.InternalFrameListener#internalFrameActivated(javax.swing.event.InternalFrameEvent)
	 */
	public void internalFrameActivated(InternalFrameEvent e) {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see javax.swing.event.InternalFrameListener#internalFrameClosed(javax.swing.event.InternalFrameEvent)
	 */
	public void internalFrameClosed(InternalFrameEvent e) {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see javax.swing.event.InternalFrameListener#internalFrameClosing(javax.swing.event.InternalFrameEvent)
	 */
	public void internalFrameClosing(InternalFrameEvent e) {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see javax.swing.event.InternalFrameListener#internalFrameDeactivated(javax.swing.event.InternalFrameEvent)
	 */
	public void internalFrameDeactivated(InternalFrameEvent e) {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see javax.swing.event.InternalFrameListener#internalFrameDeiconified(javax.swing.event.InternalFrameEvent)
	 */
	public void internalFrameDeiconified(InternalFrameEvent e) {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see javax.swing.event.InternalFrameListener#internalFrameIconified(javax.swing.event.InternalFrameEvent)
	 */
	public void internalFrameIconified(InternalFrameEvent e) {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see javax.swing.event.InternalFrameListener#internalFrameOpened(javax.swing.event.InternalFrameEvent)
	 */
	public void internalFrameOpened(InternalFrameEvent e) {
		// TODO Auto-generated method stub
		
	}
    
    void reloadDataFromHost() {
        try {
            dataManager.reloadData();
            dataManager.reloadMap();
            reloadData();
        } catch (Exception e) {

        }
    }

    void reloadData() {
        //  이미 기존에 열려진 창이 있다면 정보들을 업데이트 해야 한다.
        try {
            JInternalFrame[] frame = _sDesktopPane.getAllFrames();
            
            for (int i = 0; i < frame.length; i++) {
                if (frame[i] instanceof MapDisplayer) {
                    MapDisplayer md = (MapDisplayer)frame[i];
                    md.setNetworkMapInfo(dataManager.getNetworkMapInfoByName(md.getNetworkMapInfo().getName()));
                }
                if (frame[i] instanceof MapEditor) {
                    MapEditor me = (MapEditor)frame[i];
                    me.setNetworkMapInfo(dataManager.getNetworkMapInfoByName(me.getNetworkMapInfo().getName()));
                }
                if (frame[i] instanceof ListEditor) {
                    ListEditor le = (ListEditor)frame[i];
                    le.reloadDataFromHost();
                }
            }
        } catch (Exception e) {

        }
    }
}
