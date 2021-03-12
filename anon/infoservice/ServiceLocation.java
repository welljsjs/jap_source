/*
 * Decompiled with CFR 0.150.
 */
package anon.infoservice;

import anon.crypto.AbstractX509AlternativeName;
import anon.crypto.JAPCertificate;
import anon.crypto.X509DistinguishedName;
import anon.crypto.X509SubjectAlternativeName;
import anon.util.Util;
import anon.util.XMLUtil;
import java.util.Vector;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class ServiceLocation {
    public static final String XML_ELEMENT_NAME = "Location";
    public static final String XML_ELEMENT_CITY = "City";
    public static final String XML_ELEMENT_STATE = "State";
    public static final String XML_ELEMENT_COUNTRY = "Country";
    public static final String XML_ELEMENT_POSITION = "Position";
    public static final String XML_ELEMENT_GEO = "Geo";
    public static final String XML_ELEMENT_LONGITUDE = "Longitude";
    public static final String XML_ELEMENT_LATITUDE = "Latitude";
    private String city;
    private String state;
    private String m_country;
    private String m_commonName;
    private String longitude;
    private String latitude;

    public ServiceLocation(Node node, JAPCertificate jAPCertificate) {
        Node node2;
        Object object;
        if (jAPCertificate != null) {
            Vector vector;
            X509SubjectAlternativeName x509SubjectAlternativeName;
            X509DistinguishedName x509DistinguishedName = jAPCertificate.getSubject();
            this.city = x509DistinguishedName.getLocalityName();
            this.state = x509DistinguishedName.getStateOrProvince();
            this.m_country = x509DistinguishedName.getCountryCode();
            this.m_commonName = x509DistinguishedName.getCommonName();
            object = jAPCertificate.getExtensions().getExtension(X509SubjectAlternativeName.IDENTIFIER);
            if (object != null && object instanceof X509SubjectAlternativeName && (x509SubjectAlternativeName = (X509SubjectAlternativeName)object).getTags().size() == 2 && x509SubjectAlternativeName.getValues().size() == 2 && (vector = x509SubjectAlternativeName.getTags()).elementAt(0).equals(AbstractX509AlternativeName.TAG_OTHER) && vector.elementAt(1).equals(AbstractX509AlternativeName.TAG_OTHER)) {
                vector = x509SubjectAlternativeName.getValues();
                try {
                    this.longitude = vector.elementAt(0).toString();
                    Util.parseDouble(this.longitude);
                    this.longitude = this.longitude.trim();
                }
                catch (NumberFormatException numberFormatException) {
                    this.longitude = "";
                }
                try {
                    this.latitude = vector.elementAt(1).toString();
                    Util.parseDouble(this.latitude);
                    this.latitude = this.latitude.trim();
                }
                catch (NumberFormatException numberFormatException) {
                    this.latitude = "";
                }
            }
        }
        if (this.city == null || this.city.trim().length() == 0) {
            node2 = XMLUtil.getFirstChildByName(node, XML_ELEMENT_CITY);
            this.city = XMLUtil.parseValue(node2, "");
        }
        if (this.state == null || this.state.trim().length() == 0) {
            node2 = XMLUtil.getFirstChildByName(node, XML_ELEMENT_STATE);
            this.state = XMLUtil.parseValue(node2, "");
        }
        if (this.m_country == null || this.m_country.trim().length() == 0) {
            node2 = XMLUtil.getFirstChildByName(node, XML_ELEMENT_COUNTRY);
            this.m_country = XMLUtil.parseValue(node2, "");
        }
        object = XMLUtil.getFirstChildByName(node, XML_ELEMENT_POSITION);
        object = XMLUtil.getFirstChildByName((Node)object, XML_ELEMENT_GEO);
        if (this.longitude == null || this.longitude.trim().length() == 0) {
            node2 = XMLUtil.getFirstChildByName((Node)object, XML_ELEMENT_LONGITUDE);
            this.longitude = XMLUtil.parseValue(node2, "");
        }
        if (this.latitude == null || this.latitude.trim().length() == 0) {
            node2 = XMLUtil.getFirstChildByName((Node)object, XML_ELEMENT_LATITUDE);
            this.latitude = XMLUtil.parseValue(node2, "");
        }
    }

    public String getCity() {
        return this.city;
    }

    public String getState() {
        return this.state;
    }

    public String getCountryCode() {
        return this.m_country;
    }

    public String getCommonName() {
        return this.m_commonName;
    }

    public String getLongitude() {
        return this.longitude;
    }

    public String getLatitude() {
        return this.latitude;
    }

    public Element toXMLElement(Document document) {
        if (document == null) {
            return null;
        }
        Element element = document.createElement(XML_ELEMENT_NAME);
        if (this.city != null) {
            XMLUtil.createChildElementWithValue(element, XML_ELEMENT_CITY, XMLUtil.filterXMLChars(this.city));
        }
        if (this.state != null) {
            XMLUtil.createChildElementWithValue(element, XML_ELEMENT_STATE, XMLUtil.filterXMLChars(this.state));
        }
        if (this.m_country != null) {
            XMLUtil.createChildElementWithValue(element, XML_ELEMENT_COUNTRY, XMLUtil.filterXMLChars(this.m_country));
        }
        if (this.longitude != null && this.latitude != null) {
            Element element2 = XMLUtil.createChildElement(element, XML_ELEMENT_POSITION);
            Element element3 = XMLUtil.createChildElement(element2, XML_ELEMENT_GEO);
            XMLUtil.createChildElementWithValue(element3, XML_ELEMENT_LONGITUDE, XMLUtil.filterXMLChars(this.longitude));
            XMLUtil.createChildElementWithValue(element3, XML_ELEMENT_LATITUDE, XMLUtil.filterXMLChars(this.latitude));
        }
        return element;
    }
}

