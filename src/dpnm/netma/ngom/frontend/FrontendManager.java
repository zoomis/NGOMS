/*
 * @(#)DataManager.java
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
package dpnm.netma.ngom.frontend;

import dpnm.netma.ngom.NGOMException;

public class FrontendManager {
	private static FrontendManager _instance;

	static synchronized FrontendManager getInstance() {
		if(_instance == null) {
			_instance =new FrontendManager();
		}
		return _instance;
	}


    private DataManager _dataManager;

    private FrontendManager() {
        _dataManager = DataManager.getInstance();
    }

    public DataManager getDataManager() {
        return _dataManager;
    }

    public void connect(String host) throws NGOMException {
    	try {
            _dataManager.connect(host);
    	} catch (Exception ex) {
    		throw new NGOMException(ex);
    	}
    }

    public void start() throws NGOMException {
    }

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

        FrontendManager fm = FrontendManager.getInstance();
        try {
            fm.connect("localhost");
            fm.start();
        } catch (NGOMException ex) {
        	ex.printStackTrace();
        }
	}

}
