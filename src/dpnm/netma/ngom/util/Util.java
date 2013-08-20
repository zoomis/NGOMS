/*
 * @(#)Util.java
 * 
 * Created on 2008. 02. 18
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
 * @since 2008/02/18
 * @version $Revision: $
 */
public class Util {
    public static long getTime() {
        return (System.currentTimeMillis()+500L)/1000L;
    }

	public static String getDoubleString(double value) {
        StringBuffer sb = new StringBuffer();
        int v = (int)value;
        sb.append(v);
        sb.append(".");
        value = value - v;
        v = (int)((value+0.005) * 100);
        sb.append(v);
        return sb.toString();
    }
	
    public static String getElapsedTime(long time) {
    	int days=0, hour=0, minute=0, second=0, ms=0;
    	ms = (int)(time - (time/100)*100);
    	time = time/100;
    	second = (int)(time - (time/60)*60);
    	time = time/60;
    	minute = (int)(time - (time/60)*60);
    	time = time/60;
    	hour = (int)(time - (time/24)*24);
    	days = (int)(time/24);
    	return String.format("%d days %02dh:%02dm:%02ds.%02dth", 
    			days, hour, minute, second, ms);
    }
}

