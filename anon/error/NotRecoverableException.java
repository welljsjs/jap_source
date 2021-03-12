/*
 * Decompiled with CFR 0.150.
 */
package anon.error;

import anon.error.AnonServiceException;
import anon.error.INotRecoverableException;
import anon.infoservice.MixCascade;
import anon.infoservice.MixInfo;

public class NotRecoverableException
extends AnonServiceException
implements INotRecoverableException {
    private static final long serialVersionUID = 1L;
    private int m_iMixIndex = -1;

    public NotRecoverableException(MixCascade mixCascade, String string, int n) {
        super(mixCascade, string, n);
    }

    public NotRecoverableException(MixCascade mixCascade, String string, int n, int n2) {
        super(mixCascade, string, n);
        this.m_iMixIndex = n2;
    }

    public MixCascade getMixCascade() {
        return (MixCascade)this.getService();
    }

    public int getMixIndex() {
        return this.m_iMixIndex;
    }

    public MixInfo getMixInfo() {
        if (this.m_iMixIndex >= 0 && this.getService() != null && this.m_iMixIndex < this.getMixCascade().getNumberOfMixes()) {
            return this.getMixCascade().getMixInfo(this.m_iMixIndex);
        }
        return null;
    }
}

