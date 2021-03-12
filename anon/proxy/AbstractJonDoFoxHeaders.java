/*
 * Decompiled with CFR 0.150.
 */
package anon.proxy;

import anon.proxy.AbstractHTTPConnectionListener;
import anon.proxy.HTTPConnectionEvent;
import anon.proxy.HTTPProxyCallback;

public abstract class AbstractJonDoFoxHeaders
extends AbstractHTTPConnectionListener {
    public static final String HTTP_ENCODING_GZIP = "gzip";
    public static final String HTTP_ENCODING_DEFLATE = "deflate";
    public static final String USER_AGENT_JONDOFOX = "Mozilla/5.0 (Windows NT 6.1; rv:17.0) Gecko/17.0 Firefox/17.0";
    public static final String USER_AGENT_JONDOFOX_OLD = "Mozilla/5.0 (Windows NT 6.1; rv:10.0) Gecko/20100101 Firefox/10.0";
    public static final String USER_AGENT_TORBUTTON = "Mozilla/5.0 (Windows; U; Windows NT 6.1; en-US; rv:1.9.2.3) Gecko/20100401 Firefox/3.6.3";
    public static final String USER_AGENT_TORBUTTON_OLD = "Mozilla/5.0 (Windows; U; Windows NT 5.1; en-US; rv:1.9.0.7) Gecko/2009021910 Firefox/3.0.7";
    public static final String JONDOFOX_LANGUAGE_NEW = "en-us";
    public static final String JONDOFOX_CHARSET = "utf-8,*";
    public static final String JONDOFOX_CONTENT_TYPES = "text/html,application/xml,*/*";
    public static final String JONDOFOX_ENCODING = "gzip, deflate";
    public static final String HTTP_DO_NOT_TRACK = "DNT";

    public AbstractJonDoFoxHeaders(int n) {
        super(n);
    }

    protected boolean checkJonDoFox(HTTPConnectionEvent hTTPConnectionEvent) {
        HTTPProxyCallback.HTTPConnectionHeader hTTPConnectionHeader = hTTPConnectionEvent.getConnectionHeader();
        int n = 2;
        boolean bl = false;
        String[] arrstring = hTTPConnectionHeader.getRequestHeader("Keep-Alive");
        if (arrstring != null && arrstring.length >= 1) {
            bl = true;
        }
        if ((arrstring = hTTPConnectionHeader.getRequestHeader("Proxy-Connection")) != null && arrstring.length >= 1 && arrstring[0].toLowerCase().equals("Keep-Alive")) {
            bl = true;
        }
        if (hTTPConnectionEvent.getAnonRequest().isProxyKeepAliveEnabled()) {
            bl = true;
        }
        if (bl) {
            --n;
        }
        if ((arrstring = hTTPConnectionHeader.getRequestHeader("User-Agent")) == null || arrstring.length != 1 || !arrstring[0].equals(USER_AGENT_JONDOFOX) && !arrstring[0].equals(USER_AGENT_JONDOFOX_OLD)) {
            --n;
        }
        if ((arrstring = hTTPConnectionHeader.getRequestHeader("Accept-Language")) == null || arrstring.length != 1 || !arrstring[0].equals(JONDOFOX_LANGUAGE_NEW)) {
            --n;
        }
        if ((arrstring = hTTPConnectionHeader.getRequestHeader("Accept-Encoding")) == null || arrstring.length != 1 || !arrstring[0].equals(JONDOFOX_ENCODING)) {
            --n;
        }
        if ((arrstring = hTTPConnectionHeader.getRequestHeader("Accept-Charset")) != null && arrstring.length >= 1) {
            --n;
        }
        return n >= 0;
    }
}

