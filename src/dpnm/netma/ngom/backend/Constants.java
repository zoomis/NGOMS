/*
 * @(#)Constants.java
 * 
 * Created on 2008. 02. 19
 *
 *	This software is the confidential and proprietary information of
 *	POSTECH DP&NM. ("Confidential Information"). You shall not
 *	disclose such Confidential Information and shall use it only in
 *	accordance with the terms of the license agreement you entered into
 *	with Eric Kang.
 *
 *	Contact: Eric Kang at eliot@postech.edu
 */
package dpnm.netma.ngom.backend;

/**
 * This class is for constants
 *
 * @author Eric Kang
 * @since 2008/02/19
 * @version $Revision: 1.1 $
 */
public final class Constants {
    //  return value
    public static final int OK                               = 0x00;
    public static final int EXCEPTION                        = 0x01;

    public static final int ERROR_DEVICE_ALREADY_EXISTED     = 0x11;
    public static final int ERROR_DEVICE_NOT_FOUND           = 0x12;
    public static final int ERROR_DEVICE_NOT_REMOVED         = 0x13;
    public static final int ERROR_DEVICE_LIST_EMPTY          = 0x14;

    public static final int ERROR_INTERFACE_ALREADY_EXISTED  = 0x21;
    public static final int ERROR_INTERFACE_NOT_FOUND        = 0x22;
    public static final int ERROR_INTERFACE_NOT_REMOVED      = 0x23;
    public static final int ERROR_INTERFACE_LIST_EMPTY       = 0x24;

    public static final int ERROR_MAP_ALREADY_EXISTED        = 0x31;
    public static final int ERROR_MAP_NOT_FOUND              = 0x32;
    public static final int ERROR_MAP_NOT_REMOVED            = 0x33;
    public static final int ERROR_MAP_LIST_EMPTY             = 0x34;

    public static final int ERROR_LINK_ALREADY_EXISTED       = 0x41;
    public static final int ERROR_LINK_NOT_FOUND             = 0x42;
    public static final int ERROR_LINK_NOT_REMOVED           = 0x43;
    public static final int ERROR_LINK_LIST_EMPTY            = 0x44;
}
	
