/*
 * Decompiled with CFR 0.150.
 */
package HTTPClient;

import HTTPClient.AuthSchemeNotImplException;
import HTTPClient.AuthorizationHandler;
import HTTPClient.AuthorizationInfo;
import HTTPClient.AuthorizationPrompter;
import HTTPClient.Codecs;
import HTTPClient.GlobalConstants;
import HTTPClient.HashVerifier;
import HTTPClient.HttpHeaderElement;
import HTTPClient.MD4;
import HTTPClient.MD5;
import HTTPClient.MD5InputStream;
import HTTPClient.NVPair;
import HTTPClient.ParseException;
import HTTPClient.Request;
import HTTPClient.Response;
import HTTPClient.RoRequest;
import HTTPClient.RoResponse;
import HTTPClient.URI;
import HTTPClient.Util;
import ie.brd.crypto.algorithms.DES.DESAlgorithm;
import java.io.IOException;
import java.net.InetAddress;
import java.util.StringTokenizer;
import java.util.Vector;

public class DefaultAuthHandler
implements AuthorizationHandler,
GlobalConstants {
    private static final byte[] NUL = new byte[0];
    private static final byte[] zeros = new byte[24];
    private static AuthorizationPrompter prompter = null;
    private static DESAlgorithm DES = new DESAlgorithm(false);
    private static byte[] digest_secret = null;
    private static String[] ordering = new String[]{"Digest", "NTLM", "Basic"};

    public AuthorizationInfo[] orderChallenges(AuthorizationInfo[] arrauthorizationInfo, RoRequest roRequest, RoResponse roResponse, boolean bl) {
        int n;
        AuthorizationInfo[] arrauthorizationInfo2 = new AuthorizationInfo[arrauthorizationInfo.length];
        int n2 = 0;
        for (n = 0; n < ordering.length; ++n) {
            for (int i = 0; i < arrauthorizationInfo.length; ++i) {
                if (arrauthorizationInfo[i] == null || !arrauthorizationInfo[i].getScheme().equalsIgnoreCase(ordering[n])) continue;
                arrauthorizationInfo2[n2++] = arrauthorizationInfo[i];
                arrauthorizationInfo[i] = null;
            }
        }
        for (n = 0; n < arrauthorizationInfo.length; ++n) {
            if (arrauthorizationInfo[n] == null) continue;
            arrauthorizationInfo2[n2++] = arrauthorizationInfo[n];
        }
        return arrauthorizationInfo2;
    }

    public AuthorizationInfo fixupAuthInfo(AuthorizationInfo authorizationInfo, Request request, AuthorizationInfo authorizationInfo2, RoResponse roResponse, boolean bl) throws AuthSchemeNotImplException {
        if (authorizationInfo.getScheme().equalsIgnoreCase("Basic") || authorizationInfo.getScheme().equalsIgnoreCase("SOCKS5")) {
            return authorizationInfo;
        }
        if (!authorizationInfo.getScheme().equalsIgnoreCase("Digest") && !authorizationInfo.getScheme().equalsIgnoreCase("NTLM")) {
            throw new AuthSchemeNotImplException(authorizationInfo.getScheme());
        }
        if (authorizationInfo.getScheme().equalsIgnoreCase("Digest")) {
            return DefaultAuthHandler.digest_fixup(authorizationInfo, request, authorizationInfo2, roResponse, bl);
        }
        return DefaultAuthHandler.ntlm_fixup(authorizationInfo, request, authorizationInfo2, roResponse);
    }

    public AuthorizationInfo getAuthorization(AuthorizationInfo authorizationInfo, RoRequest roRequest, RoResponse roResponse, boolean bl) throws AuthSchemeNotImplException {
        AuthorizationInfo authorizationInfo2;
        if (!(authorizationInfo.getScheme().equalsIgnoreCase("Basic") || authorizationInfo.getScheme().equalsIgnoreCase("Digest") || authorizationInfo.getScheme().equalsIgnoreCase("NTLM") || authorizationInfo.getScheme().equalsIgnoreCase("SOCKS5"))) {
            throw new AuthSchemeNotImplException(authorizationInfo.getScheme());
        }
        if (authorizationInfo.getScheme().equalsIgnoreCase("Digest") ? (authorizationInfo2 = DefaultAuthHandler.digest_check_stale(authorizationInfo, roRequest, roResponse)) != null : authorizationInfo.getScheme().equalsIgnoreCase("NTLM") && (authorizationInfo2 = DefaultAuthHandler.ntlm_check_step2(authorizationInfo, roRequest, roResponse)) != null) {
            return authorizationInfo2;
        }
        if (prompter == null) {
            return null;
        }
        NVPair nVPair = prompter.getUsernamePassword(authorizationInfo);
        if (nVPair == null) {
            return null;
        }
        if (authorizationInfo.getScheme().equalsIgnoreCase("Basic")) {
            authorizationInfo2 = DefaultAuthHandler.basic_gen_auth_info(authorizationInfo.getHost(), authorizationInfo.getPort(), authorizationInfo.getRealm(), nVPair.getName(), nVPair.getValue());
        } else if (authorizationInfo.getScheme().equalsIgnoreCase("Digest")) {
            authorizationInfo2 = DefaultAuthHandler.digest_gen_auth_info(authorizationInfo.getHost(), authorizationInfo.getPort(), authorizationInfo.getRealm(), nVPair.getName(), nVPair.getValue(), roRequest.getConnection().getContext());
            authorizationInfo2 = DefaultAuthHandler.digest_fixup(authorizationInfo2, roRequest, authorizationInfo, null, bl);
        } else if (authorizationInfo.getScheme().equalsIgnoreCase("NTLM")) {
            authorizationInfo2 = DefaultAuthHandler.ntlm_gen_auth_info(authorizationInfo.getHost(), authorizationInfo.getPort(), authorizationInfo.getRealm(), nVPair.getName(), nVPair.getValue());
            authorizationInfo2 = DefaultAuthHandler.ntlm_fixup(authorizationInfo2, roRequest, authorizationInfo, null);
        } else {
            authorizationInfo2 = DefaultAuthHandler.socks5_gen_auth_info(authorizationInfo.getHost(), authorizationInfo.getPort(), authorizationInfo.getRealm(), nVPair.getName(), nVPair.getValue());
        }
        nVPair = null;
        System.gc();
        return authorizationInfo2;
    }

    public void handleAuthHeaders(Response response, RoRequest roRequest, AuthorizationInfo authorizationInfo, AuthorizationInfo authorizationInfo2) throws IOException {
        String string = response.getHeader("Authentication-Info");
        String string2 = response.getHeader("Proxy-Authentication-Info");
        if (string == null && authorizationInfo != null && DefaultAuthHandler.hasParam(authorizationInfo.getParams(), "qop", "auth-int")) {
            string = "";
        }
        if (string2 == null && authorizationInfo2 != null && DefaultAuthHandler.hasParam(authorizationInfo2.getParams(), "qop", "auth-int")) {
            string2 = "";
        }
        try {
            DefaultAuthHandler.handleAuthInfo(string, "Authentication-Info", authorizationInfo, response, roRequest, true);
            DefaultAuthHandler.handleAuthInfo(string2, "Proxy-Authentication-Info", authorizationInfo2, response, roRequest, true);
        }
        catch (ParseException parseException) {
            throw new IOException(parseException.toString());
        }
    }

    public void handleAuthTrailers(Response response, RoRequest roRequest, AuthorizationInfo authorizationInfo, AuthorizationInfo authorizationInfo2) throws IOException {
        String string = response.getTrailer("Authentication-Info");
        String string2 = response.getTrailer("Proxy-Authentication-Info");
        try {
            DefaultAuthHandler.handleAuthInfo(string, "Authentication-Info", authorizationInfo, response, roRequest, false);
            DefaultAuthHandler.handleAuthInfo(string2, "Proxy-Authentication-Info", authorizationInfo2, response, roRequest, false);
        }
        catch (ParseException parseException) {
            throw new IOException(parseException.toString());
        }
    }

    private static void handleAuthInfo(String string, String string2, AuthorizationInfo authorizationInfo, Response response, RoRequest roRequest, boolean bl) throws ParseException, IOException {
        if (string == null) {
            return;
        }
        Vector vector = Util.parseHeader(string);
        HttpHeaderElement httpHeaderElement = Util.getElement(vector, "nextnonce");
        if (DefaultAuthHandler.handle_nextnonce(authorizationInfo, roRequest, httpHeaderElement)) {
            vector.removeElement(httpHeaderElement);
        }
        if (DefaultAuthHandler.handle_discard(authorizationInfo, roRequest, httpHeaderElement = Util.getElement(vector, "discard"))) {
            vector.removeElement(httpHeaderElement);
        }
        if (bl) {
            HttpHeaderElement httpHeaderElement2 = null;
            if (vector != null && (httpHeaderElement2 = Util.getElement(vector, "qop")) != null && httpHeaderElement2.getValue() != null) {
                DefaultAuthHandler.handle_rspauth(authorizationInfo, response, roRequest, vector, string2);
            } else if (authorizationInfo != null && (Util.hasToken(response.getHeader("Trailer"), string2) && DefaultAuthHandler.hasParam(authorizationInfo.getParams(), "qop", null) || DefaultAuthHandler.hasParam(authorizationInfo.getParams(), "qop", "auth-int"))) {
                DefaultAuthHandler.handle_rspauth(authorizationInfo, response, roRequest, null, string2);
            } else if (vector != null && httpHeaderElement2 == null && vector.contains(new HttpHeaderElement("digest")) || Util.hasToken(response.getHeader("Trailer"), string2) && authorizationInfo != null && !DefaultAuthHandler.hasParam(authorizationInfo.getParams(), "qop", null)) {
                DefaultAuthHandler.handle_digest(authorizationInfo, response, roRequest, string2);
            }
        }
        if (vector.size() > 0) {
            response.setHeader(string2, Util.assembleHeader(vector));
        } else {
            response.deleteHeader(string2);
        }
    }

    private static final boolean hasParam(NVPair[] arrnVPair, String string, String string2) {
        for (int i = 0; i < arrnVPair.length; ++i) {
            if (!arrnVPair[i].getName().equalsIgnoreCase(string) || string2 != null && !arrnVPair[i].getValue().equalsIgnoreCase(string2)) continue;
            return true;
        }
        return false;
    }

    public void addAuthorizationInfo(String string, String string2, int n, String string3, Object object, Object object2, Object object3) throws AuthSchemeNotImplException {
        AuthorizationInfo authorizationInfo = null;
        if (string.equalsIgnoreCase("Basic")) {
            authorizationInfo = DefaultAuthHandler.basic_gen_auth_info(string2, n, string3, (String)object, (String)object2);
        } else if (string.equalsIgnoreCase("Digest")) {
            authorizationInfo = DefaultAuthHandler.digest_gen_auth_info(string2, n, string3, (String)object, (String)object2, object3);
        } else if (string.equalsIgnoreCase("NTLM")) {
            authorizationInfo = DefaultAuthHandler.ntlm_gen_auth_info(string2, n, string3, (String)object, (String)object2);
        } else if (string.equalsIgnoreCase("SOCKS5")) {
            authorizationInfo = DefaultAuthHandler.socks5_gen_auth_info(string2, n, string3, (String)object, (String)object2);
        } else {
            throw new AuthSchemeNotImplException(string);
        }
        AuthorizationInfo.addAuthorization(authorizationInfo, object3);
    }

    private static AuthorizationInfo basic_gen_auth_info(String string, int n, String string2, String string3, String string4) {
        return new AuthorizationInfo(string, n, "Basic", string2, Codecs.base64Encode(string3 + ":" + string4));
    }

    private static AuthorizationInfo socks5_gen_auth_info(String string, int n, String string2, String string3, String string4) {
        NVPair[] arrnVPair = new NVPair[]{new NVPair(string3, string4)};
        return new AuthorizationInfo(string, n, "SOCKS5", string2, arrnVPair, null);
    }

    private static AuthorizationInfo digest_gen_auth_info(String string, int n, String string2, String string3, String string4, Object object) {
        NVPair[] arrnVPair;
        String string5 = string3 + ":" + string2 + ":" + string4;
        String[] arrstring = new String[]{new MD5(string5).asHex(), null};
        AuthorizationInfo authorizationInfo = AuthorizationInfo.getAuthorization(string, n, "Digest", string2, object);
        if (authorizationInfo == null) {
            arrnVPair = new NVPair[]{new NVPair("username", string3), new NVPair("uri", ""), new NVPair("nonce", ""), new NVPair("response", "")};
        } else {
            arrnVPair = authorizationInfo.getParams();
            for (int i = 0; i < arrnVPair.length; ++i) {
                if (!arrnVPair[i].getName().equalsIgnoreCase("username")) continue;
                arrnVPair[i] = new NVPair("username", string3);
                break;
            }
        }
        return new AuthorizationInfo(string, n, "Digest", string2, arrnVPair, arrstring);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private static AuthorizationInfo digest_fixup(AuthorizationInfo authorizationInfo, RoRequest roRequest, AuthorizationInfo authorizationInfo2, RoResponse roResponse, boolean bl) throws AuthSchemeNotImplException {
        String[] arrstring;
        Object object;
        Object object2;
        String string;
        NVPair[] arrnVPair;
        int n;
        int n2 = -1;
        int n3 = -1;
        int n4 = -1;
        int n5 = -1;
        int n6 = -1;
        int n7 = -1;
        int n8 = -1;
        NVPair[] arrnVPair2 = null;
        if (authorizationInfo2 != null) {
            arrnVPair2 = authorizationInfo2.getParams();
            for (n = 0; n < arrnVPair2.length; ++n) {
                String string2 = arrnVPair2[n].getName().toLowerCase();
                if (string2.equals("domain")) {
                    n2 = n;
                    continue;
                }
                if (string2.equals("nonce")) {
                    n3 = n;
                    continue;
                }
                if (string2.equals("opaque")) {
                    n5 = n;
                    continue;
                }
                if (string2.equals("algorithm")) {
                    n4 = n;
                    continue;
                }
                if (string2.equals("stale")) {
                    n6 = n;
                    continue;
                }
                if (string2.equals("digest-required")) {
                    n7 = n;
                    continue;
                }
                if (!string2.equals("qop")) continue;
                n8 = n;
            }
        }
        n = -1;
        int n9 = -1;
        int n10 = -1;
        int n11 = -1;
        int n12 = -1;
        int n13 = -1;
        int n14 = -1;
        int n15 = -1;
        int n16 = -1;
        int n17 = -1;
        int n18 = -1;
        Object object3 = authorizationInfo;
        synchronized (object3) {
            arrnVPair = authorizationInfo.getParams();
            for (int i = 0; i < arrnVPair.length; ++i) {
                String string3 = arrnVPair[i].getName().toLowerCase();
                if (string3.equals("uri")) {
                    n = i;
                    continue;
                }
                if (string3.equals("username")) {
                    n9 = i;
                    continue;
                }
                if (string3.equals("algorithm")) {
                    n10 = i;
                    continue;
                }
                if (string3.equals("nonce")) {
                    n12 = i;
                    continue;
                }
                if (string3.equals("cnonce")) {
                    n13 = i;
                    continue;
                }
                if (string3.equals("nc")) {
                    n14 = i;
                    continue;
                }
                if (string3.equals("response")) {
                    n11 = i;
                    continue;
                }
                if (string3.equals("opaque")) {
                    n15 = i;
                    continue;
                }
                if (string3.equals("digest")) {
                    n16 = i;
                    continue;
                }
                if (string3.equals("digest-required")) {
                    n17 = i;
                    continue;
                }
                if (!string3.equals("qop")) continue;
                n18 = i;
            }
            if (n10 != -1 && !arrnVPair[n10].getValue().equalsIgnoreCase("MD5") && !arrnVPair[n10].getValue().equalsIgnoreCase("MD5-sess")) {
                throw new AuthSchemeNotImplException("Digest auth scheme: Algorithm " + arrnVPair[n10].getValue() + " not implemented");
            }
            if (n4 != -1 && !arrnVPair2[n4].getValue().equalsIgnoreCase("MD5") && !arrnVPair2[n4].getValue().equalsIgnoreCase("MD5-sess")) {
                throw new AuthSchemeNotImplException("Digest auth scheme: Algorithm " + arrnVPair2[n4].getValue() + " not implemented");
            }
            arrnVPair[n] = new NVPair("uri", roRequest.getRequestURI());
            string = arrnVPair[n12].getValue();
            if (n3 != -1 && !string.equals(arrnVPair2[n3].getValue())) {
                arrnVPair[n12] = arrnVPair2[n3];
            }
            if (n5 != -1) {
                if (n15 == -1) {
                    arrnVPair = Util.resizeArray(arrnVPair, arrnVPair.length + 1);
                    n15 = arrnVPair.length - 1;
                }
                arrnVPair[n15] = arrnVPair2[n5];
            }
            if (n4 != -1) {
                if (n10 == -1) {
                    arrnVPair = Util.resizeArray(arrnVPair, arrnVPair.length + 1);
                    n10 = arrnVPair.length - 1;
                }
                arrnVPair[n10] = arrnVPair2[n4];
            }
            if (n8 != -1 || n4 != -1 && arrnVPair2[n4].getValue().equalsIgnoreCase("MD5-sess")) {
                if (n13 == -1) {
                    arrnVPair = Util.resizeArray(arrnVPair, arrnVPair.length + 1);
                    n13 = arrnVPair.length - 1;
                }
                if (digest_secret == null) {
                    digest_secret = DefaultAuthHandler.gen_random_bytes(20);
                }
                long l = System.currentTimeMillis();
                byte[] arrby = new byte[]{(byte)(l & 0xFFL), (byte)(l >> 8 & 0xFFL), (byte)(l >> 16 & 0xFFL), (byte)(l >> 24 & 0xFFL), (byte)(l >> 32 & 0xFFL), (byte)(l >> 40 & 0xFFL), (byte)(l >> 48 & 0xFFL), (byte)(l >> 56 & 0xFFL)};
                object2 = new MD5(digest_secret);
                ((MD5)object2).Update(arrby);
                arrnVPair[n13] = new NVPair("cnonce", ((MD5)object2).asHex());
            }
            if (n8 != -1) {
                int n19;
                if (n18 == -1) {
                    arrnVPair = Util.resizeArray(arrnVPair, arrnVPair.length + 1);
                    n18 = arrnVPair.length - 1;
                }
                String[] arrstring2 = Util.splitList(arrnVPair2[n8].getValue(), ",");
                object = null;
                for (n19 = 0; n19 < arrstring2.length; ++n19) {
                    if (arrstring2[n19].equalsIgnoreCase("auth-int") && roRequest.getStream() == null) {
                        object = "auth-int";
                        break;
                    }
                    if (!arrstring2[n19].equalsIgnoreCase("auth")) continue;
                    object = "auth";
                }
                if (object == null) {
                    for (n19 = 0; n19 < arrstring2.length; ++n19) {
                        if (!arrstring2[n19].equalsIgnoreCase("auth-int")) continue;
                        throw new AuthSchemeNotImplException("Digest auth scheme: Can't comply with qop option 'auth-int' because data not available");
                    }
                    throw new AuthSchemeNotImplException("Digest auth scheme: None of the available qop options '" + arrnVPair2[n8].getValue() + "' implemented");
                }
                arrnVPair[n18] = new NVPair("qop", (String)object, false);
            }
            if (n18 != -1) {
                if (n14 == -1) {
                    arrnVPair = Util.resizeArray(arrnVPair, arrnVPair.length + 1);
                    n14 = arrnVPair.length - 1;
                    arrnVPair[n14] = new NVPair("nc", "00000001", false);
                } else if (string.equals(arrnVPair[n12].getValue())) {
                    String string4 = Long.toHexString(Long.parseLong(arrnVPair[n14].getValue(), 16) + 1L);
                    arrnVPair[n14] = new NVPair("nc", "00000000".substring(string4.length()) + string4, false);
                } else {
                    arrnVPair[n14] = new NVPair("nc", "00000001", false);
                }
            }
            arrstring = (String[])authorizationInfo.getExtraInfo();
            if (authorizationInfo2 != null && (n6 == -1 || !arrnVPair2[n6].getValue().equalsIgnoreCase("true")) && n10 != -1 && arrnVPair[n10].getValue().equalsIgnoreCase("MD5-sess")) {
                arrstring[1] = new MD5(arrstring[0] + ":" + arrnVPair[n12].getValue() + ":" + arrnVPair[n13].getValue()).asHex();
                authorizationInfo.setExtraInfo(arrstring);
            }
            authorizationInfo.setParams(arrnVPair);
        }
        object3 = n10 != -1 && arrnVPair[n10].getValue().equalsIgnoreCase("MD5-sess") ? arrstring[1] : arrstring[0];
        string = roRequest.getMethod() + ":" + arrnVPair[n].getValue();
        if (n18 != -1 && arrnVPair[n18].getValue().equalsIgnoreCase("auth-int")) {
            object = new MD5();
            ((MD5)object).Update(roRequest.getData() == null ? NUL : roRequest.getData());
            string = string + ":" + ((MD5)object).asHex();
        }
        string = new MD5(string).asHex();
        String string5 = n18 == -1 ? new MD5((String)object3 + ":" + arrnVPair[n12].getValue() + ":" + string).asHex() : new MD5((String)object3 + ":" + arrnVPair[n12].getValue() + ":" + arrnVPair[n14].getValue() + ":" + arrnVPair[n13].getValue() + ":" + arrnVPair[n18].getValue() + ":" + string).asHex();
        arrnVPair[n11] = new NVPair("response", string5);
        boolean bl2 = false;
        if (n7 != -1 && (arrnVPair2[n7].getValue() == null || arrnVPair2[n7].getValue().equalsIgnoreCase("true"))) {
            bl2 = true;
        }
        if ((bl2 || n16 != -1) && roRequest.getStream() == null) {
            if (n16 == -1) {
                object2 = Util.resizeArray(arrnVPair, arrnVPair.length + 1);
                n16 = arrnVPair.length;
            } else {
                object2 = arrnVPair;
            }
            object2[n16] = new NVPair("digest", DefaultAuthHandler.calc_digest(roRequest, arrstring[0], arrnVPair[n12].getValue()));
            if (n17 == -1) {
                n17 = ((NVPair[])object2).length;
                object2 = Util.resizeArray((NVPair[])object2, ((Object)object2).length + 1);
                object2[n17] = new NVPair("digest-required", "true");
            }
            object = new AuthorizationInfo(authorizationInfo.getHost(), authorizationInfo.getPort(), authorizationInfo.getScheme(), authorizationInfo.getRealm(), (NVPair[])object2, arrstring);
        } else {
            object = bl2 ? null : new AuthorizationInfo(authorizationInfo.getHost(), authorizationInfo.getPort(), authorizationInfo.getScheme(), authorizationInfo.getRealm(), arrnVPair, arrstring);
        }
        if (n2 != -1) {
            object2 = null;
            try {
                object2 = new URI(roRequest.getConnection().getProtocol(), roRequest.getConnection().getHost(), roRequest.getConnection().getPort(), roRequest.getRequestURI());
            }
            catch (ParseException parseException) {
                // empty catch block
            }
            StringTokenizer stringTokenizer = new StringTokenizer(arrnVPair2[n2].getValue());
            while (stringTokenizer.hasMoreTokens()) {
                URI uRI;
                try {
                    uRI = new URI((URI)object2, stringTokenizer.nextToken());
                }
                catch (ParseException parseException) {
                    continue;
                }
                AuthorizationInfo authorizationInfo3 = AuthorizationInfo.getAuthorization(uRI.getHost(), uRI.getPort(), authorizationInfo.getScheme(), authorizationInfo.getRealm(), roRequest.getConnection().getContext());
                if (authorizationInfo3 == null) {
                    arrnVPair[n] = new NVPair("uri", uRI.getPath());
                    authorizationInfo3 = new AuthorizationInfo(uRI.getHost(), uRI.getPort(), authorizationInfo.getScheme(), authorizationInfo.getRealm(), arrnVPair, arrstring);
                    AuthorizationInfo.addAuthorization(authorizationInfo3);
                }
                if (bl) continue;
                authorizationInfo3.addPath(uRI.getPath());
            }
        } else if (!bl && authorizationInfo2 != null && (object2 = AuthorizationInfo.getAuthorization(authorizationInfo2.getHost(), authorizationInfo2.getPort(), authorizationInfo.getScheme(), authorizationInfo.getRealm(), roRequest.getConnection().getContext())) != null) {
            ((AuthorizationInfo)object2).addPath("/");
        }
        return object;
    }

    private static AuthorizationInfo digest_check_stale(AuthorizationInfo authorizationInfo, RoRequest roRequest, RoResponse roResponse) throws AuthSchemeNotImplException {
        AuthorizationInfo authorizationInfo2 = null;
        NVPair[] arrnVPair = authorizationInfo.getParams();
        for (int i = 0; i < arrnVPair.length; ++i) {
            String string = arrnVPair[i].getName();
            if (!string.equalsIgnoreCase("stale") || !arrnVPair[i].getValue().equalsIgnoreCase("true")) continue;
            authorizationInfo2 = AuthorizationInfo.getAuthorization(authorizationInfo, roRequest, roResponse, false, false);
            if (authorizationInfo2 == null) break;
            return DefaultAuthHandler.digest_fixup(authorizationInfo2, roRequest, authorizationInfo, roResponse, false);
        }
        return authorizationInfo2;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private static boolean handle_nextnonce(AuthorizationInfo authorizationInfo, RoRequest roRequest, HttpHeaderElement httpHeaderElement) {
        AuthorizationInfo authorizationInfo2;
        if (authorizationInfo == null || httpHeaderElement == null || httpHeaderElement.getValue() == null) {
            return false;
        }
        try {
            authorizationInfo2 = AuthorizationInfo.getAuthorization(authorizationInfo, roRequest, null, false, false);
        }
        catch (AuthSchemeNotImplException authSchemeNotImplException) {
            authorizationInfo2 = authorizationInfo;
        }
        AuthorizationInfo authorizationInfo3 = authorizationInfo2;
        synchronized (authorizationInfo3) {
            NVPair[] arrnVPair = authorizationInfo2.getParams();
            arrnVPair = Util.setValue(arrnVPair, "nonce", httpHeaderElement.getValue());
            arrnVPair = Util.setValue(arrnVPair, "nc", "00000000", false);
            authorizationInfo2.setParams(arrnVPair);
        }
        return true;
    }

    private static boolean handle_digest(AuthorizationInfo authorizationInfo, Response response, RoRequest roRequest, String string) throws IOException {
        if (authorizationInfo == null) {
            return false;
        }
        NVPair[] arrnVPair = authorizationInfo.getParams();
        VerifyDigest verifyDigest = new VerifyDigest(((String[])authorizationInfo.getExtraInfo())[0], Util.getValue(arrnVPair, "nonce"), roRequest.getMethod(), Util.getValue(arrnVPair, "uri"), string, response);
        if (response.hasEntity()) {
            response.inp_stream = new MD5InputStream(response.inp_stream, verifyDigest);
        } else {
            verifyDigest.verifyHash(new MD5().Final(), 0L);
        }
        return true;
    }

    private static boolean handle_rspauth(AuthorizationInfo authorizationInfo, Response response, RoRequest roRequest, Vector vector, String string) throws IOException {
        Object object;
        if (authorizationInfo == null) {
            return false;
        }
        NVPair[] arrnVPair = authorizationInfo.getParams();
        int n = -1;
        int n2 = -1;
        int n3 = -1;
        int n4 = -1;
        int n5 = -1;
        for (int i = 0; i < arrnVPair.length; ++i) {
            object = arrnVPair[i].getName().toLowerCase();
            if (((String)object).equals("uri")) {
                n = i;
                continue;
            }
            if (((String)object).equals("algorithm")) {
                n2 = i;
                continue;
            }
            if (((String)object).equals("nonce")) {
                n3 = i;
                continue;
            }
            if (((String)object).equals("cnonce")) {
                n4 = i;
                continue;
            }
            if (!((String)object).equals("nc")) continue;
            n5 = i;
        }
        VerifyRspAuth verifyRspAuth = new VerifyRspAuth(arrnVPair[n].getValue(), ((String[])authorizationInfo.getExtraInfo())[0], n2 == -1 ? null : arrnVPair[n2].getValue(), arrnVPair[n3].getValue(), n4 == -1 ? "" : arrnVPair[n4].getValue(), n5 == -1 ? "" : arrnVPair[n5].getValue(), string, response);
        object = null;
        if (vector != null && (object = Util.getElement(vector, "qop")) != null && ((HttpHeaderElement)object).getValue() != null && (((HttpHeaderElement)object).getValue().equalsIgnoreCase("auth") || !response.hasEntity() && ((HttpHeaderElement)object).getValue().equalsIgnoreCase("auth-int"))) {
            verifyRspAuth.verifyHash(new MD5().Final(), 0L);
        } else {
            response.inp_stream = new MD5InputStream(response.inp_stream, verifyRspAuth);
        }
        return true;
    }

    private static String calc_digest(RoRequest roRequest, String string, String string2) {
        Object object;
        if (roRequest.getStream() != null) {
            return "";
        }
        int n = -1;
        int n2 = -1;
        int n3 = -1;
        int n4 = -1;
        int n5 = -1;
        for (int i = 0; i < roRequest.getHeaders().length; ++i) {
            object = roRequest.getHeaders()[i].getName();
            if (((String)object).equalsIgnoreCase("Content-type")) {
                n = i;
                continue;
            }
            if (((String)object).equalsIgnoreCase("Content-Encoding")) {
                n2 = i;
                continue;
            }
            if (((String)object).equalsIgnoreCase("Last-Modified")) {
                n3 = i;
                continue;
            }
            if (((String)object).equalsIgnoreCase("Expires")) {
                n4 = i;
                continue;
            }
            if (!((String)object).equalsIgnoreCase("Date")) continue;
            n5 = i;
        }
        NVPair[] arrnVPair = roRequest.getHeaders();
        object = roRequest.getData() == null ? NUL : roRequest.getData();
        MD5 mD5 = new MD5();
        mD5.Update((byte[])object);
        String string3 = new MD5(roRequest.getRequestURI() + ":" + (n == -1 ? "" : arrnVPair[n].getValue()) + ":" + ((Object)object).length + ":" + (n2 == -1 ? "" : arrnVPair[n2].getValue()) + ":" + (n3 == -1 ? "" : arrnVPair[n3].getValue()) + ":" + (n4 == -1 ? "" : arrnVPair[n4].getValue())).asHex();
        String string4 = string + ":" + string2 + ":" + roRequest.getMethod() + ":" + (n5 == -1 ? "" : arrnVPair[n5].getValue()) + ":" + string3 + ":" + mD5.asHex();
        return new MD5(string4).asHex();
    }

    private static boolean handle_discard(AuthorizationInfo authorizationInfo, RoRequest roRequest, HttpHeaderElement httpHeaderElement) {
        if (httpHeaderElement != null && authorizationInfo != null) {
            AuthorizationInfo.removeAuthorization(authorizationInfo, roRequest.getConnection().getContext());
            return true;
        }
        return false;
    }

    private static byte[] gen_random_bytes(int n) {
        byte[] arrby = new byte[n];
        try {
            long l = Runtime.getRuntime().freeMemory();
            arrby[0] = (byte)(l & 0xFFL);
            arrby[1] = (byte)(l >> 8 & 0xFFL);
            int n2 = arrby.hashCode();
            arrby[2] = (byte)(n2 & 0xFF);
            arrby[3] = (byte)(n2 >> 8 & 0xFF);
            arrby[4] = (byte)(n2 >> 16 & 0xFF);
            arrby[5] = (byte)(n2 >> 24 & 0xFF);
            long l2 = System.currentTimeMillis();
            arrby[6] = (byte)(l2 & 0xFFL);
            arrby[7] = (byte)(l2 >> 8 & 0xFFL);
        }
        catch (ArrayIndexOutOfBoundsException arrayIndexOutOfBoundsException) {
            // empty catch block
        }
        return arrby;
    }

    private static AuthorizationInfo ntlm_gen_auth_info(String string, int n, String string2, String string3, String string4) {
        int n2;
        byte[] arrby = DefaultAuthHandler.calc_lm_hpw(string4);
        byte[] arrby2 = DefaultAuthHandler.calc_ntcr_hpw(string4);
        String string5 = null;
        try {
            string5 = System.getProperty("HTTPClient.defAuthHandler.NTLM.host");
        }
        catch (SecurityException securityException) {
            // empty catch block
        }
        if (string5 == null) {
            try {
                string5 = InetAddress.getLocalHost().getHostName();
            }
            catch (Exception exception) {
                // empty catch block
            }
        }
        if (string5 == null) {
            string5 = "localhost";
        }
        if ((n2 = string5.indexOf(46)) != -1) {
            string5 = string5.substring(0, n2);
        }
        String string6 = null;
        int n3 = string3.indexOf(92);
        if (n3 != -1) {
            string6 = string3.substring(0, n3);
        } else {
            try {
                string6 = System.getProperty("HTTPClient.defAuthHandler.NTLM.domain");
            }
            catch (SecurityException securityException) {
                // empty catch block
            }
            if (string6 == null) {
                string6 = string5;
            }
        }
        string3 = string3.substring(n3 + 1);
        Object[] arrobject = new Object[]{string3, string5.toUpperCase().trim(), string6.toUpperCase().trim(), arrby, arrby2};
        return new AuthorizationInfo(string, n, "NTLM", string2, null, arrobject);
    }

    private static AuthorizationInfo ntlm_fixup(AuthorizationInfo authorizationInfo, RoRequest roRequest, AuthorizationInfo authorizationInfo2, RoResponse roResponse) throws AuthSchemeNotImplException {
        byte[] arrby;
        if (authorizationInfo2 == null) {
            return authorizationInfo;
        }
        Object[] arrobject = (Object[])authorizationInfo.getExtraInfo();
        String string = (String)arrobject[0];
        String string2 = (String)arrobject[1];
        String string3 = (String)arrobject[2];
        byte[] arrby2 = (byte[])arrobject[3];
        byte[] arrby3 = (byte[])arrobject[4];
        if (authorizationInfo2.getCookie() == null) {
            arrby = new byte[32 + string2.length() + string3.length()];
            Util.getBytes("NTLMSSP", arrby, 0);
            arrby[8] = 1;
            int n = 32;
            arrby[12] = 7;
            arrby[13] = -78;
            int n2 = string2.length();
            arrby[24] = (byte)n2;
            arrby[25] = (byte)(n2 >> 8);
            arrby[26] = (byte)n2;
            arrby[27] = (byte)(n2 >> 8);
            arrby[28] = (byte)n;
            arrby[29] = (byte)(n >> 8);
            Util.getBytes(string2, n2, arrby, n);
            n += n2;
            n2 = string3.length();
            arrby[16] = (byte)n2;
            arrby[17] = (byte)(n2 >> 8);
            arrby[18] = (byte)n2;
            arrby[19] = (byte)(n2 >> 8);
            arrby[20] = (byte)n;
            arrby[21] = (byte)(n >> 8);
            Util.getBytes(string3, n2, arrby, n);
            n += n2;
        } else {
            int n;
            String string4 = authorizationInfo2.getCookie();
            byte[] arrby4 = string4.getBytes();
            arrby = Codecs.base64Decode(arrby4);
            if (arrby.length < 32) {
                throw new AuthSchemeNotImplException("NTLM auth scheme: Received invalid type-2 message (too short).");
            }
            byte[] arrby5 = new byte[12];
            System.arraycopy(new String("NTLMSSP").getBytes(), 0, arrby5, 0, 7);
            arrby5[7] = 0;
            arrby5[8] = 2;
            arrby5[9] = 0;
            arrby5[10] = 0;
            arrby5[11] = 0;
            for (n = 0; n < 12; ++n) {
                if (arrby[n] == arrby5[n]) continue;
                throw new AuthSchemeNotImplException("NTLM auth scheme: Received invalid type-2 message (Byte " + Integer.toString(n) + " is invalid.");
            }
            n = 0;
            if ((arrby[20] & 1) == 1) {
                n = 1;
            }
            boolean bl = false;
            if ((arrby[21] & 2) == 2) {
                bl = true;
            }
            byte[] arrby6 = new byte[8];
            System.arraycopy(arrby, 24, arrby6, 0, 8);
            int n3 = string3.length();
            int n4 = string.length();
            int n5 = string2.length();
            if (n != 0) {
                n3 = 2 * n3;
                n4 = 2 * n4;
                n5 = 2 * n5;
            }
            arrby = new byte[64 + n3 + n4 + n5 + 48];
            System.arraycopy(new String("NTLMSSP").getBytes(), 0, arrby, 0, 7);
            arrby[7] = 0;
            arrby[8] = 3;
            arrby[9] = 0;
            arrby[10] = 0;
            arrby[11] = 0;
            arrby[12] = 24;
            arrby[13] = 0;
            arrby[14] = 24;
            arrby[15] = 0;
            int n6 = arrby.length - 48;
            arrby[16] = (byte)n6;
            arrby[17] = (byte)(n6 >> 8);
            arrby[18] = (byte)(n6 >> 16);
            arrby[19] = (byte)(n6 >> 24);
            arrby[20] = 24;
            arrby[21] = 0;
            arrby[22] = 24;
            arrby[23] = 0;
            int n7 = arrby.length - 24;
            arrby[24] = (byte)n7;
            arrby[25] = (byte)(n7 >> 8);
            arrby[26] = (byte)(n7 >> 16);
            arrby[27] = (byte)(n7 >> 24);
            arrby[28] = (byte)n3;
            arrby[29] = (byte)(n3 >> 8);
            arrby[30] = (byte)n3;
            arrby[31] = (byte)(n3 >> 8);
            arrby[32] = 64;
            arrby[33] = 0;
            arrby[34] = 0;
            arrby[35] = 0;
            arrby[36] = (byte)n4;
            arrby[37] = (byte)(n4 >> 8);
            arrby[38] = (byte)n4;
            arrby[39] = (byte)(n4 >> 8);
            int n8 = 64 + n3;
            arrby[40] = (byte)n8;
            arrby[41] = (byte)(n8 >> 8);
            arrby[42] = (byte)(n8 >> 16);
            arrby[43] = (byte)(n8 >> 24);
            arrby[44] = (byte)n5;
            arrby[45] = (byte)(n5 >> 8);
            arrby[46] = (byte)n5;
            arrby[47] = (byte)(n5 >> 8);
            int n9 = 64 + n3 + n4;
            arrby[48] = (byte)n9;
            arrby[49] = (byte)(n9 >> 8);
            arrby[50] = (byte)(n9 >> 16);
            arrby[51] = (byte)(n9 >> 24);
            arrby[52] = 0;
            arrby[53] = 0;
            arrby[54] = 0;
            arrby[55] = 0;
            arrby[56] = (byte)arrby.length;
            arrby[57] = (byte)(arrby.length >> 8);
            arrby[58] = (byte)(arrby.length >> 16);
            arrby[59] = (byte)(arrby.length >> 24);
            arrby[60] = n != 0 ? 1 : 2;
            arrby[61] = bl ? 2 : 0;
            arrby[62] = 0;
            arrby[63] = 0;
            if (n != 0) {
                DefaultAuthHandler.writeUnicode(string3, arrby, 64);
                DefaultAuthHandler.writeUnicode(string, arrby, n8);
                DefaultAuthHandler.writeUnicode(string2, arrby, n9);
            } else {
                System.arraycopy(string3.getBytes(), 0, arrby, 64, n3);
                System.arraycopy(string.getBytes(), 0, arrby, n8, n4);
                System.arraycopy(string2.getBytes(), 0, arrby, n9, n5);
            }
            System.arraycopy(DefaultAuthHandler.calc_ntcr_resp(arrby2, arrby6), 0, arrby, n6, 24);
            System.arraycopy(DefaultAuthHandler.calc_ntcr_resp(arrby3, arrby6), 0, arrby, n7, 24);
        }
        String string5 = new String(Codecs.base64Encode(arrby));
        AuthorizationInfo authorizationInfo3 = new AuthorizationInfo(authorizationInfo2.getHost(), authorizationInfo2.getPort(), authorizationInfo2.getScheme(), authorizationInfo2.getRealm(), string5);
        authorizationInfo3.setExtraInfo(arrobject);
        authorizationInfo.setCookie(string5);
        return authorizationInfo3;
    }

    private static AuthorizationInfo ntlm_check_step2(AuthorizationInfo authorizationInfo, RoRequest roRequest, RoResponse roResponse) throws AuthSchemeNotImplException {
        String string = Util.getValue(roRequest.getHeaders(), "Authorization");
        AuthorizationInfo authorizationInfo2 = AuthorizationInfo.getAuthorization(authorizationInfo, roRequest, roResponse, false, false);
        if (authorizationInfo.getCookie() != null && authorizationInfo2 != null && string != null && string.startsWith("NTLM TlRMTVNTUAAB")) {
            return DefaultAuthHandler.ntlm_fixup(authorizationInfo2, roRequest, authorizationInfo, null);
        }
        return null;
    }

    private static int writeUnicode(String string, byte[] arrby, int n) {
        int n2 = string.length();
        for (int i = 0; i < n2; ++i) {
            char c = string.charAt(i);
            arrby[n++] = (byte)c;
            arrby[n++] = (byte)(c >> 8);
        }
        return n;
    }

    private static byte[] calc_ntcr_hpw(String string) {
        byte[] arrby = new byte[string.length() * 2];
        int n = 0;
        for (int i = 0; i < string.length(); ++i) {
            char c = string.charAt(i);
            arrby[n++] = (byte)(c & 0xFF);
            arrby[n++] = (byte)(c >>> 8);
        }
        byte[] arrby2 = new MD4(arrby).getHash();
        return Util.resizeArray(arrby2, 21);
    }

    private static byte[] calc_lm_hpw(String string) {
        string = string.toUpperCase();
        byte[] arrby = new byte[14];
        Util.getBytes(string, Math.min(string.length(), 14), arrby, 0);
        byte[] arrby2 = new byte[21];
        byte[] arrby3 = new byte[]{75, 71, 83, 33, 64, 35, 36, 37};
        byte[] arrby4 = new byte[8];
        int[] arrn = DefaultAuthHandler.setup_key(arrby, 0);
        DES.des_ecb_encrypt(arrby3, arrby4, arrn, true);
        System.arraycopy(arrby4, 0, arrby2, 0, 8);
        arrn = DefaultAuthHandler.setup_key(arrby, 7);
        DES.des_ecb_encrypt(arrby3, arrby4, arrn, true);
        System.arraycopy(arrby4, 0, arrby2, 8, 8);
        return arrby2;
    }

    private static byte[] calc_ntcr_resp(byte[] arrby, byte[] arrby2) {
        byte[] arrby3 = new byte[24];
        byte[] arrby4 = new byte[8];
        int[] arrn = DefaultAuthHandler.setup_key(arrby, 0);
        DES.des_ecb_encrypt(arrby2, arrby4, arrn, true);
        System.arraycopy(arrby4, 0, arrby3, 0, 8);
        arrn = DefaultAuthHandler.setup_key(arrby, 7);
        DES.des_ecb_encrypt(arrby2, arrby4, arrn, true);
        System.arraycopy(arrby4, 0, arrby3, 8, 8);
        arrn = DefaultAuthHandler.setup_key(arrby, 14);
        DES.des_ecb_encrypt(arrby2, arrby4, arrn, true);
        System.arraycopy(arrby4, 0, arrby3, 16, 8);
        return arrby3;
    }

    private static int[] setup_key(byte[] arrby, int n) {
        byte[] arrby2 = new byte[8];
        int[] arrn = new int[32];
        arrby2[0] = arrby[n];
        arrby2[1] = (byte)(arrby[n + 0] << 7 | (arrby[n + 1] & 0xFF) >> 1);
        arrby2[2] = (byte)(arrby[n + 1] << 6 | (arrby[n + 2] & 0xFF) >> 2);
        arrby2[3] = (byte)(arrby[n + 2] << 5 | (arrby[n + 3] & 0xFF) >> 3);
        arrby2[4] = (byte)(arrby[n + 3] << 4 | (arrby[n + 4] & 0xFF) >> 4);
        arrby2[5] = (byte)(arrby[n + 4] << 3 | (arrby[n + 5] & 0xFF) >> 5);
        arrby2[6] = (byte)(arrby[n + 5] << 2 | (arrby[n + 6] & 0xFF) >> 6);
        arrby2[7] = (byte)(arrby[n + 6] << 1);
        DES.des_set_odd_parity(arrby2);
        DES.des_set_key(arrby2, arrn);
        return arrn;
    }

    public static AuthorizationPrompter setAuthorizationPrompter(AuthorizationPrompter authorizationPrompter) {
        AuthorizationPrompter authorizationPrompter2 = prompter;
        prompter = authorizationPrompter;
        return authorizationPrompter2;
    }

    private static final byte[] unHex(String string) {
        byte[] arrby = new byte[string.length() / 2];
        for (int i = 0; i < arrby.length; ++i) {
            arrby[i] = (byte)(0xFF & Integer.parseInt(string.substring(2 * i, 2 * (i + 1)), 16));
        }
        return arrby;
    }

    private static String hex(byte[] arrby) {
        StringBuffer stringBuffer = new StringBuffer(arrby.length * 3);
        for (int i = 0; i < arrby.length; ++i) {
            stringBuffer.append(Character.forDigit(arrby[i] >>> 4 & 0xF, 16));
            stringBuffer.append(Character.forDigit(arrby[i] & 0xF, 16));
            stringBuffer.append(':');
        }
        stringBuffer.setLength(stringBuffer.length() - 1);
        return stringBuffer.toString();
    }

    private static class VerifyDigest
    implements HashVerifier,
    GlobalConstants {
        private String HA1;
        private String nonce;
        private String method;
        private String uri;
        private String hdr;
        private RoResponse resp;

        public VerifyDigest(String string, String string2, String string3, String string4, String string5, RoResponse roResponse) {
            this.HA1 = string;
            this.nonce = string2;
            this.method = string3;
            this.uri = string4;
            this.hdr = string5;
            this.resp = roResponse;
        }

        public void verifyHash(byte[] arrby, long l) throws IOException {
            Vector vector;
            String string = this.resp.getHeader(this.hdr);
            if (string == null) {
                string = this.resp.getTrailer(this.hdr);
            }
            if (string == null) {
                return;
            }
            try {
                vector = Util.parseHeader(string);
            }
            catch (ParseException parseException) {
                throw new IOException(parseException.toString());
            }
            HttpHeaderElement httpHeaderElement = Util.getElement(vector, "digest");
            if (httpHeaderElement == null || httpHeaderElement.getValue() == null) {
                return;
            }
            byte[] arrby2 = DefaultAuthHandler.unHex(httpHeaderElement.getValue());
            String string2 = new MD5(this.uri + ":" + this.header_val("Content-type", this.resp) + ":" + this.header_val("Content-length", this.resp) + ":" + this.header_val("Content-Encoding", this.resp) + ":" + this.header_val("Last-Modified", this.resp) + ":" + this.header_val("Expires", this.resp)).asHex();
            arrby = new MD5(this.HA1 + ":" + this.nonce + ":" + this.method + ":" + this.header_val("Date", this.resp) + ":" + string2 + ":" + MD5.asHex(arrby)).Final();
            for (int i = 0; i < arrby.length; ++i) {
                if (arrby[i] == arrby2[i]) continue;
                throw new IOException("MD5-Digest mismatch: expected " + DefaultAuthHandler.hex(arrby2) + " but calculated " + DefaultAuthHandler.hex(arrby));
            }
        }

        private final String header_val(String string, RoResponse roResponse) throws IOException {
            String string2 = roResponse.getHeader(string);
            String string3 = roResponse.getTrailer(string);
            return string2 != null ? string2 : (string3 != null ? string3 : "");
        }
    }

    private static class VerifyRspAuth
    implements HashVerifier,
    GlobalConstants {
        private String uri;
        private String HA1;
        private String alg;
        private String nonce;
        private String cnonce;
        private String nc;
        private String hdr;
        private RoResponse resp;

        public VerifyRspAuth(String string, String string2, String string3, String string4, String string5, String string6, String string7, RoResponse roResponse) {
            this.uri = string;
            this.HA1 = string2;
            this.alg = string3;
            this.nonce = string4;
            this.cnonce = string5;
            this.nc = string6;
            this.hdr = string7;
            this.resp = roResponse;
        }

        public void verifyHash(byte[] arrby, long l) throws IOException {
            String string;
            Vector vector;
            String string2 = this.resp.getHeader(this.hdr);
            if (string2 == null) {
                string2 = this.resp.getTrailer(this.hdr);
            }
            if (string2 == null) {
                return;
            }
            try {
                vector = Util.parseHeader(string2);
            }
            catch (ParseException parseException) {
                throw new IOException(parseException.toString());
            }
            HttpHeaderElement httpHeaderElement = Util.getElement(vector, "qop");
            if (httpHeaderElement == null || (string = httpHeaderElement.getValue()) == null || !string.equalsIgnoreCase("auth") && !string.equalsIgnoreCase("auth-int")) {
                return;
            }
            httpHeaderElement = Util.getElement(vector, "rspauth");
            if (httpHeaderElement == null || httpHeaderElement.getValue() == null) {
                return;
            }
            byte[] arrby2 = DefaultAuthHandler.unHex(httpHeaderElement.getValue());
            httpHeaderElement = Util.getElement(vector, "cnonce");
            if (httpHeaderElement != null && httpHeaderElement.getValue() != null && !httpHeaderElement.getValue().equals(this.cnonce)) {
                throw new IOException("Digest auth scheme: received wrong client-nonce '" + httpHeaderElement.getValue() + "' - expected '" + this.cnonce + "'");
            }
            httpHeaderElement = Util.getElement(vector, "nc");
            if (httpHeaderElement != null && httpHeaderElement.getValue() != null && !httpHeaderElement.getValue().equals(this.nc)) {
                throw new IOException("Digest auth scheme: received wrong nonce-count '" + httpHeaderElement.getValue() + "' - expected '" + this.nc + "'");
            }
            String string3 = this.alg != null && this.alg.equalsIgnoreCase("MD5-sess") ? new MD5(this.HA1 + ":" + this.nonce + ":" + this.cnonce).asHex() : this.HA1;
            String string4 = ":" + this.uri;
            if (string.equalsIgnoreCase("auth-int")) {
                string4 = string4 + ":" + MD5.asHex(arrby);
            }
            string4 = new MD5(string4).asHex();
            arrby = new MD5(string3 + ":" + this.nonce + ":" + this.nc + ":" + this.cnonce + ":" + string + ":" + string4).Final();
            for (int i = 0; i < arrby.length; ++i) {
                if (arrby[i] == arrby2[i]) continue;
                throw new IOException("MD5-Digest mismatch: expected " + DefaultAuthHandler.hex(arrby2) + " but calculated " + DefaultAuthHandler.hex(arrby));
            }
        }
    }
}

