/*
 * Decompiled with CFR 0.150.
 */
package anon.infoservice;

import anon.crypto.IVerifyable;
import anon.crypto.MultiCertPath;
import anon.crypto.SignatureVerifier;
import anon.crypto.XMLSignature;
import anon.infoservice.AbstractDatabaseEntry;
import anon.infoservice.Constants;
import anon.infoservice.Database;
import anon.infoservice.IDistributable;
import anon.infoservice.MixCascade;
import anon.infoservice.PerformanceEntry;
import anon.util.IXMLEncodable;
import anon.util.XMLParseException;
import anon.util.XMLUtil;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import logging.LogHolder;
import logging.LogType;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public final class StatusInfo
extends AbstractDatabaseEntry
implements IDistributable,
IVerifyable,
IXMLEncodable {
    public static final String XML_ELEMENT_NAME = "MixCascadeStatus";
    public static final String XML_ELEMENT_CONTAINER_NAME = "MixCascadeStatusList";
    public static final int ANON_LEVEL_MIN = 0;
    public static final int ANON_LEVEL_MAX = 6;
    private String m_mixCascadeId;
    private long m_lastUpdate;
    private int m_nrOfActiveUsers;
    private int m_currentRisk;
    private int m_trafficSituation;
    private long m_mixedPackets;
    private int m_anonLevel;
    private String m_statusXmlData;
    private byte[] m_statusXmlDataBytes;
    private XMLSignature m_signature;
    private MultiCertPath m_certPath;
    static /* synthetic */ Class class$anon$infoservice$StatusInfo;
    static /* synthetic */ Class class$anon$infoservice$MixCascade;
    static /* synthetic */ Class class$anon$infoservice$PerformanceEntry;

    public static StatusInfo createDummyStatusInfo(String string) {
        return new StatusInfo(string, -1, -1, -1, -1L, -1);
    }

    public static String getXmlElementName() {
        return XML_ELEMENT_NAME;
    }

    public StatusInfo(Element element) throws Exception {
        this(element, -1L);
    }

    public StatusInfo(Element element, long l) throws Exception {
        super(System.currentTimeMillis() + (l <= 0L ? 480000L : l));
        if (element == null) {
            throw new XMLParseException("##__null__##");
        }
        if (!element.getNodeName().equals(XML_ELEMENT_NAME)) {
            throw new XMLParseException("##__root__##");
        }
        this.m_mixCascadeId = element.getAttribute("id");
        int n = -1;
        try {
            this.m_signature = SignatureVerifier.getInstance().getVerifiedXml(element, 1);
            if (this.m_signature != null) {
                this.m_certPath = this.m_signature.getMultiCertPath();
            }
        }
        catch (Exception exception) {
            LogHolder.log(3, LogType.CRYPTO, "Error while verifying status info certificates!", exception);
        }
        if (!this.checkId()) {
            throw new XMLParseException("##__root__##", "Malformed Status-Entry for Mix ID: " + this.m_mixCascadeId);
        }
        this.m_currentRisk = Integer.parseInt(element.getAttribute("currentRisk"));
        this.m_mixedPackets = Long.parseLong(element.getAttribute("mixedPackets"));
        this.m_nrOfActiveUsers = Integer.parseInt(element.getAttribute("nrOfActiveUsers"));
        this.m_trafficSituation = Integer.parseInt(element.getAttribute("trafficSituation"));
        this.m_lastUpdate = Long.parseLong(element.getAttribute("LastUpdate"));
        this.m_anonLevel = -1;
        if (this.getNrOfActiveUsers() >= 0 && this.getTrafficSituation() >= 0) {
            this.m_anonLevel = this.getNrOfActiveUsers() < 30 ? 0 : (this.getNrOfActiveUsers() < 90 ? 1 : (this.getNrOfActiveUsers() < 200 ? 2 : (this.getNrOfActiveUsers() < 300 ? 3 : (this.getNrOfActiveUsers() < 400 ? 4 : (this.getNrOfActiveUsers() < 500 ? 5 : 6)))));
        }
        if (XMLUtil.getStorageMode() == 2) {
            this.m_statusXmlData = null;
            this.m_statusXmlDataBytes = null;
        } else {
            this.m_statusXmlData = XMLUtil.toString(element);
            this.m_statusXmlDataBytes = this.m_statusXmlData.getBytes();
        }
    }

    private StatusInfo(String string, int n, int n2, int n3, long l, int n4) {
        super(System.currentTimeMillis() + 480000L);
        this.m_mixCascadeId = string;
        this.m_lastUpdate = System.currentTimeMillis();
        this.m_nrOfActiveUsers = n;
        this.m_currentRisk = n2;
        this.m_trafficSituation = n3;
        this.m_mixedPackets = l;
        this.m_anonLevel = n4;
        this.m_statusXmlData = XMLUtil.toString(this.generateXmlRepresentation());
        this.m_statusXmlDataBytes = this.m_statusXmlData.getBytes();
    }

    public String getId() {
        return this.m_mixCascadeId;
    }

    public long getLastUpdate() {
        return this.m_lastUpdate;
    }

    public long getVersionNumber() {
        return this.getLastUpdate();
    }

    public int getNrOfActiveUsers() {
        return this.m_nrOfActiveUsers;
    }

    public int getCurrentRisk() {
        return this.m_currentRisk;
    }

    public int getTrafficSituation() {
        return this.m_trafficSituation;
    }

    public long getMixedPackets() {
        return this.m_mixedPackets;
    }

    public int getAnonLevel() {
        return this.m_anonLevel;
    }

    public boolean isVerified() {
        if (this.m_signature != null) {
            return this.m_signature.isVerified();
        }
        return false;
    }

    public boolean isValid() {
        if (this.m_certPath != null) {
            return this.m_certPath.isValid(new Date());
        }
        return false;
    }

    public XMLSignature getSignature() {
        return this.m_signature;
    }

    public MultiCertPath getCertPath() {
        return this.m_certPath;
    }

    public boolean checkId() {
        return !SignatureVerifier.getInstance().isCheckSignatures() || this.m_signature != null && this.m_signature.getXORofSKIs().equalsIgnoreCase(this.getId());
    }

    public String getPostFile() {
        return "/feedback";
    }

    public static StatusInfo getStatusInfo(MixCascade mixCascade) {
        if (mixCascade == null) {
            return null;
        }
        StatusInfo statusInfo = (StatusInfo)Database.getInstance(class$anon$infoservice$StatusInfo == null ? (class$anon$infoservice$StatusInfo = StatusInfo.class$("anon.infoservice.StatusInfo")) : class$anon$infoservice$StatusInfo).getEntryById(mixCascade.getId());
        return statusInfo;
    }

    public byte[] getPostData() {
        return this.m_statusXmlDataBytes;
    }

    public int getPostEncoding() {
        return 0;
    }

    public String getStatusXmlData() {
        return this.m_statusXmlData;
    }

    public String getHtmlTableLine(boolean bl) {
        String string = "<TR><TD CLASS=\"name\">";
        MixCascade mixCascade = (MixCascade)Database.getInstance(class$anon$infoservice$MixCascade == null ? (class$anon$infoservice$MixCascade = StatusInfo.class$("anon.infoservice.MixCascade")) : class$anon$infoservice$MixCascade).getEntryById(this.getId());
        PerformanceEntry performanceEntry = (PerformanceEntry)Database.getInstance(class$anon$infoservice$PerformanceEntry == null ? (class$anon$infoservice$PerformanceEntry = StatusInfo.class$("anon.infoservice.PerformanceEntry")) : class$anon$infoservice$PerformanceEntry).getEntryById(this.getId());
        int n = 0;
        if (mixCascade != null) {
            string = string + mixCascade.getName();
            n = mixCascade.getMaxUsers();
        }
        String string2 = this.getTrafficSituation() < 0 ? "(n/a)" : Integer.toString(this.getTrafficSituation()) + " ";
        if (this.getTrafficSituation() > 60) {
            string2 = string2 + "(high)";
        } else if (this.getTrafficSituation() > 30) {
            string2 = string2 + "(medium)";
        } else if (this.getTrafficSituation() >= 0) {
            string2 = string2 + "(low)";
        }
        string = string + "</TD><TD CLASS=\"name\"><a href=\"" + "/cascadewebinfo/" + this.getId() + "\">" + this.getId() + "</a></TD><TD CLASS=\"status\" ALIGN=\"right\">" + (!bl ? "<a href=\"/values/users/" + this.getId() + "\">" : "") + (this.getNrOfActiveUsers() < 0 ? "(n/a)" : Integer.toString(this.getNrOfActiveUsers())) + (n > 0 ? " / " + n : "") + (!bl ? "</a>" : "") + "</TD><TD CLASS=\"status\" ALIGN=\"center\">" + string2 + "</TD><TD CLASS=\"status\" ALIGN=\"right\">" + "<a href=\"/values/delay/" + this.getId() + "\">" + (!bl ? (performanceEntry != null && System.currentTimeMillis() - performanceEntry.getLastTestTime() < 1200000L && performanceEntry.getLastTestAverage(1) != 0 ? String.valueOf(performanceEntry.getLastTestAverage(1)) : "?") + " (" + (performanceEntry != null && performanceEntry.getAverage(1) != 0 ? String.valueOf(performanceEntry.getAverage(1)) : "?") + ") " : "") + (!bl ? "[" : "");
        int n2 = performanceEntry == null ? 0 : performanceEntry.getBound(1).getBound();
        string = n2 == Integer.MAX_VALUE ? string + ">" + PerformanceEntry.BOUNDARIES[1][PerformanceEntry.BOUNDARIES[1].length - 2] : (n2 <= 0 ? string + "?" : string + n2);
        string = string + (!bl ? "]" : "") + " ms" + "</a>" + "</TD><TD CLASS=\"status\" ALIGN=\"right\">" + "<a href=\"/values/speed/" + this.getId() + "\">" + (!bl ? (performanceEntry != null && System.currentTimeMillis() - performanceEntry.getLastTestTime() < 1200000L && performanceEntry.getLastTestAverage(0) != 0 ? String.valueOf(performanceEntry.getLastTestAverage(0)) : "?") + " (" + (performanceEntry != null && performanceEntry.getAverage(0) != 0 ? String.valueOf(performanceEntry.getAverage(0)) : "?") + ") " : "") + (!bl ? "[" : "");
        int n3 = performanceEntry == null ? Integer.MAX_VALUE : performanceEntry.getBound(0).getBound();
        string = n3 == 0 ? string + "<" + PerformanceEntry.BOUNDARIES[0][1] : (n3 < 0 || n3 == Integer.MAX_VALUE ? string + "?" : string + n3);
        string = string + (!bl ? "]" : "") + " kbit/s" + "</a>" + "</TD><TD CLASS=\"status\" ALIGN=\"right\">" + (this.getMixedPackets() < 0L ? "n/a" : NumberFormat.getInstance(Constants.LOCAL_FORMAT).format(this.getMixedPackets())) + "</TD><TD CLASS=\"status\">" + (this.getLastUpdate() < 0L ? "n/a" : new SimpleDateFormat("HH:mm:ss").format(new Date(this.getLastUpdate()))) + "</TD></TR>";
        return string;
    }

    public Node generateMixCascadeCurrentStatus() {
        Document document = XMLUtil.createDocument();
        Element element = document.createElement("CurrentStatus");
        element.setAttribute("CurrentRisk", Integer.toString(this.getCurrentRisk()));
        element.setAttribute("TrafficSituation", Integer.toString(this.getTrafficSituation()));
        element.setAttribute("ActiveUsers", Integer.toString(this.getNrOfActiveUsers()));
        element.setAttribute("MixedPackets", Long.toString(this.getMixedPackets()));
        element.setAttribute("LastUpdate", Long.toString(this.getLastUpdate()));
        return element;
    }

    private Element generateXmlRepresentation() {
        Document document = XMLUtil.createDocument();
        Element element = document.createElement(XML_ELEMENT_NAME);
        element.setAttribute("id", this.getId());
        element.setAttribute("currentRisk", Integer.toString(this.getCurrentRisk()));
        element.setAttribute("mixedPackets", Long.toString(this.getMixedPackets()));
        element.setAttribute("nrOfActiveUsers", Integer.toString(this.getNrOfActiveUsers()));
        element.setAttribute("trafficSituation", Integer.toString(this.getTrafficSituation()));
        element.setAttribute("LastUpdate", Long.toString(this.getLastUpdate()));
        return element;
    }

    public Element toXmlElement(Document document) {
        try {
            return (Element)XMLUtil.importNode(document, XMLUtil.toXMLDocument(this.m_statusXmlDataBytes).getDocumentElement(), true);
        }
        catch (XMLParseException xMLParseException) {
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

