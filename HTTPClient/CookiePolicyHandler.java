/*
 * Decompiled with CFR 0.150.
 */
package HTTPClient;

import HTTPClient.Cookie;
import HTTPClient.RoRequest;
import HTTPClient.RoResponse;

public interface CookiePolicyHandler {
    public boolean acceptCookie(Cookie var1, RoRequest var2, RoResponse var3);

    public boolean sendCookie(Cookie var1, RoRequest var2);
}

