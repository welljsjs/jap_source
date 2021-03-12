/*
 * Decompiled with CFR 0.150.
 */
package anon.crypto;

import anon.crypto.AbstractX509Extension;
import anon.util.Util;
import java.util.StringTokenizer;
import java.util.Vector;
import org.bouncycastle.asn1.ASN1Sequence;

public abstract class AbstractX509KeyIdentifier
extends AbstractX509Extension {
    protected String m_value;

    public AbstractX509KeyIdentifier(String string, byte[] arrby) {
        super(string, false, arrby);
    }

    public AbstractX509KeyIdentifier(ASN1Sequence aSN1Sequence) {
        super(aSN1Sequence);
    }

    public String getValue() {
        return this.m_value;
    }

    public String getValueWithoutColon() {
        if (this.m_value == null) {
            return null;
        }
        StringTokenizer stringTokenizer = new StringTokenizer(this.m_value, ":");
        String string = "";
        while (stringTokenizer.hasMoreTokens()) {
            string = string + stringTokenizer.nextToken();
        }
        return string;
    }

    public Vector getValues() {
        return Util.toVector(this.m_value);
    }
}

