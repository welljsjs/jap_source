/*
 * Decompiled with CFR 0.150.
 */
package HTTPClient;

import HTTPClient.GlobalConstants;
import HTTPClient.HTTPClientModule;
import HTTPClient.HTTPConnection;
import HTTPClient.IdempotentSequence;
import HTTPClient.ModuleException;
import HTTPClient.ParseException;
import HTTPClient.Request;
import HTTPClient.Response;
import HTTPClient.RetryException;
import HTTPClient.RoRequest;
import HTTPClient.Util;
import java.io.IOException;

class RetryModule
implements HTTPClientModule,
GlobalConstants {
    RetryModule() {
    }

    public int requestHandler(Request request, Response[] arrresponse) {
        return 0;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void responsePhase1Handler(Response response, RoRequest roRequest) throws IOException, ModuleException {
        try {
            response.getStatusCode();
        }
        catch (RetryException retryException) {
            boolean bl = false;
            try {
                RetryException retryException2 = retryException.first;
                synchronized (retryException2) {
                    bl = true;
                    IdempotentSequence idempotentSequence = new IdempotentSequence();
                    RetryException retryException3 = retryException.first;
                    while (retryException3 != null) {
                        idempotentSequence.add(retryException3.request);
                        retryException3 = retryException3.next;
                    }
                    retryException3 = retryException.first;
                    while (retryException3 != null) {
                        Request request = retryException3.request;
                        HTTPConnection hTTPConnection = request.getConnection();
                        if (Thread.currentThread().isInterrupted() || !idempotentSequence.isIdempotent(request) || hTTPConnection.ServProtVersKnown && hTTPConnection.ServerProtocolVersion >= 65537 && request.num_retries > 0 || (!hTTPConnection.ServProtVersKnown || hTTPConnection.ServerProtocolVersion <= 65536) && request.num_retries > 4 || retryException3.response.got_headers) {
                            retryException3.first = null;
                        } else if (request.getStream() != null) {
                            retryException3.first = null;
                            request.getStream().reset();
                            retryException3.response.setRetryRequest(true);
                        } else {
                            if (request.getData() != null && retryException3.conn_reset) {
                                if (hTTPConnection.ServProtVersKnown && hTTPConnection.ServerProtocolVersion >= 65537) {
                                    request.setHeaders(Util.addToken(request.getHeaders(), "Expect", "100-continue"));
                                } else {
                                    request.delay_entity = 5000L << request.num_retries;
                                }
                            }
                            if (retryException3.next != null && retryException3.next.request.getData() != null && (!hTTPConnection.ServProtVersKnown || hTTPConnection.ServerProtocolVersion < 65537) && retryException3.conn_reset) {
                                request.setHeaders(Util.addToken(request.getHeaders(), "Connection", "close"));
                            }
                            if (hTTPConnection.ServProtVersKnown && hTTPConnection.ServerProtocolVersion >= 65537 && retryException3.conn_reset) {
                                request.dont_pipeline = true;
                            }
                            if (retryException3.conn_reset) {
                                ++request.num_retries;
                            }
                            retryException3.response.http_resp.set(request, hTTPConnection.sendRequest(request, retryException3.response.timeout));
                            retryException3.exception = null;
                            retryException3.first = null;
                        }
                        retryException3 = retryException3.next;
                    }
                }
            }
            catch (NullPointerException nullPointerException) {
                if (bl) {
                    throw nullPointerException;
                }
            }
            catch (ParseException parseException) {
                throw new IOException(parseException.getMessage());
            }
            if (retryException.exception != null) {
                throw retryException.exception;
            }
            retryException.restart = true;
            throw retryException;
        }
    }

    public int responsePhase2Handler(Response response, Request request) {
        request.delay_entity = 0L;
        request.dont_pipeline = false;
        request.num_retries = 0;
        return 10;
    }

    public void responsePhase3Handler(Response response, RoRequest roRequest) {
    }

    public void trailerHandler(Response response, RoRequest roRequest) {
    }
}

