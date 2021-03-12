/*
 * Decompiled with CFR 0.150.
 */
package anon.crypto;

import anon.crypto.CertPath;
import anon.crypto.CertificateInfoStructure;
import anon.crypto.JAPCertificate;
import anon.util.IXMLEncodable;
import anon.util.XMLUtil;
import java.util.Vector;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class CertificateContainer
implements IXMLEncodable {
    public static final String XML_ELEMENT_NAME = "CertificateContainer";
    private static final String XML_ATTR_REMOVABLE = "removable";
    private JAPCertificate m_parentCertificate;
    private CertPath m_certPath;
    private int m_certificateType;
    private boolean m_enabled;
    private boolean m_certificateNeedsVerification;
    private boolean m_onlyHardRemovable;
    private boolean m_bNotRemovable = false;
    private Vector m_lockList;

    public CertificateContainer(CertPath certPath, int n, boolean bl) {
        if (certPath == null || certPath.getFirstCertificate() == null) {
            throw new IllegalArgumentException("Invalid cert path!");
        }
        this.m_certPath = certPath;
        this.m_certificateType = n;
        this.m_certificateNeedsVerification = bl;
        this.m_enabled = true;
        this.m_parentCertificate = null;
        this.m_onlyHardRemovable = false;
        this.m_bNotRemovable = false;
        this.m_lockList = new Vector();
    }

    public CertificateContainer(Element element) throws Exception {
        Element element2 = (Element)XMLUtil.getFirstChildByName(element, "CertificateType");
        if (element2 == null) {
            throw new Exception("No CertificateType node found.");
        }
        this.m_certificateType = XMLUtil.parseValue((Node)element2, -1);
        if (this.m_certificateType == -1) {
            throw new Exception("Invalid CertificateType value.");
        }
        Element element3 = (Element)XMLUtil.getFirstChildByName(element, "CertificateNeedsVerification");
        if (element3 == null) {
            throw new Exception("No CertificateNeedsVerification node found.");
        }
        this.m_certificateNeedsVerification = XMLUtil.parseValue((Node)element3, true);
        Element element4 = (Element)XMLUtil.getFirstChildByName(element, "CertificateEnabled");
        if (element4 == null) {
            throw new Exception("No CertificateEnabled node found.");
        }
        this.m_enabled = XMLUtil.parseValue((Node)element4, false);
        Element element5 = (Element)XMLUtil.getFirstChildByName(element, "CertificateData");
        if (element5 == null) {
            throw new Exception("No CertificateData node found.");
        }
        JAPCertificate jAPCertificate = JAPCertificate.getInstance(XMLUtil.getFirstChildByName(element5, "X509Certificate"));
        if (jAPCertificate == null) {
            this.m_certPath = new CertPath((Element)XMLUtil.getFirstChildByName(element5, "CertPath"));
        }
        this.m_parentCertificate = null;
        this.m_onlyHardRemovable = true;
        this.m_bNotRemovable = !XMLUtil.parseAttribute((Node)element, XML_ATTR_REMOVABLE, false);
        this.m_lockList = new Vector();
    }

    public JAPCertificate getCertificate() {
        return this.m_certPath.getFirstCertificate();
    }

    public CertPath getCertPath() {
        return this.m_certPath;
    }

    public void setParentCertificate(JAPCertificate jAPCertificate) {
        this.m_parentCertificate = jAPCertificate;
    }

    public JAPCertificate getParentCertificate() {
        return this.m_parentCertificate;
    }

    public int getCertificateType() {
        return this.m_certificateType;
    }

    public boolean getCertificateNeedsVerification() {
        return this.m_certificateNeedsVerification;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public boolean isAvailable() {
        boolean bl = false;
        CertificateContainer certificateContainer = this;
        synchronized (certificateContainer) {
            bl = (!this.m_certificateNeedsVerification || this.m_parentCertificate != null) && this.m_enabled;
        }
        return bl;
    }

    public boolean isEnabled() {
        return this.m_enabled;
    }

    public void setEnabled(boolean bl) {
        this.m_enabled = bl;
        this.m_certPath.resetVerification();
    }

    public void enableOnlyHardRemovable() {
        this.m_onlyHardRemovable = true;
    }

    public boolean isOnlyHardRemovable() {
        return this.m_onlyHardRemovable;
    }

    public void enableNotRemovable() {
        this.m_bNotRemovable = true;
    }

    public boolean isNotRemovable() {
        return this.m_bNotRemovable;
    }

    public Vector getLockList() {
        return this.m_lockList;
    }

    public CertificateInfoStructure getInfoStructure() {
        return new CertificateInfoStructure(this.m_certPath, this.m_parentCertificate, this.m_certificateType, this.m_enabled, this.m_certificateNeedsVerification, this.m_onlyHardRemovable, this.m_bNotRemovable);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public Element toXmlElement(Document document) {
        Element element = document.createElement(XML_ELEMENT_NAME);
        CertificateContainer certificateContainer = this;
        synchronized (certificateContainer) {
            XMLUtil.setAttribute(element, XML_ATTR_REMOVABLE, !this.m_bNotRemovable);
            Element element2 = document.createElement("CertificateType");
            XMLUtil.setValue((Node)element2, this.m_certificateType);
            Element element3 = document.createElement("CertificateNeedsVerification");
            XMLUtil.setValue((Node)element3, this.m_certificateNeedsVerification);
            Element element4 = document.createElement("CertificateEnabled");
            XMLUtil.setValue((Node)element4, this.m_enabled);
            Element element5 = document.createElement("CertificateData");
            element5.appendChild(this.m_certPath.toXmlElement(document));
            element.appendChild(element2);
            element.appendChild(element3);
            element.appendChild(element4);
            element.appendChild(element5);
        }
        return element;
    }

    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (object == null || !(object instanceof CertificateContainer)) {
            return false;
        }
        return this.m_certPath.getFirstCertificate().getId().equals(((CertificateContainer)object).getCertificate().getId());
    }

    public String getId() {
        return this.m_certPath.getFirstCertificate().getId();
    }

    public int hashCode() {
        return this.m_certPath.getFirstCertificate().hashCode();
    }
}

