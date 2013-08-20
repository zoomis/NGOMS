/*
 * @(#)SnmpPoller.java
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

import snmp.*;

import java.io.IOException;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.*;

public class SnmpReader {
	static final int SNMP_TIMEOUT = 10; // seconds

	// state variables
	private SNMPv1CommunicationInterface comm;

    public SnmpReader(String host, int port, String community)
		throws IOException {
		// check for port information
		String snmpHost = host;
		int snmpPort = port;
		/*
		int colonIndex = host.indexOf(":");
		if(colonIndex != -1) {
			// port specified
            snmpHost = host.substring(0, colonIndex);
			String portStr = host.substring(colonIndex + 1);
			snmpPort = Integer.parseInt(portStr);
		}
		*/
		InetAddress snmpHostAddress = InetAddress.getByName(snmpHost);
		comm = new SNMPv1CommunicationInterface(0, snmpHostAddress, 
                community, snmpPort);
		comm.setSocketTimeout(SNMP_TIMEOUT * 1000);
    }

	public synchronized String get(String oid) throws IOException {
		String numericOid = SNMPOID.getNumericOid(oid);
		try {
	    	SNMPVarBindList newVars = comm.getMIBEntry(numericOid);
		    SNMPSequence pair = (SNMPSequence)(newVars.getSNMPObjectAt(0));
			SNMPObject snmpObject = pair.getSNMPObjectAt(1);
			
			if (oid.startsWith("ifPhyAddress")) {
				return ((SNMPOctetString)snmpObject).toHexString();
			}
			return snmpObject.toString().trim();
		}
		catch(SNMPBadValueException bve) {
			return null;
		}
		catch(SNMPGetException ge) {
			return null;
		}
	}

	public synchronized String get(String oid, int index) throws IOException {
		return get(oid + "." + index);
	}

	public synchronized String[] get(String[] oids) throws IOException {
		int count = oids.length;
		String[] result = new String[count];
		for(int i = 0; i < count; i++) {
			result[i] = get(oids[i]);
		}
		return result;
	}

	public synchronized SortedMap walk(String base) throws IOException {
		SortedMap map = new TreeMap();
		String baseOid = SNMPOID.getNumericOid(base);
		String currentOid = baseOid;
		try {
			while(true) { // ugly, but it works
				SNMPVarBindList newVars = comm.getNextMIBEntry(currentOid);
				SNMPSequence pair = (SNMPSequence)(newVars.getSNMPObjectAt(0));
				currentOid = pair.getSNMPObjectAt(0).toString();
 	 	  		String value = pair.getSNMPObjectAt(1).toString().trim();
	 	 	  	if(currentOid.startsWith(baseOid)) {
		   		 	// extract interface number from oid
			    	int lastDot = currentOid.lastIndexOf(".");
					String indexStr = currentOid.substring(lastDot + 1);
					int index = Integer.parseInt(indexStr);
					// store interface description
					map.put(new Integer(index), value);
		    	}
				else {
					break;
				}
			}
		}
		catch(SNMPBadValueException bve) { }
		catch(SNMPGetException ge) { }
		return map;
	}

	public synchronized SortedMap walkIfDescr() throws IOException {
		SortedMap rawInterfacesMap = walk("ifDescr");
		SortedMap enumeratedInterfacesMap = new TreeMap();
		Collection enumeratedInterfaces = enumeratedInterfacesMap.values();
		// check for duplicate interface names
		// append integer suffix to duplicated name
		Iterator iter = rawInterfacesMap.keySet().iterator();
		while(iter.hasNext()) {
			Integer ifIndex = (Integer) iter.next();
			String ifDescr = (String) rawInterfacesMap.get(ifIndex);
			if(enumeratedInterfaces.contains(ifDescr)) {
				int ifDescrSuffix = 1;
				while(enumeratedInterfaces.contains(ifDescr + "#" + ifDescrSuffix)) {
					ifDescrSuffix++;
				}
				ifDescr += "#" + ifDescrSuffix;
			}
			enumeratedInterfacesMap.put(ifIndex, ifDescr);
		}
		return enumeratedInterfacesMap;
	}

	public int getIfIndexByIfDescr(String ifDescr) throws IOException {
		SortedMap map = walkIfDescr();
		Iterator it = map.keySet().iterator();
		while(it.hasNext()) {
			Integer ix = (Integer) it.next();
			String value = (String) map.get(ix);
		    if(value.equalsIgnoreCase(ifDescr)) {
				return ix.intValue();
		    }
		}
		return -1;
	}

	public void close() {
		if(comm != null) {
			try {
				comm.closeConnection();
				comm = null;
			}
			catch (SocketException se) {}
		}
	}

	protected void finalize() {
		close();
	}

}

