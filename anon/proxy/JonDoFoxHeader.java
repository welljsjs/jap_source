/*
 * Decompiled with CFR 0.150.
 */
package anon.proxy;

import anon.proxy.AbstractJonDoFoxHeaders;
import anon.proxy.AnonProxyRequest;
import anon.proxy.HTTPConnectionEvent;
import anon.proxy.HTTPProxyCallback;
import java.util.StringTokenizer;

public final class JonDoFoxHeader
extends AbstractJonDoFoxHeaders {
    public JonDoFoxHeader(int n) {
        super(n);
    }

    public void handleRequest(HTTPConnectionEvent hTTPConnectionEvent) {
    }

    public void downstreamContentBytesReceived(HTTPConnectionEvent hTTPConnectionEvent) {
    }

    public void requestHeadersReceived(HTTPConnectionEvent hTTPConnectionEvent) {
        if (hTTPConnectionEvent == null) {
            return;
        }
        HTTPProxyCallback.HTTPConnectionHeader hTTPConnectionHeader = hTTPConnectionEvent.getConnectionHeader();
        if (hTTPConnectionHeader != null) {
            String[] arrstring;
            String[] arrstring2;
            String string;
            if (hTTPConnectionHeader.getRequestLine().startsWith("CONNECT")) {
                return;
            }
            if (this.checkJonDoFox(hTTPConnectionEvent)) {
                return;
            }
            String string2 = hTTPConnectionHeader.parseDomain(true);
            if (!(string2 == null || (string = (arrstring2 = hTTPConnectionHeader.getRequestHeader("Referer")) == null || arrstring2.length != 1 ? null : HTTPProxyCallback.parseDomain(arrstring2[0], true)) != null && string.equals(string2))) {
                hTTPConnectionHeader.removeRequestHeader("Referer");
            }
            if ((arrstring = hTTPConnectionHeader.getRequestHeader("Host")) != null && arrstring.length > 0) {
                hTTPConnectionHeader.replaceRequestHeader("Host", arrstring[0]);
            }
            hTTPConnectionHeader.replaceRequestHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.1; rv:17.0) Gecko/17.0 Firefox/17.0");
            if (!hTTPConnectionEvent.getConnectionHeader().getRequestLine().startsWith("CONNECT")) {
                hTTPConnectionHeader.replaceRequestHeader("Accept", "text/html,application/xml,*/*");
                hTTPConnectionHeader.replaceRequestHeader("Accept-Language", "en-us");
                String[] arrstring3 = hTTPConnectionHeader.getRequestHeader("Accept-Encoding");
                hTTPConnectionEvent.getAnonRequest().setInternalEncodingRequired(JonDoFoxHeader.detectInternaEncodingRequired(arrstring3));
                hTTPConnectionEvent.getAnonRequest().setContentEncodings(null);
                hTTPConnectionHeader.replaceRequestHeader("Accept-Encoding", "gzip, deflate");
                hTTPConnectionHeader.removeRequestHeader("Accept-Charset");
                arrstring = hTTPConnectionHeader.getRequestHeader("Referer");
                if (arrstring != null && arrstring.length > 0) {
                    hTTPConnectionHeader.replaceRequestHeader("Referer", arrstring[0]);
                }
                hTTPConnectionHeader.resetRequestHeader("Connection");
                hTTPConnectionHeader.replaceRequestHeader("DNT", "1");
                hTTPConnectionHeader.resetRequestHeader("Proxy-Connection");
                hTTPConnectionHeader.resetRequestHeader("Keep-Alive");
                hTTPConnectionHeader.removeRequestHeader("UA-CPU");
                hTTPConnectionHeader.removeRequestHeader("Pragma");
            }
        }
    }

    public void responseHeadersReceived(HTTPConnectionEvent hTTPConnectionEvent) {
        AnonProxyRequest anonProxyRequest = hTTPConnectionEvent.getAnonRequest();
        HTTPProxyCallback.HTTPConnectionHeader hTTPConnectionHeader = hTTPConnectionEvent.getConnectionHeader();
        if (anonProxyRequest.isInternalEncodingRequired()) {
            String[] arrstring = hTTPConnectionHeader.getResponseHeader("Content-Encoding");
            if (arrstring != null) {
                anonProxyRequest.setContentEncodings(arrstring);
                hTTPConnectionHeader.removeResponseHeader("Content-Encoding");
                hTTPConnectionHeader.removeResponseHeader("Content-Length");
            } else {
                anonProxyRequest.setInternalEncodingRequired(false);
            }
        }
    }

    private static boolean detectInternaEncodingRequired(String[] arrstring) {
        boolean bl = false;
        boolean bl2 = false;
        if (arrstring != null) {
            StringTokenizer stringTokenizer = null;
            for (int i = 0; i < arrstring.length; ++i) {
                stringTokenizer = new StringTokenizer(arrstring[i], ",");
                String string = null;
                while (stringTokenizer.hasMoreTokens()) {
                    string = stringTokenizer.nextToken().trim();
                    if (!bl) {
                        bl = string.equals("gzip");
                    }
                    if (bl2) continue;
                    bl2 = string.trim().equals("deflate");
                }
            }
        }
        return !bl || !bl2;
    }

    public void upstreamContentBytesReceived(HTTPConnectionEvent hTTPConnectionEvent) {
    }
}

