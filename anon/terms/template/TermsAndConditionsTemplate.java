/*
 * Decompiled with CFR 0.150.
 */
package anon.terms.template;

import anon.crypto.MultiCertPath;
import anon.crypto.SignatureVerifier;
import anon.crypto.XMLSignature;
import anon.infoservice.AbstractDistributableCertifiedDatabaseEntry;
import anon.infoservice.Database;
import anon.infoservice.InfoServiceHolder;
import anon.infoservice.OperatorAddress;
import anon.infoservice.ServiceOperator;
import anon.terms.TCComponent;
import anon.terms.TCComposite;
import anon.terms.TermsAndConditions;
import anon.terms.template.Preamble;
import anon.terms.template.Section;
import anon.util.Util;
import anon.util.XMLParseException;
import anon.util.XMLUtil;
import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.text.DateFormat;
import java.util.Date;
import java.util.Enumeration;
import java.util.Locale;
import java.util.Vector;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import logging.LogHolder;
import logging.LogType;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class TermsAndConditionsTemplate
extends AbstractDistributableCertifiedDatabaseEntry {
    private static final String XML_ATTR_DATE = "date";
    private static final String XML_ATTR_LOCALE = "locale";
    private static final String XML_ATTR_NAME = "name";
    private static final String XML_ATTR_TYPE = "type";
    private static final String[] REQUIRED_ATTRIBUTES = new String[]{"type", "locale", "date", "id", "name"};
    private static final String XML_ELEMENT_OPERATOR_COUNTRY = "OperatorCountry";
    private static final String XML_ELEMENT_SIGNATURE = "Sig";
    private static final String XML_ELEMENT_DATE = "Date";
    public static final String INFOSERVICE_PATH = "/tctemplate/";
    public static final String INFOSERVICE_CONTAINER_PATH = "/tctemplates";
    public static final String INFOSERVICE_SERIALS_PATH = "/tctemplateserials";
    public static final String[] REPLACEMENT_ELEMENT_NAMES = new String[]{"PrivacyPolicyUrl", "LegalOpinionsUrl", "OperationalAgreementUrl"};
    private static final String XSLT_PATH = "tac.xslt";
    public static String TERMS_AND_CONDITIONS_TYPE_COMMON_LAW = "CommonLaw";
    public static String TERMS_AND_CONDITIONS_TYPE_GERMAN_LAW = "GermanLaw";
    public static String TERMS_AND_CONDITIONS_TYPE_GENERAL_LAW = "GeneralLaw";
    public static String XML_ELEMENT_CONTAINER_NAME = "TermsAndConditionsTemplates";
    public static String XML_ELEMENT_NAME = "TermsAndConditionsTemplate";
    private String m_strId = null;
    private String m_locale = null;
    private String m_type = null;
    private String m_date;
    private Document signedDocument = null;
    private XMLSignature m_signature = null;
    private MultiCertPath m_certPath = null;
    private String name = "";
    private Preamble preamble = null;
    private TCComposite sections = new TCComposite();
    static /* synthetic */ Class class$anon$terms$template$TermsAndConditionsTemplate;

    public TermsAndConditionsTemplate(Element element, long l) throws XMLParseException {
        this(element);
    }

    public TermsAndConditionsTemplate(Node node) throws XMLParseException {
        super(Long.MAX_VALUE);
        Element element = null;
        if (node.getNodeType() == 9) {
            element = ((Document)node).getDocumentElement();
        } else if (node.getNodeType() == 1) {
            element = (Element)node;
        } else {
            throw new XMLParseException("Invalid node type");
        }
        this.name = XMLUtil.parseAttribute((Node)element, XML_ATTR_NAME, "");
        this.m_date = XMLUtil.parseAttribute((Node)element, XML_ATTR_DATE, "");
        this.m_locale = XMLUtil.parseAttribute((Node)element, XML_ATTR_LOCALE, "");
        this.m_type = XMLUtil.parseAttribute((Node)element, XML_ATTR_TYPE, TERMS_AND_CONDITIONS_TYPE_COMMON_LAW);
        this.m_strId = this.m_type + "_" + this.m_locale + "_" + this.m_date;
        this.m_signature = SignatureVerifier.getInstance().getVerifiedXml(element, 5);
        if (this.m_signature != null) {
            this.m_certPath = this.m_signature.getMultiCertPath();
            if (node.getNodeType() == 9) {
                this.signedDocument = (Document)node;
            } else {
                this.signedDocument = XMLUtil.createDocument();
                this.signedDocument.appendChild(XMLUtil.importNode(this.signedDocument, element, true));
            }
        }
        NodeList nodeList = element.getElementsByTagName(Section.XML_ELEMENT_NAME);
        for (int i = 0; i < nodeList.getLength(); ++i) {
            this.sections.addTCComponent(new Section(nodeList.item(i)));
        }
        Node node2 = XMLUtil.getFirstChildByName(element, Preamble.XML_ELEMENT_NAME);
        this.preamble = node2 != null ? new Preamble(node2) : new Preamble();
    }

    public TermsAndConditionsTemplate(File file) throws XMLParseException, IOException {
        this(XMLUtil.readXMLDocument(file));
    }

    public Document createTCDocument(TermsAndConditions.Translation translation) {
        Object object;
        Document document = XMLUtil.createDocument();
        Element element = document.createElement(XML_ELEMENT_NAME);
        Element element2 = document.createElement("City");
        Element element3 = document.createElement("Venue");
        Element element4 = document.createElement(XML_ELEMENT_DATE);
        ServiceOperator serviceOperator = null;
        OperatorAddress operatorAddress = null;
        Element element5 = document.createElement(XML_ELEMENT_SIGNATURE);
        element5.appendChild(element2);
        element5.appendChild(element4);
        TCComposite tCComposite = this.getSections();
        TCComponent[] arrtCComponent = null;
        String[] arrstring = new String[]{this.m_type, this.m_locale, this.m_date, this.m_strId, this.name};
        for (int i = 0; i < REQUIRED_ATTRIBUTES.length; ++i) {
            element.setAttribute(REQUIRED_ATTRIBUTES[i], arrstring[i]);
        }
        document.appendChild(element);
        if (translation != null) {
            serviceOperator = translation.getOperator();
            operatorAddress = translation.getOperatorAddress();
            Locale locale = new Locale(translation.getLocale(), serviceOperator.getCountryCode());
            object = new Locale(translation.getLocale(), "", "");
            if (operatorAddress != null) {
                operatorAddress.setOperatorCountry(locale.getDisplayCountry((Locale)object));
            }
            Element element6 = serviceOperator.toXMLElement(document, operatorAddress, false);
            Element element7 = document.createElement(XML_ELEMENT_OPERATOR_COUNTRY);
            XMLUtil.setValue((Node)element2, operatorAddress != null ? translation.getOperatorAddress().getCity() : "");
            XMLUtil.setValue((Node)element3, operatorAddress != null ? translation.getOperatorAddress().getVenue() : "");
            XMLUtil.setValue((Node)element4, DateFormat.getDateInstance(2, (Locale)object).format(translation.getDate()));
            element6.appendChild(element7);
            XMLUtil.setValue((Node)element7, locale.getDisplayCountry((Locale)object));
            TCComponent[] arrtCComponent2 = translation.getSections().getTCComponents();
            Section section = null;
            Section section2 = null;
            TCComponent[] arrtCComponent3 = null;
            for (int i = 0; i < arrtCComponent2.length; ++i) {
                section = (Section)arrtCComponent2[i];
                section2 = (Section)tCComposite.getTCComponent(section.getId());
                if (!section.hasContent() || section2 == null) {
                    tCComposite.addTCComponent(arrtCComponent2[i]);
                    continue;
                }
                if (section.getContent() != null) {
                    section2.setContent(section.getContent());
                }
                arrtCComponent3 = section.getTCComponents();
                for (int j = 0; j < arrtCComponent3.length; ++j) {
                    section2.addTCComponent(arrtCComponent3[j]);
                }
            }
            String[] arrstring2 = new String[]{translation.getPrivacyPolicyUrl(), translation.getLegalOpinionsUrl(), translation.getOperationalAgreementUrl()};
            final Vector<Element> vector = new Vector<Element>();
            Element element8 = null;
            for (int i = 0; i < arrstring2.length; ++i) {
                element8 = document.createElement(REPLACEMENT_ELEMENT_NAMES[i]);
                element8.appendChild(document.createTextNode(arrstring2[i]));
                vector.addElement(element8);
            }
            vector.addElement(element6);
            vector.addElement(element7);
            vector.addElement(element3);
            NodeList nodeList = new NodeList(){

                public int getLength() {
                    return vector.size();
                }

                public Node item(int n) {
                    return (Node)vector.elementAt(n);
                }
            };
            arrtCComponent = tCComposite.getTCComponents();
            for (int i = 0; i < arrtCComponent.length; ++i) {
                ((Section)arrtCComponent[i]).replaceElementNodes(nodeList);
            }
        }
        this.preamble.setOperator(serviceOperator);
        this.preamble.setOperatorAddress(operatorAddress);
        element.appendChild(this.preamble.toXmlElement(document));
        Element element9 = document.createElement(Section.XML_ELEMENT_CONTAINER_NAME);
        arrtCComponent = tCComposite.getTCComponents();
        object = null;
        for (int i = 0; i < arrtCComponent.length; ++i) {
            object = ((Section)arrtCComponent[i]).toXmlElement(document);
            if (object == null) continue;
            element9.appendChild((Node)object);
        }
        element.appendChild(element9);
        element.appendChild(element5);
        return document;
    }

    public String transform(TermsAndConditions.Translation translation) {
        try {
            StringWriter stringWriter = new StringWriter();
            this.transform(stringWriter, translation);
            stringWriter.close();
            String string = stringWriter.toString();
            return Util.replaceAll(string, "<br/>", "<br>");
        }
        catch (IOException iOException) {
            LogHolder.log(3, LogType.MISC, "IOException caught while transforming terms and conditions.");
            return null;
        }
        catch (TransformerException transformerException) {
            LogHolder.log(3, LogType.MISC, "Could not transform terms and conditions.");
            transformerException.printStackTrace();
            return null;
        }
    }

    public void transform(Writer writer, TermsAndConditions.Translation translation) throws IOException, TransformerException {
        DOMSource dOMSource = new DOMSource(this.createTCDocument(translation));
        StreamSource streamSource = new StreamSource(this.getClass().getResourceAsStream(XSLT_PATH));
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer(streamSource);
        transformer.transform(dOMSource, new StreamResult(writer));
    }

    public String getType() {
        return this.m_type;
    }

    public String getId() {
        return this.m_strId;
    }

    public long getLastUpdate() {
        return 0L;
    }

    public long getVersionNumber() {
        return 0L;
    }

    public String getLanguage() {
        return this.m_locale;
    }

    public String getDate() {
        return this.m_date;
    }

    public String getPostFile() {
        return "/posttcframework";
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

    public TCComposite getSections() {
        return (TCComposite)this.sections.clone();
    }

    public static synchronized void store(Element element) {
        Element element2 = (Element)XMLUtil.getFirstChildByName(element, XML_ELEMENT_NAME);
        while (element2 != null) {
            try {
                Database.getInstance(class$anon$terms$template$TermsAndConditionsTemplate == null ? TermsAndConditionsTemplate.class$("anon.terms.template.TermsAndConditionsTemplate") : class$anon$terms$template$TermsAndConditionsTemplate).update(new TermsAndConditionsTemplate(element2));
                element2 = (Element)XMLUtil.getNextSiblingByName(element2, XML_ELEMENT_NAME);
            }
            catch (XMLParseException xMLParseException) {
                LogHolder.log(3, LogType.MISC, "one tc templates could not be stored in the DB");
            }
        }
    }

    public static synchronized Enumeration getAllStoredRefIDs() {
        final Enumeration enumeration = Database.getInstance(class$anon$terms$template$TermsAndConditionsTemplate == null ? (class$anon$terms$template$TermsAndConditionsTemplate = TermsAndConditionsTemplate.class$("anon.terms.template.TermsAndConditionsTemplate")) : class$anon$terms$template$TermsAndConditionsTemplate).getEntryList().elements();
        return new Enumeration(){

            public boolean hasMoreElements() {
                return enumeration.hasMoreElements();
            }

            public Object nextElement() {
                return ((TermsAndConditionsTemplate)enumeration.nextElement()).getId();
            }
        };
    }

    public static void loadFromDirectory(File file) {
        File file2 = null;
        if (file == null) {
            return;
        }
        String[] arrstring = file.list();
        if (arrstring == null) {
            return;
        }
        for (int i = 0; i < arrstring.length; ++i) {
            try {
                file2 = new File(file.getAbsolutePath() + File.separator + arrstring[i]);
                TermsAndConditionsTemplate termsAndConditionsTemplate = new TermsAndConditionsTemplate(file2);
                Database.getInstance(class$anon$terms$template$TermsAndConditionsTemplate == null ? TermsAndConditionsTemplate.class$("anon.terms.template.TermsAndConditionsTemplate") : class$anon$terms$template$TermsAndConditionsTemplate).update(termsAndConditionsTemplate);
                continue;
            }
            catch (XMLParseException xMLParseException) {
                LogHolder.log(2, LogType.MISC, "XMLParseException while loading Terms & Conditions: ", xMLParseException);
                continue;
            }
            catch (IOException iOException) {
                LogHolder.log(2, LogType.MISC, "IOException while loading Terms & Conditions: ", iOException);
            }
        }
    }

    public static TermsAndConditionsTemplate getById(String string, boolean bl) {
        TermsAndConditionsTemplate termsAndConditionsTemplate = (TermsAndConditionsTemplate)Database.getInstance(class$anon$terms$template$TermsAndConditionsTemplate == null ? (class$anon$terms$template$TermsAndConditionsTemplate = TermsAndConditionsTemplate.class$("anon.terms.template.TermsAndConditionsTemplate")) : class$anon$terms$template$TermsAndConditionsTemplate).getEntryById(string);
        if (!bl || termsAndConditionsTemplate != null) {
            return termsAndConditionsTemplate;
        }
        termsAndConditionsTemplate = InfoServiceHolder.getInstance().getTCTemplate(string);
        Database.getInstance(class$anon$terms$template$TermsAndConditionsTemplate == null ? (class$anon$terms$template$TermsAndConditionsTemplate = TermsAndConditionsTemplate.class$("anon.terms.template.TermsAndConditionsTemplate")) : class$anon$terms$template$TermsAndConditionsTemplate).update(termsAndConditionsTemplate);
        return termsAndConditionsTemplate;
    }

    public boolean equals(Object object) {
        boolean bl = false;
        if (object != null && object instanceof TermsAndConditionsTemplate) {
            bl = this.getId().equals(((TermsAndConditionsTemplate)object).getId());
        }
        return bl;
    }

    public int hashCode() {
        return this.getId().hashCode();
    }

    public XMLSignature getSignature() {
        return this.m_signature;
    }

    public MultiCertPath getCertPath() {
        return this.m_certPath;
    }

    public Document getDocument() {
        return this.signedDocument != null ? this.signedDocument : this.createTCDocument(null);
    }

    public Document getSignedDocument() {
        return this.signedDocument;
    }

    public void setSignedDocument(Document document) {
        this.signedDocument = document;
    }

    public Element getXmlStructure() {
        return this.getDocument().getDocumentElement();
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

