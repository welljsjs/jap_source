/*
 * Decompiled with CFR 0.150.
 */
package anon.pay;

import anon.infoservice.MixCascade;
import anon.pay.PayAccount;

public interface IAIEventListener {
    public void accountEmpty(PayAccount var1, MixCascade var2);

    public void accountChanged(PayAccount var1, MixCascade var2);
}

