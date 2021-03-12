/*
 * Decompiled with CFR 0.150.
 */
package anon.crypto;

import anon.crypto.CertPath;
import anon.crypto.CertificateInfoStructure;
import anon.crypto.CertificateStore;
import anon.crypto.XMLSignature;
import anon.util.IXMLEncodable;
import anon.util.XMLUtil;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class SignatureVerifier
implements IXMLEncodable {
    public static final int DOCUMENT_CLASS_NONE = 0;
    public static final int DOCUMENT_CLASS_MIX = 1;
    public static final int DOCUMENT_CLASS_INFOSERVICE = 2;
    public static final int DOCUMENT_CLASS_UPDATE = 3;
    public static final int DOCUMENT_CLASS_PAYMENT = 4;
    public static final int DOCUMENT_CLASS_TERMS = 5;
    public static final String XML_ELEMENT_NAME = "SignatureVerification";
    private static final String XML_ATTR_CHECK = "check";
    private static final String XML_DOCUMENT_CLASS = "DocumentClass";
    private static final String XML_ATTR_CLASS = "class";
    private static SignatureVerifier ms_svInstance;
    private Hashtable m_hashSignatureChecks;
    private CertificateStore m_trustedCertificates = new CertificateStore();
    private boolean m_checkSignatures = true;
    static /* synthetic */ Class class$anon$crypto$SignatureVerifier;

    private SignatureVerifier() {
        this.m_hashSignatureChecks = new Hashtable();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static SignatureVerifier getInstance() {
        Class class_ = class$anon$crypto$SignatureVerifier == null ? (class$anon$crypto$SignatureVerifier = SignatureVerifier.class$("anon.crypto.SignatureVerifier")) : class$anon$crypto$SignatureVerifier;
        synchronized (class_) {
            if (ms_svInstance == null) {
                ms_svInstance = new SignatureVerifier();
            }
        }
        return ms_svInstance;
    }

    public static String getXmlSettingsRootNodeName() {
        return XML_ELEMENT_NAME;
    }

    public synchronized void setCheckSignatures(boolean bl) {
        if (this.m_checkSignatures != bl) {
            this.m_checkSignatures = bl;
            this.m_trustedCertificates.reset();
        }
    }

    public void setCheckSignatures(int n, boolean bl) {
        this.m_hashSignatureChecks.put(new Integer(n), new Boolean(bl));
    }

    public boolean isCheckSignatures(int n) {
        if (!this.isCheckSignatures()) {
            return false;
        }
        Boolean bl = (Boolean)this.m_hashSignatureChecks.get(new Integer(n));
        if (bl == null) {
            return true;
        }
        return bl;
    }

    public boolean isCheckSignatures() {
        return this.m_checkSignatures;
    }

    public CertificateStore getVerificationCertificateStore() {
        return this.m_trustedCertificates;
    }

    public boolean verifyXml(Document document, int n) {
        if (!this.isCheckSignatures(n)) {
            return true;
        }
        if (document == null) {
            return false;
        }
        return this.verifyXml(document.getDocumentElement(), n);
    }

    public boolean verifyXml(Element element, int n) {
        if (!this.isCheckSignatures(n)) {
            return true;
        }
        if (element == null) {
            return false;
        }
        XMLSignature xMLSignature = this.getVerifiedXml(element, n);
        if (xMLSignature != null) {
            return xMLSignature.isVerified();
        }
        return false;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public XMLSignature getVerifiedXml(Element element, int n) {
        XMLSignature xMLSignature = null;
        CertificateStore certificateStore = this.m_trustedCertificates;
        synchronized (certificateStore) {
            Vector vector = new Vector();
            switch (n) {
                case 1: {
                    vector = this.m_trustedCertificates.getAvailableCertificatesByType(2);
                    break;
                }
                case 2: {
                    vector = this.m_trustedCertificates.getAvailableCertificatesByType(3);
                    break;
                }
                case 3: {
                    vector = this.m_trustedCertificates.getAvailableCertificatesByType(4);
                    break;
                }
                case 4: {
                    vector = this.m_trustedCertificates.getAvailableCertificatesByType(7);
                    break;
                }
                case 5: {
                    vector = this.m_trustedCertificates.getAvailableCertificatesByType(9);
                }
            }
            Vector<CertPath> vector2 = new Vector<CertPath>();
            Enumeration enumeration = vector.elements();
            while (enumeration.hasMoreElements()) {
                CertificateInfoStructure certificateInfoStructure = (CertificateInfoStructure)enumeration.nextElement();
                if (!certificateInfoStructure.isAvailable()) continue;
                vector2.addElement(certificateInfoStructure.getCertPath());
            }
            try {
                xMLSignature = XMLSignature.getVerified(element, n, vector2);
            }
            catch (Exception exception) {
                exception.printStackTrace();
            }
        }
        return xMLSignature;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public Element toXmlElement(Document document) {
        Element element = document.createElement(XML_ELEMENT_NAME);
        CertificateStore certificateStore = this.m_trustedCertificates;
        synchronized (certificateStore) {
            Element element2 = document.createElement("CheckSignatures");
            XMLUtil.setAttribute(element2, XML_ATTR_CHECK, this.m_checkSignatures);
            Object object = this.m_hashSignatureChecks;
            synchronized (object) {
                Enumeration enumeration = this.m_hashSignatureChecks.keys();
                while (enumeration.hasMoreElements()) {
                    Integer n = (Integer)enumeration.nextElement();
                    boolean bl = (Boolean)this.m_hashSignatureChecks.get(n);
                    Element element3 = document.createElement(XML_DOCUMENT_CLASS);
                    XMLUtil.setAttribute(element3, XML_ATTR_CLASS, n);
                    XMLUtil.setAttribute(element3, XML_ATTR_CHECK, bl);
                    element2.appendChild(element3);
                }
            }
            object = this.m_trustedCertificates.toXmlElement(document);
            element.appendChild(element2);
            element.appendChild((Node)object);
        }
        return element;
    }

    public void loadSettingsFromXml(Element element) throws Exception {
        this.loadSettingsFromXml(element, null);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void loadSettingsFromXml(Element element, Hashtable hashtable) throws Exception {
        CertificateStore certificateStore = this.m_trustedCertificates;
        synchronized (certificateStore) {
            Element element2 = (Element)XMLUtil.getFirstChildByName(element, "CheckSignatures");
            if (element2 == null) {
                throw new Exception("No CheckSignatures node found.");
            }
            this.m_checkSignatures = XMLUtil.parseAttribute((Node)element2, XML_ATTR_CHECK, true);
            NodeList nodeList = element2.getElementsByTagName(XML_DOCUMENT_CLASS);
            for (int i = 0; i < nodeList.getLength(); ++i) {
                int n = XMLUtil.parseAttribute(nodeList.item(i), XML_ATTR_CLASS, -1);
                if (n < 0 || hashtable != null && hashtable.containsKey(new Integer(n))) continue;
                this.m_hashSignatureChecks.put(new Integer(n), new Boolean(XMLUtil.parseAttribute(nodeList.item(i), XML_ATTR_CHECK, true)));
            }
            Element element3 = (Element)XMLUtil.getFirstChildByName(element, CertificateStore.getXmlSettingsRootNodeName());
            if (element3 == null) {
                throw new Exception("No TrustedCertificates node found.");
            }
            this.m_trustedCertificates.loadSettingsFromXml(element3);
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

