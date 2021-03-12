/*
 * Decompiled with CFR 0.150.
 */
package anon.crypto;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.StringTokenizer;
import java.util.Vector;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x509.X509Name;

public final class X509DistinguishedName {
    public static final String IDENTIFIER_CN = X509Name.CN.getId();
    public static final String IDENTIFIER_C = X509Name.C.getId();
    public static final String IDENTIFIER_ST = X509Name.ST.getId();
    public static final String IDENTIFIER_L = X509Name.L.getId();
    public static final String IDENTIFIER_O = X509Name.O.getId();
    public static final String IDENTIFIER_OU = X509Name.OU.getId();
    public static final String IDENTIFIER_E = X509Name.E.getId();
    public static final String IDENTIFIER_EmailAddress = X509Name.EmailAddress.getId();
    public static final String IDENTIFIER_SURNAME = X509Name.SURNAME.getId();
    public static final String IDENTIFIER_GIVENNAME = X509Name.GIVENNAME.getId();
    public static final String LABEL_COMMON_NAME = "CN";
    public static final String LABEL_COUNTRY = "C";
    public static final String LABEL_STATE_OR_PROVINCE = "ST";
    public static final String LABEL_LOCALITY = "L";
    public static final String LABEL_ORGANISATION = "O";
    public static final String LABEL_ORGANISATIONAL_UNIT = "OU";
    public static final String LABEL_EMAIL = "E";
    public static final String LABEL_EMAIL_ADDRESS = "EmailAddress";
    public static final String LABEL_SURNAME = "SURNAME";
    public static final String LABEL_GIVENNAME = "GIVENNAME";
    private static Vector m_sortedIdentifiers;
    private X509Name m_bcX509Name;

    public X509DistinguishedName(String string) {
        this.m_bcX509Name = new X509Name(string);
    }

    public X509DistinguishedName(Hashtable hashtable) throws IllegalCharacterException {
        if (hashtable == null) {
            throw new IllegalArgumentException("Attributes must not be null!");
        }
        Enumeration enumeration = hashtable.keys();
        Vector<Object> vector = new Vector<Object>();
        Vector<String> vector2 = new Vector<String>();
        while (enumeration.hasMoreElements()) {
            String string;
            Object object = enumeration.nextElement();
            if (hashtable.get(object) == null || (string = hashtable.get(object).toString()).trim().length() == 0) continue;
            if (!(object instanceof ASN1ObjectIdentifier)) {
                object = new ASN1ObjectIdentifier(object.toString());
            }
            if (object.equals(X509Name.E) || object.equals(X509Name.EmailAddress) || object.equals(X509Name.OU)) {
                StringTokenizer stringTokenizer = new StringTokenizer(string, ",");
                while (stringTokenizer.hasMoreTokens()) {
                    vector.addElement(object);
                    vector2.addElement(stringTokenizer.nextToken().trim());
                }
                continue;
            }
            vector.addElement(object);
            vector2.addElement(string.trim());
        }
        if (vector2.size() == 0) {
            throw new IllegalArgumentException("Attributes are empty!");
        }
        this.m_bcX509Name = new X509Name(vector, vector2);
    }

    public X509DistinguishedName(X509Name x509Name) {
        this.m_bcX509Name = x509Name;
    }

    public X509DistinguishedName(X500Name x500Name) {
        this.m_bcX509Name = X509Name.getInstance(x500Name);
    }

    public static String getAttributeNameFromAttributeIdentifier(String string) {
        if (string == null) {
            return null;
        }
        if (string.equals(IDENTIFIER_CN)) {
            return LABEL_COMMON_NAME;
        }
        if (string.equals(IDENTIFIER_C)) {
            return LABEL_COUNTRY;
        }
        if (string.equals(IDENTIFIER_ST)) {
            return LABEL_STATE_OR_PROVINCE;
        }
        if (string.equals(IDENTIFIER_L)) {
            return LABEL_LOCALITY;
        }
        if (string.equals(IDENTIFIER_O)) {
            return LABEL_ORGANISATION;
        }
        if (string.equals(IDENTIFIER_OU)) {
            return LABEL_ORGANISATIONAL_UNIT;
        }
        if (string.equals(IDENTIFIER_E)) {
            return LABEL_EMAIL;
        }
        if (string.equals(IDENTIFIER_EmailAddress)) {
            return LABEL_EMAIL_ADDRESS;
        }
        if (string.equals(IDENTIFIER_SURNAME)) {
            return LABEL_SURNAME;
        }
        if (string.equals(IDENTIFIER_GIVENNAME)) {
            return LABEL_GIVENNAME;
        }
        return string;
    }

    public String getCommonName() {
        return this.getAttributeValue(IDENTIFIER_CN);
    }

    public String getSurname() {
        return this.getAttributeValue(IDENTIFIER_SURNAME);
    }

    public String getGivenName() {
        return this.getAttributeValue(IDENTIFIER_GIVENNAME);
    }

    public String getCountryCode() {
        return this.getAttributeValue(IDENTIFIER_C);
    }

    public String getStateOrProvince() {
        return this.getAttributeValue(IDENTIFIER_ST);
    }

    public String getLocalityName() {
        return this.getAttributeValue(IDENTIFIER_L);
    }

    public String getOrganisation() {
        return this.getAttributeValue(IDENTIFIER_O);
    }

    public String getOrganisationalUnit() {
        return this.getAttributeValue(IDENTIFIER_OU);
    }

    public String getE_EmailAddress() {
        return this.getAttributeValue(IDENTIFIER_E);
    }

