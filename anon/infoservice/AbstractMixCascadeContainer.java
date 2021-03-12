/*
 * Decompiled with CFR 0.150.
 */
package anon.infoservice;

import anon.AnonServerDescription;
import anon.IServiceContainer;
import anon.client.ITrustModel;
import anon.infoservice.MixCascade;
import java.util.Observable;

public abstract class AbstractMixCascadeContainer
extends Observable
implements IServiceContainer {
    public void reset() {
    }

    public abstract MixCascade getNextRandomCascade();

    public abstract MixCascade getNextCascade();

    public abstract MixCascade getCurrentCascade();

    public final AnonServerDescription getCurrentService() {
        return this.getCurrentCascade();
    }

    public abstract void keepCurrentService(boolean var1);

    public abstract boolean isServiceAutoSwitched();

    public abstract boolean isReconnectedAutomatically();

    public abstract ITrustModel getTrustModel();
}

