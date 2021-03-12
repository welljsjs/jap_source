/*
 * Decompiled with CFR 0.150.
 */
package anon.terms;

import anon.crypto.MultiCertPath;
import anon.crypto.SignatureVerifier;
import anon.crypto.XMLSignature;
import anon.infoservice.Database;
import anon.infoservice.OperatorAddress;
import anon.infoservice.ServiceOperator;
import anon.terms.TCComponent;
import anon.terms.TCComposite;
import anon.terms.TermsAndConditionsTranslation;
import anon.terms.template.Section;
import anon.terms.template.TermsAndConditionsTemplate;
import anon.util.IXMLEncodable;
import anon.util.JAPMessages;
import anon.util.XMLParseException;
import anon.util.XMLUtil;
import java.security.SignatureException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Locale;
import logging.LogHolder;
import logging.LogType;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class TermsAndConditions
implements IXMLEncodable {
    public static final String XML_ATTR_ACCEPTED = "accepted";
    public static final String XML_ATTR_DATE = "date";
    private static final String MSG_DISPLAY_ERROR = (class$anon$terms$TermsAndConditions == null ? (class$anon$terms$TermsAndConditions = TermsAndConditions.class$("anon.terms.TermsAndConditions")) : class$anon$terms$TermsAndConditions).getName() + "_displayError";
    public static final String XML_ELEMENT_CONTAINER_NAME = "TermsAndConditionsList";
    public static final String XML_ELEMENT_NAME = "TermsAndConditions";
    public static final String XML_ELEMENT_TRANSLATION_NAME = "TCTranslation";
    public static final String DATE_FORMAT = "yyyyMMdd";
    private ServiceOperator operator;
    private Date m_date;
    private Hashtable translations;
    private Translation defaultTl = null;
    private boolean accepted;
    private static final Hashtable tcHashtable = new Hashtable();
    static /* synthetic */ Class class$anon$terms$TermsAndConditions;
    static /* synthetic */ Class class$anon$infoservice$ServiceOperator;

    public TermsAndConditions(ServiceOperator serviceOperator, String string) throws ParseException {
        this(serviceOperator, new SimpleDateFormat(DATE_FORMAT).parse(string));
    }

    public TermsAndConditions(ServiceOperator serviceOperator, Date date) throws ParseException {
        if (serviceOperator == null) {
            throw new NullPointerException("Operator of terms and conditions must not be null!");
        }
        this.operator = serviceOperator;
        if (date == null) {
            throw new NullPointerException("Date of terms and conditions must not be null!");
        }
        this.m_date = date;
        if (this.m_date == null) {
            throw new IllegalArgumentException("Date has not the valid format yyyyMMdd");
        }
        this.translations = new Hashtable();
        this.accepted = false;
    }

    public TermsAndConditions(Element element) throws XMLParseException, ParseException, SignatureException {
        this(element, null, true);
    }

    public TermsAndConditions(Element element, ServiceOperator serviceOperator, boolean bl) throws XMLParseException, ParseException, SignatureException {
        String string;
        if (serviceOperator != null) {
            this.operator = serviceOperator;
        } else {
            string = XMLUtil.parseAttribute((Node)element, "id", null);
            if (string == null) {
                throw new XMLParseException("attribute 'id' of TermsAndConditions must not be null!");
            }
            string = string.toUpperCase();
            this.operator = (ServiceOperator)Database.getInstance(class$anon$infoservice$ServiceOperator == null ? (class$anon$infoservice$ServiceOperator = TermsAndConditions.class$("anon.infoservice.ServiceOperator")) : class$anon$infoservice$ServiceOperator).getEntryById(string);
            if (this.operator == null) {
                throw new XMLParseException("invalid  id " + string + ": no operator found with this subject key identifier");
            }
        }
        string = XMLUtil.parseAttribute((Node)element, XML_ATTR_DATE, null);
        if (string == null) {
            throw new XMLParseException("attribute 'date' must not be null!");
        }
        this.m_date = new SimpleDateFormat(DATE_FORMAT).parse(string);
        this.translations = new Hashtable();
        Element element2 = (Element)XMLUtil.getFirstChildByName(element, XML_ELEMENT_TRANSLATION_NAME);
        while (element2 != null) {
            this.addTranslation(new Translation(element2), bl);
            element2 = (Element)XMLUtil.getNextSiblingByName(element2, XML_ELEMENT_TRANSLATION_NAME);
        }
        this.accepted = XMLUtil.parseAttribute((Node)element, XML_ATTR_ACCEPTED, false);
    }

    public String getDateString() {
        return new SimpleDateFormat(DATE_FORMAT).format(this.m_date);
    }

    public void addTranslation(Element element) throws XMLParseException, SignatureException {
        this.addTranslation(new Translation(element), true);
    }

    public Translation removeTranslation(String string) {
        Translation translation = (Translation)this.translations.remove(string.trim().toLowerCase());
        if (this.defaultTl == translation) {
            this.defaultTl = null;
        }
        return translation;
    }

    public Translation removeTranslation(Locale locale) {
        return this.removeTranslation(locale.getLanguage());
    }

    public Translation initializeEmptyTranslation(String string) {
        Translation translation = new Translation();
        translation.setLocale(string.trim().toLowerCase());
        try {
            this.addTranslation(translation, false);
        }
        catch (SignatureException signatureException) {
            // empty catch block
        }
        return translation;
    }

    public Translation initializeEmptyTranslation(Locale locale) {
        return this.initializeEmptyTranslation(locale.getLanguage());
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private synchronized void addTranslation(Translation translation, boolean bl) throws SignatureException {
        if (bl) {
            if (!translation.isVerified()) {
                throw new SignatureException("Translation [" + translation.getLocale() + "] of " + this.operator.getOrganization() + " is not verified");
            }
            if (!translation.checkId()) {
                throw new SignatureException("Translation [" + translation.getLocale() + "] is not signed by its operator '" + this.operator.getOrganization() + "'");
            }
        }
        TermsAndConditions termsAndConditions = this;
        synchronized (termsAndConditions) {
            if (translation.isDefaultTranslation()) {
                this.defaultTl = translation;
            }
        }
        this.translations.put(translation.getLocale(), translation);
    }

    public synchronized Translation getDefaultTranslation() {
        return this.defaultTl;
    }

    public Translation getTranslation(Locale locale) {
        return this.getTranslation(locale.getLanguage());
    }

    public Translation getTranslation(String string) {
        return (Translation)this.translations.get(string.trim().toLowerCase());
    }

    public Enumeration getAllTranslations() {
        return this.translations.elements();
    }

    public String getTemplateReferenceId(String string) {
        Translation translation = (Translation)this.translations.get(string.trim().toLowerCase());
        return translation != null ? translation.getTemplateReferenceId() : null;
    }

    public boolean hasTranslation(String string) {
        return this.translations.containsKey(string.trim().toLowerCase());
    }

    public boolean hasTranslation(Locale locale) {
        return this.hasTranslation(locale.getLanguage());
    }

    public boolean hasTranslations() {
        return !this.translations.isEmpty();
    }

    public synchronized boolean hasDefaultTranslation() {
        return this.defaultTl != null;
    }

    public ServiceOperator getOperator() {
        return this.operator;
    }

    public void setDate(Date date) {
        this.m_date = date;
    }

    public Date getDate() {
        return this.m_date;
    }

    public synchronized void setAccepted(boolean bl) {
        this.accepted = bl;
    }

    public boolean isAccepted() {
        return this.accepted;
    }

    public static void storeTermsAndConditions(TermsAndConditions termsAndConditions) {
        tcHashtable.put(termsAndConditions.operator, termsAndConditions);
    }

    public static TermsAndConditions getTermsAndConditions(ServiceOperator serviceOperator) {
        return (TermsAndConditions)tcHashtable.get(serviceOperator);
    }

    public static void removeTermsAndConditions(TermsAndConditions termsAndConditions) {
        tcHashtable.remove(termsAndConditions.operator);
    }

    public static void removeTermsAndConditions(ServiceOperator serviceOperator) {
        tcHashtable.remove(serviceOperator);
    }

    public static Element getAllTermsAndConditionsAsXMLElement(Document document) {
        Enumeration enumeration = null;
        Element element = document.createElement(XML_ELEMENT_CONTAINER_NAME);
        enumeration = tcHashtable.elements();
        TermsAndConditions termsAndConditions = null;
        while (enumeration.hasMoreElements()) {
            termsAndConditions = (TermsAndConditions)enumeration.nextElement();
            if (!termsAndConditions.hasTranslations()) continue;
            element.appendChild(termsAndConditions.toXmlElement(document));
        }
        return element;
    }

    public static void loadTermsAndConditionsFromXMLElement(Element element) {
        if (element == null) {
            LogHolder.log(4, LogType.MISC, "TC list root is null!");
            return;
        }
        Element element2 = (Element)XMLUtil.getFirstChildByName(element, XML_ELEMENT_NAME);
        while (element2 != null) {
            try {
                TermsAndConditions.storeTermsAndConditions(new TermsAndConditions(element2));
            }
            catch (XMLParseException xMLParseException) {
                LogHolder.log(4, LogType.MISC, "XML error occured while parsing the TC node:", xMLParseException);
            }
            catch (ParseException parseException) {
                LogHolder.log(4, LogType.MISC, "Could not parse the TC node:", parseException);
            }
            catch (SignatureException signatureException) {
                LogHolder.log(4, LogType.MISC, "Terms and Condition cannot be loaded due to a wrong signature:", signatureException);
            }
            element2 = (Element)XMLUtil.getNextSiblingByName(element2, XML_ELEMENT_NAME);
        }
    }

    public String getHTMLText(Locale locale) {
        return this.getHTMLText(locale.getLanguage());
    }

    public String getHTMLText(String string) {
        Translation translation = this.getTranslation(string);
        if (translation == null) {
            translation = this.getDefaultTranslation();
        }
        return TermsAndConditions.getHTMLText(translation);
    }

    public static String getHTMLText(Translation translation) {
        try {
            if (translation == null) {
                throw new NullPointerException("Translation is null!)");
            }
            TermsAndConditionsTemplate termsAndConditionsTemplate = TermsAndConditionsTemplate.getById(translation.getTemplateReferenceId(), false);
            if (termsAndConditionsTemplate == null) {
                throw new NullPointerException("Associated template '" + translation.getTemplateReferenceId() + "' for" + " translation [" + translation.getLocale() + "] of terms and conditions for operator '" + translation.getOperator().getOrganization() + "' not found.");
            }
            return termsAndConditionsTemplate.transform(translation);
        }
        catch (Exception exception) {
            LogHolder.log(2, LogType.MISC, exception);
            StringBuffer stringBuffer = new StringBuffer();
            stringBuffer.append("<html><head><title>");
            stringBuffer.append(JAPMessages.getString("error"));
            stringBuffer.append("</title></head><body><head><h1>");
            stringBuffer.append(JAPMessages.getString("error"));
            stringBuffer.append("</h1><h2>");
            stringBuffer.append(JAPMessages.getString(MSG_DISPLAY_ERROR));
            stringBuffer.append("</h2><p>");
            stringBuffer.append(exception);
            stringBuffer.append("</p></body></html>");
            return stringBuffer.toString();
        }
    }

    public boolean isSignatureObsolete() {
        return false;
    }

    public boolean equals(Object object) {
        return this.operator.equals(((TermsAndConditions)object).operator);
    }

    public int compareTo(Object object) {
        TermsAndConditions termsAndConditions = (TermsAndConditions)object;
        return this.m_date.equals(termsAndConditions.getDate()) ? 0 : (this.m_date.before(termsAndConditions.getDate()) ? -1 : 1);
    }

    public boolean isMostRecent(String string) throws ParseException {
        return this.isMostRecent(new SimpleDateFormat(DATE_FORMAT).parse(string));
    }

    public boolean isMostRecent(Date date) {
        return this.m_date.equals(date) || this.m_date.after(date);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private Element xmlOut(Document document, boolean bl) {
        Element element = this.createTCRoot(document);
        Enumeration enumeration = null;
        TermsAndConditions termsAndConditions = this;
        synchronized (termsAndConditions) {
            if (this.accepted) {
                XMLUtil.setAttribute(element, XML_ATTR_ACCEPTED, this.accepted);
            }
            enumeration = this.translations.elements();
        }
        while (enumeration.hasMoreElements()) {
            element.appendChild(bl ? ((Translation)enumeration.nextElement()).toXmlElement(document) : ((Translation)enumeration.nextElement()).createXMLOutput(document));
        }
        return element;
    }

    public Element createTCRoot(Document document) {
        Element element = document.createElement(XML_ELEMENT_NAME);
        XMLUtil.setAttribute(element, "id", this.operator.getId());
        XMLUtil.setAttribute(element, XML_ATTR_DATE, this.getDateString());
        return element;
    }

    public Element toXmlElement(Document document) {
        return this.xmlOut(document, true);
    }

    public Element createXMLOutput(Document document) {
        return this.xmlOut(document, false);
    }

    static /* synthetic */ Class class$(String string) {
        try {
            return Class.forName(string);
        }
        catch (ClassNotFoundException classNotFoundException) {
            throw new NoClassDefFoundError(classNotFoundException.getMessage());
        }
    }

    public class Translation
    implements IXMLEncodable {
        public static final String XML_ELEMENT_NAME = "TCTranslation";
        public static final String XML_ELEMENT_CONTAINER_NAME = "TermsAndConditions";
        public static final String XML_ELEMENT_PRIVACY_POLICY = "PrivacyPolicyUrl";
        public static final String XML_ELEMENT_LEGAL_OPINIONS = "LegalOpinionsUrl";
        public static final String XML_ELEMENT_OPERATIONAL_AGREEMENT = "OperationalAgreementUrl";
        public static final String XML_ATTR_LOCALE = "locale";
        public static final String XML_ATTR_DEFAULT_LOCALE = "default";
        public static final String XML_ATTR_REFERENCE_ID = "referenceId";
        public static final String PROPERTY_NAME_PRIVACY_POLICY = "privacyPolicyUrl";
        public static final String PROPERTY_NAME_LEGAL_OPINIONS = "legalOpinionsUrl";
        public static final String PROPERTY_NAME_OPERATIONAL_AGREEMENT = "operationalAgreementUrl";
        public static final String PROPERTY_NAME_TEMPLATE_REFERENCE_ID = "templateReferenceId";
        private String templateReferenceId;
        private String locale;
        private boolean defaultTranslation;
        private Element translationElement;
        private String privacyPolicyUrl;
        private String legalOpinionsUrl;
        private String operationalAgreementUrl;
        private OperatorAddress operatorAddress;
        private XMLSignature signature = null;
        private MultiCertPath certPath = null;
        private TCComposite sections = new TCComposite();

        public Translation(Element element) throws XMLParseException {
            this(element, true);
        }

        private Translation() {
        }

        public Translation(Element element, boolean bl) throws XMLParseException {
            this.templateReferenceId = XMLUtil.parseAttribute((Node)element, XML_ATTR_REFERENCE_ID, "");
            if (bl && this.templateReferenceId.equals("")) {
                LogHolder.log(4, LogType.MISC, "TC translation must refer to a valid TC template");
            }
            this.locale = XMLUtil.parseAttribute((Node)element, XML_ATTR_LOCALE, "");
            if (bl && this.locale.equals("")) {
                throw new XMLParseException("TC translation must set attribute 'locale'");
            }
            this.locale = this.locale.trim().toLowerCase();
            this.setDefaultTranslation(XMLUtil.parseAttribute((Node)element, XML_ATTR_DEFAULT_LOCALE, false));
            this.privacyPolicyUrl = XMLUtil.parseValue(XMLUtil.getFirstChildByName(element, XML_ELEMENT_PRIVACY_POLICY), "");
            this.legalOpinionsUrl = XMLUtil.parseValue(XMLUtil.getFirstChildByName(element, XML_ELEMENT_LEGAL_OPINIONS), "");
            this.operationalAgreementUrl = XMLUtil.parseValue(XMLUtil.getFirstChildByName(element, XML_ELEMENT_OPERATIONAL_AGREEMENT), "");
            Element element2 = (Element)XMLUtil.getFirstChildByName(element, "Operator");
            this.operatorAddress = element2 != null ? new OperatorAddress(element2) : null;
            this.translationElement = element;
            this.signature = SignatureVerifier.getInstance().getVerifiedXml(element, 1);
            if (this.signature != null) {
                this.certPath = this.signature.getMultiCertPath();
            }
            NodeList nodeList = element.getElementsByTagName(Section.XML_ELEMENT_NAME);
            Section section = null;
            for (int i = 0; i < nodeList.getLength(); ++i) {
                section = new Section(nodeList.item(i));
                this.sections.addTCComponent(section);
            }
        }

        public boolean hasContent() {
            return this.sections.hasContent();
        }

        public void setTemplateReferenceId(String string) {
            this.templateReferenceId = string;
        }

        public String getTemplateReferenceId() {
            return this.templateReferenceId;
        }

        public void setLocale(String string) {
            this.locale = string;
        }

        public String getLocale() {
            return this.locale;
        }

        public boolean isDefaultTranslation() {
            return this.defaultTranslation;
        }

        public void setDefaultTranslation(boolean bl) {
            this.defaultTranslation = bl;
            if (bl) {
                if (TermsAndConditions.this.defaultTl != null) {
                    TermsAndConditions.this.defaultTl.setDefaultTranslation(false);
                }
                TermsAndConditions.this.defaultTl = this;
            }
        }

        public Element getTranslationElement() {
            return this.translationElement != null ? (Element)this.translationElement.cloneNode(true) : null;
        }

        public XMLSignature getSignature() {
            return this.signature;
        }

        public MultiCertPath getCertPath() {
            return this.certPath;
        }

        public boolean isVerified() {
            return this.signature != null ? this.signature.isVerified() : false;
        }

        public boolean isValid() {
            return this.certPath != null ? this.certPath.isValid(new Date()) : false;
        }

        public boolean checkId() {
            return this.certPath != null ? this.certPath.getPath().getSecondCertificate().getSubjectKeyIdentifierConcatenated().equals(this.getOperator().getId()) : false;
        }

        public boolean equals(Object object) {
            if (object == null || !(object instanceof TermsAndConditionsTranslation)) {
                return false;
            }
            return this.locale.equals(((Translation)object).locale);
        }

        public Element toXmlElement(Document document) {
            if (document.equals(this.translationElement.getOwnerDocument())) {
                return this.translationElement;
            }
            try {
                return (Element)XMLUtil.importNode(document, this.translationElement, true);
            }
            catch (XMLParseException xMLParseException) {
                return null;
            }
        }

        public Element createXMLOutput(Document document) {
            Element element;
            TCComponent[] arrtCComponent;
            Element element2 = document.createElement("TCTranslation");
            element2.setAttribute(XML_ATTR_REFERENCE_ID, this.templateReferenceId);
            element2.setAttribute(XML_ATTR_LOCALE, this.locale);
            if (this.defaultTranslation) {
                element2.setAttribute(XML_ATTR_DEFAULT_LOCALE, "true");
            }
            if (this.privacyPolicyUrl != null && !this.privacyPolicyUrl.equals("")) {
                XMLUtil.createChildElementWithValue(element2, XML_ELEMENT_PRIVACY_POLICY, this.privacyPolicyUrl);
            }
            if (this.legalOpinionsUrl != null && !this.legalOpinionsUrl.equals("")) {
                XMLUtil.createChildElementWithValue(element2, XML_ELEMENT_LEGAL_OPINIONS, this.legalOpinionsUrl);
            }
            if (this.operationalAgreementUrl != null && !this.operationalAgreementUrl.equals("")) {
                XMLUtil.createChildElementWithValue(element2, XML_ELEMENT_OPERATIONAL_AGREEMENT, this.operationalAgreementUrl);
            }
            if (this.operatorAddress != null) {
                arrtCComponent = this.operatorAddress.getAddressAsNodeList(document);
                element = null;
                if (arrtCComponent.hasMoreElements()) {
                    element = document.createElement("Operator");
                    element2.appendChild(element);
                }
                while (arrtCComponent.hasMoreElements()) {
                    element.appendChild((Element)arrtCComponent.nextElement());
                }
            }
            if (this.sections != null) {
                arrtCComponent = this.sections.getTCComponents();
                element = null;
                for (int i = 0; i < arrtCComponent.length; ++i) {
                    element = ((Section)arrtCComponent[i]).toXmlElement(document, true);
                    if (element == null) continue;
                    element2.appendChild(element);
                }
            }
            return element2;
        }

        public void setOperatorAddress(OperatorAddress operatorAddress) {
            this.operatorAddress = operatorAddress;
        }

        public OperatorAddress getOperatorAddress() {
            return this.operatorAddress;
        }

        public String toString() {
            return new Locale(this.locale, "").getDisplayLanguage(JAPMessages.getLocale()) + (this.defaultTranslation ? " (default)" : "");
        }

        public Date getDate() {
            return TermsAndConditions.this.getDate();
        }

        public ServiceOperator getOperator() {
            return TermsAndConditions.this.operator;
        }

        public String getPrivacyPolicyUrl() {
            return this.privacyPolicyUrl;
        }

        public void setPrivacyPolicyUrl(String string) {
            this.privacyPolicyUrl = string;
        }

        public String getLegalOpinionsUrl() {
            return this.legalOpinionsUrl;
        }

        public void setLegalOpinionsUrl(String string) {
            this.legalOpinionsUrl = string;
        }

        public String getOperationalAgreementUrl() {
            return this.operationalAgreementUrl;
        }

        public void setOperationalAgreementUrl(String string) {
            this.operationalAgreementUrl = string;
        }

        public void setSections(TCComposite tCComposite) {
            this.sections = tCComposite;
        }

        public TCComposite getSections() {
            return (TCComposite)this.sections.clone();
        }
    }
}

