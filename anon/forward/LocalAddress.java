/*
 * Decompiled with CFR 0.150.
 */
package anon.forward;

import anon.transport.address.AddressParameter;
import anon.transport.address.IAddress;

public class LocalAddress
implements IAddress {
    public static final String TRANSPORT_IDENTIFIER = "local";

    public AddressParameter[] getAllParameters() {
        return new AddressParameter[0];
    }

    public String getTransportIdentifier() {
        return TRANSPORT_IDENTIFIER;
    }
}

