/*
 * Decompiled with CFR 0.150.
 */
package anon.crypto;

import anon.crypto.AbstractX509Extension;
import anon.infoservice.ListenerInterface;
import anon.util.Util;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.Enumeration;
import java.util.StringTokenizer;
import java.util.Vector;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1InputStream;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.DEROutputStream;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.DERTaggedObject;

public abstract class AbstractX509AlternativeName
extends AbstractX509Extension {
    public static final Integer TAG_OTHER = new Integer(0);
    public static final Integer TAG_EMAIL = new Integer(1);
    public static final Integer TAG_DNS = new Integer(2);
    public static final Integer TAG_URL = new Integer(6);
    public static final Integer TAG_IP = new Integer(7);
    public static final String OTHER_NAME = "otherName";
    public static final String RFC_822_NAME = "rfc822Name";
    public static final String DNS_NAME = "dNSName";
    public static final String X400_ADDRESS = "x400Address";
    public static final String DIRECTORY_NAME = "directoryName";
    public static final String EDI_PARTY_NAME = "ediPartyName";
    public static final String UNIFORM_RESOURCE_IDENTIFIER = "uniformResourceIdentifier";
    public static final String IP_ADDRESS = "iPAddress";
    public static final String REGISTERED_ID = "registeredID";
    private Vector m_values;
    private Vector m_tags;

    public AbstractX509AlternativeName(String string, String string2, Integer n) {
        this(string, Util.toVector(string2), Util.toVector(n));
    }

    public AbstractX509AlternativeName(String string, boolean bl, String string2, Integer n) {
        this(string, bl, Util.toVector(string2), Util.toVector(n));
    }

    public AbstractX509AlternativeName(String string, Vector vector, Vector vector2) {
        this(string, false, vector, vector2);
    }

    public AbstractX509AlternativeName(String string, boolean bl, Vector vector, Vector vector2) {
        super(string, bl, AbstractX509AlternativeName.createValue(vector, vector2));
        this.m_values = (Vector)vector.clone();
        this.m_tags = (Vector)vector2.clone();
    }

    public AbstractX509AlternativeName(ASN1Sequence aSN1Sequence) {
        super(aSN1Sequence);
        ASN1Sequence aSN1Sequence2;
        this.m_values = new Vector();
        this.m_tags = new Vector();
        try {
            aSN1Sequence2 = (ASN1Sequence)new ASN1InputStream(new ByteArrayInputStream(this.getDEROctets())).readObject();
        }
        catch (IOException iOException) {
            iOException.printStackTrace();
            throw new RuntimeException("Could not read object from DER sequence!");
        }
        Enumeration enumeration = aSN1Sequence2.getObjects();
        while (enumeration.hasMoreElements()) {
            DERTaggedObject dERTaggedObject = (DERTaggedObject)enumeration.nextElement();
            Integer n = new Integer(dERTaggedObject.getTagNo());
            byte[] arrby = ((DEROctetString)dERTaggedObject.getObject()).getOctets();
            if (n.equals(TAG_IP)) {
                String string = new String(arrby);
                if (!AbstractX509AlternativeName.isValidIP(string)) {
                    for (int i = 0; i < arrby.length; ++i) {
                        string = string + (arrby[i] & 0xFF);
                        if (i + 1 >= arrby.length) continue;
                        string = string + ".";
                    }
                }
                this.m_values.addElement(string);
            } else {
                this.m_values.addElement(new String(arrby));
            }
            this.m_tags.addElement(n);
        }
    }

    public static boolean isValidIP(String string) {
        return ListenerInterface.isValidIP(string);
    }

    public static boolean isValidEMail(String string) {
        if (string == null) {
            return false;
        }
        StringTokenizer stringTokenizer = new StringTokenizer(string, ",");
        if (!stringTokenizer.hasMoreElements()) {
            return false;
        }
        while (stringTokenizer.hasMoreElements()) {
            String string2 = stringTokenizer.nextToken().trim();
            if (string2.length() == 0) {
                return false;
            }
            int n = string2.lastIndexOf(46);
            int n2 = string2.length();
            int n3 = string2.indexOf(64);
            if (n2 == 0 || n3 == -1 || n == -1 || n3 == 0 || n < n3) {
                return false;
            }
            if (n + 2 < n2) continue;
            return false;
        }
        return true;
    }

    public Vector getValues() {
        return (Vector)this.m_values.clone();
    }

    public Vector getTags() {
        return (Vector)this.m_tags.clone();
    }

    private static byte[] createValue(Vector vector, Vector vector2) {
        byte[] arrby = null;
        ASN1EncodableVector aSN1EncodableVector = new ASN1EncodableVector();
        if (vector != null && vector.size() != 0) {
            int n;
            if (vector2 == null || vector.size() != vector2.size()) {
                throw new IllegalArgumentException("Tags have an invalid size!");
            }
            Vector vector3 = new Vector();
            Vector<String> vector4 = new Vector<String>();
            for (n = 0; n < vector.size(); ++n) {
                if (!(vector.elementAt(n) instanceof String)) {
                    throw new IllegalArgumentException("Values must be Strings!");
                }
                StringTokenizer stringTokenizer = new StringTokenizer((String)vector.elementAt(n), ",");
                while (stringTokenizer.hasMoreTokens()) {
                    vector3.addElement(vector2.elementAt(n));
                    vector4.addElement(stringTokenizer.nextToken().trim());
                }
            }
            vector2 = vector3;
            vector = vector4;
            for (n = 0; n < vector.size(); ++n) {
                String string = (String)vector.elementAt(n);
                if (string == null || string.length() == 0) continue;
                if (vector2.elementAt(n) == null || !(vector2.elementAt(n) instanceof Integer)) {
                    throw new IllegalArgumentException("Unsupported tag: " + vector2.elementAt(n));
                }
                Integer n2 = (Integer)vector2.elementAt(n);
                if (n2.equals(TAG_IP)) {
                    if (!AbstractX509AlternativeName.isValidIP(string)) {
                        throw new IllegalArgumentException("Invalid IP address: " + string);
                    }
                } else if (n2.equals(TAG_EMAIL)) {
                    if (!AbstractX509AlternativeName.isValidEMail(string)) {
                        throw new IllegalArgumentException("Invalid email address: " + string);
                    }
                } else if (n2.equals(TAG_URL)) {
                    try {
                        new URL(string);
                    }
                    catch (Exception exception) {
                        throw new IllegalArgumentException(exception.getMessage());
                    }
                } else if (!n2.equals(TAG_DNS) && !n2.equals(TAG_OTHER)) {
                    throw new IllegalArgumentException("Unsupported tag: " + n2);
                }
                if (arrby == null) {
                    arrby = string.getBytes();
                }
                aSN1EncodableVector.add(new DERTaggedObject(n2, new DEROctetString(arrby)));
                arrby = null;
            }
        }
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        try {
            new DEROutputStream(byteArrayOutputStream).writeObject(new DERSequence(aSN1EncodableVector));
        }
        catch (IOException iOException) {
            throw new RuntimeException("Error while writing object to byte array.");
        }
        return byteArrayOutputStream.toByteArray();
    }

    public static String getTagAsString(int n) {
        switch (n) {
            case 0: {
                return OTHER_NAME;
            }
            case 1: {
                return RFC_822_NAME;
            }
            case 2: {
                return DNS_NAME;
            }
            case 3: {
                return X400_ADDRESS;
            }
            case 4: {
                return DIRECTORY_NAME;
            }
            case 5: {
                return EDI_PARTY_NAME;
            }
            case 6: {
                return UNIFORM_RESOURCE_IDENTIFIER;
            }
            case 7: {
                return IP_ADDRESS;
            }
            case 8: {
                return REGISTERED_ID;
            }
        }
        return null;
    }
}

