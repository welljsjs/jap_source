/*
 * Decompiled with CFR 0.150.
 */
package HTTPClient;

import HTTPClient.AuthSchemeNotImplException;
import HTTPClient.AuthorizationInfo;
import HTTPClient.Request;
import HTTPClient.Response;
import HTTPClient.RoRequest;
import HTTPClient.RoResponse;
import java.io.IOException;

public interface AuthorizationHandler {
    public AuthorizationInfo[] orderChallenges(AuthorizationInfo[] var1, RoRequest var2, RoResponse var3, boolean var4);

    public AuthorizationInfo getAuthorization(AuthorizationInfo var1, RoRequest var2, RoResponse var3, boolean var4) throws AuthSchemeNotImplException;

    public AuthorizationInfo fixupAuthInfo(AuthorizationInfo var1, Request var2, AuthorizationInfo var3, RoResponse var4, boolean var5) throws AuthSchemeNotImplException;

    public void handleAuthHeaders(Response var1, RoRequest var2, AuthorizationInfo var3, AuthorizationInfo var4) throws IOException;

    public void handleAuthTrailers(Response var1, RoRequest var2, AuthorizationInfo var3, AuthorizationInfo var4) throws IOException;

    public void addAuthorizationInfo(String var1, String var2, int var3, String var4, Object var5, Object var6, Object var7) throws AuthSchemeNotImplException;
}

