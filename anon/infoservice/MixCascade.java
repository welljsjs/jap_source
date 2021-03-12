/*
 * Decompiled with CFR 0.150.
 */
package anon.infoservice;

import anon.AnonServerDescription;
import anon.crypto.CertPath;
import anon.crypto.IVerifyable;
import anon.crypto.MultiCertPath;
import anon.crypto.SignatureVerifier;
import anon.crypto.X509DistinguishedName;
import anon.crypto.XMLSignature;
import anon.infoservice.AbstractDistributableCertifiedDatabaseEntry;
import anon.infoservice.DataRetentionInformation;
import anon.infoservice.Database;
import anon.infoservice.IServiceContextContainer;
import anon.infoservice.InfoServiceHolder;
import anon.infoservice.ListenerInterface;
import anon.infoservice.MixInfo;
import anon.infoservice.MixPosition;
import anon.infoservice.PerformanceEntry;
import anon.infoservice.ServiceLocation;
import anon.infoservice.ServiceOperator;
import anon.infoservice.StatusInfo;
import anon.pay.PaymentInstanceDBEntry;
import anon.pay.xml.XMLEasyCC;
import anon.util.CountryMapper;
import anon.util.IXMLEncodable;
import anon.util.Util;
import anon.util.XMLParseException;
import anon.util.XMLUtil;
import anon.util.ZLibTools;
import java.util.Date;
import java.util.Hashtable;
import java.util.StringTokenizer;
import java.util.Vector;
import logging.LogHolder;
import logging.LogType;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class MixCascade
extends AbstractDistributableCertifiedDatabaseEntry
implements AnonServerDescription,
IVerifyable,
IServiceContextContainer,
Database.IWebInfo,
ListenerInterface.IListenerInterfaceGetter {
    public static final String SUPPORTED_PAYMENT_PROTOCOL_VERSION = "2.0";
    public static final String TC_REQUIRED_VERSION_SUFFIX = "tc";
    public static final int DISTRIBUTION_MIN = 0;
    public static final int DISTRIBUTION_MAX = 6;
    public static final String XML_ELEMENT_NAME = "MixCascade";
    public static final String XML_ELEMENT_CONTAINER_NAME = "MixCascades";
    private static final String XML_ATTR_USER_DEFINED = "userDefined";
    private static final String XML_ATTR_STUDY = "study";
    private static final String XML_ATTR_MAX_USERS = "maxUsers";
    private static final String XML_ATTR_PAYMENT = "payment";
    public static final String XML_ELEMENT_WEBINFO_CONTAINER = "CascadeWebInfos";
    public static final String XML_ELEMENT_WEBINFO = "CascadeWebInfo";
    public static final String XML_ELEMENT_WEBINFO_CASCADE_NAME = "CascadeName";
    public static final String XML_ELEMENT_WEBINFO_NAME = "Name";
    public static final String XML_ELEMENT_WEBINFO_COMPOSED_NAME = "ComposedName";
    public static final String XML_ELEMENT_WEBINFO_CURR_USERS = "CurrentUsers";
    public static final String XML_ATTR_WEBINFO_OPERATOR_COUNT = "operatorCount";
    public static final String XML_ATTR_WEBINFO_DISTRIBUTION = "distribution";
    public static final String XML_ATTR_WEBINFO_MIX_COUNTRY = "mixCountry";
    public static final String XML_ATTR_WEBINFO_MIX_POSITION = "mixPosition";
    public static final String XML_ATTR_WEBINFO_OP_COUNTRY = "operatorCountry";
    public static final String INFOSERVICE_COMMAND_WEBINFOS = "/cascadewebinfos";
    public static final String INFOSERVICE_COMMAND_WEBINFO = "/cascadewebinfo/";
    public static final int MAX_CASCADE_NAME_LENGTH = 35;
    private final Object CACHE_HOSTS_PORT = new Object();
    private boolean m_bDefaultVerified = false;
    private boolean m_bImplicitTrust = false;
    private boolean m_bSock5Support = false;
    private String m_strConcatenatedPriceCertHashes;
    private DataRetentionInformation m_dataRetentionInfo;
    private String m_mixCascadeId;
    private long m_lastUpdate;
    private String m_strName;
    private final Object SYNC_NAME = new Object();
    private Vector m_decomposedCascadeName;
    private Vector m_listenerInterfaces;
    private Hashtable m_hashListenerInterfaces;
    private Vector m_mixIds;
    private String m_strMixIds;
    private String m_piid = "";
    private MixInfo[] m_mixInfos;
    private String m_strMixNames;
    private int m_nrPriceCerts = 0;
    private Vector m_mixNodes;
    private long m_serial;
    private Element m_xmlStructure;
    private byte[] m_compressedXmlStructure;
    private XMLSignature m_signature;
    private MultiCertPath m_certPath;
    private int m_nrCountries = 0;
    private int m_nrOperators = 0;
    private int m_nrOperatorsCountForDistribution = 0;
    private int m_nrOperatorsShown = 0;
    private int m_distributionPoints = 0;
    private boolean[] m_mixCertVerifiedAndValid;
    private Object SYNC_OPERATORS_AND_COUNTRIES = new Object();
    private volatile boolean termsAndConditionsConfirmationRequired = false;
    private boolean m_userDefined;
    private boolean m_bStudy = false;
    private int m_maxUsers = 0;
    private String m_strPorts;
    private String m_strHosts;
    private boolean m_isPayment;
    private long m_prepaidInterval = 4000000L;
    private String m_mixProtocolVersion;
    private String m_paymentProtocolVersion;
    private Hashtable m_priceCertificateHashes = new Hashtable();
    private Vector m_priceCertificates = new Vector();
    private boolean m_bFromCascade;
    private String m_context;
    static /* synthetic */ Class class$anon$pay$PaymentInstanceDBEntry;
    static /* synthetic */ Class class$anon$infoservice$StatusInfo;
    static /* synthetic */ Class class$anon$infoservice$PerformanceEntry;

    public MixCascade(byte[] arrby) throws XMLParseException {
        this(arrby, null, 0L, null);
    }

    public MixCascade(Element element) throws XMLParseException {
        this(null, element, 0L, null);
    }

    public MixCascade(Element element, long l) throws XMLParseException {
        this(null, element, l, null);
    }

    public MixCascade(Element element, long l, String string) throws XMLParseException {
        this(null, element, l, string);
    }

    private MixCascade(byte[] arrby, Element element, long l, String string) throws XMLParseException {
        super(l <= 0L ? System.currentTimeMillis() + 900000L : l);
        Vector vector;
        int n;
        Object object;
        Object object2;
        Element element2;
        NodeList nodeList;
        Node node;
        boolean bl = this.m_bFromCascade = string != null;
        if (element == null && arrby == null) {
            throw new XMLParseException("##__null__##");
        }
        if (element == null) {
            element = (Element)XMLUtil.getFirstChildByName(XMLUtil.toXMLDocument(ZLibTools.decompress(arrby)), XML_ELEMENT_NAME);
        }
        try {
            this.m_signature = SignatureVerifier.getInstance().getVerifiedXml(element, 1);
            if (this.m_signature != null) {
                this.m_certPath = this.m_signature.getMultiCertPath();
            } else {
                LogHolder.log(7, LogType.MISC, "No signature node found while looking for MixCascade certificate.");
            }
        }
        catch (Exception exception) {
            LogHolder.log(3, LogType.MISC, "Error while looking for appended certificates in the MixCascade structure: " + exception.toString());
        }
        this.m_userDefined = XMLUtil.parseAttribute((Node)element, XML_ATTR_USER_DEFINED, false);
        this.m_bStudy = XMLUtil.parseAttribute((Node)element, XML_ATTR_STUDY, false);
        this.m_maxUsers = XMLUtil.parseAttribute((Node)element, XML_ATTR_MAX_USERS, 0);
        this.m_maxUsers = Math.min(this.m_maxUsers, 9999);
        if (element == null || !element.getNodeName().equals(XML_ELEMENT_NAME)) {
            throw new XMLParseException(XML_ELEMENT_NAME);
        }
        this.m_mixCascadeId = XMLUtil.parseAttribute((Node)element, "id", null);
        if (this.m_mixCascadeId == null) {
            node = XMLUtil.getFirstChildByName(XMLUtil.getFirstChildByName(element, "Mixes"), "Mix");
            this.m_mixCascadeId = XMLUtil.parseAttribute(node, "id", string);
        }
        if (!this.checkId()) {
            throw new XMLParseException("##__root__##", "Malformed Mix-Cascade ID: " + this.m_mixCascadeId);
        }
        this.m_mixProtocolVersion = XMLUtil.parseValue(XMLUtil.getFirstChildByName(element, "MixProtocolVersion"), null);
        if (this.m_mixProtocolVersion != null) {
            this.m_mixProtocolVersion = this.m_mixProtocolVersion.trim();
            if (this.m_mixProtocolVersion.endsWith(TC_REQUIRED_VERSION_SUFFIX)) {
                this.m_mixProtocolVersion = this.m_mixProtocolVersion.substring(0, this.m_mixProtocolVersion.length() - TC_REQUIRED_VERSION_SUFFIX.length());
                this.termsAndConditionsConfirmationRequired = true;
            }
        }
        node = XMLUtil.getFirstChildByName(element, "Payment");
        this.m_isPayment = XMLUtil.parseAttribute(node, "required", false);
        this.m_paymentProtocolVersion = XMLUtil.parseAttribute(node, "version", SUPPORTED_PAYMENT_PROTOCOL_VERSION);
        this.m_prepaidInterval = XMLUtil.parseAttribute(node, "prepaidInterval", 4000001L);
        this.m_piid = XMLUtil.parseAttribute(node, "piid", "");
        this.m_context = XMLUtil.parseAttribute((Node)element, "context", null);
        if (this.m_context == null || this.m_context.equals("de.jondos.jondonym")) {
            this.m_context = this.m_isPayment ? "jondonym.premium" : "jondonym";
        }
        if (!this.m_bFromCascade) {
            nodeList = element.getElementsByTagName("Network");
            if (nodeList.getLength() == 0) {
                throw new XMLParseException("Network");
            }
            element2 = (Element)nodeList.item(0);
            NodeList nodeList2 = element2.getElementsByTagName("ListenerInterfaces");
            if (nodeList2.getLength() == 0) {
                throw new XMLParseException("ListenerInterfaces");
            }
            object2 = (Element)nodeList2.item(0);
            NodeList nodeList3 = object2.getElementsByTagName("ListenerInterface");
            if (nodeList3.getLength() == 0) {
                throw new XMLParseException("ListenerInterface");
            }
            this.m_listenerInterfaces = new Vector();
            this.m_hashListenerInterfaces = new Hashtable();
            for (int i = 0; i < nodeList3.getLength(); ++i) {
                Element element3 = (Element)nodeList3.item(i);
                object = new ListenerInterface(element3);
                this.m_listenerInterfaces.addElement(object);
                this.m_hashListenerInterfaces.put(((ListenerInterface)object).getId(), object);
            }
            ListenerInterface.blockInterfacesFromDatabase(this);
        }
        if ((nodeList = element.getElementsByTagName("Mixes")).getLength() == 0) {
            throw new XMLParseException("Mixes");
        }
        element2 = (Element)nodeList.item(0);
        int n2 = Integer.parseInt(element2.getAttribute("count"));
        object2 = element2.getElementsByTagName("Mix");
        if (object2.getLength() == 0 || n2 != object2.getLength()) {
            throw new XMLParseException("Mix");
        }
        this.m_mixIds = new Vector();
        this.m_mixNodes = new Vector();
        for (n = 0; n < object2.getLength(); ++n) {
            object = (Element)object2.item(n);
            this.m_mixIds.addElement(object.getAttribute("id"));
            if (n == 0 && !this.isUserDefined() && !this.m_mixIds.lastElement().equals(this.m_mixCascadeId)) {
                throw new XMLParseException("##__root__##", "Cascade ID not ID of first mix: " + this.m_mixCascadeId);
            }
            this.m_mixNodes.addElement(object);
        }
        this.m_mixInfos = new MixInfo[object2.getLength()];
        n = 0;
        for (int i = 0; i < object2.getLength(); ++i) {
            try {
                this.m_mixInfos[i] = new MixInfo((Element)object2.item(i), l, true);
                if (i + 1 == object2.getLength()) {
                    this.m_bSock5Support = this.m_mixInfos[i].isSocks5Supported();
                }
                if (this.m_mixInfos[i].getPriceCertificate() != null) {
                    if (i == 0) {
                        this.m_piid = this.m_mixInfos[i].getPriceCertificate().getBiID();
                    }
                    if (i == 0 && this.m_prepaidInterval > 4000000L) {
                        this.m_prepaidInterval = this.m_mixInfos[i].getPrepaidInterval();
                    }
                    this.m_priceCertificates.addElement(this.m_mixInfos[i].getPriceCertificate());
                    this.m_priceCertificateHashes.put(new MixPosition(i, this.m_mixInfos[i].getId()), this.m_mixInfos[i].getPriceCertificate().getHashValue());
                    ++this.m_nrPriceCerts;
                }
                if (this.m_mixInfos[i].getDataRetentionInformation() == null) continue;
                ++n;
                continue;
            }
            catch (XMLParseException xMLParseException) {
                this.m_mixInfos[i] = null;
            }
        }
        this.m_strConcatenatedPriceCertHashes = XMLEasyCC.createConcatenatedPriceCertHashes(this.m_priceCertificateHashes, true);
        this.m_strName = XMLUtil.parseValue(XMLUtil.getFirstChildByName(element, XML_ELEMENT_WEBINFO_NAME), null);
        if (!this.m_bFromCascade) {
            this.getDecomposedCascadeName();
        }
        if (l == 0L && this.m_mixInfos.length > 0 && (vector = this.m_mixInfos[this.m_mixInfos.length - 1].getVisibleAddresses()).size() == 0) {
            vector = this.m_mixInfos[this.m_mixInfos.length - 1].getListenerAddresses();
        }
        if (this.m_mixCascadeId == null) {
            this.m_mixCascadeId = (String)this.m_mixIds.elementAt(0);
        }
        Node node2 = XMLUtil.getFirstChildByName(element, "LastUpdate");
        if (this.m_bFromCascade) {
            this.m_lastUpdate = 0L;
            this.m_serial = 0L;
        } else {
            if (node2 == null) {
                throw new XMLParseException("LastUpdate");
            }
            this.m_lastUpdate = XMLUtil.parseValue(node2, System.currentTimeMillis() - 900000L);
            this.m_serial = XMLUtil.parseAttribute((Node)element, "serial", this.m_lastUpdate);
        }
        this.m_compressedXmlStructure = arrby != null ? arrby : ZLibTools.compress(XMLSignature.toCanonical(element));
        this.m_xmlStructure = element;
        if (this.m_bFromCascade && string.trim().length() > 0) {
            this.m_mixCascadeId = string;
        }
        this.createMixIDString();
        this.calculateOperatorsAndCountries();
        if (this.isPayment()) {
            if (this.m_piid == null || this.m_piid.trim().length() == 0) {
                throw new XMLParseException("Payment instance id is null on paid cascade!");
            }
            if (this.m_prepaidInterval > 4000000L) {
                LogHolder.log(4, LogType.PAY, "Prepaid interval of cascade " + this.getId() + "is too high: " + this.m_prepaidInterval);
            } else if (this.m_prepaidInterval < 5000L) {
                LogHolder.log(4, LogType.PAY, "Prepaid interval of cascade " + this.getId() + "is too low: " + this.m_prepaidInterval);
            }
        }
        this.m_prepaidInterval = Math.min(this.m_prepaidInterval, 4000000L);
        this.m_prepaidInterval = Math.max(this.m_prepaidInterval, 5000L);
        this.m_dataRetentionInfo = DataRetentionInformation.getCascadeDataRetentionInformation(this);
    }

    public MixCascade(String string, int n) throws Exception {
        this(null, null, string, n);
    }

    public MixCascade(String string, String string2, String string3, int n) throws Exception {
        this(string, string2, new ListenerInterface(string3, n, 2).toVector());
    }

    public MixCascade(String string, String string2, Vector vector) throws Exception {
        this(string, string2, null, vector);
    }

    public MixCascade(String string, String string2, Vector vector, Vector vector2) throws Exception {
        this(string, string2, vector, vector2, Long.MAX_VALUE);
    }

    public MixCascade(String string, String string2, String string3, int n, long l) throws Exception {
        this(string, string2, null, new ListenerInterface(string3, n, 2).toVector(), l);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public MixCascade(String string, String string2, Vector vector, Vector vector2, long l) throws Exception {
        super(l);
        ListenerInterface listenerInterface = (ListenerInterface)vector2.elementAt(0);
        String string3 = listenerInterface.getHost();
        String string4 = Integer.toString(listenerInterface.getPort());
        this.m_mixCascadeId = string2 == null || string2.length() == 0 ? "(user)" + string3 + "%3A" + string4 : string2;
        this.m_strName = string != null ? string : string3 + ":" + string4;
        this.m_listenerInterfaces = (Vector)vector2.clone();
        if (this.m_listenerInterfaces != null) {
            this.m_hashListenerInterfaces = new Hashtable();
            Vector vector3 = this.m_listenerInterfaces;
            synchronized (vector3) {
                for (int i = 0; i < this.m_listenerInterfaces.size(); ++i) {
                    ListenerInterface listenerInterface2 = (ListenerInterface)this.m_listenerInterfaces.elementAt(i);
                    this.m_hashListenerInterfaces.put(listenerInterface2.getId(), listenerInterface2);
                }
            }
        }
        ListenerInterface.blockInterfacesFromDatabase(this);
        this.m_lastUpdate = 0L;
        this.m_mixNodes = new Vector();
        if (vector == null || vector.size() == 0) {
            this.m_mixIds = new Vector();
            this.m_mixIds.addElement(this.m_mixCascadeId);
        } else {
            this.m_mixIds = (Vector)vector.clone();
        }
        this.m_mixInfos = new MixInfo[this.m_mixIds.size()];
        for (int i = 0; i < this.m_mixInfos.length; ++i) {
            this.m_mixInfos[i] = null;
        }
        this.m_userDefined = true;
        this.m_bDefaultVerified = true;
        this.m_xmlStructure = this.generateXmlRepresentation();
        this.m_compressedXmlStructure = ZLibTools.compress(XMLSignature.toCanonical(this.m_xmlStructure));
        this.createMixIDString();
        this.calculateOperatorsAndCountries();
    }

    public boolean isPersistanceDeletionAllowed() {
        return XMLUtil.getStorageMode() == 2;
    }

    public void deletePersistence() {
        if (this.isPersistanceDeletionAllowed()) {
            this.m_signature = null;
            this.m_compressedXmlStructure = null;
            this.m_xmlStructure = null;
        }
    }

    public boolean compareMixIDs(MixCascade mixCascade) {
        if (mixCascade == null) {
            return false;
        }
        return mixCascade.getMixIDsAsString().equals(this.getMixIDsAsString());
    }

    public String getId() {
        return this.m_mixCascadeId;
    }

    public String getMixProtocolVersion() {
        return this.m_mixProtocolVersion;
    }

    public String getPaymentProtocolVersion() {
        return this.m_paymentProtocolVersion;
    }

    public PaymentInstanceDBEntry getPaymentInstance() {
        PaymentInstanceDBEntry paymentInstanceDBEntry = (PaymentInstanceDBEntry)Database.getInstance(class$anon$pay$PaymentInstanceDBEntry == null ? (class$anon$pay$PaymentInstanceDBEntry = MixCascade.class$("anon.pay.PaymentInstanceDBEntry")) : class$anon$pay$PaymentInstanceDBEntry).getEntryById(this.getPIID());
        return paymentInstanceDBEntry;
    }

    public String getPIID() {
        return this.m_piid;
    }

    public long getPrepaidInterval() {
        return this.m_prepaidInterval;
    }

    public boolean isFromCascade() {
        return this.m_bFromCascade;
    }

    public long getVersionNumber() {
        return this.m_serial;
    }

    public long getLastUpdate() {
        return this.m_lastUpdate;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public String getMixNames() {
        MixInfo[] arrmixInfo = this.m_mixInfos;
        synchronized (this.m_mixInfos) {
            if (this.m_strMixNames == null) {
                this.m_strMixNames = "";
                for (int i = 0; i < this.m_mixInfos.length; ++i) {
                    if (this.m_mixInfos[i] == null) continue;
                    if (this.m_strMixNames.length() > 0) {
                        this.m_strMixNames = this.m_strMixNames + "-";
                    }
                    this.m_strMixNames = this.m_strMixNames + this.m_mixInfos[i].getName();
                }
                if (this.m_strMixNames.length() == 0) {
                    this.m_strMixNames = this.m_strName;
                } else if (!this.m_strName.equals(this.m_strMixNames)) {
                    this.m_strMixNames = this.m_strName + "|" + this.m_strMixNames;
                }
            }
            // ** MonitorExit[var1_1] (shouldn't be in output)
            return this.m_strMixNames;
        }
    }

    public String getName() {
        this.getDecomposedCascadeName();
        return this.m_strName;
    }

    public int getMaxUsers() {
        return this.m_maxUsers;
    }

    public String toString() {
        return this.getName();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void checkJAPTeamName() {
        Object object = this.SYNC_NAME;
        synchronized (object) {
            this.m_strName = Util.stripString(this.m_strName, "-/\\");
            boolean bl = false;
            int n = 35;
            if (this.m_strName != null && this.m_strName.indexOf("JAP") < 0 && this.m_mixInfos[0] != null && this.m_mixInfos[0].getServiceOperator().getOrganization() != null && this.m_mixInfos[0].getServiceOperator().getOrganization().indexOf("JAP") >= 0 && this.m_mixInfos[this.m_mixInfos.length - 1].getServiceOperator().getOrganization() != null && this.m_mixInfos[this.m_mixInfos.length - 1].getServiceOperator().getOrganization().indexOf("JAP") >= 0) {
                bl = true;
                n -= 6;
            }
            this.m_strName = Util.cutString(this.m_strName, n);
            if (bl) {
                this.m_strName = this.m_strName + " (JAP)";
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public Vector getDecomposedCascadeName() {
        Object object = this.SYNC_NAME;
        synchronized (object) {
            if (this.m_decomposedCascadeName == null) {
                int n;
                this.m_decomposedCascadeName = new Vector();
                if (this.m_strName != null && (this.isUserDefined() || this.m_mixInfos.length == 0 && this.isShownAsTrusted())) {
                    this.m_decomposedCascadeName.addElement(this.m_strName);
                    return this.m_decomposedCascadeName;
                }
                if (this.m_mixInfos.length == 0) {
                    this.m_decomposedCascadeName.addElement("Unknown");
                    return this.m_decomposedCascadeName;
                }
                boolean bl = false;
                boolean bl2 = true;
                Vector<ServiceOperator> vector = new Vector<ServiceOperator>();
                ServiceOperator serviceOperator = null;
                for (n = 0; n < this.m_mixInfos.length; ++n) {
                    if (this.m_mixInfos[n] == null || this.m_mixInfos[n].getServiceOperator() == null) {
                        bl = true;
                        break;
                    }
                    serviceOperator = this.m_mixInfos[n].getServiceOperator();
                    if (vector.contains(serviceOperator)) {
                        bl2 = false;
                        break;
                    }
                    vector.addElement(serviceOperator);
                }
                if (this.m_strName != null) {
                    StringTokenizer stringTokenizer = new StringTokenizer(this.m_strName, "-/\\");
                    if (stringTokenizer.countTokens() == this.getNumberOfMixes()) {
                        String string;
                        StringTokenizer stringTokenizer2;
                        while (stringTokenizer.hasMoreTokens() && (stringTokenizer2 = new StringTokenizer(string = stringTokenizer.nextToken().trim())).hasMoreTokens() && (string = stringTokenizer2.nextToken().trim()).length() != 0) {
                            this.m_decomposedCascadeName.addElement(string);
                        }
                    }
                    if (this.m_decomposedCascadeName.size() == 0) {
                        int n2;
                        for (n2 = 0; !(n2 >= this.m_mixInfos.length || this.m_mixInfos[n2] != null && this.m_mixInfos[n2].isCascadaNameFragmentUsed()); ++n2) {
                        }
                        if (n2 == this.m_mixInfos.length) {
                            this.checkJAPTeamName();
                            this.m_decomposedCascadeName.addElement(this.m_strName);
                            return this.m_decomposedCascadeName;
                        }
                    }
                }
                if (bl || this.m_mixInfos[0].getServiceOperator().equals(this.m_mixInfos[this.m_mixInfos.length - 1].getServiceOperator())) {
                    this.m_strName = this.m_mixInfos[0] != null && this.m_mixInfos[0].isCascadaNameFragmentUsed() ? this.m_mixInfos[0].getNameFragmentForCascade() : (this.m_decomposedCascadeName.size() > 0 ? (String)this.m_decomposedCascadeName.elementAt(0) : (this.m_mixInfos[0] != null ? this.m_mixInfos[0].getName() : "Unknown"));
                    this.checkJAPTeamName();
                    this.m_decomposedCascadeName.removeAllElements();
                    this.m_decomposedCascadeName.addElement(this.m_strName);
                } else {
                    int n3;
                    if (!bl2) {
                        this.m_decomposedCascadeName.removeAllElements();
                    }
                    vector = new Vector();
                    n = this.m_decomposedCascadeName.size() > 0 ? 1 : 0;
                    for (n3 = 0; n3 < this.m_mixInfos.length; ++n3) {
                        serviceOperator = this.m_mixInfos[n3].getServiceOperator();
                        if (vector.contains(serviceOperator)) continue;
                        vector.addElement(serviceOperator);
                        if (n != 0) {
                            if (this.m_mixInfos[n3].isCascadaNameFragmentUsed()) {
                                this.m_decomposedCascadeName.setElementAt(this.m_mixInfos[n3].getNameFragmentForCascade(), n3);
                            }
                        } else {
                            if (this.m_mixInfos[n3].isCascadaNameFragmentUsed()) {
                                this.m_decomposedCascadeName.addElement(this.m_mixInfos[n3].getNameFragmentForCascade());
                            } else {
                                this.m_decomposedCascadeName.addElement(this.m_mixInfos[n3].getName());
                            }
                            if (this.m_decomposedCascadeName.elementAt(n3) == null) {
                                this.m_decomposedCascadeName.setElementAt("Unknown", n3);
                            }
                        }
                        this.m_decomposedCascadeName.setElementAt(Util.stripString(this.m_decomposedCascadeName.elementAt(n3).toString(), "-/\\"), n3);
                    }
                    this.m_strName = "";
                    for (n3 = 0; n3 < this.m_decomposedCascadeName.size(); ++n3) {
                        this.m_strName = this.m_strName + (this.m_strName.equals("") ? "" : "-");
                        this.m_strName = this.m_strName + this.m_decomposedCascadeName.elementAt(n3);
                    }
                    n3 = 15;
                    while (this.m_strName.length() > 35) {
                        this.m_strName = "";
                        for (int i = 0; i < this.m_decomposedCascadeName.size(); ++i) {
                            this.m_strName = this.m_strName + (this.m_strName.equals("") ? "" : "-");
                            this.m_decomposedCascadeName.setElementAt(Util.cutString(this.m_decomposedCascadeName.elementAt(i).toString(), n3), i);
                            this.m_strName = this.m_strName + this.m_decomposedCascadeName.elementAt(i);
                        }
                        --n3;
                    }
                }
            }
        }
        return this.m_decomposedCascadeName;
    }

    public boolean equals(Object object) {
        boolean bl = false;
        if (object != null && object instanceof MixCascade) {
            bl = this.getId().equals(((MixCascade)object).getId());
        }
        return bl;
    }

    public boolean checkId() {
        return this.m_userDefined || super.checkId();
    }

    public int hashCode() {
        return this.getId().hashCode();
    }

    public int getNumberOfListenerInterfaces() {
        if (this.m_listenerInterfaces != null) {
            return this.m_listenerInterfaces.size();
        }
        return 0;
    }

    public ListenerInterface getListenerInterface(int n) {
        ListenerInterface listenerInterface = null;
        if (n >= 0 && n < this.getNumberOfListenerInterfaces()) {
            listenerInterface = (ListenerInterface)this.m_listenerInterfaces.elementAt(n);
        }
        return listenerInterface;
    }

    public ListenerInterface getListenerInterface(String string) {
        if (this.m_hashListenerInterfaces != null) {
            return (ListenerInterface)this.m_hashListenerInterfaces.get(string);
        }
        return null;
    }

    public Vector getListenerInterfaces() {
        if (this.m_listenerInterfaces != null) {
            return (Vector)this.m_listenerInterfaces.clone();
        }
        return null;
    }

    public String getHostsAsString() {
        this.cacheHostAndPortsAsString();
        return this.m_strHosts;
    }

    public String getPortsAsString() {
        this.cacheHostAndPortsAsString();
        return this.m_strPorts;
    }

    public Vector getHosts() {
        Vector<String> vector = new Vector<String>();
        for (int i = 0; i < this.getNumberOfListenerInterfaces(); ++i) {
            String string = ((ListenerInterface)this.m_listenerInterfaces.elementAt(0)).getHost();
            if (vector.contains(string)) continue;
            vector.addElement(string);
        }
        return vector;
    }

    public int getNumberOfMixes() {
        return this.m_mixIds.size();
    }

    public String getMixIDsAsString() {
        return this.m_strMixIds;
    }

    public MixInfo getMixInfo(int n) {
        if (n < 0 || n >= this.getNumberOfMixes() || n >= this.m_mixInfos.length) {
            return null;
        }
        return this.m_mixInfos[n];
    }

    public MixInfo getMixInfo(String string) {
        if (string == null) {
            return null;
        }
        for (int i = 0; i < this.m_mixIds.size(); ++i) {
            if (!this.m_mixIds.elementAt(i).equals(string)) continue;
            return this.m_mixInfos[i];
        }
        return null;
    }

    public String getMixId(int n) {
        if (n < 0 || n >= this.getNumberOfMixes() || n >= this.m_mixIds.size()) {
            return null;
        }
        return this.m_mixIds.elementAt(n).toString();
    }

    public Vector getMixIds() {
        return (Vector)this.m_mixIds.clone();
    }

    public boolean isUserDefined() {
        return this.m_userDefined;
    }

    public boolean isSocks5Supported() {
        return this.m_bSock5Support;
    }

    public void showAsTrusted(boolean bl) {
        this.m_bImplicitTrust = bl;
    }

    public boolean isShownAsTrusted() {
        return this.m_bImplicitTrust;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void setUserDefined(boolean bl, MixCascade mixCascade) throws XMLParseException {
        this.m_userDefined = bl;
        if (this.m_userDefined && mixCascade != null && mixCascade.getId().equals(this.getId())) {
            this.m_strName = mixCascade.m_strName;
            Object object = this.SYNC_NAME;
            synchronized (object) {
                this.m_decomposedCascadeName = new Vector();
                this.m_decomposedCascadeName.addElement(this.m_strName);
            }
            this.m_listenerInterfaces = mixCascade.m_listenerInterfaces;
            if (this.m_listenerInterfaces != null) {
                this.m_hashListenerInterfaces = new Hashtable();
                Vector vector = this.m_listenerInterfaces;
                synchronized (vector) {
                    for (int i = 0; i < this.m_listenerInterfaces.size(); ++i) {
                        object = (ListenerInterface)this.m_listenerInterfaces.elementAt(i);
                        this.m_hashListenerInterfaces.put(((ListenerInterface)object).getId(), object);
                    }
                }
            }
            ListenerInterface.blockInterfacesFromDatabase(this);
            this.m_lastUpdate = 0L;
            this.m_serial = 0L;
        }
        if (this.m_xmlStructure != null) {
            this.m_xmlStructure = this.generateXmlRepresentation();
        }
        if (this.m_compressedXmlStructure != null) {
            this.m_compressedXmlStructure = ZLibTools.compress(XMLSignature.toCanonical(this.m_xmlStructure));
        }
        this.calculateOperatorsAndCountries();
    }

    public StatusInfo fetchCurrentStatus() {
        return this.fetchCurrentStatus(-1L);
    }

    public StatusInfo fetchCurrentStatus(long l) {
        String string = this.getMixId(0);
        if (string == null) {
            string = this.getId();
        }
        StatusInfo statusInfo = l <= 0L ? InfoServiceHolder.getInstance().getStatusInfo(this) : InfoServiceHolder.getInstance().getStatusInfo(this, l);
        return statusInfo;
    }

    public StatusInfo getCurrentStatus() {
        StatusInfo statusInfo = (StatusInfo)Database.getInstance(class$anon$infoservice$StatusInfo == null ? (class$anon$infoservice$StatusInfo = MixCascade.class$("anon.infoservice.StatusInfo")) : class$anon$infoservice$StatusInfo).getEntryById(this.getId());
        if (statusInfo == null) {
            statusInfo = StatusInfo.createDummyStatusInfo(this.getId());
        }
        return statusInfo;
    }

    public String getPostFile() {
        return "/cascade";
    }

    public int getPostEncoding() {
        return 1;
    }

    public byte[] getPostData() {
        return this.m_compressedXmlStructure;
    }

    public byte[] getCompressedData() {
        return this.m_compressedXmlStructure;
    }

    public Element getXmlStructure() {
        return this.m_xmlStructure;
    }

    public String getConcatenatedPriceCertHashes() {
        return this.m_strConcatenatedPriceCertHashes;
    }

    public Hashtable getPriceCertificateHashes() {
        return (Hashtable)this.m_priceCertificateHashes.clone();
    }

    public Vector getPriceCertificates() {
        return (Vector)this.m_priceCertificates.clone();
    }

    public int getNrOfPriceCerts() {
        return this.m_nrPriceCerts;
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
        return this.m_bDefaultVerified;
    }

    public boolean isDefaultVerified() {
        return this.m_bDefaultVerified;
    }

    public boolean isValid() {
        if (this.m_certPath != null) {
            return this.m_certPath.isValid(new Date());
        }
        return false;
    }

    public DataRetentionInformation getDataRetentionInformation() {
        return this.m_dataRetentionInfo;
    }

    public boolean isActiveStudy() {
        return this.m_bStudy || this.m_userDefined;
    }

    public int getNumberOfOperators() {
        this.calculateOperatorsAndCountries();
        return this.m_nrOperators;
    }

    public boolean areListenerInterfacesBlocked() {
        boolean bl = false;
        for (int i = 0; i < this.getNumberOfListenerInterfaces(); ++i) {
            if (!this.getListenerInterface(i).isValid()) continue;
            bl = true;
            break;
        }
        return !bl && this.getNumberOfListenerInterfaces() > 0;
    }

    public int getNumberOfOperatorsShown() {
        this.calculateOperatorsAndCountries();
        return this.m_nrOperatorsShown;
    }

    public int getNumberOfCountries() {
        this.calculateOperatorsAndCountries();
        return this.m_nrCountries;
    }

    public int getDistribution() {
        this.calculateOperatorsAndCountries();
        return this.m_distributionPoints;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void cacheHostAndPortsAsString() {
        Object object = this.CACHE_HOSTS_PORT;
        synchronized (object) {
            int n;
            if (this.m_strPorts != null || this.m_strHosts != null) {
                return;
            }
            String string = "";
            String string2 = "";
            int[] arrn = new int[this.getNumberOfListenerInterfaces()];
            for (n = 0; n < this.getNumberOfListenerInterfaces(); ++n) {
                if (string.indexOf(this.getListenerInterface(n).getHost()) == -1) {
                    if (string.length() > 0) {
                        string = string + "\n";
                    }
                    string = string + this.getListenerInterface(n).getHost();
                }
                arrn[n] = this.getListenerInterface(n).getPort();
            }
            for (n = 0; n < arrn.length; ++n) {
                for (int i = n + 1; i < arrn.length; ++i) {
                    if (arrn[n] <= arrn[i]) continue;
                    int n2 = arrn[i];
                    arrn[i] = arrn[n];
                    arrn[n] = n2;
                }
            }
            Vector<Integer> vector = new Vector<Integer>(arrn.length);
            for (n = 0; n < arrn.length; ++n) {
                Integer n3 = new Integer(arrn[n]);
                if (vector.contains(n3)) continue;
                vector.addElement(new Integer(arrn[n]));
            }
            for (n = 0; n < vector.size(); ++n) {
                string2 = string2 + vector.elementAt(n).toString();
                if (n == vector.size() - 1) continue;
                string2 = string2 + ", ";
            }
            this.m_strHosts = string;
            this.m_strPorts = string2;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void calculateOperatorsAndCountries() {
        Object object = this.SYNC_OPERATORS_AND_COUNTRIES;
        synchronized (object) {
            String string;
            String string2;
            int n;
            Hashtable<String, String> hashtable = new Hashtable<String, String>();
            Hashtable<String, String> hashtable2 = new Hashtable<String, String>();
            Hashtable<String, String> hashtable3 = new Hashtable<String, String>();
            boolean bl = this.m_mixCertVerifiedAndValid == null || this.m_mixCertVerifiedAndValid.length != this.getNumberOfMixes();
            if (bl) {
                this.m_mixCertVerifiedAndValid = new boolean[this.getNumberOfMixes()];
                for (n = 0; n < this.m_mixCertVerifiedAndValid.length; ++n) {
                    this.m_mixCertVerifiedAndValid[n] = false;
                }
            }
            for (n = 0; n < this.getNumberOfMixes(); ++n) {
                boolean bl2;
                boolean bl3 = bl2 = this.getMixInfo(n) != null && this.getMixInfo(n).getCertPath() != null && this.getMixInfo(n).getCertPath().isVerified() && this.getMixInfo(n).getCertPath().isValid(new Date());
                if (this.m_mixCertVerifiedAndValid[n] != bl2) {
                    bl = true;
                }
                this.m_mixCertVerifiedAndValid[n] = bl2;
            }
            if (!bl) {
                return;
            }
            this.m_nrOperatorsCountForDistribution = 0;
            this.m_nrOperators = 0;
            this.m_nrOperatorsShown = 0;
            this.m_nrCountries = 0;
            for (n = 0; n < this.getNumberOfMixes(); ++n) {
                if (this.getMixInfo(n) == null || this.getMixInfo(n).getCertPath() == null) continue;
                X509DistinguishedName x509DistinguishedName = this.getMixInfo(n).getCertPath().getIssuer();
                if (x509DistinguishedName != null && x509DistinguishedName.getOrganisation() != null && !hashtable.contains(x509DistinguishedName.getOrganisation()) && !hashtable3.contains(this.getMixInfo(n).getId())) {
                    string2 = x509DistinguishedName.getCountryCode();
                    string = this.getMixInfo(n).getCertPath().getSubject().getCountryCode();
                    try {
                        if (new CountryMapper(string2).toString() == string2) {
                            string2 = null;
                        } else if (new CountryMapper(string).toString() == string) {
                            string = null;
                        }
                    }
                    catch (IllegalArgumentException illegalArgumentException) {
                        string2 = null;
                        string = null;
                    }
                    if (!(string2 == null || string == null || hashtable2.contains(string) || hashtable2.contains(string2) || !this.m_mixCertVerifiedAndValid[n] || n > 1 && n + 1 != this.getNumberOfMixes())) {
                        ++this.m_nrCountries;
                    }
                    if (this.m_mixCertVerifiedAndValid[n]) {
                        if (string2 != null) {
                            hashtable2.put(string2, string2);
                        }
                        if (string != null) {
                            hashtable2.put(string, string);
                        }
                    }
                    hashtable.put(x509DistinguishedName.getOrganisation(), x509DistinguishedName.getOrganisation());
                    hashtable3.put(this.getMixInfo(n).getId(), this.getMixInfo(n).getId());
                    if (this.m_mixCertVerifiedAndValid[n]) {
                        ++this.m_nrOperators;
                        if (n <= 1 || n + 1 == this.getNumberOfMixes()) {
                            ++this.m_nrOperatorsCountForDistribution;
                        }
                    }
                    ++this.m_nrOperatorsShown;
                    continue;
                }
                if (this.m_nrOperators <= 0) {
                    for (int i = 0; i < this.getNumberOfMixes(); ++i) {
                        if (!this.m_mixCertVerifiedAndValid[i]) continue;
                        this.m_nrOperators = 1;
                        this.m_nrOperatorsCountForDistribution = 1;
                        break;
                    }
                } else {
                    this.m_nrOperators = 1;
                    this.m_nrOperatorsCountForDistribution = 1;
                }
                this.m_nrOperatorsShown = 1;
                this.m_nrCountries = Math.min(this.m_nrOperatorsCountForDistribution, 1);
                break;
            }
            if (this.m_nrOperatorsCountForDistribution >= 2 && this.m_nrCountries == 1 && this.m_mixCertVerifiedAndValid[0] && this.m_mixCertVerifiedAndValid[this.getNumberOfMixes() - 1]) {
                string2 = this.getMixInfo(0).getCertPath().getIssuer().getCountryCode();
                string = this.getMixInfo(0).getCertPath().getSubject().getCountryCode();
                String string3 = this.getMixInfo(this.getNumberOfMixes() - 1).getCertPath().getIssuer().getCountryCode();
                String string4 = this.getMixInfo(this.getNumberOfMixes() - 1).getCertPath().getSubject().getCountryCode();
                if (!(string3 == null || string4 == null || string2 == null || string == null || string3.equals(string2) || string4.equals(string2) || string4.equals(string) || string3.equals(string))) {
                    ++this.m_nrCountries;
                }
            }
            this.m_distributionPoints = this.m_nrOperatorsCountForDistribution == 3 && this.m_nrCountries == 1 ? 3 : (this.m_nrOperatorsCountForDistribution == 2 && this.m_nrCountries == 1 ? 2 : (this.m_nrOperatorsCountForDistribution == 1 ? 1 : this.m_nrOperatorsCountForDistribution + this.m_nrCountries));
            if (this.m_distributionPoints > 1 && this.getNumberOfMixes() > 1 && this.getMixInfo(0) != null && this.getMixInfo(0).getCertPath() != null && this.getMixInfo(this.getNumberOfMixes() - 1) != null && this.getMixInfo(this.getNumberOfMixes() - 1).getCertPath() != null && this.getMixInfo(0).getCertPath().getIssuer().getCountryCode() != null && this.getMixInfo(this.getNumberOfMixes() - 1).getCertPath().getIssuer().getCountryCode() != null && this.getMixInfo(0).getCertPath().getIssuer().getCountryCode().equals(this.getMixInfo(this.getNumberOfMixes() - 1).getCertPath().getIssuer().getCountryCode())) {
                this.m_distributionPoints = Math.max(2, this.m_distributionPoints - 2);
            }
            this.calculateOperatorsAndCountries();
        }
    }

    private Element generateXmlRepresentation() {
        Element[] arrelement;
        Object object;
        Element element;
        Document document = XMLUtil.createDocument();
        Element element2 = document.createElement(XML_ELEMENT_NAME);
        XMLUtil.setAttribute(element2, "id", this.getId());
        if (this.m_isPayment) {
            element = document.createElement("Payment");
            XMLUtil.setAttribute(element, "required", this.m_isPayment);
            XMLUtil.setAttribute(element, "version", this.m_paymentProtocolVersion);
            element2.appendChild(element);
        }
        element = document.createElement(XML_ELEMENT_WEBINFO_NAME);
        XMLUtil.setValue((Node)element, this.getName());
        Element element3 = document.createElement("Network");
        Element element4 = document.createElement("ListenerInterfaces");
        for (int i = 0; i < this.getNumberOfListenerInterfaces(); ++i) {
            object = this.getListenerInterface(i);
            Element element5 = ((ListenerInterface)object).toXmlElement(document);
            element4.appendChild(element5);
        }
        element3.appendChild(element4);
        Element element6 = document.createElement("Mixes");
        XMLUtil.setAttribute(element6, "count", this.getNumberOfMixes());
        object = this.m_mixIds.elements();
        int n = 0;
        while (object.hasMoreElements()) {
            if (this.m_mixNodes.size() > n) {
                object.nextElement();
                try {
                    element6.appendChild(XMLUtil.importNode(document, (Node)this.m_mixNodes.elementAt(n), true));
                }
                catch (XMLParseException xMLParseException) {
                    LogHolder.log(2, LogType.MISC, "Could not import node " + ((Node)this.m_mixNodes.elementAt(n)).getNodeName() + "!");
                }
            } else {
                arrelement = document.createElement("Mix");
                XMLUtil.setAttribute((Element)arrelement, "id", (String)object.nextElement());
                element6.appendChild((Node)arrelement);
            }
            ++n;
        }
        Element element7 = document.createElement("LastUpdate");
        XMLUtil.setValue((Node)element7, this.getLastUpdate());
        element2.appendChild(element);
        element2.appendChild(element3);
        element2.appendChild(element6);
        element2.appendChild(element7);
        if (this.isUserDefined()) {
            XMLUtil.setAttribute(element2, XML_ATTR_USER_DEFINED, true);
            if (this.m_signature != null) {
                arrelement = this.m_signature.getXMLElements(document);
                for (int i = 0; i < arrelement.length; ++i) {
                    element2.appendChild(arrelement[i]);
                }
            }
        }
        return element2;
    }

    public boolean isPaymentProtocolSupported() {
        return !this.m_isPayment || this.m_isPayment && (this.getPaymentProtocolVersion().equals(SUPPORTED_PAYMENT_PROTOCOL_VERSION) || this.getPaymentProtocolVersion().equals(SUPPORTED_PAYMENT_PROTOCOL_VERSION));
    }

    public boolean isPayment() {
        return this.m_isPayment;
    }

    public boolean isTermsAndConditionsConfirmationRequired() {
        return this.termsAndConditionsConfirmationRequired;
    }

    private void createMixIDString() {
        this.m_strMixIds = "";
        for (int i = 0; i < this.m_mixIds.size(); ++i) {
            this.m_strMixIds = this.m_strMixIds + this.m_mixIds.elementAt(i);
        }
    }

    public String getContext() {
        if (this.m_context == null) {
            if (this.isPayment()) {
                return "jondonym.premium";
            }
            return "jondonym";
        }
        return this.m_context;
    }

    public Element getWebInfo(Document document) {
        IXMLEncodable iXMLEncodable;
        if (document == null) {
            return null;
        }
        Vector vector = this.getDecomposedCascadeName();
        Vector<String> vector2 = new Vector<String>(vector.size());
        String string = null;
        for (int i = 0; i < vector.size(); ++i) {
            string = (String)vector.elementAt(i);
            if (string == null || vector2.contains(string)) {
                string = "";
            }
            vector2.insertElementAt(string, i);
        }
        Element element = document.createElement(XML_ELEMENT_WEBINFO);
        XMLUtil.setAttribute(element, XML_ATTR_PAYMENT, this.isPayment());
        XMLUtil.setAttribute(element, "id", this.getId());
        if (this.getMaxUsers() > 0) {
            XMLUtil.setAttribute(element, XML_ATTR_MAX_USERS, this.getMaxUsers());
        }
        XMLUtil.setAttribute(element, XML_ATTR_WEBINFO_OPERATOR_COUNT, this.getNumberOfOperatorsShown());
        XMLUtil.setAttribute(element, XML_ATTR_WEBINFO_DISTRIBUTION, this.getDistribution());
        if (this.getContext() != null) {
            XMLUtil.setAttribute(element, "context", this.getContext());
        }
        Element element2 = XMLUtil.createChildElement(element, XML_ELEMENT_WEBINFO_CASCADE_NAME);
        Element element3 = document.createElement("Mixes");
        XMLUtil.createChildElementWithValue(element, XML_ELEMENT_WEBINFO_CURR_USERS, "" + this.getCurrentStatus().getNrOfActiveUsers());
        PerformanceEntry performanceEntry = (PerformanceEntry)Database.getInstance(class$anon$infoservice$PerformanceEntry == null ? (class$anon$infoservice$PerformanceEntry = MixCascade.class$("anon.infoservice.PerformanceEntry")) : class$anon$infoservice$PerformanceEntry).getEntryById(this.getId());
        if (performanceEntry != null) {
            element.appendChild(performanceEntry.toXmlElement(document));
        }
        if (this.getDataRetentionInformation() != null) {
            XMLUtil.createChildElement(element, "DataRetention");
        }
        Element element4 = null;
        String string2 = null;
        MixInfo mixInfo = null;
        ServiceOperator serviceOperator = null;
        ServiceLocation serviceLocation = null;
        Element element5 = null;
        Element element6 = null;
        Element element7 = null;
        String string3 = null;
        element.appendChild(element3);
        for (int i = 0; i < this.getNumberOfMixes(); ++i) {
            mixInfo = this.getMixInfo(i);
            if (mixInfo == null) continue;
            if (mixInfo.getCertPath() == null) {
                return null;
            }
            iXMLEncodable = mixInfo.getCertPath().getPath();
            serviceOperator = new ServiceOperator(null, mixInfo.getCertPath(), 0L);
            serviceLocation = new ServiceLocation(null, ((CertPath)iXMLEncodable).getFirstCertificate());
            string3 = mixInfo.getName();
            if (serviceOperator == null || serviceLocation == null) {
                return null;
            }
            string2 = null;
            if (i < vector2.size() && (string2 = (String)vector2.elementAt(i)) != null && string2.equals("")) {
                string2 = null;
            }
            if (string2 != null) {
                element4 = XMLUtil.createChildElementWithValue(element2, XML_ELEMENT_WEBINFO_NAME, string2);
                if (serviceLocation.getCountryCode() != null) {
                    XMLUtil.setAttribute(element4, XML_ATTR_WEBINFO_MIX_COUNTRY, serviceLocation.getCountryCode());
                }
                if (serviceOperator.getCountryCode() != null) {
                    XMLUtil.setAttribute(element4, XML_ATTR_WEBINFO_OP_COUNTRY, serviceOperator.getCountryCode());
                }
                XMLUtil.setAttribute(element4, XML_ATTR_WEBINFO_MIX_POSITION, i);
            }
            element5 = XMLUtil.createChildElement(element3, "Mix");
            XMLUtil.setAttribute(element5, "id", mixInfo.getId());
            if (string3 != null) {
                XMLUtil.createChildElementWithValue(element5, XML_ELEMENT_WEBINFO_NAME, string3);
            }
            element6 = serviceOperator.toXMLElement(document);
            element7 = serviceLocation.toXMLElement(document);
            if (element6 != null) {
                element5.appendChild(element6);
            }
            if (element7 == null) continue;
            element5.appendChild(element7);
        }
        element4 = XMLUtil.createChildElementWithValue(element2, XML_ELEMENT_WEBINFO_COMPOSED_NAME, this.getName());
        Element element8 = XMLUtil.createChildElement(element, "ListenerInterfaces");
        Hashtable<String, Element> hashtable = new Hashtable<String, Element>();
        for (int i = 0; i < this.getNumberOfListenerInterfaces(); ++i) {
            Element element9;
            iXMLEncodable = this.getListenerInterface(i);
            if (((ListenerInterface)iXMLEncodable).isHidden()) continue;
            if (hashtable.containsKey(((ListenerInterface)iXMLEncodable).getHost())) {
                element9 = (Element)hashtable.get(((ListenerInterface)iXMLEncodable).getHost());
            } else {
                element9 = XMLUtil.createChildElement(element8, "ListenerInterface");
                XMLUtil.setAttribute(element9, "Host", ((ListenerInterface)iXMLEncodable).getHost());
                hashtable.put(((ListenerInterface)iXMLEncodable).getHost(), element9);
            }
            if (((ListenerInterface)iXMLEncodable).getProtocol() == 5) continue;
            XMLUtil.createChildElementWithValue(element9, "Port", "" + ((ListenerInterface)iXMLEncodable).getPort());
        }
        return element;
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

