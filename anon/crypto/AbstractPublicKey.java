/*
 * Decompiled with CFR 0.150.
 */
package anon.crypto;

import anon.crypto.IMyPublicKey;
import anon.crypto.ISignatureVerificationAlgorithm;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import org.bouncycastle.asn1.DEROutputStream;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public abstract class AbstractPublicKey
implements IMyPublicKey {
    private static final long serialVersionUID = 1L;

    protected AbstractPublicKey() {
    }

    public AbstractPublicKey(SubjectPublicKeyInfo subjectPublicKeyInfo) {
    }

    public final byte[] getEncoded() {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        DEROutputStream dEROutputStream = new DEROutputStream(byteArrayOutputStream);
        try {
            dEROutputStream.writeObject(this.getAsSubjectPublicKeyInfo());
            dEROutputStream.close();
        }
        catch (IOException iOException) {
            throw new RuntimeException("IOException while encoding public key");
        }
        return byteArrayOutputStream.toByteArray();
    }

    public abstract int hashCode();

    public abstract boolean equals(Object var1);

    public abstract /* synthetic */ int getKeyLength();

    public abstract /* synthetic */ SubjectPublicKeyInfo getAsSubjectPublicKeyInfo();

    public abstract /* synthetic */ ISignatureVerificationAlgorithm getSignatureAlgorithm();

    public abstract /* synthetic */ String getFormat();

    public abstract /* synthetic */ String getAlgorithm();

    public abstract /* synthetic */ Element toXmlElement(Document var1);
}

