/*
 * @(#)NetworkMapLink.java
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
package dpnm.netma.ngom.data;

import dpnm.netma.ngom.util.Util;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.Date;
import java.util.Hashtable;

/**
 * This class is for Map Device
 *
 * @author Eric Kang
 * @since 2005/02/19
 * @version $Revision: 1.1 $
 */
public class NetworkMapLink {
    /** map link */
    public static final String INTERFACE   = "link";
    public static final String IFDESCR     = "ifDescr";
    public static final String SRC         = "source";
    public static final String DST         = "destination";
    public static final String KIND        = "kind";

	private String ifDescr = "";
    private String src = "";
    private String dst = "";
    private int kind = 0;

	public NetworkMapLink() { }

    /**
     * Constructor
     * 
     * @param ifDescr   interface description
     * @param kind      type of interface
     * @param src       source device address
     * @param dst       destination device address
     */
    public NetworkMapLink(String ifDescr, int kind, String src, String dst) {
        setIfDescr(ifDescr);
        setKind(kind);
        setSrc(src);
        setDst(dst);
    }

	public NetworkMapLink(Node linkNode) {
		NodeList nodes = linkNode.getChildNodes();
		for(int i = 0; i < nodes.getLength(); i++) {
			Node node = nodes.item(i);
			String name = node.getNodeName();
			Node firstChild = node.getFirstChild();
			String value = (firstChild != null)? firstChild.getNodeValue().trim(): null;
			if(name.intern() == IFDESCR.intern()) {
				setIfDescr(value);
			}
            else if(name.intern() == SRC.intern()) {
				setSrc(value);
			}
            else if(name.intern() == DST.intern()) {
				setDst(value);
			}
            else if(name.intern() == KIND.intern()) {
                setKind(Integer.parseInt(value));
            }
		}
	}

	public String toString() {
		return ifDescr + " ["+ src + "] -> [" + dst + "]";
	}

	/**
	 * @return the ifDescr
	 */
	public String getIfDescr() {
		return ifDescr;
	}

	/**
	 * @param ifDescr the ifDescr to set
	 */
	public void setIfDescr(String ifDescr) {
		this.ifDescr = ifDescr;
	}

	/**
	 * @return the src
	 */
	public String getSrc() {
		return src;
	}

	/**
	 * @param src the src to set
	 */
	public void setSrc(String src) {
		this.src = src;
	}

	/**
	 * @return the dst
	 */
	public String getDst() {
		return dst;
	}

	/**
	 * @param dst the dst to set
	 */
	public void setDst(String dst) {
		this.dst = dst;
	}

	/**
	 * @return the kind
	 */
	public int getKind() {
		return kind;
	}

	/**
	 * @param kind the kind to set
	 */
	public void setKind(int kind) {
		this.kind = kind;
	}

	public Hashtable getLinkInfo() {
		Hashtable link = new Hashtable();
		link.put(IFDESCR, ifDescr);
		link.put(SRC, src);
		link.put(DST, dst);
        link.put(KIND, new Integer(kind));
		return link;
	}

	void appendXml(Element mapElem) {
        Document doc = mapElem.getOwnerDocument();
		Element linkElem = doc.createElement(INTERFACE);
		mapElem.appendChild(linkElem);
		Element ifDescrElem = doc.createElement(IFDESCR);
		ifDescrElem.appendChild(doc.createTextNode(ifDescr));
		linkElem.appendChild(ifDescrElem);
        Element kindElem = doc.createElement(KIND);
        kindElem.appendChild(doc.createTextNode(String.valueOf(kind)));
        linkElem.appendChild(kindElem);
        Element srcRouterElem = doc.createElement(SRC);
		srcRouterElem.appendChild(doc.createTextNode(src));
		linkElem.appendChild(srcRouterElem);
		Element dstRouterElem = doc.createElement(DST);
		dstRouterElem.appendChild(doc.createTextNode(dst));
		linkElem.appendChild(dstRouterElem);
	}
}
