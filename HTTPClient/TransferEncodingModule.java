/*
 * Decompiled with CFR 0.150.
 */
package HTTPClient;

import HTTPClient.ChunkedInputStream;
import HTTPClient.GlobalConstants;
import HTTPClient.HTTPClientModule;
import HTTPClient.HttpHeaderElement;
import HTTPClient.ModuleException;
import HTTPClient.NVPair;
import HTTPClient.ParseException;
import HTTPClient.Request;
import HTTPClient.Response;
import HTTPClient.RoRequest;
import HTTPClient.UncompressInputStream;
import HTTPClient.Util;
import java.io.IOException;
import java.util.Vector;
import java.util.zip.GZIPInputStream;
import java.util.zip.InflaterInputStream;

class TransferEncodingModule
implements HTTPClientModule,
GlobalConstants {
    TransferEncodingModule() {
    }

    public int requestHandler(Request request, Response[] arrresponse) throws ModuleException {
        Vector<HttpHeaderElement> vector;
        NVPair[] arrnVPair = request.getHeaders();
        int n = Util.getIndex(arrnVPair, "TE");
        if (n == -1) {
            n = arrnVPair.length;
            arrnVPair = Util.resizeArray(arrnVPair, n + 1);
            request.setHeaders(arrnVPair);
            vector = new Vector<HttpHeaderElement>();
        } else {
            try {
                vector = Util.parseHeader(arrnVPair[n].getValue());
            }
            catch (ParseException parseException) {
                throw new ModuleException(parseException.toString());
            }
        }
        HttpHeaderElement httpHeaderElement = Util.getElement(vector, "*");
        if (httpHeaderElement != null) {
            NVPair[] arrnVPair2 = httpHeaderElement.getParams();
            for (n = 0; n < arrnVPair2.length && !arrnVPair2[n].getName().equalsIgnoreCase("q"); ++n) {
            }
            if (n == arrnVPair2.length) {
                return 0;
            }
            if (arrnVPair2[n].getValue() == null || arrnVPair2[n].getValue().length() == 0) {
                throw new ModuleException("Invalid q value for \"*\" in TE header: ");
            }
            try {
                if ((double)Float.valueOf(arrnVPair2[n].getValue()).floatValue() > 0.0) {
                    return 0;
                }
            }
            catch (NumberFormatException numberFormatException) {
                throw new ModuleException("Invalid q value for \"*\" in TE header: " + numberFormatException.getMessage());
            }
        }
        if (!vector.contains(new HttpHeaderElement("deflate"))) {
            vector.addElement(new HttpHeaderElement("deflate"));
        }
        if (!vector.contains(new HttpHeaderElement("gzip"))) {
            vector.addElement(new HttpHeaderElement("gzip"));
        }
        if (!vector.contains(new HttpHeaderElement("compress"))) {
            vector.addElement(new HttpHeaderElement("compress"));
        }
        arrnVPair[n] = new NVPair("TE", Util.assembleHeader(vector));
        return 0;
    }

    public void responsePhase1Handler(Response response, RoRequest roRequest) {
    }

    public int responsePhase2Handler(Response response, Request request) {
        return 10;
    }

    public void responsePhase3Handler(Response response, RoRequest roRequest) throws IOException, ModuleException {
        Vector vector;
        String string = response.getHeader("Transfer-Encoding");
        if (string == null || roRequest.getMethod().equals("HEAD")) {
            return;
        }
        try {
            vector = Util.parseHeader(string);
        }
        catch (ParseException parseException) {
            throw new ModuleException(parseException.toString());
        }
        while (vector.size() > 0) {
            String string2 = ((HttpHeaderElement)vector.lastElement()).getName();
            if (string2.equalsIgnoreCase("gzip")) {
                response.inp_stream = new GZIPInputStream(response.inp_stream);
            } else if (string2.equalsIgnoreCase("deflate")) {
                response.inp_stream = new InflaterInputStream(response.inp_stream);
            } else if (string2.equalsIgnoreCase("compress")) {
                response.inp_stream = new UncompressInputStream(response.inp_stream);
            } else if (string2.equalsIgnoreCase("chunked")) {
                response.inp_stream = new ChunkedInputStream(response.inp_stream);
            } else if (!string2.equalsIgnoreCase("identity")) break;
            vector.removeElementAt(vector.size() - 1);
        }
        if (vector.size() > 0) {
            response.setHeader("Transfer-Encoding", Util.assembleHeader(vector));
        } else {
            response.deleteHeader("Transfer-Encoding");
        }
    }

    public void trailerHandler(Response response, RoRequest roRequest) {
    }
}

