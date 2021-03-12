/*
 * Decompiled with CFR 0.150.
 */
package anon.pay;

import anon.pay.PayMessage;

public interface IMessageListener {
    public void messageReceived(PayMessage var1);

    public void messageRemoved(PayMessage var1);
}

