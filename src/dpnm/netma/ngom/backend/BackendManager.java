/*
 * @(#)BackendManager.java
 * 
 * Created on 2005. 12. 15
 *
 *	This software is the confidential and proprietary informatioon of
 *	POSTECH DP&NM. ("Confidential Information"). You shall not
 *	disclose such Confidential Information and shall use it only in
 *	accordance with the terms of the license agreement you entered into
 *	with Eric Kang.
 *
 *	Contact: Eric Kang at eliot@postech.edu
 */
package dpnm.netma.ngom.backend;

import dpnm.netma.ngom.Conf;
import dpnm.netma.ngom.NGOMException;
import dpnm.netma.ngom.util.Logger;
import dpnm.netma.ngom.db.rrd.RrdWriter;

import java.util.Hashtable;
import java.util.Date;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.jrobin.core.RrdDb;
import org.jrobin.core.RrdDbPool;
import org.jrobin.core.RrdException;
/**
 * Backend manager monitors devices
 *
 * @author Eric Kang
 * @since 2008/02/19
 * @version $Revision: 1.1 $
 */
public final class BackendManager {
	private static BackendManager _instance;
	
    /*  Data manager class */
	private DataManager                     _dataManager;

    /*  Communication Protocol Manager class */
    private CommunicationProtocolManager    _comManager;

    //  Frontend manager communication handler
    private FrontCommHandler                _commHandler;

    //  RrdWriter
    private RrdWriter                       _rrdWriter;

    //  TaskManager
    private TaskManager                     _taskManager;

    //  start date
    private Date                            _startDate;

	private boolean active = false;
	
	private int serverPort = Conf.SERVER_PORT;

	public synchronized static BackendManager getInstance() {
		if (_instance == null) {
			_instance = new BackendManager();
		}
		return _instance;
	}

	private BackendManager() {
//		RrdDb.setLockMode(RrdDb.NO_LOCKS);
		try {
			RrdDbPool.getInstance().setCapacity(Conf.POOL_CAPACITY);
		} catch (RrdException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	
		
        try {
            initializeRrd();
            _rrdWriter = new RrdWriter();
        } catch (NGOMException ex) {
        	if (Conf.DEBUG) {
        		Logger.getInstance().logBackend("BackendManager", 
        				ex.toString());
        	}
        }
        if (Conf.DEBUG) {
    		Logger.getInstance().logBackend("BackendManager", "DataManager is started");
    	}
		_dataManager = DataManager.getInstance();
        //  create rrd writer

        if (Conf.DEBUG) {
    		Logger.getInstance().logBackend("BackendManager", 
                    "CommunicationProtocolManager is started");
    	}
        _comManager  = CommunicationProtocolManager.getInstance();
    	if (Conf.DEBUG) {
    		Logger.getInstance().logBackend("BackendManager", 
    				"Frontend Communication Handler is created.");
    	}
        _commHandler = new FrontCommHandler();
        _commHandler.setDataManager(_dataManager);


    	if (Conf.DEBUG) {
    		Logger.getInstance().logBackend("BackendManager", 
    				"TaskManager is created.");
    	}
        _taskManager = new TaskManager(_dataManager);
	}

	public synchronized void start() 
        throws NGOMException {
		if(active) {
			throw new NGOMException("Cannot start Server, already started");
		}
		_comManager.startXMLRPC(serverPort, Conf.HANDLER_NAME, _commHandler);

        _taskManager.start();

        _startDate = new Date();
        active = true;
	}

	public synchronized void stop() throws NGOMException {
		if(!active) {
			throw new NGOMException("Cannot stop Server, not started");
		}
		_comManager.stopXMLRPC();
		active = false;
	}

    public RrdWriter getRrdWriter() {
        return _rrdWriter;
    }
    
    public void setServerPort(int port) {
    	this.serverPort = port;
    }

	Hashtable getInfo() {
		Hashtable hash = new Hashtable();
		hash.put("sampleCount", String.valueOf(_rrdWriter.getSampleCount()));
		hash.put("savesCount", String.valueOf(_rrdWriter.getSavesCount()));
		hash.put("goodSavesCount", String.valueOf(_rrdWriter.getGoodSavesCount()));
		hash.put("badSavesCount",String.valueOf(_rrdWriter.getBadSavesCount()));
		hash.put("startDate", _startDate);
		hash.put("port", new Integer(serverPort));
		hash.put("poolEfficency", new Double(_rrdWriter.getPoolEfficency()));
		return hash;
	}

    private void initializeRrd() throws NGOMException {


		// set default backend factory
		try {
			RrdDb.setDefaultFactory(Conf.BACKEND_FACTORY_NAME);
		} catch (RrdException e) {
			throw new NGOMException("Inavlide backend factory (" + Conf.BACKEND_FACTORY_NAME + ")");
		}

		// create template files
		try {
			createXmlTemplateIfNecessary(Resource.getRrdTemplateFile(), Resource.RRD_TEMPLATE_STR);
			createXmlTemplateIfNecessary(Resource.getGraphTemplateFile(), Resource.GRAPH_TEMPLATE_STR);
		}
		catch(IOException ioe) {
			throw new NGOMException(ioe);
		}
    }

	private void createXmlTemplateIfNecessary(String filePath, String fileContent)
		throws IOException {
		File file = new File(filePath);
		if(!file.exists()) {
			FileWriter writer = new FileWriter(filePath, false);
			writer.write(fileContent);
			writer.flush();
			writer.close();
		}
	}

	public static void print_usage() {
		System.out.println("USAGE: BackendManager [port number (default="+Conf.SERVER_PORT+")]");
	}

	public static void main(String[] args) throws Exception {
		if (args.length == 0) {
            BackendManager.getInstance().start();
		} else if (args.length == 1) {
			BackendManager bm = BackendManager.getInstance();
			try {
				bm.setServerPort(Integer.parseInt(args[0]));
				bm.start();
			} catch (Exception ex) {
				print_usage();
				System.exit(0);
			}
			
		}
	}
}

