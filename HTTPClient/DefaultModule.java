/*
 * Decompiled with CFR 0.150.
 */
package HTTPClient;

import HTTPClient.GlobalConstants;
import HTTPClient.HTTPClientModule;
import HTTPClient.Request;
import HTTPClient.Response;
import HTTPClient.RoRequest;
import java.io.IOException;
import java.net.ProtocolException;

class DefaultModule
implements HTTPClientModule,
GlobalConstants {
    private int req_timeout_retries = 3;

    DefaultModule() {
    }

    public int requestHandler(Request request, Response[] arrresponse) {
        return 0;
    }

    public void responsePhase1Handler(Response response, RoRequest roRequest) {
    }

    public int responsePhase2Handler(Response response, Request request) throws IOException {
        int n = response.getStatusCode();
        switch (n) {
            case 408: {
                if (this.req_timeout_retries-- == 0 || request.getStream() != null) {
                    return 10;
                }
                return 13;
            }
            case 411: {
                if (request.getStream() != null && request.getStream().getLength() == -1) {
                    return 10;
                }
                try {
                    response.getInputStream().close();
                }
                catch (IOException iOException) {
                    // empty catch block
                }
                if (request.getData() != null) {
                    throw new ProtocolException("Received status code 411 even though Content-Length was sent");
                }
                request.setData(new byte[0]);
                return 13;
            }
            case 505: {
                return 10;
            }
        }
        return 10;
    }

    public void responsePhase3Handler(Response response, RoRequest roRequest) {
    }

    public void trailerHandler(Response response, RoRequest roRequest) {
    }
}

