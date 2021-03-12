/*
 * Decompiled with CFR 0.150.
 */
package anon.forward.client;

import anon.infoservice.MixCascade;
import java.util.Enumeration;
import java.util.Vector;

public class ForwardConnectionDescriptor {
    private Vector m_supportedMixCascades = new Vector();
    private int m_maximumBandwidth = 0;
    private int m_guaranteedBandwidth = 0;
    private int m_minDummyTrafficInterval = -1;

    public void addMixCascade(MixCascade mixCascade) {
        this.m_supportedMixCascades.addElement(mixCascade);
    }

    public Vector getMixCascadeList() {
        Vector vector = new Vector();
        Enumeration enumeration = this.m_supportedMixCascades.elements();
        while (enumeration.hasMoreElements()) {
            vector.addElement(enumeration.nextElement());
        }
        return vector;
    }

    public void setMaximumBandwidth(int n) {
        this.m_maximumBandwidth = n;
    }

    public int getMaximumBandwidth() {
        return this.m_maximumBandwidth;
    }

    public void setGuaranteedBandwidth(int n) {
        this.m_guaranteedBandwidth = n;
    }

    public int getGuaranteedBandwidth() {
        return this.m_guaranteedBandwidth;
    }

    public void setMinDummyTrafficInterval(int n) {
        this.m_minDummyTrafficInterval = n;
    }

    public int getMinDummyTrafficInterval() {
        return this.m_minDummyTrafficInterval;
    }
}

