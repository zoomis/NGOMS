/*
 * @(#)Log.java
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
package dpnm.netma.ngom.util;

/**
 * Debug class
 *
 * @author Eric Kang
 * @since 2008/02/14
 * @version $Revision: $
 */
public class Log {

	public static void message(String id, String msg) {
		System.out.println("["+id+"]\t"+msg);
	}

	public static void message(String msg) {
		System.out.println(msg);
	}

}

