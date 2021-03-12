/*
 * Decompiled with CFR 0.150.
 */
package anon.infoservice;

import HTTPClient.HTTPConnection;
import HTTPClient.NVPair;
import anon.crypto.ExpiredSignatureException;
import anon.crypto.IVerifyable;
import anon.crypto.MultiCertPath;
import anon.crypto.SignatureCreator;
import anon.crypto.SignatureVerifier;
import anon.crypto.XMLSignature;
import anon.infoservice.AbstractDatabaseEntry;
import anon.infoservice.AbstractDistributableCertifiedDatabaseEntry;
import anon.infoservice.AbstractDistributableDatabaseEntry;
import anon.infoservice.Database;
import anon.infoservice.HTTPConnectionDescriptor;
import anon.infoservice.HTTPConnectionFactory;
import anon.infoservice.HttpRequestStructure;
import anon.infoservice.IBoostrapable;
import anon.infoservice.IBrowserIdentification;
import anon.infoservice.IMutableProxyInterface;
import anon.infoservice.ImmutableProxyInterface;
import anon.infoservice.JAPMinVersion;
import anon.infoservice.JAPVersionInfo;
import anon.infoservice.ListenerInterface;
import anon.infoservice.MixCascade;
import anon.infoservice.MixCascadeExitAddresses;
import anon.infoservice.MixInfo;
import anon.infoservice.PerformanceInfo;
import anon.infoservice.ServiceSoftware;
import anon.infoservice.StatusInfo;
import anon.pay.PaymentInstanceDBEntry;
import anon.terms.template.TermsAndConditionsTemplate;
import anon.util.ClassUtil;
import anon.util.Util;
import anon.util.XMLParseException;
import anon.util.XMLUtil;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.security.SignatureException;
import java.util.Date;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.StringTokenizer;
import java.util.Vector;
import logging.LogHolder;
import logging.LogType;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class InfoServiceDBEntry
extends AbstractDistributableCertifiedDatabaseEntry
implements IVerifyable,
IBoostrapable,
ListenerInterface.IListenerInterfaceGetter {
    public static final String XML_ELEMENT_CONTAINER_NAME = "InfoServices";
    public static final String XML_ELEMENT_NAME = "InfoService";
    public static final String HEADER_STATISTICS = "statistics";
    public static final int MINIMUM_GET_XML_CONNECTION_TIMEOUT = 10000;
    public static final int DEFAULT_GET_XML_CONNECTION_TIMEOUT = 15000;
    private static final long BLOCK_TIMEOUT = 180000L;
    private static int m_getXmlConnectionTimeout = 15000;
    private static long m_timeFirstJVMSocketError = Long.MAX_VALUE;
    private static long m_timeHandleAfterJVMSocketError = Long.MAX_VALUE;
    private static Runnable m_threadHandleAfterJVMSocketError;
    private static boolean ms_bUseStatistics;
    private static IMutableProxyInterface ms_proxyInterface;
    private static IBrowserIdentification ms_browserIdentification;
    private String m_strInfoServiceId;
    private String m_strName;
    private boolean m_bTemp = false;
    private ServiceSoftware m_infoserviceSoftware;
    private Vector m_listenerInterfaces;
    private Hashtable m_hashListenerInterfaces;
    private int m_preferedListenerInterface;
    private boolean m_bPrimaryForwarderList;
    private boolean m_neighbour;
    private Element m_xmlDescription;
    private boolean m_userDefined;
    private long m_creationTimeStamp;
    private XMLSignature m_signature;
    private long m_serial;
    private boolean m_bPerfServerEnabled;
    public static final Integer PROXY_AUTO;
    public static final Integer PROXY_FORCE_ANONYMOUS;
    public static final Integer PROXY_FORCE_DEFAULT;
    public static final Integer PROXY_FORCE_DIRECT;
    static /* synthetic */ Class class$anon$infoservice$InfoServiceDBEntry;
    static /* synthetic */ Class class$anon$infoservice$MixCascade;
    static /* synthetic */ Class class$anon$pay$PaymentInstanceDBEntry;
    static /* synthetic */ Class class$anon$infoservice$MixInfo;
    static /* synthetic */ Class class$org$w3c$dom$Element;
    static /* synthetic */ Class class$anon$infoservice$MessageDBEntry;
    static /* synthetic */ Class class$anon$infoservice$JavaVersionDBEntry;

    private static String generateId(ListenerInterface listenerInterface) {
        return listenerInterface.getHost() + "%3A" + listenerInterface.getPort();
    }

    public InfoServiceDBEntry(Element element) throws XMLParseException {
        this(element, 0L);
    }

    public InfoServiceDBEntry(Element element, long l) throws XMLParseException {
        super(l <= 0L ? System.currentTimeMillis() + 900000L : l);
        if (element == null) {
            throw new XMLParseException("##__null__##");
        }
        this.m_xmlDescription = element;
        this.m_signature = SignatureVerifier.getInstance().getVerifiedXml(element, 2);
        this.m_userDefined = XMLUtil.getFirstChildByName(element, "UserDefined") != null;
        this.m_bPerfServerEnabled = XMLUtil.getFirstChildByName(element, "PerformanceServer") != null;
        this.m_strInfoServiceId = element.getAttribute("id");
        if (!this.checkId()) {
            throw new XMLParseException("##__root__##", "Malformed InfoService ID: " + this.m_strInfoServiceId);
        }
        this.m_strName = XMLUtil.parseValue(XMLUtil.getFirstChildByName(element, "Name"), null);
        if (this.m_strName == null) {
            throw new XMLParseException("Name");
        }
        this.m_infoserviceSoftware = new ServiceSoftware((Element)XMLUtil.getFirstChildByName(element, ServiceSoftware.getXmlElementName()));
        Node node = XMLUtil.getFirstChildByName(element, "Network");
        if (node == null) {
            throw new XMLParseException("Network");
        }
        Element element2 = (Element)XMLUtil.getFirstChildByName(node, "ListenerInterfaces");
        if (node == null) {
            throw new XMLParseException("ListenerInterfaces");
        }
        NodeList nodeList = element2.getElementsByTagName("ListenerInterface");
        if (nodeList.getLength() == 0) {
            throw new XMLParseException("ListenerInterface");
        }
        this.m_listenerInterfaces = new Vector();
        this.m_hashListenerInterfaces = new Hashtable();
        for (int i = 0; i < nodeList.getLength(); ++i) {
            Element element3 = (Element)nodeList.item(i);
            ListenerInterface listenerInterface = new ListenerInterface(element3);
            this.m_listenerInterfaces.addElement(listenerInterface);
            this.m_hashListenerInterfaces.put(listenerInterface.getId(), listenerInterface);
        }
        ListenerInterface.blockInterfacesFromDatabase(this);
        this.m_preferedListenerInterface = 0;
        this.m_creationTimeStamp = XMLUtil.parseValue(XMLUtil.getFirstChildByName(element, "LastUpdate"), -1L);
        if (this.m_creationTimeStamp == -1L) {
            throw new XMLParseException("LastUpdate");
        }
        this.m_serial = XMLUtil.parseAttribute((Node)element, "serial", 0L);
        this.m_bPrimaryForwarderList = XMLUtil.getFirstChildByName(element, "ForwarderList") != null;
        this.m_neighbour = true;
    }

    public InfoServiceDBEntry(String string, int n) throws IllegalArgumentException {
        this(null, null, new ListenerInterface(string, n).toVector(), false, true, 0L, 0L, false, null);
        this.setUserDefined(true);
    }

    public InfoServiceDBEntry(Vector vector) throws IllegalArgumentException {
        this(null, null, vector, false, true, 0L, 0L, false, null);
        this.setUserDefined(true);
    }

    public InfoServiceDBEntry(String string, String string2, Vector vector, boolean bl, boolean bl2, long l, long l2, boolean bl3, ServiceSoftware serviceSoftware) throws IllegalArgumentException {
        super(bl2 ? Long.MAX_VALUE : System.currentTimeMillis() + 900000L);
        if (vector == null) {
            throw new IllegalArgumentException("No listener interfaces!");
        }
        Enumeration enumeration = vector.elements();
        this.m_listenerInterfaces = new Vector();
        this.m_hashListenerInterfaces = new Hashtable();
        while (enumeration.hasMoreElements()) {
            ListenerInterface listenerInterface = (ListenerInterface)enumeration.nextElement();
            this.m_listenerInterfaces.addElement(listenerInterface);
            this.m_hashListenerInterfaces.put(listenerInterface.getId(), listenerInterface);
        }
        ListenerInterface.blockInterfacesFromDatabase(this);
        this.m_strInfoServiceId = string2 == null ? InfoServiceDBEntry.generateId((ListenerInterface)this.m_listenerInterfaces.firstElement()) : string2;
        this.m_strName = string;
        if (this.m_strName == null) {
            ListenerInterface listenerInterface = (ListenerInterface)this.m_listenerInterfaces.firstElement();
            this.m_strName = listenerInterface.getHost() + ":" + Integer.toString(listenerInterface.getPort());
        }
        this.m_bPrimaryForwarderList = bl;
        this.m_infoserviceSoftware = serviceSoftware == null ? new ServiceSoftware("unknown") : serviceSoftware;
        this.m_preferedListenerInterface = 0;
        this.m_creationTimeStamp = l;
        this.m_serial = l2;
        this.m_neighbour = false;
        this.m_bPerfServerEnabled = bl3;
        this.m_xmlDescription = this.generateXmlRepresentation();
    }

    public boolean isPersistanceDeletionAllowed() {
        return XMLUtil.getStorageMode() == 2;
    }

    public void deletePersistence() {
        if (this.isPersistanceDeletionAllowed()) {
            this.m_xmlDescription = null;
        }
    }

    public static void setUseInfoServiceStatistics(boolean bl) {
        ms_bUseStatistics = bl;
    }

    public static boolean isInfoServiceStatisticsUsed() {
        return ms_bUseStatistics;
    }

    public static void setConnectionTimeout(int n) {
        if (n >= 10000) {
            m_getXmlConnectionTimeout = n;
        }
    }

    public static int getConnectionTimeout() {
        return m_getXmlConnectionTimeout;
    }

    public static void setBrowserIdentification(IBrowserIdentification iBrowserIdentification) {
        if (iBrowserIdentification != null) {
            ms_browserIdentification = iBrowserIdentification;
        }
    }

    public static void setMutableProxyInterface(IMutableProxyInterface iMutableProxyInterface) {
        if (iMutableProxyInterface != null) {
            ms_proxyInterface = iMutableProxyInterface;
        }
    }

    public static void setJVMNetworkErrorHandling(Runnable runnable, long l) {
        if (l < 0L || runnable == null) {
            throw new IllegalArgumentException("Runnable: " + runnable + " " + "Timeout: " + l);
        }
        m_threadHandleAfterJVMSocketError = runnable;
        m_timeHandleAfterJVMSocketError = l;
    }

    private Element generateXmlRepresentation() {
        Element element;
        Object object;
        Document document = XMLUtil.createDocument();
        Element element2 = document.createElement(XML_ELEMENT_NAME);
        XMLUtil.setAttribute(element2, "id", this.m_strInfoServiceId);
        XMLUtil.setAttribute(element2, "serial", this.m_serial);
        Element element3 = document.createElement("Name");
        XMLUtil.setValue((Node)element3, this.m_strName);
        Element element4 = document.createElement("Network");
        Element element5 = document.createElement("ListenerInterfaces");
        Enumeration enumeration = this.m_listenerInterfaces.elements();
        while (enumeration.hasMoreElements()) {
            object = (ListenerInterface)enumeration.nextElement();
            element5.appendChild(((ListenerInterface)object).toXmlElement(document));
        }
        element4.appendChild(element5);
        object = document.createElement("LastUpdate");
        XMLUtil.setValue((Node)object, this.m_creationTimeStamp);
        element2.appendChild(element3);
        element2.appendChild(this.m_infoserviceSoftware.toXmlElement(document));
        element2.appendChild(element4);
        element2.appendChild((Node)object);
        if (this.m_bPrimaryForwarderList) {
            element = document.createElement("ForwarderList");
            element2.appendChild(element);
        }
        if (this.m_userDefined) {
            element = document.createElement("UserDefined");
            element2.appendChild(element);
        }
        if (this.m_bPerfServerEnabled) {
            element = document.createElement("PerformanceServer");
            element2.appendChild(element);
        }
        try {
            this.m_signature = SignatureCreator.getInstance().getSignedXml(2, element2);
        }
        catch (Exception exception) {
            LogHolder.log(2, LogType.MISC, "Document could not be signed!");
        }
        return element2;
    }

    public String getId() {
        return this.m_strInfoServiceId;
    }

    public Element getXmlStructure() {
        return this.m_xmlDescription;
    }

    public String getName() {
        return this.m_strName;
    }

    public boolean isVerified() {
        if (this.m_signature != null) {
            return this.m_signature.isVerified();
        }
        return false;
    }

    public boolean isValid() {
        if (this.m_signature != null && this.m_signature.getMultiCertPath() != null) {
            return this.m_signature.getMultiCertPath().isValid(new Date());
        }
        return false;
    }

    public boolean isPerfServerEnabled() {
        return this.m_bPerfServerEnabled;
    }

    public boolean checkId() {
        return this.m_userDefined || super.checkId();
    }

    public XMLSignature getSignature() {
        return this.m_signature;
    }

    public MultiCertPath getCertPath() {
        if (this.m_signature != null) {
            return this.m_signature.getMultiCertPath();
        }
        return null;
    }

    public long getLastUpdate() {
        return this.m_creationTimeStamp;
    }

    public long getVersionNumber() {
        return this.m_serial;
    }

    public boolean hasPrimaryForwarderList() {
        return this.m_bPrimaryForwarderList;
    }

    public Vector getListenerInterfaces() {
        return (Vector)this.m_listenerInterfaces.clone();
    }

    public ListenerInterface getListenerInterface(String string) {
        return (ListenerInterface)this.m_hashListenerInterfaces.get(string);
    }

    public boolean isUserDefined() {
        return this.m_userDefined;
    }

    public void setUserDefined(boolean bl) {
        this.m_userDefined = bl;
        this.m_infoserviceSoftware = new ServiceSoftware("unknown");
        this.m_xmlDescription = this.generateXmlRepresentation();
    }

    public void markAsBootstrap() {
        this.m_bTemp = true;
    }

    public boolean isBootstrap() {
        return this.m_bTemp;
    }

    public String toString() {
        return this.m_strName;
    }

    public boolean equals(Object object) {
        boolean bl = false;
        if (object != null && object instanceof InfoServiceDBEntry) {
            bl = this.getId().equals(((InfoServiceDBEntry)object).getId());
        }
        return bl;
    }

    public int hashCode() {
        return this.getId().hashCode();
    }

    public String getPostFile() {
        return "/infoservice";
    }

    public boolean isNeighbour() {
        return this.m_neighbour;
    }

    public void setNeighbour(boolean bl) {
        this.m_neighbour = bl;
    }

    private HTTPConnectionDescriptor getNextConnectionDescriptor(HTTPConnectionDescriptor hTTPConnectionDescriptor, ImmutableProxyInterface immutableProxyInterface, int n, boolean bl, int n2, Integer n3) {
        Vector<String> vector;
        int n4 = n2;
        if (hTTPConnectionDescriptor != null) {
            int n5 = this.m_listenerInterfaces.indexOf(hTTPConnectionDescriptor.getTargetInterface());
            if (n5 < 0) {
                LogHolder.log(1, LogType.NET, "Current target interfaces not found in list of interfaces!");
                n5 = 0;
            }
            n4 = bl ? n5 : (n5 + 1) % this.m_listenerInterfaces.size();
        }
        ListenerInterface listenerInterface = (ListenerInterface)this.m_listenerInterfaces.elementAt(n4);
        Vector vector2 = null;
        if (ms_bUseStatistics && PROXY_FORCE_ANONYMOUS != n3) {
            try {
                vector2 = new Vector();
                vector = new Vector();
                vector.addElement("java.version");
                vector.addElement("java.vm.vendor");
                InfoServiceDBEntry.addPropertyHeader(vector, vector2);
                vector = new Vector<String>();
                vector.addElement("os.name");
                InfoServiceDBEntry.addPropertyHeader(vector, vector2);
                InfoServiceDBEntry.addPropertyHeader("anonlib.version", "00.20.001", vector2);
                String string = ms_browserIdentification.getBrowserName();
                if (string != null) {
                    InfoServiceDBEntry.addPropertyHeader("browser.name", string, vector2);
                }
            }
            catch (Exception exception) {
                LogHolder.log(2, LogType.NET, exception);
            }
        }
        vector = HTTPConnectionFactory.getInstance().createHTTPConnection(listenerInterface, immutableProxyInterface, n, true, vector2);
        return new HTTPConnectionDescriptor((HTTPConnection)((Object)vector), listenerInterface);
    }

    private static void addPropertyHeader(String string, Vector vector) {
        InfoServiceDBEntry.addPropertyHeader(string, null, vector);
    }

    private static void addPropertyHeader(String string, String string2, Vector vector) {
        InfoServiceDBEntry.addPropertyHeader(Util.toVector(string), string2, vector);
    }

    private static void addPropertyHeader(Vector vector, Vector vector2) {
        InfoServiceDBEntry.addPropertyHeader(vector, null, vector2);
    }

    private static void addPropertyHeader(Vector vector, String string, Vector vector2) {
        if (vector == null || vector.size() == 0 || vector2 == null || vector.elementAt(0) == null) {
            return;
        }
        String string2 = null;
        if (string == null) {
            string = "";
            for (int i = 0; i < vector.size(); ++i) {
                String string3;
                block15: {
                    string3 = (String)vector.elementAt(i);
                    if (string3 == null || string3.trim().length() == 0) continue;
                    try {
                        String string4 = System.getProperty(string3);
                        if (string4 == null || string4.trim().equals("Sun Microsystems Inc.")) break block15;
                        string = string + string4.trim();
                        if (!string3.equals("java.version")) break block15;
                        if (string.indexOf("_") > 0) {
                            string = string.substring(0, string.indexOf("_"));
                        }
                        if (string.indexOf("-") <= 0) break block15;
                        string = string.substring(0, string.indexOf("-"));
                    }
                    catch (Exception exception) {
                        continue;
                    }
                }
                if (i + 1 < vector.size()) {
                    string = string + " / ";
                }
                string3 = Util.replaceAll(string3, ".", "-").trim();
                if (string2 == null) {
                    string2 = string3;
                    continue;
                }
                while (string2.length() > 0 && !string3.startsWith(string2)) {
                    int n = string2.lastIndexOf("-");
                    if (n < 0) {
                        string2 = "";
                        continue;
                    }
                    string2 = string2.substring(0, n);
                }
            }
            if (string.trim().length() == 0) {
                return;
            }
        } else {
            string2 = Util.replaceAll((String)vector.elementAt(0), ".", "-").trim();
        }
        if (string != null) {
            StringTokenizer stringTokenizer = new StringTokenizer(string, "\r\n");
            if (!stringTokenizer.hasMoreTokens()) {
                return;
            }
            string = stringTokenizer.nextToken();
            if (string2.length() > 0) {
                string2 = "-" + string2;
            }
            vector2.addElement(new NVPair(HEADER_STATISTICS + string2, string));
        }
    }

    private Document getXmlDocument(HttpRequestStructure httpRequestStructure) throws Exception {
        return this.getXmlDocument(httpRequestStructure, PROXY_AUTO);
    }

    private Document getXmlDocument(HttpRequestStructure httpRequestStructure, Integer n) throws Exception {
        return this.getXmlDocument(httpRequestStructure, 1, n);
    }

    private Document getXmlDocument(HttpRequestStructure httpRequestStructure, int n) throws Exception {
        return this.getXmlDocument(httpRequestStructure, n, PROXY_AUTO);
    }

    private Document getXmlDocument(HttpRequestStructure httpRequestStructure, int n, Integer n2) throws Exception {
        byte[] arrby = this.doHttpRequest(httpRequestStructure, n, n2);
        if (arrby == null) {
            return null;
        }
        return XMLUtil.toXMLDocument(arrby);
    }

    /*
     * Exception decompiling
     */
    private byte[] doHttpRequest(HttpRequestStructure var1_1, int var2_2, Integer var3_3) throws Exception {
        /*
         * This method has failed to decompile.  When submitting a bug report, please provide this stack trace, and (if you hold appropriate legal rights) the relevant class file.
         * org.benf.cfr.reader.util.ConfusedCFRException: Tried to end blocks [1[TRYBLOCK], 3[TRYBLOCK]], but top level block is 5[TRYBLOCK]
         * org.benf.cfr.reader.bytecode.analysis.opgraph.Op04StructuredStatement.processEndingBlocks(Op04StructuredStatement.java:429)
         * org.benf.cfr.reader.bytecode.analysis.opgraph.Op04StructuredStatement.buildNestedBlocks(Op04StructuredStatement.java:478)
         * org.benf.cfr.reader.bytecode.analysis.opgraph.Op03SimpleStatement.createInitialStructuredBlock(Op03SimpleStatement.java:728)
         * org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysisInner(CodeAnalyser.java:806)
         * org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysisOrWrapFail(CodeAnalyser.java:258)
         * org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysis(CodeAnalyser.java:192)
         * org.benf.cfr.reader.entities.attributes.AttributeCode.analyse(AttributeCode.java:94)
         * org.benf.cfr.reader.entities.Method.analyse(Method.java:521)
         * org.benf.cfr.reader.entities.ClassFile.analyseMid(ClassFile.java:1035)
         * org.benf.cfr.reader.entities.ClassFile.analyseTop(ClassFile.java:922)
         * org.benf.cfr.reader.Driver.doJarVersionTypes(Driver.java:253)
         * org.benf.cfr.reader.Driver.doJar(Driver.java:135)
         * org.benf.cfr.reader.CfrDriverImpl.analyse(CfrDriverImpl.java:65)
         * org.benf.cfr.reader.Main.main(Main.java:49)
         */
        throw new IllegalStateException(Decompilation failed);
    }

    public MixCascade getMixCascadeInfo(String string) throws Exception {
        Document document = this.getXmlDocument(HttpRequestStructure.createGetRequest("/cascadeinfo/" + string), 1);
        if (document == null) {
            return null;
        }
        Element element = document.getDocumentElement();
        if (!SignatureVerifier.getInstance().verifyXml(element, 1)) {
            throw new Exception("Cannot verify the signature for MixCascade entry: " + XMLUtil.toString(element));
        }
        return new MixCascade(element, Long.MAX_VALUE);
    }

    public Hashtable getMixCascades() throws Exception {
        return this.getMixCascades(true);
    }

    public Hashtable getPaymentInstances() throws Exception {
        return this.getPaymentInstances(true);
    }

    public PaymentInstanceDBEntry getPaymentInstance(String string) throws Exception {
        HttpRequestStructure httpRequestStructure = HttpRequestStructure.createGetRequest("/paymentinstance/" + string);
        Document document = this.getXmlDocument(httpRequestStructure);
        if (document == null) {
            return null;
        }
        PaymentInstanceDBEntry paymentInstanceDBEntry = new PaymentInstanceDBEntry(document.getDocumentElement());
        if (!paymentInstanceDBEntry.isVerified()) {
            throw new SignatureException("Document could not be verified!");
        }
        return paymentInstanceDBEntry;
    }

    private Hashtable getEntries(EntryGetter entryGetter) throws ExpiredSignatureException, SignatureException, Exception {
        Document document = this.getXmlDocument(HttpRequestStructure.createGetRequest(entryGetter.m_postFile), 1);
        if (document == null) {
            return new Hashtable();
        }
        Object object = SignatureVerifier.getInstance().getVerifiedXml(document.getDocumentElement(), 2);
        boolean bl = false;
        if (SignatureVerifier.getInstance().isCheckSignatures() && (object == null || !((XMLSignature)object).isVerified() || (bl = !((XMLSignature)object).getMultiCertPath().isValid(new Date())))) {
            if (bl) {
                throw new ExpiredSignatureException("Document signature validity has expired for InfoService " + this.getId() + "!");
            }
            throw new SignatureException("Document could not be verified for InfoService " + this.getId() + "!");
        }
        object = document.getElementsByTagName(XMLUtil.getXmlElementContainerName(entryGetter.m_dbEntryClass));
        if (object.getLength() == 0) {
            throw new XMLParseException(XMLUtil.getXmlElementContainerName(entryGetter.m_dbEntryClass), "Error in XML structure.");
        }
        Element element = (Element)object.item(0);
        NodeList nodeList = element.getElementsByTagName(XMLUtil.getXmlElementName(entryGetter.m_dbEntryClass));
        Hashtable<String, InfoServiceDBEntry> hashtable = new Hashtable<String, InfoServiceDBEntry>();
        for (int i = 0; i < nodeList.getLength(); ++i) {
            Element element2 = (Element)nodeList.item(i);
            try {
                AbstractDistributableCertifiedDatabaseEntry abstractDistributableCertifiedDatabaseEntry = entryGetter.m_dbEntryClass == (class$anon$infoservice$InfoServiceDBEntry == null ? InfoServiceDBEntry.class$("anon.infoservice.InfoServiceDBEntry") : class$anon$infoservice$InfoServiceDBEntry) ? new InfoServiceDBEntry(element2, entryGetter.m_bJAPContext ? Long.MAX_VALUE : 0L) : (entryGetter.m_dbEntryClass == (class$anon$infoservice$MixCascade == null ? InfoServiceDBEntry.class$("anon.infoservice.MixCascade") : class$anon$infoservice$MixCascade) ? (entryGetter.m_bJAPContext ? new MixCascade(element2, Long.MAX_VALUE) : new MixCascade(element2)) : (entryGetter.m_dbEntryClass == (class$anon$pay$PaymentInstanceDBEntry == null ? InfoServiceDBEntry.class$("anon.pay.PaymentInstanceDBEntry") : class$anon$pay$PaymentInstanceDBEntry) ? (entryGetter.m_bJAPContext ? new PaymentInstanceDBEntry(element2, Long.MAX_VALUE) : new PaymentInstanceDBEntry(element2)) : (entryGetter.m_bJAPContext ? new MixInfo(element2, Long.MAX_VALUE, false) : new MixInfo(element2))));
                if (((AbstractDistributableCertifiedDatabaseEntry)abstractDistributableCertifiedDatabaseEntry).isVerified() || !SignatureVerifier.getInstance().isCheckSignatures()) {
                    hashtable.put(((AbstractDatabaseEntry)abstractDistributableCertifiedDatabaseEntry).getId(), (InfoServiceDBEntry)abstractDistributableCertifiedDatabaseEntry);
                    continue;
                }
                String string = XMLUtil.parseAttribute((Node)element2, "id", null);
                if (string == null) {
                    string = XMLUtil.toString(element2);
                }
                LogHolder.log(4, LogType.MISC, "Cannot verify the signature for " + ClassUtil.getShortClassName(entryGetter.m_dbEntryClass) + " entry: " + string);
                continue;
            }
            catch (Exception exception) {
                LogHolder.log(3, LogType.MISC, "Error in " + ClassUtil.getShortClassName(entryGetter.m_dbEntryClass) + " XML node!", exception);
            }
        }
        return hashtable;
    }

    public Hashtable getInfoServices(boolean bl) throws Exception {
        EntryGetter entryGetter = new EntryGetter();
        entryGetter.m_bJAPContext = bl;
        entryGetter.m_dbEntryClass = class$anon$infoservice$InfoServiceDBEntry == null ? (class$anon$infoservice$InfoServiceDBEntry = InfoServiceDBEntry.class$("anon.infoservice.InfoServiceDBEntry")) : class$anon$infoservice$InfoServiceDBEntry;
        entryGetter.m_postFile = "/infoservices";
        return this.getEntries(entryGetter);
    }

    public Hashtable getMixCascades(boolean bl) throws Exception {
        EntryGetter entryGetter = new EntryGetter();
        entryGetter.m_bJAPContext = bl;
        entryGetter.m_dbEntryClass = class$anon$infoservice$MixCascade == null ? (class$anon$infoservice$MixCascade = InfoServiceDBEntry.class$("anon.infoservice.MixCascade")) : class$anon$infoservice$MixCascade;
        entryGetter.m_postFile = "/cascades";
        return this.getEntries(entryGetter);
    }

    public TermsAndConditionsTemplate getTCTemplate(String string) throws Exception {
        Document document = this.getXmlDocument(HttpRequestStructure.createGetRequest("/tctemplate/" + string), 1);
        if (document == null) {
            return null;
        }
        NodeList nodeList = document.getElementsByTagName(TermsAndConditionsTemplate.XML_ELEMENT_NAME);
        if (nodeList.getLength() == 0) {
            throw new Exception("Error in XML structure for mix with ID " + string);
        }
        Element element = (Element)nodeList.item(0);
        TermsAndConditionsTemplate termsAndConditionsTemplate = new TermsAndConditionsTemplate(element);
        if (!termsAndConditionsTemplate.isVerified()) {
            throw new Exception("Cannot verify the signature for Mix entry: " + XMLUtil.toString(element));
        }
        return termsAndConditionsTemplate;
    }

    public Hashtable getPaymentInstances(boolean bl) throws Exception {
        EntryGetter entryGetter = new EntryGetter();
        entryGetter.m_bJAPContext = bl;
        entryGetter.m_dbEntryClass = class$anon$pay$PaymentInstanceDBEntry == null ? (class$anon$pay$PaymentInstanceDBEntry = InfoServiceDBEntry.class$("anon.pay.PaymentInstanceDBEntry")) : class$anon$pay$PaymentInstanceDBEntry;
        entryGetter.m_postFile = "/paymentinstances";
        return this.getEntries(entryGetter);
    }

    public Hashtable getMixes(boolean bl) throws Exception {
        EntryGetter entryGetter = new EntryGetter();
        entryGetter.m_bJAPContext = bl;
        entryGetter.m_dbEntryClass = class$anon$infoservice$MixInfo == null ? (class$anon$infoservice$MixInfo = InfoServiceDBEntry.class$("anon.infoservice.MixInfo")) : class$anon$infoservice$MixInfo;
        entryGetter.m_postFile = "/mixes";
        return this.getEntries(entryGetter);
    }

    public Hashtable getInfoServices() throws Exception {
        return this.getInfoServices(true);
    }

    public InetAddress getMyIP(Integer n) throws Exception {
        Document document = this.getXmlDocument(HttpRequestStructure.createGetRequest("/echoip"), n);
        if (document == null) {
            return null;
        }
        String string = XMLUtil.parseValue(XMLUtil.getFirstChildByName(document.getDocumentElement(), "IP"), null);
        if (string == null) {
            throw new UnknownHostException("null");
        }
        try {
            return InetAddress.getByName(string);
        }
        catch (UnknownHostException unknownHostException) {
            LogHolder.log(3, LogType.NET, unknownHostException);
            throw unknownHostException;
        }
    }

    public Hashtable getMixCascadeSerials() throws Exception {
        Document document = this.getXmlDocument(HttpRequestStructure.createGetRequest("/cascadeserials"), 1);
        if (document == null) {
            return new Hashtable();
        }
        if (!SignatureVerifier.getInstance().verifyXml(document, 2)) {
            throw new SignatureException("Cannot verify the signature: " + XMLUtil.toString(document));
        }
        return new AbstractDistributableDatabaseEntry.Serials(class$anon$infoservice$MixCascade == null ? (class$anon$infoservice$MixCascade = InfoServiceDBEntry.class$("anon.infoservice.MixCascade")) : class$anon$infoservice$MixCascade).parse(document.getDocumentElement());
    }

    public Hashtable getInfoServiceSerials() throws Exception {
        Document document = this.getXmlDocument(HttpRequestStructure.createGetRequest("/infoserviceserials"), 1);
        if (document == null) {
            return new Hashtable();
        }
        if (!SignatureVerifier.getInstance().verifyXml(document, 2)) {
            throw new SignatureException("Cannot verify the signature: " + XMLUtil.toString(document));
        }
        return new AbstractDistributableDatabaseEntry.Serials(class$anon$infoservice$InfoServiceDBEntry == null ? (class$anon$infoservice$InfoServiceDBEntry = InfoServiceDBEntry.class$("anon.infoservice.InfoServiceDBEntry")) : class$anon$infoservice$InfoServiceDBEntry).parse(document.getDocumentElement());
    }

    public MixInfo getMixInfo(String string) throws Exception {
        Document document = this.getXmlDocument(HttpRequestStructure.createGetRequest("/mixinfo/" + string));
        if (document == null) {
            return null;
        }
        NodeList nodeList = document.getElementsByTagName("Mix");
        if (nodeList.getLength() == 0) {
            throw new Exception("Error in XML structure for mix with ID " + string);
        }
        Element element = (Element)nodeList.item(0);
        MixInfo mixInfo = new MixInfo(element, Long.MAX_VALUE, false);
        if (!mixInfo.isVerified()) {
            throw new Exception("Cannot verify the signature for Mix entry: " + XMLUtil.toString(element));
        }
        return mixInfo;
    }

    public StatusInfo getStatusInfo(MixCascade mixCascade) throws Exception {
        return this.getStatusInfo(mixCascade, -1L);
    }

    public Hashtable getExitAddresses() throws Exception {
        Hashtable<String, MixCascadeExitAddresses> hashtable = new Hashtable<String, MixCascadeExitAddresses>();
        Document document = this.getXmlDocument(HttpRequestStructure.createGetRequest("/exitaddresses"));
        if (document == null) {
            return null;
        }
        Element element = document.getDocumentElement();
        Node node = XMLUtil.getFirstChildByName(element, "ExitAddresses");
        Node node2 = null;
        String string = null;
        String string2 = null;
        int n = XMLUtil.parseAttribute(node, "distribution", 6);
        while (node != null) {
            string = XMLUtil.parseAttribute(node, "id", "");
            if (!string.equals("")) {
                MixCascade mixCascade = (MixCascade)Database.getInstance(class$anon$infoservice$MixCascade == null ? InfoServiceDBEntry.class$("anon.infoservice.MixCascade") : class$anon$infoservice$MixCascade).getEntryById(string);
                if (mixCascade != null) {
                    n = mixCascade.getDistribution();
                }
                node2 = XMLUtil.getFirstChildByName(node, "ExitAddress");
                while (node2 != null) {
                    MixCascadeExitAddresses mixCascadeExitAddresses;
                    string2 = XMLUtil.parseValue(node2, "");
                    if (!string2.equals("") && (mixCascadeExitAddresses = MixCascadeExitAddresses.addInetAddress(string, InetAddress.getByName(string2), n, this.getId())) != null) {
                        hashtable.put(mixCascadeExitAddresses.getId(), mixCascadeExitAddresses);
                    }
                    node2 = XMLUtil.getNextSiblingByName(node2, "ExitAddress");
                }
            }
            node = XMLUtil.getNextSiblingByName(node, "ExitAddresses");
        }
        return hashtable;
    }

    public StatusInfo getStatusInfo(MixCascade mixCascade, long l) throws Exception {
        Document document = this.getXmlDocument(HttpRequestStructure.createGetRequest("/mixcascadestatus/" + mixCascade.getId()));
        if (document == null) {
            return null;
        }
        Element element = document.getDocumentElement();
        if (element == null) {
            return null;
        }
        NodeList nodeList = document.getElementsByTagName("MixCascadeStatus");
        if (nodeList.getLength() == 0) {
            if (XMLUtil.getFirstChildByName(document, "HTML") == null) {
                throw new Exception("Error in XML structure for cascade with ID " + mixCascade.getId() + ".\n" + XMLUtil.toString(document));
            }
            throw new Exception("No status data found for cascade with ID " + mixCascade.getId());
        }
        Element element2 = (Element)nodeList.item(0);
        StatusInfo statusInfo = l > 0L ? new StatusInfo(element2, l) : new StatusInfo(element2);
        if (SignatureVerifier.getInstance().isCheckSignatures() && !statusInfo.isVerified()) {
            throw new Exception("Cannot verify the signature for MixCascadeStatus entry: " + XMLUtil.toString(element2));
        }
        return statusInfo;
    }

    public JAPMinVersion getNewVersionNumber() throws Exception {
        Document document = this.getXmlDocument(HttpRequestStructure.createGetRequest("/currentjapversion"));
        if (document == null) {
            return null;
        }
        Element element = (Element)XMLUtil.getFirstChildByName(document, JAPMinVersion.getXmlElementName());
        if (!SignatureVerifier.getInstance().verifyXml(element, 3)) {
            throw new Exception("Cannot verify the signature for JAPMinVersion entry: " + XMLUtil.toString(element));
        }
        return new JAPMinVersion(element);
    }

    private Hashtable getUpdateEntries(Class class_, boolean bl) throws Exception {
        Document document = bl ? this.getXmlDocument(HttpRequestStructure.createGetRequest(AbstractDistributableDatabaseEntry.getHttpSerialsRequestString(class_))) : this.getXmlDocument(HttpRequestStructure.createGetRequest(AbstractDistributableDatabaseEntry.getHttpRequestString(class_)));
        if (document == null) {
            return new Hashtable();
        }
        if (!SignatureVerifier.getInstance().verifyXml(document.getDocumentElement(), 2)) {
            LogHolder.log(6, LogType.MISC, "Cannot verify the signature for " + class_.getName() + " document: " + XMLUtil.toString(document));
            return new Hashtable();
        }
        if (bl) {
            return new AbstractDistributableDatabaseEntry.Serials(class_).parse(document.getDocumentElement());
        }
        Node node = XMLUtil.getFirstChildByName(document, XMLUtil.getXmlElementContainerName(class_));
        if (node == null || !(node instanceof Element)) {
            throw new XMLParseException(XMLUtil.getXmlElementContainerName(class_), "Node missing!");
        }
        NodeList nodeList = ((Element)node).getElementsByTagName(XMLUtil.getXmlElementName(class_));
        Hashtable<String, AbstractDistributableDatabaseEntry> hashtable = new Hashtable<String, AbstractDistributableDatabaseEntry>();
        for (int i = 0; i < nodeList.getLength(); ++i) {
            Element element = (Element)nodeList.item(i);
            if (SignatureVerifier.getInstance().verifyXml(element, 3)) {
                try {
                    AbstractDistributableDatabaseEntry abstractDistributableDatabaseEntry = (AbstractDistributableDatabaseEntry)class_.getConstructor(class$org$w3c$dom$Element == null ? InfoServiceDBEntry.class$("org.w3c.dom.Element") : class$org$w3c$dom$Element).newInstance(element);
                    hashtable.put(((AbstractDatabaseEntry)abstractDistributableDatabaseEntry).getId(), abstractDistributableDatabaseEntry);
                }
                catch (Exception exception) {
                    LogHolder.log(2, LogType.MISC, "Error in " + class_.getName() + " XML node.");
                }
                continue;
            }
            LogHolder.log(6, LogType.MISC, "Cannot verify the signature for " + class_.getName() + " entry: " + XMLUtil.toString(element));
        }
        return hashtable;
    }

    public Hashtable getMessages() throws Exception {
        return this.getUpdateEntries(class$anon$infoservice$MessageDBEntry == null ? (class$anon$infoservice$MessageDBEntry = InfoServiceDBEntry.class$("anon.infoservice.MessageDBEntry")) : class$anon$infoservice$MessageDBEntry, false);
    }

    public Hashtable getMessageSerials() throws Exception {
        return this.getUpdateEntries(class$anon$infoservice$MessageDBEntry == null ? (class$anon$infoservice$MessageDBEntry = InfoServiceDBEntry.class$("anon.infoservice.MessageDBEntry")) : class$anon$infoservice$MessageDBEntry, true);
    }

    public PerformanceInfo getPerformanceInfo() throws Exception {
        Document document = this.getXmlDocument(HttpRequestStructure.createGetRequest("/performanceinfo"), 1);
        if (document == null) {
            return null;
        }
        Element element = (Element)XMLUtil.getFirstChildByName(document, "PerformanceInfo");
        PerformanceInfo performanceInfo = new PerformanceInfo(element);
        if (SignatureVerifier.getInstance().isCheckSignatures() && !performanceInfo.isVerified()) {
            throw new SignatureException("Document could not be verified!");
        }
        return performanceInfo;
    }

    public Hashtable getLatestJava() throws Exception {
        return this.getUpdateEntries(class$anon$infoservice$JavaVersionDBEntry == null ? (class$anon$infoservice$JavaVersionDBEntry = InfoServiceDBEntry.class$("anon.infoservice.JavaVersionDBEntry")) : class$anon$infoservice$JavaVersionDBEntry, false);
    }

    public Hashtable getLatestJavaSerials() throws Exception {
        return this.getUpdateEntries(class$anon$infoservice$JavaVersionDBEntry == null ? (class$anon$infoservice$JavaVersionDBEntry = InfoServiceDBEntry.class$("anon.infoservice.JavaVersionDBEntry")) : class$anon$infoservice$JavaVersionDBEntry, true);
    }

    public JAPVersionInfo getJAPVersionInfo(int n) throws Exception {
        Document document = null;
        if (n == 1) {
            document = this.getXmlDocument(HttpRequestStructure.createGetRequest("/japRelease.jnlp"));
        } else if (n == 2) {
            document = this.getXmlDocument(HttpRequestStructure.createGetRequest("/japDevelopment.jnlp"));
        } else {
            throw new Exception("InfoServiceDBEntry: getJAPVersionInfo: Invalid version info requested.");
        }
        if (document == null) {
            return null;
        }
        Element element = (Element)XMLUtil.getFirstChildByName(document, JAPVersionInfo.getXmlElementName());
        XMLSignature xMLSignature = SignatureVerifier.getInstance().getVerifiedXml(element, 3);
        if (!xMLSignature.isVerified()) {
            throw new Exception("Cannot verify the signature for JAPVersionInfo entry: " + XMLUtil.toString(element));
        }
        JAPVersionInfo jAPVersionInfo = new JAPVersionInfo(element, n);
        if (!xMLSignature.getMultiCertPath().isValid(new Date())) {
            LogHolder.log(4, LogType.MISC, "Found an expired JAP/JonDo update entry for " + jAPVersionInfo.getPostFile() + "! The update verification certificate might become invalid soon.");
        }
        return jAPVersionInfo;
    }

    public byte[] getTorNodesList() throws Exception {
        byte[] arrby = null;
        try {
            arrby = this.doHttpRequest(HttpRequestStructure.createGetRequest("/tornodes"), 1, PROXY_AUTO);
        }
        catch (Exception exception) {
            // empty catch block
        }
        if (arrby == null) {
            try {
                arrby = this.doHttpRequest(HttpRequestStructure.createGetRequest("/tornodes"), 1, PROXY_AUTO);
            }
            catch (Exception exception) {
                // empty catch block
            }
        }
        if (arrby == null) {
            throw new Exception("Error while parsing the TOR nodes list XML structure.");
        }
        return arrby;
    }

    public byte[] getMixminionNodesList() throws Exception {
        byte[] arrby = null;
        try {
            arrby = this.doHttpRequest(HttpRequestStructure.createGetRequest("/mixminionnodes"), 1, PROXY_AUTO);
        }
        catch (Exception exception) {
            // empty catch block
        }
        if (arrby == null) {
            throw new Exception("Error while parsing the TOR nodes list XML structure.");
        }
        return arrby;
    }

    public Element postNewForwarder(Element element) throws Exception {
        if (!this.hasPrimaryForwarderList()) {
            throw new Exception("InfoService: postNewForwarder: The InfoService " + this.getName() + " has no forwarder list.");
        }
        Document document = this.getXmlDocument(HttpRequestStructure.createPostRequest("/addforwarder", element.getOwnerDocument()));
        if (document == null) {
            return null;
        }
        NodeList nodeList = document.getElementsByTagName("JapForwarder");
        if (nodeList.getLength() == 0) {
            throw new Exception("InfoService: postNewForwarder: Error in XML structure.");
        }
        return (Element)nodeList.item(0);
    }

    public Element postRenewForwarder(Element element) throws Exception {
        if (!this.hasPrimaryForwarderList()) {
            throw new Exception("InfoService: postRenewForwarder: The InfoService " + this.getName() + " has no forwarder list.");
        }
        Document document = this.getXmlDocument(HttpRequestStructure.createPostRequest("/renewforwarder", element.getOwnerDocument()));
        if (document == null) {
            return null;
        }
        NodeList nodeList = document.getElementsByTagName("JapForwarder");
        if (nodeList.getLength() == 0) {
            throw new Exception("InfoService: postRenewForwarder: Error in XML structure.");
        }
        return (Element)nodeList.item(0);
    }

    public Element getForwarder() throws Exception {
        Document document = this.getXmlDocument(HttpRequestStructure.createGetRequest("/getforwarder"));
        if (document == null) {
            return null;
        }
        NodeList nodeList = document.getElementsByTagName("JapForwarder");
        if (nodeList.getLength() == 0) {
            throw new Exception("InfoService: getForwarder: Error in XML structure.");
        }
        Element element = (Element)nodeList.item(0);
        NodeList nodeList2 = element.getElementsByTagName("ErrorInformation");
        if (nodeList2.getLength() > 0) {
            Element element2 = (Element)nodeList2.item(0);
            throw new Exception("InfoService: getForwarder: The infoservice returned error " + element2.getAttribute("code") + ": " + element2.getFirstChild().getNodeValue());
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

    static {
        ms_bUseStatistics = true;
        ms_proxyInterface = new IMutableProxyInterface.DummyMutableProxyInterface();
        ms_browserIdentification = new IBrowserIdentification(){

            public String getBrowserName() {
                return null;
            }
        };
        PROXY_AUTO = new Integer(0);
        PROXY_FORCE_ANONYMOUS = new Integer(1);
        PROXY_FORCE_DEFAULT = new Integer(2);
        PROXY_FORCE_DIRECT = new Integer(3);
    }

    private static class EntryGetter {
        String m_postFile;
        Class m_dbEntryClass;
        boolean m_bJAPContext;

        private EntryGetter() {
        }
    }
}

