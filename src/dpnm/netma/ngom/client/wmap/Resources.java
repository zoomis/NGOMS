/*
 * @(#)Resources.java
 * 
 * Created on 2005. 6. 2
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

import java.io.File;
import java.awt.Font;
import java.awt.Color;

/**
 * This class is for definition all constants.
 * And, resources are difined
 *
 * @author Eric Kang
 * @since 2005/12/15
 */
public class Resources {
	///////////////////////////////////////////////////////////////////////////
	// GENERAL
	///////////////////////////////////////////////////////////////////////////
    //  resource home path definition
    public static String HOME = (new File("resources")).getAbsolutePath();

	///////////////////////////////////////////////////////////////////////////
	// FONT
	///////////////////////////////////////////////////////////////////////////
	public static final Font DIALOG_10 = new Font("Dialog", Font.PLAIN, 10);
    public static final Font DIALOG_10B = new Font("Dialog", Font.BOLD, 10);
	public static final Font DIALOG_12 = new Font("Dialog", Font.PLAIN, 12);
    public static final Font DIALOG_12B = new Font("Dialog", Font.BOLD, 12);
    public static final Font DIALOG_14 = new Font("Dialog", Font.BOLD, 14);
	public static final Font ARIAL_10 = new Font("Arial", Font.BOLD, 10);
	public static final Font ARIAL_12 = new Font("Arial", Font.BOLD, 12);
	public static final Font ARIAL_14 = new Font("Arial", Font.BOLD, 14);

	///////////////////////////////////////////////////////////////////////////
	// COLOR SCHEME
	///////////////////////////////////////////////////////////////////////////
     

	///////////////////////////////////////////////////////////////////////////
	// IMAGE SCHEME
	///////////////////////////////////////////////////////////////////////////
    public static final String LOGO_IMG_STR = "logo.gif";
    public static final String SPLASH_IMG_STR = "splash.gif";
    public static final String CONNECT_IMG_STR = "connect.gif";
    public static final String DISCONNECT_IMG_STR = "disconnect.gif";
    public static final String CREATELIST_IMG_STR = "list.gif";
    public static final String CREATEMAP_IMG_STR = "create.gif";
    public static final String DISPLAYMAP_IMG_STR = "display.gif";
    public static final String TREE_IMG_STR = "tree.gif";
    public static final String DEVICE_IMG_STR = "deviceicon6.gif";
    public static final String LINK_IMG_STR = "link.gif";
    public static final String NEW_IMG_STR = "new.gif";
    public static final String OPEN_IMG_STR = "open.gif";
    public static final String SAVE_IMG_STR = "save.gif";
    public static final String EXPORT_IMG_STR = "export.gif";
    public static final String EXIT_IMG_STR = "exit.gif";
    
    public static final String MAP_DIR = "maps"+File.separator;
    //	MAP
    public static final String MAP[] = {
    	"india_map_02.jpg",
    	"usa_map_02.jpg",
    	"world_map_2000.jpg"
    };
    
    public static final String[] DEVICE_ICONS = {"deviceicon1.gif", 
    	"deviceicon2.gif", "deviceicon3.gif", "deviceicon4.gif", "deviceicon5.gif",
    	"deviceicon6.gif"};
  
}
