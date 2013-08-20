package dpnm.netma.ngom.client.wmap;

import dpnm.netma.ngom.data.*;
import dpnm.netma.ngom.frontend.DataManager;

import java.io.*;

import java.util.Date;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

public class MapDumper {
    private static final String DEFAULT_HOST = "localhost";
//    private static final String HOME = "./";
//    private String HOME = "D:/SpaceTag/www/htdocs/map/postech/images/";
//    private String HOME = "C:/APM_Setup/htdocs/map/postech/images/";
    private String HOME = "D:/APM_Setup/htdocs/map/postech/images/";

    private DataManager dataManager = DataManager.getInstance();
    private String host = null;
    private int map = 0;
    private String mapName = null;
    private long timeInterval = 300L;

    Timer timer = null;
    SimpleDateFormat dateFormat;
    Calendar cal = null;
    MapDisplayer displayer = null;

    private Date startTime = null;
    private Date endTime = null;
    
    public MapDumper() {
        host = DEFAULT_HOST;
        timer = new Timer();
    }

    public void setHost(String host) {
        this.host = host;
    }

    public void start() {
        BufferedReader bi = null;
        try {
            System.out.print("Select Host : ");
        	bi = new BufferedReader(new InputStreamReader(System.in));
            this.host = bi.readLine();
        	System.out.print("Select Saved Folder : (Default="+HOME+"\n");
        	System.out.println("Press \"no\" if you use Default.");
        	System.out.print("Path = ");
            String h = bi.readLine();
            if (!h.equalsIgnoreCase("no")) {
            	HOME = h;
            }
        } catch (Exception ex) {
        	ex.printStackTrace();
        }

        try {
            dataManager.connect(host);
            dataManager.reload();

            System.out.println("Select Map : ");
            String[] mapNames = dataManager.getMapNames();
            if (mapNames.length == 0) {
                System.out.println("There is no map....");
            } else {
                for (int i = 0; i < mapNames.length; i++) {
                    System.out.println("["+i+"] : " +mapNames[i]);
                }
                System.out.print("Index =  ");
                bi = new BufferedReader(new InputStreamReader(System.in));
                this.map= Integer.parseInt(bi.readLine());
                mapName = mapNames[map];
                System.out.print("Time interval (sec) = ");
                this.timeInterval = Long.parseLong(bi.readLine());

	        	NetworkMapInfo info = dataManager.getNetworkMapInfo(map);
	            displayer = new MapDisplayer(info, timeInterval*1000);

	            
	            dumpMap();
	            dumpGraph();
            }
        } catch (Exception ex) {
            ex.printStackTrace();	
        }
        timer.scheduleAtFixedRate(new MyTimerTask(),0, timeInterval*1000);
    }

    private void dumpMap() {
    	File f = new File(HOME);
    	if (!f.exists()) {
    		f.mkdir();
    	}
    	f = new File(f.getAbsoluteFile()+File.separator + mapName);
    	if (!f.exists()) {
    		f.mkdir();
    	}
        dateFormat = new SimpleDateFormat("EEEEE, dd MMMMM yyyy - HH:mm:ss Z",
                Locale.US);
        cal = Calendar.getInstance();
//    	String name = f.getAbsolutePath()+File.separator+"Map_"+ dateFormat.format(cal.getTime())+".png";
    	String name = f.getAbsolutePath()+File.separator+"Map.png";
    	String name2 = f.getAbsolutePath()+File.separator+"Map2.png";

    	displayer.exportPNGImageGraphics(new File(name), new File(name2));
//        System.out.println("Map created : " + name);
        
        try {
        	FileWriter fw = new FileWriter(new File(f.getAbsoluteFile()+
        			File.separator+"map.inc"));
        	fw.write("<MAP name=\"ImageMap\">\n");
        	fw.write(displayer.exportNetworkMapInfo(1.0));
        	fw.write("</MAP>");
        	fw.close();
        	fw = new FileWriter(new File(f.getAbsoluteFile()+
        			File.separator+"update.inc"));
        	fw.write(dateFormat.format(cal.getTime()));
        	fw.close();
        	
        	FileWriter fw2 = new FileWriter(new File(f.getAbsoluteFile()+
        			File.separator+"map2.inc"));
        	fw2.write("<MAP name=\"ImageMap\">\n");
        	fw2.write(displayer.exportNetworkMapInfo(0.5));
        	fw2.write("</MAP>");
        	fw2.close();
        	fw2 = new FileWriter(new File(f.getAbsoluteFile()+
        			File.separator+"update.inc"));
        	fw2.write(dateFormat.format(cal.getTime()));
        	fw2.close();
        } catch (Exception ex) {
        	ex.printStackTrace();
        }
    }
    
