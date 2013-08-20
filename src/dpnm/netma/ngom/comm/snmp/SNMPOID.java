/*
 * @(#)SNMPOID.java
 * 
 * Created on 2008. 02. 20
 *
 *	This software is the confidential and proprietary information of
 *	POSTECH DP&NM. ("Confidential Information"). You shall not
 *	disclose such Confidential Information and shall use it only in
 *	accordance with the terms of the license agreement you entered into
 *	with Eric Kang.
 *
 *	Contact: Eric Kang at eliot@postech.edu
 */
package dpnm.netma.ngom.comm.snmp;

public final class SNMPOID {
	private static final String[][] OIDS = {
        //  Device system information
		{"sysDescr",      		"1.3.6.1.2.1.1.1.0"       },
		{"sysObjectID",   		"1.3.6.1.2.1.1.2.0"       },
		{"sysUpTime",     		"1.3.6.1.2.1.1.3.0"       },
		{"sysContact",    		"1.3.6.1.2.1.1.4.0"       },
		{"sysName",       		"1.3.6.1.2.1.1.5.0"       },
		{"sysLocation",   		"1.3.6.1.2.1.1.6.0"       },
		{"sysServices",   		"1.3.6.1.2.1.1.7.0"       },

        {"avg5util",            "1.3.6.1.4.1.9.2.1.58.0"},
        {"prevmemory",         "1.3.6.1.4.1.9.9.48.1.1.1.5.1"},
        {"aftermemory",         "1.3.6.1.4.1.9.9.48.1.1.1.6.1"},


        //  interface information
		{"ifIndex",             "1.3.6.1.2.1.2.2.1.1"     },
		{"ifDescr",             "1.3.6.1.2.1.2.2.1.2"     },
		{"ifType",              "1.3.6.1.2.1.2.2.1.3"     },
		{"ifMtu",               "1.3.6.1.2.1.2.2.1.4"     },
		{"ifSpeed",             "1.3.6.1.2.1.2.2.1.5"     },
		{"ifPhyAddress",        "1.3.6.1.2.1.2.2.1.6"     },
		{"ifAdminStatus",       "1.3.6.1.2.1.2.2.1.7"     },
		{"ifOperStatus",        "1.3.6.1.2.1.2.2.1.8"     },
		{"ifLastChange",        "1.3.6.1.2.1.2.2.1.9"     },
		{"ifInOctets",          "1.3.6.1.2.1.2.2.1.10"    },
		{"ifInUcastPkts",       "1.3.6.1.2.1.2.2.1.11"    },
		{"ifInNUcastPkts",      "1.3.6.1.2.1.2.2.1.12"    },
		{"ifInDiscards",        "1.3.6.1.2.1.2.2.1.13"    },
		{"ifInErrors",          "1.3.6.1.2.1.2.2.1.14"    },
		{"ifInUnknownProtos",   "1.3.6.1.2.1.2.2.1.15"    },
		{"ifOutOctets",         "1.3.6.1.2.1.2.2.1.16"    },
		{"ifOutUcastPkts",      "1.3.6.1.2.1.2.2.1.17"    },
		{"ifOutNUcastPkts",     "1.3.6.1.2.1.2.2.1.18"    },
		{"ifOutDiscards",       "1.3.6.1.2.1.2.2.1.19"    },
		{"ifOutErrors",         "1.3.6.1.2.1.2.2.1.20"    },
		{"ifOutQLen",           "1.3.6.1.2.1.2.2.1.21"    },
		{"ifSpecific",          "1.3.6.1.2.1.2.2.1.22"    },

        //  private MIB
		{"ifHCInOctets",        "1.3.6.1.2.1.31.1.1.1.6" },
		{"ifHCOutOctets",       "1.3.6.1.2.1.31.1.1.1.10" },
		{"ifAlias",             "1.3.6.1.2.1.31.1.1.1.18" }
	};

    static String getNumericOid(String oid) {
    	int n = OIDS.length;
    	for(int i = 0; i < n; i++) {
    		String name = OIDS[i][0], value = OIDS[i][1];
    		if(oid.startsWith(name)) {
    			return oid.replaceFirst(name, value);
    		}
    	}
    	// probably numerical
    	return oid;
    }


}
