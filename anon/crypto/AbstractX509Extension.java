/*
 * Decompiled with CFR 0.150.
 */
package anon.crypto;

import anon.crypto.X509UnknownExtension;
import anon.util.ClassUtil;
import java.lang.reflect.Constructor;
import java.util.Enumeration;
import java.util.Vector;
import logging.LogHolder;
import logging.LogType;
import org.bouncycastle.asn1.ASN1Boolean;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.x509.Extension;

public abstract class AbstractX509Extension {
    public static final String IDENTIFIER = null;
    private static Class[] AVAILABLE_EXTENSIONS = new Class[]{class$anon$crypto$X509UnknownExtension == null ? (class$anon$crypto$X509UnknownExtension = AbstractX509Extension.class$("anon.crypto.X509UnknownExtension")) : class$anon$crypto$X509UnknownExtension, class$anon$crypto$X509SubjectKeyIdentifier == null ? (class$anon$crypto$X509SubjectKeyIdentifier = AbstractX509Extension.class$("anon.crypto.X509SubjectKeyIdentifier")) : class$anon$crypto$X509SubjectKeyIdentifier, class$anon$crypto$X509AuthorityKeyIdentifier == null ? (class$anon$crypto$X509AuthorityKeyIdentifier = AbstractX509Extension.class$("anon.crypto.X509AuthorityKeyIdentifier")) : class$anon$crypto$X509AuthorityKeyIdentifier, class$anon$crypto$X509SubjectAlternativeName == null ? (class$anon$crypto$X509SubjectAlternativeName = AbstractX509Extension.class$("anon.crypto.X509SubjectAlternativeName")) : class$anon$crypto$X509SubjectAlternativeName, class$anon$crypto$X509IssuerAlternativeName == null ? (class$anon$crypto$X509IssuerAlternativeName = AbstractX509Extension.class$("anon.crypto.X509IssuerAlternativeName")) : class$anon$crypto$X509IssuerAlternativeName, class$anon$crypto$X509BasicConstraints == null ? (class$anon$crypto$X509BasicConstraints = AbstractX509Extension.class$("anon.crypto.X509BasicConstraints")) : class$anon$crypto$X509BasicConstraints, class$anon$crypto$X509KeyUsage == null ? (class$anon$crypto$X509KeyUsage = AbstractX509Extension.class$("anon.crypto.X509KeyUsage")) : class$anon$crypto$X509KeyUsage};
    private static Vector ms_classExtensions;
    private ASN1ObjectIdentifier m_identifier;
    private boolean m_critical;
    private byte[] m_value;
    private ASN1Sequence m_extension;
    static /* synthetic */ Class class$anon$crypto$X509UnknownExtension;
    static /* synthetic */ Class class$anon$crypto$X509SubjectKeyIdentifier;
    static /* synthetic */ Class class$anon$crypto$X509AuthorityKeyIdentifier;
    static /* synthetic */ Class class$anon$crypto$X509SubjectAlternativeName;
    static /* synthetic */ Class class$anon$crypto$X509IssuerAlternativeName;
    static /* synthetic */ Class class$anon$crypto$X509BasicConstraints;
    static /* synthetic */ Class class$anon$crypto$X509KeyUsage;
    static /* synthetic */ Class class$org$bouncycastle$asn1$ASN1Sequence;

    public AbstractX509Extension(String string, boolean bl, byte[] arrby) {
        ASN1EncodableVector aSN1EncodableVector = new ASN1EncodableVector();
        this.m_identifier = new ASN1ObjectIdentifier(string);
        this.m_critical = bl;
        this.m_value = arrby;
        aSN1EncodableVector.add(this.m_identifier);
        aSN1EncodableVector.add(ASN1Boolean.getInstance(bl));
        aSN1EncodableVector.add(new DEROctetString(arrby));
        this.m_extension = new DERSequence(aSN1EncodableVector);
    }

