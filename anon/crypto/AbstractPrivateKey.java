/*
 * Decompiled with CFR 0.150.
 */
package anon.crypto;

import anon.crypto.IMyPrivateKey;
import anon.crypto.IMyPublicKey;
import anon.crypto.ISignatureCreationAlgorithm;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import org.bouncycastle.asn1.DEROutputStream;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public abstract class AbstractPrivateKey
implements IMyPrivateKey {
    private static final long serialVersionUID = 1L;

    protected AbstractPrivateKey() {
    }

    public AbstractPrivateKey(PrivateKeyInfo privateKeyInfo) {
    }

    public final byte[] getEncoded() {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        DEROutputStream dEROutputStream = new DEROutputStream(byteArrayOutputStream);
        try {
            dEROutputStream.writeObject(this.getAsPrivateKeyInfo());
            dEROutputStream.close();
        }
        catch (IOException iOException) {
            throw new RuntimeException("IOException while encoding private key");
        }
        return byteArrayOutputStream.toByteArray();
    }

    public abstract /* synthetic */ PrivateKeyInfo getAsPrivateKeyInfo();

    public abstract /* synthetic */ ISignatureCreationAlgorithm getSignatureAlgorithm();

    public abstract /* synthetic */ IMyPublicKey createPublicKey();

    public abstract /* synthetic */ String getFormat();

    public abstract /* synthetic */ String getAlgorithm();

    public abstract /* synthetic */ Element toXmlElement(Document var1);
}