    private void dumpGraph() {
    	File f = new File(HOME);
    	if (!f.exists()) {
    		f.mkdir();
    	}
    	f = new File(f.getAbsoluteFile()+File.separator + mapName);
    	if (!f.exists()) {
    		f.mkdir();
    	}
    	String name = f.getAbsolutePath()+File.separator+"Graph";
    	setDates(GraphInFrame.TYPE_DAILY);
    	String n = name + "_daily";
    	displayer.exportGraph(n, startTime, endTime);
    	setDates(GraphInFrame.TYPE_WEEKLY);
    	n = name + "_weekly";
    	displayer.exportGraph(n, startTime, endTime);
    	setDates(GraphInFrame.TYPE_MONTHLY);
    	n = name + "_monthly";
    	displayer.exportGraph(n, startTime, endTime);
    	setDates(GraphInFrame.TYPE_YEARLY);
    	n = name + "_yearly";
    	displayer.exportGraph(n, startTime, endTime);
    	setDates(GraphInFrame.TYPE_QUICK);
    	n = name + "_quick";
    	displayer.exportGraph(n, startTime, endTime);
    }
    
	private void setDates(int type) {
		GregorianCalendar start = null, end = null, gc = new GregorianCalendar();
		switch(type) {
			case GraphInFrame.TYPE_QUICK:
				start = new GregorianCalendar(gc.get(Calendar.YEAR), gc.get(Calendar.MONTH),
					gc.get(Calendar.DAY_OF_MONTH) - 1, gc.get(Calendar.HOUR_OF_DAY) + 1, 0);
				end = new GregorianCalendar(gc.get(Calendar.YEAR), gc.get(Calendar.MONTH),
					gc.get(Calendar.DAY_OF_MONTH), gc.get(Calendar.HOUR_OF_DAY) + 1, 0);
				break;
			case GraphInFrame.TYPE_DAILY:
			case GraphInFrame.TYPE_CUSTOM:
				start = new GregorianCalendar(gc.get(Calendar.YEAR),
					gc.get(Calendar.MONTH), gc.get(Calendar.DAY_OF_MONTH));
				end = new GregorianCalendar(gc.get(Calendar.YEAR),
					gc.get(Calendar.MONTH), gc.get(Calendar.DAY_OF_MONTH) + 1);
				break;
			case GraphInFrame.TYPE_WEEKLY:
				int shift = gc.get(Calendar.DAY_OF_WEEK) - 1;
				start = new GregorianCalendar(gc.get(Calendar.YEAR), gc.get(Calendar.MONTH),
					gc.get(Calendar.DAY_OF_MONTH) - shift);
				end = new GregorianCalendar(gc.get(Calendar.YEAR), gc.get(Calendar.MONTH),
					gc.get(Calendar.DAY_OF_MONTH) - shift + 7);
				break;
			case GraphInFrame.TYPE_MONTHLY:
				start = new GregorianCalendar(gc.get(Calendar.YEAR),
					gc.get(Calendar.MONTH), 1);
				end = new GregorianCalendar(gc.get(Calendar.YEAR),
					gc.get(Calendar.MONTH) + 1, 1);
				break;
			case GraphInFrame.TYPE_YEARLY:
				start = new GregorianCalendar(gc.get(Calendar.YEAR), 0, 1);
				end = new GregorianCalendar(gc.get(Calendar.YEAR) + 1, 0, 1);
				break;
		}
		startTime = start.getTime();
		endTime = end.getTime();
	}
	/**
	 * @param args
	 */
	public static void main(String[] args) {
        MapDumper md = new MapDumper();

        md.start();
	}

    public class MyTimerTask extends TimerTask {
        public void run() {
            dateFormat = new SimpleDateFormat("yyyy_MMdd_HH:mm:ss",
                    Locale.US);
            cal = Calendar.getInstance();
        	System.out.println(dateFormat.format(cal.getTime()));
        	dumpMap();
            dumpGraph();
        }
    }

}
