/*
 * Decompiled with CFR 0.150.
 */
package anon.crypto;

import java.security.interfaces.DSAParams;
import java.security.interfaces.DSAPrivateKey;
import org.bouncycastle.asn1.x509.DSAParameter;
import org.bouncycastle.crypto.params.DSAParameters;

public final class MyDSAParams
extends DSAParameters
implements DSAParams {
    public MyDSAParams() {
        super(null, null, null);
    }

    public MyDSAParams(DSAParams dSAParams) {
        super(dSAParams.getP(), dSAParams.getQ(), dSAParams.getG());
    }

    public MyDSAParams(DSAParameter dSAParameter) {
        super(dSAParameter.getP(), dSAParameter.getQ(), dSAParameter.getG());
    }

    public MyDSAParams(DSAParameters dSAParameters) {
        super(dSAParameters.getP(), dSAParameters.getQ(), dSAParameters.getG(), dSAParameters.getValidationParameters());
    }

    public MyDSAParams(DSAPrivateKey dSAPrivateKey) {
        this(dSAPrivateKey.getParams());
    }

    public boolean equals(Object object) {
        if (object == null) {
            return false;
        }
        if (!(object instanceof DSAParams)) {
            return false;
        }
        DSAParams dSAParams = (DSAParams)object;
        return dSAParams.getG().equals(this.getG()) && dSAParams.getP().equals(this.getP()) && dSAParams.getQ().equals(this.getQ());
    }
}

