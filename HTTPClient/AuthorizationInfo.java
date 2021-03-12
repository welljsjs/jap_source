/*
 * Decompiled with CFR 0.150.
 */
package HTTPClient;

import HTTPClient.AuthSchemeNotImplException;
import HTTPClient.AuthorizationHandler;
import HTTPClient.DefaultAuthHandler;
import HTTPClient.GlobalConstants;
import HTTPClient.HTTPConnection;
import HTTPClient.NVPair;
import HTTPClient.RoRequest;
import HTTPClient.RoResponse;
import HTTPClient.Util;
import java.net.ProtocolException;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

public class AuthorizationInfo
implements GlobalConstants,
Cloneable {
    private static Hashtable CntxtList = new Hashtable();
    private static AuthorizationHandler AuthHandler = new DefaultAuthHandler();
    private String Host;
    private int Port;
    private String Scheme;
    private String Realm;
    private String cookie;
    private NVPair[] auth_params = new NVPair[0];
    private Object extra_info = null;
    private String[] paths = new String[0];

    AuthorizationInfo(String string, int n) {
        this.Host = string.trim().toLowerCase();
        this.Port = n;
    }

    public AuthorizationInfo(String string, int n, String string2, String string3, NVPair[] arrnVPair, Object object) {
        this.Scheme = string2.trim();
        this.Host = string.trim().toLowerCase();
        this.Port = n;
        this.Realm = string3;
        this.cookie = null;
        if (arrnVPair != null) {
            this.auth_params = Util.resizeArray(arrnVPair, arrnVPair.length);
        }
        this.extra_info = object;
    }

    public AuthorizationInfo(String string, int n, String string2, String string3, String string4) {
        this.Scheme = string2.trim();
        this.Host = string.trim().toLowerCase();
        this.Port = n;
        this.Realm = string3;
        this.cookie = string4 != null ? string4.trim() : null;
    }

    AuthorizationInfo(AuthorizationInfo authorizationInfo) {
        this.Scheme = authorizationInfo.Scheme;
        this.Host = authorizationInfo.Host;
        this.Port = authorizationInfo.Port;
        this.Realm = authorizationInfo.Realm;
        this.cookie = authorizationInfo.cookie;
        this.auth_params = Util.resizeArray(authorizationInfo.auth_params, authorizationInfo.auth_params.length);
        this.extra_info = authorizationInfo.extra_info;
    }

    public static AuthorizationHandler setAuthHandler(AuthorizationHandler authorizationHandler) {
        AuthorizationHandler authorizationHandler2 = AuthHandler;
        AuthHandler = authorizationHandler;
        return authorizationHandler2;
    }

    public static AuthorizationHandler getAuthHandler() {
        return AuthHandler;
    }

    public static AuthorizationInfo getAuthorization(String string, int n, String string2, String string3) {
        return AuthorizationInfo.getAuthorization(string, n, string2, string3, HTTPConnection.getDefaultContext());
    }

    public static synchronized AuthorizationInfo getAuthorization(String string, int n, String string2, String string3, Object object) {
        Hashtable hashtable = Util.getList(CntxtList, object);
        AuthorizationInfo authorizationInfo = new AuthorizationInfo(string.trim(), n, string2.trim(), string3, null, null);
        return (AuthorizationInfo)hashtable.get(authorizationInfo);
    }

    static AuthorizationInfo queryAuthHandler(AuthorizationInfo authorizationInfo, RoRequest roRequest, RoResponse roResponse, boolean bl) throws AuthSchemeNotImplException {
        if (AuthHandler == null) {
            return null;
        }
        AuthorizationInfo authorizationInfo2 = AuthHandler.getAuthorization(authorizationInfo, roRequest, roResponse, bl);
        if (authorizationInfo2 != null) {
            if (roRequest != null) {
                AuthorizationInfo.addAuthorization((AuthorizationInfo)authorizationInfo2.clone(), roRequest.getConnection().getContext());
            } else {
                AuthorizationInfo.addAuthorization((AuthorizationInfo)authorizationInfo2.clone(), HTTPConnection.getDefaultContext());
            }
        }
        return authorizationInfo2;
    }

    static synchronized AuthorizationInfo getAuthorization(AuthorizationInfo authorizationInfo, RoRequest roRequest, RoResponse roResponse, boolean bl, boolean bl2) throws AuthSchemeNotImplException {
        Hashtable hashtable = roRequest != null ? Util.getList(CntxtList, roRequest.getConnection().getContext()) : Util.getList(CntxtList, HTTPConnection.getDefaultContext());
        AuthorizationInfo authorizationInfo2 = (AuthorizationInfo)hashtable.get(authorizationInfo);
        if (authorizationInfo2 == null && bl2) {
            authorizationInfo2 = AuthorizationInfo.queryAuthHandler(authorizationInfo, roRequest, roResponse, bl);
        }
        return authorizationInfo2;
    }

    static AuthorizationInfo getAuthorization(String string, int n, String string2, String string3, boolean bl, boolean bl2) throws AuthSchemeNotImplException {
        return AuthorizationInfo.getAuthorization(new AuthorizationInfo(string.trim(), n, string2.trim(), string3, null, null), null, null, bl, bl2);
    }

    public static void addAuthorization(AuthorizationInfo authorizationInfo) {
        AuthorizationInfo.addAuthorization(authorizationInfo, HTTPConnection.getDefaultContext());
    }

    public static void addAuthorization(AuthorizationInfo authorizationInfo, Object object) {
        Hashtable hashtable = Util.getList(CntxtList, object);
        AuthorizationInfo authorizationInfo2 = (AuthorizationInfo)hashtable.get(authorizationInfo);
        if (authorizationInfo2 != null) {
            int n = authorizationInfo2.paths.length;
            int n2 = authorizationInfo.paths.length;
            if (n2 == 0) {
                authorizationInfo.paths = authorizationInfo2.paths;
            } else {
                authorizationInfo.paths = Util.resizeArray(authorizationInfo.paths, n2 + n);
                System.arraycopy(authorizationInfo2.paths, 0, authorizationInfo.paths, n2, n);
            }
        }
        hashtable.put(authorizationInfo, authorizationInfo);
    }

    public static void addAuthorization(String string, int n, String string2, String string3, String string4, NVPair[] arrnVPair, Object object) {
        AuthorizationInfo.addAuthorization(string, n, string2, string3, string4, arrnVPair, object, HTTPConnection.getDefaultContext());
    }

    public static void addAuthorization(String string, int n, String string2, String string3, String string4, NVPair[] arrnVPair, Object object, Object object2) {
        AuthorizationInfo authorizationInfo = new AuthorizationInfo(string, n, string2, string3, string4);
        if (arrnVPair != null && arrnVPair.length > 0) {
            authorizationInfo.auth_params = Util.resizeArray(arrnVPair, arrnVPair.length);
        }
        authorizationInfo.extra_info = object;
        AuthorizationInfo.addAuthorization(authorizationInfo, object2);
    }

    public static void addBasicAuthorization(String string, int n, String string2, String string3, String string4) throws AuthSchemeNotImplException {
        AuthorizationInfo.addAuthorization("Basic", string, n, string2, (Object)string3, string4, HTTPConnection.getDefaultContext());
    }

    public static void addBasicAuthorization(String string, int n, String string2, String string3, String string4, Object object) throws AuthSchemeNotImplException {
        AuthorizationInfo.addAuthorization("Basic", string, n, string2, (Object)string3, string4, object);
    }

    public static void addDigestAuthorization(String string, int n, String string2, String string3, String string4) throws AuthSchemeNotImplException {
        AuthorizationInfo.addAuthorization("Digest", string, n, string2, (Object)string3, string4, HTTPConnection.getDefaultContext());
    }

    public static void addDigestAuthorization(String string, int n, String string2, String string3, String string4, Object object) throws AuthSchemeNotImplException {
        AuthorizationInfo.addAuthorization("Digest", string, n, string2, (Object)string3, string4, object);
    }

    public static void addAuthorization(String string, String string2, int n, String string3, Object object, Object object2) throws AuthSchemeNotImplException {
        AuthorizationInfo.addAuthorization(string, string2, n, string3, object, object2, HTTPConnection.getDefaultContext());
    }

    public static void addAuthorization(String string, String string2, int n, String string3, Object object, Object object2, Object object3) throws AuthSchemeNotImplException {
        if (AuthHandler == null) {
            throw new AuthSchemeNotImplException("no authorization handler installed");
        }
        AuthHandler.addAuthorizationInfo(string, string2, n, string3, object, object2, object3);
    }

    public static void removeAuthorization(AuthorizationInfo authorizationInfo) {
        AuthorizationInfo.removeAuthorization(authorizationInfo, HTTPConnection.getDefaultContext());
    }

    public static void removeAuthorization(AuthorizationInfo authorizationInfo, Object object) {
        Hashtable hashtable = Util.getList(CntxtList, object);
        hashtable.remove(authorizationInfo);
    }

    public static void removeAuthorization(String string, int n, String string2, String string3) {
        AuthorizationInfo.removeAuthorization(new AuthorizationInfo(string, n, string2, string3, null, null));
    }

    public static void removeAuthorization(String string, int n, String string2, String string3, Object object) {
        AuthorizationInfo.removeAuthorization(new AuthorizationInfo(string, n, string2, string3, null, null), object);
    }

    static AuthorizationInfo findBest(RoRequest roRequest) {
        int n;
        String[] arrstring;
        AuthorizationInfo authorizationInfo;
        String string = Util.getPath(roRequest.getRequestURI());
        String string2 = roRequest.getConnection().getHost();
        int n2 = roRequest.getConnection().getPort();
        Hashtable hashtable = Util.getList(CntxtList, roRequest.getConnection().getContext());
        Enumeration enumeration = hashtable.elements();
        while (enumeration.hasMoreElements()) {
            authorizationInfo = (AuthorizationInfo)enumeration.nextElement();
            if (!authorizationInfo.Host.equals(string2) || authorizationInfo.Port != n2) continue;
            arrstring = authorizationInfo.paths;
            for (n = 0; n < arrstring.length; ++n) {
                if (!string.equals(arrstring[n])) continue;
                return authorizationInfo;
            }
        }
        authorizationInfo = null;
        arrstring = string.substring(0, string.lastIndexOf(47) + 1);
        n = Integer.MAX_VALUE;
        enumeration = hashtable.elements();
        while (enumeration.hasMoreElements()) {
            AuthorizationInfo authorizationInfo2 = (AuthorizationInfo)enumeration.nextElement();
            if (!authorizationInfo2.Host.equals(string2) || authorizationInfo2.Port != n2) continue;
            String[] arrstring2 = authorizationInfo2.paths;
            for (int i = 0; i < arrstring2.length; ++i) {
                int n3;
                int n4;
                String string3 = arrstring2[i].substring(0, arrstring2[i].lastIndexOf(47) + 1);
                if (arrstring.equals(string3)) {
                    return authorizationInfo2;
                }
                if (arrstring.startsWith(string3)) {
                    n4 = 0;
                    n3 = string3.length() - 1;
                    while ((n3 = arrstring.indexOf(47, n3 + 1)) != -1) {
                        ++n4;
                    }
                    if (n4 >= n) continue;
                    n = n4;
                    authorizationInfo = authorizationInfo2;
                    continue;
                }
                if (!string3.startsWith((String)arrstring)) continue;
                n4 = 0;
                n3 = arrstring.length();
                while ((n3 = string3.indexOf(47, n3 + 1)) != -1) {
                    ++n4;
                }
                if (n4 >= n) continue;
                n = n4;
                authorizationInfo = authorizationInfo2;
            }
        }
        return authorizationInfo;
    }

    synchronized void addPath(String string) {
        String string2 = Util.getPath(string);
        for (int i = 0; i < this.paths.length; ++i) {
            if (!this.paths[i].equals(string2)) continue;
            return;
        }
        this.paths = Util.resizeArray(this.paths, this.paths.length + 1);
        this.paths[this.paths.length - 1] = string2;
    }

    static AuthorizationInfo[] parseAuthString(String string, String string2, int n) throws ProtocolException {
        int n2 = 0;
        int n3 = 0;
        char[] arrc = string.toCharArray();
        int n4 = arrc.length;
        AuthorizationInfo[] arrauthorizationInfo = new AuthorizationInfo[]{};
        while (Character.isWhitespace(arrc[n4 - 1])) {
            --n4;
        }
        while ((n2 = Util.skipSpace(arrc, n2)) < n4) {
            n3 = Util.findSpace(arrc, n2 + 1);
            AuthorizationInfo authorizationInfo = new AuthorizationInfo(string2, n);
            authorizationInfo.Scheme = string.substring(n2, n3);
            boolean bl = true;
            Vector<NVPair> vector = new Vector<NVPair>();
            while ((n2 = Util.skipSpace(arrc, n3)) < n4) {
                String string3;
                if (!bl) {
                    if (arrc[n2] != ',') {
                        throw new ProtocolException("Bad Authentication header format: '" + string + "'\nExpected \",\" at position " + n2);
                    }
                    if ((n2 = Util.skipSpace(arrc, n2 + 1)) >= n4) break;
                    if (arrc[n2] == ',') {
                        n3 = n2;
                        continue;
                    }
                }
                int n5 = n2;
                for (n3 = n2 + 1; n3 < n4 && !Character.isWhitespace(arrc[n3]) && arrc[n3] != '=' && arrc[n3] != ','; ++n3) {
                }
                if (bl && (n3 == n4 || arrc[n3] == '=' && (n3 + 1 == n4 || arrc[n3 + 1] == '=' && n3 + 2 == n4))) {
                    authorizationInfo.cookie = string.substring(n2, n4);
                    n2 = n4;
                    break;
                }
                String string4 = string.substring(n2, n3);
                n2 = Util.skipSpace(arrc, n3);
                if (n2 < n4 && arrc[n2] != '=' && arrc[n2] != ',') {
                    n2 = n5;
                    break;
                }
                if (arrc[n2] == '=') {
                    if ((n2 = Util.skipSpace(arrc, n2 + 1)) >= n4) {
                        throw new ProtocolException("Bad Authentication header format: " + string + "\nUnexpected EOL after token" + " at position " + (n3 - 1));
                    }
                    if (arrc[n2] != '\"') {
                        n3 = Util.skipToken(arrc, n2);
                        if (n3 == n2) {
                            throw new ProtocolException("Bad Authentication header format: " + string + "\nToken expected at " + "position " + n2);
                        }
                        string3 = string.substring(n2, n3);
                    } else {
                        n3 = n2++;
                        while ((n3 = string.indexOf(34, n3 + 1)) != -1 && string.charAt(n3 - 1) == '\\') {
                        }
                        if (n3 == -1) {
                            throw new ProtocolException("Bad Authentication header format: " + string + "\nClosing <\"> for " + "quoted-string starting at position " + n2 + " not found");
                        }
                        string3 = Util.dequoteString(string.substring(n2, n3));
                        ++n3;
                    }
                } else {
                    string3 = null;
                }
                if (string4.equalsIgnoreCase("realm")) {
                    authorizationInfo.Realm = string3;
                } else {
                    vector.addElement(new NVPair(string4, string3));
                }
                bl = false;
            }
            if (!vector.isEmpty()) {
                authorizationInfo.auth_params = new NVPair[vector.size()];
                vector.copyInto(authorizationInfo.auth_params);
            }
            if (authorizationInfo.Realm == null) {
                authorizationInfo.Realm = "";
            }
            arrauthorizationInfo = Util.resizeArray(arrauthorizationInfo, arrauthorizationInfo.length + 1);
            arrauthorizationInfo[arrauthorizationInfo.length - 1] = authorizationInfo;
        }
        return arrauthorizationInfo;
    }

    public final String getHost() {
        return this.Host;
    }

    public final int getPort() {
        return this.Port;
    }

    public final String getScheme() {
        return this.Scheme;
    }

    public final String getRealm() {
        return this.Realm;
    }

    public final String getCookie() {
        return this.cookie;
    }

    public final void setCookie(String string) {
        this.cookie = string;
    }

    public final NVPair[] getParams() {
        return Util.resizeArray(this.auth_params, this.auth_params.length);
    }

    public final void setParams(NVPair[] arrnVPair) {
        this.auth_params = arrnVPair != null ? Util.resizeArray(arrnVPair, arrnVPair.length) : new NVPair[0];
    }

    public final Object getExtraInfo() {
        return this.extra_info;
    }

    public final void setExtraInfo(Object object) {
        this.extra_info = object;
    }

    public String toString() {
        StringBuffer stringBuffer = new StringBuffer(100);
        stringBuffer.append(this.Scheme);
        stringBuffer.append(" ");
        if (this.cookie != null) {
            stringBuffer.append(this.cookie);
        } else {
            if (this.Realm.length() > 0) {
                stringBuffer.append("realm=\"");
                stringBuffer.append(Util.quoteString(this.Realm, "\\\""));
                stringBuffer.append('\"');
            }
            for (int i = 0; i < this.auth_params.length; ++i) {
                stringBuffer.append(',');
                stringBuffer.append(this.auth_params[i].getName());
                stringBuffer.append("=");
                if (this.auth_params[i].quoteValue()) {
                    stringBuffer.append('\"');
                    stringBuffer.append(Util.quoteString(this.auth_params[i].getValue(), "\\\""));
                    stringBuffer.append('\"');
                    continue;
                }
                stringBuffer.append(this.auth_params[i].getValue());
            }
        }
        return stringBuffer.toString();
    }

    public int hashCode() {
        return (this.Host + this.Scheme.toLowerCase() + this.Realm).hashCode();
    }

    public boolean equals(Object object) {
        if (object != null && object instanceof AuthorizationInfo) {
            AuthorizationInfo authorizationInfo = (AuthorizationInfo)object;
            if (this.Host.equals(authorizationInfo.Host) && this.Port == authorizationInfo.Port && this.Scheme.equalsIgnoreCase(authorizationInfo.Scheme) && this.Realm.equals(authorizationInfo.Realm)) {
                return true;
            }
        }
        return false;
    }

    public Object clone() {
        AuthorizationInfo authorizationInfo;
        try {
            authorizationInfo = (AuthorizationInfo)super.clone();
            authorizationInfo.auth_params = Util.resizeArray(this.auth_params, this.auth_params.length);
            try {
                authorizationInfo.extra_info = this.extra_info.getClass().getMethod("clone", null).invoke(this.extra_info, null);
            }
            catch (Throwable throwable) {
                // empty catch block
            }
            authorizationInfo.paths = new String[this.paths.length];
            System.arraycopy(this.paths, 0, authorizationInfo.paths, 0, this.paths.length);
        }
        catch (CloneNotSupportedException cloneNotSupportedException) {
            throw new InternalError(cloneNotSupportedException.toString());
        }
        return authorizationInfo;
    }

    static {
        CntxtList.put(HTTPConnection.getDefaultContext(), new Hashtable());
    }
}

