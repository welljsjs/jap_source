/*
 * Decompiled with CFR 0.150.
 */
package org.bouncycastle.asn1;

import java.io.InputStream;
import org.bouncycastle.asn1.IndefiniteLengthInputStream;

abstract class LimitedInputStream
extends InputStream {
    protected final InputStream _in;
    private int _limit;

    LimitedInputStream(InputStream inputStream, int n) {
        this._in = inputStream;
        this._limit = n;
    }

    int getRemaining() {
        return this._limit;
    }

    protected void setParentEofDetect(boolean bl) {
        if (this._in instanceof IndefiniteLengthInputStream) {
            ((IndefiniteLengthInputStream)this._in).setEofOn00(bl);
        }
    }
}

