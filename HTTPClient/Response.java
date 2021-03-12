/*
 * Decompiled with CFR 0.150.
 */
package HTTPClient;

import HTTPClient.CIHashtable;
import HTTPClient.GlobalConstants;
import HTTPClient.HTTPConnection;
import HTTPClient.HTTPResponse;
import HTTPClient.HttpHeaderElement;
import HTTPClient.ModuleException;
import HTTPClient.NVPair;
import HTTPClient.ParseException;
import HTTPClient.Request;
import HTTPClient.RespInputStream;
import HTTPClient.RoResponse;
import HTTPClient.StreamDemultiplexor;
import HTTPClient.URI;
import HTTPClient.Util;
import java.io.ByteArrayInputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InterruptedIOException;
import java.io.SequenceInputStream;
import java.net.ProtocolException;
import java.util.Date;
import java.util.Vector;

public final class Response
implements RoResponse,
GlobalConstants,
Cloneable {
    private HTTPConnection connection;
    private StreamDemultiplexor stream_handler;
    HTTPResponse http_resp;
    int timeout = 0;
    public InputStream inp_stream;
    private RespInputStream resp_inp_stream = null;
    private String method;
    String resource;
    private boolean used_proxy;
    private boolean sent_entity;
    int StatusCode = 0;
    String ReasonLine;
    String Version;
    URI EffectiveURI = null;
    CIHashtable Headers = new CIHashtable();
    CIHashtable Trailers = new CIHashtable();
    int ContentLength = -1;
    int cd_type = 0;
    byte[] Data = null;
    boolean reading_headers = false;
    boolean got_headers = false;
    boolean got_trailers = false;
    private boolean interrupted = false;
    private IOException exception = null;
    boolean final_resp = false;
    boolean retry = false;
    private byte[] buf = new byte[600];
    private char[] hdrs = new char[600];
    private int buf_pos = 0;
    private int hdr_pos = 0;
    private boolean reading_lines = false;
    char[] trailers;
    Request req = null;
    boolean isFirstResponse = false;

    Response(Request request, boolean bl, StreamDemultiplexor streamDemultiplexor) throws IOException {
        this.connection = request.getConnection();
        this.method = request.getMethod();
        this.resource = request.getRequestURI();
        this.used_proxy = bl;
        this.stream_handler = streamDemultiplexor;
        this.sent_entity = request.getData() != null;
        streamDemultiplexor.register(this, request);
        this.resp_inp_stream = streamDemultiplexor.getStream(this);
        this.inp_stream = this.resp_inp_stream;
    }

    Response(Request request, InputStream inputStream) {
        this.connection = request.getConnection();
        this.method = request.getMethod();
        this.resource = request.getRequestURI();
        this.used_proxy = false;
        this.stream_handler = null;
        this.sent_entity = request.getData() != null;
        this.inp_stream = inputStream;
    }

    public Response(String string, int n, String string2, NVPair[] arrnVPair, byte[] arrby, InputStream inputStream, int n2) {
        this.Version = string;
        this.StatusCode = n;
        this.ReasonLine = string2;
        if (arrnVPair != null) {
            for (int i = 0; i < arrnVPair.length; ++i) {
                this.setHeader(arrnVPair[i].getName(), arrnVPair[i].getValue());
            }
        }
        if (arrby != null) {
            this.Data = arrby;
        } else if (inputStream == null) {
            this.Data = new byte[0];
        } else {
            this.inp_stream = inputStream;
            this.ContentLength = n2;
        }
        this.got_headers = true;
        this.got_trailers = true;
    }

    public final int getStatusCode() throws IOException {
        if (!this.got_headers) {
            this.getHeaders(true);
        }
        return this.StatusCode;
    }

    public final String getReasonLine() throws IOException {
        if (!this.got_headers) {
            this.getHeaders(true);
        }
        return this.ReasonLine;
    }

    public final String getVersion() throws IOException {
        if (!this.got_headers) {
            this.getHeaders(true);
        }
        return this.Version;
    }

    int getContinue() throws IOException {
        this.getHeaders(false);
        return this.StatusCode;
    }

    public final URI getEffectiveURI() throws IOException {
        if (!this.got_headers) {
            this.getHeaders(true);
        }
        return this.EffectiveURI;
    }

    public void setEffectiveURI(URI uRI) {
        this.EffectiveURI = uRI;
    }

    public String getHeader(String string) throws IOException {
        if (!this.got_headers) {
            this.getHeaders(true);
        }
        return (String)this.Headers.get(string.trim());
    }

    public int getHeaderAsInt(String string) throws IOException, NumberFormatException {
        return Integer.parseInt(this.getHeader(string));
    }

    public Date getHeaderAsDate(String string) throws IOException, IllegalArgumentException {
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

    public void setHeader(String string, String string2) {
        this.Headers.put(string.trim(), string2.trim());
    }

    public void deleteHeader(String string) {
        this.Headers.remove(string.trim());
    }

    public String getTrailer(String string) throws IOException {
        if (!this.got_trailers) {
            this.getTrailers();
        }
        return (String)this.Trailers.get(string.trim());
    }

    public int getTrailerAsInt(String string) throws IOException, NumberFormatException {
        return Integer.parseInt(this.getTrailer(string));
    }

    public Date getTrailerAsDate(String string) throws IOException, IllegalArgumentException {
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

    public void setTrailer(String string, String string2) {
        this.Trailers.put(string.trim(), string2.trim());
    }

    public void deleteTrailer(String string) {
        this.Trailers.remove(string.trim());
    }

    public synchronized byte[] getData() throws IOException {
        if (!this.got_headers) {
            this.getHeaders(true);
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

    public synchronized InputStream getInputStream() throws IOException {
        if (!this.got_headers) {
            this.getHeaders(true);
        }
        if (this.Data == null) {
            return this.inp_stream;
        }
        return new ByteArrayInputStream(this.Data);
    }

    public synchronized boolean hasEntity() throws IOException {
        if (!this.got_headers) {
            this.getHeaders(true);
        }
        return this.cd_type != 2;
    }

    public void setRetryRequest(boolean bl) {
        this.retry = bl;
    }

    public boolean retryRequest() {
        return this.retry;
    }

    private synchronized void getHeaders(boolean bl) throws IOException {
        Object object;
        if (this.got_headers) {
            return;
        }
        if (this.exception != null) {
            throw (IOException)this.exception.fillInStackTrace();
        }
        this.reading_headers = true;
        try {
            do {
                this.Headers.clear();
                char[] arrc = this.readResponseHeaders(this.inp_stream);
                this.parseResponseHeaders(arrc);
            } while (this.StatusCode == 100 && bl || this.StatusCode > 101 && this.StatusCode < 200);
        }
        catch (IOException iOException) {
            if (!(iOException instanceof InterruptedIOException)) {
                this.exception = iOException;
            }
            if (iOException instanceof ProtocolException) {
                this.cd_type = 3;
                if (this.stream_handler != null) {
                    this.stream_handler.markForClose(this);
                }
            }
            throw iOException;
        }
        finally {
            this.reading_headers = false;
        }
        if (this.StatusCode == 100) {
            return;
        }
        this.got_headers = true;
        boolean bl2 = false;
        boolean bl3 = true;
        boolean bl4 = false;
        Vector vector = null;
        try {
            vector = Util.parseHeader(this.getHeader("Transfer-Encoding"));
        }
        catch (ParseException parseException) {
            // empty catch block
        }
        if (vector != null) {
            bl2 = ((HttpHeaderElement)vector.lastElement()).getName().equalsIgnoreCase("chunked");
            for (int i = 0; i < vector.size(); ++i) {
                if (((HttpHeaderElement)vector.elementAt(i)).getName().equalsIgnoreCase("identity")) {
                    vector.removeElementAt(i--);
                    continue;
                }
                bl3 = false;
            }
        }
        try {
            object = this.getHeader("Content-Type");
            if (object != null) {
                Vector vector2 = Util.parseHeader((String)object);
                bl4 = vector2.contains(new HttpHeaderElement("multipart/byteranges")) || vector2.contains(new HttpHeaderElement("multipart/x-byteranges"));
            }
        }
        catch (ParseException parseException) {
            // empty catch block
        }
        if (this.method.equals("HEAD") || this.ContentLength == 0 || this.StatusCode < 200 || this.StatusCode == 204 || this.StatusCode == 205 || this.StatusCode == 304) {
            this.Data = new byte[0];
            this.cd_type = 2;
            this.inp_stream.close();
        } else if (bl2) {
            this.cd_type = 5;
            vector.removeElementAt(vector.size() - 1);
            if (vector.size() > 0) {
                this.setHeader("Transfer-Encoding", Util.assembleHeader(vector));
            } else {
                this.deleteHeader("Transfer-Encoding");
            }
        } else if (this.ContentLength != -1 && bl3) {
            this.cd_type = 4;
        } else if (bl4 && bl3) {
            this.cd_type = 6;
        } else {
            this.cd_type = 3;
            this.ContentLength = -1;
            if (this.stream_handler != null) {
                this.stream_handler.markForClose(this);
            }
            if (this.Version.equals("HTTP/0.9")) {
                this.inp_stream = new SequenceInputStream(new ByteArrayInputStream(this.Data), this.inp_stream);
                this.Data = null;
            }
        }
        if (this.isFirstResponse && !this.connection.handleFirstRequest(this.req, this)) {
            try {
                object = this.connection.sendRequest(this.req, this.timeout);
            }
            catch (ModuleException moduleException) {
                throw new IOException(moduleException.toString());
            }
            ((Response)object).getVersion();
            this.StatusCode = ((Response)object).StatusCode;
            this.ReasonLine = ((Response)object).ReasonLine;
            this.Version = ((Response)object).Version;
            this.EffectiveURI = ((Response)object).EffectiveURI;
            this.ContentLength = ((Response)object).ContentLength;
            this.Headers = ((Response)object).Headers;
            this.inp_stream = ((Response)object).inp_stream;
            this.Data = ((Response)object).Data;
            this.req = null;
        }
        if (this.connection.ServerProtocolVersion < 65537) {
            String string;
            try {
                object = Util.parseHeader(this.getHeader("Connection"));
            }
            catch (ParseException parseException) {
                object = null;
            }
            if (object != null) {
                if (this.connection.getProxyHost() != null) {
                    ((Vector)object).removeAllElements();
                }
                for (int i = 0; i < ((Vector)object).size(); ++i) {
                    string = ((HttpHeaderElement)((Vector)object).elementAt(i)).getName();
                    if (string.equalsIgnoreCase("keep-alive")) continue;
                    ((Vector)object).removeElementAt(i);
                    this.deleteHeader(string);
                    --i;
                }
                if (((Vector)object).size() > 0) {
                    this.setHeader("Connection", Util.assembleHeader((Vector)object));
                } else {
                    this.deleteHeader("Connection");
                }
            }
            try {
                object = Util.parseHeader(this.getHeader("Proxy-Connection"));
            }
            catch (ParseException parseException) {
                object = null;
            }
            if (object != null) {
                if (this.connection.getProxyHost() == null) {
                    ((Vector)object).removeAllElements();
                }
                for (int i = 0; i < ((Vector)object).size(); ++i) {
                    string = ((HttpHeaderElement)((Vector)object).elementAt(i)).getName();
                    if (string.equalsIgnoreCase("keep-alive")) continue;
                    ((Vector)object).removeElementAt(i);
                    this.deleteHeader(string);
                    --i;
                }
                if (((Vector)object).size() > 0) {
                    this.setHeader("Proxy-Connection", Util.assembleHeader((Vector)object));
                } else {
                    this.deleteHeader("Proxy-Connection");
                }
            }
        } else {
            this.deleteHeader("Proxy-Connection");
        }
    }

    private char[] readResponseHeaders(InputStream inputStream) throws IOException {
        if (!this.reading_lines) {
            int n;
            this.cd_type = 0;
            if (this.buf_pos == 0) {
                do {
                    if ((n = inputStream.read()) != -1) continue;
                    throw new EOFException("Encountered premature EOF while reading Version");
                } while (Character.isWhitespace((char)(n & 0xFF)));
                this.buf[0] = (byte)(n & 0xFF);
                this.buf_pos = 1;
            }
            while (this.buf_pos < 5) {
                n = inputStream.read(this.buf, this.buf_pos, 5 - this.buf_pos);
                if (n == -1) {
                    throw new EOFException("Encountered premature EOF while reading Version");
                }
                this.buf_pos += n;
            }
            for (n = 0; n < this.buf_pos; ++n) {
                this.hdrs[this.hdr_pos++] = (char)(this.buf[n] & 0xFF);
            }
            this.reading_lines = true;
        }
        if (this.hdrs[0] == 'H' && this.hdrs[1] == 'T' && this.hdrs[2] == 'T' && this.hdrs[3] == 'P' && (this.hdrs[4] == '/' || this.hdrs[4] == ' ')) {
            this.cd_type = 1;
            this.readHeaderBlock(inputStream);
        }
        this.buf_pos = 0;
        this.reading_lines = false;
        char[] arrc = Util.resizeArray(this.hdrs, this.hdr_pos);
        this.hdr_pos = 0;
        return arrc;
    }

    void readTrailers(InputStream inputStream) throws IOException {
        try {
            this.readHeaderBlock(inputStream);
            this.trailers = Util.resizeArray(this.hdrs, this.hdr_pos);
        }
        catch (IOException iOException) {
            if (!(iOException instanceof InterruptedIOException)) {
                this.exception = iOException;
            }
            throw iOException;
        }
    }

    private void readHeaderBlock(InputStream inputStream) throws IOException {
        int n;
        while ((n = inputStream.read(this.buf, 0, this.buf.length)) > 0) {
            if (this.hdr_pos + n > this.hdrs.length) {
                this.hdrs = Util.resizeArray(this.hdrs, (this.hdr_pos + n) * 2);
            }
            for (int i = 0; i < n; ++i) {
                this.hdrs[this.hdr_pos++] = (char)(this.buf[i] & 0xFF);
            }
        }
        this.hdr_pos -= 2;
    }

    private void parseResponseHeaders(char[] arrc) throws ProtocolException {
        if (arrc[0] != 'H' || arrc[1] != 'T' || arrc[2] != 'T' || arrc[3] != 'P' || arrc[4] != '/' && arrc[4] != ' ') {
            this.Version = "HTTP/0.9";
            this.StatusCode = 200;
            this.ReasonLine = "OK";
            this.Data = new byte[arrc.length];
            for (int i = 0; i < this.Data.length; ++i) {
                this.Data[i] = (byte)arrc[i];
            }
            return;
        }
        int n = 0;
        int n2 = Util.findSpace(arrc, n);
        this.Version = n2 - n > 4 ? new String(arrc, n, n2 - n) : "HTTP/1.0";
        n = Util.skipSpace(arrc, n2);
        if (n == (n2 = Util.findSpace(arrc, n))) {
            throw new ProtocolException("Invalid HTTP status line received: no status code found in '" + new String(arrc) + "'");
        }
        try {
            this.StatusCode = Integer.parseInt(new String(arrc, n, n2 - n));
        }
        catch (NumberFormatException numberFormatException) {
            throw new ProtocolException("Invalid HTTP status line received: status code '" + new String(arrc, n, n2 - n) + "' not a number in '" + new String(arrc) + "'");
        }
        n = n2;
        while (n2 < arrc.length && arrc[n2] != '\r' && arrc[n2] != '\n') {
            ++n2;
        }
        this.ReasonLine = new String(arrc, n, n2 - n).trim();
        if (this.StatusCode >= 300 && this.sent_entity && this.stream_handler != null) {
            this.stream_handler.markForClose(this);
        }
        this.parseHeaderFields(arrc, Util.skipSpace(arrc, n2), this.Headers);
        if (this.Headers.get("Trailer") != null && this.resp_inp_stream != null) {
            this.resp_inp_stream.dontTruncate();
        }
        boolean bl = !this.Version.equals("HTTP/0.9") && !this.Version.equals("HTTP/1.0");
        try {
            String string = (String)this.Headers.get("Connection");
            String string2 = (String)this.Headers.get("Proxy-Connection");
            if (!((!bl || string == null || !Util.hasToken(string, "close")) && (bl || !this.used_proxy && string != null && Util.hasToken(string, "keep-alive") || this.used_proxy && string2 != null && Util.hasToken(string2, "keep-alive")) || this.stream_handler == null)) {
                this.stream_handler.markForClose(this);
            }
        }
        catch (ParseException parseException) {
            // empty catch block
        }
    }

    private synchronized void getTrailers() throws IOException {
        if (this.got_trailers) {
            return;
        }
        if (this.exception != null) {
            throw (IOException)this.exception.fillInStackTrace();
        }
        try {
            if (this.trailers == null && this.resp_inp_stream != null) {
                this.resp_inp_stream.readAll(this.timeout);
            }
            if (this.trailers != null) {
                this.parseHeaderFields(this.trailers, 0, this.Trailers);
            }
        }
        catch (IOException iOException) {
            if (!(iOException instanceof InterruptedIOException)) {
                this.exception = iOException;
            }
            throw iOException;
        }
        this.got_trailers = true;
    }

    private void parseHeaderFields(char[] arrc, int n, CIHashtable cIHashtable) throws ProtocolException {
        int n2 = n;
        int n3 = arrc.length;
        while (n2 < n3) {
            while (n2 < n3 && !Character.isWhitespace(arrc[n2]) && arrc[n2] != ':') {
                ++n2;
            }
            String string = new String(arrc, n, n2 - n);
            while (n2 < n3 && Character.isWhitespace(arrc[n2])) {
                ++n2;
            }
            String string2 = "";
            if (arrc[n2 - 1] != '\n') {
                for (n = n2 < n3 && arrc[n2] == ':' && arrc[n2 - 1] != '\n' ? n2 + 1 : n2; n < n3 && Character.isWhitespace(arrc[n]); ++n) {
                }
                for (n2 = n; n2 < n3 && arrc[n2] != '\n'; ++n2) {
                }
                string2 = arrc[n2 - 1] == '\r' ? new String(arrc, n, n2 - 1 - n) : new String(arrc, n, n2 - n);
                ++n2;
                while (n2 < n3 && (arrc[n2] == ' ' || arrc[n2] == '\t')) {
                    for (n = n2 + 1; n < n3 && (arrc[n] == ' ' || arrc[n] == '\t'); ++n) {
                    }
                    for (n2 = n; n2 < n3 && arrc[n2] != '\n'; ++n2) {
                    }
                    string2 = arrc[n2 - 1] == '\r' ? string2 + ' ' + new String(arrc, n, n2 - 1 - n) : string2 + ' ' + new String(arrc, n, n2 - n);
                    ++n2;
                }
                n = n2;
            }
            if (string.equalsIgnoreCase("Content-length")) {
                try {
                    this.ContentLength = Integer.parseInt(string2.trim());
                    if (this.ContentLength < 0) {
                        throw new NumberFormatException();
                    }
                }
                catch (NumberFormatException numberFormatException) {
                    throw new ProtocolException("Invalid Content-length header received: '" + string2 + "'");
                }
                cIHashtable.put(string, string2);
                continue;
            }
            String string3 = (String)cIHashtable.get(string);
            if (string3 == null) {
                cIHashtable.put(string, string2);
                continue;
            }
            cIHashtable.put(string, string3 + ", " + string2);
        }
    }

    private void readResponseData(InputStream inputStream) throws IOException {
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

    void markAsFirstResponse(Request request) {
        this.req = request;
        this.isFirstResponse = true;
    }

    public Object clone() {
        Response response;
        try {
            response = (Response)super.clone();
        }
        catch (CloneNotSupportedException cloneNotSupportedException) {
            throw new InternalError(cloneNotSupportedException.toString());
        }
        response.Headers = (CIHashtable)this.Headers.clone();
        response.Trailers = (CIHashtable)this.Trailers.clone();
        return response;
    }
}

