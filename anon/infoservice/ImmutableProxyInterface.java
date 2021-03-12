/*
 * Decompiled with CFR 0.150.
 */
package anon.infoservice;

import HTTPClient.NVPair;
import anon.infoservice.ImmutableListenerInterface;
import anon.util.IXMLEncodable;

public interface ImmutableProxyInterface
extends ImmutableListenerInterface,
IXMLEncodable {
    public boolean isAuthenticationUsed();

    public String getAuthenticationPassword();

    public String getAuthenticationUserID();

    public String getProxyAuthorizationHeaderAsString();

    public NVPair getProxyAuthorizationHeader();
}

