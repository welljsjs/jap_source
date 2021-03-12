/*
 * Decompiled with CFR 0.150.
 */
package anon;

import anon.client.ITrustModel;

public interface IServiceContainer {
    public void keepCurrentService(boolean var1);

    public boolean isServiceAutoSwitched();

    public boolean isReconnectedAutomatically();

    public ITrustModel getTrustModel();

    public void reset();
}

