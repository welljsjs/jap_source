/*
 * Decompiled with CFR 0.150.
 */
package HTTPClient;

import HTTPClient.Cookie;
import HTTPClient.DefaultCookiePolicyHandler;

interface CookiePrompter {
    public boolean accept(Cookie var1, DefaultCookiePolicyHandler var2, String var3);
}

