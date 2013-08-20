/*
 * @(#)Resource.java
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

import dpnm.netma.ngom.Conf;

import java.io.File;

/**
 * Resource class
 *
 * @author Eric Kang
 * @since 2008/02/19
 * @version $Revision: 1.1 $
 */
public final class Resource {
	// various paths
	private static final String DELIM = System.getProperty("file.separator");
	private static final String HOME_DIR = 
        (new File("data")).getAbsolutePath() + DELIM;
	private static final String CONF_DIR = HOME_DIR + "conf" + DELIM;
	private static final String RRD_DIR  = HOME_DIR + "rrd" + DELIM;
	private static final String RESOURCE_FILE = CONF_DIR + "resource.xml";
	private static final String MAP_FILE = CONF_DIR + "map.xml";
    private static final String ERROR_FILE = CONF_DIR + "error.xml";
    private static final String RRD_DEF_TEMPLATE_FILE = CONF_DIR + 
        "rrd_template.xml";
	private static final String RRD_GRAPH_DEF_TEMPLATE_FILE = CONF_DIR + 
        "graph_template.xml";

	static {
		// create directories if not found
		new File(CONF_DIR).mkdirs();
		new File(RRD_DIR).mkdirs();
	}

	static String getHomeDir() {
		return HOME_DIR;
	}

	static String getConfDir() {
		return CONF_DIR;
	}

	public static String getRrdDir() {
		return RRD_DIR;
	}

	static String getResourceFile() {
        return RESOURCE_FILE;
	}

	public static String getRrdTemplateFile() {
		return RRD_DEF_TEMPLATE_FILE;
	}

	public static String getGraphTemplateFile() {
		return RRD_GRAPH_DEF_TEMPLATE_FILE;
	}

	static String getMapFile() {
		return MAP_FILE;
	}
    
    static String getErrorFile() {
        return ERROR_FILE;
    }

	// initial template for RrdDef
	static final String RRD_TEMPLATE_STR =
		"<rrd_def>                                 \n" +
		"    <path>${path}</path>                  \n" +
		"    <step>300</step>                      \n" +
		"    <datasource>                          \n" +
		"        <name>in</name>                   \n" +
		"        <type>COUNTER</type>              \n" +
		"        <heartbeat>6000</heartbeat>       \n" +
		"        <min>U</min>                      \n" +
		"        <max>U</max>                      \n" +
		"    </datasource>                         \n" +
		"    <datasource>                          \n" +
		"        <name>out</name>                  \n" +
		"        <type>COUNTER</type>              \n" +
		"        <heartbeat>6000</heartbeat>       \n" +
		"        <min>U</min>                      \n" +
		"        <max>U</max>                      \n" +
		"    </datasource>                         \n" +
		"    <archive>                             \n" +
		"        <cf>AVERAGE</cf>                  \n" +
		"        <xff>0.5</xff>                    \n" +
		"        <steps>1</steps>                  \n" +
		"        <rows>600</rows>                  \n" +
		"    </archive>                            \n" +
		"    <archive>                             \n" +
		"        <cf>AVERAGE</cf>                  \n" +
		"        <xff>0.5</xff>                    \n" +
		"        <steps>6</steps>                  \n" +
		"        <rows>700</rows>                  \n" +
		"    </archive>                            \n" +
		"    <archive>                             \n" +
		"        <cf>AVERAGE</cf>                  \n" +
		"        <xff>0.5</xff>                    \n" +
		"        <steps>24</steps>                 \n" +
		"        <rows>775</rows>                  \n" +
		"    </archive>                            \n" +
		"    <archive>                             \n" +
		"        <cf>AVERAGE</cf>                  \n" +
		"        <xff>0.5</xff>                    \n" +
		"        <steps>288</steps>                \n" +
		"        <rows>797</rows>                  \n" +
		"    </archive>                            \n" +
		"    <archive>                             \n" +
		"        <cf>MAX</cf>                      \n" +
		"        <xff>0.5</xff>                    \n" +
		"        <steps>1</steps>                  \n" +
		"        <rows>600</rows>                  \n" +
		"    </archive>                            \n" +
		"    <archive>                             \n" +
		"        <cf>MAX</cf>                      \n" +
		"        <xff>0.5</xff>                    \n" +
		"        <steps>6</steps>                  \n" +
		"        <rows>700</rows>                  \n" +
		"    </archive>                            \n" +
		"    <archive>                             \n" +
		"        <cf>MAX</cf>                      \n" +
		"        <xff>0.5</xff>                    \n" +
		"        <steps>24</steps>                 \n" +
		"        <rows>775</rows>                  \n" +
		"    </archive>                            \n" +
		"    <archive>                             \n" +
		"        <cf>MAX</cf>                      \n" +
		"        <xff>0.5</xff>                    \n" +
		"        <steps>288</steps>                \n" +
		"        <rows>797</rows>                  \n" +
		"    </archive>                            \n" +
		"    <archive>                             \n" +
		"        <cf>MIN</cf>                      \n" +
		"        <xff>0.5</xff>                    \n" +
		"        <steps>1</steps>                  \n" +
		"        <rows>600</rows>                  \n" +
		"    </archive>                            \n" +
		"    <archive>                             \n" +
		"        <cf>MIN</cf>                      \n" +
		"        <xff>0.5</xff>                    \n" +
		"        <steps>6</steps>                  \n" +
		"        <rows>700</rows>                  \n" +
		"    </archive>                            \n" +
		"    <archive>                             \n" +
		"        <cf>MIN</cf>                      \n" +
		"        <xff>0.5</xff>                    \n" +
		"        <steps>24</steps>                 \n" +
		"        <rows>775</rows>                  \n" +
		"    </archive>                            \n" +
		"    <archive>                             \n" +
		"        <cf>MIN</cf>                      \n" +
		"        <xff>0.5</xff>                    \n" +
		"        <steps>288</steps>                \n" +
		"        <rows>797</rows>                  \n" +
		"    </archive>                            \n" +
		"</rrd_def>                                ";

