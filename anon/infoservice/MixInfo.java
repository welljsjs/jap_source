/*
 * Decompiled with CFR 0.150.
 */
package anon.infoservice;

import anon.crypto.CertPath;
import anon.crypto.IVerifyable;
import anon.crypto.JAPCertificate;
import anon.crypto.MultiCertPath;
import anon.crypto.SignatureVerifier;
import anon.crypto.XMLSignature;
import anon.infoservice.AbstractDistributableCertifiedDatabaseEntry;
import anon.infoservice.DataRetentionInformation;
import anon.infoservice.Database;
import anon.infoservice.ListenerInterface;
import anon.infoservice.MixCascadeExitAddresses;
import anon.infoservice.ServiceLocation;
import anon.infoservice.ServiceOperator;
import anon.infoservice.ServiceSoftware;
import anon.pay.xml.XMLPriceCertificate;
import anon.terms.TermsAndConditionsMixInfo;
import anon.util.XMLParseException;
import anon.util.XMLUtil;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Date;
import java.util.Hashtable;
import java.util.Vector;
import logging.LogHolder;
import logging.LogType;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class MixInfo
extends AbstractDistributableCertifiedDatabaseEntry
implements IVerifyable,
Database.IWebInfo {
    public static final String NAME_TYPE_MIX = "Mix";
    public static final String NAME_TYPE_OPERATOR = "Operator";
    public static final String XML_ELEMENT_CONTAINER_NAME = "Mixes";
    public static final String XML_ELEMENT_NAME = "Mix";
    public static final String XML_ELEMENT_MIX_NAME = "Name";
    public static final String XML_ATTRIBUTE_NAME_FOR_CASCADE = "forCascade";
    public static final String XML_ELEM_PREMIUM_REMINDER = "PaymentReminderProbability";
    public static final String XML_ELEMENT_WEBINFO_CONTAINER = "MixWebInfos";
    public static final String INFOSERVICE_COMMAND_WEBINFOS = "/mixwebinfos";
    public static final String INFOSERVICE_COMMAND_WEBINFO = "/mixwebinfo/";
    private static final String XML_ELEMENT_WEBINFO = "MixWebInfo";
    private static final String XML_ELEM_SERVER_MONITORING = "ServerMonitoring";
    private static final String XML_ATTR_PAYMENT = "payment";
    public static final int FIRST_MIX = 0;
    public static final int MIDDLE_MIX = 1;
    public static final int LAST_MIX = 2;
    private int m_type;
    private int m_iPremiumProbability = -1;
    private DataRetentionInformation m_drInfo;
    private boolean m_bPayment = false;
    private boolean m_dynamic = false;
    private boolean m_bSocks = false;
    private final Vector m_vecVisibleAdresses = new Vector();
    private final Vector m_vecListenerAdresses = new Vector();
    private final Vector m_vecListenerInterfaces = new Vector();
    private final Vector m_vecListenerMonitoring = new Vector();
    private String m_mixId;
    private long m_lastUpdate;
    private long m_serial;
    private String m_name;
    private String m_nameFragmentForCascade;
    private boolean m_bUseCascadeNameFragment = false;
    private ServiceLocation m_mixLocation;
    private ServiceOperator m_mixOperator;
    private ServiceSoftware m_mixSoftware;
    private boolean m_freeMix;
    private Element m_xmlStructure;
    private MultiCertPath m_mixCertPath;
    private XMLPriceCertificate m_priceCert;
    private long m_prepaidInterval;
    private XMLSignature m_mixSignature;
    private boolean m_bFromCascade;
    private TermsAndConditionsMixInfo m_mixTnCInfo = null;
    static /* synthetic */ Class class$anon$infoservice$ServiceOperator;

    public MixInfo(Element element) throws XMLParseException {
        this(element, 0L);
    }

    public MixInfo(Element element, long l) throws XMLParseException {
        this(element, l, false);
    }

    public MixInfo(MultiCertPath multiCertPath) {
        super(Long.MAX_VALUE);
        if (multiCertPath == null) {
            throw new IllegalArgumentException("No Mix cert path!");
        }
        Vector<JAPCertificate> vector = new Vector<JAPCertificate>();
        Vector vector2 = multiCertPath.getPaths();
        for (int i = 0; i < vector2.size(); ++i) {
            vector.addElement(((CertPath)vector2.elementAt(i)).getFirstCertificate());
        }
        this.m_mixId = JAPCertificate.calculateXORofSKIs(vector);
        this.m_name = multiCertPath.getSubject().getCommonName();
        if (this.m_name == null) {
            this.m_name = "Mix";
        }
        this.m_type = -1;
        this.m_bFromCascade = true;
        this.m_mixCertPath = multiCertPath;
        this.m_lastUpdate = 0L;
        this.m_serial = 0L;
        CertPath certPath = multiCertPath.getPath();
        this.m_mixLocation = new ServiceLocation(null, certPath.getFirstCertificate());
        this.m_mixOperator = new ServiceOperator(null, multiCertPath, 0L);
        this.m_freeMix = false;
        this.m_prepaidInterval = 4000000L;
    }

    public MixInfo(String string, MultiCertPath multiCertPath, XMLPriceCertificate xMLPriceCertificate, long l) {
        super(Long.MAX_VALUE);
        this.m_mixId = string;
        this.m_name = string;
        this.m_type = -1;
        this.m_bFromCascade = true;
        this.m_mixCertPath = multiCertPath;
        this.m_lastUpdate = 0L;
        this.m_serial = 0L;
        JAPCertificate jAPCertificate = null;
        if (multiCertPath != null) {
            jAPCertificate = multiCertPath.getPath().getFirstCertificate();
        }
        this.m_mixLocation = new ServiceLocation(null, jAPCertificate);
        this.m_mixOperator = new ServiceOperator(null, multiCertPath, 0L);
        this.m_freeMix = false;
        this.m_priceCert = xMLPriceCertificate;
        if (this.m_priceCert != null) {
            this.m_bPayment = true;
        }
        this.m_prepaidInterval = l;
    }

    public MixInfo(Element element, long l, boolean bl) throws XMLParseException {
        super(l <= 0L ? System.currentTimeMillis() + 900000L : l);
        Object object;
        Object object2;
        Object object3;
        Object object4;
        this.m_bFromCascade = bl;
        this.m_mixId = XMLUtil.parseAttribute((Node)element, "id", null);
        if (this.m_mixId == null) {
            throw new XMLParseException("##__null__##", "id");
        }
        try {
            this.m_mixSignature = SignatureVerifier.getInstance().getVerifiedXml(element, 1);
            if (this.m_mixSignature != null) {
                this.m_mixCertPath = this.m_mixSignature.getMultiCertPath();
            } else {
                LogHolder.log(7, LogType.MISC, "No signature node found while looking for MixCascade certificate.");
            }
        }
        catch (Exception exception) {
            LogHolder.log(3, LogType.MISC, "Error while looking for appended certificates in the MixInfo structure: " + exception.toString());
        }
        if (!this.checkId()) {
            throw new XMLParseException("##__root__##", "Malformed Mix ID: " + this.m_mixId);
        }
        this.m_iPremiumProbability = XMLUtil.parseValue(XMLUtil.getFirstChildByName(element, XML_ELEM_PREMIUM_REMINDER), -1);
        this.m_bSocks = XMLUtil.parseAttribute(XMLUtil.getFirstChildByName(element, "Proxies"), "socks5Support", false);
        Node node = XMLUtil.getFirstChildByName(element, NAME_TYPE_OPERATOR);
        Node node2 = XMLUtil.getFirstChildByName(element, "Location");
        Node node3 = XMLUtil.getFirstChildByName(element, "LastUpdate");
        Node node4 = XMLUtil.getFirstChildByName(element, "Software");
        Node node5 = XMLUtil.getFirstChildByName(element, "PrepaidIntervalKbytes");
        Node node6 = XMLUtil.getFirstChildByName(element, "PriceCertificate");
        if (node6 != null) {
            this.m_priceCert = new XMLPriceCertificate((Element)node6);
            if (!this.m_priceCert.getSubjectKeyIdentifier().equals(this.getId())) {
                object4 = "SKI in price certificate differs from Mix ID! SKI: $" + this.m_priceCert.getSubjectKeyIdentifier() + "$ MixID: $" + this.getId() + "$";
                LogHolder.log(3, LogType.PAY, (String)object4);
            }
        }
        if (l < Long.MAX_VALUE) {
            this.parseVisibleAdresses(element);
            this.parseListenerAdresses(element);
        }
        object4 = (Element)XMLUtil.getFirstChildByName(element, "ListenerInterfaces");
        XMLUtil.assertNotNull((Node)object4);
        NodeList nodeList = object4.getElementsByTagName("ListenerInterface");
        if (nodeList.getLength() == 0) {
            throw new XMLParseException("First Mix has no ListenerInterfaces in its XML structure.");
        }
        for (int i = 0; i < nodeList.getLength(); ++i) {
            this.m_vecListenerInterfaces.addElement(new ListenerInterface((Element)nodeList.item(i)));
        }
        Element element2 = (Element)XMLUtil.getFirstChildByName(element, XML_ELEM_SERVER_MONITORING);
        if (element2 != null) {
            object4 = (Element)XMLUtil.getFirstChildByName(element2, "ListenerInterfaces");
            if (object4 != null) {
                nodeList = object4.getElementsByTagName("ListenerInterface");
                for (int i = 0; i < nodeList.getLength(); ++i) {
                    this.m_vecListenerMonitoring.addElement(new ListenerInterface((Element)nodeList.item(i)));
                }
            } else {
                String string = XMLUtil.parseValue(XMLUtil.getFirstChildByName(element2, "Host"), null);
                int n = XMLUtil.parseValue(XMLUtil.getFirstChildByName(element2, "Port"), -1);
                if (string != null && n >= 0) {
                    this.m_vecListenerMonitoring.addElement(new ListenerInterface(string, n));
                }
            }
        }
        if (!bl) {
            Node node7 = XMLUtil.getFirstChildByName(element, "MixType");
            XMLUtil.assertNotNull(node7);
            this.m_type = this.parseMixType(XMLUtil.parseValue(node7, null));
            this.m_bPayment = XMLUtil.parseAttribute(node7, XML_ATTR_PAYMENT, false);
            this.m_dynamic = XMLUtil.parseValue(XMLUtil.getFirstChildByName(element, "Dynamic"), false);
            if (node4 == null) {
                throw new XMLParseException("Software", this.m_mixId);
            }
            if (node3 == null) {
                throw new XMLParseException("LastUpdate", this.m_mixId);
            }
            this.m_lastUpdate = XMLUtil.parseValue(node3, 0L);
        } else {
            this.m_lastUpdate = System.currentTimeMillis() - 900000L;
        }
        this.m_prepaidInterval = XMLUtil.parseValue(node5, 4000L) * 1000L;
        this.m_serial = XMLUtil.parseValue(node3, 0L);
        Node node8 = XMLUtil.getFirstChildByName(element, "TermsAndConditionsInfos");
        TermsAndConditionsMixInfo termsAndConditionsMixInfo = this.m_mixTnCInfo = node8 != null ? new TermsAndConditionsMixInfo(node8) : null;
        if (node4 != null) {
            this.m_mixSoftware = new ServiceSoftware(node4);
        }
        CertPath certPath = null;
        if (this.m_mixCertPath != null) {
            certPath = this.m_mixCertPath.getPath();
        }
        if (certPath != null) {
            this.m_mixLocation = new ServiceLocation(node2, certPath.getFirstCertificate());
            this.m_mixOperator = new ServiceOperator(node, this.m_mixCertPath, this.m_lastUpdate);
        } else {
            this.m_mixLocation = new ServiceLocation(node2, null);
            this.m_mixOperator = new ServiceOperator(node, null, this.m_lastUpdate);
        }
        Node node9 = XMLUtil.getFirstChildByName(element, "DataRetention");
        if (node9 != null) {
            if (this.m_mixOperator.getOrganization() != null && (this.m_mixOperator.getOrganization().indexOf("JAP-Team") >= 0 || this.m_mixOperator.getOrganization().indexOf("Independent Centre") >= 0) && XMLUtil.getFirstChildByName(node9, "Description") == null) {
                node9 = XMLUtil.importNode(XMLUtil.createDocument(), node9, true);
                object3 = node9.getOwnerDocument().createElement("Description");
                XMLUtil.setAttribute((Element)object3, "lang", "en");
                object2 = node9.getOwnerDocument().createElement("URL");
                XMLUtil.setValue((Node)object2, "http://anon.inf.tu-dresden.de/dataretention_en.html");
                object3.appendChild((Node)object2);
                node9.appendChild((Node)object3);
                object3 = node9.getOwnerDocument().createElement("Description");
                XMLUtil.setAttribute((Element)object3, "lang", "de");
                object2 = node9.getOwnerDocument().createElement("URL");
                XMLUtil.setValue((Node)object2, "http://anon.inf.tu-dresden.de/dataretention_de.html");
                object3.appendChild((Node)object2);
                node9.appendChild((Node)object3);
            }
            this.m_drInfo = new DataRetentionInformation((Element)node9);
        }
        object3 = (ServiceOperator)Database.getInstance(class$anon$infoservice$ServiceOperator == null ? (class$anon$infoservice$ServiceOperator = MixInfo.class$("anon.infoservice.ServiceOperator")) : class$anon$infoservice$ServiceOperator).getEntryById(this.m_mixOperator.getId());
        if (this.m_mixOperator.getCertPath() != null && this.m_mixOperator.getCertPath().getPaths() != null && this.m_mixOperator.getCertPath().getPaths().size() > 0) {
            if (object3 == null || ((ServiceOperator)object3).getCertPath() == null || ((ServiceOperator)object3).getCertPath().getPaths() == null || ((ServiceOperator)object3).getCertPath().getPaths().size() == 0 || ((ServiceOperator)object3).getOrganization() == null) {
                Database.getInstance(class$anon$infoservice$ServiceOperator == null ? (class$anon$infoservice$ServiceOperator = MixInfo.class$("anon.infoservice.ServiceOperator")) : class$anon$infoservice$ServiceOperator).update(this.m_mixOperator);
            } else {
                object2 = this.m_mixOperator.getCertPath().getPaths();
                object = ((ServiceOperator)object3).getCertPath().getPaths();
                for (int i = 0; i < ((Vector)object2).size(); ++i) {
                    if (((Vector)object2).size() < i + 1 || ((Vector)object).size() < i + 1) {
                        if (((Vector)object2).size() <= ((Vector)object).size()) break;
                        Database.getInstance(class$anon$infoservice$ServiceOperator == null ? (class$anon$infoservice$ServiceOperator = MixInfo.class$("anon.infoservice.ServiceOperator")) : class$anon$infoservice$ServiceOperator).update(this.m_mixOperator);
                        break;
                    }
                    JAPCertificate jAPCertificate = ((CertPath)((Vector)object2).elementAt(i)).getSecondCertificate();
                    JAPCertificate jAPCertificate2 = ((CertPath)((Vector)object).elementAt(i)).getSecondCertificate();
                    if (jAPCertificate == null) break;
                    if (jAPCertificate2 != null && !jAPCertificate.getValidity().getValidTo().after(jAPCertificate2.getValidity().getValidTo())) continue;
                    Database.getInstance(class$anon$infoservice$ServiceOperator == null ? (class$anon$infoservice$ServiceOperator = MixInfo.class$("anon.infoservice.ServiceOperator")) : class$anon$infoservice$ServiceOperator).update(this.m_mixOperator);
                    break;
                }
            }
        }
        this.m_freeMix = false;
        this.m_xmlStructure = element;
        object2 = XMLUtil.getFirstChildByName(element, XML_ELEMENT_MIX_NAME);
        this.m_name = XMLUtil.parseValue((Node)object2, null);
        object = XMLUtil.parseAttribute((Node)object2, XML_ATTRIBUTE_NAME_FOR_CASCADE, "");
        if (((String)object).equals(NAME_TYPE_OPERATOR) && this.m_mixOperator != null) {
            this.m_nameFragmentForCascade = this.m_mixOperator != null ? this.m_mixOperator.getCommonName() : null;
            this.m_bUseCascadeNameFragment = true;
        } else if (((String)object).equals("Mix") && this.m_mixLocation != null) {
            this.m_nameFragmentForCascade = this.m_mixLocation.getCommonName();
            this.m_bUseCascadeNameFragment = true;
        }
        if (this.m_nameFragmentForCascade != null && this.m_nameFragmentForCascade.equals("AN.ON Operator Certificate")) {
            this.m_nameFragmentForCascade = this.m_mixLocation != null && this.m_mixLocation.getCommonName() != null && !this.m_mixLocation.getCommonName().startsWith("<Mix id=") ? this.m_mixLocation.getCommonName() : null;
        }
        if (this.m_nameFragmentForCascade == null || this.m_nameFragmentForCascade.startsWith("<Mix id=")) {
            if (this.m_name != null) {
                this.m_nameFragmentForCascade = this.m_name;
            } else {
                LogHolder.log(4, LogType.MISC, "Could not set cascade name fragment for Mix!");
                this.m_nameFragmentForCascade = "Unknown Mix";
            }
        }
        if (this.m_name == null) {
            this.m_name = this.m_mixLocation != null && this.m_mixLocation.getCommonName() != null && !this.m_mixLocation.getCommonName().startsWith("<Mix id=") ? this.m_mixLocation.getCommonName() : this.m_nameFragmentForCascade;
        }
    }

    public int getPremiumProbability() {
        if (this.m_iPremiumProbability >= 0) {
            return 0;
        }
        return this.m_iPremiumProbability;
    }

    private void parseListenerAdresses(Node node) {
        this.parseVisibleAdresses(node, "ListenerInterfaces", "ListenerInterface", this.m_vecListenerAdresses);
    }

    private void parseVisibleAdresses(Node node) {
        Node node2 = XMLUtil.getFirstChildByName(node, "Proxies");
        if (node2 == null) {
            return;
        }
        node2 = XMLUtil.getFirstChildByName(node2, "Proxy");
        while (node2 != null) {
            if (node2.getNodeName().equals("Proxy")) {
                this.parseVisibleAdresses(node2, "VisibleAddresses", "VisibleAddress", this.m_vecVisibleAdresses);
            }
            node2 = XMLUtil.getNextSibling(node2);
        }
    }

    private void parseVisibleAdresses(Node node, String string, String string2, Vector vector) {
        Node node2 = XMLUtil.getFirstChildByName(node, string);
        Node node3 = XMLUtil.getFirstChildByName(node2, string2);
        while (node3 != null) {
            Node node4;
            String string3;
            if (node3.getNodeName().equals(string2) && (string3 = XMLUtil.parseValue(node4 = XMLUtil.getFirstChildByName(node3, "Host"), null)) != null) {
                try {
                    InetAddress inetAddress = InetAddress.getByName(string3);
                    if (MixCascadeExitAddresses.isValidAddress(inetAddress) && !vector.contains(inetAddress)) {
                        vector.addElement(inetAddress);
                    }
                }
                catch (UnknownHostException unknownHostException) {
                    LogHolder.log(6, LogType.NET, unknownHostException);
                }
                catch (Exception exception) {
                    LogHolder.log(2, LogType.NET, exception);
                }
            }
            node3 = XMLUtil.getNextSibling(node3);
        }
    }

    private int parseMixType(String string) throws XMLParseException {
        if ("FirstMix".equals(string)) {
            return 0;
        }
        if ("MiddleMix".equals(string)) {
            return 1;
        }
        if ("LastMix".equals(string)) {
            return 2;
        }
        throw new XMLParseException("MixType", "Unkonwn type: " + string);
    }

    public boolean isPersistanceDeletionAllowed() {
        return XMLUtil.getStorageMode() == 2;
    }

    public void deletePersistence() {
        if (this.isPersistanceDeletionAllowed()) {
            this.m_mixSignature = null;
            this.m_xmlStructure = null;
        }
    }

    public Vector getVisibleAddresses() {
        return (Vector)this.m_vecVisibleAdresses.clone();
    }

    public Vector getListenerAddresses() {
        return (Vector)this.m_vecListenerAdresses.clone();
    }

    public Vector getListenerInterfaces() {
        return (Vector)this.m_vecListenerInterfaces.clone();
    }

    public Vector getMonitoringListenerInterfaces() {
        return (Vector)this.m_vecListenerMonitoring.clone();
    }

    public String getId() {
        return this.m_mixId;
    }

    public boolean isSocks5Supported() {
        return this.m_bSocks;
    }

    public boolean isFromCascade() {
        return this.m_bFromCascade;
    }

    public long getLastUpdate() {
        return this.m_lastUpdate;
    }

    public long getVersionNumber() {
        return this.m_serial;
    }

    public String getName() {
        return this.m_name;
    }

    public DataRetentionInformation getDataRetentionInformation() {
        return this.m_drInfo;
    }

    public boolean isVerified() {
        if (this.m_mixCertPath != null) {
            return this.m_mixCertPath.isVerified();
        }
        return false;
    }

    public boolean isValid() {
        if (this.m_mixCertPath != null) {
            return this.m_mixCertPath.isValid(new Date());
        }
        return false;
    }

    public XMLPriceCertificate getPriceCertificate() {
        return this.m_priceCert;
    }

    public long getPrepaidInterval() {
        return this.m_prepaidInterval;
    }

    public void setPriceCertificate(XMLPriceCertificate xMLPriceCertificate) {
        this.m_priceCert = xMLPriceCertificate;
    }

    public MultiCertPath getCertPath() {
        return this.m_mixCertPath;
    }

    public XMLSignature getSignature() {
        return this.m_mixSignature;
    }

    public ServiceLocation getServiceLocation() {
        return this.m_mixLocation;
    }

    public ServiceOperator getServiceOperator() {
        return this.m_mixOperator;
    }

    public ServiceSoftware getServiceSoftware() {
        return this.m_mixSoftware;
    }

    public boolean isFreeMix() {
        return this.m_freeMix;
    }

    public void setFreeMix(boolean bl) {
        this.m_freeMix = bl;
    }

    public String getPostFile() {
        String string = "/helo";
        if (this.isFreeMix()) {
            string = "/configure";
        }
        return string;
    }

    public Element getXmlStructure() {
        return this.m_xmlStructure;
    }

    public int getType() {
        return this.m_type;
    }

    public boolean isPayment() {
        return this.m_bPayment;
    }

    public String getTypeAsString() {
        switch (this.m_type) {
            case 0: {
                return "First Mix";
            }
            case 1: {
                return "Middle Mix";
            }
            case 2: {
                return "Last Mix";
            }
        }
        return "Unknown type!";
    }

    public boolean isDynamic() {
        return this.m_dynamic;
    }

    public String getFirstHostName() throws Exception {
        for (int i = 0; i < this.m_vecListenerInterfaces.size(); ++i) {
            ListenerInterface listenerInterface = (ListenerInterface)this.m_vecListenerInterfaces.elementAt(i);
            if (listenerInterface.isHidden()) continue;
            return listenerInterface.getHost();
        }
        return "";
    }

    public int getFirstPort() throws Exception {
        for (int i = 0; i < this.m_vecListenerInterfaces.size(); ++i) {
            ListenerInterface listenerInterface = (ListenerInterface)this.m_vecListenerInterfaces.elementAt(i);
            if (listenerInterface.isHidden()) continue;
            return listenerInterface.getPort();
        }
        return -1;
    }

    public boolean isCascadaNameFragmentUsed() {
        return this.m_bUseCascadeNameFragment;
    }

    public String getNameFragmentForCascade() {
        return this.m_nameFragmentForCascade;
    }

    public TermsAndConditionsMixInfo getTermsAndConditionMixInfo() {
        return this.m_mixTnCInfo;
    }

    public Element getWebInfo(Document document) {
        if (document == null) {
            return null;
        }
        Element element = document.createElement(XML_ELEMENT_WEBINFO);
        XMLUtil.setAttribute(element, XML_ATTR_PAYMENT, this.isPayment());
        XMLUtil.setAttribute(element, "id", this.getId());
        Element element2 = null;
        Element element3 = null;
        if (this.getCertPath() == null) {
            return null;
        }
        XMLUtil.createChildElementWithValue(element, XML_ELEMENT_MIX_NAME, this.getName());
        CertPath certPath = this.getCertPath().getPath();
        element2 = new ServiceOperator(null, this.getCertPath(), 0L).toXMLElement(document);
        element3 = new ServiceLocation(null, certPath.getFirstCertificate()).toXMLElement(document);
        if (element2 != null) {
            element.appendChild(element2);
        }
        if (element3 != null) {
            element.appendChild(element3);
        }
        this.appendListenerInterfaces(element, this.m_vecListenerInterfaces);
        if (this.m_vecListenerMonitoring.size() > 0) {
            this.appendListenerInterfaces(XMLUtil.createChildElement(element, XML_ELEM_SERVER_MONITORING), this.m_vecListenerMonitoring);
        }
        element.appendChild(this.m_mixCertPath.toXmlElement(document));
        return element;
    }

    private void appendListenerInterfaces(Element element, Vector vector) {
        Element element2 = XMLUtil.createChildElement(element, "ListenerInterfaces");
        Hashtable<String, Element> hashtable = new Hashtable<String, Element>();
        Hashtable<String, Element> hashtable2 = new Hashtable<String, Element>();
        Hashtable<String, Element> hashtable3 = new Hashtable<String, Element>();
        for (int i = 0; i < vector.size(); ++i) {
            Element element3;
            ListenerInterface listenerInterface = (ListenerInterface)vector.elementAt(i);
            if (listenerInterface.isHidden() && hashtable2.containsKey(listenerInterface.getHost())) {
                element3 = (Element)hashtable2.get(listenerInterface.getHost());
            } else if (listenerInterface.isVirtual() && hashtable3.containsKey(listenerInterface.getHost())) {
                element3 = (Element)hashtable3.get(listenerInterface.getHost());
            } else if (hashtable.containsKey(listenerInterface.getHost())) {
                element3 = (Element)hashtable.get(listenerInterface.getHost());
            } else {
                element3 = XMLUtil.createChildElement(element2, "ListenerInterface");
                if (listenerInterface.isVirtual()) {
                    XMLUtil.setAttribute(element3, "virtual", listenerInterface.isVirtual());
                    hashtable3.put(listenerInterface.getHost(), element3);
                } else if (listenerInterface.isHidden()) {
                    XMLUtil.setAttribute(element3, "hidden", listenerInterface.isHidden());
                    hashtable2.put(listenerInterface.getHost(), element3);
                } else {
                    hashtable.put(listenerInterface.getHost(), element3);
                }
                XMLUtil.setAttribute(element3, "Host", listenerInterface.getHost());
            }
            if (listenerInterface.getProtocol() == 5) continue;
            XMLUtil.createChildElementWithValue(element3, "Port", "" + listenerInterface.getPort());
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

