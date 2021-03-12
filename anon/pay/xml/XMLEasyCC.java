/*
 * Decompiled with CFR 0.150.
 */
package anon.pay.xml;

import anon.crypto.IMyPrivateKey;
import anon.crypto.IMyPublicKey;
import anon.crypto.PKCS12;
import anon.crypto.XMLSignature;
import anon.infoservice.MixPosition;
import anon.util.IXMLEncodable;
import anon.util.Util;
import anon.util.XMLParseException;
import anon.util.XMLUtil;
import java.util.Enumeration;
import java.util.Hashtable;
import logging.LogHolder;
import logging.LogType;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class XMLEasyCC
implements IXMLEncodable {
    public static final String XML_ELEMENT_NAME = "CC";
    private long m_lTransferredBytes;
    private long m_lAccountNumber;
    private int m_id = 0;
    private Hashtable m_priceCerts = new Hashtable();
    private String m_cascadeID;
    private Document m_docTheEasyCC;
    private String m_priceCertHashesConcatenated;
    private boolean m_bOldHashFormat = false;
    private boolean m_bIsLastCC = false;
    private String m_strPIID;

    public XMLEasyCC(long l, long l2, PKCS12 pKCS12, Hashtable hashtable, String string, String string2) throws XMLParseException {
        this.m_priceCerts = hashtable;
        this.m_priceCertHashesConcatenated = XMLEasyCC.createConcatenatedPriceCertHashes(hashtable, true);
        this.m_lTransferredBytes = l2;
        this.m_lAccountNumber = l;
        this.m_cascadeID = string;
        this.m_strPIID = string2;
        this.m_docTheEasyCC = XMLUtil.createDocument();
        this.m_docTheEasyCC.appendChild(this.internal_toXmlElement(this.m_docTheEasyCC));
        if (pKCS12 != null) {
            XMLSignature.sign((Node)this.m_docTheEasyCC, pKCS12, 0);
        }
    }

    public XMLEasyCC(byte[] arrby) throws Exception {
        Document document = XMLUtil.toXMLDocument(arrby);
        this.setValues(document.getDocumentElement());
        this.m_docTheEasyCC = document;
    }

    public XMLEasyCC(String string) throws XMLParseException {
        Document document = XMLUtil.toXMLDocument(string);
        this.setValues(document.getDocumentElement());
        this.m_docTheEasyCC = document;
    }

    public XMLEasyCC(char[] arrc) throws XMLParseException {
        this(new String(arrc));
    }

    public XMLEasyCC(Element element) throws Exception {
        this.setValues(element);
        this.m_docTheEasyCC = XMLUtil.createDocument();
        this.m_docTheEasyCC.appendChild(XMLUtil.importNode(this.m_docTheEasyCC, element, true));
    }

    public XMLEasyCC(XMLEasyCC xMLEasyCC) throws XMLParseException {
        this.m_lTransferredBytes = xMLEasyCC.m_lTransferredBytes;
        this.m_lAccountNumber = xMLEasyCC.m_lAccountNumber;
        xMLEasyCC.m_id = 0;
        this.m_id = 0;
        this.m_priceCerts = (Hashtable)xMLEasyCC.m_priceCerts.clone();
        this.m_cascadeID = xMLEasyCC.m_cascadeID;
        this.m_docTheEasyCC = XMLUtil.createDocument();
        this.m_docTheEasyCC.appendChild(XMLUtil.importNode(this.m_docTheEasyCC, xMLEasyCC.m_docTheEasyCC.getDocumentElement(), true));
        this.m_priceCertHashesConcatenated = xMLEasyCC.m_priceCertHashesConcatenated;
        this.m_strPIID = xMLEasyCC.m_strPIID;
    }

    private void setValues(Element element) throws XMLParseException {
        if (!element.getTagName().equals(XML_ELEMENT_NAME)) {
            throw new XMLParseException("XMLEasyCC wrong xml root element name");
        }
        String string = XMLUtil.parseAttribute((Node)element, "version", null);
        if (string == null || !string.equals("1.2") && !string.equals("1.1")) {
            throw new XMLParseException("XMLEasyCC wrong version");
        }
        Element element2 = (Element)XMLUtil.getFirstChildByName(element, "AccountNumber");
        this.m_lAccountNumber = XMLUtil.parseValue((Node)element2, 0L);
        element2 = (Element)XMLUtil.getFirstChildByName(element, "TransferredBytes");
        this.m_lTransferredBytes = XMLUtil.parseValue((Node)element2, -1L);
        this.m_bIsLastCC = XMLUtil.parseAttribute((Node)element2, "isLastCC", this.m_bIsLastCC);
        element2 = (Element)XMLUtil.getFirstChildByName(element, "PIID");
        this.m_strPIID = XMLUtil.parseValue((Node)element2, (String)null);
        element2 = (Element)XMLUtil.getFirstChildByName(element, "Cascade");
        this.m_cascadeID = XMLUtil.parseValue((Node)element2, (String)null);
        Element element3 = (Element)XMLUtil.getFirstChildByName(element, "PriceCertificates");
        if (element3 != null) {
            NodeList nodeList = element3.getElementsByTagName("PriceCertHash");
            for (int i = 0; i < nodeList.getLength(); ++i) {
                Element element4 = (Element)nodeList.item(i);
                String string2 = XMLUtil.parseValue((Node)element4, "abc");
                String string3 = XMLUtil.parseAttribute((Node)element4, "id", "abc");
                if (string3.equals("abc")) {
                    throw new XMLParseException("wrong or missing id of price certificate");
                }
                int n = XMLUtil.parseAttribute((Node)element4, "position", -1);
                if (n < 0) {
                    this.m_bOldHashFormat = true;
                }
                this.m_priceCerts.put(new MixPosition(n, string3), string2);
            }
        }
        this.m_priceCertHashesConcatenated = XMLEasyCC.createConcatenatedPriceCertHashes(this.m_priceCerts, !this.m_bOldHashFormat);
        if (this.m_bOldHashFormat) {
            LogHolder.log(4, LogType.PAY, "Found old hash format for CC: " + this.m_priceCertHashesConcatenated);
        }
    }

    private Element internal_toXmlElement(Document document) {
        Element element = document.createElement(XML_ELEMENT_NAME);
        element.setAttribute("version", "1.2");
        Element element2 = document.createElement("TransferredBytes");
        XMLUtil.setValue((Node)element2, Long.toString(this.m_lTransferredBytes));
        XMLUtil.setAttribute(element2, "isLastCC", this.m_bIsLastCC);
        element.appendChild(element2);
        element2 = document.createElement("AccountNumber");
        XMLUtil.setValue((Node)element2, Long.toString(this.m_lAccountNumber));
        element.appendChild(element2);
        element2 = document.createElement("PIID");
        if (this.m_strPIID != null) {
            XMLUtil.setValue((Node)element2, this.m_strPIID);
        }
        element.appendChild(element2);
        element2 = document.createElement("Cascade");
        if (this.m_cascadeID != null) {
            XMLUtil.setValue((Node)element2, this.m_cascadeID);
        }
        element.appendChild(element2);
        Element element3 = document.createElement("PriceCertificates");
        element.appendChild(element3);
        Enumeration enumeration = this.m_priceCerts.keys();
        while (enumeration.hasMoreElements()) {
            MixPosition mixPosition = (MixPosition)enumeration.nextElement();
            String string = (String)this.m_priceCerts.get(mixPosition);
            Element element4 = document.createElement("PriceCertHash");
            XMLUtil.setValue((Node)element4, string);
            XMLUtil.setAttribute(element4, "id", mixPosition.getId());
            if (!this.m_bOldHashFormat) {
                XMLUtil.setAttribute(element4, "position", mixPosition.getPosition());
            }
            element3.appendChild(element4);
        }
        return element;
    }

    public String getPIID() {
        return this.m_strPIID;
    }

    public synchronized void setPIID(String string) {
        this.m_strPIID = string;
        this.m_docTheEasyCC = XMLUtil.createDocument();
        this.m_docTheEasyCC.appendChild(this.internal_toXmlElement(this.m_docTheEasyCC));
    }

    public int getId() {
        return this.m_id;
    }

    public void setId(int n) {
        this.m_id = n;
    }

    public void setCascadeID(String string) {
        this.m_cascadeID = string;
        this.m_docTheEasyCC = XMLUtil.createDocument();
        this.m_docTheEasyCC.appendChild(this.internal_toXmlElement(this.m_docTheEasyCC));
    }

    public long getAccountNumber() {
        return this.m_lAccountNumber;
    }

    public long getTransferredBytes() {
        return this.m_lTransferredBytes;
    }

    public Enumeration getMixIds() {
        Enumeration enumeration = this.m_priceCerts.keys();
        Hashtable hashtable = new Hashtable();
        while (enumeration.hasMoreElements()) {
            MixPosition mixPosition = (MixPosition)enumeration.nextElement();
            String string = mixPosition.getId();
            hashtable.put(string, this.m_priceCerts.get(mixPosition));
        }
        return hashtable.keys();
    }

    public String getCascadeID() {
        return this.m_cascadeID;
    }

    public Hashtable getPriceCertHashes() {
        return (Hashtable)this.m_priceCerts.clone();
    }

    public String getConcatenatedPriceCertHashes() {
        return this.m_priceCertHashesConcatenated;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static String createConcatenatedPriceCertHashes(Hashtable hashtable, boolean bl) {
        StringBuffer stringBuffer = new StringBuffer();
        if (hashtable != null) {
            Hashtable hashtable2 = hashtable;
            synchronized (hashtable2) {
                int n;
                String[] arrstring = new String[hashtable.size()];
                String[] arrstring2 = new String[hashtable.size()];
                Enumeration enumeration = hashtable.keys();
                for (n = 0; n < hashtable.size(); ++n) {
                    Object k = enumeration.nextElement();
                    arrstring[n] = !bl ? ((MixPosition)k).getId() : Integer.toString(((MixPosition)k).getPosition());
                    arrstring2[n] = hashtable.get(k).toString();
                }
                if (!bl) {
                    Util.sort(arrstring2, arrstring);
                } else {
                    Util.sort(arrstring, arrstring2);
                }
                for (n = 0; n < arrstring2.length; ++n) {
                    stringBuffer.append(arrstring2[n]);
                }
            }
        }
        return stringBuffer.toString();
    }

    public int getNrOfPriceCerts() {
        return this.m_priceCerts.size();
    }

    public boolean isLastCC() {
        return this.m_bIsLastCC;
    }

    public void setLastCC(boolean bl) {
        this.m_bIsLastCC = bl;
    }

    public void setPriceCerts(Hashtable hashtable) {
        this.m_bOldHashFormat = false;
        this.m_priceCerts = hashtable;
        this.m_priceCertHashesConcatenated = XMLEasyCC.createConcatenatedPriceCertHashes(this.m_priceCerts, !this.m_bOldHashFormat);
        if (this.m_bOldHashFormat) {
            LogHolder.log(4, LogType.PAY, "Found old hash format for CC: " + this.m_priceCertHashesConcatenated);
        }
        this.m_docTheEasyCC = XMLUtil.createDocument();
        this.m_docTheEasyCC.appendChild(this.internal_toXmlElement(this.m_docTheEasyCC));
    }

    public synchronized void addTransferredBytes(long l) {
        this.m_lTransferredBytes += l;
        this.m_docTheEasyCC = XMLUtil.createDocument();
        this.m_docTheEasyCC.appendChild(this.internal_toXmlElement(this.m_docTheEasyCC));
    }

    public synchronized void setTransferredBytes(long l) {
        this.m_lTransferredBytes = l;
        this.m_docTheEasyCC = XMLUtil.createDocument();
        this.m_docTheEasyCC.appendChild(this.internal_toXmlElement(this.m_docTheEasyCC));
    }

    public boolean sign(IMyPrivateKey iMyPrivateKey) {
        try {
            XMLSignature.sign((Node)this.m_docTheEasyCC, iMyPrivateKey, 0);
            return true;
        }
        catch (Exception exception) {
            return false;
        }
    }

    public boolean verify(IMyPublicKey iMyPublicKey) {
        try {
            return XMLSignature.verifyFast((Node)this.m_docTheEasyCC, iMyPublicKey);
        }
        catch (Throwable throwable) {
            return false;
        }
    }

    public Document getDocument() {
        return this.m_docTheEasyCC;
    }

    public synchronized Element toXmlElement(Document document) {
        try {
            return (Element)XMLUtil.importNode(document, this.m_docTheEasyCC.getDocumentElement(), true);
        }
        catch (Exception exception) {
            return null;
        }
    }

    public int hashCode() {
        int n = 1;
        n = 31 * n + (this.m_bOldHashFormat ? 1231 : 1237);
        n = 31 * n + (this.m_cascadeID == null ? 0 : this.m_cascadeID.hashCode());
        n = 31 * n + (int)(this.m_lAccountNumber ^ this.m_lAccountNumber >>> 32);
        n = 31 * n + (int)(this.m_lTransferredBytes ^ this.m_lTransferredBytes >>> 32);
        n = 31 * n + (this.m_priceCertHashesConcatenated == null ? 0 : this.m_priceCertHashesConcatenated.hashCode());
        n = 31 * n + (this.m_strPIID == null ? 0 : this.m_strPIID.hashCode());
        return n;
    }

    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (object == null) {
            return false;
        }
        if (this.getClass() != object.getClass()) {
            return false;
        }
        XMLEasyCC xMLEasyCC = (XMLEasyCC)object;
        if (this.m_bOldHashFormat != xMLEasyCC.m_bOldHashFormat) {
            return false;
        }
        if (this.m_cascadeID == null ? xMLEasyCC.m_cascadeID != null : !this.m_cascadeID.equals(xMLEasyCC.m_cascadeID)) {
            return false;
        }
        if (this.m_lAccountNumber != xMLEasyCC.m_lAccountNumber) {
            return false;
        }
        if (this.m_lTransferredBytes != xMLEasyCC.m_lTransferredBytes) {
            return false;
        }
        if (this.m_priceCertHashesConcatenated == null ? xMLEasyCC.m_priceCertHashesConcatenated != null : !this.m_priceCertHashesConcatenated.equals(xMLEasyCC.m_priceCertHashesConcatenated)) {
            return false;
        }
        return !(this.m_strPIID == null ? xMLEasyCC.m_strPIID != null : !this.m_strPIID.equals(xMLEasyCC.m_strPIID));
    }
}

