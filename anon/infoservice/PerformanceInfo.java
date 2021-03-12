/*
 * Decompiled with CFR 0.150.
 */
package anon.infoservice;

import anon.crypto.MultiCertPath;
import anon.crypto.SignatureVerifier;
import anon.crypto.XMLSignature;
import anon.infoservice.AbstractCertifiedDatabaseEntry;
import anon.infoservice.Database;
import anon.infoservice.PerformanceEntry;
import anon.util.IXMLEncodable;
import anon.util.Util;
import anon.util.XMLParseException;
import anon.util.XMLUtil;
import java.util.Date;
import java.util.Hashtable;
import java.util.Vector;
import logging.LogHolder;
import logging.LogType;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class PerformanceInfo
extends AbstractCertifiedDatabaseEntry
implements IXMLEncodable {
    private static final double PERFORMANCE_INFO_MIN_PERCENTAGE_OF_VALID_ENTRIES = 0.6;
    private long m_lastUpdate;
    private long m_serial;
    private String m_id;
    private Element m_xmlData;
    private XMLSignature m_signature;
    private MultiCertPath m_certPath;
    private Hashtable m_entries = new Hashtable();
    public static final String XML_ATTR_ID = "id";
    public static final String XML_ELEMENT_NAME = "PerformanceInfo";
    public static final String XML_ELEMENT_CONTAINER_NAME = "PerformanceInfoList";
    public static final int PERFORMANCE_INFO_TTL = 518400000;
    static /* synthetic */ Class class$anon$infoservice$PerformanceInfo;

    public PerformanceInfo(Element element) throws XMLParseException {
        super(System.currentTimeMillis() + 518400000L);
        if (element == null) {
            throw new XMLParseException("Could not parse PerformanceInfo. Invalid document element.");
        }
        NodeList nodeList = element.getElementsByTagName("PerformanceEntry");
        try {
            this.m_signature = SignatureVerifier.getInstance().getVerifiedXml(element, 2);
            if (this.m_signature != null) {
                this.m_certPath = this.m_signature.getMultiCertPath();
            }
        }
        catch (Exception exception) {
            LogHolder.log(3, LogType.MISC, exception);
        }
        this.m_id = XMLUtil.parseAttribute((Node)element, XML_ATTR_ID, "");
        if (!this.checkId()) {
            throw new XMLParseException("PerformanceInfo: invalid id");
        }
        for (int i = 0; i < nodeList.getLength(); ++i) {
            PerformanceEntry performanceEntry = new PerformanceEntry((Element)nodeList.item(i));
            this.m_entries.put(performanceEntry.getId(), performanceEntry);
        }
        this.m_serial = this.m_lastUpdate = XMLUtil.parseAttribute((Node)element, "lastUpdate", System.currentTimeMillis());
        this.m_xmlData = element;
    }

    public XMLSignature getSignature() {
        return this.m_signature;
    }

    public MultiCertPath getCertPath() {
        return this.m_certPath;
    }

    public boolean isVerified() {
        if (this.m_certPath != null) {
            return this.m_certPath.isVerified();
        }
        return false;
    }

    public boolean isValid() {
        if (this.m_certPath != null) {
            return this.m_certPath.isValid(new Date());
        }
        return false;
    }

    public String getId() {
        return this.m_id;
    }

    public long getLastUpdate() {
        return this.m_lastUpdate;
    }

    public long getVersionNumber() {
        return this.m_serial;
    }

    private PerformanceEntry getEntry(String string) {
        return (PerformanceEntry)this.m_entries.get(string);
    }

    public static PerformanceEntry getLowestCommonBoundEntry(String string) {
        PerformanceEntry.StabilityAttributes stabilityAttributes;
        PerformanceEntry performanceEntry = new PerformanceEntry(string, true);
        Vector<PerformanceEntry> vector = new Vector<PerformanceEntry>();
        Vector<Integer> vector2 = new Vector<Integer>();
        Vector<Integer> vector3 = new Vector<Integer>();
        Vector<Integer> vector4 = new Vector<Integer>();
        Vector<Integer> vector5 = new Vector<Integer>();
        Vector<Integer> vector6 = new Vector<Integer>();
        Vector<Integer> vector7 = new Vector<Integer>();
        Vector<Integer> vector8 = new Vector<Integer>();
        Vector<Integer> vector9 = new Vector<Integer>();
        Vector<Integer> vector10 = new Vector<Integer>();
        Vector vector11 = Database.getInstance(class$anon$infoservice$PerformanceInfo == null ? (class$anon$infoservice$PerformanceInfo = PerformanceInfo.class$("anon.infoservice.PerformanceInfo")) : class$anon$infoservice$PerformanceInfo).getEntryList();
        for (int i = 0; i < vector11.size(); ++i) {
            PerformanceEntry performanceEntry2 = ((PerformanceInfo)vector11.elementAt(i)).getEntry(string);
            if (performanceEntry2 == null) continue;
            vector.addElement(performanceEntry2);
            Integer n = new Integer(performanceEntry2.getBound(0).getBound());
            if (n != Integer.MAX_VALUE && n >= 0) {
                vector2.addElement(n);
            }
            if ((n = new Integer(performanceEntry2.getBound(0).getNotRecoveredBound())) != Integer.MAX_VALUE && n >= 0) {
                vector3.addElement(n);
            }
            if ((n = new Integer(performanceEntry2.getBound(1).getBound())) > 0) {
                vector5.addElement(n);
            }
            if ((n = new Integer(performanceEntry2.getBound(1).getNotRecoveredBound())) > 0) {
                vector6.addElement(n);
            }
            if ((n = new Integer(performanceEntry2.getBestBound(0))) != Integer.MAX_VALUE && n >= 0) {
                vector4.addElement(n);
            }
            if ((n = new Integer(performanceEntry2.getBestBound(1))) > 0) {
                vector7.addElement(n);
            }
            if ((stabilityAttributes = performanceEntry2.getStabilityAttributes()).getValueSize() <= 0) continue;
            vector8.addElement(new Integer(stabilityAttributes.getBoundErrors()));
            vector9.addElement(new Integer(stabilityAttributes.getBoundUnknown()));
            vector10.addElement(new Integer(stabilityAttributes.getBoundResets()));
        }
        vector11.removeAllElements();
        vector11 = null;
        if (vector.size() == 0) {
            performanceEntry.setBound(0, new PerformanceEntry.Bound(Integer.MAX_VALUE, Integer.MAX_VALUE));
            performanceEntry.setBestBound(0, Integer.MAX_VALUE);
            performanceEntry.setBound(1, new PerformanceEntry.Bound(0, 0));
            performanceEntry.setBestBound(1, 0);
            performanceEntry.setStabilityAttributes(new PerformanceEntry.StabilityAttributes(0, 0, 0, 0));
            return performanceEntry;
        }
        Util.sort(vector2, new Util.IntegerSortDesc());
        Util.sort(vector3, new Util.IntegerSortDesc());
        Util.sort(vector4, new Util.IntegerSortDesc());
        Util.sort(vector5, new Util.IntegerSortAsc());
        Util.sort(vector6, new Util.IntegerSortAsc());
        Util.sort(vector7, new Util.IntegerSortAsc());
        Util.sort(vector8, new Util.IntegerSortAsc());
        Util.sort(vector9, new Util.IntegerSortAsc());
        Util.sort(vector10, new Util.IntegerSortAsc());
        stabilityAttributes = new PerformanceEntry.StabilityAttributes(100, PerformanceInfo.getMajorityBoundFromSortedBounds(vector9, 0), PerformanceInfo.getMajorityBoundFromSortedBounds(vector8, 0), PerformanceInfo.getMajorityBoundFromSortedBounds(vector10, 0));
        performanceEntry.setBound(0, new PerformanceEntry.Bound(PerformanceInfo.getMajorityBoundFromSortedBounds(vector2, Integer.MAX_VALUE), PerformanceInfo.getMajorityBoundFromSortedBounds(vector3, Integer.MAX_VALUE)));
        performanceEntry.setBestBound(0, PerformanceInfo.getMajorityBoundFromSortedBounds(vector4, Integer.MAX_VALUE));
        performanceEntry.setBound(1, new PerformanceEntry.Bound(PerformanceInfo.getMajorityBoundFromSortedBounds(vector5, 0), PerformanceInfo.getMajorityBoundFromSortedBounds(vector6, 0)));
        performanceEntry.setBestBound(1, PerformanceInfo.getMajorityBoundFromSortedBounds(vector7, 0));
        performanceEntry.setStabilityAttributes(stabilityAttributes);
        return performanceEntry;
    }

    private static int getMajorityBoundFromSortedBounds(Vector vector, int n) {
        int n2 = n;
        for (int i = 0; i < vector.size(); ++i) {
            n2 = (Integer)vector.elementAt(i);
            if ((double)(i + 1) / (double)vector.size() >= 0.6) break;
        }
        return n2;
    }

    public Element toXmlElement(Document document) {
        try {
            return (Element)XMLUtil.importNode(document, this.m_xmlData, true);
        }
        catch (XMLParseException xMLParseException) {
            LogHolder.log(2, LogType.NET, "Could not store PerformanceInfo to XML element", xMLParseException);
            return null;
        }
    }

    static /* synthetic */ Class class$(String string) {
        try {
            return Class.forName(string);
        }
        catch (ClassNotFoundException classNotFoundException) {
            throw new NoClassDefFoundError(classNotFoundException.getMessage());
        }
    }
}

