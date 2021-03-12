/*
 * Decompiled with CFR 0.150.
 */
package anon;

import anon.AnonServerDescription;
import anon.AnonServiceEventListener;
import anon.error.AnonServiceException;

public class AnonServiceEventAdapter
implements AnonServiceEventListener {
    public void currentServiceChanged(AnonServerDescription anonServerDescription) {
    }

    public void disconnected() {
    }

    public void connectionError(AnonServiceException anonServiceException) {
    }

    public void connecting(AnonServerDescription anonServerDescription, boolean bl) {
    }

    public void connectionEstablished(AnonServerDescription anonServerDescription) {
    }

    public void packetMixed(long l) {
    }

    public void dataChainErrorSignaled(AnonServiceException anonServiceException) {
    }

    public void integrityErrorSignaled(AnonServiceException anonServiceException) {
    }
}

