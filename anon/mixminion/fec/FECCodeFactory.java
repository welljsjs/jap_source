/*
 * Decompiled with CFR 0.150.
 */
package anon.mixminion.fec;

import anon.mixminion.fec.FECCode;
import anon.mixminion.fec.Pure16Code;
import anon.mixminion.fec.PureCode;

public class FECCodeFactory {
    protected FECCodeFactory() {
    }

    public FECCode createFECCode(int n, int n2) {
        if (n < 1 || n > 65536 || n2 < n || n2 > 65536) {
            throw new IllegalArgumentException("k and n must be between 1 and 65536 and n must not be smaller than k: k=" + n + ",n=" + n2);
        }
        if (n2 <= 256) {
            return new PureCode(n, n2);
        }
        return new Pure16Code(n, n2);
    }

    public static synchronized FECCodeFactory getDefault() {
        return new FECCodeFactory();
    }
}

