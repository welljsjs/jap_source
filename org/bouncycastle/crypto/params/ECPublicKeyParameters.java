/*
 * Decompiled with CFR 0.150.
 */
package org.bouncycastle.crypto.params;

import org.bouncycastle.crypto.params.ECDomainParameters;
import org.bouncycastle.crypto.params.ECKeyParameters;
import org.bouncycastle.math.ec.ECPoint;

public class ECPublicKeyParameters
extends ECKeyParameters {
    ECPoint Q;

    public ECPublicKeyParameters(ECPoint eCPoint, ECDomainParameters eCDomainParameters) {
        super(false, eCDomainParameters);
        this.Q = eCPoint.normalize();
    }

    public ECPoint getQ() {
        return this.Q;
    }
}

