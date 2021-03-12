/*
 * Decompiled with CFR 0.150.
 */
package anon.crypto;

import anon.crypto.AbstractX509Extension;
import anon.util.Util;
import java.util.Enumeration;
import java.util.Vector;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1Set;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.DERSet;
import org.bouncycastle.asn1.x509.Extension;
import org.bouncycastle.asn1.x509.Extensions;

public final class MyX509Extensions {
    private ASN1ObjectIdentifier X509_EXTENSIONS_IDENTIFIER = new ASN1ObjectIdentifier("1.2.840.113549.1.9.14");
    private static final Vector KNOWN_CERTIFICATE_EXTENSIONS = new Vector();
    private static final Vector KNOWN_CRL_EXTENSIONS = new Vector();
    private static final Vector KNOWN_CRL_ENTRY_EXTENSIONS = new Vector();
    private ASN1Set m_extensions;
    private Vector m_vecExtensions;

    public MyX509Extensions(AbstractX509Extension abstractX509Extension) {
        this(Util.toVector(abstractX509Extension));
    }

    public MyX509Extensions(Vector vector) {
        ASN1EncodableVector aSN1EncodableVector = new ASN1EncodableVector();
        if (vector == null) {
            vector = new Vector();
        }
        this.m_vecExtensions = new Vector();
        for (int i = 0; i < vector.size(); ++i) {
            if (!(vector.elementAt(i) instanceof AbstractX509Extension)) {
                throw new IllegalArgumentException("X509 extension expected, but was: " + vector.elementAt(i));
            }
            this.m_vecExtensions.addElement(vector.elementAt(i));
            aSN1EncodableVector.add(((AbstractX509Extension)vector.elementAt(i)).getBCExtension());
        }
        ASN1EncodableVector aSN1EncodableVector2 = new ASN1EncodableVector();
        aSN1EncodableVector2.add(this.X509_EXTENSIONS_IDENTIFIER);
        aSN1EncodableVector2.add(new DERSet(new DERSequence(aSN1EncodableVector)));
        this.m_extensions = new DERSet(new DERSequence(aSN1EncodableVector2));
    }

    MyX509Extensions(ASN1Set aSN1Set) {
        this.m_extensions = aSN1Set;
        this.m_vecExtensions = new Vector();
        if (this.m_extensions.size() == 0) {
            return;
        }
        ASN1Sequence aSN1Sequence = (ASN1Sequence)this.m_extensions.getObjectAt(0);
        ASN1ObjectIdentifier aSN1ObjectIdentifier = (ASN1ObjectIdentifier)aSN1Sequence.getObjectAt(0);
        if (!aSN1ObjectIdentifier.equals(this.X509_EXTENSIONS_IDENTIFIER)) {
            throw new IllegalArgumentException("Wrong identifier: " + aSN1ObjectIdentifier.getId());
        }
        aSN1Sequence = (ASN1Sequence)((ASN1Set)aSN1Sequence.getObjectAt(1)).getObjectAt(0);
        for (int i = 0; i < aSN1Sequence.size(); ++i) {
            this.m_vecExtensions.addElement(AbstractX509Extension.getInstance(aSN1Sequence.getObjectAt(i)));
        }
    }

    MyX509Extensions(Extensions extensions) {
        this(MyX509Extensions.createExtensionsFromX509Extensions(extensions));
    }

    public int getSize() {
        return this.m_vecExtensions.size();
    }

    public AbstractX509Extension getExtension(int n) {
        return (AbstractX509Extension)this.m_vecExtensions.elementAt(n);
    }

    public AbstractX509Extension getExtension(String string) {
        for (int i = 0; i < this.m_vecExtensions.size(); ++i) {
            AbstractX509Extension abstractX509Extension = (AbstractX509Extension)this.m_vecExtensions.elementAt(i);
            if (!abstractX509Extension.getIdentifier().equals(string)) continue;
            return abstractX509Extension;
        }
        return null;
    }

    public Vector getExtensions(String string) {
        Vector<AbstractX509Extension> vector = new Vector<AbstractX509Extension>();
        for (int i = 0; i < this.m_vecExtensions.size(); ++i) {
            AbstractX509Extension abstractX509Extension = (AbstractX509Extension)this.m_vecExtensions.elementAt(i);
            if (!abstractX509Extension.getIdentifier().equals(string)) continue;
            vector.addElement(abstractX509Extension);
        }
        return vector;
    }

    public Vector getExtensions() {
        return (Vector)this.m_vecExtensions.clone();
    }

    ASN1Set getExtensionsAsASN1Set() {
        return this.m_extensions;
    }

    Extensions getExtensionsAsBCExtensions() {
        ASN1Sequence aSN1Sequence = (ASN1Sequence)this.m_extensions.getObjectAt(0);
        return Extensions.getInstance((ASN1Sequence)((ASN1Set)aSN1Sequence.getObjectAt(1)).getObjectAt(0));
    }

    private static Vector createExtensionsFromX509Extensions(ASN1Object aSN1Object) {
        Vector<AbstractX509Extension> vector = new Vector<AbstractX509Extension>();
        if (aSN1Object == null) {
            return vector;
        }
        ASN1Sequence aSN1Sequence = (ASN1Sequence)aSN1Object.toASN1Primitive();
        for (int i = 0; i < aSN1Sequence.size(); ++i) {
            ASN1Encodable aSN1Encodable = aSN1Sequence.getObjectAt(i);
            AbstractX509Extension abstractX509Extension = AbstractX509Extension.getInstance(aSN1Encodable);
            vector.addElement(abstractX509Extension);
        }
        return vector;
    }

    protected boolean hasUnknownCriticalExtensions() {
        Enumeration enumeration = this.m_vecExtensions.elements();
        while (enumeration.hasMoreElements()) {
            AbstractX509Extension abstractX509Extension = (AbstractX509Extension)enumeration.nextElement();
            String string = abstractX509Extension.getIdentifier();
            if (!abstractX509Extension.isCritical() || KNOWN_CERTIFICATE_EXTENSIONS.contains(string) || KNOWN_CRL_EXTENSIONS.contains(string) || KNOWN_CRL_EXTENSIONS.contains(string)) continue;
            return true;
        }
        return false;
    }

    static {
        KNOWN_CERTIFICATE_EXTENSIONS.addElement(Extension.authorityKeyIdentifier.toString());
        KNOWN_CERTIFICATE_EXTENSIONS.addElement(Extension.subjectKeyIdentifier.toString());
        KNOWN_CERTIFICATE_EXTENSIONS.addElement(Extension.basicConstraints.toString());
        KNOWN_CERTIFICATE_EXTENSIONS.addElement(Extension.keyUsage.toString());
        KNOWN_CERTIFICATE_EXTENSIONS.addElement(Extension.issuerAlternativeName.toString());
        KNOWN_CERTIFICATE_EXTENSIONS.addElement(Extension.subjectAlternativeName.toString());
        KNOWN_CRL_EXTENSIONS.addElement(Extension.authorityKeyIdentifier.toString());
        KNOWN_CRL_EXTENSIONS.addElement(Extension.issuerAlternativeName.toString());
        KNOWN_CRL_EXTENSIONS.addElement(Extension.issuingDistributionPoint.toString());
        KNOWN_CRL_ENTRY_EXTENSIONS.addElement(Extension.certificateIssuer.toString());
    }
}

