/*
 * Decompiled with CFR 0.150.
 */
package org.bouncycastle.asn1;

import java.util.Date;
import org.bouncycastle.asn1.ASN1GeneralizedTime;

public class DERGeneralizedTime
extends ASN1GeneralizedTime {
    DERGeneralizedTime(byte[] arrby) {
        super(arrby);
    }

    public DERGeneralizedTime(Date date) {
        super(date);
    }

    public DERGeneralizedTime(String string) {
        super(string);
    }
}

