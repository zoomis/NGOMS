/*
 * @(#)NGOMException.java
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
package dpnm.netma.ngom;

/**
 * Exception class
 *
 * @author Eric Kang
 * @since 2008/02/14
 * @version $Revision: 1.1 $
 */
public class NGOMException extends Exception {

	public NGOMException(String cause) {
		super(cause);
	}

	public NGOMException(Exception e) {
		super(e);
	}
}
