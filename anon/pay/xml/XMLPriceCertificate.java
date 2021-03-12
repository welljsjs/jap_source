/*
 * Decompiled with CFR 0.150.
 */
package anon.pay.xml;

import anon.crypto.IMyPrivateKey;
import anon.crypto.SignatureVerifier;
import anon.crypto.XMLSignature;
import anon.pay.PaymentInstanceDBEntry;
import anon.util.IXMLEncodable;
import anon.util.XMLParseException;
import anon.util.XMLUtil;
import java.io.ByteArrayInputStream;
import java.sql.Timestamp;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;
import logging.LogHolder;
import logging.LogType;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class XMLPriceCertificate
implements IXMLEncodable {
    public static final String XML_ELEMENT_NAME = "PriceCertificate";
    private String m_subjectKeyIdentifier;
    private double m_rate;
    private Timestamp m_signatureTime;
    private String m_biID;
    private String m_hashValue;
    private Document m_docThePriceCert;
    private static final String XML_ELEM_SUBJECT_KEY_IDENTIFIER = "SubjectKeyIdentifier";
    private static final String XML_ELEM_RATE = "Rate";
    private static final String XML_ELEM_SIG_TIME = "SignatureTime";
    private static final String XML_ELEM_BIID = "BiID";

    public XMLPriceCertificate(String string, double d, Timestamp timestamp, String string2) {
        this.m_subjectKeyIdentifier = string;
        this.m_signatureTime = timestamp;
        this.m_rate = d;
        this.m_biID = string2;
        this.m_docThePriceCert = XMLUtil.createDocument();
        this.m_docThePriceCert.appendChild(this.internal_toXmlElement(this.m_docThePriceCert));
        this.m_hashValue = XMLSignature.getHashValueOfElement(this.m_docThePriceCert);
    }

    public XMLPriceCertificate(String string, double d, String string2) {
        this.m_subjectKeyIdentifier = string;
        this.m_signatureTime = null;
        this.m_rate = d;
        this.m_biID = string2;
        this.m_docThePriceCert = XMLUtil.createDocument();
        this.m_docThePriceCert.appendChild(this.internal_toXmlElement(this.m_docThePriceCert));
        this.m_hashValue = XMLSignature.getHashValueOfElement(this.m_docThePriceCert);
    }

    public XMLPriceCertificate(String string, double d, Timestamp timestamp, String string2, String string3) {
        this.m_subjectKeyIdentifier = string;
        this.m_signatureTime = timestamp;
        this.m_rate = d;
        this.m_biID = string2;
        this.m_docThePriceCert = XMLUtil.createDocument();
        this.m_docThePriceCert.appendChild(this.internal_toXmlElement(this.m_docThePriceCert));
        this.addSignatureNode(this.m_docThePriceCert, string3);
        this.m_hashValue = XMLSignature.getHashValueOfElement(this.m_docThePriceCert);
    }

    public void addSignature(String string) {
        this.addSignatureNode(this.m_docThePriceCert, string);
    }

    private void addSignatureNode(Document document, String string) {
        Document document2 = null;
        try {
            document2 = XMLUtil.toXMLDocument(string);
            Element element = document2.getDocumentElement();
            XMLUtil.importNode(document, element, true);
        }
        catch (Exception exception) {
            LogHolder.log(7, LogType.PAY, "Could not parse signature node from string");
            LogHolder.log(7, LogType.PAY, exception.getMessage());
        }
    }

    private Node internal_toXmlElement(Document document) {
        Element element = document.createElement(XML_ELEMENT_NAME);
        element.setAttribute("version", "1.1");
        Element element2 = document.createElement(XML_ELEM_SUBJECT_KEY_IDENTIFIER);
        XMLUtil.setValue((Node)element2, this.m_subjectKeyIdentifier);
        element.appendChild(element2);
        element2 = document.createElement(XML_ELEM_RATE);
        NumberFormat numberFormat = NumberFormat.getInstance(Locale.ENGLISH);
        numberFormat.setMinimumFractionDigits(2);
        numberFormat.setMaximumFractionDigits(2);
        String string = numberFormat.format(this.m_rate);
        XMLUtil.setValue((Node)element2, string);
        element.appendChild(element2);
        element2 = document.createElement(XML_ELEM_SIG_TIME);
        String string2 = "";
        if (this.m_signatureTime != null) {
            string2 = this.m_signatureTime.toString();
        }
        XMLUtil.setValue((Node)element2, string2);
        element.appendChild(element2);
        element2 = document.createElement(XML_ELEM_BIID);
        XMLUtil.setValue((Node)element2, this.m_biID);
        element.appendChild(element2);
        return element;
    }

    public XMLPriceCertificate(String string) throws Exception {
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(string.getBytes());
        Document document = XMLUtil.readXMLDocument(byteArrayInputStream);
        this.setValues(document.getDocumentElement());
        this.m_docThePriceCert = document;
        this.m_hashValue = XMLSignature.getHashValueOfElement(this.m_docThePriceCert);
    }

    public XMLPriceCertificate(String string, String string2, double d) throws Exception {
        this(string);
        this.m_rate = d;
        this.m_hashValue = string2;
    }

    public XMLPriceCertificate(char[] arrc) throws Exception {
        this(new String(arrc));
    }

    public XMLPriceCertificate(byte[] arrby) throws Exception {
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(arrby);
        Document document = XMLUtil.readXMLDocument(byteArrayInputStream);
        this.setValues(document.getDocumentElement());
        this.m_docThePriceCert = document;
        this.m_hashValue = XMLSignature.getHashValueOfElement(this.m_docThePriceCert);
    }

    public XMLPriceCertificate(Element element) throws XMLParseException {
        this.setValues(element);
        this.m_docThePriceCert = XMLUtil.createDocument();
        this.m_docThePriceCert.appendChild(XMLUtil.importNode(this.m_docThePriceCert, element, true));
        this.m_hashValue = XMLSignature.getHashValueOfElement(this.m_docThePriceCert);
    }

    public XMLPriceCertificate(Document document) throws Exception {
        Element element = document.getDocumentElement();
        this.setValues(element);
        this.m_docThePriceCert = document;
        this.m_hashValue = XMLSignature.getHashValueOfElement(this.m_docThePriceCert);
    }

    public boolean sign(IMyPrivateKey iMyPrivateKey) {
        try {
            XMLSignature xMLSignature = XMLSignature.sign((Node)this.m_docThePriceCert, iMyPrivateKey, 0);
            xMLSignature.clearCertificates();
            this.m_hashValue = XMLSignature.getHashValueOfElement(this.m_docThePriceCert);
            return true;
        }
        catch (Exception exception) {
            LogHolder.log(2, LogType.PAY, "Error signing the certificate: ", exception);
            return false;
        }
    }

    public boolean verify(PaymentInstanceDBEntry paymentInstanceDBEntry) {
        if (!SignatureVerifier.getInstance().isCheckSignatures(4)) {
            return true;
        }
        if (paymentInstanceDBEntry == null) {
            return false;
        }
        return XMLSignature.verifyFast((Node)this.m_docThePriceCert, paymentInstanceDBEntry.getCertPath().getEndEntityKeys());
    }

    private void setValues(Element element) throws XMLParseException {
        String string;
        XMLUtil.assertNodeName(element, XML_ELEMENT_NAME);
        Element element2 = (Element)XMLUtil.getFirstChildByName(element, XML_ELEM_SUBJECT_KEY_IDENTIFIER);
        XMLUtil.assertNotNull(element2);
        this.m_subjectKeyIdentifier = XMLUtil.parseValue((Node)element2, (String)null);
        if (this.m_subjectKeyIdentifier == null) {
            throw new XMLParseException(XML_ELEM_SUBJECT_KEY_IDENTIFIER);
        }
        element2 = (Element)XMLUtil.getFirstChildByName(element, XML_ELEM_RATE);
        this.m_rate = XMLUtil.parseValue((Node)element2, -9999.99);
        if (this.m_rate == -9999.99) {
            throw new XMLParseException(XML_ELEM_RATE);
        }
        element2 = (Element)XMLUtil.getFirstChildByName(element, XML_ELEM_SIG_TIME);
        if (element2 != null && !(string = XMLUtil.parseValue((Node)element2, "0")).equals("0")) {
            this.m_signatureTime = Timestamp.valueOf(string);
        }
        element2 = (Element)XMLUtil.getFirstChildByName(element, XML_ELEM_BIID);
        this.m_biID = XMLUtil.parseValue((Node)element2, "unknown");
        if (this.m_biID.equals("unknown")) {
            throw new XMLParseException(XML_ELEM_BIID);
        }
    }

    public Timestamp getSignatureTime() {
        return this.m_signatureTime;
    }

    public double getRate() {
        return this.m_rate;
    }

    public String getSubjectKeyIdentifier() {
        return this.m_subjectKeyIdentifier;
    }

    public String getBiID() {
        return this.m_biID;
    }

    public String getHashValue() {
        return this.m_hashValue;
    }

    public Document getDocument() {
        return this.m_docThePriceCert;
    }

    public Element toXmlElement(Document document) {
        try {
            Element element = this.m_docThePriceCert.getDocumentElement();
            return (Element)XMLUtil.importNode(document, element, true);
        }
        catch (Exception exception) {
            return null;
        }
    }

    public String toString() {
        String string;
        String string2 = new String("Price: ");
        String string3 = XMLPriceCertificate.formatEuroCentValue(this.getRate());
        if (this.getSignatureTime() == null) {
            string = "Not signed";
        } else {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yy");
            string = "Signed on : " + simpleDateFormat.format(this.getSignatureTime());
        }
        return string2 + string3 + ", " + string;
    }

    private static String formatEuroCentValue(double d) {
        NumberFormat numberFormat = NumberFormat.getInstance();
        numberFormat.setMaximumFractionDigits(2);
        numberFormat.setMinimumFractionDigits(2);
        return numberFormat.format(d) + " Eurocent";
    }
}

