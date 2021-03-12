/*
 * Decompiled with CFR 0.150.
 */
package HTTPClient;

import HTTPClient.HTTPClientModule;
import HTTPClient.ModuleException;
import HTTPClient.Request;
import HTTPClient.Response;
import HTTPClient.RoRequest;
import java.io.IOException;

public class HTTPClientModuleAdapter
implements HTTPClientModule {
    public int requestHandler(Request request, Response[] arrresponse) throws IOException, ModuleException {
        return 0;
    }

    public void responsePhase1Handler(Response response, RoRequest roRequest) throws IOException, ModuleException {
    }

    public int responsePhase2Handler(Response response, Request request) throws IOException, ModuleException {
        return 10;
    }

    public void responsePhase3Handler(Response response, RoRequest roRequest) throws IOException, ModuleException {
    }

    public void trailerHandler(Response response, RoRequest roRequest) throws IOException, ModuleException {
    }
}

