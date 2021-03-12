/*
 * Decompiled with CFR 0.150.
 */
package anon.transport.address;

import anon.transport.address.AddressParameter;

public interface IAddress {
    public String getTransportIdentifier();

    public AddressParameter[] getAllParameters();
}

