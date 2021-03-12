/*
 * Decompiled with CFR 0.150.
 */
package anon.proxy;

import anon.infoservice.IBrowserIdentification;
import anon.infoservice.InfoServiceDBEntry;
import anon.proxy.AbstractJonDoFoxHeaders;
import anon.proxy.HTTPConnectionEvent;
import anon.proxy.HTTPProxyCallback;

public class BrowserIdentification
extends AbstractJonDoFoxHeaders
implements IBrowserIdentification {
    private static final int BROWSER_RECOGNITION_UNINITIALISED = -1;
    private static final int BROWSER_UNKNOWN = 0;
    private static final int BROWSER_TORBUTTON = 1;
    private static final int BROWSER_JONDOFOX = 2;
    private static final int BROWSER_INTERNET_EXPLORER = 3;
    private static final int BROWSER_FIREFOX = 4;
    private static final int BROWSER_OPERA = 5;
    private static final int BROWSER_SAFARI = 6;
    private static final int BROWSER_KONQUEROR = 7;
    private static final int BROWSER_CHROME = 8;
    private static final long[] BROWSER_OCCURENCE = new long[9];
    private static final String[] BROWSER_NAME = new String[]{"other", "Tor", "JonDoFox", "Internet Explorer", "Firefox", "Opera", "Safari", "Konqueror", "Chrome"};
    private static boolean ms_bTestShown = false;
    private static boolean ms_bDetectedBrowser = false;

    public BrowserIdentification(int n) {
        super(n);
        InfoServiceDBEntry.setBrowserIdentification(this);
    }

    public boolean isBlockable() {
        return false;
    }

    public int getMostFrequentBrowser() {
        int n = -1;
        long l = 0L;
        for (int i = 0; i < BROWSER_OCCURENCE.length; ++i) {
            if (BROWSER_OCCURENCE[i] <= l) continue;
            l = BROWSER_OCCURENCE[i];
            n = i;
        }
        return n;
    }

    public String getBrowserName() {
        int n = this.getMostFrequentBrowser();
        if (n == -1) {
            return null;
        }
        return BROWSER_NAME[n];
    }

    public boolean isJonDoFoxDetected() {
        return BROWSER_OCCURENCE[2] > 0L;
    }

    public void responseHeadersReceived(HTTPConnectionEvent hTTPConnectionEvent) {
    }

    public void downstreamContentBytesReceived(HTTPConnectionEvent hTTPConnectionEvent) {
    }

    public void upstreamContentBytesReceived(HTTPConnectionEvent hTTPConnectionEvent) {
    }

    public void requestHeadersReceived(HTTPConnectionEvent hTTPConnectionEvent) {
        if (hTTPConnectionEvent == null) {
            return;
        }
        HTTPProxyCallback.HTTPConnectionHeader hTTPConnectionHeader = hTTPConnectionEvent.getConnectionHeader();
        if (hTTPConnectionHeader != null) {
            if (hTTPConnectionHeader.getRequestLine().startsWith("CONNECT")) {
                return;
            }
            this.countBrowserType(hTTPConnectionEvent, this.checkJonDoFox(hTTPConnectionEvent));
        }
    }

    private void countBrowserType(HTTPConnectionEvent hTTPConnectionEvent, boolean bl) {
        if (bl) {
            BrowserIdentification.BROWSER_OCCURENCE[2] = 1L;
            ms_bDetectedBrowser = true;
            return;
        }
        if (ms_bDetectedBrowser && !ms_bTestShown && HTTPProxyCallback.isAnonymityTestDomain(hTTPConnectionEvent.getConnectionHeader())) {
            ms_bTestShown = true;
        }
        if (!ms_bTestShown && BROWSER_OCCURENCE[2] == 0L && !hTTPConnectionEvent.getConnectionHeader().getRequestLine().startsWith("CONNECT")) {
            int n = -1;
            String[] arrstring = hTTPConnectionEvent.getConnectionHeader().getRequestHeader("User-Agent");
            if (arrstring != null && arrstring.length > 0) {
                String string = arrstring[0].toLowerCase();
                if (string.indexOf("firefox") >= 0) {
                    n = arrstring[0].equals("Mozilla/5.0 (Windows; U; Windows NT 6.1; en-US; rv:1.9.2.3) Gecko/20100401 Firefox/3.6.3") || arrstring[0].equals("Mozilla/5.0 (Windows; U; Windows NT 5.1; en-US; rv:1.9.0.7) Gecko/2009021910 Firefox/3.0.7") ? 1 : 4;
                } else if (string.indexOf("msie") >= 0) {
                    n = 3;
                } else if (string.indexOf("opera") >= 0) {
                    n = 5;
                } else if (string.indexOf("chrome") >= 0) {
                    n = 8;
                } else if (string.indexOf("konqueror") >= 0) {
                    n = 7;
                } else if (string.indexOf("safari") >= 0) {
                    n = 6;
                } else if (string.indexOf("httpclient") < 0) {
                    n = 0;
                }
                if (n > -1) {
                    ms_bDetectedBrowser = true;
                    int n2 = n;
                    BROWSER_OCCURENCE[n2] = BROWSER_OCCURENCE[n2] + 1L;
                }
            }
            if (n > 2 && BROWSER_OCCURENCE[n] > 40L) {
                hTTPConnectionEvent.getAnonRequest().showBrowserWarning(true);
            }
        }
    }
}

