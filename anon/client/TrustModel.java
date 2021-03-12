/*
 * Decompiled with CFR 0.150.
 */
package anon.client;

import anon.client.BasicTrustModel;
import anon.error.ServiceSignatureException;
import anon.error.TrustException;
import anon.infoservice.Database;
import anon.infoservice.ListenerInterface;
import anon.infoservice.MixCascade;
import anon.infoservice.MixInfo;
import anon.infoservice.PerformanceEntry;
import anon.infoservice.PerformanceInfo;
import anon.infoservice.ServiceOperator;
import anon.infoservice.StatusInfo;
import anon.pay.PayAccountsFile;
import anon.pay.PaymentInstanceDBEntry;
import anon.util.IXMLEncodable;
import anon.util.JAPMessages;
import anon.util.XMLParseException;
import anon.util.XMLUtil;
import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Observable;
import java.util.Observer;
import java.util.Vector;
import logging.LogHolder;
import logging.LogType;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class TrustModel
extends BasicTrustModel
implements IXMLEncodable {
    public static final Integer NOTIFY_TRUST_MODEL_CHANGED = new Integer(0);
    public static final Integer NOTIFY_TRUST_MODEL_ADDED = new Integer(1);
    public static final Integer NOTIFY_TRUST_MODEL_REMOVED = new Integer(2);
    public static final int FIRST_UNRESERVED_MODEL_ID = 5;
    public static final String XML_ELEMENT_NAME = "TrustModel";
    public static final String XML_ELEMENT_CONTAINER_NAME = "TrustModels";
    private static final String XML_ATTR_CURRENT_TRUST_MODEL = "currentTrustModel";
    private static final String XML_ATTR_NAME = "name";
    public static final int TRUST_MODEL_DEFAULT_INDEX = 0;
    public static final TrustModel TRUST_MODEL_ALL_SERVICES;
    private static TrustModel TRUST_MODEL_CUSTOM_FILTER;
    private static MixCascade ms_cascadeWhitelisted;
    public static final int TRUST_ALWAYS = 0;
    public static final int TRUST_IF_NOT_TRUE = 1;
    public static final int TRUST_IF_TRUE = 2;
    public static final int TRUST_IF_AT_LEAST = 3;
    public static final int TRUST_IF_AT_MOST = 5;
    public static final int TRUST_IF_NOT_IN_LIST = 6;
    public static final int TRUST_RESERVED = 7;
    private static final String MSG_SERVICES_WITH_COSTS;
    public static final String MSG_SERVICES_WITHOUT_COSTS;
    private static final String MSG_SERVICES_USER_DEFINED;
    private static final String MSG_CASCADES_FILTER;
    private static final String MSG_ALL_SERVICES;
    private static final String MSG_DEFAULT_FILTER;
    private static final String MSG_SERVICES_BUSINESS;
    private static final String MSG_SERVICES_TEST;
    private static final String MSG_SERVICES_PREMIUM_PRIVATE;
    public static final String MSG_PI_UNAVAILABLE;
    public static final String MSG_BLACKLISTED;
    private static final String MSG_EXCEPTION_NO_SOCKS;
    private static final String MSG_EXCEPTION_DATA_RETENTION;
    private static final String MSG_EXCEPTION_PAY_CASCADE;
    public static final String MSG_EXCEPTION_FREE_CASCADE;
    private static final String MSG_EXCEPTION_WRONG_SERVICE_CONTEXT;
    public static final String MSG_EXCEPTION_NOT_ENOUGH_MIXES;
    public static final String MSG_EXCEPTION_TOO_MANY_MIXES;
    private static final String MSG_EXCEPTION_EXPIRED_CERT;
    private static final String MSG_EXCEPTION_NOT_USER_DEFINED;
    private static final String MSG_EXCEPTION_TOO_FEW_COUNTRIES;
    private static final String MSG_EXCEPTION_TOO_MANY_COUNTRIES;
    private static final String MSG_EXCEPTION_NOT_INTERNATIONAL;
    private static final String MSG_EXCEPTION_INTERNATIONAL;
    private static final String MSG_EXCEPTION_NOT_ENOUGH_ANON;
    private static final String MSG_EXCEPTION_BLACKLISTED;
    private static final String MSG_EXCEPTION_NOT_ENOUGH_SPEED;
    private static final String MSG_EXCEPTION_RESPONSE_TIME_TOO_HIGH;
    private static Vector ms_trustModels;
    private static TrustModel ms_currentTrustModel;
    private static String ms_strContext;
    private static boolean ms_bFreeAllowed;
    private static InnerObservable ms_trustModelObservable;
    private Hashtable m_trustAttributes = new Hashtable();
    private String m_strName;
    private long m_id;
    private boolean m_bEditable;
    static /* synthetic */ Class class$anon$client$TrustModel;
    static /* synthetic */ Class class$anon$infoservice$ServiceOperator;
    static /* synthetic */ Class class$java$lang$Object;
    static /* synthetic */ Class class$anon$pay$PaymentInstanceDBEntry;
    static /* synthetic */ Class class$anon$infoservice$StatusInfo;
    static /* synthetic */ Class class$anon$infoservice$PerformanceInfo;
    static /* synthetic */ Class class$anon$client$TrustModel$ContextAttribute;
    static /* synthetic */ Class class$anon$client$TrustModel$NumberOfMixesAttribute;
    static /* synthetic */ Class class$anon$client$TrustModel$ForcePremiumIfChargedAccountAttribute;
    static /* synthetic */ Class class$anon$infoservice$MixCascade;
    static /* synthetic */ Class class$anon$client$TrustModel$UsePremiumAttribute;
    static /* synthetic */ Class class$anon$infoservice$BlacklistedCascadeIDEntry;

    private static int getIntegerConditionValue(Object object) {
        if (object == null || !(object instanceof Integer)) {
            return 0;
        }
        return (Integer)object;
    }

    public static String getContext() {
        return ms_strContext;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static boolean updateContext(String string) {
        Vector vector = ms_trustModels;
        synchronized (vector) {
            boolean bl = false;
            if (!(ms_strContext == string || string == null || ms_strContext != null && ms_strContext.equals(string))) {
                if (ms_strContext == null && !string.equals("jondonym") || ms_strContext != null) {
                    bl = true;
                }
                ms_strContext = string;
            }
            if (!bl) {
                return false;
            }
            TrustModel trustModel = ms_currentTrustModel;
            ms_trustModelObservable.setChanged();
            TrustModel.setCurrentTrustModel(trustModel.getId());
            return true;
        }
    }

    public TrustModel(String string, long l) {
        this(string, l, false);
        this.setEditable(true);
    }

    private TrustModel(String string, long l, boolean bl) {
        if (!bl && l < 5L) {
            throw new IllegalArgumentException("Trust model ID " + l + " is reserved!");
        }
        this.m_id = l;
        this.m_strName = string == null ? "Default trust model" : string;
    }

    public TrustModel(TrustModel trustModel) {
        this.setEditable(true);
        this.clone(trustModel);
        if (!trustModel.isEditable()) {
            this.setEditable(false);
        }
    }

    public TrustModel(Element element) throws XMLParseException {
        XMLUtil.assertNodeName(element, XML_ELEMENT_NAME);
        XMLUtil.assertNotNull(element, "id");
        this.m_id = XMLUtil.parseAttribute((Node)element, "id", -1L);
        if (this.m_id != 5L) {
            this.m_id = 5L;
        }
        this.m_strName = JAPMessages.getString(MSG_DEFAULT_FILTER);
        this.m_bEditable = true;
        for (int i = 0; i < element.getChildNodes().getLength(); ++i) {
            Element element2 = (Element)element.getChildNodes().item(i);
            try {
                this.setAttribute(TrustAttribute.fromXmlElement(element2));
                continue;
            }
            catch (XMLParseException xMLParseException) {
                LogHolder.log(3, LogType.MISC, xMLParseException);
            }
        }
        this.getAttribute(class$anon$client$TrustModel$NumberOfMixesAttribute == null ? (class$anon$client$TrustModel$NumberOfMixesAttribute = TrustModel.class$("anon.client.TrustModel$NumberOfMixesAttribute")) : class$anon$client$TrustModel$NumberOfMixesAttribute);
        this.getAttribute(class$anon$client$TrustModel$ForcePremiumIfChargedAccountAttribute == null ? (class$anon$client$TrustModel$ForcePremiumIfChargedAccountAttribute = TrustModel.class$("anon.client.TrustModel$ForcePremiumIfChargedAccountAttribute")) : class$anon$client$TrustModel$ForcePremiumIfChargedAccountAttribute);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void unblockInterfacesFromDatabase() {
        ListenerInterface.unblockInterfacesFromDatabase(class$anon$infoservice$MixCascade == null ? (class$anon$infoservice$MixCascade = TrustModel.class$("anon.infoservice.MixCascade")) : class$anon$infoservice$MixCascade);
        Vector vector = ms_trustModels;
        synchronized (vector) {
            ms_trustModelObservable.setChanged();
            ms_trustModelObservable.notifyObservers(NOTIFY_TRUST_MODEL_CHANGED);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void copyAttributes(TrustModel trustModel) {
        if (trustModel == null) {
            throw new NullPointerException("No argument given!");
        }
        if (!this.isEditable()) {
            throw new IllegalStateException("Trust model not editable!");
        }
        Cloneable cloneable = this.m_trustAttributes;
        synchronized (cloneable) {
            this.m_trustAttributes = (Hashtable)trustModel.m_trustAttributes.clone();
        }
        cloneable = ms_trustModels;
        synchronized (cloneable) {
            if (ms_trustModels.contains(this)) {
                ms_trustModelObservable.setChanged();
                ms_trustModelObservable.notifyObservers(NOTIFY_TRUST_MODEL_CHANGED);
            }
        }
    }

    public void clone(TrustModel trustModel) {
        if (trustModel == null) {
            throw new NullPointerException("No argument given!");
        }
        if (!this.isEditable()) {
            throw new IllegalStateException("Trust model not editable!");
        }
        this.m_id = trustModel.m_id;
        this.m_strName = trustModel.m_strName;
        this.copyAttributes(trustModel);
        this.m_bEditable = trustModel.m_bEditable;
    }

    public static Observable getObservable() {
        return ms_trustModelObservable;
    }

    public static void addModelObserver(Observer observer) {
        ms_trustModelObservable.addObserver(observer);
    }

    public static void deleteModelObserver(Observer observer) {
        ms_trustModelObservable.deleteObserver(observer);
    }

    public boolean equals(Object object) {
        if (object == null || !(object instanceof TrustModel)) {
            return false;
        }
        return this.getId() == ((TrustModel)object).getId();
    }

    public int hashCode() {
        return (int)this.getId();
    }

    public static void setFreeAllowed(boolean bl) {
        ms_bFreeAllowed = bl;
    }

    public static boolean isFreeAllowed() {
        return ms_bFreeAllowed;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static void allowAttributeWhitelist(MixCascade mixCascade) {
        Vector vector = ms_trustModels;
        synchronized (vector) {
            if (!(mixCascade == null || ms_cascadeWhitelisted != null && ms_cascadeWhitelisted.getId().equals(mixCascade))) {
                ms_cascadeWhitelisted = mixCascade;
                ms_trustModelObservable.setChanged();
                ms_trustModelObservable.notifyObservers(NOTIFY_TRUST_MODEL_CHANGED);
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static void cleanAttributeWhitelist(MixCascade mixCascade) {
        Vector vector = ms_trustModels;
        synchronized (vector) {
            if (mixCascade == null || ms_cascadeWhitelisted != null && !ms_cascadeWhitelisted.getId().equals(mixCascade.getId())) {
                ms_cascadeWhitelisted = null;
                ms_trustModelObservable.setChanged();
                ms_trustModelObservable.notifyObservers(NOTIFY_TRUST_MODEL_CHANGED);
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private static boolean addTrustModel(TrustModel trustModel) {
        Vector vector = ms_trustModels;
        synchronized (vector) {
            if (trustModel != null && !ms_trustModels.contains(trustModel)) {
                if (trustModel.getId() == 5L) {
                    TRUST_MODEL_CUSTOM_FILTER = trustModel;
                }
                if (ms_currentTrustModel != null && ms_currentTrustModel.getId() == trustModel.getId()) {
                    ms_currentTrustModel = trustModel;
                }
                ms_trustModels.insertElementAt(trustModel, 0);
                ms_trustModelObservable.setChanged();
                ms_trustModelObservable.notifyObservers(NOTIFY_TRUST_MODEL_ADDED);
                return true;
            }
        }
        return false;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static TrustModel removeTrustModel(TrustModel trustModel) {
        if (trustModel == null) {
            return null;
        }
        if (!trustModel.isEditable()) {
            return null;
        }
        Vector vector = ms_trustModels;
        synchronized (vector) {
            if (trustModel != null && ms_trustModels.removeElement(trustModel)) {
                if (TrustModel.getCurrentTrustModel() == trustModel) {
                    TrustModel.restoreDefault();
                }
                ms_trustModelObservable.setChanged();
                ms_trustModelObservable.notifyObservers(NOTIFY_TRUST_MODEL_REMOVED);
                return trustModel;
            }
        }
        return null;
    }

    public TrustAttribute setAttribute(Class class_, int n) {
        return this.setAttribute(class_, n, false);
    }

    private TrustAttribute setAttribute(Class class_, int n, boolean bl) {
        return this.setAttribute(class_, n, null, bl);
    }

    public TrustAttribute setAttribute(Class class_, int n, int n2) {
        return this.setAttribute(class_, n, n2, false);
    }

    public TrustAttribute setAttribute(Class class_, int n, Object object) {
        return this.setAttribute(class_, n, object, false);
    }

    private TrustAttribute setAttribute(Class class_, int n, int n2, boolean bl) {
        return this.setAttribute(class_, n, new Integer(n2), bl);
    }

    public TrustAttribute setAttribute(Class class_, int n, Vector vector) {
        return this.setAttribute(class_, n, vector, false);
    }

    private void setEditable(boolean bl) {
        this.m_bEditable = bl;
    }

    public boolean isEditable() {
        return this.m_bEditable;
    }

    private TrustAttribute setAttribute(Class class_, int n, Object object, boolean bl) {
        return this.setAttribute(TrustAttribute.getInstance(class_, n, object, bl));
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void removeAllAttributes() {
        if (!this.isEditable()) {
            throw new IllegalStateException("Trust model not editable!");
        }
        Hashtable hashtable = this.m_trustAttributes;
        synchronized (hashtable) {
            if (this.m_trustAttributes.size() > 0) {
                ms_trustModelObservable.setChanged();
                this.m_trustAttributes.clear();
            }
        }
        ms_trustModelObservable.notifyObservers(NOTIFY_TRUST_MODEL_CHANGED);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private TrustAttribute setAttribute(TrustAttribute trustAttribute) {
        if (trustAttribute != null) {
            Vector vector = ms_trustModels;
            synchronized (vector) {
                Hashtable hashtable = this.m_trustAttributes;
                synchronized (hashtable) {
                    TrustAttribute trustAttribute2 = (TrustAttribute)this.m_trustAttributes.get(trustAttribute);
                    if (trustAttribute2 != null && (trustAttribute2.getTrustCondition() != trustAttribute.getTrustCondition() || trustAttribute2.getConditionValue() != trustAttribute.getConditionValue())) {
                        this.m_trustAttributes.put(trustAttribute, trustAttribute2);
                        if (ms_trustModels.contains(this)) {
                            ms_trustModelObservable.setChanged();
                        }
                    }
                    this.m_trustAttributes.put(trustAttribute.getClass(), trustAttribute);
                }
                if (ms_trustModels.contains(this)) {
                    ms_trustModelObservable.notifyObservers(NOTIFY_TRUST_MODEL_CHANGED);
                }
            }
        }
        return trustAttribute;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public TrustAttribute getAttribute(Class class_) {
        if (class_ == null) {
            return null;
        }
        Hashtable hashtable = this.m_trustAttributes;
        synchronized (hashtable) {
            TrustAttribute trustAttribute = (TrustAttribute)this.m_trustAttributes.get(class_);
            if (trustAttribute == null) {
                Method method;
                Object object = new Integer(0);
                int n = 0;
                try {
                    method = class_.getMethod("getDefaultValue", null);
                    object = method.invoke(null, null);
                }
                catch (Exception exception) {
                    LogHolder.log(2, LogType.MISC, "Exception occured while trying to get the default value of a TrustAttribute: ", exception);
                }
                try {
                    method = class_.getMethod("getDefaultCondition", null);
                    n = (Integer)method.invoke(null, null);
                }
                catch (Exception exception) {
                    LogHolder.log(2, LogType.MISC, "Exception occured while trying to get the default value of a TrustAttribute: ", exception);
                }
                return this.setAttribute(class_, n, object);
            }
            return trustAttribute;
        }
    }

    public static Vector getTrustModels() {
        return (Vector)ms_trustModels.clone();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static void setCurrentTrustModel(long l) {
        if (l < 0L) {
            return;
        }
        Vector vector = ms_trustModels;
        synchronized (vector) {
            for (int i = 0; i < ms_trustModels.size(); ++i) {
                if (((TrustModel)ms_trustModels.elementAt(i)).getId() != l) continue;
                ms_currentTrustModel = (TrustModel)ms_trustModels.elementAt(i);
                ms_trustModelObservable.setChanged();
                ms_trustModelObservable.notifyObservers(NOTIFY_TRUST_MODEL_CHANGED);
                break;
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static void setCurrentTrustModel(TrustModel trustModel) {
        if (trustModel == null) {
            return;
        }
        Vector vector = ms_trustModels;
        synchronized (vector) {
            if (!ms_trustModels.contains(trustModel)) {
                ms_trustModels.addElement(trustModel);
            }
            if (ms_currentTrustModel != trustModel) {
                ms_currentTrustModel = trustModel;
                ms_trustModelObservable.setChanged();
            }
            ms_trustModelObservable.notifyObservers(NOTIFY_TRUST_MODEL_CHANGED);
        }
    }

    public static TrustModel getTrustModelDefault() {
        return (TrustModel)ms_trustModels.elementAt(0);
    }

    public static TrustModel getCurrentTrustModel() {
        return ms_currentTrustModel;
    }

    public static TrustModel getCustomFilter() {
        return TRUST_MODEL_CUSTOM_FILTER;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static void fromXmlElement(Element element) {
        int n = 0;
        if (element != null && element.getNodeName().equals(XML_ELEMENT_CONTAINER_NAME)) {
            NodeList nodeList = element.getElementsByTagName(XML_ELEMENT_NAME);
            for (int i = 0; i < nodeList.getLength(); ++i) {
                Vector vector = ms_trustModels;
                synchronized (vector) {
                    try {
                        TrustModel trustModel = new TrustModel((Element)nodeList.item(i));
                        boolean bl = false;
                        for (int j = 0; j < ms_trustModels.size(); ++j) {
                            if (((TrustModel)ms_trustModels.elementAt(j)).getId() != trustModel.getId()) continue;
                            if (trustModel.getId() == 5L) {
                                TRUST_MODEL_CUSTOM_FILTER = trustModel;
                            }
                            ms_trustModels.removeElementAt(j);
                            ms_trustModels.insertElementAt(trustModel, j);
                            bl = true;
                            break;
                        }
                        if (trustModel.getId() == ms_currentTrustModel.getId()) {
                            ms_currentTrustModel = trustModel;
                        }
                        if (bl) {
                            ms_trustModelObservable.setChanged();
                            ms_trustModelObservable.notifyObservers(NOTIFY_TRUST_MODEL_CHANGED);
                        } else {
                            TrustModel.addTrustModel(trustModel);
                        }
                        ++n;
                    }
                    catch (Exception exception) {
                        LogHolder.log(2, LogType.MISC, "Could not load trust model from XML!", exception);
                    }
                    continue;
                }
            }
            if (n == 0) {
                TrustModel.addCustomFilter();
            }
            TrustModel.setCurrentTrustModel(XMLUtil.parseAttribute((Node)element, XML_ATTR_CURRENT_TRUST_MODEL, 0L));
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private static void addCustomFilter() {
        Vector vector = ms_trustModels;
        synchronized (vector) {
            TrustModel.removeTrustModel(TRUST_MODEL_CUSTOM_FILTER);
            TrustModel trustModel = new TrustModel(MSG_DEFAULT_FILTER, 5L, false);
            trustModel.setAttribute(class$anon$client$TrustModel$ForcePremiumIfChargedAccountAttribute == null ? (class$anon$client$TrustModel$ForcePremiumIfChargedAccountAttribute = TrustModel.class$("anon.client.TrustModel$ForcePremiumIfChargedAccountAttribute")) : class$anon$client$TrustModel$ForcePremiumIfChargedAccountAttribute, 2);
            trustModel.setEditable(true);
            TrustModel.addTrustModel(trustModel);
        }
    }

    public static void restoreDefault() {
        TrustModel.addCustomFilter();
        TrustModel.setCurrentTrustModel(0L);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static Element toXmlElement(Document document, String string) {
        if (document == null || string == null) {
            return null;
        }
        Element element = document.createElement(string);
        XMLUtil.setAttribute(element, XML_ATTR_CURRENT_TRUST_MODEL, TrustModel.getCurrentTrustModel().getId());
        Vector vector = ms_trustModels;
        synchronized (vector) {
            for (int i = 0; i < ms_trustModels.size(); ++i) {
                if (!((TrustModel)ms_trustModels.elementAt(i)).isEditable()) continue;
                element.appendChild(((TrustModel)ms_trustModels.elementAt(i)).toXmlElement(document));
            }
        }
        return element;
    }

    public void setName(String string) throws IllegalArgumentException {
        if (string == null || string.trim().length() == 0) {
            throw new IllegalArgumentException("Invalid name for trust model!");
        }
        this.m_strName = string.trim().equals(JAPMessages.getString(MSG_CASCADES_FILTER)) ? "" : string;
    }

    public String getName() {
        if (this.m_strName == null || this.m_strName.trim().length() == 0) {
            return JAPMessages.getString(MSG_CASCADES_FILTER);
        }
        return JAPMessages.getString(this.m_strName);
    }

    public String toString() {
        return this.getName();
    }

    public long getId() {
        return this.m_id;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public Element toXmlElement(Document document) {
        if (document == null) {
            return null;
        }
        Element element = document.createElement(XML_ELEMENT_NAME);
        XMLUtil.setAttribute(element, "id", this.m_id);
        Hashtable hashtable = this.m_trustAttributes;
        synchronized (hashtable) {
            Enumeration enumeration = this.m_trustAttributes.elements();
            while (enumeration.hasMoreElements()) {
                TrustAttribute trustAttribute = (TrustAttribute)enumeration.nextElement();
                element.appendChild(trustAttribute.toXmlElement(document));
            }
        }
        return element;
    }

    public boolean isFreeServicesForced() {
        TrustAttribute trustAttribute = this.getAttribute(class$anon$client$TrustModel$UsePremiumAttribute == null ? (class$anon$client$TrustModel$UsePremiumAttribute = TrustModel.class$("anon.client.TrustModel$UsePremiumAttribute")) : class$anon$client$TrustModel$UsePremiumAttribute);
        return trustAttribute == null ? false : trustAttribute.getTrustCondition() == 1;
    }

    public boolean isPaymentForced() {
        if (!TrustModel.isFreeAllowed()) {
            return true;
        }
        TrustAttribute trustAttribute = this.getAttribute(class$anon$client$TrustModel$UsePremiumAttribute == null ? (class$anon$client$TrustModel$UsePremiumAttribute = TrustModel.class$("anon.client.TrustModel$UsePremiumAttribute")) : class$anon$client$TrustModel$UsePremiumAttribute);
        return trustAttribute == null ? false : trustAttribute.getTrustCondition() == 2;
    }

    public boolean isAdded() {
        return true;
    }

    public int countTrustedCascades() {
        Vector vector = Database.getInstance(class$anon$infoservice$MixCascade == null ? (class$anon$infoservice$MixCascade = TrustModel.class$("anon.infoservice.MixCascade")) : class$anon$infoservice$MixCascade).getEntryList();
        int n = 0;
        for (int i = 0; i < vector.size(); ++i) {
            if (!this.isTrusted((MixCascade)vector.elementAt(i))) continue;
            ++n;
        }
        return n;
    }

    public boolean hasTrustedCascades() {
        Vector vector = Database.getInstance(class$anon$infoservice$MixCascade == null ? (class$anon$infoservice$MixCascade = TrustModel.class$("anon.infoservice.MixCascade")) : class$anon$infoservice$MixCascade).getEntryList();
        for (int i = 0; i < vector.size(); ++i) {
            if (!this.isTrusted((MixCascade)vector.elementAt(i))) continue;
            return true;
        }
        return false;
    }

    public boolean hasPremiumCascades() {
        Vector vector = Database.getInstance(class$anon$infoservice$MixCascade == null ? (class$anon$infoservice$MixCascade = TrustModel.class$("anon.infoservice.MixCascade")) : class$anon$infoservice$MixCascade).getEntryList();
        for (int i = 0; i < vector.size(); ++i) {
            if (!((MixCascade)vector.elementAt(i)).isPayment() || !this.isTrusted((MixCascade)vector.elementAt(i))) continue;
            return true;
        }
        return false;
    }

    public boolean hasFreeCascades() {
        Vector vector = Database.getInstance(class$anon$infoservice$MixCascade == null ? (class$anon$infoservice$MixCascade = TrustModel.class$("anon.infoservice.MixCascade")) : class$anon$infoservice$MixCascade).getEntryList();
        for (int i = 0; i < vector.size(); ++i) {
            if (((MixCascade)vector.elementAt(i)).isPayment() || !this.isTrusted((MixCascade)vector.elementAt(i))) continue;
            return true;
        }
        return false;
    }

    public static boolean isBlacklisted(MixCascade mixCascade) {
        if (mixCascade == null) {
            return false;
        }
        return Database.getInstance(class$anon$infoservice$BlacklistedCascadeIDEntry == null ? (class$anon$infoservice$BlacklistedCascadeIDEntry = TrustModel.class$("anon.infoservice.BlacklistedCascadeIDEntry")) : class$anon$infoservice$BlacklistedCascadeIDEntry).getEntryById(mixCascade.getMixIDsAsString()) != null;
    }

    public static boolean areListenerInterfacesBlocked(MixCascade mixCascade) {
        if (mixCascade == null) {
            return false;
        }
        return mixCascade.areListenerInterfacesBlocked();
    }

    public static boolean isNoPaymentInstanceFound(MixCascade mixCascade) {
        if (mixCascade == null) {
            return false;
        }
        return mixCascade.isPayment() && PayAccountsFile.getInstance().getBI(mixCascade.getPIID()) == null && PayAccountsFile.getInstance().getChargedAccount(mixCascade.getPIID()) != null;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public Vector readUntrustedAttributeText(MixCascade mixCascade) {
        Vector<String> vector = new Vector<String>();
        if (!this.isTrusted(mixCascade)) {
            Hashtable hashtable = this.m_trustAttributes;
            synchronized (hashtable) {
                Enumeration enumeration = this.m_trustAttributes.elements();
                while (enumeration.hasMoreElements()) {
                    TrustAttribute trustAttribute = (TrustAttribute)enumeration.nextElement();
                    try {
                        trustAttribute.checkTrust(mixCascade);
                    }
                    catch (TrustException trustException) {
                        vector.addElement(trustException.getMessage());
                    }
                    catch (ServiceSignatureException serviceSignatureException) {
                        vector.addElement(serviceSignatureException.getMessage());
                    }
                }
            }
        }
        return vector;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void checkTrust(MixCascade mixCascade, boolean bl) throws TrustException, ServiceSignatureException {
        if (mixCascade == null) {
            throw new TrustException(mixCascade, "Null cascade!");
        }
        if (TrustModel.isBlacklisted(mixCascade)) {
            throw new TrustException(mixCascade, JAPMessages.getString(MSG_BLACKLISTED));
        }
        if (bl || !mixCascade.isUserDefined()) {
            if (!ms_bFreeAllowed && !mixCascade.isPayment()) {
                throw new TrustException(mixCascade, JAPMessages.getString(MSG_EXCEPTION_FREE_CASCADE));
            }
            if (TrustModel.isNoPaymentInstanceFound(mixCascade)) {
                throw new TrustException(mixCascade, JAPMessages.getString(MSG_PI_UNAVAILABLE));
            }
            super.checkTrust(mixCascade, bl);
        } else if (mixCascade.getNumberOfOperators() <= 0) {
            return;
        }
        MixCascade mixCascade2 = ms_cascadeWhitelisted;
        if (mixCascade2 == null || !mixCascade2.getId().equals(mixCascade.getId())) {
            Hashtable hashtable = this.m_trustAttributes;
            synchronized (hashtable) {
                Enumeration enumeration = this.m_trustAttributes.elements();
                while (enumeration.hasMoreElements()) {
                    TrustAttribute trustAttribute = (TrustAttribute)enumeration.nextElement();
                    trustAttribute.checkTrust(mixCascade);
                }
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
        ms_cascadeWhitelisted = null;
        MSG_SERVICES_WITH_COSTS = (class$anon$client$TrustModel == null ? (class$anon$client$TrustModel = TrustModel.class$("anon.client.TrustModel")) : class$anon$client$TrustModel).getName() + "_servicesWithCosts";
        MSG_SERVICES_WITHOUT_COSTS = (class$anon$client$TrustModel == null ? (class$anon$client$TrustModel = TrustModel.class$("anon.client.TrustModel")) : class$anon$client$TrustModel).getName() + "_servicesWithoutCosts";
        MSG_SERVICES_USER_DEFINED = (class$anon$client$TrustModel == null ? (class$anon$client$TrustModel = TrustModel.class$("anon.client.TrustModel")) : class$anon$client$TrustModel).getName() + "_servicesUserDefined";
        MSG_CASCADES_FILTER = (class$anon$client$TrustModel == null ? (class$anon$client$TrustModel = TrustModel.class$("anon.client.TrustModel")) : class$anon$client$TrustModel).getName() + "_servicesFilter";
        MSG_ALL_SERVICES = (class$anon$client$TrustModel == null ? (class$anon$client$TrustModel = TrustModel.class$("anon.client.TrustModel")) : class$anon$client$TrustModel).getName() + "_allServices";
        MSG_DEFAULT_FILTER = (class$anon$client$TrustModel == null ? (class$anon$client$TrustModel = TrustModel.class$("anon.client.TrustModel")) : class$anon$client$TrustModel).getName() + ".defaultFilter";
        MSG_SERVICES_BUSINESS = (class$anon$client$TrustModel == null ? (class$anon$client$TrustModel = TrustModel.class$("anon.client.TrustModel")) : class$anon$client$TrustModel).getName() + "_servicesBusiness";
        MSG_SERVICES_TEST = (class$anon$client$TrustModel == null ? (class$anon$client$TrustModel = TrustModel.class$("anon.client.TrustModel")) : class$anon$client$TrustModel).getName() + "_servicesTest";
        MSG_SERVICES_PREMIUM_PRIVATE = (class$anon$client$TrustModel == null ? (class$anon$client$TrustModel = TrustModel.class$("anon.client.TrustModel")) : class$anon$client$TrustModel).getName() + "_servicesPremiumPrivate";
        MSG_PI_UNAVAILABLE = (class$anon$client$TrustModel == null ? (class$anon$client$TrustModel = TrustModel.class$("anon.client.TrustModel")) : class$anon$client$TrustModel).getName() + "_piUnavailable";
        MSG_BLACKLISTED = (class$anon$client$TrustModel == null ? (class$anon$client$TrustModel = TrustModel.class$("anon.client.TrustModel")) : class$anon$client$TrustModel).getName() + "_blacklisted";
        MSG_EXCEPTION_NO_SOCKS = (class$anon$client$TrustModel == null ? (class$anon$client$TrustModel = TrustModel.class$("anon.client.TrustModel")) : class$anon$client$TrustModel).getName() + "_exceptionNoSocks";
        MSG_EXCEPTION_DATA_RETENTION = (class$anon$client$TrustModel == null ? (class$anon$client$TrustModel = TrustModel.class$("anon.client.TrustModel")) : class$anon$client$TrustModel).getName() + "_exceptionDataRetention";
        MSG_EXCEPTION_PAY_CASCADE = (class$anon$client$TrustModel == null ? (class$anon$client$TrustModel = TrustModel.class$("anon.client.TrustModel")) : class$anon$client$TrustModel).getName() + "_exceptionPayCascade";
        MSG_EXCEPTION_FREE_CASCADE = (class$anon$client$TrustModel == null ? (class$anon$client$TrustModel = TrustModel.class$("anon.client.TrustModel")) : class$anon$client$TrustModel).getName() + "_exceptionFreeCascade";
        MSG_EXCEPTION_WRONG_SERVICE_CONTEXT = (class$anon$client$TrustModel == null ? (class$anon$client$TrustModel = TrustModel.class$("anon.client.TrustModel")) : class$anon$client$TrustModel).getName() + "_wrongServiceContext";
        MSG_EXCEPTION_NOT_ENOUGH_MIXES = (class$anon$client$TrustModel == null ? (class$anon$client$TrustModel = TrustModel.class$("anon.client.TrustModel")) : class$anon$client$TrustModel).getName() + "_exceptionNotEnoughMixes";
        MSG_EXCEPTION_TOO_MANY_MIXES = (class$anon$client$TrustModel == null ? (class$anon$client$TrustModel = TrustModel.class$("anon.client.TrustModel")) : class$anon$client$TrustModel).getName() + "_exceptionTooManyMixes";
        MSG_EXCEPTION_EXPIRED_CERT = (class$anon$client$TrustModel == null ? (class$anon$client$TrustModel = TrustModel.class$("anon.client.TrustModel")) : class$anon$client$TrustModel).getName() + "_exceptionExpiredCert";
        MSG_EXCEPTION_NOT_USER_DEFINED = (class$anon$client$TrustModel == null ? (class$anon$client$TrustModel = TrustModel.class$("anon.client.TrustModel")) : class$anon$client$TrustModel).getName() + "_exceptionNotUserDefined";
        MSG_EXCEPTION_TOO_FEW_COUNTRIES = (class$anon$client$TrustModel == null ? (class$anon$client$TrustModel = TrustModel.class$("anon.client.TrustModel")) : class$anon$client$TrustModel).getName() + "_exceptionTooFewCountries";
        MSG_EXCEPTION_TOO_MANY_COUNTRIES = (class$anon$client$TrustModel == null ? (class$anon$client$TrustModel = TrustModel.class$("anon.client.TrustModel")) : class$anon$client$TrustModel).getName() + "_exceptionTooManyCountries";
        MSG_EXCEPTION_NOT_INTERNATIONAL = (class$anon$client$TrustModel == null ? (class$anon$client$TrustModel = TrustModel.class$("anon.client.TrustModel")) : class$anon$client$TrustModel).getName() + "_exceptionNotInternational";
        MSG_EXCEPTION_INTERNATIONAL = (class$anon$client$TrustModel == null ? (class$anon$client$TrustModel = TrustModel.class$("anon.client.TrustModel")) : class$anon$client$TrustModel).getName() + "_exceptionInternational";
        MSG_EXCEPTION_NOT_ENOUGH_ANON = (class$anon$client$TrustModel == null ? (class$anon$client$TrustModel = TrustModel.class$("anon.client.TrustModel")) : class$anon$client$TrustModel).getName() + "_exceptionNotEnoughAnon";
        MSG_EXCEPTION_BLACKLISTED = (class$anon$client$TrustModel == null ? (class$anon$client$TrustModel = TrustModel.class$("anon.client.TrustModel")) : class$anon$client$TrustModel).getName() + "_exceptionBlacklisted";
        MSG_EXCEPTION_NOT_ENOUGH_SPEED = (class$anon$client$TrustModel == null ? (class$anon$client$TrustModel = TrustModel.class$("anon.client.TrustModel")) : class$anon$client$TrustModel).getName() + "_exceptionNotEnoughSpeed";
        MSG_EXCEPTION_RESPONSE_TIME_TOO_HIGH = (class$anon$client$TrustModel == null ? (class$anon$client$TrustModel = TrustModel.class$("anon.client.TrustModel")) : class$anon$client$TrustModel).getName() + "_exceptionResponseTimeTooHigh";
        ms_trustModels = new Vector();
        ms_strContext = "jondonym";
        ms_bFreeAllowed = true;
        ms_trustModelObservable = new InnerObservable();
        TrustModel trustModel = new TrustModel(MSG_ALL_SERVICES, -1L, true);
        trustModel.setAttribute(class$anon$client$TrustModel$ContextAttribute == null ? (class$anon$client$TrustModel$ContextAttribute = TrustModel.class$("anon.client.TrustModel$ContextAttribute")) : class$anon$client$TrustModel$ContextAttribute, 7);
        TRUST_MODEL_ALL_SERVICES = trustModel;
        TrustModel.addCustomFilter();
        TrustModel.setCurrentTrustModel((TrustModel)ms_trustModels.elementAt(0));
    }

    public static class DelayAttribute
    extends TrustAttribute {
        public DelayAttribute(int n, Object object, boolean bl) {
            super(5, object, bl);
        }

        public void checkTrust(MixCascade mixCascade) throws TrustException {
            if (Database.getInstance(class$anon$infoservice$PerformanceInfo == null ? (class$anon$infoservice$PerformanceInfo = TrustModel.class$("anon.infoservice.PerformanceInfo")) : class$anon$infoservice$PerformanceInfo).getNumberOfEntries() == 0) {
                return;
            }
            PerformanceEntry performanceEntry = PerformanceInfo.getLowestCommonBoundEntry(mixCascade.getId());
            Object object = this.getConditionValue();
            if (object == null || TrustModel.getIntegerConditionValue(object) == Integer.MAX_VALUE) {
                return;
            }
            if (performanceEntry == null || performanceEntry.getBound(1).getBound() == 0) {
                if (this.isNoDataIgnored()) {
                    return;
                }
                throw new TrustException(mixCascade, JAPMessages.getString(MSG_EXCEPTION_RESPONSE_TIME_TOO_HIGH));
            }
            if (this.getTrustCondition() == 5 && (performanceEntry == null || performanceEntry.getBound(1).getBound() < 0 || performanceEntry.getBound(1).getBound() > TrustModel.getIntegerConditionValue(object))) {
                throw new TrustException(mixCascade, JAPMessages.getString(MSG_EXCEPTION_RESPONSE_TIME_TOO_HIGH));
            }
        }

        public static Object getDefaultValue() {
            return new Integer(Integer.MAX_VALUE);
        }
    }

    public static class SpeedAttribute
    extends TrustAttribute {
        public SpeedAttribute(int n, Object object, boolean bl) {
            super(3, object, bl);
        }

        public void checkTrust(MixCascade mixCascade) throws TrustException {
            if (Database.getInstance(class$anon$infoservice$PerformanceInfo == null ? (class$anon$infoservice$PerformanceInfo = TrustModel.class$("anon.infoservice.PerformanceInfo")) : class$anon$infoservice$PerformanceInfo).getNumberOfEntries() == 0) {
                return;
            }
            PerformanceEntry performanceEntry = PerformanceInfo.getLowestCommonBoundEntry(mixCascade.getId());
            Object object = this.getConditionValue();
            if (object == null || TrustModel.getIntegerConditionValue(object) <= 0) {
                return;
            }
            if (performanceEntry == null || performanceEntry.getBound(0).getBound() == Integer.MAX_VALUE) {
                if (this.isNoDataIgnored()) {
                    return;
                }
                throw new TrustException(mixCascade, JAPMessages.getString(MSG_EXCEPTION_NOT_ENOUGH_SPEED));
            }
            if (this.getTrustCondition() == 3 && (performanceEntry == null || performanceEntry.getBound(0).getBound() < TrustModel.getIntegerConditionValue(object))) {
                throw new TrustException(mixCascade, JAPMessages.getString(MSG_EXCEPTION_NOT_ENOUGH_SPEED));
            }
        }
    }

    public static class OperatorBlacklistAttribute
    extends TrustAttribute {
        public OperatorBlacklistAttribute(int n, Object object, boolean bl) {
            super(6, object == null || !(object instanceof Vector) ? new Vector() : object, bl);
        }

        public void checkTrust(MixCascade mixCascade) throws TrustException, ServiceSignatureException {
            Object object = this.getConditionValue();
            if (object == null || !(object instanceof Vector)) {
                return;
            }
            if (this.getTrustCondition() == 6) {
                for (int i = 0; i < mixCascade.getNumberOfMixes(); ++i) {
                    Vector vector = (Vector)object;
                    MixInfo mixInfo = mixCascade.getMixInfo(i);
                    ServiceOperator serviceOperator = null;
                    if (mixInfo != null) {
                        serviceOperator = mixInfo.getServiceOperator();
                    }
                    if (serviceOperator != null && vector.contains(serviceOperator)) {
                        throw new TrustException(mixCascade, JAPMessages.getString(MSG_EXCEPTION_BLACKLISTED));
                    }
                    if (serviceOperator == null || serviceOperator.getOrganization() == null) continue;
                    for (int j = 0; j < vector.size(); ++j) {
                        ServiceOperator serviceOperator2 = (ServiceOperator)vector.elementAt(j);
                        if (serviceOperator2.getOrganization() == null || !serviceOperator2.getOrganization().equals(serviceOperator.getOrganization())) continue;
                        throw new TrustException(mixCascade, JAPMessages.getString(MSG_EXCEPTION_BLACKLISTED));
                    }
                }
            }
        }
    }

    public static class AnonLevelAttribute
    extends TrustAttribute {
        public AnonLevelAttribute(int n, Object object, boolean bl) {
            super(3, object, bl);
        }

        public void checkTrust(MixCascade mixCascade) throws TrustException, ServiceSignatureException {
            Object object = this.getConditionValue();
            StatusInfo statusInfo = (StatusInfo)Database.getInstance(class$anon$infoservice$StatusInfo == null ? (class$anon$infoservice$StatusInfo = TrustModel.class$("anon.infoservice.StatusInfo")) : class$anon$infoservice$StatusInfo).getEntryById(mixCascade.getId());
            if (this.getTrustCondition() == 3 && (statusInfo == null || object != null && statusInfo.getAnonLevel() < TrustModel.getIntegerConditionValue(object))) {
                throw new TrustException(mixCascade, JAPMessages.getString(MSG_EXCEPTION_NOT_ENOUGH_ANON));
            }
        }
    }

    public static class InternationalAttribute
    extends TrustAttribute {
        public InternationalAttribute(int n, Object object, boolean bl) {
            super(n, object, bl);
        }

        public void checkTrust(MixCascade mixCascade) throws TrustException, ServiceSignatureException {
            Object object = this.getConditionValue();
            if (object == null) {
                return;
            }
            if (this.getTrustCondition() == 3 && mixCascade.getNumberOfCountries() < TrustModel.getIntegerConditionValue(object)) {
                throw new TrustException(mixCascade, JAPMessages.getString(MSG_EXCEPTION_TOO_FEW_COUNTRIES) + ": " + mixCascade.getNumberOfCountries());
            }
            if (this.getTrustCondition() == 5 && mixCascade.getNumberOfCountries() > TrustModel.getIntegerConditionValue(object)) {
                throw new TrustException(mixCascade, JAPMessages.getString(MSG_EXCEPTION_TOO_MANY_COUNTRIES) + ": " + mixCascade.getNumberOfCountries());
            }
        }
    }

    public static class UserDefinedAttribute
    extends TrustAttribute {
        public UserDefinedAttribute(int n, Object object, boolean bl) {
            super(n, object, bl);
        }

        public void checkTrust(MixCascade mixCascade) throws TrustException, ServiceSignatureException {
            if (this.getTrustCondition() == 2) {
                if (mixCascade.isUserDefined()) {
                    return;
                }
                throw new TrustException(mixCascade, JAPMessages.getString(MSG_EXCEPTION_NOT_USER_DEFINED));
            }
        }
    }

    public static class NumberOfMixesAttribute
    extends TrustAttribute {
        public NumberOfMixesAttribute(int n, Object object, boolean bl) {
            super(n, object, bl);
        }

        public static int getDefaultCondition() {
            return 0;
        }

        public static Object getDefaultValue() {
            return new Integer(1);
        }

        public void checkTrust(MixCascade mixCascade) throws TrustException, ServiceSignatureException {
            Object object = this.getConditionValue();
            if (object == null) {
                return;
            }
            int n = TrustModel.getIntegerConditionValue(object);
            if (this.getTrustCondition() == 3 && (mixCascade == null || mixCascade.getNumberOfOperators() < n)) {
                throw new TrustException(mixCascade, JAPMessages.getString(MSG_EXCEPTION_NOT_ENOUGH_MIXES) + ": " + mixCascade.getNumberOfOperators());
            }
            if (this.getTrustCondition() == 5 && mixCascade != null && mixCascade.getNumberOfOperators() > n) {
                throw new TrustException(mixCascade, JAPMessages.getString(MSG_EXCEPTION_TOO_MANY_MIXES) + ": " + mixCascade.getNumberOfOperators());
            }
        }
    }

    public static class SocksAttribute
    extends TrustAttribute {
        public SocksAttribute(int n, Object object, boolean bl) {
            super(n, object, bl);
        }

        public void checkTrust(MixCascade mixCascade) throws TrustException, ServiceSignatureException {
            if (!mixCascade.isSocks5Supported() && this.getTrustCondition() == 2) {
                throw new TrustException(mixCascade, JAPMessages.getString(MSG_EXCEPTION_NO_SOCKS));
            }
        }
    }

    public static class DataRetentionAttribute
    extends TrustAttribute {
        public DataRetentionAttribute(int n, Object object, boolean bl) {
            super(n, object, bl);
        }

        public void checkTrust(MixCascade mixCascade) throws TrustException, ServiceSignatureException {
            if (mixCascade.getDataRetentionInformation() != null && this.getTrustCondition() == 1) {
                throw new TrustException(mixCascade, JAPMessages.getString(MSG_EXCEPTION_DATA_RETENTION));
            }
        }
    }

    public static class UsePremiumAttribute
    extends TrustAttribute {
        public UsePremiumAttribute(int n, Object object, boolean bl) {
            super(n, object, bl);
        }

        public void checkTrust(MixCascade mixCascade) throws TrustException, ServiceSignatureException {
            if (mixCascade.isPayment()) {
                if (this.getTrustCondition() == 1) {
                    throw new TrustException(mixCascade, JAPMessages.getString(MSG_EXCEPTION_PAY_CASCADE));
                }
            } else if (this.getTrustCondition() == 2) {
                throw new TrustException(mixCascade, JAPMessages.getString(MSG_EXCEPTION_FREE_CASCADE));
            }
        }
    }

    public static class PremiumChargedAccountAttribute
    extends TrustAttribute {
        public PremiumChargedAccountAttribute(int n, Object object, boolean bl) {
            super(n, object, bl);
        }

        public void checkTrust(MixCascade mixCascade) throws TrustException, ServiceSignatureException {
            if (this.getTrustCondition() != 0 && mixCascade.isPayment() && PayAccountsFile.getInstance().getChargedAccount(mixCascade.getPIID()) == null) {
                throw new TrustException(mixCascade, JAPMessages.getString(MSG_EXCEPTION_PAY_CASCADE));
            }
        }
    }

    public static class ForcePremiumIfChargedAccountAttribute
    extends TrustAttribute {
        public ForcePremiumIfChargedAccountAttribute(int n, Object object, boolean bl) {
            super(n, object, bl);
        }

        public static int getDefaultCondition() {
            return 2;
        }

        public void checkTrust(MixCascade mixCascade) throws TrustException, ServiceSignatureException {
            if (this.getTrustCondition() == 2 && !mixCascade.isPayment() && !mixCascade.isPayment()) {
                Vector vector = Database.getInstance(class$anon$pay$PaymentInstanceDBEntry == null ? (class$anon$pay$PaymentInstanceDBEntry = TrustModel.class$("anon.pay.PaymentInstanceDBEntry")) : class$anon$pay$PaymentInstanceDBEntry).getEntryList();
                for (int i = 0; i < vector.size(); ++i) {
                    PaymentInstanceDBEntry paymentInstanceDBEntry = (PaymentInstanceDBEntry)vector.elementAt(i);
                    if (paymentInstanceDBEntry.isTest() || PayAccountsFile.getInstance().getChargedAccount(paymentInstanceDBEntry.getId()) == null) continue;
                    throw new TrustException(mixCascade, JAPMessages.getString(MSG_EXCEPTION_FREE_CASCADE));
                }
            }
        }
    }

    public static class ForcePremiumIfExistingUserAttribute
    extends TrustAttribute {
        public ForcePremiumIfExistingUserAttribute(int n, Object object, boolean bl) {
            super(n, object, bl);
        }

        public static int getDefaultCondition() {
            return 0;
        }

        public void checkTrust(MixCascade mixCascade) throws TrustException, ServiceSignatureException {
            if (this.getTrustCondition() == 2) {
                if (mixCascade.isPayment()) {
                    if (mixCascade.getNumberOfOperators() < 2) {
                        throw new TrustException(mixCascade, JAPMessages.getString(MSG_EXCEPTION_NOT_ENOUGH_MIXES) + ": " + mixCascade.getNumberOfOperators());
                    }
                    if (!mixCascade.isSocks5Supported()) {
                        throw new TrustException(mixCascade, JAPMessages.getString(MSG_EXCEPTION_NO_SOCKS));
                    }
                } else if (!mixCascade.isPayment() && !PayAccountsFile.getInstance().isNewUser()) {
                    throw new TrustException(mixCascade, JAPMessages.getString(MSG_EXCEPTION_FREE_CASCADE));
                }
            }
        }
    }

    public static class ContextAttribute
    extends TrustAttribute {
        public ContextAttribute(int n, Object object, boolean bl) {
            super(n, object, bl);
        }

        public void checkTrust(MixCascade mixCascade) throws TrustException, ServiceSignatureException {
            String string = mixCascade.getContext();
            String string2 = ms_strContext;
            if (this.getTrustCondition() == 2 && !string.equals(string2)) {
                throw new TrustException(mixCascade, JAPMessages.getString(MSG_EXCEPTION_WRONG_SERVICE_CONTEXT));
            }
            if (this.getTrustCondition() == 1 && string.equals(string2)) {
                throw new TrustException(mixCascade, JAPMessages.getString(MSG_EXCEPTION_WRONG_SERVICE_CONTEXT));
            }
            if (!(string.equals(string2) || string2.startsWith("jondonym") && string.equals("jondonym.premium"))) {
                throw new TrustException(mixCascade, JAPMessages.getString(MSG_EXCEPTION_WRONG_SERVICE_CONTEXT));
            }
        }
    }

    public static abstract class TrustAttribute
    implements IXMLEncodable {
        public static final int CATEGORY_DEFAULT = 0;
        public static final String XML_ELEMENT_NAME = "TrustAttribute";
        public static final String XML_VALUE_ELEMENT_NAME = "ConditionValue";
        public static final String XML_VALUE_CONTAINER_ELEMENT_NAME = "ConditionValueList";
        public static final String XML_ATTR_NAME = "name";
        public static final String XML_ATTR_TRUST_CONDITION = "trustCondition";
        public static final String XML_ATTR_CONDITION_VALUE = "conditonValue";
        public static final String XML_ATTR_IGNORE_NO_DATA = "ignoreNoData";
        private int m_category;
        private boolean m_bIgnoreNoDataAvailable;
        private int m_trustCondition;
        private Object m_conditionValue;

        protected TrustAttribute(int n, Object object, boolean bl) {
            this.m_trustCondition = n;
            this.m_conditionValue = object;
            this.m_category = 0;
            this.m_bIgnoreNoDataAvailable = bl;
        }

        public static int getDefaultCondition() {
            return 0;
        }

        public static Object getDefaultValue() {
            return new Integer(0);
        }

        public boolean isNoDataIgnored() {
            return this.m_bIgnoreNoDataAvailable;
        }

        public final int getCategory() {
            return this.m_category;
        }

        public int getTrustCondition() {
            return this.m_trustCondition;
        }

        public Object getConditionValue() {
            return this.m_conditionValue;
        }

        public boolean isTrusted(MixCascade mixCascade) {
            try {
                this.checkTrust(mixCascade);
                return true;
            }
            catch (TrustException trustException) {
            }
            catch (ServiceSignatureException serviceSignatureException) {
                // empty catch block
            }
            return false;
        }

        public abstract void checkTrust(MixCascade var1) throws TrustException, ServiceSignatureException;

        public Element toXmlElement(Document document) {
            if (document == null) {
                return null;
            }
            Element element = document.createElement("TrustAttribute");
            XMLUtil.setAttribute(element, "name", this.getClass().getName());
            XMLUtil.setAttribute(element, "trustCondition", this.m_trustCondition);
            XMLUtil.setAttribute(element, "ignoreNoData", this.m_bIgnoreNoDataAvailable);
            if (this.m_conditionValue instanceof Integer) {
                XMLUtil.setAttribute(element, "conditonValue", (Integer)this.m_conditionValue);
            } else if (this.m_conditionValue instanceof Vector) {
                Vector vector = (Vector)this.m_conditionValue;
                Element element2 = document.createElement("ConditionValueList");
                for (int i = 0; i < vector.size(); ++i) {
                    Element element3 = document.createElement("ConditionValue");
                    XMLUtil.setValue((Node)element3, ((ServiceOperator)vector.elementAt(i)).getId());
                    element2.appendChild(element3);
                }
                element.appendChild(element2);
            }
            return element;
        }

        public static TrustAttribute fromXmlElement(Element element) throws XMLParseException {
            TrustAttribute trustAttribute;
            if (element == null) {
                return null;
            }
            XMLUtil.assertNodeName(element, "TrustAttribute");
            XMLUtil.assertNotNull(element, "name");
            String string = XMLUtil.parseAttribute((Node)element, "name", null);
            int n = XMLUtil.parseAttribute((Node)element, "trustCondition", 0);
            int n2 = XMLUtil.parseAttribute((Node)element, "conditonValue", 0);
            boolean bl = XMLUtil.parseAttribute((Node)element, "ignoreNoData", false);
            try {
                Serializable serializable = null;
                if (n == 6) {
                    Node node = XMLUtil.getFirstChildByName(element, "ConditionValueList");
                    XMLUtil.assertNotNull(node);
                    NodeList nodeList = node.getChildNodes();
                    serializable = new Vector();
                    for (int i = 0; i < nodeList.getLength(); ++i) {
                        ServiceOperator serviceOperator = (ServiceOperator)Database.getInstance(class$anon$infoservice$ServiceOperator == null ? TrustModel.class$("anon.infoservice.ServiceOperator") : class$anon$infoservice$ServiceOperator).getEntryById(XMLUtil.parseValue(nodeList.item(i), null));
                        if (serviceOperator == null) continue;
                        ((Vector)serializable).addElement(serviceOperator);
                    }
                } else {
                    serializable = new Integer(n2);
                }
                if (string != null && string.startsWith("jap.TrustModel")) {
                    string = (class$anon$client$TrustModel == null ? (class$anon$client$TrustModel = TrustModel.class$("anon.client.TrustModel")) : class$anon$client$TrustModel).getName() + string.substring("jap.TrustModel".length(), string.length());
                }
                if ((trustAttribute = TrustAttribute.getInstance(Class.forName(string), n, serializable, bl)) == null) {
                    throw new XMLParseException("TrustAttribute", "Could not create TrustAttribute + " + string + "!");
                }
            }
            catch (Exception exception) {
                LogHolder.log(5, LogType.DB, exception);
                throw new XMLParseException("TrustAttribute", exception.getMessage());
            }
            return trustAttribute;
        }

        public static TrustAttribute getInstance(Class class_, int n, Object object, boolean bl) {
            try {
                return (TrustAttribute)class_.getConstructor(Integer.TYPE, class$java$lang$Object == null ? (class$java$lang$Object = TrustModel.class$("java.lang.Object")) : class$java$lang$Object, Boolean.TYPE).newInstance(new Integer(n), object, new Boolean(bl));
            }
            catch (Exception exception) {
                LogHolder.log(2, LogType.MISC, "Could not create " + class_);
                return null;
            }
        }
    }

    public static class InnerObservable
    extends Observable {
        public void setChanged() {
            super.setChanged();
        }
    }
}

