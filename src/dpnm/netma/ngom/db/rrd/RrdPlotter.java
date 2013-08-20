/*
 * @(#)RrdPlotter.java
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
package dpnm.netma.ngom.db.rrd;

import dpnm.netma.ngom.Conf;
import dpnm.netma.ngom.NGOMException;
import dpnm.netma.ngom.backend.Resource;

import org.jrobin.core.*;
import org.jrobin.graph.RrdGraph;
import org.jrobin.graph.RrdGraphDefTemplate;
import org.jrobin.graph.RrdGraphDef;


import java.awt.*;
import java.awt.image.*;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.File;
import java.util.Date;
import javax.imageio.*;

public class RrdPlotter {

	private String ifDescr, host, alias;
	private static RrdGraphDefTemplate rrdGraphDefTemplate = null;
	public static final Font DIALOG_10 = new Font("Dialog", Font.PLAIN, 10);

	static {
		try {
			rrdGraphDefTemplate =
				new RrdGraphDefTemplate(new File(Resource.getGraphTemplateFile()));
		} catch (IOException e) {
			e.printStackTrace();
		} catch (RrdException e) {
			e.printStackTrace();
		}
	}

	public RrdPlotter(String host, String ifDescr, String alias) 
        throws NGOMException {
	
		if(rrdGraphDefTemplate == null) {
			throw new NGOMException("Could not load graph XML template");
		}
		this.host = host;
		this.ifDescr = ifDescr;
		this.alias = alias;
	}

	public byte[] getDevicePngGraphBytes(long start, long stop) throws NGOMException{
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		try {
    		BufferedImage bi = new BufferedImage(Conf.GRAPH_WIDTH, Conf.GRAPH_HEIGHT, BufferedImage.TYPE_INT_RGB);
    		RrdGraph graph = getDeviceRrdGraph(start, stop);
    		Graphics g = bi.getGraphics();
    		g.setFont(DIALOG_10);
    		graph.render(g);
    		ImageIO.write(bi, "png", outputStream);
		} catch (Exception ex) {
			ex.printStackTrace();
			throw new NGOMException(ex);
		}
		return outputStream.toByteArray();
	}
	
	public RrdGraph getDeviceRrdGraph(long start, long end) throws RrdException, IOException {
		RrdGraphDef graphDef = new RrdGraphDef();
		
		graphDef.setTimeSpan(start, end);
		graphDef.setMaxValue(100);
		graphDef.setMinValue(0);
		graphDef.setAntiAliasing(false);
		graphDef.setTitle(host);
		graphDef.setVerticalLabel("Utilization [%]");
		graphDef.datasource("cpu", RrdWriter.getRrdFilename(host), "cpu", ConsolFuns.CF_AVERAGE);
		graphDef.datasource("memory", RrdWriter.getRrdFilename(host), "memory", ConsolFuns.CF_AVERAGE);
		graphDef.area("memory", Color.yellow, "memory");
		graphDef.line("cpu", Color.red, "cpu\\l");
		
		graphDef.gprint("memory", ConsolFuns.CF_AVERAGE, "Average memory:%3.2lf %s%%");
		graphDef.gprint("memory", ConsolFuns.CF_MAX, "Maximum memory:%3.0lf %s%%");
		graphDef.gprint("memory", ConsolFuns.CF_MIN, "Minimum memory:%3.0lf %s%%\\l");
		
		graphDef.gprint("cpu", ConsolFuns.CF_AVERAGE, "Average cpu:%3.2lf %s%%");
		graphDef.gprint("cpu", ConsolFuns.CF_MAX, "Maximum cpu:%3.0lf %s%%");
		graphDef.gprint("cpu", ConsolFuns.CF_MIN, "Minimum cpu:%3.0lf %s%%\\l");
		
		graphDef.comment("\\l");
		graphDef.comment("Descripton on device: " + alias +"\\l");
		graphDef.comment("["+(new Date(start * 1000L))+"] -- ["+(new Date(end * 1000L))+"]");
		
		graphDef.setWidth(Conf.GRAPH_WIDTH-90);
		graphDef.setHeight(Conf.GRAPH_HEIGHT-138);
		
		return new RrdGraph(graphDef);
	}
		public byte[] getInterfacePngGraphBytes(long start, long stop) throws NGOMException{
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		try {
    		BufferedImage bi = new BufferedImage(Conf.GRAPH_WIDTH, Conf.GRAPH_HEIGHT, BufferedImage.TYPE_INT_RGB);
    		RrdGraph graph = getInterfaceRrdGraph(start, stop);
    		Graphics g = bi.getGraphics();
    		g.setFont(DIALOG_10);
    		graph.render(g);
    		ImageIO.write(bi, "png", outputStream);
		} catch (Exception ex) {
			ex.printStackTrace();
			throw new NGOMException(ex);
		}
		return outputStream.toByteArray();
	}
	
	public RrdGraph getInterfaceRrdGraph(long start, long end) throws RrdException, IOException {
		RrdGraphDef graphDef = new RrdGraphDef();
		
		graphDef.setTimeSpan(start, end);
		graphDef.setAntiAliasing(false);
		graphDef.setTitle(ifDescr + " at " + host);
		graphDef.setVerticalLabel("transfer speed [bits/sec]");
		graphDef.datasource("in", RrdWriter.getRrdFilename(host, ifDescr), "in", ConsolFuns.CF_AVERAGE);
		graphDef.datasource("in8", "in,8,*");
		graphDef.datasource("out", RrdWriter.getRrdFilename(host, ifDescr), "out", ConsolFuns.CF_AVERAGE);
		graphDef.datasource("out8", "out,8,*");
		/*
		graphDef.area("out8", new Color(0, 0xFF, 0), "output traffic");
		graphDef.line("in8", new Color(0, 0, 0xFF), "in traffic\\l");
		*/
		graphDef.area("in8", new Color(0, 0xFF, 0), "in traffic");
		graphDef.line("out8", new Color(0, 0, 0xFF), "out traffic\\l");
		
		graphDef.gprint("out8", ConsolFuns.CF_AVERAGE, "Average output:%7.2lf %sbits/s");
		graphDef.gprint("out8", ConsolFuns.CF_MAX, "Maximum output:%7.2lf %sbits/s");
		graphDef.gprint("out", ConsolFuns.CF_TOTAL, "Total output:%7.2lf %sbytes\\l");
		
		graphDef.gprint("in8", ConsolFuns.CF_AVERAGE, "Average input: %7.2lf %sbits/s");
		graphDef.gprint("in8", ConsolFuns.CF_MAX, "Maximum input: %7.2lf %sbits/s");
		graphDef.gprint("in", ConsolFuns.CF_TOTAL, "Total input: %7.2lf %sbytes\\l");
		
		graphDef.comment("\\l");
		graphDef.comment("Descripton on device: " + alias +"\\l");
		graphDef.comment("["+(new Date(start * 1000L))+"] -- ["+(new Date(end * 1000L))+"]");
		
		graphDef.setWidth(Conf.GRAPH_WIDTH-90);
		graphDef.setHeight(Conf.GRAPH_HEIGHT-140);
		
		return new RrdGraph(graphDef);
	}
	
	public RrdGraph getRrdGraph2(long start, long end) throws NGOMException {
		RrdGraphDef rrdGraphDef;
		// only one template parsed, many threads plotting
		synchronized(rrdGraphDefTemplate) {
			rrdGraphDefTemplate.setVariable("start", start);
			rrdGraphDefTemplate.setVariable("end", end);
			rrdGraphDefTemplate.setVariable("interface", ifDescr);
			rrdGraphDefTemplate.setVariable("host", host);
			rrdGraphDefTemplate.setVariable("rrd", RrdWriter.getRrdFilename(host, ifDescr));
			rrdGraphDefTemplate.setVariable("alias", alias);
			rrdGraphDefTemplate.setVariable("date_start", new Date(start * 1000L).toString());
			rrdGraphDefTemplate.setVariable("date_end", new Date(end * 1000L).toString());
			
			try {
				rrdGraphDef = rrdGraphDefTemplate.getRrdGraphDef();
				RrdGraph graph = new RrdGraph(rrdGraphDef); // use pool
        		return graph;
			} catch (RrdException e) {
				throw new NGOMException(e);
			} catch (IOException e) {
				throw new NGOMException(e);
			}
		}
	}
/*
	public RrdGraph getRrdGraph2(long start, long end) 
        throws NGOMException {
		RrdGraphDef rrdGraphDef;
		// only one template parsed, many threads plotting
		synchronized(rrdGraphDefTemplate) {
			rrdGraphDefTemplate.setVariable("start", start);
			rrdGraphDefTemplate.setVariable("end", end);
			rrdGraphDefTemplate.setVariable("interface", ifDescr);
			rrdGraphDefTemplate.setVariable("host", host);
			rrdGraphDefTemplate.setVariable("rrd", 
                    RrdWriter.getRrdFilename(host, ifDescr));
			rrdGraphDefTemplate.setVariable("alias", alias);
			rrdGraphDefTemplate.setVariable("date_start", new Date(start * 1000L).toString());
			rrdGraphDefTemplate.setVariable("date_end", new Date(end * 1000L).toString());
			
			try {
				rrdGraphDef = rrdGraphDefTemplate.getRrdGraphDef();
				rrdGraphDef.setDefaultFont(DIALOG_10);
				
			} catch (RrdException e) {
				throw new NGOMException(e);
			}
		}
		RrdGraph graph = new RrdGraph(rrdGraphDef, true); // use pool
		return graph;
	}
	*/
}
