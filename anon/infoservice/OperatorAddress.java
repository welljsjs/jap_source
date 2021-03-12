/*
 * Decompiled with CFR 0.150.
 */
package anon.infoservice;

import anon.util.XMLParseException;
import anon.util.XMLUtil;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Enumeration;
import java.util.Vector;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class OperatorAddress {
    public static final String NODE_NAME_STREET = "Street";
    public static final String NODE_NAME_POSTALCODE = "PostalCode";
    public static final String NODE_NAME_CITY = "City";
    public static final String NODE_NAME_VAT = "Vat";
    public static final String NODE_NAME_FAX = "Fax";
    public static final String NODE_NAME_VENUE = "Venue";
    public static final String NODE_NAME_ADDITIONALINFO = "AdditionalInfo";
    public static final String NODE_NAME_OPERATORCOUNTRY = "OperatorCountry";
    public static final String PROPERTY_NAME_STREET = "street";
    public static final String PROPERTY_NAME_POSTALCODE = "postalCode";
    public static final String PROPERTY_NAME_CITY = "city";
    public static final String PROPERTY_NAME_VAT = "vat";
    public static final String PROPERTY_NAME_FAX = "fax";
    public static final String PROPERTY_NAME_VENUE = "venue";
    public static final String PROPERTY_NAME_ADDITIONALINFO = "additionalInfo";
    private String street;
    private String postalCode;
    private String city;
    private String vat;
    private String operatorCountry;
    private String fax;
    private String venue;
    private String additionalInfo;

    public OperatorAddress() {
    }

    public OperatorAddress(Element element) throws XMLParseException {
        NodeList nodeList = element.getChildNodes();
        Element element2 = null;
        Field field = null;
        for (int i = 0; i < nodeList.getLength(); ++i) {
            if (nodeList.item(i).getNodeType() != 1) continue;
            element2 = (Element)nodeList.item(i);
            try {
                String string = element2.getTagName();
                string = Character.toLowerCase(string.charAt(0)) + string.substring(1);
                field = this.getClass().getDeclaredField(string);
                field.set(this, XMLUtil.parseValue((Node)element2, (String)null));
                continue;
            }
            catch (SecurityException securityException) {
                continue;
            }
            catch (NoSuchFieldException noSuchFieldException) {
                continue;
            }
            catch (IllegalArgumentException illegalArgumentException) {
                continue;
            }
            catch (DOMException dOMException) {
                throw new XMLParseException(dOMException.getMessage());
            }
            catch (IllegalAccessException illegalAccessException) {
                // empty catch block
            }
        }
    }

    public String getStreet() {
        return this.street;
    }

    public void setStreet(String string) {
        this.street = string;
    }

    public void setAdditionalInfo(String string) {
        this.additionalInfo = string;
    }

    public String getAdditionalInfo() {
        return this.additionalInfo;
    }

    public String getPostalCode() {
        return this.postalCode;
    }

    public void setPostalCode(String string) {
        this.postalCode = string;
    }

    public String getCity() {
        return this.city;
    }

    public void setCity(String string) {
        this.city = string;
    }

    public String getVat() {
        return this.vat;
    }

    public void setVat(String string) {
        this.vat = string;
    }

    public String getFax() {
        return this.fax;
    }

    public void setFax(String string) {
        this.fax = string;
    }

    public String getOperatorCountry() {
        return this.operatorCountry;
    }

    public void setOperatorCountry(String string) {
        this.operatorCountry = string;
    }

    public String getVenue() {
        return this.venue;
    }

    public void setVenue(String string) {
        this.venue = string;
    }

    public Enumeration getAddressAsNodeList(Document document) {
        Vector<Element> vector = new Vector<Element>();
        Field[] arrfield = this.getClass().getDeclaredFields();
        for (int i = 0; i < arrfield.length; ++i) {
            if (Modifier.isFinal(arrfield[i].getModifiers()) || Modifier.isStatic(arrfield[i].getModifiers())) continue;
            try {
                Object object = arrfield[i].get(this);
                if (object == null || object.toString().equals("")) continue;
                Field field = this.getClass().getDeclaredField("NODE_NAME_" + arrfield[i].getName().toUpperCase());
                Element element = document.createElement(field.get(this).toString());
                XMLUtil.setValue((Node)element, object.toString());
                vector.addElement(element);
                continue;
            }
            catch (SecurityException securityException) {
                securityException.printStackTrace();
                continue;
            }
            catch (NoSuchFieldException noSuchFieldException) {
                noSuchFieldException.printStackTrace();
                continue;
            }
            catch (DOMException dOMException) {
                dOMException.printStackTrace();
                continue;
            }
            catch (IllegalArgumentException illegalArgumentException) {
                illegalArgumentException.printStackTrace();
                continue;
            }
            catch (IllegalAccessException illegalAccessException) {
                illegalAccessException.printStackTrace();
            }
        }
        return vector.elements();
    }
}

