/*
 * Decompiled with CFR 0.150.
 */
package anon.util;

import anon.util.IProgressCapsule;

public interface IProgressCallback
extends IProgressCapsule {
    public void setValue(int var1);

    public long getCurrentSize();

    public int getCurrentMaximum();
}

