/*
 * Decompiled with CFR 0.150.
 */
package org.bouncycastle.math.ec.endo;

import org.bouncycastle.math.ec.ECPointMap;

public interface ECEndomorphism {
    public ECPointMap getPointMap();

    public boolean hasEfficientPointMap();
}

