/*
 * Decompiled with CFR 0.150.
 */
package anon.infoservice;

import anon.client.AbstractAutoSwitchedMixCascadeContainer;
import anon.crypto.ExpiredSignatureException;
import anon.crypto.SignatureVerifier;
import anon.infoservice.AbstractDatabaseEntry;
import anon.infoservice.AbstractDistributableDatabaseEntry;
import anon.infoservice.Database;
import anon.infoservice.IServiceContextContainer;
import anon.infoservice.InfoServiceDBEntry;
import anon.infoservice.InfoServiceHolderMessage;
import anon.infoservice.JAPMinVersion;
import anon.infoservice.JAPVersionInfo;
import anon.infoservice.MixCascade;
import anon.infoservice.MixInfo;
import anon.infoservice.PerformanceInfo;
import anon.infoservice.StatusInfo;
import anon.pay.PaymentInstanceDBEntry;
import anon.terms.template.TermsAndConditionsTemplate;
import anon.util.ClassUtil;
import anon.util.IXMLEncodable;
import anon.util.ThreadPool;
import anon.util.Util;
import anon.util.XMLParseException;
import anon.util.XMLUtil;
import java.lang.reflect.Field;
import java.net.InetAddress;
import java.security.SignatureException;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Observable;
import java.util.Random;
import java.util.Vector;
import logging.LogHolder;
import logging.LogType;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class InfoServiceHolder
extends Observable
implements IXMLEncodable {
    public static final String XML_ELEMENT_NAME = "InfoserviceManagement";
    public static final String XML_ELEM_CHANGE_INFO_SERVICES = "ChangeInfoService";
    public static final int MAXIMUM_OF_ASKED_INFO_SERVICES = 4;
    public static final int MAXIMUM_OF_ASKED_INFO_SERVICES_FOR_PERFORMANCE = Integer.MAX_VALUE;
    public static final int DEFAULT_OF_ASKED_INFO_SERVICES = 3;
    private static final int GET_MIXCASCADES = 1;
    private static final int GET_INFOSERVICES = 2;
    private static final int GET_MIXINFO = 3;
    private static final int GET_STATUSINFO = 4;
    private static final int GET_NEWVERSIONNUMBER = 5;
    private static final int GET_JAPVERSIONINFO = 6;
    private static final int GET_TORNODESLIST = 7;
    private static final int GET_FORWARDER = 8;
    private static final int GET_PAYMENT_INSTANCES = 9;
    private static final int GET_PAYMENT_INSTANCE = 10;
    private static final int GET_MIXMINIONNODESLIST = 11;
    private static final int GET_CASCADEINFO = 12;
    private static final int GET_LATEST_JAVA = 13;
    private static final int GET_INFOSERVICE_SERIALS = 14;
    private static final int GET_MIXCASCADE_SERIALS = 15;
    private static final int GET_MESSAGES = 16;
    private static final int GET_LATEST_JAVA_SERIALS = 17;
    private static final int GET_MESSAGE_SERIALS = 18;
    private static final int GET_STATUSINFO_TIMEOUT = 19;
    private static final int GET_PERFORMANCE_INFO = 20;
    private static final int GET_TC_TEMPLATE = 21;
    private static final int GET_TCS = 22;
    private static final int GET_TC_SERIALS = 23;
    private static final int GET_EXIT_ADDRESSES = 24;
    private static final int GET_TC_TEMPLATES = 25;
    private static final int GET_MY_IP = 26;
    private static final int GET_MIXINFOS = 25;
    private static final String[] GETS;
    public static final boolean DEFAULT_INFOSERVICE_CHANGES = true;
    private static final String XML_ATTR_ASKED_INFO_SERVICES = "askInfoServices";
    private static InfoServiceHolder ms_infoServiceHolderInstance;
    private ThreadPool m_poolFetchInformation = new ThreadPool("Fetch Information Thread Pool", 6, 1);
    private InfoServiceDBEntry m_preferredInfoService = null;
    private boolean m_changeInfoServices = true;
    private int m_nrAskedInfoServices = 3;
    static /* synthetic */ Class class$anon$infoservice$InfoServiceHolder;
    static /* synthetic */ Class class$anon$infoservice$InfoServiceDBEntry;

    private InfoServiceHolder() {
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static InfoServiceHolder getInstance() {
        Class class_ = class$anon$infoservice$InfoServiceHolder == null ? (class$anon$infoservice$InfoServiceHolder = InfoServiceHolder.class$("anon.infoservice.InfoServiceHolder")) : class$anon$infoservice$InfoServiceHolder;
        synchronized (class_) {
            if (ms_infoServiceHolderInstance == null) {
                ms_infoServiceHolderInstance = new InfoServiceHolder();
            }
        }
        return ms_infoServiceHolderInstance;
    }

    public void shutdown() {
        this.m_poolFetchInformation.shutdown();
    }

    public static String getXmlSettingsRootNodeName() {
        return XML_ELEMENT_NAME;
    }

    public synchronized void setPreferredInfoService(InfoServiceDBEntry infoServiceDBEntry) {
        if (infoServiceDBEntry != null) {
            this.m_preferredInfoService = infoServiceDBEntry;
            this.setChanged();
            this.notifyObservers(new InfoServiceHolderMessage(1, this.m_preferredInfoService));
            LogHolder.log(6, LogType.NET, "Preferred InfoService is now: " + this.m_preferredInfoService.getName());
        }
    }

    public InfoServiceDBEntry getPreferredInfoService() {
        return this.m_preferredInfoService;
    }

    public int getNumberOfAskedInfoServices() {
        return this.m_nrAskedInfoServices;
    }

    public void setNumberOfAskedInfoServices(int n) {
        if (n < 1) {
            this.m_nrAskedInfoServices = 1;
        } else if (n > 4) {
            n = 4;
        } else {
            this.m_nrAskedInfoServices = n;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void setChangeInfoServices(boolean bl) {
        InfoServiceHolder infoServiceHolder = this;
        synchronized (infoServiceHolder) {
            if (this.m_changeInfoServices != bl) {
                this.m_changeInfoServices = bl;
                this.setChanged();
                this.notifyObservers(new InfoServiceHolderMessage(2, new Boolean(this.m_changeInfoServices)));
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public boolean isChangeInfoServices() {
        boolean bl = true;
        InfoServiceHolder infoServiceHolder = this;
        synchronized (infoServiceHolder) {
            bl = this.m_changeInfoServices;
        }
        return bl;
    }

    public Vector getInfoservicesWithForwarderList() {
        Vector<InfoServiceDBEntry> vector = new Vector<InfoServiceDBEntry>();
        InfoServiceDBEntry infoServiceDBEntry = this.getPreferredInfoService();
        if (infoServiceDBEntry.hasPrimaryForwarderList()) {
            vector.addElement(infoServiceDBEntry);
        }
        Enumeration enumeration = Database.getInstance(class$anon$infoservice$InfoServiceDBEntry == null ? (class$anon$infoservice$InfoServiceDBEntry = InfoServiceHolder.class$("anon.infoservice.InfoServiceDBEntry")) : class$anon$infoservice$InfoServiceDBEntry).getEntryList().elements();
        while (enumeration.hasMoreElements()) {
            InfoServiceDBEntry infoServiceDBEntry2 = (InfoServiceDBEntry)enumeration.nextElement();
            if (!infoServiceDBEntry2.hasPrimaryForwarderList() || infoServiceDBEntry2.getId().equals(infoServiceDBEntry.getId())) continue;
            vector.addElement(infoServiceDBEntry2);
        }
        return vector;
    }

    private Object fetchInformation(int n, Vector vector) {
        InformationFetcher informationFetcher = new InformationFetcher(n, vector);
        try {
            this.m_poolFetchInformation.addRequestAndWait(informationFetcher);
        }
        catch (InterruptedException interruptedException) {
            Thread.currentThread().interrupt();
            LogHolder.log(4, LogType.MISC, interruptedException);
        }
        return informationFetcher.getResult();
    }

    public Hashtable getMixCascades() {
        return (Hashtable)this.fetchInformation(1, null);
    }

    public Hashtable getMixCascades(String string) {
        if (string == null) {
            Hashtable hashtable = this.getMixCascades();
            if (hashtable == null) {
                return new Hashtable();
            }
            return this.getMixCascades();
        }
        Vector<String> vector = new Vector<String>();
        vector.addElement(string);
        return (Hashtable)this.fetchInformation(1, vector);
    }

    public Hashtable getMixCascadeSerials() {
        return (Hashtable)this.fetchInformation(15, null);
    }

    public Hashtable getMixCascadeSerials(String string) {
        if (string == null) {
            return this.getMixCascadeSerials();
        }
        Vector<String> vector = new Vector<String>();
        vector.addElement(string);
        return (Hashtable)this.fetchInformation(15, vector);
    }

    public TermsAndConditionsTemplate getTCTemplate(String string) {
        return (TermsAndConditionsTemplate)this.fetchInformation(21, Util.toVector(string));
    }

    public Hashtable getTCTemplates() {
        return (Hashtable)this.fetchInformation(25, null);
    }

    public Hashtable getTermsAndConditions() {
        return (Hashtable)this.fetchInformation(22, null);
    }

    public Hashtable getTermsAndConditionsSerials() {
        return (Hashtable)this.fetchInformation(23, null);
    }

    public Hashtable getPerformanceInfos() {
        return (Hashtable)this.fetchInformation(20, null);
    }

    public Hashtable updateExitAddresses() {
        return (Hashtable)this.fetchInformation(24, null);
    }

    public Hashtable getPaymentInstances() {
        return (Hashtable)this.fetchInformation(9, null);
    }

    public PaymentInstanceDBEntry getPaymentInstance(String string) throws Exception {
        return (PaymentInstanceDBEntry)this.fetchInformation(10, Util.toVector(string));
    }

    public Hashtable getInfoServices() {
        return (Hashtable)this.fetchInformation(2, null);
    }

    public Hashtable getInfoServiceSerials() {
        return (Hashtable)this.fetchInformation(14, null);
    }

    public InetAddress getMyIP(Integer n) {
        return (InetAddress)this.fetchInformation(26, Util.toVector(n));
    }

    public MixInfo getMixInfo(String string) {
        if (string == null) {
            return null;
        }
        return (MixInfo)this.fetchInformation(3, Util.toVector(string));
    }

    public Hashtable getMixInfos() {
        return (Hashtable)this.fetchInformation(25, null);
    }

    public StatusInfo getStatusInfo(MixCascade mixCascade) {
        if (mixCascade == null || mixCascade == AbstractAutoSwitchedMixCascadeContainer.INITIAL_DUMMY_SERVICE) {
            return null;
        }
        return (StatusInfo)this.fetchInformation(4, Util.toVector(mixCascade));
    }

    public StatusInfo getStatusInfo(MixCascade mixCascade, long l) {
        if (mixCascade == null || mixCascade == AbstractAutoSwitchedMixCascadeContainer.INITIAL_DUMMY_SERVICE) {
            return null;
        }
        Vector<Object> vector = new Vector<Object>();
        vector.addElement(mixCascade);
        vector.addElement(new Long(l));
        return (StatusInfo)this.fetchInformation(19, vector);
    }

    public JAPMinVersion getNewVersionNumber() {
        return (JAPMinVersion)this.fetchInformation(5, null);
    }

    public Hashtable getLatestJavaVersions() {
        return (Hashtable)this.fetchInformation(13, null);
    }

    public Hashtable getLatestJavaVersionSerials() {
        return (Hashtable)this.fetchInformation(17, null);
    }

    public Hashtable getMessages() {
        return (Hashtable)this.fetchInformation(16, null);
    }

    public Hashtable getMessageSerials() {
        return (Hashtable)this.fetchInformation(18, null);
    }

    public JAPVersionInfo getJAPVersionInfo(int n) {
        return (JAPVersionInfo)this.fetchInformation(6, Util.toVector(new Integer(n)));
    }

    public byte[] getTorNodesList() {
        return (byte[])this.fetchInformation(7, null);
    }

    public MixCascade getMixCascadeInfo(String string) {
        if (string == null || string.equals(AbstractAutoSwitchedMixCascadeContainer.INITIAL_DUMMY_SERVICE.getId())) {
            return null;
        }
        return (MixCascade)this.fetchInformation(12, Util.toVector(string));
    }

    public byte[] getMixminionNodesList() {
        return (byte[])this.fetchInformation(11, null);
    }

    public Element getForwarder() {
        return (Element)this.fetchInformation(8, null);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public Element toXmlElement(Document document) {
        Element element = document.createElement(XML_ELEMENT_NAME);
        Element element2 = Database.getInstance(class$anon$infoservice$InfoServiceDBEntry == null ? (class$anon$infoservice$InfoServiceDBEntry = InfoServiceHolder.class$("anon.infoservice.InfoServiceDBEntry")) : class$anon$infoservice$InfoServiceDBEntry).toXmlElement(document, "InfoServices");
        Element element3 = document.createElement("PreferredInfoService");
        Element element4 = document.createElement(XML_ELEM_CHANGE_INFO_SERVICES);
        XMLUtil.setAttribute(element, XML_ATTR_ASKED_INFO_SERVICES, this.m_nrAskedInfoServices);
        InfoServiceHolder infoServiceHolder = this;
        synchronized (infoServiceHolder) {
            InfoServiceDBEntry infoServiceDBEntry = this.getPreferredInfoService();
            if (infoServiceDBEntry != null) {
                element3.appendChild(infoServiceDBEntry.toXmlElement(document));
            }
            XMLUtil.setValue((Node)element4, this.isChangeInfoServices());
        }
        element.appendChild(element2);
        element.appendChild(element3);
        element.appendChild(element4);
        return element;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void loadSettingsFromXml(Element element, boolean bl) throws Exception {
        int n;
        this.setNumberOfAskedInfoServices(XMLUtil.parseAttribute((Node)element, XML_ATTR_ASKED_INFO_SERVICES, 3));
        Element element2 = (Element)XMLUtil.getFirstChildByName(element, "InfoServices");
        if (element2 == null) {
            throw new Exception("No InfoServices node found.");
        }
        Database.getInstance(class$anon$infoservice$InfoServiceDBEntry == null ? (class$anon$infoservice$InfoServiceDBEntry = InfoServiceHolder.class$("anon.infoservice.InfoServiceDBEntry")) : class$anon$infoservice$InfoServiceDBEntry).loadFromXml(element2, true);
        Element element3 = (Element)XMLUtil.getFirstChildByName(element, "PreferredInfoService");
        if (element3 == null) {
            throw new Exception("No PreferredInfoService node found.");
        }
        Element element4 = (Element)XMLUtil.getFirstChildByName(element3, "InfoService");
        InfoServiceDBEntry infoServiceDBEntry = null;
        if (element4 != null) {
            try {
                infoServiceDBEntry = new InfoServiceDBEntry(element4, Long.MAX_VALUE);
            }
            catch (XMLParseException xMLParseException) {
                // empty catch block
            }
        }
        Vector vector = Database.getInstance(class$anon$infoservice$InfoServiceDBEntry == null ? (class$anon$infoservice$InfoServiceDBEntry = InfoServiceHolder.class$("anon.infoservice.InfoServiceDBEntry")) : class$anon$infoservice$InfoServiceDBEntry).getEntryList();
        Vector<String> vector2 = new Vector<String>();
        int n2 = 0;
        for (n = 0; n < vector.size(); ++n) {
            InfoServiceDBEntry infoServiceDBEntry2 = (InfoServiceDBEntry)vector.elementAt(n);
            if (!(infoServiceDBEntry2.isBootstrap() || infoServiceDBEntry2.isUserDefined() || infoServiceDBEntry2.isVerified() && infoServiceDBEntry2.isValid())) {
                Database.getInstance(class$anon$infoservice$InfoServiceDBEntry == null ? InfoServiceHolder.class$("anon.infoservice.InfoServiceDBEntry") : class$anon$infoservice$InfoServiceDBEntry).remove(infoServiceDBEntry2.getId());
                continue;
            }
            if (infoServiceDBEntry2.isBootstrap()) {
                vector2.addElement(infoServiceDBEntry2.getId());
                continue;
            }
            if (infoServiceDBEntry2.isUserDefined()) continue;
            ++n2;
        }
        if (n2 >= 3) {
            for (n = 0; n < vector2.size(); ++n) {
                Database.getInstance(class$anon$infoservice$InfoServiceDBEntry == null ? InfoServiceHolder.class$("anon.infoservice.InfoServiceDBEntry") : class$anon$infoservice$InfoServiceDBEntry).remove(vector2.elementAt(n).toString());
            }
        }
        InfoServiceHolder infoServiceHolder = this;
        synchronized (infoServiceHolder) {
            if (infoServiceDBEntry != null) {
                this.setPreferredInfoService(infoServiceDBEntry);
            } else if (this.getPreferredInfoService() == null) {
                this.setPreferredInfoService((InfoServiceDBEntry)Database.getInstance(class$anon$infoservice$InfoServiceDBEntry == null ? (class$anon$infoservice$InfoServiceDBEntry = InfoServiceHolder.class$("anon.infoservice.InfoServiceDBEntry")) : class$anon$infoservice$InfoServiceDBEntry).getRandomEntry());
            }
            if (bl) {
                this.setChangeInfoServices(true);
            } else {
                Element element5 = (Element)XMLUtil.getFirstChildByName(element, XML_ELEM_CHANGE_INFO_SERVICES);
                this.setChangeInfoServices(XMLUtil.parseValue((Node)element5, this.isChangeInfoServices()));
            }
        }
    }

    private static void filterServiceContext(Hashtable hashtable, String string) {
        boolean bl = false;
        if (string != null && hashtable != null) {
            String string2 = null;
            try {
                Enumeration enumeration = hashtable.keys();
                while (enumeration.hasMoreElements()) {
                    Object k = enumeration.nextElement();
                    IServiceContextContainer iServiceContextContainer = (IServiceContextContainer)hashtable.get(k);
                    string2 = iServiceContextContainer.getContext();
                    bl = !(string2 != null && string2.equals(string) || string2.startsWith("jondonym") && string2.equals("jondonym.premium"));
                    if (!bl) continue;
                    hashtable.remove(k);
                }
            }
            catch (ClassCastException classCastException) {
                LogHolder.log(3, LogType.MISC, "Wrong type for filter specified", classCastException);
            }
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

    static {
        ms_infoServiceHolderInstance = null;
        Field[] arrfield = (class$anon$infoservice$InfoServiceHolder == null ? (class$anon$infoservice$InfoServiceHolder = InfoServiceHolder.class$("anon.infoservice.InfoServiceHolder")) : class$anon$infoservice$InfoServiceHolder).getDeclaredFields();
        GETS = new String[arrfield.length];
        for (int i = 0; i < arrfield.length; ++i) {
            if (!arrfield[i].getName().startsWith("GET") || arrfield[i].getType() != Integer.TYPE) continue;
            try {
                InfoServiceHolder.GETS[arrfield[i].getInt(null)] = arrfield[i].getName();
                continue;
            }
            catch (Exception exception) {
                LogHolder.log(3, LogType.DB, exception);
                break;
            }
        }
    }

    private class InformationFetcher
    implements Runnable {
        private int functionNumber;
        private Vector arguments;
        private Object m_result;

        public InformationFetcher(int n, Vector vector) {
            this.functionNumber = n;
            this.arguments = vector;
        }

        public Object getResult() {
            return this.m_result;
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        public void run() {
            Hashtable hashtable;
            Object object;
            InfoServiceDBEntry infoServiceDBEntry = null;
            Random random = new Random(System.currentTimeMillis());
            int n = 1;
            infoServiceDBEntry = InfoServiceHolder.this.getPreferredInfoService();
            Vector vector = null;
            Exception exception = null;
            if (InfoServiceHolder.this.m_changeInfoServices) {
                vector = Database.getInstance(class$anon$infoservice$InfoServiceDBEntry == null ? (class$anon$infoservice$InfoServiceDBEntry = InfoServiceHolder.class$("anon.infoservice.InfoServiceDBEntry")) : class$anon$infoservice$InfoServiceDBEntry).getEntryList();
                object = (Vector)vector.clone();
                for (int i = 0; i < ((Vector)object).size(); ++i) {
                    hashtable = (InfoServiceDBEntry)((Vector)object).elementAt(i);
                    boolean bl = false;
                    if ((((InfoServiceDBEntry)((Object)hashtable)).isBootstrap() || ((InfoServiceDBEntry)((Object)hashtable)).isUserDefined()) && ((InfoServiceDBEntry)((Object)hashtable)).getCertPath() == null || !SignatureVerifier.getInstance().isCheckSignatures() || ((InfoServiceDBEntry)((Object)hashtable)).getCertPath() != null && ((InfoServiceDBEntry)((Object)hashtable)).getCertPath().isVerified() && !(bl = !((InfoServiceDBEntry)((Object)hashtable)).isValid())) continue;
                    if (bl) {
                        if (exception == null || exception instanceof ExpiredSignatureException) {
                            exception = new ExpiredSignatureException("Signature expired for IS " + ((InfoServiceDBEntry)((Object)hashtable)).getId() + ".");
                        }
                    } else {
                        exception = new SignatureException("No valid signature for IS " + ((InfoServiceDBEntry)((Object)hashtable)).getId() + ".");
                    }
                    vector.removeElement(hashtable);
                }
            } else {
                vector = new Vector();
                if (infoServiceDBEntry != null) {
                    vector.addElement(infoServiceDBEntry);
                }
            }
            object = new Hashtable();
            n = InfoServiceHolder.this.m_nrAskedInfoServices;
            if (this.functionNumber == 20) {
                n = Integer.MAX_VALUE;
            }
            if (this.functionNumber == 4 || this.functionNumber == 19) {
                infoServiceDBEntry = null;
            }
            while (!(vector.size() <= 0 && infoServiceDBEntry == null || Thread.currentThread().isInterrupted())) {
                if (infoServiceDBEntry == null) {
                    infoServiceDBEntry = (InfoServiceDBEntry)vector.elementAt(Math.abs(random.nextInt()) % vector.size());
                }
                LogHolder.log(5, LogType.DB, "Trying InfoService: " + infoServiceDBEntry.getName(), 1);
                try {
                    hashtable = null;
                    if (this.functionNumber == 1) {
                        hashtable = infoServiceDBEntry.getMixCascades();
                        if (this.arguments != null) {
                            String string = (String)this.arguments.firstElement();
                            InfoServiceHolder.filterServiceContext(hashtable, string);
                        }
                    } else if (this.functionNumber == 26) {
                        object = infoServiceDBEntry.getMyIP((Integer)this.arguments.elementAt(0));
                    } else if (this.functionNumber == 2) {
                        hashtable = infoServiceDBEntry.getInfoServices();
                    } else if (this.functionNumber == 25) {
                        hashtable = infoServiceDBEntry.getMixes(true);
                    } else if (this.functionNumber == 3) {
                        object = infoServiceDBEntry.getMixInfo((String)this.arguments.elementAt(0));
                    } else if (this.functionNumber == 13) {
                        hashtable = infoServiceDBEntry.getLatestJava();
                    } else if (this.functionNumber == 17) {
                        hashtable = infoServiceDBEntry.getLatestJavaSerials();
                    } else if (this.functionNumber == 21) {
                        object = infoServiceDBEntry.getTCTemplate((String)this.arguments.elementAt(0));
                    } else if (this.functionNumber == 20) {
                        PerformanceInfo performanceInfo = infoServiceDBEntry.getPerformanceInfo();
                        hashtable = new Hashtable();
                        if (performanceInfo != null) {
                            hashtable.put(((AbstractDatabaseEntry)performanceInfo).getId(), performanceInfo);
                        }
                    } else if (this.functionNumber == 16) {
                        hashtable = infoServiceDBEntry.getMessages();
                    } else if (this.functionNumber == 18) {
                        hashtable = infoServiceDBEntry.getMessageSerials();
                    } else if (this.functionNumber == 4) {
                        object = infoServiceDBEntry.getStatusInfo((MixCascade)this.arguments.elementAt(0));
                    } else if (this.functionNumber == 19) {
                        object = infoServiceDBEntry.getStatusInfo((MixCascade)this.arguments.elementAt(0), (Long)this.arguments.elementAt(1));
                    } else if (this.functionNumber == 15) {
                        hashtable = infoServiceDBEntry.getMixCascadeSerials();
                        if (this.arguments != null) {
                            String string = (String)this.arguments.firstElement();
                            InfoServiceHolder.filterServiceContext(hashtable, string);
                        }
                    } else if (this.functionNumber == 14) {
                        hashtable = infoServiceDBEntry.getInfoServiceSerials();
                    } else if (this.functionNumber == 5) {
                        object = infoServiceDBEntry.getNewVersionNumber();
                    } else if (this.functionNumber == 6) {
                        object = infoServiceDBEntry.getJAPVersionInfo((Integer)this.arguments.elementAt(0));
                    } else if (this.functionNumber == 7) {
                        object = infoServiceDBEntry.getTorNodesList();
                    } else if (this.functionNumber == 11) {
                        object = infoServiceDBEntry.getMixminionNodesList();
                    } else if (this.functionNumber == 8) {
                        object = infoServiceDBEntry.getForwarder();
                    } else if (this.functionNumber == 9) {
                        hashtable = infoServiceDBEntry.getPaymentInstances();
                    } else if (this.functionNumber == 10) {
                        object = infoServiceDBEntry.getPaymentInstance((String)this.arguments.firstElement());
                    } else if (this.functionNumber == 24) {
                        hashtable = infoServiceDBEntry.getExitAddresses();
                    } else if (this.functionNumber == 12) {
                        object = infoServiceDBEntry.getMixCascadeInfo((String)this.arguments.firstElement());
                    }
                    if (hashtable == null && object == null || hashtable != null && hashtable.size() == 0) {
                        LogHolder.log(6, LogType.NET, "IS " + infoServiceDBEntry.getName() + " did not have the requested info!");
                        vector.removeElement(infoServiceDBEntry);
                        infoServiceDBEntry = null;
                        continue;
                    }
                    if (hashtable == null) break;
                    Enumeration enumeration = hashtable.elements();
                    while (enumeration.hasMoreElements()) {
                        AbstractDatabaseEntry abstractDatabaseEntry = (AbstractDatabaseEntry)enumeration.nextElement();
                        if (((Hashtable)object).containsKey(abstractDatabaseEntry.getId())) {
                            AbstractDatabaseEntry abstractDatabaseEntry2 = (AbstractDatabaseEntry)((Hashtable)object).get(abstractDatabaseEntry.getId());
                            if (abstractDatabaseEntry instanceof AbstractDistributableDatabaseEntry.SerialDBEntry && abstractDatabaseEntry2 instanceof AbstractDistributableDatabaseEntry.SerialDBEntry) {
                                AbstractDistributableDatabaseEntry.SerialDBEntry serialDBEntry = (AbstractDistributableDatabaseEntry.SerialDBEntry)abstractDatabaseEntry;
                                AbstractDistributableDatabaseEntry.SerialDBEntry serialDBEntry2 = (AbstractDistributableDatabaseEntry.SerialDBEntry)abstractDatabaseEntry2;
                                if (serialDBEntry.getVersionNumber() != serialDBEntry2.getVersionNumber()) {
                                    LogHolder.log(4, LogType.NET, "InfoServices report different serial numbers for " + serialDBEntry.getId() + "!");
                                    serialDBEntry = new AbstractDistributableDatabaseEntry.SerialDBEntry(serialDBEntry.getId(), 0L, Long.MAX_VALUE, serialDBEntry.isVerified(), serialDBEntry.isValid(), serialDBEntry.getContext());
                                }
                                if (serialDBEntry.isVerified() != serialDBEntry2.isVerified()) {
                                    LogHolder.log(4, LogType.NET, "InfoServices report different verification status for " + ClassUtil.getShortClassName(abstractDatabaseEntry.getClass()) + " with id " + serialDBEntry.getId() + "!");
                                    serialDBEntry = new AbstractDistributableDatabaseEntry.SerialDBEntry(serialDBEntry.getId(), serialDBEntry.getVersionNumber(), Long.MAX_VALUE, true, serialDBEntry.isValid(), serialDBEntry.getContext());
                                }
                                if (serialDBEntry.isValid() != serialDBEntry2.isValid()) {
                                    LogHolder.log(4, LogType.NET, "InfoServices report different validity status for " + serialDBEntry.getId() + "!");
                                    serialDBEntry = new AbstractDistributableDatabaseEntry.SerialDBEntry(serialDBEntry.getId(), serialDBEntry.getVersionNumber(), Long.MAX_VALUE, serialDBEntry.isVerified(), true, serialDBEntry.getContext());
                                }
                                abstractDatabaseEntry = serialDBEntry;
                            }
                            if (abstractDatabaseEntry2.getLastUpdate() > abstractDatabaseEntry.getLastUpdate()) continue;
                        }
                        ((Hashtable)object).put(abstractDatabaseEntry.getId(), abstractDatabaseEntry);
                    }
                    if (--n == 0) break;
                    vector.removeElement(infoServiceDBEntry);
                    infoServiceDBEntry = null;
                }
                catch (Exception exception2) {
                    LogHolder.log(3, LogType.NET, "Contacting IS " + infoServiceDBEntry.getName() + " produced an error!", exception2);
                    vector.removeElement(infoServiceDBEntry);
                    infoServiceDBEntry = null;
                    if (exception == null) {
                        exception = exception2;
                        continue;
                    }
                    if (!(exception instanceof ExpiredSignatureException) && exception2 instanceof ExpiredSignatureException || !(exception instanceof SignatureException) && exception2 instanceof SignatureException) continue;
                    exception = exception2;
                }
            }
            if (!(object == null || object instanceof Hashtable && ((Hashtable)object).size() <= 0)) {
                this.m_result = object;
                return;
            }
            hashtable = null;
            if (GETS.length > this.functionNumber) {
                hashtable = GETS[this.functionNumber];
            }
            if (hashtable == null) {
                hashtable = "the needed information (" + this.functionNumber + ")";
            }
            LogHolder.log(3, LogType.NET, "No InfoService with " + (String)((Object)hashtable) + " available" + (this.arguments == null || this.arguments.elementAt(0) == null ? "." : " for argument: " + this.arguments.elementAt(0)), 1);
            this.m_result = null;
            if (exception != null && exception instanceof SignatureException) {
                LogHolder.log(2, LogType.CRYPTO, "Could not contact InfoServices due to certificate problems.", exception);
                InfoServiceHolder infoServiceHolder = InfoServiceHolder.getInstance();
                synchronized (infoServiceHolder) {
                    InfoServiceHolder.this.setChanged();
                    InfoServiceHolder.this.notifyObservers(new InfoServiceHolderMessage(3, exception));
                }
            }
        }
    }
}

