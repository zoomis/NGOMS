/*
 * @(#)Env.java
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

/**
 * This class is Environment configuration classes.
 *
 * @author Eric Kang
 */
class Env {
	static final boolean DEBUG = true;

	static final String TITLE = "NGOM (Next Generation Operation and Management System) - NetMA, DPNM";
	static final String VERSION = "0.1 (2008)";
	
	static final String DEFAULT_LOOKANDFEEL = 
		"swing.addon.plaf.threeD.ThreeDLookAndFeel";
	
	static final String ABOUT_MSG =
		"NGOM (Next Generation Operation and Management System\n" +
		"This presents the network status by various color\n" +
		"NGOM is maintained by Eric Kang(eliot@postech.edu)";
}
