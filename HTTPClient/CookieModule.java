/*
 * Decompiled with CFR 0.150.
 */
package HTTPClient;

import HTTPClient.Cookie;
import HTTPClient.Cookie2;
import HTTPClient.CookiePolicyHandler;
import HTTPClient.DefaultCookiePolicyHandler;
import HTTPClient.GlobalConstants;
import HTTPClient.HTTPClientModule;
import HTTPClient.HTTPConnection;
import HTTPClient.NVPair;
import HTTPClient.Request;
import HTTPClient.Response;
import HTTPClient.RoRequest;
import HTTPClient.Util;
import java.io.IOException;
import java.io.Serializable;
import java.net.ProtocolException;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

public class CookieModule
implements HTTPClientModule,
GlobalConstants {
    private static Hashtable cookie_cntxt_list = new Hashtable();
    private static CookiePolicyHandler cookie_handler = new DefaultCookiePolicyHandler();

    CookieModule() {
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public int requestHandler(Request request, Response[] arrresponse) {
        request.setHeaders(Util.removeAllValues(request.getHeaders(), "Cookie"));
        Hashtable hashtable = Util.getList(cookie_cntxt_list, request.getConnection().getContext());
        if (hashtable.size() == 0) {
            return 0;
        }
        Vector<String> vector = new Vector<String>();
        Vector<Integer> vector2 = new Vector<Integer>();
        boolean bl = false;
        Serializable serializable = hashtable;
        synchronized (serializable) {
            Enumeration enumeration = hashtable.elements();
            Vector<Cookie> vector3 = null;
            while (enumeration.hasMoreElements()) {
                int n;
                Cookie cookie = (Cookie)enumeration.nextElement();
                if (cookie.hasExpired()) {
                    if (vector3 == null) {
                        vector3 = new Vector<Cookie>();
                    }
                    vector3.addElement(cookie);
                    continue;
                }
                if (!cookie.sendWith(request) || cookie_handler != null && !cookie_handler.sendCookie(cookie, request)) continue;
                int n2 = cookie.getPath().length();
                for (n = 0; n < vector2.size() && (Integer)vector2.elementAt(n) >= n2; ++n) {
                }
                vector.insertElementAt(cookie.toExternalForm(), n);
                vector2.insertElementAt(new Integer(n2), n);
                if (!(cookie instanceof Cookie2)) continue;
                bl = true;
            }
            if (vector3 != null) {
                for (int i = 0; i < vector3.size(); ++i) {
                    hashtable.remove(vector3.elementAt(i));
                }
            }
        }
        if (!vector.isEmpty()) {
            int n;
            serializable = new StringBuffer();
            if (bl) {
                ((StringBuffer)serializable).append("$Version=\"1\"; ");
            }
            ((StringBuffer)serializable).append((String)vector.elementAt(0));
            for (int i = 1; i < vector.size(); ++i) {
                ((StringBuffer)serializable).append("; ");
                ((StringBuffer)serializable).append((String)vector.elementAt(i));
            }
            NVPair[] arrnVPair = request.getHeaders();
            arrnVPair = Util.resizeArray(arrnVPair, arrnVPair.length + 1);
            arrnVPair[arrnVPair.length - 1] = new NVPair("Cookie", ((StringBuffer)serializable).toString());
            if (!bl && (n = Util.getIndex(arrnVPair, "Cookie2")) == arrnVPair.length) {
                arrnVPair = Util.addValue(arrnVPair, "Cookie2", "$Version=\"1\"");
            }
            request.setHeaders(arrnVPair);
        }
        return 0;
    }

    public void responsePhase1Handler(Response response, RoRequest roRequest) throws IOException {
        String string = response.getHeader("Set-Cookie");
        String string2 = response.getHeader("Set-Cookie2");
        if (string == null && string2 == null) {
            return;
        }
        response.deleteHeader("Set-Cookie");
        response.deleteHeader("Set-Cookie2");
        if (string != null) {
            this.handleCookie(string, false, roRequest, response);
        }
        if (string2 != null) {
            this.handleCookie(string2, true, roRequest, response);
        }
    }

    public int responsePhase2Handler(Response response, Request request) {
        return 10;
    }

    public void responsePhase3Handler(Response response, RoRequest roRequest) {
    }

    public void trailerHandler(Response response, RoRequest roRequest) throws IOException {
        String string = response.getTrailer("Set-Cookie");
        String string2 = response.getHeader("Set-Cookie2");
        if (string == null && string2 == null) {
            return;
        }
        response.deleteTrailer("Set-Cookie");
        response.deleteTrailer("Set-Cookie2");
        if (string != null) {
            this.handleCookie(string, false, roRequest, response);
        }
        if (string2 != null) {
            this.handleCookie(string2, true, roRequest, response);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void handleCookie(String string, boolean bl, RoRequest roRequest, Response response) throws ProtocolException {
        Hashtable hashtable;
        Cookie[] arrcookie = bl ? Cookie2.parse(string, roRequest) : Cookie.parse(string, roRequest);
        Hashtable hashtable2 = hashtable = Util.getList(cookie_cntxt_list, roRequest.getConnection().getContext());
        synchronized (hashtable2) {
            for (int i = 0; i < arrcookie.length; ++i) {
                Cookie cookie = (Cookie)hashtable.get(arrcookie[i]);
                if (cookie != null && arrcookie[i].hasExpired()) {
                    hashtable.remove(cookie);
                    continue;
                }
                if (cookie_handler != null && !cookie_handler.acceptCookie(arrcookie[i], roRequest, response)) continue;
                hashtable.put(arrcookie[i], arrcookie[i]);
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static void discardAllCookies() {
        Hashtable hashtable = cookie_cntxt_list;
        synchronized (hashtable) {
            cookie_cntxt_list.clear();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static void discardAllCookies(Object object) {
        Hashtable hashtable;
        Hashtable hashtable2 = hashtable = Util.getList(cookie_cntxt_list, object);
        synchronized (hashtable2) {
            hashtable.clear();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static Cookie[] listAllCookies() {
        Hashtable hashtable = cookie_cntxt_list;
        synchronized (hashtable) {
            Cookie[] arrcookie = new Cookie[]{};
            int n = 0;
            Enumeration enumeration = cookie_cntxt_list.elements();
            while (enumeration.hasMoreElements()) {
                Hashtable hashtable2;
                Hashtable hashtable3 = hashtable2 = (Hashtable)enumeration.nextElement();
                synchronized (hashtable3) {
                    arrcookie = Util.resizeArray(arrcookie, n + hashtable2.size());
                    Enumeration enumeration2 = hashtable2.elements();
                    while (enumeration2.hasMoreElements()) {
                        arrcookie[n++] = (Cookie)enumeration2.nextElement();
                    }
                }
            }
            return arrcookie;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static Cookie[] listAllCookies(Object object) {
        Hashtable hashtable;
        Hashtable hashtable2 = hashtable = Util.getList(cookie_cntxt_list, object);
        synchronized (hashtable2) {
            Cookie[] arrcookie = new Cookie[hashtable.size()];
            int n = 0;
            Enumeration enumeration = hashtable.elements();
            while (enumeration.hasMoreElements()) {
                arrcookie[n++] = (Cookie)enumeration.nextElement();
            }
            return arrcookie;
        }
    }

    public static void addCookie(Cookie cookie) {
        Hashtable hashtable = Util.getList(cookie_cntxt_list, HTTPConnection.getDefaultContext());
        hashtable.put(cookie, cookie);
    }

    public static void addCookie(Cookie cookie, Object object) {
        Hashtable hashtable = Util.getList(cookie_cntxt_list, object);
        hashtable.put(cookie, cookie);
    }

    public static void removeCookie(Cookie cookie) {
        Hashtable hashtable = Util.getList(cookie_cntxt_list, HTTPConnection.getDefaultContext());
        hashtable.remove(cookie);
    }

    public static void removeCookie(Cookie cookie, Object object) {
        Hashtable hashtable = Util.getList(cookie_cntxt_list, object);
        hashtable.remove(cookie);
    }

    public static synchronized CookiePolicyHandler setCookiePolicyHandler(CookiePolicyHandler cookiePolicyHandler) {
        CookiePolicyHandler cookiePolicyHandler2 = cookie_handler;
        cookie_handler = cookiePolicyHandler;
        return cookiePolicyHandler2;
    }
}

