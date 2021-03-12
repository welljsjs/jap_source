/*
 * Decompiled with CFR 0.150.
 */
package HTTPClient;

import HTTPClient.GlobalConstants;
import HTTPClient.HTTPClientModule;
import HTTPClient.HTTPConnection;
import HTTPClient.HttpOutputStream;
import HTTPClient.ParseException;
import HTTPClient.ProtocolNotSuppException;
import HTTPClient.Request;
import HTTPClient.Response;
import HTTPClient.RoRequest;
import HTTPClient.URI;
import HTTPClient.Util;
import java.io.IOException;
import java.net.ProtocolException;
import java.util.Hashtable;

class RedirectionModule
implements HTTPClientModule,
GlobalConstants {
    private static Hashtable perm_redir_cntxt_list = new Hashtable();
    private static Hashtable deferred_redir_list = new Hashtable();
    private int level = 0;
    private URI lastURI = null;
    private boolean new_con;
    private Request saved_req = null;

    RedirectionModule() {
    }

    public int requestHandler(Request request, Response[] arrresponse) {
        URI uRI;
        HTTPConnection hTTPConnection = request.getConnection();
        HttpOutputStream httpOutputStream = request.getStream();
        if (httpOutputStream != null && deferred_redir_list.get(httpOutputStream) != null) {
            this.copyFrom((RedirectionModule)deferred_redir_list.get(httpOutputStream));
            request.copyFrom(this.saved_req);
            deferred_redir_list.remove(httpOutputStream);
            if (this.new_con) {
                return 5;
            }
            return 1;
        }
        try {
            uRI = new URI(hTTPConnection.getProtocol(), hTTPConnection.getHost(), hTTPConnection.getPort(), request.getRequestURI());
        }
        catch (ParseException parseException) {
            uRI = null;
        }
        Hashtable hashtable = Util.getList(perm_redir_cntxt_list, request.getConnection().getContext());
        URI uRI2 = (URI)hashtable.get(uRI);
        if (uRI2 != null) {
            String string = uRI2.getPath();
            request.setRequestURI(string);
            try {
                this.lastURI = new URI(uRI2, string);
            }
            catch (ParseException parseException) {
                // empty catch block
            }
            if (!this.sameServer(hTTPConnection, uRI2)) {
                try {
                    hTTPConnection = new HTTPConnection(uRI2);
                }
                catch (ProtocolNotSuppException protocolNotSuppException) {
                    throw new Error("HTTPClient Internal Error: unexpected exception '" + protocolNotSuppException + "'");
                }
                hTTPConnection.setContext(request.getConnection().getContext());
                request.setConnection(hTTPConnection);
                return 5;
            }
            return 1;
        }
        return 0;
    }

    public void responsePhase1Handler(Response response, RoRequest roRequest) throws IOException {
        int n = response.getStatusCode();
        if ((n < 301 || n > 307 || n == 304) && this.lastURI != null) {
            response.setEffectiveURI(this.lastURI);
        }
    }

    public int responsePhase2Handler(Response response, Request request) throws IOException {
        int n = response.getStatusCode();
        switch (n) {
            case 302: {
                n = 303;
            }
            case 301: 
            case 303: 
            case 307: {
                if (!request.getMethod().equals("GET") && !request.getMethod().equals("HEAD") && n != 303) {
                    if (n == 301 && response.getHeader("Location") != null) {
                        RedirectionModule.update_perm_redir_list(request, this.resLocHdr(response.getHeader("Location"), request));
                    }
                    response.setEffectiveURI(this.lastURI);
                    return 10;
                }
            }
            case 305: 
            case 306: {
                String string;
                HTTPConnection hTTPConnection;
                if (n == 305 && request.getConnection().getProxyHost() != null) {
                    response.setEffectiveURI(this.lastURI);
                    return 10;
                }
                if (this.level == 15 || response.getHeader("Location") == null) {
                    response.setEffectiveURI(this.lastURI);
                    return 10;
                }
                ++this.level;
                URI uRI = this.resLocHdr(response.getHeader("Location"), request);
                this.new_con = false;
                if (n == 305) {
                    hTTPConnection = new HTTPConnection(request.getConnection().getProtocol(), request.getConnection().getHost(), request.getConnection().getPort());
                    hTTPConnection.setCurrentProxy(uRI.getHost(), uRI.getPort());
                    hTTPConnection.setContext(request.getConnection().getContext());
                    this.new_con = true;
                    string = request.getRequestURI();
                    request.setMethod("GET");
                    request.setData(null);
                    request.setStream(null);
                } else {
                    if (n == 306) {
                        return 10;
                    }
                    if (this.sameServer(request.getConnection(), uRI)) {
                        hTTPConnection = request.getConnection();
                        string = uRI.getPath();
                    } else {
                        try {
                            hTTPConnection = new HTTPConnection(uRI);
                            string = uRI.getPath();
                        }
                        catch (ProtocolNotSuppException protocolNotSuppException) {
                            if (request.getConnection().getProxyHost() == null || !uRI.getScheme().equalsIgnoreCase("ftp")) {
                                return 10;
                            }
                            hTTPConnection = new HTTPConnection("http", request.getConnection().getProxyHost(), request.getConnection().getProxyPort());
                            hTTPConnection.setCurrentProxy(null, 0);
                            string = uRI.toExternalForm();
                        }
                        hTTPConnection.setContext(request.getConnection().getContext());
                        this.new_con = true;
                    }
                    if (n == 303 && !request.getMethod().equals("HEAD")) {
                        request.setMethod("GET");
                        request.setData(null);
                        request.setStream(null);
                    } else {
                        if (request.getStream() != null) {
                            this.saved_req = (Request)request.clone();
                            deferred_redir_list.put(request.getStream(), this);
                            request.getStream().reset();
                            response.setRetryRequest(true);
                        }
                        if (n == 301) {
                            try {
                                RedirectionModule.update_perm_redir_list(request, new URI(uRI, string));
                            }
                            catch (ParseException parseException) {
                                // empty catch block
                            }
                        }
                    }
                    Util.updateValue(request.getHeaders(), "Referer", request.getConnection() + request.getRequestURI());
                }
                request.setConnection(hTTPConnection);
                request.setRequestURI(string);
                try {
                    response.getInputStream().close();
                }
                catch (IOException iOException) {
                    // empty catch block
                }
                if (n != 305 && n != 306) {
                    try {
                        this.lastURI = new URI(uRI, string);
                    }
                    catch (ParseException parseException) {
                        // empty catch block
                    }
                }
                if (request.getStream() != null) {
                    return 10;
                }
                if (this.new_con) {
                    return 15;
                }
                return 13;
            }
        }
        return 10;
    }

    public void responsePhase3Handler(Response response, RoRequest roRequest) {
    }

    public void trailerHandler(Response response, RoRequest roRequest) {
    }

    private static void update_perm_redir_list(RoRequest roRequest, URI uRI) {
        HTTPConnection hTTPConnection = roRequest.getConnection();
        URI uRI2 = null;
        try {
            uRI2 = new URI(hTTPConnection.getProtocol(), hTTPConnection.getHost(), hTTPConnection.getPort(), roRequest.getRequestURI());
        }
        catch (ParseException parseException) {
            // empty catch block
        }
        if (!uRI2.equals(uRI)) {
            Hashtable hashtable = Util.getList(perm_redir_cntxt_list, hTTPConnection.getContext());
            hashtable.put(uRI2, uRI);
        }
    }

    private URI resLocHdr(String string, RoRequest roRequest) throws ProtocolException {
        try {
            return new URI(string);
        }
        catch (ParseException parseException) {
            try {
                URI uRI = new URI(roRequest.getConnection().getProtocol(), roRequest.getConnection().getHost(), roRequest.getConnection().getPort(), roRequest.getRequestURI());
                return new URI(uRI, string);
            }
            catch (ParseException parseException2) {
                throw new ProtocolException("Malformed URL in Location header: " + string);
            }
        }
    }

    private boolean sameServer(HTTPConnection hTTPConnection, URI uRI) {
        if (!uRI.getScheme().equalsIgnoreCase(hTTPConnection.getProtocol())) {
            return false;
        }
        if (!uRI.getHost().equalsIgnoreCase(hTTPConnection.getHost())) {
            return false;
        }
        return uRI.getPort() == hTTPConnection.getPort();
    }

    private void copyFrom(RedirectionModule redirectionModule) {
        this.level = redirectionModule.level;
        this.lastURI = redirectionModule.lastURI;
        this.saved_req = redirectionModule.saved_req;
    }
}

