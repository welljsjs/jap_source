/*
 * Decompiled with CFR 0.150.
 */
package anon.infoservice;

import anon.crypto.AbstractX509AlternativeName;
import anon.crypto.CertPath;
import anon.crypto.JAPCertificate;
import anon.crypto.MultiCertPath;
import anon.crypto.X509DistinguishedName;
import anon.crypto.X509SubjectAlternativeName;
import anon.infoservice.AbstractDatabaseEntry;
import anon.infoservice.OperatorAddress;
import anon.terms.TermsAndConditions;
import anon.util.Util;
import anon.util.XMLUtil;
import java.net.URL;
import java.util.Enumeration;
import java.util.Vector;
import logging.LogHolder;
import logging.LogType;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class ServiceOperator
extends AbstractDatabaseEntry {
    public static final String XML_ELEMENT_NAME = "Operator";
    public static final String XML_ELEMENT_ORGANISATION = "Organisation";
    public static final String XML_ELEMENT_COUNTRYCODE = "CountryCode";
    public static final String XML_ELEMENT_URL = "URL";
    public static final String XML_ELEMENT_ORG_UNIT = "OrganisationalUnit";
    public static final String XML_ELEMENT_EMAIL = "EMail";
    public static final String XML_ELEMENT_EMAIL_SPAMSAFE = "Liame";
    private static final String AT_SUBSTITUTE = "([at]";
    private static final String DOT_SUBSTITUTE = "([dot]";
    private static final boolean SPAM_SAFE = true;
    private String m_strOrganization;
    private String m_strOrgUnit;
    private String m_strUrl;
    private String m_strEmail;
    private String m_countryCode;
    private String m_commonName;
    private long m_lastUpdate;
    private String m_strID;
    private MultiCertPath m_certPath;

    public ServiceOperator(JAPCertificate jAPCertificate) {
        super(Long.MAX_VALUE);
        X509DistinguishedName x509DistinguishedName = jAPCertificate.getSubject();
        this.m_certPath = null;
        this.m_strOrganization = x509DistinguishedName.getOrganisation();
        this.m_commonName = x509DistinguishedName.getCommonName();
        if (this.m_strOrganization == null || this.m_strOrganization.trim().length() == 0) {
            this.m_strOrganization = x509DistinguishedName.getCommonName();
        }
        this.m_countryCode = x509DistinguishedName.getCountryCode();
        this.m_strOrgUnit = x509DistinguishedName.getOrganisationalUnit();
        this.m_strEmail = x509DistinguishedName.getE_EmailAddress();
        if (this.m_strEmail == null || this.m_strEmail.trim().length() == 0) {
            this.m_strEmail = x509DistinguishedName.getEmailAddress();
        }
        Vector vector = jAPCertificate.getExtensions().getExtensions(X509SubjectAlternativeName.IDENTIFIER);
        block2: for (int i = 0; i < vector.size(); ++i) {
            X509SubjectAlternativeName x509SubjectAlternativeName = (X509SubjectAlternativeName)vector.elementAt(i);
            Vector vector2 = x509SubjectAlternativeName.getTags();
            Vector vector3 = x509SubjectAlternativeName.getValues();
            if (vector2.size() != vector3.size()) continue;
            for (int j = 0; j < vector2.size(); ++j) {
                if (!vector2.elementAt(j).equals(AbstractX509AlternativeName.TAG_URL)) continue;
                try {
                    this.m_strUrl = new URL(vector3.elementAt(j).toString()).toString();
                }
                catch (Exception exception) {}
                continue block2;
            }
        }
        this.m_strID = jAPCertificate.getSubjectKeyIdentifierConcatenated();
        if (this.m_strID == null) {
            LogHolder.log(1, LogType.DB, "Could not create ID for ServiceOperator entry!");
            this.m_strID = "";
        }
    }

    public ServiceOperator(Node node, MultiCertPath multiCertPath, long l) {
        super(Long.MAX_VALUE);
        CertPath certPath;
        String string = XMLUtil.parseValue(XMLUtil.getFirstChildByName(node, XML_ELEMENT_ORGANISATION), null);
        if (string == null) {
            string = XMLUtil.parseValue(XMLUtil.getFirstChildByName(node, "Organization"), null);
        }
        this.m_certPath = multiCertPath;
        this.m_lastUpdate = l;
        if (this.m_certPath != null && (certPath = this.m_certPath.getPath()) != null && certPath.getSecondCertificate() != null) {
            int n;
            Cloneable cloneable;
            Vector<Cloneable> vector;
            X509DistinguishedName x509DistinguishedName = certPath.getSecondCertificate().getSubject();
            this.m_strOrganization = x509DistinguishedName.getOrganisation();
            this.m_commonName = x509DistinguishedName.getCommonName();
            if (this.m_strOrganization == null || this.m_strOrganization.trim().length() == 0) {
                this.m_strOrganization = x509DistinguishedName.getCommonName();
            }
            if (this.m_strOrganization != null && this.m_strOrganization.trim().length() != 0) {
                string = this.m_strOrganization;
            }
            this.m_countryCode = x509DistinguishedName.getCountryCode();
            this.m_strOrgUnit = x509DistinguishedName.getOrganisationalUnit();
            this.m_strEmail = x509DistinguishedName.getE_EmailAddress();
            if (this.m_strEmail == null || this.m_strEmail.trim().length() == 0) {
                this.m_strEmail = x509DistinguishedName.getEmailAddress();
            }
            Vector vector2 = certPath.getSecondCertificate().getExtensions().getExtensions(X509SubjectAlternativeName.IDENTIFIER);
            block2: for (int i = 0; i < vector2.size(); ++i) {
                X509SubjectAlternativeName x509SubjectAlternativeName = (X509SubjectAlternativeName)vector2.elementAt(i);
                vector = x509SubjectAlternativeName.getTags();
                cloneable = x509SubjectAlternativeName.getValues();
                if (vector.size() != ((Vector)cloneable).size()) continue;
                for (n = 0; n < vector.size(); ++n) {
                    if (!vector.elementAt(n).equals(AbstractX509AlternativeName.TAG_URL)) continue;
                    try {
                        this.m_strUrl = new URL(((Vector)cloneable).elementAt(n).toString()).toString();
                    }
                    catch (Exception exception) {}
                    continue block2;
                }
            }
            Vector vector3 = this.m_certPath.getPaths();
            vector = new Vector<Cloneable>();
            for (n = 0; n < vector3.size(); ++n) {
                cloneable = ((CertPath)vector3.elementAt(n)).getSecondCertificate();
                if (cloneable == null) continue;
                vector.addElement(cloneable);
            }
            if (vector3.size() == 0) {
                LogHolder.log(3, LogType.CRYPTO, "No certificate paths for ServiceOperator entry available!");
            } else if (vector.size() == 0) {
                LogHolder.log(3, LogType.CRYPTO, "No operator certificates for ServiceOperator entry available!");
            }
            this.m_strID = JAPCertificate.calculateXORofSKIs(vector);
        }
        if (this.m_strID == null) {
            LogHolder.log(1, LogType.DB, "Could not create ID for ServiceOperator entry for the organization '" + string + "'! The respective operator certificate is missing.");
            this.m_strID = "";
        }
    }

    public long getVersionNumber() {
        return this.m_lastUpdate;
    }

    public long getLastUpdate() {
        return this.m_lastUpdate;
    }

    public String getId() {
        return this.m_strID;
    }

    public String getEMail() {
        return this.m_strEmail;
    }

    public String getEMailSpamSafe() {
        if (this.m_strEmail != null) {
            this.m_strEmail = Util.replaceAll(this.m_strEmail, "@", AT_SUBSTITUTE);
            this.m_strEmail = Util.replaceAll(this.m_strEmail, ".", DOT_SUBSTITUTE);
        }
        return this.m_strEmail;
    }

    public String getOrganization() {
        return this.m_strOrganization;
    }

    public String getCommonName() {
        return this.m_commonName;
    }

    public String getOrganizationUnit() {
        return this.m_strOrgUnit;
    }

    public MultiCertPath getCertPath() {
        return this.m_certPath;
    }

    public JAPCertificate getCertificate() {
        JAPCertificate jAPCertificate;
        if (this.m_certPath == null || this.m_certPath.getPath() == null || (jAPCertificate = this.m_certPath.getPath().getSecondCertificate()) == null) {
            return null;
        }
        return jAPCertificate;
    }

    public String getUrl() {
        return this.m_strUrl;
    }

    public String getCountryCode() {
        return this.m_countryCode;
    }

    public Element toXMLElement(Document document) {
        return this.toXMLElement(document, true);
    }

    public boolean hasTermsAndConditions() {
        return TermsAndConditions.getTermsAndConditions(this) != null;
    }

    public Element toXMLElement(Document document, boolean bl) {
        return this.toXMLElement(document, null, bl);
    }

    public Element toXMLElement(Document document, OperatorAddress operatorAddress, boolean bl) {
        if (document == null) {
            return null;
        }
        Element element = document.createElement(XML_ELEMENT_NAME);
        if (this.m_strOrganization != null) {
            XMLUtil.createChildElementWithValue(element, XML_ELEMENT_ORGANISATION, XMLUtil.filterXMLChars(this.m_strOrganization));
        }
        if (this.m_strUrl != null) {
            XMLUtil.createChildElementWithValue(element, XML_ELEMENT_URL, XMLUtil.filterXMLChars(this.m_strUrl));
        }
        if (this.m_countryCode != null) {
            XMLUtil.createChildElementWithValue(element, XML_ELEMENT_COUNTRYCODE, XMLUtil.filterXMLChars(this.m_countryCode));
        }
        if (this.m_strOrgUnit != null) {
            XMLUtil.createChildElementWithValue(element, XML_ELEMENT_ORG_UNIT, XMLUtil.filterXMLChars(this.m_strOrgUnit));
        }
        if (this.m_strEmail != null) {
            XMLUtil.createChildElementWithValue(element, bl ? XML_ELEMENT_EMAIL_SPAMSAFE : XML_ELEMENT_EMAIL, bl ? XMLUtil.filterXMLChars(this.getEMailSpamSafe()) : XMLUtil.filterXMLChars(this.getEMail()));
        }
        if (operatorAddress != null) {
            Enumeration enumeration = operatorAddress.getAddressAsNodeList(document);
            while (enumeration.hasMoreElements()) {
                element.appendChild((Element)enumeration.nextElement());
            }
        }
        return element;
    }

    public int hashCode() {
        return this.getId().hashCode();
    }

    public boolean equals(Object object) {
        if (object == null || !(object instanceof ServiceOperator)) {
            return false;
        }
        ServiceOperator serviceOperator = (ServiceOperator)object;
        return this.getId().equals(serviceOperator.getId());
    }
}

