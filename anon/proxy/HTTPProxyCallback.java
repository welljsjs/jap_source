/*
 * Decompiled with CFR 0.150.
 */
package anon.proxy;

import anon.crypto.MyRandom;
import anon.infoservice.Database;
import anon.infoservice.ServiceOperator;
import anon.pay.PayAccountsFile;
import anon.proxy.AbstractHTTPConnectionListener;
import anon.proxy.AnonProxyRequest;
import anon.proxy.HTTPConnectionEvent;
import anon.proxy.HTTPHeaderParseException;
import anon.proxy.ProxyCallback;
import anon.proxy.ProxyCallbackBuffer;
import anon.proxy.ProxyCallbackNotProcessableException;
import anon.util.JAPMessages;
import anon.util.URLCoder;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.StringTokenizer;
import java.util.Vector;
import logging.LogHolder;
import logging.LogType;

public class HTTPProxyCallback
implements ProxyCallback {
    private static final boolean FIRE_EVENT = true;
    private static final MyRandom RANDOM = new MyRandom();
    public static final String MSG_URL_ANONYMITY_TEST = (class$anon$proxy$HTTPProxyCallback == null ? (class$anon$proxy$HTTPProxyCallback = HTTPProxyCallback.class$("anon.proxy.HTTPProxyCallback")) : class$anon$proxy$HTTPProxyCallback).getName() + ".urlAnonymityTest";
    static final int MESSAGE_TYPE_REQUEST = 0;
    static final int MESSAGE_TYPE_RESPONSE = 1;
    static final String CRLF = "\r\n";
    static final String HTTP_HEADER_END = "\r\n\r\n";
    static final byte[] HTTP_HEADER_END_BYTES = new byte[]{13, 10, 13, 10};
    static final String HTTP_HEADER_DELIM = ": ";
    public static final String HTTP_START_LINE_KEY = "start-line";
    public static final String HTTP_VERSION_PREFIX = "HTTP/";
    static final byte[] HTTP_VERSION_PREFIX_BYTES = "HTTP/".getBytes();
    static final String[] HTTP_REQUEST_METHODS = new String[]{"GET", "POST", "CONNECT", "HEAD", "PUT", "OPTIONS", "DELETE", "TRACE"};
    static final byte[][] HTTP_REQUEST_METHODS_BYTES = new byte[HTTP_REQUEST_METHODS.length][];
    static final String MSG_INVALID_LINETERM_REQUEST = "httpFilter.invalidlineterm.request";
    static final String MSG_INVALID_LINETERM_RESPONSE = "httpFilter.invalidlineterm.response";
    public static final String HTTP_CONTENT_LENGTH = "Content-Length";
    public static final String HTTP_CONTENT_ENCODING = "Content-Encoding";
    public static final String HTTP_CONTENT_TYPE = "Content-Type";
    public static final String HTTP_HOST = "Host";
    public static final String HTTP_USER_AGENT = "User-Agent";
    public static final String HTTP_ACCEPT = "Accept";
    public static final String HTTP_LOCATION = "Location";
    public static final String HTTP_ACCEPT_LANGUAGE = "Accept-Language";
    public static final String HTTP_ACCEPT_ENCODING = "Accept-Encoding";
    public static final String HTTP_ACCEPT_CHARSET = "Accept-Charset";
    public static final String HTTP_KEEP_ALIVE = "Keep-Alive";
    public static final String HTTP_ATTR_KEEP_ALIVE = "keep-alive";
    public static final String HTTP_ATTR_CLOSE = "close";
    public static final String HTTP_PROXY_CONNECTION = "Proxy-Connection";
    public static final String HTTP_CONNECTION = "Connection";
    public static final String HTTP_REFERER = "Referer";
    public static final String HTTP_CACHE_CONTROL = "Cache-Control";
    public static final String HTTP_COOKIE = "Cookie";
    public static final String HTTP_PRAGMA = "Pragma";
    public static final String HTTP_RANGE = "Range";
    public static final String HTTP_IE_UA_CPU = "UA-CPU";
    public static final int REDIRECT_ANONYMITY_TEST = 0;
    public static final int REDIRECT_SQUID_REMINDER = 1;
    private static long ms_lCountHTML;
    private static long ms_lTotalCountHTML;
    private static long ms_lNextRedirect;
    private static int ms_iRedirectProbability;
    private static boolean ms_bCountRedirect;
    private static final Object SYNC_COUNTER;
    private Hashtable m_connectionHTTPHeaders = new Hashtable();
    private Hashtable m_unfinishedRequests = new Hashtable();
    private Hashtable m_unfinishedResponses = new Hashtable();
    private Hashtable m_downstreamBytes = new Hashtable();
    private Hashtable m_upstreamBytes = new Hashtable();
    private Vector m_httpConnectionListeners = new Vector();
    private boolean m_bBlockHTTPListeners = false;
    private static final IHTTPHelper UPSTREAM_HELPER;
    private static final IHTTPHelper DOWNSTREAM_HELPER;
    static /* synthetic */ Class class$anon$proxy$HTTPProxyCallback;
    static /* synthetic */ Class class$anon$infoservice$ServiceOperator;

    public int handleUpstreamChunk(AnonProxyRequest anonProxyRequest, ProxyCallbackBuffer proxyCallbackBuffer) throws ProxyCallbackNotProcessableException {
        return this.handleStreamChunk(anonProxyRequest, proxyCallbackBuffer, 0, UPSTREAM_HELPER);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public int handleDownstreamChunk(AnonProxyRequest anonProxyRequest, ProxyCallbackBuffer proxyCallbackBuffer) throws ProxyCallbackNotProcessableException {
        boolean bl = false;
        HTTPProxyCallback hTTPProxyCallback = this;
        synchronized (hTTPProxyCallback) {
            HTTPConnectionHeader hTTPConnectionHeader = (HTTPConnectionHeader)this.m_connectionHTTPHeaders.get(anonProxyRequest);
            bl = hTTPConnectionHeader == null ? true : !hTTPConnectionHeader.isResponseExpected();
        }
        return bl ? 2 : this.handleStreamChunk(anonProxyRequest, proxyCallbackBuffer, 1, DOWNSTREAM_HELPER);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private int handleStreamChunk(AnonProxyRequest anonProxyRequest, ProxyCallbackBuffer proxyCallbackBuffer, int n, IHTTPHelper iHTTPHelper) throws ProxyCallbackNotProcessableException {
        int n2 = proxyCallbackBuffer.getModificationStartOffset();
        int n3 = proxyCallbackBuffer.getModificationEndOffset();
        int n4 = n3 - n2 + 1;
        byte[] arrby = proxyCallbackBuffer.getChunk();
        if (anonProxyRequest == null) {
            throw new NullPointerException("AnonProxyRequest must not be null!");
        }
        int n5 = HTTP_HEADER_END.length();
        int n6 = -1;
        byte[] arrby2 = null;
        Hashtable hashtable = n == 0 ? this.m_unfinishedRequests : this.m_unfinishedResponses;
        String string = null;
        Hashtable hashtable2 = n == 0 ? this.m_upstreamBytes : this.m_downstreamBytes;
        HTTPProxyCallback hTTPProxyCallback = this;
        synchronized (hTTPProxyCallback) {
            string = (String)hashtable.get(anonProxyRequest);
        }
        if (string != null) {
            arrby2 = string.length() > HTTP_HEADER_END_BYTES.length - 1 ? string.substring(string.length() - (HTTP_HEADER_END_BYTES.length - 1)).getBytes() : string.getBytes();
        }
        int n7 = (n6 = HTTPProxyCallback.indexOfHTTPHeaderEnd(arrby2, arrby, n2, n3)) == -1 ? n4 : n6 - n2;
        int n8 = n4;
        String string2 = null;
        if (string != null || this.hasAlignedHTTPStartLine(arrby, n2, n7, n)) {
            n8 = n4 - n7;
            string2 = (string == null ? "" : string) + new String(arrby, n2, n7);
            boolean bl = false;
            byte[] arrby3 = null;
            HTTPConnectionHeader hTTPConnectionHeader = null;
            boolean bl2 = false;
            try {
                bl = this.extractHeaderParts(anonProxyRequest, string2, n);
                if (!bl) {
                    return 1;
                }
                HTTPProxyCallback hTTPProxyCallback2 = this;
                synchronized (hTTPProxyCallback2) {
                    hTTPConnectionHeader = (HTTPConnectionHeader)this.m_connectionHTTPHeaders.get(anonProxyRequest);
                    bl2 = hTTPConnectionHeader != null && hTTPConnectionHeader.getRequestLine() != null;
                }
            }
            catch (DownstreamUnhandledException downstreamUnhandledException) {
                arrby3 = string2.getBytes();
                bl2 = true;
                LogHolder.log(4, LogType.NET, "Skipped parsing of invalid response headers:\n" + string2);
            }
            if (bl2) {
                if (arrby3 == null) {
                    arrby3 = iHTTPHelper.dumpHeader(this, hTTPConnectionHeader, anonProxyRequest);
                }
                this.countContentBytes(anonProxyRequest, n8, hashtable2, true);
                int n9 = arrby3.length + n2;
                int n10 = n9 + n8;
                byte[] arrby4 = new byte[n10 + proxyCallbackBuffer.getTrailingDataLength()];
                proxyCallbackBuffer.copyLeadingData(arrby4);
                System.arraycopy(arrby3, 0, arrby4, n2, arrby3.length);
                System.arraycopy(arrby, n3 + 1 - n8, arrby4, n9, n8);
                proxyCallbackBuffer.copyTrailingData(arrby4, n10);
                proxyCallbackBuffer.setChunk(arrby4);
                proxyCallbackBuffer.setModificationStartOffset(n9);
                proxyCallbackBuffer.setModificationEndOffset(n10 - 1);
                return 2;
            }
        }
        this.countContentBytes(anonProxyRequest, n8, hashtable2, true);
        return 2;
    }

    private synchronized long countContentBytes(AnonProxyRequest anonProxyRequest, int n, Hashtable hashtable, boolean bl) {
        long l = 0L;
        Long l2 = (Long)hashtable.remove(anonProxyRequest);
        if (l2 != null) {
            l = l2;
        }
        hashtable.put(anonProxyRequest, new Long(l += (long)n));
        if (bl) {
            HTTPConnectionEvent hTTPConnectionEvent = new HTTPConnectionEvent();
            hTTPConnectionEvent.setAnonRequest(anonProxyRequest);
            hTTPConnectionEvent.setConnectionHeader((HTTPConnectionHeader)this.m_connectionHTTPHeaders.get(anonProxyRequest));
            if (hashtable == this.m_downstreamBytes) {
                hTTPConnectionEvent.setUpStreamContentBytes(this.getUpStreamContentBytes(anonProxyRequest));
                hTTPConnectionEvent.setDownStreamContentBytes(l);
                this.fireDownstreamContentBytesReceived(hTTPConnectionEvent);
            } else if (hashtable == this.m_upstreamBytes) {
                hTTPConnectionEvent.setDownStreamContentBytes(this.getDownStreamContentBytes(anonProxyRequest));
                hTTPConnectionEvent.setUpStreamContentBytes(l);
                this.fireUpstreamContentBytesReceived(hTTPConnectionEvent);
            }
        }
        return l;
    }

    public synchronized long getUpStreamContentBytes(AnonProxyRequest anonProxyRequest) {
        return this.getContentBytes(anonProxyRequest, this.m_upstreamBytes);
    }

    public synchronized long getDownStreamContentBytes(AnonProxyRequest anonProxyRequest) {
        return this.getContentBytes(anonProxyRequest, this.m_downstreamBytes);
    }

    private long getContentBytes(AnonProxyRequest anonProxyRequest, Hashtable hashtable) {
        if (hashtable == null) {
            throw new NullPointerException("Bug: No count table specified for getContentBytes");
        }
        Long l = (Long)hashtable.get(anonProxyRequest);
        return l == null ? 0L : l;
    }

    private synchronized HTTPConnectionEvent getEvent(AnonProxyRequest anonProxyRequest) {
        long l = this.getUpStreamContentBytes(anonProxyRequest);
        long l2 = this.getDownStreamContentBytes(anonProxyRequest);
        HTTPConnectionHeader hTTPConnectionHeader = (HTTPConnectionHeader)this.m_connectionHTTPHeaders.get(anonProxyRequest);
        return new HTTPConnectionEvent(hTTPConnectionHeader, l, l2, anonProxyRequest);
    }

    private synchronized boolean extractHeaderParts(AnonProxyRequest anonProxyRequest, String string, int n) throws ProxyCallbackNotProcessableException, DownstreamUnhandledException {
        String string2;
        if (anonProxyRequest == null) {
            throw new NullPointerException("AnonProxyRequest must not be null!");
        }
        HTTPConnectionHeader hTTPConnectionHeader = null;
        hTTPConnectionHeader = (HTTPConnectionHeader)this.m_connectionHTTPHeaders.get(anonProxyRequest);
        if (hTTPConnectionHeader != null) {
            if (n == 0 && hTTPConnectionHeader.isRequestFinished()) {
                hTTPConnectionHeader.clearRequest();
                this.m_upstreamBytes.remove(anonProxyRequest);
            } else if (n == 1 && hTTPConnectionHeader.isResponseFinished()) {
                hTTPConnectionHeader.clearResponse();
                this.m_downstreamBytes.remove(anonProxyRequest);
            }
        }
        if (hTTPConnectionHeader == null) {
            hTTPConnectionHeader = new HTTPConnectionHeader();
            this.m_connectionHTTPHeaders.put(anonProxyRequest, hTTPConnectionHeader);
        }
        Hashtable hashtable = n == 0 ? this.m_unfinishedRequests : this.m_unfinishedResponses;
        int n2 = string.indexOf(HTTP_HEADER_END);
        String string3 = string2 = n2 == -1 ? string : string.substring(0, n2);
        if (!HTTPProxyCallback.checkValidity(string2)) {
            String string4 = null;
            if (n == 0) {
                hTTPConnectionHeader.setRequestFinished(true);
                string4 = MSG_INVALID_LINETERM_REQUEST;
            } else if (n == 1) {
                hTTPConnectionHeader.setResponseFinished(true);
                string4 = MSG_INVALID_LINETERM_RESPONSE;
                throw new DownstreamUnhandledException();
            }
            hashtable.remove(anonProxyRequest);
            LogHolder.log(3, LogType.FILTER, "Error while parsing header: " + string2);
            throw new HTTPHeaderParseException(400, n, JAPMessages.getString(string4));
        }
        if (n2 != -1) {
            this.parseHTTPHeader(string2, hTTPConnectionHeader, n);
            if (n == 0) {
                hTTPConnectionHeader.setRequestFinished(true);
                hTTPConnectionHeader.setResponseExpected(true);
            } else if (n == 1) {
                hTTPConnectionHeader.setResponseFinished(true);
                hTTPConnectionHeader.setResponseExpected(false);
            }
            hashtable.remove(anonProxyRequest);
            return true;
        }
        hashtable.put(anonProxyRequest, string);
        return false;
    }

    public static boolean isJonDosDomain(HTTPConnectionHeader hTTPConnectionHeader) {
        if (hTTPConnectionHeader == null) {
            return false;
        }
        return HTTPProxyCallback.isJonDosDomain(hTTPConnectionHeader.parseDomain(true));
    }

    public static boolean isAnonymityTestDomain(HTTPConnectionHeader hTTPConnectionHeader) {
        if (hTTPConnectionHeader == null) {
            return false;
        }
        return HTTPProxyCallback.isAnonymityTestDomain(hTTPConnectionHeader.parseDomain(false));
    }

    protected void resetRedirect(int n, boolean bl) {
        ms_iRedirectProbability = n;
        if (ms_iRedirectProbability >= 0) {
            if (ms_lNextRedirect < 0L) {
                ms_lNextRedirect = 20L;
                if (!ms_bCountRedirect) {
                    ms_bCountRedirect = true;
                    ms_lCountHTML = 0L;
                }
            }
        } else {
            ms_bCountRedirect = bl;
            ms_lNextRedirect = -1L;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static boolean redirect(HTTPConnectionHeader hTTPConnectionHeader, int n) {
        if (n == 1 && (!ms_bCountRedirect || HTTPProxyCallback.isJonDosDomain(hTTPConnectionHeader))) {
            return false;
        }
        if (n == 0 && HTTPProxyCallback.isAnonymityTestDomain(hTTPConnectionHeader)) {
            return false;
        }
        String[] arrstring = hTTPConnectionHeader.getResponseHeader(HTTP_CONTENT_TYPE);
        boolean bl = false;
        if (arrstring != null && arrstring.length > 0 && hTTPConnectionHeader.parseStatus() == 200 && hTTPConnectionHeader.getRequestLine().startsWith("GET")) {
            for (int i = 0; i < arrstring.length; ++i) {
                if (arrstring[i].toLowerCase().indexOf("text/html") < 0) continue;
                bl = true;
                if (n != 1 || ms_lTotalCountHTML >= 100L) break;
                ++ms_lTotalCountHTML;
                break;
            }
        }
        if (bl && n == 1) {
            Object object = SYNC_COUNTER;
            synchronized (object) {
                if (ms_lNextRedirect < 0L || ms_lNextRedirect > ms_lCountHTML) {
                    bl = false;
                    ++ms_lCountHTML;
                } else if (ms_iRedirectProbability < 0) {
                    bl = false;
                } else {
                    if (ms_lNextRedirect > 0L) {
                        --ms_lNextRedirect;
                    }
                    ms_lCountHTML = 0L;
                }
                if (ms_iRedirectProbability > 0 && ms_lTotalCountHTML * 100L >= (long)(10000 / ms_iRedirectProbability) && ms_iRedirectProbability > RANDOM.nextInt(100)) {
                    bl = true;
                }
            }
        }
        if (bl) {
            String string = n == 1 ? "http://premium-" + JAPMessages.getLocale().getLanguage() : JAPMessages.getString(MSG_URL_ANONYMITY_TEST);
            hTTPConnectionHeader.removeResponseHeaders();
            hTTPConnectionHeader.replaceResponseHeader(HTTP_START_LINE_KEY, "HTTP/1.0 302 Moved Temporarily");
            hTTPConnectionHeader.replaceResponseHeader(HTTP_LOCATION, string);
            hTTPConnectionHeader.replaceResponseHeader(HTTP_CONTENT_TYPE, "text/html");
            hTTPConnectionHeader.replaceResponseHeader(HTTP_CONNECTION, HTTP_ATTR_CLOSE);
        }
        return bl;
    }

    private static boolean isJonDosDomain(String string) {
        if (string == null) {
            return false;
        }
        return string.equals("anonymous-proxy-servers.net") || string.equals("anonym-surfen.de") || string.equals("anonym-surfen.de") || string.equals("jondopay.de") || HTTPProxyCallback.isAnonymityTestDomain(string) || HTTPProxyCallback.isOperatorDomain(string);
    }

    private static boolean isOperatorDomain(String string) {
        Enumeration enumeration = Database.getInstance(class$anon$infoservice$ServiceOperator == null ? (class$anon$infoservice$ServiceOperator = HTTPProxyCallback.class$("anon.infoservice.ServiceOperator")) : class$anon$infoservice$ServiceOperator).getEntrySnapshotAsEnumeration();
        while (enumeration.hasMoreElements()) {
            ServiceOperator serviceOperator = (ServiceOperator)enumeration.nextElement();
            if (serviceOperator.getUrl() == null || !string.equals(HTTPProxyCallback.parseDomain(serviceOperator.getUrl(), true))) continue;
            return true;
        }
        return false;
    }

    private static boolean isAnonymityTestDomain(String string) {
        if (string == null) {
            return false;
        }
        return string.equals("ip-check.info") || string.equals("ipcheck.info") || string.equals("ip-check.org") || string.equals("what-is-my-ip-address.anonymous-proxy-servers.net") || string.equals("what-is-my-ip-address.anonymous-proxy-servers.eu");
    }

    public static boolean checkValidity(String string) {
        boolean bl;
        int n = -1;
        int n2 = -1;
        boolean bl2 = false;
        boolean bl3 = false;
        boolean bl4 = false;
        boolean bl5 = false;
        boolean bl6 = bl = Math.max(n + 1, n2 + 1) >= string.length();
        while (!bl) {
            n = string.indexOf(13, n + 1);
            n2 = string.indexOf(10, n2 + 1);
            boolean bl7 = bl5 = n == -1 && n2 == -1;
            if (bl5) break;
            boolean bl8 = bl3 = n2 != -1 && (n == -1 || n != n2 - 1);
            if (bl3) break;
            bl2 = n != -1 && (n2 == -1 || n != n2 - 1);
            boolean bl9 = bl4 = n == string.length() - 1;
            if (bl4) break;
            bl = Math.max(n + 1, n2 + 1) >= string.length();
        }
        return !bl3 && (!bl2 || bl4);
    }

    private boolean hasAlignedHTTPStartLine(String string, int n) {
        return n == 0 ? this.isRequest(string) : this.isResponse(string);
    }

    private boolean hasAlignedHTTPStartLine(byte[] arrby, int n, int n2, int n3) {
        return n3 == 0 ? this.isRequest(arrby, n, n2) : this.isResponse(arrby, n, n2);
    }

    private boolean isRequest(String string) {
        for (int i = 0; i < HTTP_REQUEST_METHODS.length; ++i) {
            if (!string.startsWith(HTTP_REQUEST_METHODS[i])) continue;
            return true;
        }
        return false;
    }

    private boolean isRequest(byte[] arrby, int n, int n2) {
        boolean bl = true;
        for (int i = 0; i < HTTP_REQUEST_METHODS_BYTES.length; ++i) {
            int n3 = Math.min(n2, HTTP_REQUEST_METHODS_BYTES[i].length);
            for (int j = n; j < n3; ++j) {
                if (arrby[j] == HTTP_REQUEST_METHODS_BYTES[i][j]) continue;
                bl = false;
                break;
            }
            if (bl) {
                return true;
            }
            bl = true;
        }
        return false;
    }

    private boolean isResponse(String string) {
        return string.startsWith(HTTP_VERSION_PREFIX);
    }

    private boolean isResponse(byte[] arrby, int n, int n2) {
        int n3 = Math.min(n2, HTTP_VERSION_PREFIX_BYTES.length);
        for (int i = n; i < n3; ++i) {
            if (arrby[i] == HTTP_VERSION_PREFIX_BYTES[i]) continue;
            return false;
        }
        return true;
    }

    public static int indexOfHTTPHeaderEnd(byte[] arrby, int n, int n2) {
        boolean bl = false;
        for (int i = n; i <= n2 - (HTTP_HEADER_END_BYTES.length - 1); ++i) {
            bl = true;
            for (int j = 0; j < HTTP_HEADER_END_BYTES.length; ++j) {
                if (arrby[i + j] == HTTP_HEADER_END_BYTES[j]) continue;
                bl = false;
                break;
            }
            if (!bl) continue;
            return i + HTTP_HEADER_END_BYTES.length;
        }
        return -1;
    }

    public static int indexOfHTTPHeaderEnd(byte[] arrby, byte[] arrby2, int n, int n2) {
        if (arrby != null) {
            int n3;
            boolean bl = false;
            int n4 = arrby.length;
            int n5 = n3 = n4 >= HTTP_HEADER_END_BYTES.length - 1 ? n4 - (HTTP_HEADER_END_BYTES.length - 1) : 0;
            if (n2 + 1 + n4 < HTTP_HEADER_END_BYTES.length) {
                return -1;
            }
            for (int i = n3; i < n4; ++i) {
                int n6;
                int n7 = HTTP_HEADER_END_BYTES.length - (n4 - i);
                if (n7 > n2 - n + 1) {
                    return -1;
                }
                bl = true;
                int n8 = 0;
                while (i + n8 < n4) {
                    if (arrby[i + n8] != HTTP_HEADER_END_BYTES[n8]) {
                        bl = false;
                        break;
                    }
                    ++n8;
                }
                if (!bl) continue;
                for (n6 = n; n6 < n7 || n8 < HTTP_HEADER_END_BYTES.length; ++n6) {
                    if (arrby2[n6] == HTTP_HEADER_END_BYTES[n8++]) continue;
                    bl = false;
                    break;
                }
                if (!bl) continue;
                return n6;
            }
        }
        return HTTPProxyCallback.indexOfHTTPHeaderEnd(arrby2, n, n2);
    }

    private synchronized void parseHTTPHeader(String string, HTTPConnectionHeader hTTPConnectionHeader, int n) {
        StringTokenizer stringTokenizer = new StringTokenizer(string, CRLF);
        if (stringTokenizer.countTokens() == 0) {
            return;
        }
        String string2 = null;
        String string3 = null;
        String string4 = null;
        if (n == 0) {
            hTTPConnectionHeader.setRequestHeader(HTTP_START_LINE_KEY, stringTokenizer.nextToken());
        } else if (n == 1) {
            hTTPConnectionHeader.setResponseHeader(HTTP_START_LINE_KEY, stringTokenizer.nextToken());
        }
        while (stringTokenizer.hasMoreTokens()) {
            string2 = stringTokenizer.nextToken();
            int n2 = string2.indexOf(HTTP_HEADER_DELIM);
            if (n2 == -1) {
                n2 = string2.indexOf("\n\n");
            }
            if (n2 == -1) continue;
            string3 = string2.substring(0, n2).trim();
            if (n2 + 1 < string2.length()) {
                string4 = string2.substring(n2 + 1).trim();
            }
            if (string3 == null || string4 == null) continue;
            if (n == 0) {
                hTTPConnectionHeader.setRequestHeader(string3, string4);
                continue;
            }
            if (n != 1) continue;
            hTTPConnectionHeader.setResponseHeader(string3, string4);
        }
    }

    public synchronized void addHTTPConnectionListener(AbstractHTTPConnectionListener abstractHTTPConnectionListener) {
        if (!this.m_httpConnectionListeners.contains(abstractHTTPConnectionListener)) {
            int n;
            for (n = 0; n < this.m_httpConnectionListeners.size() && ((AbstractHTTPConnectionListener)this.m_httpConnectionListeners.elementAt(n)).getPriority() < abstractHTTPConnectionListener.getPriority(); ++n) {
            }
            this.m_httpConnectionListeners.insertElementAt(abstractHTTPConnectionListener, n);
        }
    }

    public synchronized void removeHTTPConnectionListener(AbstractHTTPConnectionListener abstractHTTPConnectionListener) {
        this.m_httpConnectionListeners.removeElement(abstractHTTPConnectionListener);
    }

    public synchronized void removeAllHTTPConnectionListeners() {
        this.m_httpConnectionListeners.removeAllElements();
    }

    public synchronized void fireRequestHeadersReceived(HTTPConnectionEvent hTTPConnectionEvent) {
        Object object;
        Object object2 = this.m_httpConnectionListeners.elements();
        while (object2.hasMoreElements()) {
            object = (AbstractHTTPConnectionListener)object2.nextElement();
            if (object == null || this.m_bBlockHTTPListeners && ((AbstractHTTPConnectionListener)object).isBlockable()) continue;
            ((AbstractHTTPConnectionListener)object).requestHeadersReceived(hTTPConnectionEvent);
        }
        object2 = hTTPConnectionEvent.getConnectionHeader();
        if (object2 != null) {
            hTTPConnectionEvent.getAnonRequest().setHttpParsed(((HTTPConnectionHeader)object2).parseDomain(false));
            if (ms_lNextRedirect >= 0L && (object = ((HTTPConnectionHeader)object2).parseURL()) != null && ((String)object).startsWith("http://premium-") && ((String)object).indexOf(".") < 0) {
                if (PayAccountsFile.getInstance().isNewUser()) {
                    ((HTTPConnectionHeader)object2).setRequestHeader("X-JonDonym-Premium", "false");
                } else {
                    ((HTTPConnectionHeader)object2).setRequestHeader("X-JonDonym-Premium", "true");
                }
                try {
                    if (ms_lNextRedirect == 0L) {
                        ((HTTPConnectionHeader)object2).replaceRequestHeader("X-JonDonym-Redirect", "permanent");
                    } else {
                        ((HTTPConnectionHeader)object2).replaceRequestHeader("X-JonDonym-Redirect", URLCoder.encode(((HTTPConnectionHeader)object2).parseURL()));
                    }
                }
                catch (Exception exception) {
                    LogHolder.log(3, LogType.NET, exception);
                }
            }
        }
    }

    public synchronized void fireResponseHeadersReceived(HTTPConnectionEvent hTTPConnectionEvent) {
        if (hTTPConnectionEvent != null) {
            HTTPProxyCallback.redirect(hTTPConnectionEvent.getConnectionHeader(), 1);
        }
        Enumeration enumeration = this.m_httpConnectionListeners.elements();
        while (enumeration.hasMoreElements()) {
            AbstractHTTPConnectionListener abstractHTTPConnectionListener = (AbstractHTTPConnectionListener)enumeration.nextElement();
            if (abstractHTTPConnectionListener == null || this.m_bBlockHTTPListeners && abstractHTTPConnectionListener.isBlockable()) continue;
            abstractHTTPConnectionListener.responseHeadersReceived(hTTPConnectionEvent);
        }
    }

    protected void blockHTTPListeners(boolean bl) {
        this.m_bBlockHTTPListeners = bl;
    }

    public synchronized void fireDownstreamContentBytesReceived(HTTPConnectionEvent hTTPConnectionEvent) {
        Enumeration enumeration = this.m_httpConnectionListeners.elements();
        while (enumeration.hasMoreElements()) {
            AbstractHTTPConnectionListener abstractHTTPConnectionListener = (AbstractHTTPConnectionListener)enumeration.nextElement();
            if (abstractHTTPConnectionListener == null || this.m_bBlockHTTPListeners && abstractHTTPConnectionListener.isBlockable()) continue;
            abstractHTTPConnectionListener.downstreamContentBytesReceived(hTTPConnectionEvent);
        }
    }

    public synchronized void fireUpstreamContentBytesReceived(HTTPConnectionEvent hTTPConnectionEvent) {
        Enumeration enumeration = this.m_httpConnectionListeners.elements();
        while (enumeration.hasMoreElements()) {
            AbstractHTTPConnectionListener abstractHTTPConnectionListener = (AbstractHTTPConnectionListener)enumeration.nextElement();
            if (abstractHTTPConnectionListener == null || this.m_bBlockHTTPListeners && abstractHTTPConnectionListener.isBlockable()) continue;
            abstractHTTPConnectionListener.upstreamContentBytesReceived(hTTPConnectionEvent);
        }
    }

    public synchronized void closeRequest(AnonProxyRequest anonProxyRequest) {
        HTTPConnectionHeader hTTPConnectionHeader = (HTTPConnectionHeader)this.m_connectionHTTPHeaders.get(anonProxyRequest);
        if (hTTPConnectionHeader != null) {
            hTTPConnectionHeader.clearRequest();
            hTTPConnectionHeader.clearResponse();
            this.m_upstreamBytes.remove(anonProxyRequest);
            this.m_downstreamBytes.remove(anonProxyRequest);
            this.m_connectionHTTPHeaders.remove(anonProxyRequest);
        }
    }

    public static String parseDomain(String string, boolean bl) {
        String string2 = null;
        if (string != null) {
            int n = string.indexOf("://");
            string2 = n >= 0 ? string.substring(n + 3, string.length()) : string;
            n = string2.indexOf(" ");
            if (n >= 0) {
                string2 = string2.substring(0, n);
            }
            if (string2.endsWith("/")) {
                string2 = string2.substring(0, string2.length() - 1);
            }
            if ((n = string2.lastIndexOf(":")) >= 0 && (string2.length() <= n + 1 || string2.charAt(n + 1) != '/')) {
                string2 = string2.substring(0, n);
            }
            if ((n = string2.indexOf("/")) >= 0) {
                string2 = string2.substring(0, n);
            }
            while (bl && (n = string2.indexOf(".")) >= 0 && n < string2.lastIndexOf(".")) {
                string2 = string2.substring(n + 1, string2.length());
            }
        }
        if (string2 != null && string2.trim().length() == 0) {
            string2 = null;
        }
        return string2;
    }

    static /* synthetic */ Class class$(String string) {
        try {
            return Class.forName(string);
        }
        catch (ClassNotFoundException classNotFoundException) {
            throw new NoClassDefFoundError(classNotFoundException.getMessage());
        }
    }

    static {
        for (int i = 0; i < HTTP_REQUEST_METHODS.length; ++i) {
            byte[] arrby = HTTP_REQUEST_METHODS[i].getBytes();
            HTTPProxyCallback.HTTP_REQUEST_METHODS_BYTES[i] = arrby;
        }
        ms_lCountHTML = 0L;
        ms_lTotalCountHTML = 0L;
        ms_lNextRedirect = -1L;
        ms_iRedirectProbability = -1;
        ms_bCountRedirect = false;
        SYNC_COUNTER = new Object();
        UPSTREAM_HELPER = new IHTTPHelper(){

            public byte[] dumpHeader(HTTPProxyCallback hTTPProxyCallback, HTTPConnectionHeader hTTPConnectionHeader, AnonProxyRequest anonProxyRequest) {
                hTTPProxyCallback.fireRequestHeadersReceived(hTTPProxyCallback.getEvent(anonProxyRequest));
                return hTTPConnectionHeader.dumpRequestHeaders();
            }
        };
        DOWNSTREAM_HELPER = new IHTTPHelper(){

            public byte[] dumpHeader(HTTPProxyCallback hTTPProxyCallback, HTTPConnectionHeader hTTPConnectionHeader, AnonProxyRequest anonProxyRequest) {
                hTTPProxyCallback.fireResponseHeadersReceived(hTTPProxyCallback.getEvent(anonProxyRequest));
                return hTTPConnectionHeader.dumpResponseHeaders();
            }
        };
    }

    public class DownstreamUnhandledException
    extends Exception {
    }

    public static class HTTPConnectionHeader {
        private Hashtable reqHeaders = new Hashtable();
        private Hashtable resHeaders = new Hashtable();
        private Vector reqHeaderOrder = new Vector();
        private Vector resHeaderOrder = new Vector();
        private boolean requestFinished = false;
        private boolean responseFinished = false;
        private boolean responseExpected = false;

        private synchronized boolean isResponseExpected() {
            return this.responseExpected;
        }

        private synchronized void setResponseExpected(boolean bl) {
            this.responseExpected = bl;
        }

        public Hashtable getRequestHeaders() {
            return (Hashtable)this.reqHeaders.clone();
        }

        public Hashtable getResponseHeaders() {
            return (Hashtable)this.resHeaders.clone();
        }

        public synchronized boolean isResponseFinished() {
            return this.responseFinished;
        }

        private synchronized void setResponseFinished(boolean bl) {
            this.responseFinished = bl;
        }

        private synchronized boolean isRequestFinished() {
            return this.requestFinished;
        }

        public synchronized void setRequestFinished(boolean bl) {
            this.requestFinished = bl;
        }

        public synchronized void setRequestHeader(String string, String string2) {
            this.setHeader(this.reqHeaders, this.reqHeaderOrder, string, string2);
        }

        public synchronized void setResponseHeader(String string, String string2) {
            this.setHeader(this.resHeaders, this.resHeaderOrder, string, string2);
        }

        public synchronized void resetRequestHeader(String string) {
            this.resetHeader(this.reqHeaders, this.reqHeaderOrder, string);
        }

        public synchronized void resetResponseHeader(String string) {
            this.resetHeader(this.resHeaders, this.resHeaderOrder, string);
        }

        public synchronized void replaceRequestHeader(String string, String string2) {
            this.replaceHeader(this.reqHeaders, this.reqHeaderOrder, string, string2);
        }

        public synchronized void replaceResponseHeader(String string, String string2) {
            this.replaceHeader(this.resHeaders, this.resHeaderOrder, string, string2);
        }

        public synchronized void removeRequestHeaders() {
            this.reqHeaders.clear();
            this.reqHeaderOrder.removeAllElements();
        }

        public synchronized void removeResponseHeaders() {
            this.resHeaders.clear();
            this.resHeaderOrder.removeAllElements();
        }

        public int parseStatus() {
            String string = this.getResponseLine();
            StringTokenizer stringTokenizer = new StringTokenizer(string);
            stringTokenizer.nextToken();
            if (string == null) {
                return 500;
            }
            if (!string.startsWith(HTTPProxyCallback.HTTP_VERSION_PREFIX)) {
                return 500;
            }
            if (!stringTokenizer.hasMoreTokens()) {
                return 500;
            }
            try {
                return Integer.parseInt(stringTokenizer.nextToken());
            }
            catch (NumberFormatException numberFormatException) {
                LogHolder.log(3, LogType.NET, numberFormatException);
                return 500;
            }
        }

        public String parseDomain(boolean bl) {
            return HTTPProxyCallback.parseDomain(this.parseURL(), bl);
        }

        public String parseURL() {
            String string = this.getRequestLine();
            if (string != null) {
                int n = string.indexOf(" ");
                string = n != -1 ? ((n = (string = string.substring(n + 1)).indexOf(" ")) != -1 ? string.substring(0, n) : null) : null;
            }
            return string;
        }

        public synchronized String getRequestLine() {
            return this.getStartLine(this.reqHeaders);
        }

        public synchronized String getResponseLine() {
            return this.getStartLine(this.resHeaders);
        }

        public synchronized void replaceResponseLine(String string) {
            Vector<String> vector = new Vector<String>();
            vector.addElement(string);
            this.resHeaders.put(HTTPProxyCallback.HTTP_START_LINE_KEY.toLowerCase(), vector);
        }

        public synchronized int countRequestHeaders() {
            return this.reqHeaders.size();
        }

        public synchronized int countResponseHeaders() {
            return this.resHeaders.size();
        }

        public synchronized String[] getRequestHeader(String string) {
            return this.getHeader(this.reqHeaders, string);
        }

        public synchronized String[] getResponseHeader(String string) {
            return this.getHeader(this.resHeaders, string);
        }

        public synchronized String[] removeRequestHeader(String string) {
            return this.removeHeader(this.reqHeaders, this.reqHeaderOrder, string);
        }

        public synchronized String[] removeResponseHeader(String string) {
            return this.removeHeader(this.resHeaders, this.resHeaderOrder, string);
        }

        protected synchronized void clearRequest() {
            this.clearHeader(this.reqHeaders, this.reqHeaderOrder);
        }

        protected synchronized void clearResponse() {
            this.clearHeader(this.resHeaders, this.resHeaderOrder);
        }

        private void setHeader(Hashtable hashtable, Vector vector, String string, String string2) {
            Vector<String> vector2 = (Vector<String>)hashtable.get(string.toLowerCase());
            if (vector2 == null) {
                boolean bl = true;
                Enumeration enumeration = vector.elements();
                while (enumeration.hasMoreElements()) {
                    String string3 = (String)enumeration.nextElement();
                    if (!string3.equalsIgnoreCase(string)) continue;
                    bl = false;
                }
                if (bl) {
                    vector.addElement(string);
                }
                vector2 = new Vector<String>();
            }
            vector2.addElement(string2);
            hashtable.put(string.toLowerCase(), vector2);
        }

        private void resetHeader(Hashtable hashtable, Vector vector, String string) {
            String[] arrstring = this.getHeader(hashtable, string);
            if (arrstring != null && arrstring.length > 0) {
                this.replaceRequestHeader(string, arrstring[0]);
            }
        }

        private void replaceHeader(Hashtable hashtable, Vector vector, String string, String string2) {
            this.removeHeader(hashtable, vector, string);
            this.setHeader(hashtable, vector, string, string2);
        }

        private String[] getHeader(Hashtable hashtable, String string) {
            Vector vector = (Vector)hashtable.get(string.toLowerCase());
            return this.valuesToArray(vector);
        }

        private String[] removeHeader(Hashtable hashtable, Vector vector, String string) {
            Object object = vector.elements();
            while (object.hasMoreElements()) {
                String string2 = (String)object.nextElement();
                if (!string2.equalsIgnoreCase(string)) continue;
                vector.removeElement(string2);
            }
            object = (Vector)hashtable.remove(string.toLowerCase());
            return this.valuesToArray((Vector)object);
        }

        private void clearHeader(Hashtable hashtable, Vector vector) {
            hashtable.clear();
            vector.removeAllElements();
        }

        private String getStartLine(Hashtable hashtable) {
            Vector vector = (Vector)hashtable.get(HTTPProxyCallback.HTTP_START_LINE_KEY.toLowerCase());
            if (vector == null || vector.size() == 0) {
                LogHolder.log(3, LogType.FILTER, "Invalid request because it contains no startline");
                return null;
            }
            if (vector.size() > 1) {
                String string = "";
                for (int i = 0; i < vector.size(); ++i) {
                    string = string + vector.elementAt(i) + "\n";
                }
                LogHolder.log(3, LogType.FILTER, "This HTTP message seems to be invalid, because it has multiple start lines:\n" + string);
            }
            return (String)vector.elementAt(0);
        }

        private String[] valuesToArray(Vector vector) {
            if (vector == null) {
                return null;
            }
            int n = vector.size();
            if (n == 0) {
                return null;
            }
            String[] arrstring = new String[n];
            Enumeration enumeration = vector.elements();
            int n2 = 0;
            while (enumeration.hasMoreElements()) {
                arrstring[n2] = (String)enumeration.nextElement();
                ++n2;
            }
            return arrstring;
        }

        private byte[] dumpRequestHeaders() {
            return this.dumpHeaders(this.reqHeaders, this.reqHeaderOrder);
        }

        private byte[] dumpResponseHeaders() {
            return this.dumpHeaders(this.resHeaders, this.resHeaderOrder);
        }

        private byte[] dumpHeaders(Hashtable hashtable, Vector vector) {
            String string = "";
            String string2 = null;
            Enumeration enumeration = vector.elements();
            while (enumeration.hasMoreElements()) {
                string2 = (String)enumeration.nextElement();
                if (string2.equalsIgnoreCase(HTTPProxyCallback.HTTP_START_LINE_KEY)) {
                    if (!string.equals("")) {
                        LogHolder.log(3, LogType.FILTER, "HTTP startline set after Message-Header. This is a Bug. please report this.");
                        throw new IllegalStateException("HTTP startline set after Message-Header. This is a Bug. please report this.");
                    }
                    string = string + this.getStartLine(hashtable) + HTTPProxyCallback.CRLF;
                    continue;
                }
                String[] arrstring = this.getHeader(hashtable, string2);
                if (arrstring == null) continue;
                for (int i = 0; i < arrstring.length; ++i) {
                    string = string + string2 + HTTPProxyCallback.HTTP_HEADER_DELIM + arrstring[i] + HTTPProxyCallback.CRLF;
                }
            }
            string = string + HTTPProxyCallback.CRLF;
            if (LogHolder.isLogged(6, LogType.FILTER)) {
                LogHolder.log(6, LogType.FILTER, Thread.currentThread().getName() + ": header dump:" + System.getProperty("line.separator") + string);
            }
            return string.getBytes();
        }
    }

    private static interface IHTTPHelper {
        public byte[] dumpHeader(HTTPProxyCallback var1, HTTPConnectionHeader var2, AnonProxyRequest var3);
    }
}

