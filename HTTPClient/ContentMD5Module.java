/*
 * Decompiled with CFR 0.150.
 */
package HTTPClient;

import HTTPClient.GlobalConstants;
import HTTPClient.HTTPClientModule;
import HTTPClient.MD5InputStream;
import HTTPClient.ModuleException;
import HTTPClient.ParseException;
import HTTPClient.Request;
import HTTPClient.Response;
import HTTPClient.RoRequest;
import HTTPClient.Util;
import HTTPClient.VerifyMD5;
import java.io.IOException;

class ContentMD5Module
implements HTTPClientModule,
GlobalConstants {
    ContentMD5Module() {
    }

    public int requestHandler(Request request, Response[] arrresponse) {
        return 0;
    }

    public void responsePhase1Handler(Response response, RoRequest roRequest) {
    }

    public int responsePhase2Handler(Response response, Request request) {
        return 10;
    }

    public void responsePhase3Handler(Response response, RoRequest roRequest) throws IOException, ModuleException {
        if (roRequest.getMethod().equals("HEAD")) {
            return;
        }
        String string = response.getHeader("Content-MD5");
        String string2 = response.getHeader("Trailer");
        boolean bl = false;
        try {
            if (string2 != null) {
                bl = Util.hasToken(string2, "Content-MD5");
            }
        }
        catch (ParseException parseException) {
            throw new ModuleException(parseException.toString());
        }
        if (string == null && !bl || response.getHeader("Transfer-Encoding") != null) {
            return;
        }
        response.inp_stream = new MD5InputStream(response.inp_stream, new VerifyMD5(response));
    }

    public void trailerHandler(Response response, RoRequest roRequest) {
    }
}

