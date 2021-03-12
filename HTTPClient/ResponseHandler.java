/*
 * Decompiled with CFR 0.150.
 */
package HTTPClient;

import HTTPClient.DemultiplexorInputStream;
import HTTPClient.GlobalConstants;
import HTTPClient.ParseException;
import HTTPClient.Request;
import HTTPClient.RespInputStream;
import HTTPClient.Response;
import HTTPClient.StreamDemultiplexor;
import HTTPClient.Util;
import java.io.IOException;

final class ResponseHandler
implements GlobalConstants {
    RespInputStream stream;
    Response resp;
    Request request;
    boolean eof = false;
    IOException exception = null;
    private boolean set_terminator = false;

    ResponseHandler(Response response, Request request, StreamDemultiplexor streamDemultiplexor) {
        this.resp = response;
        this.request = request;
        this.stream = new RespInputStream(streamDemultiplexor, this);
    }

    public void setupBoundary(DemultiplexorInputStream demultiplexorInputStream) throws IOException, ParseException {
        if (this.set_terminator) {
            return;
        }
        String string = "--" + Util.getParameter("boundary", this.resp.getHeader("Content-Type")) + "--\r\n";
        byte[] arrby = string.getBytes();
        int[] arrn = Util.compile_search(arrby);
        demultiplexorInputStream.setTerminator(arrby, arrn);
        this.set_terminator = true;
    }
}