    public String getEmailAddress() {
        return this.getAttributeValue(IDENTIFIER_EmailAddress);
    }

    public String getAttributeValue(String string) {
        if (string == null || string.trim().length() == 0) {
            return null;
        }
        String string2 = null;
        ASN1ObjectIdentifier aSN1ObjectIdentifier = new ASN1ObjectIdentifier(string);
        Vector vector = this.m_bcX509Name.getValues();
        Vector vector2 = this.m_bcX509Name.getOIDs();
        int n = vector2.indexOf(aSN1ObjectIdentifier);
        if (n < 0) {
            return null;
        }
        string2 = (String)vector.elementAt(n);
        if (string2 != null) {
            for (int i = n + 1; i < vector2.size(); ++i) {
                if (!vector2.elementAt(i).equals(aSN1ObjectIdentifier)) continue;
                if ((string2 = string2.trim()).length() > 0) {
                    string2 = string2 + ", ";
                }
                string2 = string2 + (String)vector.elementAt(i);
            }
            string2 = string2.trim();
        }
        return string2;
    }

    public Vector getAttributeIdentifiers() {
        Vector<String> vector = new Vector<String>();
        Vector vector2 = this.m_bcX509Name.getOIDs();
        Enumeration enumeration = X509DistinguishedName.getSortedIdentifiers();
        while (enumeration.hasMoreElements()) {
            int n = vector2.indexOf(enumeration.nextElement());
            if (n < 0) continue;
            vector.addElement(((ASN1ObjectIdentifier)vector2.elementAt(n)).getId());
            vector2.removeElementAt(n);
        }
        for (int i = 0; i < vector2.size(); ++i) {
            vector.addElement(((ASN1ObjectIdentifier)vector2.elementAt(i)).getId());
        }
        return vector;
    }

    public Vector getAttributeValues() {
        Vector vector = this.m_bcX509Name.getOIDs();
        Vector vector2 = this.m_bcX509Name.getValues();
        Vector vector3 = new Vector();
        Enumeration enumeration = X509DistinguishedName.getSortedIdentifiers();
        while (enumeration.hasMoreElements()) {
            int n = vector.indexOf(enumeration.nextElement());
            if (n < 0) continue;
            vector3.addElement(vector2.elementAt(n));
            vector.removeElementAt(n);
            vector2.removeElementAt(n);
        }
        for (int i = 0; i < vector2.size(); ++i) {
            vector3.addElement(vector2.elementAt(i));
        }
        return vector3;
    }

    public Hashtable getDistinguishedName() {
        Hashtable hashtable = new Hashtable();
        Vector vector = this.getAttributeIdentifiers();
        Vector vector2 = this.getAttributeValues();
        for (int i = 0; i < vector.size(); ++i) {
            hashtable.put(vector.elementAt(i), vector2.elementAt(i));
        }
        return hashtable;
    }

    public int hashCode() {
        return this.m_bcX509Name.hashCode();
    }

    public boolean equals(Object object) {
        if (object == null) {
            return false;
        }
        if (object instanceof X509DistinguishedName) {
            return this.m_bcX509Name.equals(((X509DistinguishedName)object).m_bcX509Name);
        }
        if (object instanceof X509Name) {
            return this.m_bcX509Name.equals((X509Name)object);
        }
        if (object instanceof X500Name) {
            this.getX500Name().equals(object);
        }
        return false;
    }

    public String toString() {
        Vector vector = this.getAttributeIdentifiers();
        Vector vector2 = this.getAttributeValues();
        String string = "";
        for (int i = 0; i < vector.size(); ++i) {
            string = string + X509DistinguishedName.getAttributeNameFromAttributeIdentifier((String)vector.elementAt(i)) + "=" + vector2.elementAt(i);
            if (i + 1 >= vector.size()) continue;
            string = string + ", ";
        }
        return string;
    }

    X500Name getX500Name() {
        ASN1Primitive aSN1Primitive = this.m_bcX509Name.toASN1Primitive();
        return X500Name.getInstance(aSN1Primitive);
    }

    private static Enumeration getSortedIdentifiers() {
        if (m_sortedIdentifiers == null) {
            m_sortedIdentifiers = new Vector();
            m_sortedIdentifiers.addElement(X509Name.CN);
            m_sortedIdentifiers.addElement(X509Name.SURNAME);
            m_sortedIdentifiers.addElement(X509Name.GIVENNAME);
            m_sortedIdentifiers.addElement(X509Name.O);
            m_sortedIdentifiers.addElement(X509Name.OU);
            m_sortedIdentifiers.addElement(X509Name.L);
            m_sortedIdentifiers.addElement(X509Name.ST);
            m_sortedIdentifiers.addElement(X509Name.C);
            m_sortedIdentifiers.addElement(X509Name.E);
            m_sortedIdentifiers.addElement(X509Name.EmailAddress);
        }
        return m_sortedIdentifiers.elements();
    }

    public class IllegalCharacterException
    extends IllegalArgumentException {
        private char m_character;
        private String m_attribute;

        private IllegalCharacterException(ASN1ObjectIdentifier aSN1ObjectIdentifier, char c) {
            super("'" + c + "' characters are not allowed!");
            this.m_attribute = X509DistinguishedName.getAttributeNameFromAttributeIdentifier(aSN1ObjectIdentifier.getId());
            this.m_character = c;
        }

        public char getCharacter() {
            return this.m_character;
        }

        public String getAttribute() {
            return this.m_attribute;
        }
    }
}

