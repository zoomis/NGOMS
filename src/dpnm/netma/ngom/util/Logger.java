package dpnm.netma.ngom.util;

import java.io.*;
import java.util.*;
import java.text.SimpleDateFormat;

import dpnm.netma.ngom.backend.BackendManager;

public class Logger {
	private static Logger _instance;
	
	public synchronized static Logger getInstance() {
		if (_instance == null) {
			_instance = new Logger();
		}
		return _instance;
	}

	File backendLog;
    File appClientLog;
    SimpleDateFormat sdf;
	
	public Logger() {
		backendLog = new File("backend.log");
        appClientLog = new File("app.log");
//        sdf = new SimpleDateFormat("[yyyy-MM-dd HH:mm:ss.SSS]");
        sdf = new SimpleDateFormat("yyMMdd HHmmss.SSS");
		try {

    		String log = sdf.format(new Date()) + " [BACKEND LOG STARTED]";
			FileWriter writer = new FileWriter(backendLog, false);
			writer.write(log+"\r\n");
			writer.close();
    		log = sdf.format(new Date()) + " [APP LOG STARTED]";
			writer = new FileWriter(appClientLog, false);
			writer.write(log+"\r\n");
			writer.close();
		}
		catch(Exception e) { Log.message(e.toString()); }
	
	}
	
	public void logBackend(String id, String str) {
		String log = sdf.format(new Date()) + " [" + id + "] : " + str;
		try {

			FileWriter writer = new FileWriter(backendLog, true);
			writer.write(log+ "\r\n");
			writer.close();
		}
		catch(Exception e) { Log.message(e.toString()); }
		Log.message(log);
		
	}
	
	public void logAppClient(String id, String str) {
		String log = sdf.format(new Date()) + " [" + id + "] : " + str;
		try {

			FileWriter writer = new FileWriter(appClientLog, true);
			writer.write(log+ "\r\n");
			writer.close();
		}
		catch(Exception e) { Log.message(e.toString()); }
		Log.message(log);
	}
}
