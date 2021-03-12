/*
 * Decompiled with CFR 0.150.
 */
package anon;

import anon.AnonServerDescription;
import anon.error.AnonServiceException;

public interface AnonServiceEventListener {
    public void connectionError(AnonServiceException var1);

    public void currentServiceChanged(AnonServerDescription var1);

    public void disconnected();

    public void connecting(AnonServerDescription var1, boolean var2);

    public void connectionEstablished(AnonServerDescription var1);

    public void packetMixed(long var1);

    public void dataChainErrorSignaled(AnonServiceException var1);

    public void integrityErrorSignaled(AnonServiceException var1);
}

