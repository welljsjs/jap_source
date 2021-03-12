/*
 * Decompiled with CFR 0.150.
 */
package HTTPClient;

import HTTPClient.CIHashtable;
import HTTPClient.GlobalConstants;
import HTTPClient.HTTPConnection;
import HTTPClient.HTTPResponse;
import HTTPClient.HttpOutputStream;
import HTTPClient.ModuleException;
import HTTPClient.NVPair;
import HTTPClient.ParseException;
import HTTPClient.ProtocolNotSuppException;
import HTTPClient.URI;
import HTTPClient.Util;
import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InterruptedIOException;
import java.io.OutputStream;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Date;
import java.util.Enumeration;

public class HttpURLConnection
extends java.net.HttpURLConnection
implements GlobalConstants {
    private static CIHashtable connections;
    private HTTPConnection con;
    private String resource;
    private String method;
    private boolean method_set;
    private static NVPair[] default_headers;
    private NVPair[] headers;
    private HTTPResponse resp;
    private boolean do_redir;
    private static Class redir_mod;
    private OutputStream output_stream;
    private static boolean in_hotjava;
    private static String non_proxy_hosts;
    private static String proxy_host;
    private static int proxy_port;
    private String[] hdr_keys;
    private String[] hdr_values;

    public HttpURLConnection(URL uRL) throws ProtocolNotSuppException, IOException {
        super(uRL);
        String string;
        try {
            string = System.getProperty("http.nonProxyHosts", "");
            if (!string.equalsIgnoreCase(non_proxy_hosts)) {
                connections.clear();
                non_proxy_hosts = string;
                String[] arrstring = Util.splitProperty(string);
                for (int i = 0; i < arrstring.length; ++i) {
                    HTTPConnection.dontProxyFor(arrstring[i]);
                }
            }
        }
        catch (ParseException parseException) {
            throw new IOException(parseException.toString());
        }
        catch (SecurityException securityException) {
            // empty catch block
        }
        try {
            string = System.getProperty("http.proxyHost", "");
            int n = Integer.getInteger("http.proxyPort", -1);
            if (!string.equalsIgnoreCase(proxy_host) || n != proxy_port) {
                connections.clear();
                proxy_host = string;
                proxy_port = n;
                HTTPConnection.setProxyServer(string, n);
            }
        }
        catch (SecurityException securityException) {
            // empty catch block
        }
        this.con = this.getConnection(uRL);
        this.method = "GET";
        this.method_set = false;
        this.resource = uRL.getFile();
        this.headers = default_headers;
        this.do_redir = java.net.HttpURLConnection.getFollowRedirects();
        this.output_stream = null;
    }

    private HTTPConnection getConnection(URL uRL) throws ProtocolNotSuppException {
        String string = uRL.getProtocol() + ":" + uRL.getHost() + ":" + (uRL.getPort() != -1 ? uRL.getPort() : URI.defaultPort(uRL.getProtocol()));
        HTTPConnection hTTPConnection = (HTTPConnection)connections.get(string);
        if (hTTPConnection != null) {
            return hTTPConnection;
        }
        hTTPConnection = new HTTPConnection(uRL);
        connections.put(string, hTTPConnection);
        return hTTPConnection;
    }

    public void setRequestMethod(String string) throws ProtocolException {
        if (this.connected) {
            throw new ProtocolException("Already connected!");
        }
        this.method = string.trim().toUpperCase();
        this.method_set = true;
    }

    public String getRequestMethod() {
        return this.method;
    }

    public int getResponseCode() throws IOException {
        if (!this.connected) {
            this.connect();
        }
        try {
            if (in_hotjava && this.resp.getStatusCode() >= 300) {
                try {
                    this.resp.getData();
                }
                catch (InterruptedIOException interruptedIOException) {
                    this.disconnect();
                }
            }
            return this.resp.getStatusCode();
        }
        catch (ModuleException moduleException) {
            throw new IOException(moduleException.toString());
        }
    }

    public String getResponseMessage() throws IOException {
        if (!this.connected) {
            this.connect();
        }
        try {
            return this.resp.getReasonLine();
        }
        catch (ModuleException moduleException) {
            throw new IOException(moduleException.toString());
        }
    }

    public String getHeaderField(String string) {
        try {
            if (!this.connected) {
                this.connect();
            }
            return this.resp.getHeader(string);
        }
        catch (Exception exception) {
            return null;
        }
    }

    public int getHeaderFieldInt(String string, int n) {
        try {
            if (!this.connected) {
                this.connect();
            }
            return this.resp.getHeaderAsInt(string);
        }
        catch (Exception exception) {
            return n;
        }
    }

    public long getHeaderFieldDate(String string, long l) {
        try {
            if (!this.connected) {
                this.connect();
            }
            return this.resp.getHeaderAsDate(string).getTime();
        }
        catch (Exception exception) {
            return l;
        }
    }

    public String getHeaderFieldKey(int n) {
        if (this.hdr_keys == null) {
            this.fill_hdr_arrays();
        }
        if (n >= 0 && n < this.hdr_keys.length) {
            return this.hdr_keys[n];
        }
        return null;
    }

    public String getHeaderField(int n) {
        if (this.hdr_values == null) {
            this.fill_hdr_arrays();
        }
        if (n >= 0 && n < this.hdr_values.length) {
            return this.hdr_values[n];
        }
        return null;
    }

    private void fill_hdr_arrays() {
        try {
            if (!this.connected) {
                this.connect();
            }
            int n = 1;
            Enumeration enumeration = this.resp.listHeaders();
            while (enumeration.hasMoreElements()) {
                ++n;
                enumeration.nextElement();
            }
            this.hdr_keys = new String[n];
            this.hdr_values = new String[n];
            enumeration = this.resp.listHeaders();
            for (int i = 1; i < n; ++i) {
                this.hdr_keys[i] = (String)enumeration.nextElement();
                this.hdr_values[i] = this.resp.getHeader(this.hdr_keys[i]);
            }
            this.hdr_values[0] = this.resp.getVersion() + " " + this.resp.getStatusCode() + " " + this.resp.getReasonLine();
        }
        catch (Exception exception) {
            this.hdr_values = new String[0];
            this.hdr_keys = this.hdr_values;
        }
    }

    public InputStream getInputStream() throws IOException {
        InputStream inputStream;
        if (!this.doInput) {
            throw new ProtocolException("Input not enabled! (use setDoInput(true))");
        }
        if (!this.connected) {
            this.connect();
        }
        try {
            inputStream = this.resp.getInputStream();
            inputStream = new BufferedInputStream(inputStream);
        }
        catch (ModuleException moduleException) {
            throw new IOException(moduleException.toString());
        }
        return inputStream;
    }

    public InputStream getErrorStream() {
        try {
            if (!this.doInput || !this.connected || this.resp.getStatusCode() < 300 || this.resp.getHeaderAsInt("Content-length") <= 0) {
                return null;
            }
            return this.resp.getInputStream();
        }
        catch (Exception exception) {
            return null;
        }
    }

    public synchronized OutputStream getOutputStream() throws IOException {
        if (this.connected) {
            throw new ProtocolException("Already connected!");
        }
        if (!this.doOutput) {
            throw new ProtocolException("Output not enabled! (use setDoOutput(true))");
        }
        if (!this.method_set) {
            this.method = "POST";
        } else if (this.method.equals("HEAD") || this.method.equals("GET") || this.method.equals("TRACE")) {
            throw new ProtocolException("Method " + this.method + " does not support output!");
        }
        if (this.getRequestProperty("Content-type") == null) {
            this.setRequestProperty("Content-type", "application/x-www-form-urlencoded");
        }
        if (this.output_stream == null) {
            if (this.getRequestProperty("Content-type").equals("application/x-www-form-urlencoded")) {
                this.output_stream = new ByteArrayOutputStream(300);
            } else {
                this.output_stream = new HttpOutputStream();
                this.connect();
            }
        }
        return this.output_stream;
    }

    public URL getURL() {
        if (this.connected) {
            try {
                if (this.resp.getEffectiveURL() != this.resp.getOriginalURL()) {
                    return this.resp.getEffectiveURL();
                }
            }
            catch (Exception exception) {
                return null;
            }
        }
        return this.url;
    }

    public void setIfModifiedSince(long l) {
        super.setIfModifiedSince(l);
        this.setRequestProperty("If-Modified-Since", Util.httpDate(new Date(l)));
    }

    public void setRequestProperty(String string, String string2) {
        int n;
        for (n = 0; n < this.headers.length && !this.headers[n].getName().equalsIgnoreCase(string); ++n) {
        }
        if (n == this.headers.length) {
            this.headers = Util.resizeArray(this.headers, n + 1);
        }
        this.headers[n] = new NVPair(string, string2);
    }

    public String getRequestProperty(String string) {
        for (int i = 0; i < this.headers.length; ++i) {
            if (!this.headers[i].getName().equalsIgnoreCase(string)) continue;
            return this.headers[i].getValue();
        }
        return null;
    }

    public static void setDefaultRequestProperty(String string, String string2) {
        int n;
        for (n = 0; n < default_headers.length && !default_headers[n].getName().equalsIgnoreCase(string); ++n) {
        }
        if (n == default_headers.length) {
            default_headers = Util.resizeArray(default_headers, n + 1);
        }
        HttpURLConnection.default_headers[n] = new NVPair(string, string2);
    }

    public static String getDefaultRequestProperty(String string) {
        for (int i = 0; i < default_headers.length; ++i) {
            if (!default_headers[i].getName().equalsIgnoreCase(string)) continue;
            return default_headers[i].getValue();
        }
        return null;
    }

    public void setInstanceFollowRedirects(boolean bl) {
        if (this.connected) {
            throw new IllegalStateException("Already connected!");
        }
        this.do_redir = bl;
    }

    public boolean getInstanceFollowRedirects() {
        return this.do_redir;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public synchronized void connect() throws IOException {
        if (this.connected) {
            return;
        }
        HTTPConnection hTTPConnection = this.con;
        synchronized (hTTPConnection) {
            if (this.do_redir) {
                this.con.addModule(redir_mod, 2);
            } else {
                this.con.removeModule(redir_mod);
            }
            try {
                this.resp = this.output_stream instanceof ByteArrayOutputStream ? this.con.ExtensionMethod(this.method, this.resource, ((ByteArrayOutputStream)this.output_stream).toByteArray(), this.headers) : this.con.ExtensionMethod(this.method, this.resource, (HttpOutputStream)this.output_stream, this.headers);
            }
            catch (ModuleException moduleException) {
                throw new IOException(moduleException.toString());
            }
        }
        this.connected = true;
    }

    public void disconnect() {
        this.con.stop();
    }

    protected void finalize() throws Throwable {
        super.finalize();
    }

    public boolean usingProxy() {
        return this.con.getProxyHost() != null;
    }

    public String toString() {
        return this.getClass().getName() + "[" + this.url + "]";
    }

    static {
        String string;
        connections = new CIHashtable();
        default_headers = new NVPair[0];
        in_hotjava = false;
        try {
            string = System.getProperty("browser");
            if (string != null && string.equals("HotJava")) {
                in_hotjava = true;
            }
        }
        catch (SecurityException securityException) {
            // empty catch block
        }
        try {
            if (Boolean.getBoolean("HTTPClient.HttpURLConnection.AllowUI")) {
                URLConnection.setDefaultAllowUserInteraction(true);
            }
        }
        catch (SecurityException securityException) {
            // empty catch block
        }
        try {
            redir_mod = Class.forName("HTTPClient.RedirectionModule");
        }
        catch (ClassNotFoundException classNotFoundException) {
            throw new NoClassDefFoundError(classNotFoundException.getMessage());
        }
        try {
            string = System.getProperty("http.agent");
            if (string != null) {
                HttpURLConnection.setDefaultRequestProperty("User-Agent", string);
            }
        }
        catch (SecurityException securityException) {
            // empty catch block
        }
        non_proxy_hosts = "";
        proxy_host = "";
        proxy_port = -1;
    }
}