	// initial RrdGraphDef template
	static final String GRAPH_TEMPLATE_STR =
		"<rrd_graph_def>                                                          \n" +
		"    <span>                                                               \n" +
		"        <start>${start}</start>                                          \n" +
		"        <end>${end}</end>                                                \n" +
     	"    </span>                                                              \n" +
     	"    <options>                                                            \n" +
		"        <anti_aliasing>off</anti_aliasing>                               \n" +
		"        <border>                                                         \n" +
		"            <color>#FFFFFF</color>                                       \n" +
		"            <width>0</width>                                             \n" +
		"        </border>                                                        \n" +
		"        <title>${interface} at ${host}</title>                           \n" +
		"        <vertical_label>transfer speed [bits/sec]</vertical_label>       \n" +
		"    </options>                                                           \n" +
		"    <datasources>                                                        \n" +
		"        <def>                                                            \n" +
		"            <name>in</name>                                              \n" +
		"            <rrd>${rrd}</rrd>                                            \n" +
		"            <source>in</source>                                          \n" +
		"            <cf>AVERAGE</cf>                                             \n" +
	    "        </def>                                                           \n" +
		"        <def>                                                            \n" +
		"            <name>out</name>                                             \n" +
		"            <rrd>${rrd}</rrd>                                            \n" +
		"            <source>out</source>                                         \n" +
		"            <cf>AVERAGE</cf>                                             \n" +
	    "        </def>                                                           \n" +
        "        <def>                                                            \n" +
		"            <name>in8</name>                                             \n" +
		"            <rpn>in,8,*</rpn>                                            \n" +
		"        </def>                                                           \n" +
        "        <def>                                                            \n" +
		"            <name>out8</name>                                            \n" +
		"            <rpn>out,8,*</rpn>                                           \n" +
		"        </def>                                                           \n" +
		"    </datasources>                                                       \n" +
		"    <graph>                                                              \n" +
		"        <area>                                                           \n" +
		"            <datasource>out8</datasource>                                \n" +
		"            <color>#00FF00</color>                                       \n" +
		"            <legend>output traffic@l</legend>                              \n" +
		"        </area>                                                          \n" +
		"        <line>                                                           \n" +
		"            <datasource>in8</datasource>                                 \n" +
		"            <color>#0000FF</color>                                       \n" +
		"            <legend>input traffic@l</legend>                             \n" +
		"        </line>                                                          \n" +
		"        <gprint>                                                         \n" +
		"            <datasource>out8</datasource>                                \n" +
		"            <cf>AVERAGE</cf>                                             \n" +
		"            <format>Average output:@7.2 @sbits/s</format>                \n" +
		"        </gprint>                                                        \n" +
		"        <gprint>                                                         \n" +
		"            <datasource>out8</datasource>                                \n" +
		"            <cf>MAX</cf>                                                 \n" +
		"            <format>Maximum output:@7.2 @Sbits/s</format>                \n" +
		"        </gprint>                                                        \n" +
		"        <gprint>                                                         \n" +
		"            <datasource>out</datasource>                                 \n" +
		"            <cf>TOTAL</cf>                                               \n" +
		"            <format>Total output:@7.2 @sbytes@l</format>                 \n" +
		"            <base>1024</base>                                            \n" +
		"        </gprint>                                                        \n" +
		"        <gprint>                                                         \n" +
		"            <datasource>in8</datasource>                                 \n" +
		"            <cf>AVERAGE</cf>                                             \n" +
		"            <format>Average input: @7.2 @sbits/s</format>                \n" +
		"        </gprint>                                                        \n" +
		"        <gprint>                                                         \n" +
		"            <datasource>in8</datasource>                                 \n" +
		"            <cf>MAX</cf>                                                 \n" +
		"            <format>Maximum input: @7.2 @Sbits/s</format>                \n" +
		"        </gprint>                                                        \n" +
		"        <gprint>                                                         \n" +
		"            <datasource>in</datasource>                                  \n" +
		"            <cf>TOTAL</cf>                                               \n" +
		"            <format>Total input :@7.2 @sbytes@l</format>                 \n" +
		"            <base>1024</base>                                            \n" +
		"        </gprint>                                                        \n" +
		"        <comment>@l</comment>                                            \n" +
		"        <comment>Description on device: ${alias}@l</comment>             \n" +
		"        <comment>[${date_start}] -- [${date_end}]</comment>              \n" +
		"    </graph>                                                             \n" +
		"</rrd_graph_def>                                                         ";
}
