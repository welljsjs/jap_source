/*
 * Decompiled with CFR 0.150.
 */
package anon.crypto;

import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.DERNull;
import org.bouncycastle.asn1.nist.NISTNamedCurves;
import org.bouncycastle.asn1.sec.SECNamedCurves;
import org.bouncycastle.asn1.sec.SECObjectIdentifiers;
import org.bouncycastle.asn1.teletrust.TeleTrusTNamedCurves;
import org.bouncycastle.asn1.x9.X962NamedCurves;
import org.bouncycastle.asn1.x9.X962Parameters;
import org.bouncycastle.asn1.x9.X9ECParameters;
import org.bouncycastle.crypto.params.ECDomainParameters;
import org.bouncycastle.math.ec.ECCurve;

public final class MyECParams {
    private static final ASN1ObjectIdentifier IMPLICIT_CURVE_ID = SECObjectIdentifiers.secp160r1;
    ECDomainParameters m_params;
    boolean m_isImplicitlyCA = false;
    boolean m_isNamedCurve = false;
    ASN1ObjectIdentifier m_curveIdentifier = null;

    public MyECParams() {
        this(SECNamedCurves.getByOID(IMPLICIT_CURVE_ID));
        this.m_isImplicitlyCA = true;
    }

    public MyECParams(ECDomainParameters eCDomainParameters) {
        this.m_params = eCDomainParameters;
    }

    public MyECParams(X9ECParameters x9ECParameters) {
        this.m_params = new ECDomainParameters(x9ECParameters.getCurve(), x9ECParameters.getG(), x9ECParameters.getN(), x9ECParameters.getH());
    }

    public MyECParams(X962Parameters x962Parameters) {
        X9ECParameters x9ECParameters = null;
        if (x962Parameters.isNamedCurve()) {
            this.m_isNamedCurve = true;
            this.m_curveIdentifier = (ASN1ObjectIdentifier)x962Parameters.getParameters();
            x9ECParameters = SECNamedCurves.getByOID(this.m_curveIdentifier);
            if (x9ECParameters == null) {
                x9ECParameters = X962NamedCurves.getByOID(this.m_curveIdentifier);
            }
            if (x9ECParameters == null) {
                x9ECParameters = NISTNamedCurves.getByOID(this.m_curveIdentifier);
            }
            if (x9ECParameters == null) {
                x9ECParameters = TeleTrusTNamedCurves.getByOID(this.m_curveIdentifier);
            }
            if (x9ECParameters == null) {
                throw new IllegalArgumentException("Unknown Named Curve Identifier!");
            }
        } else if (x962Parameters.isImplicitlyCA()) {
            this.m_isImplicitlyCA = true;
            x9ECParameters = SECNamedCurves.getByOID(IMPLICIT_CURVE_ID);
            this.m_curveIdentifier = IMPLICIT_CURVE_ID;
        } else {
            x9ECParameters = X9ECParameters.getInstance(x962Parameters.getParameters());
            this.m_isNamedCurve = false;
        }
        this.m_params = new ECDomainParameters(x9ECParameters.getCurve(), x9ECParameters.getG(), x9ECParameters.getN(), x9ECParameters.getH());
    }

    public boolean equals(Object object) {
        if (object == null) {
            return false;
        }
        if (!(object instanceof ECDomainParameters)) {
            return false;
        }
        ECDomainParameters eCDomainParameters = (ECDomainParameters)object;
        if (eCDomainParameters.getH().equals(this.m_params.getH()) && eCDomainParameters.getN().equals(this.m_params.getN())) {
            if (eCDomainParameters.getCurve() instanceof ECCurve.F2m) {
                return ((ECCurve.F2m)eCDomainParameters.getCurve()).equals(this.m_params.getCurve());
            }
            if (eCDomainParameters.getCurve() instanceof ECCurve.Fp) {
                return ((ECCurve.Fp)eCDomainParameters.getCurve()).equals(this.m_params.getCurve());
            }
        }
        return false;
    }

    protected ECDomainParameters getECDomainParams() {
        return this.m_params;
    }

    protected X962Parameters getX962Params() {
        if (this.m_isNamedCurve) {
            return new X962Parameters(this.m_curveIdentifier);
        }
        if (this.m_isImplicitlyCA) {
            return new X962Parameters(DERNull.INSTANCE);
        }
        X9ECParameters x9ECParameters = new X9ECParameters(this.m_params.getCurve(), this.m_params.getG(), this.m_params.getN(), this.m_params.getH());
        return new X962Parameters(x9ECParameters);
    }

    protected void setNamedCurveID(ASN1ObjectIdentifier aSN1ObjectIdentifier) {
        if (aSN1ObjectIdentifier != null) {
            this.m_curveIdentifier = aSN1ObjectIdentifier;
            this.m_isNamedCurve = true;
        }
    }

    protected ASN1ObjectIdentifier getCurveID() {
        return this.m_curveIdentifier;
    }
}

