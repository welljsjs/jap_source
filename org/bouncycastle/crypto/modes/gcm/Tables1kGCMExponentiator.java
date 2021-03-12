/*
 * Decompiled with CFR 0.150.
 */
package org.bouncycastle.crypto.modes.gcm;

import java.util.Vector;
import org.bouncycastle.crypto.modes.gcm.GCMExponentiator;
import org.bouncycastle.crypto.modes.gcm.GCMUtil;
import org.bouncycastle.util.Arrays;

public class Tables1kGCMExponentiator
implements GCMExponentiator {
    private Vector lookupPowX2;

    public void init(byte[] arrby) {
        int[] arrn = GCMUtil.asInts(arrby);
        if (this.lookupPowX2 != null && Arrays.areEqual(arrn, (int[])this.lookupPowX2.elementAt(0))) {
            return;
        }
        this.lookupPowX2 = new Vector(8);
        this.lookupPowX2.addElement(arrn);
    }

    public void exponentiateX(long l, byte[] arrby) {
        int[] arrn = GCMUtil.oneAsInts();
        int n = 0;
        while (l > 0L) {
            if ((l & 1L) != 0L) {
                this.ensureAvailable(n);
                GCMUtil.multiply(arrn, (int[])this.lookupPowX2.elementAt(n));
            }
            ++n;
            l >>>= 1;
        }
        GCMUtil.asBytes(arrn, arrby);
    }

    private void ensureAvailable(int n) {
        int n2 = this.lookupPowX2.size();
        if (n2 <= n) {
            int[] arrn = (int[])this.lookupPowX2.elementAt(n2 - 1);
            do {
                arrn = Arrays.clone(arrn);
                GCMUtil.multiply(arrn, arrn);
                this.lookupPowX2.addElement(arrn);
            } while (++n2 <= n);
        }
    }
}

