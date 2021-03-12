/*
 * Decompiled with CFR 0.150.
 */
package HTTPClient;

import HTTPClient.GlobalConstants;
import HTTPClient.HTTPClientModule;
import HTTPClient.ModuleException;
import HTTPClient.Request;
import HTTPClient.Response;
import HTTPClient.RoRequest;
import java.io.IOException;
import java.util.Date;
import java.util.Hashtable;

public class RetryAfterModule
implements HTTPClientModule,
GlobalConstants {
    private static int threshold;
    private static Hashtable retry_list;
    private int delay = -1;

    RetryAfterModule() {
    }

    public int requestHandler(Request request, Response[] arrresponse) {
        Object v;
        if (request.getStream() != null && (v = retry_list.get(request.getStream())) != null) {
            this.delay = (Integer)v;
            retry_list.remove(request.getStream());
        }
        if (this.delay > threshold) {
            return 4;
        }
        if (this.delay >= 0) {
            try {
                Thread.sleep((long)this.delay * 1000L);
            }
            catch (InterruptedException interruptedException) {
                // empty catch block
            }
            this.delay = -1;
        }
        return 0;
    }

    public void responsePhase1Handler(Response response, RoRequest roRequest) throws IOException, ModuleException {
        this.delay = -1;
        if (response.getHeader("Retry-After") == null) {
            return;
        }
        try {
            this.delay = response.getHeaderAsInt("Retry-After");
        }
        catch (NumberFormatException numberFormatException) {
            Date date;
            Date date2;
            try {
                date2 = response.getHeaderAsDate("Retry-After");
            }
            catch (IllegalArgumentException illegalArgumentException) {
                throw new ModuleException("Illegal value in Retry-After header: '" + response.getHeader("Retry-After") + "'");
            }
            if (date2 == null) {
                return;
            }
            try {
                date = response.getHeaderAsDate("Date");
            }
            catch (IllegalArgumentException illegalArgumentException) {
                throw new ModuleException("Illegal value in Date header: '" + response.getHeader("Date") + "'");
            }
            if (date == null) {
                date = new Date();
            }
            this.delay = (int)((date2.getTime() - date.getTime()) / 1000L);
        }
    }

    public int responsePhase2Handler(Response response, Request request) throws IOException {
        if (response.getStatusCode() == 503 && this.delay >= 0) {
            if (this.delay > threshold) {
                return 10;
            }
            if (request.getStream() != null) {
                retry_list.put(request.getStream(), new Integer(this.delay));
                request.getStream().reset();
                response.setRetryRequest(true);
                return 10;
            }
            return 13;
        }
        return 10;
    }

    public void responsePhase3Handler(Response response, RoRequest roRequest) {
    }

    public void trailerHandler(Response response, RoRequest roRequest) {
    }

    public static void setThreshold(int n) {
        threshold = n;
    }

    static {
        retry_list = new Hashtable();
        try {
            threshold = Integer.getInteger("HTTPClient.retryafter.threshold", 30);
        }
        catch (Exception exception) {
            threshold = 30;
        }
    }
}

