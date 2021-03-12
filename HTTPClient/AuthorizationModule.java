/*
 * Decompiled with CFR 0.150.
 */
package HTTPClient;

import HTTPClient.AuthSchemeNotImplException;
import HTTPClient.AuthorizationHandler;
import HTTPClient.AuthorizationInfo;
import HTTPClient.GlobalConstants;
import HTTPClient.HTTPClientModule;
import HTTPClient.HTTPConnection;
import HTTPClient.HttpOutputStream;
import HTTPClient.Request;
import HTTPClient.Response;
import HTTPClient.RoRequest;
import HTTPClient.RoResponse;
import HTTPClient.Util;
import java.io.IOException;
import java.net.ProtocolException;
import java.util.Hashtable;

class AuthorizationModule
implements HTTPClientModule,
GlobalConstants {
    private static Hashtable proxy_cntxt_list = new Hashtable();
    private static Hashtable deferred_auth_list = new Hashtable();
    private int auth_lst_idx = 0;
    private int prxy_lst_idx = 0;
    private int auth_scm_idx = 0;
    private int prxy_scm_idx = 0;
    private AuthorizationInfo auth_sent = null;
    private AuthorizationInfo prxy_sent = null;
    private boolean auth_from_4xx = false;
    private boolean prxy_from_4xx = false;
    private int num_tries = 0;
    private Request saved_req = null;
    private Response saved_resp = null;

    AuthorizationModule() {
    }

    public int requestHandler(Request request, Response[] arrresponse) throws IOException, AuthSchemeNotImplException {
        block12: {
            AuthorizationInfo authorizationInfo;
            block14: {
                AuthorizationHandler authorizationHandler;
                block11: {
                    block13: {
                        Hashtable hashtable;
                        HTTPConnection hTTPConnection = request.getConnection();
                        authorizationHandler = AuthorizationInfo.getAuthHandler();
                        HttpOutputStream httpOutputStream = request.getStream();
                        if (httpOutputStream != null && deferred_auth_list.get(httpOutputStream) != null) {
                            this.copyFrom((AuthorizationModule)deferred_auth_list.get(httpOutputStream));
                            request.copyFrom(this.saved_req);
                            deferred_auth_list.remove(httpOutputStream);
                            this.handle_auth_challenge(request, this.saved_resp);
                            return 1;
                        }
                        if (hTTPConnection.getProxyHost() == null || this.prxy_from_4xx || (authorizationInfo = (AuthorizationInfo)(hashtable = Util.getList(proxy_cntxt_list, request.getConnection().getContext())).get(hTTPConnection.getProxyHost() + ":" + hTTPConnection.getProxyPort())) == null) break block11;
                        if (authorizationHandler == null) break block13;
                        try {
                            authorizationInfo = authorizationHandler.fixupAuthInfo(authorizationInfo, request, null, null, true);
                        }
                        catch (AuthSchemeNotImplException authSchemeNotImplException) {
                            break block11;
                        }
                        if (authorizationInfo == null) break block11;
                    }
                    request.setHeaders(Util.setValue(request.getHeaders(), "Proxy-Authorization", authorizationInfo.toString()));
                    this.prxy_sent = authorizationInfo;
                    this.prxy_from_4xx = false;
                }
                if (this.auth_from_4xx || (authorizationInfo = AuthorizationInfo.findBest(request)) == null) break block12;
                if (authorizationHandler == null) break block14;
                try {
                    authorizationInfo = authorizationHandler.fixupAuthInfo(authorizationInfo, request, null, null, false);
                }
                catch (AuthSchemeNotImplException authSchemeNotImplException) {
                    break block12;
                }
                if (authorizationInfo == null) break block12;
            }
            request.setHeaders(Util.setValue(request.getHeaders(), "Authorization", authorizationInfo.toString()));
            this.auth_sent = authorizationInfo;
            this.auth_from_4xx = false;
        }
        return 0;
    }

    public void responsePhase1Handler(Response response, RoRequest roRequest) throws IOException {
        if (response.getStatusCode() != 401 && response.getStatusCode() != 407) {
            if (this.auth_sent != null && this.auth_from_4xx) {
                try {
                    AuthorizationInfo.getAuthorization(this.auth_sent, roRequest, response, false, false).addPath(roRequest.getRequestURI());
                }
                catch (AuthSchemeNotImplException authSchemeNotImplException) {
                    // empty catch block
                }
            }
            this.num_tries = 0;
        }
        this.auth_from_4xx = false;
        this.prxy_from_4xx = false;
    }

    public int responsePhase2Handler(Response response, Request request) throws IOException, AuthSchemeNotImplException {
        AuthorizationHandler authorizationHandler = AuthorizationInfo.getAuthHandler();
        if (authorizationHandler != null) {
            authorizationHandler.handleAuthHeaders(response, request, this.auth_sent, this.prxy_sent);
        }
        int n = response.getStatusCode();
        switch (n) {
            case 401: 
            case 407: {
                ++this.num_tries;
                if (this.num_tries > 10) {
                    throw new ProtocolException("Bug in authorization handling: server refused the given info 10 times");
                }
                if (request.getStream() != null) {
                    this.saved_req = (Request)request.clone();
                    this.saved_resp = (Response)response.clone();
                    deferred_auth_list.put(request.getStream(), this);
                    request.getStream().reset();
                    response.setRetryRequest(true);
                    return 10;
                }
                this.handle_auth_challenge(request, response);
                if (this.auth_sent == null && this.prxy_sent == null) {
                    return 10;
                }
                try {
                    response.getInputStream().close();
                }
                catch (IOException iOException) {
                    // empty catch block
                }
                return 13;
            }
        }
        return 10;
    }

    public void responsePhase3Handler(Response response, RoRequest roRequest) {
    }

    public void trailerHandler(Response response, RoRequest roRequest) throws IOException {
        AuthorizationHandler authorizationHandler = AuthorizationInfo.getAuthHandler();
        if (authorizationHandler != null) {
            authorizationHandler.handleAuthTrailers(response, roRequest, this.auth_sent, this.prxy_sent);
        }
    }

    private void handle_auth_challenge(Request request, Response response) throws AuthSchemeNotImplException, IOException {
        int[] arrn = new int[]{this.auth_lst_idx, this.auth_scm_idx};
        this.auth_sent = this.setAuthHeaders(response.getHeader("WWW-Authenticate"), request, response, "Authorization", false, arrn, this.auth_sent);
        if (this.auth_sent != null) {
            this.auth_from_4xx = true;
        }
        this.auth_lst_idx = arrn[0];
        this.auth_scm_idx = arrn[1];
        arrn[0] = this.prxy_lst_idx;
        arrn[1] = this.prxy_scm_idx;
        this.prxy_sent = this.setAuthHeaders(response.getHeader("Proxy-Authenticate"), request, response, "Proxy-Authorization", true, arrn, this.prxy_sent);
        if (this.prxy_sent != null) {
            this.prxy_from_4xx = true;
        }
        this.prxy_lst_idx = arrn[0];
        this.prxy_scm_idx = arrn[1];
        if (this.prxy_sent != null) {
            HTTPConnection hTTPConnection = request.getConnection();
            Util.getList(proxy_cntxt_list, hTTPConnection.getContext()).put(hTTPConnection.getProxyHost() + ":" + hTTPConnection.getProxyPort(), this.prxy_sent);
        }
        if (this.auth_sent == null && this.prxy_sent == null && response.getHeader("WWW-Authenticate") == null && response.getHeader("Proxy-Authenticate") == null) {
            if (response.getStatusCode() == 401) {
                throw new ProtocolException("Missing WWW-Authenticate header");
            }
            throw new ProtocolException("Missing Proxy-Authenticate header");
        }
    }

    private AuthorizationInfo setAuthHeaders(String string, Request request, RoResponse roResponse, String string2, boolean bl, int[] arrn, AuthorizationInfo authorizationInfo) throws ProtocolException, AuthSchemeNotImplException {
        if (string == null) {
            return null;
        }
        HTTPConnection hTTPConnection = request.getConnection();
        AuthorizationInfo[] arrauthorizationInfo = bl && hTTPConnection.getProxyHost() != null ? AuthorizationInfo.parseAuthString(string, hTTPConnection.getProxyHost(), hTTPConnection.getProxyPort()) : AuthorizationInfo.parseAuthString(string, hTTPConnection.getHost(), hTTPConnection.getPort());
        if (authorizationInfo != null && authorizationInfo.getScheme().equalsIgnoreCase("Basic")) {
            for (int i = 0; i < arrauthorizationInfo.length; ++i) {
                if (!authorizationInfo.getRealm().equals(arrauthorizationInfo[i].getRealm()) || !authorizationInfo.getScheme().equalsIgnoreCase(arrauthorizationInfo[i].getScheme())) continue;
                AuthorizationInfo.removeAuthorization(authorizationInfo, hTTPConnection.getContext());
            }
        }
        AuthorizationInfo authorizationInfo2 = null;
        AuthorizationHandler authorizationHandler = AuthorizationInfo.getAuthHandler();
        if (authorizationHandler != null && (arrauthorizationInfo = authorizationHandler.orderChallenges(arrauthorizationInfo, request, roResponse, bl)) == null) {
            return null;
        }
        while (authorizationInfo2 == null && arrn[0] != -1) {
            authorizationInfo2 = AuthorizationInfo.getAuthorization(arrauthorizationInfo[arrn[0]], request, roResponse, bl, false);
            if (authorizationHandler != null && authorizationInfo2 != null) {
                authorizationInfo2 = authorizationHandler.fixupAuthInfo(authorizationInfo2, request, arrauthorizationInfo[arrn[0]], roResponse, bl);
            }
            if ((arrn[0] = arrn[0] + 1) != arrauthorizationInfo.length) continue;
            arrn[0] = -1;
        }
        if (authorizationInfo2 == null) {
            for (int i = 0; i < arrauthorizationInfo.length; ++i) {
                try {
                    authorizationInfo2 = AuthorizationInfo.queryAuthHandler(arrauthorizationInfo[arrn[1]], request, roResponse, bl);
                    break;
                }
                catch (AuthSchemeNotImplException authSchemeNotImplException) {
                    if (i != arrauthorizationInfo.length - 1) continue;
                    throw authSchemeNotImplException;
                }
            }
            if ((arrn[1] = arrn[1] + 1) == arrauthorizationInfo.length) {
                arrn[1] = 0;
            }
        }
        if (authorizationInfo2 == null) {
            return null;
        }
        request.setHeaders(Util.setValue(request.getHeaders(), string2, authorizationInfo2.toString()));
        return authorizationInfo2;
    }

    private void copyFrom(AuthorizationModule authorizationModule) {
        this.auth_lst_idx = authorizationModule.auth_lst_idx;
        this.prxy_lst_idx = authorizationModule.prxy_lst_idx;
        this.auth_scm_idx = authorizationModule.auth_scm_idx;
        this.prxy_scm_idx = authorizationModule.prxy_scm_idx;
        this.auth_sent = authorizationModule.auth_sent;
        this.prxy_sent = authorizationModule.prxy_sent;
        this.auth_from_4xx = authorizationModule.auth_from_4xx;
        this.prxy_from_4xx = authorizationModule.prxy_from_4xx;
        this.num_tries = authorizationModule.num_tries;
        this.saved_req = authorizationModule.saved_req;
        this.saved_resp = authorizationModule.saved_resp;
    }
}

