/*
 * Decompiled with CFR 0.150.
 */
package anon.client;

import anon.client.AbstractDataChain;
import anon.client.AbstractDataChannel;

public interface IDataChannelFactory {
    public AbstractDataChannel createDataChannel(int var1, AbstractDataChain var2);
}