    public AbstractX509Extension(ASN1Sequence aSN1Sequence) {
        int n = 1;
        this.m_extension = aSN1Sequence;
        this.m_identifier = (ASN1ObjectIdentifier)aSN1Sequence.getObjectAt(0);
        if (aSN1Sequence.size() == 3) {
            this.m_critical = ((ASN1Boolean)aSN1Sequence.getObjectAt(1)).isTrue();
            n = 2;
        } else {
            this.m_critical = false;
        }
        this.m_value = ((DEROctetString)aSN1Sequence.getObjectAt(n)).getOctets();
    }

    static AbstractX509Extension getInstance(ASN1Encodable aSN1Encodable) {
        ASN1Sequence aSN1Sequence = null;
        if (aSN1Encodable instanceof ASN1Sequence) {
            aSN1Sequence = (ASN1Sequence)aSN1Encodable;
        } else if (aSN1Encodable instanceof Extension) {
            aSN1Sequence = (ASN1Sequence)aSN1Encodable.toASN1Primitive();
        } else {
            throw new RuntimeException("AbstractX509Extension problem --> probably incompatible changes in a new Bouncycastle Lib!");
        }
        ASN1ObjectIdentifier aSN1ObjectIdentifier = (ASN1ObjectIdentifier)aSN1Sequence.getObjectAt(0);
        Object[] arrobject = new Object[]{aSN1Sequence};
        Class[] arrclass = new Class[1];
        Class class_ = arrclass[0] = class$org$bouncycastle$asn1$ASN1Sequence == null ? (class$org$bouncycastle$asn1$ASN1Sequence = AbstractX509Extension.class$("org.bouncycastle.asn1.ASN1Sequence")) : class$org$bouncycastle$asn1$ASN1Sequence;
        if (ms_classExtensions == null) {
            try {
                ms_classExtensions = ClassUtil.findSubclasses(ClassUtil.getClassStatic());
            }
            catch (Throwable throwable) {
                LogHolder.log(2, LogType.CRYPTO, throwable);
            }
            if (ms_classExtensions == null) {
                ms_classExtensions = new Vector();
            }
            if (ms_classExtensions.size() < AVAILABLE_EXTENSIONS.length) {
                int n = ClassUtil.isFindSubclassesEnabled() ? 2 : 5;
                LogHolder.log(n, LogType.CRYPTO, "X509 extension classes have not been loaded automatically!");
                for (int i = 0; i < AVAILABLE_EXTENSIONS.length; ++i) {
                    if (ms_classExtensions.contains(AVAILABLE_EXTENSIONS[i])) continue;
                    ms_classExtensions.addElement(AVAILABLE_EXTENSIONS[i]);
                }
            }
        }
        Enumeration enumeration = ms_classExtensions.elements();
        while (enumeration.hasMoreElements()) {
            Class class_2 = (Class)enumeration.nextElement();
            try {
                if (!class_2.getDeclaredField("IDENTIFIER").get(null).equals(aSN1ObjectIdentifier.getId())) continue;
                Constructor constructor = class_2.getConstructor(arrclass);
                return (AbstractX509Extension)constructor.newInstance(arrobject);
            }
            catch (Exception exception) {
            }
        }
        return new X509UnknownExtension(aSN1Sequence);
    }

    public abstract String getName();

    public final boolean isCritical() {
        return this.m_critical;
    }

    public final String getIdentifier() {
        return this.m_identifier.getId();
    }

    public final byte[] getDEROctets() {
        return this.m_value;
    }

    public final int hashCode() {
        return this.getIdentifier().hashCode();
    }

    public final boolean equals(Object object) {
        if (object == null || !(object instanceof AbstractX509Extension)) {
            return false;
        }
        return this.getIdentifier().equals(((AbstractX509Extension)object).getIdentifier());
    }

    public abstract Vector getValues();

    public final String toString() {
        return this.getName();
    }

    final ASN1Sequence getBCExtension() {
        return this.m_extension;
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

