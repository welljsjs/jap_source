/*
 * Decompiled with CFR 0.150.
 */
package HTTPClient;

import HTTPClient.Cookie;
import HTTPClient.CookiePolicyHandler;
import HTTPClient.CookiePrompter;
import HTTPClient.RoRequest;
import HTTPClient.RoResponse;
import HTTPClient.Util;

class DefaultCookiePolicyHandler
implements CookiePolicyHandler {
    private String[] accept_domains = new String[0];
    private String[] reject_domains = new String[0];
    private static CookiePrompter prompter = null;

    DefaultCookiePolicyHandler() {
        String string;
        try {
            string = System.getProperty("HTTPClient.cookies.hosts.accept");
        }
        catch (Exception exception) {
            string = null;
        }
        String[] arrstring = Util.splitProperty(string);
        for (int i = 0; i < arrstring.length; ++i) {
            this.addAcceptDomain(arrstring[i].toLowerCase());
        }
        try {
            string = System.getProperty("HTTPClient.cookies.hosts.reject");
        }
        catch (Exception exception) {
            string = null;
        }
        arrstring = Util.splitProperty(string);
        for (int i = 0; i < arrstring.length; ++i) {
            this.addRejectDomain(arrstring[i].toLowerCase());
        }
    }

    public boolean acceptCookie(Cookie cookie, RoRequest roRequest, RoResponse roResponse) {
        int n;
        String string = roRequest.getConnection().getHost();
        if (string.indexOf(46) == -1) {
            string = string + ".local";
        }
        for (n = 0; n < this.reject_domains.length; ++n) {
            if (this.reject_domains[n].length() != 0 && (this.reject_domains[n].charAt(0) != '.' || !string.endsWith(this.reject_domains[n])) && (this.reject_domains[n].charAt(0) == '.' || !string.equals(this.reject_domains[n]))) continue;
            return false;
        }
        for (n = 0; n < this.accept_domains.length; ++n) {
            if (this.accept_domains[n].length() != 0 && (this.accept_domains[n].charAt(0) != '.' || !string.endsWith(this.accept_domains[n])) && (this.accept_domains[n].charAt(0) == '.' || !string.equals(this.accept_domains[n]))) continue;
            return true;
        }
        if (prompter == null) {
            return true;
        }
        return prompter.accept(cookie, this, string);
    }

    public boolean sendCookie(Cookie cookie, RoRequest roRequest) {
        return true;
    }

    void addAcceptDomain(String string) {
        if (string.indexOf(46) == -1) {
            string = string + ".local";
        }
        for (int i = 0; i < this.accept_domains.length; ++i) {
            if (string.endsWith(this.accept_domains[i])) {
                return;
            }
            if (!this.accept_domains[i].endsWith(string)) continue;
            this.accept_domains[i] = string;
            return;
        }
        this.accept_domains = Util.resizeArray(this.accept_domains, this.accept_domains.length + 1);
        this.accept_domains[this.accept_domains.length - 1] = string;
    }

    void addRejectDomain(String string) {
        if (string.indexOf(46) == -1) {
            string = string + ".local";
        }
        for (int i = 0; i < this.reject_domains.length; ++i) {
            if (string.endsWith(this.reject_domains[i])) {
                return;
            }
            if (!this.reject_domains[i].endsWith(string)) continue;
            this.reject_domains[i] = string;
            return;
        }
        this.reject_domains = Util.resizeArray(this.reject_domains, this.reject_domains.length + 1);
        this.reject_domains[this.reject_domains.length - 1] = string;
    }
}

