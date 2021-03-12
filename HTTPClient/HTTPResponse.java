/*
 * Decompiled with CFR 0.150.
 */
package HTTPClient;

import HTTPClient.CIHashtable;
import HTTPClient.GlobalConstants;
import HTTPClient.HTTPClientModule;
import HTTPClient.HTTPClientModuleConstants;
import HTTPClient.HttpOutputStream;
import HTTPClient.ModuleException;
import HTTPClient.ParseException;
import HTTPClient.Request;
import HTTPClient.Response;
import HTTPClient.RetryException;
import HTTPClient.URI;
import HTTPClient.Util;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InterruptedIOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;
import java.util.Enumeration;

public class HTTPResponse
implements GlobalConstants,
HTTPClientModuleConstants {
    private HTTPClientModule[] modules;
    private int timeout;
    private Request request = null;
    private Response response = null;
    private HttpOutputStream out_stream = null;
    private InputStream inp_stream;
    private int StatusCode;
    private String ReasonLine;
    private String Version;
    private URI OriginalURI = null;
    private URI EffectiveURI = null;
    private CIHashtable Headers = null;
    private CIHashtable Trailers = null;
    private int ContentLength = -1;
    private byte[] Data = null;
    private boolean initialized = false;
    private boolean got_trailers = false;
    private boolean interrupted = false;
    private boolean aborted = false;
    private boolean retry = false;
    private String method = null;
    private boolean handle_trailers = false;
    private boolean trailers_handled = false;

    HTTPResponse(HTTPClientModule[] arrhTTPClientModule, int n, Request request) {
        this.modules = arrhTTPClientModule;
        this.timeout = n;
        try {
            this.OriginalURI = new URI(request.getConnection().getProtocol(), request.getConnection().getHost(), request.getConnection().getPort(), request.getRequestURI());
        }
        catch (ParseException parseException) {
            // empty catch block
        }
        this.method = request.getMethod();
    }

    void set(Request request, Response response) {
        this.request = request;
        this.response = response;
        response.http_resp = this;
        response.timeout = this.timeout;
        this.aborted = response.final_resp;
    }

    void set(Request request, HttpOutputStream httpOutputStream) {
        this.request = request;
        this.out_stream = httpOutputStream;
    }

    public final int getStatusCode() throws IOException, ModuleException {
        if (!this.initialized) {
            this.handleResponse();
        }
        return this.StatusCode;
    }

    public final String getReasonLine() throws IOException, ModuleException {
        if (!this.initialized) {
            this.handleResponse();
        }
        return this.ReasonLine;
    }

    public final String getVersion() throws IOException, ModuleException {
        if (!this.initialized) {
            this.handleResponse();
        }
        return this.Version;
    }

    public final String getServer() throws IOException, ModuleException {
        if (!this.initialized) {
            this.handleResponse();
        }
        return this.getHeader("Server");
    }

    public final URL getOriginalURL() {
        try {
            return this.OriginalURI.toURL();
        }
        catch (MalformedURLException malformedURLException) {
            return null;
        }
    }

    public final URL getEffectiveURL() throws IOException, ModuleException {
        if (!this.initialized) {
            this.handleResponse();
        }
        if (this.EffectiveURI != null) {
            return this.EffectiveURI.toURL();
        }
        return this.OriginalURI.toURL();
    }

    public String getHeader(String string) throws IOException, ModuleException {
        if (!this.initialized) {
            this.handleResponse();
        }
        return (String)this.Headers.get(string.trim());
    }

    public int getHeaderAsInt(String string) throws IOException, ModuleException, NumberFormatException {
        return Integer.parseInt(this.getHeader(string));
    }

    public Date getHeaderAsDate(String string) throws IOException, IllegalArgumentException, ModuleException {
        Date date;
        String string2 = this.getHeader(string);
        if (string2 == null) {
            return null;
        }
        if (string2.toUpperCase().indexOf("GMT") == -1) {
            string2 = string2 + " GMT";
        }
        try {
            date = Util.parseDate(string2);
        }
        catch (IllegalArgumentException illegalArgumentException) {
            long l;
            try {
                l = Long.parseLong(string2);
            }
            catch (NumberFormatException numberFormatException) {
                throw illegalArgumentException;
            }
            if (l < 0L) {
                l = 0L;
            }
            date = new Date(l * 1000L);
        }
        return date;
    }

    public Enumeration listHeaders() throws IOException, ModuleException {
        if (!this.initialized) {
            this.handleResponse();
        }
        return this.Headers.keys();
    }

    public String getTrailer(String string) throws IOException, ModuleException {
        if (!this.got_trailers) {
            this.getTrailers();
        }
        return (String)this.Trailers.get(string.trim());
    }

    public int getTrailerAsInt(String string) throws IOException, ModuleException, NumberFormatException {
        return Integer.parseInt(this.getTrailer(string));
    }

    public Date getTrailerAsDate(String string) throws IOException, IllegalArgumentException, ModuleException {
        Date date;
        String string2 = this.getTrailer(string);
        if (string2 == null) {
            return null;
        }
        if (string2.toUpperCase().indexOf("GMT") == -1) {
            string2 = string2 + " GMT";
        }
        try {
            date = Util.parseDate(string2);
        }
        catch (IllegalArgumentException illegalArgumentException) {
            long l;
            try {
                l = Long.parseLong(string2);
            }
            catch (NumberFormatException numberFormatException) {
                throw illegalArgumentException;
            }
            if (l < 0L) {
                l = 0L;
            }
            date = new Date(l * 1000L);
        }
        return date;
    }

    public Enumeration listTrailers() throws IOException, ModuleException {
        if (!this.got_trailers) {
            this.getTrailers();
        }
        return this.Trailers.keys();
    }

    public synchronized byte[] getData() throws IOException, ModuleException {
        if (!this.initialized) {
            this.handleResponse();
        }
        if (this.Data == null || this.interrupted) {
            try {
                this.readResponseData(this.inp_stream);
            }
            catch (InterruptedIOException interruptedIOException) {
                throw interruptedIOException;
            }
            catch (IOException iOException) {
                try {
                    this.inp_stream.close();
                }
                catch (Exception exception) {
                    // empty catch block
                }
                throw iOException;
            }
            this.inp_stream.close();
        }
        return this.Data;
    }

    public synchronized String getText() throws IOException, ModuleException, ParseException {
        String string = this.getHeader("Content-Type");
        if (string == null || !string.toLowerCase().startsWith("text/")) {
            throw new IOException("Content-Type `" + string + "' is not a text type");
        }
        String string2 = Util.getParameter("charset", string);
        if (string2 == null) {
            string2 = "ISO-8859-1";
        }
        return new String(this.getData(), string2);
    }

    public synchronized InputStream getInputStream() throws IOException, ModuleException {
        if (!this.initialized) {
            this.handleResponse();
        }
        if (this.Data == null) {
            return this.inp_stream;
        }
        this.getData();
        return new ByteArrayInputStream(this.Data);
    }

    public boolean retryRequest() throws IOException, ModuleException {
        if (!this.initialized) {
            try {
                this.handleResponse();
            }
            catch (RetryException retryException) {
                this.retry = this.response.retry;
            }
        }
        return this.retry;
    }

    public String toString() {
        if (!this.initialized) {
            try {
                this.handleResponse();
            }
            catch (Exception exception) {
                return "Failed to read headers: " + exception;
            }
        }
        String string = System.getProperty("line.separator", "\n");
        StringBuffer stringBuffer = new StringBuffer(this.Version);
        stringBuffer.append(' ');
        stringBuffer.append(this.StatusCode);
        stringBuffer.append(' ');
        stringBuffer.append(this.ReasonLine);
        stringBuffer.append(string);
        if (this.EffectiveURI != null) {
            stringBuffer.append("Effective-URI: ");
            stringBuffer.append(this.EffectiveURI);
            stringBuffer.append(string);
        }
        Enumeration enumeration = this.Headers.keys();
        while (enumeration.hasMoreElements()) {
            String string2 = (String)enumeration.nextElement();
            stringBuffer.append(string2);
            stringBuffer.append(": ");
            stringBuffer.append(this.Headers.get(string2));
            stringBuffer.append(string);
        }
        return stringBuffer.toString();
    }

    HTTPClientModule[] getModules() {
        return this.modules;
    }

    /*
     * Unable to fully structure code
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     * Lifted jumps to return sites
     */
    synchronized boolean handleResponse() throws IOException, ModuleException {
        block21: {
            if (this.initialized) {
                return false;
            }
            if (this.out_stream != null) {
                this.response = this.out_stream.getResponse();
                this.response.http_resp = this;
                this.out_stream = null;
            }
            while (true) lbl-1000:
            // 6 sources

            {
                for (var1_1 = 0; var1_1 < this.modules.length && !this.aborted; ++var1_1) {
                    try {
                        this.modules[var1_1].responsePhase1Handler(this.response, this.request);
                        continue;
                    }
                    catch (RetryException var2_2) {
                        if (var2_2.restart == false) throw var2_2;
                        continue;
                    }
                }
                block11: for (var1_1 = 0; var1_1 < this.modules.length && !this.aborted; ++var1_1) {
                    var2_3 = this.modules[var1_1].responsePhase2Handler(this.response, this.request);
                    switch (var2_3) {
                        case 10: {
                            continue block11;
                        }
                        case 11: {
                            var1_1 = -1;
                            ** continue;
                        }
                        case 12: {
                            break block21;
                        }
                        case 13: 
                        case 15: {
                            this.response.getInputStream().close();
                            if (this.handle_trailers) {
                                this.invokeTrailerHandlers(true);
                            }
                            if (this.request.internal_subrequest) {
                                return true;
                            }
                            this.request.getConnection().handleRequest(this.request, this, this.response, true);
                            if (!this.initialized) {
                                var1_1 = -1;
                                ** continue;
                            }
                            break block21;
                        }
                        case 14: 
                        case 16: {
                            this.response.getInputStream().close();
                            if (this.handle_trailers) {
                                this.invokeTrailerHandlers(true);
                            }
                            if (this.request.internal_subrequest) {
                                return true;
                            }
                            this.request.getConnection().handleRequest(this.request, this, this.response, false);
                            var1_1 = -1;
                            ** continue;
                        }
                        default: {
                            throw new Error("HTTPClient Internal Error: invalid status " + var2_3 + " returned by module " + this.modules[var1_1].getClass().getName());
                        }
                    }
                }
                break;
            }
            for (var1_1 = 0; var1_1 < this.modules.length && !this.aborted; ++var1_1) {
                this.modules[var1_1].responsePhase3Handler(this.response, this.request);
            }
        }
        this.response.getStatusCode();
        if (!this.request.internal_subrequest) {
            this.init(this.response);
        }
        if (this.handle_trailers == false) return false;
        this.invokeTrailerHandlers(false);
        return false;
    }

    void init(Response response) {
        if (this.initialized) {
            return;
        }
        this.StatusCode = response.StatusCode;
        this.ReasonLine = response.ReasonLine;
        this.Version = response.Version;
        this.EffectiveURI = response.EffectiveURI;
        this.ContentLength = response.ContentLength;
        this.Headers = response.Headers;
        this.inp_stream = response.inp_stream;
        this.Data = response.Data;
        this.retry = response.retry;
        this.initialized = true;
    }

    void invokeTrailerHandlers(boolean bl) throws IOException, ModuleException {
        if (this.trailers_handled) {
            return;
        }
        if (!bl && !this.initialized) {
            this.handle_trailers = true;
            return;
        }
        for (int i = 0; i < this.modules.length && !this.aborted; ++i) {
            this.modules[i].trailerHandler(this.response, this.request);
        }
        this.trailers_handled = true;
    }

    void markAborted() {
        this.aborted = true;
    }

    private synchronized void getTrailers() throws IOException, ModuleException {
        if (this.got_trailers) {
            return;
        }
        if (!this.initialized) {
            this.handleResponse();
        }
        this.response.getTrailer("Any");
        this.Trailers = this.response.Trailers;
        this.got_trailers = true;
        this.invokeTrailerHandlers(false);
    }

    private void readResponseData(InputStream inputStream) throws IOException, ModuleException {
        if (this.Data == null) {
            this.Data = new byte[0];
        }
        int n = this.Data.length;
        try {
            this.interrupted = false;
            if (this.getHeader("Content-Length") != null && this.ContentLength != -1 && this.getHeader("Transfer-Encoding") == null) {
                int n2 = 0;
                this.Data = Util.resizeArray(this.Data, this.ContentLength);
                while ((n2 = inputStream.read(this.Data, n += n2, this.ContentLength - n)) != -1 && n + n2 < this.ContentLength) {
                }
                if (n2 == -1) {
                    this.Data = Util.resizeArray(this.Data, n);
                }
            } else {
                int n3 = 1000;
                int n4 = 0;
                do {
                    this.Data = Util.resizeArray(this.Data, (n += n4) + n3);
                } while ((n4 = inputStream.read(this.Data, n, n3)) != -1);
                this.Data = Util.resizeArray(this.Data, n);
            }
        }
        catch (InterruptedIOException interruptedIOException) {
            this.Data = Util.resizeArray(this.Data, n);
            this.interrupted = true;
            throw interruptedIOException;
        }
        catch (IOException iOException) {
            this.Data = Util.resizeArray(this.Data, n);
            throw iOException;
        }
        finally {
            if (!this.interrupted) {
                try {
                    inputStream.close();
                }
                catch (IOException iOException) {}
            }
        }
    }

    int getTimeout() {
        return this.timeout;
    }
}

