/*
 * Decompiled with CFR 0.150.
 */
package anon.crypto;

import anon.crypto.ByteSignature;
import anon.crypto.CertPath;
import anon.crypto.IMyPrivateKey;
import anon.crypto.IMyPublicKey;
import anon.crypto.JAPCertificate;
import anon.crypto.XMLSignature;
import anon.util.Base64;
import anon.util.IXMLEncodable;
import anon.util.Util;
import anon.util.XMLParseException;
import anon.util.XMLUtil;
import java.util.Enumeration;
import java.util.Vector;
import org.bouncycastle.crypto.digests.SHA1Digest;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class XMLSignatureElement
implements IXMLEncodable {
    private static final String XML_ELEMENT_NAME = "Signature";
    private static final String ELEM_CANONICALIZATION_METHOD = "CanonicalizationMethod";
    private static final String ELEM_SIGNATURE_METHOD = "SignatureMethod";
    private static final String ELEM_SIGNATURE_VALUE = "SignatureValue";
    private static final String ELEM_KEY_INFO = "KeyInfo";
    private static final String ELEM_SIGNED_INFO = "SignedInfo";
    private static final String ELEM_REFERENCE = "Reference";
    private static final String ELEM_DIGEST_VALUE = "DigestValue";
    private static final String ELEM_DIGEST_METHOD = "DigestMethod";
    private static final String ATTR_URI = "URI";
    private static final String ATTR_ALGORITHM = "Algorithm";
    private static final String DIGEST_METHOD_ALGORITHM = "http://www.w3.org/2000/09/xmldsig#sha1";
    private XMLSignature m_parent;
    private Element m_elemSignature;
    private String m_signatureMethod;
    private String m_signatureValue;
    private String m_referenceURI;
    private String m_digestMethod;
    private String m_digestValue;
    private byte[] m_signedInfoCanonical;
    private Vector m_appendedCerts;
    private Vector m_appendedCertXMLElements;
    private CertPath m_certPath;

    protected XMLSignatureElement(XMLSignature xMLSignature) {
        this.m_parent = xMLSignature;
        this.m_appendedCerts = new Vector();
        this.m_appendedCertXMLElements = new Vector();
    }

    protected XMLSignatureElement(XMLSignature xMLSignature, Element element, IMyPrivateKey iMyPrivateKey, byte[] arrby) throws Exception {
        this(xMLSignature);
        this.createSignatureElement(iMyPrivateKey, element, arrby);
    }

    protected XMLSignatureElement(XMLSignature xMLSignature, Element element) throws XMLParseException {
        if (element == null || !element.getNodeName().equals(XML_ELEMENT_NAME)) {
            throw new XMLParseException("##__root__##", "This is no signature element!");
        }
        this.m_parent = xMLSignature;
        this.m_elemSignature = element;
        this.findCertificates(this.m_elemSignature);
        Node node = XMLUtil.getFirstChildByName(this.m_elemSignature, ELEM_SIGNED_INFO);
        if (node == null) {
            this.m_signedInfoCanonical = XMLSignature.toCanonicalDeprecated(this.m_elemSignature);
            if (this.m_signedInfoCanonical == null) {
                throw new XMLParseException(ELEM_SIGNED_INFO);
            }
        } else {
            this.m_signedInfoCanonical = XMLSignature.toCanonical(node);
            Node node2 = XMLUtil.getFirstChildByName(node, ELEM_SIGNATURE_METHOD);
            this.m_signatureMethod = XMLUtil.parseValue(node2, "");
            if ((node = XMLUtil.getFirstChildByName(node, ELEM_REFERENCE)) == null) {
                throw new XMLParseException(ELEM_REFERENCE);
            }
            this.m_referenceURI = XMLUtil.parseAttribute((Node)((Element)node), ATTR_URI, "");
            node2 = XMLUtil.getFirstChildByName(node, ELEM_DIGEST_METHOD);
            this.m_digestMethod = XMLUtil.parseValue(node2, "");
            if ((node = XMLUtil.getFirstChildByName(node, ELEM_DIGEST_VALUE)) == null) {
                throw new XMLParseException(ELEM_DIGEST_VALUE);
            }
            this.m_digestValue = XMLUtil.parseValue(node, "");
        }
        if ((node = XMLUtil.getFirstChildByName(this.m_elemSignature, ELEM_SIGNATURE_VALUE)) == null) {
            throw new XMLParseException(ELEM_SIGNATURE_VALUE);
        }
        this.m_signatureValue = XMLUtil.parseValue(node, "");
    }

    private void createSignatureElement(IMyPrivateKey iMyPrivateKey, Element element, byte[] arrby) throws Exception {
        this.m_referenceURI = "";
        this.m_digestMethod = DIGEST_METHOD_ALGORITHM;
        this.m_digestValue = new String(Base64.encode(arrby, false));
        Document document = element.getOwnerDocument();
        Element element2 = document.createElement(ELEM_SIGNED_INFO);
        Element element3 = document.createElement(ELEM_CANONICALIZATION_METHOD);
        Element element4 = document.createElement(ELEM_SIGNATURE_METHOD);
        String string = iMyPrivateKey.getSignatureAlgorithm().getXMLSignatureAlgorithmReference();
        if (string != null) {
            this.m_signatureMethod = string;
            XMLUtil.setAttribute(element4, ATTR_ALGORITHM, string);
        } else {
            this.m_signatureMethod = "";
        }
        Element element5 = document.createElement(ELEM_REFERENCE);
        if (this.m_referenceURI.length() > 0) {
            element5.setAttribute(ATTR_URI, this.m_referenceURI);
        }
        Element element6 = document.createElement(ELEM_DIGEST_METHOD);
        XMLUtil.setAttribute(element6, ATTR_ALGORITHM, DIGEST_METHOD_ALGORITHM);
        Element element7 = document.createElement(ELEM_DIGEST_VALUE);
        XMLUtil.setValue((Node)element7, this.m_digestValue);
        element5.appendChild(element6);
        element5.appendChild(element7);
        element2.appendChild(element3);
        element2.appendChild(element4);
        element2.appendChild(element5);
        this.m_signedInfoCanonical = XMLSignature.toCanonical(element2);
        byte[] arrby2 = ByteSignature.sign(this.m_signedInfoCanonical, iMyPrivateKey);
        arrby2 = iMyPrivateKey.getSignatureAlgorithm().encodeForXMLSignature(arrby2);
        if (arrby2 == null) {
            throw new Exception();
        }
        this.m_signatureValue = new String(Base64.encode(arrby2, false));
        Element element8 = document.createElement(ELEM_SIGNATURE_VALUE);
        element8.appendChild(document.createTextNode(this.m_signatureValue));
        Element element9 = document.createElement(XML_ELEMENT_NAME);
        element9.appendChild(element2);
        element9.appendChild(element8);
        element.appendChild(element9);
        this.m_elemSignature = element9;
    }

    private synchronized void findCertificates(Element element) {
        this.m_appendedCerts = new Vector();
        this.m_appendedCertXMLElements = new Vector();
        Element element2 = (Element)XMLUtil.getFirstChildByName(element, ELEM_KEY_INFO);
        if (element2 == null) {
            return;
        }
        if ((element2 = (Element)XMLUtil.getFirstChildByName(element2, "X509Data")) == null) {
            return;
        }
        Node node = XMLUtil.getFirstChildByName(element2, "X509Certificate");
        while (node != null) {
            try {
                JAPCertificate jAPCertificate = JAPCertificate.getInstance(node);
                if (jAPCertificate != null) {
                    this.m_appendedCerts.addElement(jAPCertificate);
                    this.m_appendedCertXMLElements.addElement(node);
                }
            }
            catch (ClassCastException classCastException) {
                // empty catch block
            }
            node = XMLUtil.getNextSibling(node);
        }
    }

    public boolean verifyFast(Node node, IMyPublicKey iMyPublicKey) throws XMLParseException {
        boolean bl = this.verify(node, iMyPublicKey);
        if (XMLUtil.getStorageMode() == 2) {
            this.m_elemSignature = null;
            this.m_signedInfoCanonical = null;
        }
        return bl;
    }

    public boolean verify(Node node, int n, Vector vector) throws XMLParseException {
        if (this.m_appendedCerts.size() > 0) {
            Enumeration enumeration = this.m_appendedCerts.elements();
            while (enumeration.hasMoreElements()) {
                JAPCertificate jAPCertificate = (JAPCertificate)enumeration.nextElement();
                if (!this.verify(node, jAPCertificate.getPublicKey())) continue;
                Vector vector2 = (Vector)this.getCertificates().clone();
                vector2.removeElement(jAPCertificate);
                this.m_certPath = CertPath.getInstance(jAPCertificate, n, vector2);
                if (XMLUtil.getStorageMode() == 2) {
                    this.m_elemSignature = null;
                    this.m_signedInfoCanonical = null;
                }
                return true;
            }
        } else {
            Enumeration enumeration = vector.elements();
            while (enumeration.hasMoreElements()) {
                CertPath certPath = (CertPath)enumeration.nextElement();
                if (!this.verify(node, certPath.getFirstCertificate().getPublicKey())) continue;
                this.m_certPath = certPath;
                if (XMLUtil.getStorageMode() == 2) {
                    this.m_elemSignature = null;
                    this.m_signedInfoCanonical = null;
                }
                return true;
            }
        }
        if (XMLUtil.getStorageMode() == 2) {
            this.m_elemSignature = null;
            this.m_signedInfoCanonical = null;
        }
        return false;
    }

    private boolean verify(Node node, IMyPublicKey iMyPublicKey) throws XMLParseException {
        if (iMyPublicKey == null || node == null) {
            return false;
        }
        if (!this.checkMessageDigest(node)) {
            return false;
        }
        return this.checkSignature(iMyPublicKey);
    }

    private boolean checkSignature(IMyPublicKey iMyPublicKey) {
        byte[] arrby = Base64.decode(this.m_signatureValue);
        arrby = iMyPublicKey.getSignatureAlgorithm().decodeForXMLSignature(arrby);
        if (arrby == null) {
            return false;
        }
        return ByteSignature.verify(this.m_signedInfoCanonical, arrby, iMyPublicKey);
    }

    private boolean checkMessageDigest(Node node) throws XMLParseException {
        if (this.m_digestMethod == null) {
            return true;
        }
        SHA1Digest sHA1Digest = new SHA1Digest();
        byte[] arrby = new byte[sHA1Digest.getDigestSize()];
        byte[] arrby2 = XMLSignature.toCanonical(node, this.m_parent.getSignatureElements());
        sHA1Digest.update(arrby2, 0, arrby2.length);
        sHA1Digest.doFinal(arrby, 0);
        return Util.arraysEqual(Base64.decode(this.m_digestValue), arrby);
    }

    protected Element getSignatureElement() {
        return this.m_elemSignature;
    }

    public String getSignatureMethod() {
        return this.m_signatureMethod;
    }

    public String getDigestMethod() {
        return this.m_digestMethod;
    }

    public String getReferenceURI() {
        return this.m_referenceURI.trim();
    }

    public CertPath getCertPath() {
        return this.m_certPath;
    }

    private synchronized Vector getCertificates() {
        Vector vector = new Vector(this.m_appendedCerts.size());
        Enumeration enumeration = this.m_appendedCerts.elements();
        while (enumeration.hasMoreElements()) {
            vector.addElement(enumeration.nextElement());
        }
        return vector;
    }

    public synchronized boolean containsCertificate(JAPCertificate jAPCertificate) {
        return this.m_appendedCerts.contains(jAPCertificate);
    }

    public synchronized int countCertificates() {
        return this.m_appendedCerts.size();
    }

    public synchronized void clearCertificates() {
        Enumeration enumeration = this.m_appendedCertXMLElements.elements();
        while (enumeration.hasMoreElements()) {
            Element element = (Element)enumeration.nextElement();
            Node node = element.getParentNode();
            if (node == null) continue;
            node.removeChild(element);
        }
        this.m_appendedCertXMLElements.removeAllElements();
        this.m_appendedCerts.removeAllElements();
    }

    public synchronized boolean removeCertificate(JAPCertificate jAPCertificate) {
        int n = this.m_appendedCerts.indexOf(jAPCertificate);
        if (n >= 0) {
            this.m_appendedCerts.removeElementAt(n);
            if (n >= this.m_appendedCertXMLElements.size()) {
                this.m_appendedCertXMLElements.removeElementAt(n);
                return true;
            }
        }
        return false;
    }

    public synchronized boolean addCertificate(JAPCertificate jAPCertificate) {
        Node node;
        if (jAPCertificate == null) {
            return false;
        }
        Node node2 = XMLUtil.getFirstChildByName(this.m_elemSignature, ELEM_KEY_INFO);
        if (node2 == null) {
            node2 = this.m_elemSignature.getOwnerDocument().createElement(ELEM_KEY_INFO);
            this.m_elemSignature.appendChild(node2);
        }
        if ((node = XMLUtil.getFirstChildByName(node2, "X509Data")) == null) {
            node = this.m_elemSignature.getOwnerDocument().createElement("X509Data");
            node2.appendChild(node);
        }
        if (this.m_appendedCerts.contains(jAPCertificate) || !this.checkSignature(jAPCertificate.getPublicKey())) {
            return false;
        }
        Element element = jAPCertificate.toXmlElement(this.m_elemSignature.getOwnerDocument());
        this.m_appendedCerts.addElement(jAPCertificate);
        this.m_appendedCertXMLElements.addElement(element);
        node.appendChild(element);
        return true;
    }

    public Element toXmlElement(Document document) {
        Element element = this.toXmlElementInternal(document);
        if (element != null && this.m_elemSignature == element) {
            element = (Element)element.cloneNode(true);
        }
        return element;
    }

    private Element toXmlElementInternal(Document document) {
        if (XMLUtil.getStorageMode() == 2 && this.m_elemSignature == null) {
            return null;
        }
        if (this.m_elemSignature.getOwnerDocument() == document) {
            return this.m_elemSignature;
        }
        try {
            return (Element)XMLUtil.importNode(document, this.m_elemSignature, true);
        }
        catch (Exception exception) {
            return null;
        }
    }
}

