/*
 * Decompiled with CFR 0.150.
 */
package HTTPClient;

import HTTPClient.AuthSchemeNotImplException;
import HTTPClient.AuthorizationInfo;
import HTTPClient.CIHashtable;
import HTTPClient.Codecs;
import HTTPClient.EstablishConnection;
import HTTPClient.ExtByteArrayOutputStream;
import HTTPClient.ForbiddenIOException;
import HTTPClient.GlobalConstants;
import HTTPClient.HTTPClientModule;
import HTTPClient.HTTPClientModuleConstants;
import HTTPClient.HTTPResponse;
import HTTPClient.HttpHeaderElement;
import HTTPClient.HttpOutputStream;
import HTTPClient.IdempotentSequence;
import HTTPClient.LinkedList;
import HTTPClient.ModuleException;
import HTTPClient.NVPair;
import HTTPClient.ParseException;
import HTTPClient.ProtocolNotSuppException;
import HTTPClient.Request;
import HTTPClient.Response;
import HTTPClient.SocksClient;
import HTTPClient.SocksException;
import HTTPClient.StreamDemultiplexor;
import HTTPClient.ThreadInterruptedIOException;
import HTTPClient.URI;
import HTTPClient.Util;
import java.io.IOException;
import java.io.InterruptedIOException;
import java.io.OutputStream;
import java.net.ConnectException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.Vector;

public class HTTPConnection
implements GlobalConstants,
HTTPClientModuleConstants {
    public static final String version = "RPT-HTTPClient/0.4.1-dev";
    private static final Object dflt_context;
    private Object Context = null;
    private int Protocol;
    int ServerProtocolVersion;
    boolean ServProtVersKnown;
    private String RequestProtocolVersion;
    private static boolean force_1_0;
    private String Host;
    private int Port;
    private String Proxy_Host = null;
    private int Proxy_Port;
    private static String Default_Proxy_Host;
    private static int Default_Proxy_Port;
    private static CIHashtable non_proxy_host_list;
    private static Vector non_proxy_dom_list;
    private static Vector non_proxy_addr_list;
    private static Vector non_proxy_mask_list;
    private String Tunnel_Host = null;
    private int Tunnel_Port;
    private static String Default_Tunnel_Host;
    private static int Default_Tunnel_Port;
    private SocksClient Socks_client = null;
    private static SocksClient Default_Socks_client;
    private StreamDemultiplexor input_demux = null;
    LinkedList DemuxList = new LinkedList();
    private LinkedList RequestList = new LinkedList();
    private boolean DoesKeepAlive = false;
    private boolean KeepAliveUnknown = true;
    private int KeepAliveReqMax = -1;
    private int KeepAliveReqLeft;
    private static boolean NeverPipeline;
    private static boolean disable_nagle;
    private static int DefaultTimeout;
    private int Timeout;
    private NVPair[] DefaultHeaders = new NVPair[0];
    private static Vector DefaultModuleList;
    private Vector ModuleList;
    private Response early_stall = null;
    private Response late_stall = null;
    private Response prev_resp = null;
    private boolean output_finished = true;

    public HTTPConnection(String string) {
        this.Setup(0, string, 80);
    }

    public HTTPConnection(String string, int n) {
        this.Setup(0, string, n);
    }

    public HTTPConnection(String string, String string2, int n) throws ProtocolNotSuppException {
        string = string.trim();
        if (!string.equalsIgnoreCase("http")) {
            throw new ProtocolNotSuppException("Unsupported protocol '" + string + "'");
        }
        if (string.equalsIgnoreCase("http")) {
            this.Setup(0, string2, n);
        } else if (string.equalsIgnoreCase("https")) {
            this.Setup(1, string2, n);
        } else if (string.equalsIgnoreCase("shttp")) {
            this.Setup(2, string2, n);
        } else if (string.equalsIgnoreCase("http-ng")) {
            this.Setup(3, string2, n);
        }
    }

    public HTTPConnection(URL uRL) throws ProtocolNotSuppException {
        this(uRL.getProtocol(), uRL.getHost(), uRL.getPort());
    }

    public HTTPConnection(URI uRI) throws ProtocolNotSuppException {
        this(uRI.getScheme(), uRI.getHost(), uRI.getPort());
    }

    private void Setup(int n, String string, int n2) {
        this.Protocol = n;
        this.Host = string.trim().toLowerCase();
        this.Port = n2;
        if (this.Port == -1) {
            this.Port = URI.defaultPort(this.getProtocol());
        }
        if (Default_Proxy_Host != null && !this.matchNonProxy(string)) {
            this.setCurrentProxy(Default_Proxy_Host, Default_Proxy_Port);
        } else {
            this.setCurrentProxy(null, 0);
        }
        this.Socks_client = Default_Socks_client;
        this.Tunnel_Host = Default_Tunnel_Host;
        this.Tunnel_Port = Default_Tunnel_Port;
        this.Timeout = DefaultTimeout;
        this.ModuleList = (Vector)DefaultModuleList.clone();
    }

    private boolean matchNonProxy(String string) {
        InetAddress[] arrinetAddress;
        if (non_proxy_host_list.get(string) != null) {
            return true;
        }
        for (int i = 0; i < non_proxy_dom_list.size(); ++i) {
            if (!string.endsWith((String)non_proxy_dom_list.elementAt(i))) continue;
            return true;
        }
        if (non_proxy_addr_list.size() == 0) {
            return false;
        }
        try {
            arrinetAddress = InetAddress.getAllByName(string);
        }
        catch (UnknownHostException unknownHostException) {
            return false;
        }
        for (int i = 0; i < non_proxy_addr_list.size(); ++i) {
            byte[] arrby = (byte[])non_proxy_addr_list.elementAt(i);
            byte[] arrby2 = (byte[])non_proxy_mask_list.elementAt(i);
            block4: for (int j = 0; j < arrinetAddress.length; ++j) {
                byte[] arrby3 = arrinetAddress[j].getAddress();
                if (arrby3.length != arrby.length) continue;
                for (int k = 0; k < arrby3.length; ++k) {
                    if ((arrby3[k] & arrby2[k]) != (arrby[k] & arrby2[k])) continue block4;
                }
                return true;
            }
        }
        return false;
    }

    public HTTPResponse Head(String string) throws IOException, ModuleException {
        return this.Head(string, (String)null, null);
    }

    public HTTPResponse Head(String string, NVPair[] arrnVPair) throws IOException, ModuleException {
        return this.Head(string, arrnVPair, null);
    }

    public HTTPResponse Head(String string, NVPair[] arrnVPair, NVPair[] arrnVPair2) throws IOException, ModuleException {
        String string2 = this.stripRef(string);
        String string3 = Codecs.nv2query(arrnVPair);
        if (string3 != null && string3.length() > 0) {
            string2 = string2 + "?" + string3;
        }
        return this.setupRequest("HEAD", string2, arrnVPair2, null, null);
    }

    public HTTPResponse Head(String string, String string2) throws IOException, ModuleException {
        return this.Head(string, string2, null);
    }

    public HTTPResponse Head(String string, String string2, NVPair[] arrnVPair) throws IOException, ModuleException {
        String string3 = this.stripRef(string);
        if (string2 != null && string2.length() > 0) {
            string3 = string3 + "?" + Codecs.URLEncode(string2);
        }
        return this.setupRequest("HEAD", string3, arrnVPair, null, null);
    }

    public HTTPResponse Get(String string) throws IOException, ModuleException {
        return this.Get(string, (String)null, null);
    }

    public HTTPResponse Get(String string, NVPair[] arrnVPair) throws IOException, ModuleException {
        return this.Get(string, arrnVPair, null);
    }

    public HTTPResponse Get(String string, NVPair[] arrnVPair, NVPair[] arrnVPair2) throws IOException, ModuleException {
        String string2 = this.stripRef(string);
        String string3 = Codecs.nv2query(arrnVPair);
        if (string3 != null && string3.length() > 0) {
            string2 = string2 + "?" + string3;
        }
        return this.setupRequest("GET", string2, arrnVPair2, null, null);
    }

    public HTTPResponse Get(String string, String string2) throws IOException, ModuleException {
        return this.Get(string, string2, null);
    }

    public HTTPResponse Get(String string, String string2, NVPair[] arrnVPair) throws IOException, ModuleException {
        String string3 = this.stripRef(string);
        if (string2 != null && string2.length() > 0) {
            string3 = string3 + "?" + Codecs.URLEncode(string2);
        }
        return this.setupRequest("GET", string3, arrnVPair, null, null);
    }

    public HTTPResponse Post(String string) throws IOException, ModuleException {
        return this.Post(string, (byte[])null, null);
    }

    public HTTPResponse Post(String string, NVPair[] arrnVPair) throws IOException, ModuleException {
        NVPair[] arrnVPair2 = new NVPair[]{new NVPair("Content-type", "application/x-www-form-urlencoded")};
        return this.Post(string, Codecs.nv2query(arrnVPair), arrnVPair2);
    }

    public HTTPResponse Post(String string, NVPair[] arrnVPair, NVPair[] arrnVPair2) throws IOException, ModuleException {
        if (Util.getIndex(arrnVPair2, "Content-Type") == -1) {
            arrnVPair2 = Util.addValue(arrnVPair2, "Content-type", "application/x-www-form-urlencoded");
        }
        return this.Post(string, Codecs.nv2query(arrnVPair), arrnVPair2);
    }

    public HTTPResponse Post(String string, String string2) throws IOException, ModuleException {
        return this.Post(string, string2, null);
    }

    public HTTPResponse Post(String string, String string2, NVPair[] arrnVPair) throws IOException, ModuleException {
        byte[] arrby = null;
        if (string2 != null && string2.length() > 0) {
            arrby = string2.getBytes();
        }
        return this.Post(string, arrby, arrnVPair);
    }

    public HTTPResponse Post(String string, byte[] arrby) throws IOException, ModuleException {
        return this.Post(string, arrby, null);
    }

    public HTTPResponse Post(String string, byte[] arrby, NVPair[] arrnVPair) throws IOException, ModuleException {
        if (arrby == null) {
            arrby = new byte[]{};
        }
        return this.setupRequest("POST", this.stripRef(string), arrnVPair, arrby, null);
    }

    public HTTPResponse Post(String string, HttpOutputStream httpOutputStream) throws IOException, ModuleException {
        return this.Post(string, httpOutputStream, null);
    }

    public HTTPResponse Post(String string, HttpOutputStream httpOutputStream, NVPair[] arrnVPair) throws IOException, ModuleException {
        return this.setupRequest("POST", this.stripRef(string), arrnVPair, null, httpOutputStream);
    }

    public HTTPResponse Put(String string, String string2) throws IOException, ModuleException {
        return this.Put(string, string2, null);
    }

    public HTTPResponse Put(String string, String string2, NVPair[] arrnVPair) throws IOException, ModuleException {
        byte[] arrby = null;
        if (string2 != null) {
            arrby = string2.getBytes();
        }
        return this.Put(string, arrby, arrnVPair);
    }

    public HTTPResponse Put(String string, byte[] arrby) throws IOException, ModuleException {
        return this.Put(string, arrby, null);
    }

    public HTTPResponse Put(String string, byte[] arrby, NVPair[] arrnVPair) throws IOException, ModuleException {
        if (arrby == null) {
            arrby = new byte[]{};
        }
        return this.setupRequest("PUT", this.stripRef(string), arrnVPair, arrby, null);
    }

    public HTTPResponse Put(String string, HttpOutputStream httpOutputStream) throws IOException, ModuleException {
        return this.Put(string, httpOutputStream, null);
    }

    public HTTPResponse Put(String string, HttpOutputStream httpOutputStream, NVPair[] arrnVPair) throws IOException, ModuleException {
        return this.setupRequest("PUT", this.stripRef(string), arrnVPair, null, httpOutputStream);
    }

    public HTTPResponse Options(String string) throws IOException, ModuleException {
        return this.Options(string, null, (byte[])null);
    }

    public HTTPResponse Options(String string, NVPair[] arrnVPair) throws IOException, ModuleException {
        return this.Options(string, arrnVPair, (byte[])null);
    }

    public HTTPResponse Options(String string, NVPair[] arrnVPair, byte[] arrby) throws IOException, ModuleException {
        return this.setupRequest("OPTIONS", this.stripRef(string), arrnVPair, arrby, null);
    }

    public HTTPResponse Options(String string, NVPair[] arrnVPair, HttpOutputStream httpOutputStream) throws IOException, ModuleException {
        return this.setupRequest("OPTIONS", this.stripRef(string), arrnVPair, null, httpOutputStream);
    }

    public HTTPResponse Delete(String string) throws IOException, ModuleException {
        return this.Delete(string, null);
    }

    public HTTPResponse Delete(String string, NVPair[] arrnVPair) throws IOException, ModuleException {
        return this.setupRequest("DELETE", this.stripRef(string), arrnVPair, null, null);
    }

    public HTTPResponse Trace(String string, NVPair[] arrnVPair) throws IOException, ModuleException {
        return this.setupRequest("TRACE", this.stripRef(string), arrnVPair, null, null);
    }

    public HTTPResponse Trace(String string) throws IOException, ModuleException {
        return this.Trace(string, null);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public Socket Connect() throws IOException, ModuleException, ThreadInterruptedIOException {
        Socket socket = null;
        HTTPConnection hTTPConnection = this;
        synchronized (hTTPConnection) {
            block14: {
                if (this.Proxy_Host == null) {
                    socket = this.getSocket(this.Timeout);
                } else {
                    StreamDemultiplexor streamDemultiplexor = this.input_demux;
                    this.input_demux = null;
                    try {
                        HTTPResponse hTTPResponse = this.setupRequest("CONNECT", this.Host + ":" + this.Port, null, null, null);
                        if (hTTPResponse.getStatusCode() == 200) {
                            socket = this.input_demux.releaseSocket();
                            break block14;
                        }
                        String string = "";
                        try {
                            string = hTTPResponse.getReasonLine();
                        }
                        catch (Exception exception) {
                            // empty catch block
                        }
                        String string2 = "HTTPClient: Connect: CONNECT was not successful. HTTP status: " + hTTPResponse.getStatusCode() + " reason: " + string;
                        if (hTTPResponse.getStatusCode() == 403) {
                            throw new ForbiddenIOException(string2);
                        }
                        throw new IOException(string2);
                    }
                    finally {
                        if (this.input_demux != null) {
                            this.input_demux.releaseHttpConnectResources();
                        }
                        this.input_demux = streamDemultiplexor;
                    }
                }
            }
        }
        if (socket == null) {
            throw new IOException("HTTPClient: Connect: Internal error - could not get the Socket.");
        }
        return socket;
    }

    public HTTPResponse ExtensionMethod(String string, String string2, byte[] arrby, NVPair[] arrnVPair) throws IOException, ModuleException {
        return this.setupRequest(string.trim(), this.stripRef(string2), arrnVPair, arrby, null);
    }

    public HTTPResponse ExtensionMethod(String string, String string2, HttpOutputStream httpOutputStream, NVPair[] arrnVPair) throws IOException, ModuleException {
        return this.setupRequest(string.trim(), this.stripRef(string2), arrnVPair, null, httpOutputStream);
    }

    public void stop() {
        Object object = (Request)this.RequestList.enumerate();
        while (object != null) {
            ((Request)object).aborted = true;
            object = (Request)this.RequestList.next();
        }
        object = (StreamDemultiplexor)this.DemuxList.enumerate();
        while (object != null) {
            ((StreamDemultiplexor)object).abort();
            object = (StreamDemultiplexor)this.DemuxList.next();
        }
    }

    public void setDefaultHeaders(NVPair[] arrnVPair) {
        int n = arrnVPair == null ? 0 : arrnVPair.length;
        this.DefaultHeaders = new NVPair[n];
        int n2 = 0;
        for (int i = 0; i < n; ++i) {
            String string = arrnVPair[i].getName().trim();
            if (string.equalsIgnoreCase("Content-length") || string.equalsIgnoreCase("Host")) continue;
            this.DefaultHeaders[n2++] = arrnVPair[i];
        }
        if (n2 < n) {
            this.DefaultHeaders = Util.resizeArray(this.DefaultHeaders, n2);
        }
    }

    public NVPair[] getDefaultHeaders() {
        NVPair[] arrnVPair = new NVPair[this.DefaultHeaders.length];
        System.arraycopy(this.DefaultHeaders, 0, arrnVPair, 0, arrnVPair.length);
        return arrnVPair;
    }

    public String getProtocol() {
        switch (this.Protocol) {
            case 0: {
                return "http";
            }
            case 1: {
                return "https";
            }
            case 2: {
                return "shttp";
            }
            case 3: {
                return "http-ng";
            }
        }
        throw new Error("HTTPClient Internal Error: invalid protocol " + this.Protocol);
    }

    public String getHost() {
        return this.Host;
    }

    public int getPort() {
        return this.Port;
    }

    public String getProxyHost() {
        return this.Proxy_Host;
    }

    public int getProxyPort() {
        return this.Proxy_Port;
    }

    public void setRawMode(boolean bl) {
        String[] arrstring = new String[]{"HTTPClient.CookieModule", "HTTPClient.RedirectionModule", "HTTPClient.AuthorizationModule", "HTTPClient.DefaultModule", "HTTPClient.TransferEncodingModule", "HTTPClient.ContentMD5Module", "HTTPClient.ContentEncodingModule"};
        for (int i = 0; i < arrstring.length; ++i) {
            try {
                if (bl) {
                    this.removeModule(Class.forName(arrstring[i]));
                    continue;
                }
                this.addModule(Class.forName(arrstring[i]), -1);
                continue;
            }
            catch (ClassNotFoundException classNotFoundException) {
                // empty catch block
            }
        }
    }

    public static void setDefaultTimeout(int n) {
        DefaultTimeout = n;
    }

    public static int getDefaultTimeout() {
        return DefaultTimeout;
    }

    public void setTimeout(int n) {
        this.Timeout = n;
    }

    public int getTimeout() {
        return this.Timeout;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static Class[] getDefaultModules() {
        Vector vector = DefaultModuleList;
        synchronized (vector) {
            Object[] arrobject = new Class[DefaultModuleList.size()];
            DefaultModuleList.copyInto(arrobject);
            return arrobject;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static boolean addDefaultModule(Class class_, int n) {
        Object object;
        try {
            object = (HTTPClientModule)class_.newInstance();
        }
        catch (RuntimeException runtimeException) {
            throw runtimeException;
        }
        catch (Exception exception) {
            throw new RuntimeException(exception.toString());
        }
        object = DefaultModuleList;
        synchronized (object) {
            if (DefaultModuleList.contains(class_)) {
                return false;
            }
            if (n < 0) {
                DefaultModuleList.insertElementAt(class_, DefaultModuleList.size() + n + 1);
            } else {
                DefaultModuleList.insertElementAt(class_, n);
            }
        }
        return true;
    }

    public static boolean removeDefaultModule(Class class_) {
        boolean bl = DefaultModuleList.removeElement(class_);
        return bl;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public Class[] getModules() {
        Vector vector = this.ModuleList;
        synchronized (vector) {
            Object[] arrobject = new Class[this.ModuleList.size()];
            this.ModuleList.copyInto(arrobject);
            return arrobject;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public boolean addModule(Class class_, int n) {
        Object object;
        try {
            object = (HTTPClientModule)class_.newInstance();
        }
        catch (RuntimeException runtimeException) {
            throw runtimeException;
        }
        catch (Exception exception) {
            throw new RuntimeException(exception.toString());
        }
        object = this.ModuleList;
        synchronized (object) {
            if (this.ModuleList.contains(class_)) {
                return false;
            }
            if (n < 0) {
                this.ModuleList.insertElementAt(class_, this.ModuleList.size() + n + 1);
            } else {
                this.ModuleList.insertElementAt(class_, n);
            }
        }
        return true;
    }

    public boolean removeModule(Class class_) {
        if (class_ == null) {
            return false;
        }
        return this.ModuleList.removeElement(class_);
    }

    public void setContext(Object object) {
        if (object == null) {
            throw new IllegalArgumentException("Context must be non-null");
        }
        if (this.Context != null) {
            throw new RuntimeException("Context already set");
        }
        this.Context = object;
    }

    public Object getContext() {
        if (this.Context != null) {
            return this.Context;
        }
        return dflt_context;
    }

    static Object getDefaultContext() {
        return dflt_context;
    }

    public void addDigestAuthorization(String string, String string2, String string3) throws AuthSchemeNotImplException {
        AuthorizationInfo.addDigestAuthorization(this.Host, this.Port, string, string2, string3, this.getContext());
    }

    public void addBasicAuthorization(String string, String string2, String string3) throws AuthSchemeNotImplException {
        AuthorizationInfo.addBasicAuthorization(this.Host, this.Port, string, string2, string3, this.getContext());
    }

    public static void setProxyServer(String string, int n) {
        if (string == null || string.trim().length() == 0) {
            Default_Proxy_Host = null;
        } else {
            Default_Proxy_Host = string.trim().toLowerCase();
            Default_Proxy_Port = n;
        }
    }

    public synchronized void setCurrentProxy(String string, int n) {
        if (string == null || string.trim().length() == 0) {
            this.Proxy_Host = null;
        } else {
            this.Proxy_Host = string.trim().toLowerCase();
            this.Proxy_Port = n <= 0 ? 80 : n;
        }
        switch (this.Protocol) {
            case 0: 
            case 1: {
                if (force_1_0) {
                    this.ServerProtocolVersion = 65536;
                    this.ServProtVersKnown = true;
                    this.RequestProtocolVersion = "HTTP/1.0";
                    break;
                }
                this.ServerProtocolVersion = 65537;
                this.ServProtVersKnown = false;
                this.RequestProtocolVersion = "HTTP/1.1";
                break;
            }
            case 3: {
                this.ServerProtocolVersion = -1;
                this.ServProtVersKnown = false;
                this.RequestProtocolVersion = "";
                break;
            }
            case 2: {
                this.ServerProtocolVersion = -1;
                this.ServProtVersKnown = false;
                this.RequestProtocolVersion = "Secure-HTTP/1.3";
                break;
            }
            default: {
                throw new Error("HTTPClient Internal Error: invalid protocol " + this.Protocol);
            }
        }
        this.KeepAliveUnknown = true;
        this.DoesKeepAlive = false;
        this.input_demux = null;
        this.early_stall = null;
        this.late_stall = null;
        this.prev_resp = null;
    }

    public static void dontProxyFor(String string) throws ParseException {
        int n;
        byte[] arrby;
        byte[] arrby2;
        if ((string = string.trim().toLowerCase()).charAt(0) == '.') {
            if (!non_proxy_dom_list.contains(string)) {
                non_proxy_dom_list.addElement(string);
            }
            return;
        }
        for (int i = 0; i < string.length(); ++i) {
            if (Character.isDigit(string.charAt(i)) || string.charAt(i) == '.' || string.charAt(i) == '/') continue;
            non_proxy_host_list.put(string, "");
            return;
        }
        int n2 = string.indexOf(47);
        if (n2 != -1) {
            arrby2 = HTTPConnection.string2arr(string.substring(0, n2));
            if (arrby2.length != (arrby = HTTPConnection.string2arr(string.substring(n2 + 1))).length) {
                throw new ParseException("length of IP-address (" + arrby2.length + ") != length of netmask (" + arrby.length + ")");
            }
        } else {
            arrby2 = HTTPConnection.string2arr(string);
            arrby = new byte[arrby2.length];
            for (n = 0; n < arrby.length; ++n) {
                arrby[n] = -1;
            }
        }
        block2: for (n = 0; n < non_proxy_addr_list.size(); ++n) {
            byte[] arrby3 = (byte[])non_proxy_addr_list.elementAt(n);
            byte[] arrby4 = (byte[])non_proxy_mask_list.elementAt(n);
            if (arrby3.length != arrby2.length) continue;
            for (int i = 0; i < arrby3.length; ++i) {
                if ((arrby2[i] & arrby4[i]) != (arrby3[i] & arrby4[i]) || arrby4[i] != arrby[i]) continue block2;
            }
            return;
        }
        non_proxy_addr_list.addElement(arrby2);
        non_proxy_mask_list.addElement(arrby);
    }

    public static boolean doProxyFor(String string) throws ParseException {
        int n;
        byte[] arrby;
        byte[] arrby2;
        if ((string = string.trim().toLowerCase()).charAt(0) == '.') {
            return non_proxy_dom_list.removeElement(string);
        }
        for (int i = 0; i < string.length(); ++i) {
            if (Character.isDigit(string.charAt(i)) || string.charAt(i) == '.' || string.charAt(i) == '/') continue;
            return non_proxy_host_list.remove(string) != null;
        }
        int n2 = string.indexOf(47);
        if (n2 != -1) {
            arrby2 = HTTPConnection.string2arr(string.substring(0, n2));
            if (arrby2.length != (arrby = HTTPConnection.string2arr(string.substring(n2 + 1))).length) {
                throw new ParseException("length of IP-address (" + arrby2.length + ") != length of netmask (" + arrby.length + ")");
            }
        } else {
            arrby2 = HTTPConnection.string2arr(string);
            arrby = new byte[arrby2.length];
            for (n = 0; n < arrby.length; ++n) {
                arrby[n] = -1;
            }
        }
        block2: for (n = 0; n < non_proxy_addr_list.size(); ++n) {
            byte[] arrby3 = (byte[])non_proxy_addr_list.elementAt(n);
            byte[] arrby4 = (byte[])non_proxy_mask_list.elementAt(n);
            if (arrby3.length != arrby2.length) continue;
            for (int i = 0; i < arrby3.length; ++i) {
                if ((arrby2[i] & arrby4[i]) != (arrby3[i] & arrby4[i]) || arrby4[i] != arrby[i]) continue block2;
            }
            non_proxy_addr_list.removeElementAt(n);
            non_proxy_mask_list.removeElementAt(n);
            return true;
        }
        return false;
    }

    private static byte[] string2arr(String string) {
        int n;
        char[] arrc = new char[string.length()];
        string.getChars(0, arrc.length, arrc, 0);
        int n2 = 0;
        for (n = 0; n < arrc.length; ++n) {
            if (arrc[n] != '.') continue;
            ++n2;
        }
        byte[] arrby = new byte[n2 + 1];
        n2 = 0;
        n = 0;
        for (int i = 0; i < arrc.length; ++i) {
            if (arrc[i] != '.') continue;
            arrby[n2] = (byte)Integer.parseInt(string.substring(n, i));
            ++n2;
            n = i + 1;
        }
        arrby[n2] = (byte)Integer.parseInt(string.substring(n));
        return arrby;
    }

    public static void setSocksServer(String string) {
        HTTPConnection.setSocksServer(string, 1080);
    }

    public static void setSocksServer(String string, int n) {
        if (n <= 0) {
            n = 1080;
        }
        Default_Socks_client = string == null || string.length() == 0 ? null : new SocksClient(string, n);
    }

    public static void setSocksServer(String string, int n, int n2) throws SocksException {
        if (n <= 0) {
            n = 1080;
        }
        Default_Socks_client = string == null || string.length() == 0 ? null : new SocksClient(string, n, n2);
    }

    private final String stripRef(String string) {
        if (string == null) {
            return "/";
        }
        int n = string.indexOf(35);
        if ((string = n != -1 ? string.substring(0, n).trim() : string.trim()).length() == 0) {
            string = "/";
        }
        return string;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private HTTPResponse setupRequest(String string, String string2, NVPair[] arrnVPair, byte[] arrby, HttpOutputStream httpOutputStream) throws IOException, ModuleException {
        Request request = new Request(this, string, string2, this.mergedHeaders(arrnVPair), arrby, httpOutputStream);
        this.RequestList.addToEnd(request);
        try {
            HTTPResponse hTTPResponse = new HTTPResponse(this.gen_mod_insts(), this.Timeout, request);
            this.handleRequest(request, hTTPResponse, null, true);
            HTTPResponse hTTPResponse2 = hTTPResponse;
            return hTTPResponse2;
        }
        finally {
            this.RequestList.remove(request);
        }
    }

    private NVPair[] mergedHeaders(NVPair[] arrnVPair) {
        int n = arrnVPair != null ? arrnVPair.length : 0;
        int n2 = this.DefaultHeaders != null ? this.DefaultHeaders.length : 0;
        NVPair[] arrnVPair2 = new NVPair[n + n2];
        System.arraycopy(this.DefaultHeaders, 0, arrnVPair2, 0, n2);
        int n3 = n2;
        for (int i = 0; i < n; ++i) {
            int n4;
            String string = arrnVPair[i].getName().trim();
            if (string.equalsIgnoreCase("Content-length") || string.equalsIgnoreCase("Host")) continue;
            for (n4 = 0; n4 < n3 && !arrnVPair2[n4].getName().trim().equalsIgnoreCase(string); ++n4) {
            }
            arrnVPair2[n4] = arrnVPair[i];
            if (n4 != n3) continue;
            ++n3;
        }
        if (n3 < arrnVPair2.length) {
            arrnVPair2 = Util.resizeArray(arrnVPair2, n3);
        }
        return arrnVPair2;
    }

    private HTTPClientModule[] gen_mod_insts() {
        HTTPClientModule[] arrhTTPClientModule = new HTTPClientModule[this.ModuleList.size()];
        for (int i = 0; i < this.ModuleList.size(); ++i) {
            Class class_ = (Class)this.ModuleList.elementAt(i);
            try {
                arrhTTPClientModule[i] = (HTTPClientModule)class_.newInstance();
                continue;
            }
            catch (Exception exception) {
                throw new Error("HTTPClient Internal Error: could not create instance of " + class_.getName() + " -\n" + exception);
            }
        }
        return arrhTTPClientModule;
    }

    void handleRequest(Request request, HTTPResponse hTTPResponse, Response response, boolean bl) throws IOException, ModuleException {
        Response[] arrresponse = new Response[]{response};
        HTTPClientModule[] arrhTTPClientModule = hTTPResponse.getModules();
        if (bl) {
            block10: for (int i = 0; i < arrhTTPClientModule.length; ++i) {
                int n = arrhTTPClientModule[i].requestHandler(request, arrresponse);
                switch (n) {
                    case 0: {
                        continue block10;
                    }
                    case 1: {
                        i = -1;
                        continue block10;
                    }
                    case 2: {
                        break block10;
                    }
                    case 3: 
                    case 4: {
                        if (arrresponse[0] == null) {
                            throw new Error("HTTPClient Internal Error: no response returned by module " + arrhTTPClientModule[i].getClass().getName());
                        }
                        hTTPResponse.set(request, arrresponse[0]);
                        if (request.getStream() != null) {
                            request.getStream().ignoreData(request);
                        }
                        if (request.internal_subrequest) {
                            return;
                        }
                        if (n == 3) {
                            hTTPResponse.handleResponse();
                        } else {
                            hTTPResponse.init(arrresponse[0]);
                        }
                        return;
                    }
                    case 5: {
                        if (request.internal_subrequest) {
                            return;
                        }
                        request.getConnection().handleRequest(request, hTTPResponse, arrresponse[0], true);
                        return;
                    }
                    case 6: {
                        if (request.internal_subrequest) {
                            return;
                        }
                        request.getConnection().handleRequest(request, hTTPResponse, arrresponse[0], false);
                        return;
                    }
                    default: {
                        throw new Error("HTTPClient Internal Error: invalid status " + n + " returned by module " + arrhTTPClientModule[i].getClass().getName());
                    }
                }
            }
        }
        if (request.internal_subrequest) {
            return;
        }
        if (request.getStream() != null && request.getStream().getLength() == -1) {
            if (!this.ServProtVersKnown || this.ServerProtocolVersion < 65537) {
                request.getStream().goAhead(request, null, hTTPResponse.getTimeout());
                hTTPResponse.set(request, request.getStream());
            } else {
                try {
                    request.setHeaders(Util.addToken(request.getHeaders(), "Transfer-Encoding", "chunked"));
                }
                catch (ParseException parseException) {
                    throw new IOException(parseException.toString());
                }
                hTTPResponse.set(request, this.sendRequest(request, hTTPResponse.getTimeout()));
            }
        } else {
            hTTPResponse.set(request, this.sendRequest(request, hTTPResponse.getTimeout()));
        }
        if (request.aborted) {
            throw new IOException("Request aborted by user");
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    Response sendRequest(Request request, int n) throws IOException, ModuleException {
        String[] arrstring;
        ExtByteArrayOutputStream extByteArrayOutputStream = new ExtByteArrayOutputStream(600);
        Response response = null;
        if (this.early_stall != null) {
            try {
                arrstring = this.early_stall;
                synchronized (this.early_stall) {
                    try {
                        this.early_stall.getVersion();
                    }
                    catch (IOException iOException) {
                        // empty catch block
                    }
                    this.early_stall = null;
                    // ** MonitorExit[var6_5] (shouldn't be in output)
                }
            }
            catch (NullPointerException nullPointerException) {
                // empty catch block
            }
        }
        {
            boolean bl;
            arrstring = this.assembleHeaders(request, extByteArrayOutputStream);
            try {
                bl = this.ServerProtocolVersion >= 65537 && !Util.hasToken(arrstring[0], "close") || this.ServerProtocolVersion == 65536 && Util.hasToken(arrstring[0], "keep-alive");
            }
            catch (ParseException parseException) {
                throw new IOException(parseException.toString());
            }
            HTTPConnection hTTPConnection = this;
            synchronized (hTTPConnection) {
                if (this.late_stall != null) {
                    if (this.input_demux != null || this.KeepAliveUnknown) {
                        try {
                            this.late_stall.getVersion();
                            if (this.KeepAliveUnknown) {
                                this.determineKeepAlive(this.late_stall);
                            }
                        }
                        catch (IOException iOException) {
                            // empty catch block
                        }
                    }
                    this.late_stall = null;
                }
                if (request.getMethod().equals("POST") && this.prev_resp != null && this.input_demux != null) {
                    try {
                        this.prev_resp.getVersion();
                    }
                    catch (IOException iOException) {
                        // empty catch block
                    }
                }
                if (!this.output_finished) {
                    try {
                        this.wait();
                    }
                    catch (InterruptedException interruptedException) {
                        throw new IOException(interruptedException.toString());
                    }
                }
                if (request.aborted) {
                    throw new IOException("Request aborted by user");
                }
                int n2 = 3;
                while (n2-- > 0) {
                    try {
                        boolean bl2;
                        Socket[] arrsocket;
                        Socket socket;
                        if (this.input_demux == null || (socket = this.input_demux.getSocket()) == null) {
                            socket = this.getSocket(n);
                            try {
                                if (disable_nagle) {
                                    socket.setTcpNoDelay(true);
                                }
                            }
                            catch (Throwable throwable) {
                                // empty catch block
                            }
                            if (this.Protocol == 1) {
                                if (Default_Tunnel_Host == null && this.Tunnel_Host != null) {
                                    this.Proxy_Host = this.Tunnel_Host;
                                }
                                if (this.Proxy_Host != null) {
                                    arrsocket = new Socket[]{socket};
                                    response = this.enableSSLTunneling(arrsocket, request, n);
                                    if (response != null) {
                                        response.final_resp = true;
                                        return response;
                                    }
                                    socket = arrsocket[0];
                                    this.Tunnel_Host = this.Proxy_Host;
                                    this.Tunnel_Port = this.Proxy_Port;
                                    this.Proxy_Host = null;
                                }
                            }
                            if (this.input_demux != null && this.input_demux.isHttpConnectCompatibilityModeUsed()) {
                                this.input_demux.releaseHttpConnectResources();
                            }
                            this.input_demux = new StreamDemultiplexor(this.Protocol, socket, this, request.getMethod().equals("CONNECT"));
                            this.DemuxList.addToEnd(this.input_demux);
                            this.KeepAliveReqLeft = this.KeepAliveReqMax;
                        }
                        if (request.aborted) {
                            throw new IOException("Request aborted by user");
                        }
                        arrsocket = socket.getOutputStream();
                        try {
                            bl2 = Util.hasToken(arrstring[1], "100-continue");
                        }
                        catch (ParseException parseException) {
                            throw new IOException(parseException.toString());
                        }
                        if (!(request.getData() == null || request.getData().length <= 0 || request.getData().length >= 10000 || request.delay_entity != 0L || this.ServProtVersKnown && this.ServerProtocolVersion >= 65537 && bl2)) {
                            extByteArrayOutputStream.write(request.getData());
                            extByteArrayOutputStream.writeTo((OutputStream)arrsocket);
                        } else {
                            extByteArrayOutputStream.writeTo((OutputStream)arrsocket);
                            try {
                                if (this.ServProtVersKnown && this.ServerProtocolVersion >= 65537 && bl2) {
                                    response = new Response(request, this.Proxy_Host != null && this.Protocol != 1, this.input_demux);
                                    response.timeout = 60;
                                    if (response.getContinue() != 100) {
                                        break;
                                    }
                                }
                            }
                            catch (InterruptedIOException interruptedIOException) {
                            }
                            finally {
                                if (response != null) {
                                    response.timeout = 0;
                                }
                            }
                            if (request.getData() != null && request.getData().length > 0) {
                                if (request.delay_entity > 0L) {
                                    long l = request.delay_entity / 100L;
                                    long l2 = request.delay_entity / l;
                                    int n3 = 0;
                                    while ((long)n3 < l && this.input_demux.available(null) == 0) {
                                        try {
                                            Thread.sleep(l2);
                                        }
                                        catch (InterruptedException interruptedException) {
                                            // empty catch block
                                        }
                                        ++n3;
                                    }
                                    if (this.input_demux.available(null) == 0) {
                                        arrsocket.write(request.getData());
                                    } else {
                                        bl = false;
                                    }
                                } else {
                                    arrsocket.write(request.getData());
                                }
                            }
                        }
                        if (request.getStream() != null) {
                            request.getStream().goAhead(request, (OutputStream)arrsocket, 0);
                        } else {
                            arrsocket.flush();
                        }
                        if (response == null) {
                            response = new Response(request, this.Proxy_Host != null && this.Protocol != 1, this.input_demux);
                        }
                        this.prev_resp = response;
                        break;
                    }
                    catch (IOException iOException) {
                        this.closeDemux(iOException);
                        if (n2 != 0 && !(iOException instanceof UnknownHostException) && !(iOException instanceof InterruptedIOException) && !(iOException instanceof ConnectException) && !request.aborted) continue;
                        throw iOException;
                    }
                }
                if ((!this.KeepAliveUnknown && !this.DoesKeepAlive || !bl || this.KeepAliveReqMax != -1 && this.KeepAliveReqLeft-- == 0) && !request.getMethod().equals("CONNECT")) {
                    this.input_demux.markForClose(response);
                    this.input_demux = null;
                } else {
                    this.input_demux.restartTimer();
                }
                if (!this.ServProtVersKnown) {
                    this.early_stall = response;
                    response.markAsFirstResponse(request);
                }
                if (this.KeepAliveUnknown || !IdempotentSequence.methodIsIdempotent(request.getMethod()) || request.dont_pipeline || NeverPipeline) {
                    this.late_stall = response;
                }
                if (request.getStream() != null) {
                    this.output_finished = false;
                } else {
                    this.output_finished = true;
                    this.notify();
                }
            }
            return response;
        }
    }

    private Socket getSocket(int n) throws IOException, ThreadInterruptedIOException {
        int n2;
        String string;
        Socket socket = null;
        if (this.Tunnel_Host != null) {
            string = this.Tunnel_Host;
            n2 = this.Tunnel_Port;
        } else if (this.Proxy_Host != null) {
            string = this.Proxy_Host;
            n2 = this.Proxy_Port;
        } else {
            string = this.Host;
            n2 = this.Port;
        }
        if (n == 0) {
            if (this.Socks_client != null) {
                socket = this.Socks_client.getSocket(string, n2);
            } else {
                InetAddress[] arrinetAddress = InetAddress.getAllByName(string);
                for (int i = 0; i < arrinetAddress.length; ++i) {
                    try {
                        socket = new Socket(arrinetAddress[i], n2);
                        break;
                    }
                    catch (SocketException socketException) {
                        if (i != arrinetAddress.length - 1) continue;
                        throw socketException;
                    }
                }
            }
        } else {
            EstablishConnection establishConnection = new EstablishConnection(string, n2, this.Socks_client);
            establishConnection.start();
            try {
                establishConnection.join(n);
            }
            catch (InterruptedException interruptedException) {
                socket = establishConnection.getSocket();
                establishConnection.forget();
                throw new ThreadInterruptedIOException("Current thread was interrupted!");
            }
            if (establishConnection.getException() != null) {
                throw establishConnection.getException();
            }
            socket = establishConnection.getSocket();
            if (socket == null) {
                establishConnection.forget();
                socket = establishConnection.getSocket();
                if (socket == null) {
                    throw new InterruptedIOException("Connection establishment timed out");
                }
            }
        }
        return socket;
    }

    private Response enableSSLTunneling(Socket[] arrsocket, Request request, int n) throws IOException, ModuleException {
        Object object;
        Vector<NVPair> vector = new Vector<NVPair>();
        for (int i = 0; i < request.getHeaders().length; ++i) {
            object = request.getHeaders()[i].getName();
            if (!((String)object).equalsIgnoreCase("User-Agent") && !((String)object).equalsIgnoreCase("Proxy-Authorization")) continue;
            vector.addElement(request.getHeaders()[i]);
        }
        Object[] arrobject = new NVPair[vector.size()];
        vector.copyInto(arrobject);
        object = new Request(this, "CONNECT", this.Host + ":" + this.Port, (NVPair[])arrobject, null, null);
        ((Request)object).internal_subrequest = true;
        ExtByteArrayOutputStream extByteArrayOutputStream = new ExtByteArrayOutputStream(600);
        HTTPResponse hTTPResponse = new HTTPResponse(this.gen_mod_insts(), n, (Request)object);
        Response response = null;
        while (true) {
            this.handleRequest((Request)object, hTTPResponse, response, true);
            extByteArrayOutputStream.reset();
            this.assembleHeaders((Request)object, extByteArrayOutputStream);
            extByteArrayOutputStream.writeTo(arrsocket[0].getOutputStream());
            response = new Response((Request)object, arrsocket[0].getInputStream());
            if (response.getStatusCode() == 200) {
                return null;
            }
            try {
                response.getData();
            }
            catch (IOException iOException) {
                // empty catch block
            }
            try {
                arrsocket[0].close();
            }
            catch (IOException iOException) {
                // empty catch block
            }
            hTTPResponse.set((Request)object, response);
            if (!hTTPResponse.handleResponse()) {
                return response;
            }
            arrsocket[0] = this.getSocket(n);
        }
    }

    /*
     * Unable to fully structure code
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     * Lifted jumps to return sites
     */
    private String[] assembleHeaders(Request var1_1, ExtByteArrayOutputStream var2_2) throws IOException {
        var3_3 = new String[]{"", ""};
        var4_4 = var1_1.getHeaders();
        var5_5 = Util.escapeUnsafeChars(var1_1.getRequestURI());
        if (var1_1.getMethod().equals("CONNECT")) {
            var2_2.write(var1_1.getMethod(), " ", this.Host);
            var2_2.write(":", Integer.toString(this.Port), "");
            var2_2.write(" ", this.RequestProtocolVersion, "\r\n");
        } else if (this.Proxy_Host != null && this.Protocol != 1 && !var5_5.equals("*")) {
            var2_2.write(var1_1.getMethod(), " http://", this.Host);
            var2_2.write(":", Integer.toString(this.Port), var5_5);
            var2_2.write(" ", this.RequestProtocolVersion, "\r\n");
        } else {
            var2_2.write(var1_1.getMethod(), " ", var5_5);
            var2_2.write(" ", this.RequestProtocolVersion, "\r\n");
        }
        if (this.Port != 80 || var1_1.getMethod().equals("CONNECT")) {
            var2_2.write("Host: ", this.Host, ":");
            var2_2.write(Integer.toString(this.Port), "\r\n");
        } else {
            var2_2.write("Host: ", this.Host, "\r\n");
        }
        var6_6 = -1;
        var7_7 = -1;
        var8_8 = -1;
        var9_9 = -1;
        var10_10 = -1;
        var11_11 = -1;
        var12_12 = -1;
        var13_13 = -1;
        var14_14 = -1;
        for (var15_15 = 0; var15_15 < var4_4.length; ++var15_15) {
            var16_17 = var4_4[var15_15].getName().trim();
            if (var16_17.equalsIgnoreCase("Content-Type")) {
                var6_6 = var15_15;
                continue;
            }
            if (var16_17.equalsIgnoreCase("User-Agent")) {
                var7_7 = var15_15;
                continue;
            }
            if (var16_17.equalsIgnoreCase("Connection")) {
                var8_8 = var15_15;
                continue;
            }
            if (var16_17.equalsIgnoreCase("Proxy-Connection")) {
                var9_9 = var15_15;
                continue;
            }
            if (var16_17.equalsIgnoreCase("Keep-Alive")) {
                var10_10 = var15_15;
                continue;
            }
            if (var16_17.equalsIgnoreCase("Expect")) {
                var11_11 = var15_15;
                continue;
            }
            if (var16_17.equalsIgnoreCase("TE")) {
                var12_12 = var15_15;
                continue;
            }
            if (var16_17.equalsIgnoreCase("Transfer-Encoding")) {
                var13_13 = var15_15;
                continue;
            }
            if (!var16_17.equalsIgnoreCase("Upgrade")) continue;
            var14_14 = var15_15;
        }
        var15_16 = null;
        if (!this.ServProtVersKnown || this.ServerProtocolVersion < 65537 || var8_8 != -1) {
            if (var8_8 == -1) {
                var15_16 = "Keep-Alive";
                var3_3[0] = "Keep-Alive";
            } else {
                var3_3[0] = var4_4[var8_8].getValue().trim();
                var15_16 = var3_3[0];
            }
            try {
                if (var10_10 != -1 && Util.hasToken(var3_3[0], "keep-alive")) {
                    var2_2.write("Keep-Alive: ", var4_4[var10_10].getValue().trim(), "\r\n");
                }
            }
            catch (ParseException var16_18) {
                throw new IOException(var16_18.toString());
            }
        }
        if (!(this.Proxy_Host == null || this.Protocol == 1 || this.ServProtVersKnown && this.ServerProtocolVersion >= 65537 || var15_16 == null)) {
            var2_2.write("Proxy-Connection: ", var15_16, "\r\n");
            var15_16 = null;
        }
        if (var15_16 != null) {
            try {
                if (Util.hasToken(var15_16, "TE")) ** GOTO lbl84
                var15_16 = var15_16 + ", TE";
            }
            catch (ParseException var16_19) {
                throw new IOException(var16_19.toString());
            }
        } else {
            var15_16 = "TE";
        }
lbl84:
        // 3 sources

        if (var14_14 != -1) {
            var15_16 = var15_16 + ", Upgrade";
        }
        if (var15_16 != null) {
            var2_2.write("Connection: ", var15_16, "\r\n");
        }
        if (var12_12 != -1) {
            var2_2.write("TE: ");
            try {
                var16_17 = Util.parseHeader(var4_4[var12_12].getValue());
            }
            catch (ParseException var17_22) {
                throw new IOException(var17_22.toString());
            }
            if (!var16_17.contains(new HttpHeaderElement("trailers"))) {
                var2_2.write("trailers, ");
            }
            var2_2.write(var4_4[var12_12].getValue().trim(), "\r\n");
        } else {
            var2_2.write("TE: trailers\r\n");
        }
        if (var7_7 != -1) {
            var2_2.write("User-Agent: ", var4_4[var7_7].getValue().trim(), " ");
            var2_2.write("RPT-HTTPClient/0.4.1-dev", "\r\n");
        } else {
            var2_2.write("User-Agent: ", "RPT-HTTPClient/0.4.1-dev", "\r\n");
        }
        for (var16_20 = 0; var16_20 < var4_4.length; ++var16_20) {
            if (var16_20 == var6_6 || var16_20 == var7_7 || var16_20 == var8_8 || var16_20 == var9_9 || var16_20 == var10_10 || var16_20 == var11_11 || var16_20 == var12_12) continue;
            var2_2.write(var4_4[var16_20].getName().trim(), ": ");
            var2_2.write(var4_4[var16_20].getValue().trim(), "\r\n");
        }
        if (var1_1.getData() != null || var1_1.getStream() != null) {
            if (var6_6 != -1) {
                var2_2.write("Content-type: ", var4_4[var6_6].getValue().trim(), "\r\n");
            } else {
                var2_2.write("Content-type: application/octet-stream\r\n");
            }
            if (var1_1.getData() != null) {
                var2_2.write("Content-length: ", Integer.toString(var1_1.getData().length), "\r\n");
            } else if (var1_1.getStream().getLength() != -1 && var13_13 == -1) {
                var2_2.write("Content-length: ", Integer.toString(var1_1.getStream().getLength()), "\r\n");
            }
            if (var11_11 != -1) {
                var3_3[1] = var4_4[var11_11].getValue().trim();
                var2_2.write("Expect: ", var3_3[1], "\r\n");
            }
        } else if (var11_11 != -1) {
            try {
                var16_21 = Util.parseHeader(var4_4[var11_11].getValue());
            }
            catch (ParseException var17_23) {
                throw new IOException(var17_23.toString());
            }
            var17_24 = new HttpHeaderElement("100-continue");
            while (var16_21.removeElement(var17_24)) {
            }
            if (!var16_21.isEmpty()) {
                var3_3[1] = Util.assembleHeader(var16_21);
                var2_2.write("Expect: ", var3_3[1], "\r\n");
            }
        }
        var2_2.write("\r\n");
        return var3_3;
    }

    boolean handleFirstRequest(Request request, Response response) throws IOException {
        this.ServerProtocolVersion = HTTPConnection.String2ProtVers(response.getVersion());
        this.ServProtVersKnown = true;
        if (this.Proxy_Host != null && this.Protocol != 1 && response.getHeader("Via") == null) {
            this.ServerProtocolVersion = 65536;
        }
        if (this.ServerProtocolVersion == 65536 && (response.getStatusCode() == 400 || response.getStatusCode() == 500)) {
            if (this.input_demux != null && this.input_demux.isHttpConnectCompatibilityModeUsed()) {
                this.input_demux.releaseHttpConnectResources();
            }
            this.input_demux.markForClose(response);
            this.input_demux = null;
            this.RequestProtocolVersion = "HTTP/1.0";
            return false;
        }
        return true;
    }

    private void determineKeepAlive(Response response) throws IOException {
        try {
            HttpHeaderElement httpHeaderElement;
            String string;
            if (this.ServerProtocolVersion >= 65537 || ((this.Proxy_Host == null || this.Protocol == 1) && (string = response.getHeader("Connection")) != null || this.Proxy_Host != null && this.Protocol != 1 && (string = response.getHeader("Proxy-Connection")) != null) && Util.hasToken(string, "keep-alive")) {
                this.DoesKeepAlive = true;
                this.KeepAliveUnknown = false;
            } else if (response.getStatusCode() < 400) {
                this.KeepAliveUnknown = false;
            }
            if (this.DoesKeepAlive && this.ServerProtocolVersion == 65536 && (string = response.getHeader("Keep-Alive")) != null && (httpHeaderElement = Util.getElement(Util.parseHeader(string), "max")) != null && httpHeaderElement.getValue() != null) {
                this.KeepAliveReqLeft = this.KeepAliveReqMax = Integer.parseInt(httpHeaderElement.getValue());
            }
        }
        catch (ParseException parseException) {
        }
        catch (NumberFormatException numberFormatException) {
        }
        catch (ClassCastException classCastException) {
            // empty catch block
        }
    }

    synchronized void outputFinished() {
        this.output_finished = true;
        this.notify();
    }

    synchronized void closeDemux(IOException iOException) {
        if (this.input_demux != null) {
            this.input_demux.close(iOException, true);
        }
        this.early_stall = null;
        this.late_stall = null;
        this.prev_resp = null;
    }

    static final String ProtVers2String(int n) {
        return "HTTP/" + (n >>> 16) + "." + (n & 0xFFFF);
    }

    static final int String2ProtVers(String string) {
        String string2 = string.substring(5);
        int n = string2.indexOf(46);
        return Integer.parseInt(string2.substring(0, n)) << 16 | Integer.parseInt(string2.substring(n + 1));
    }

    public String toString() {
        return this.getProtocol() + "://" + this.getHost() + (this.getPort() != URI.defaultPort(this.getProtocol()) ? ":" + this.getPort() : "");
    }

    static {
        int n;
        String string;
        dflt_context = new Object();
        force_1_0 = false;
        Default_Proxy_Host = null;
        non_proxy_host_list = new CIHashtable();
        non_proxy_dom_list = new Vector();
        non_proxy_addr_list = new Vector();
        non_proxy_mask_list = new Vector();
        Default_Tunnel_Host = null;
        Default_Socks_client = null;
        NeverPipeline = false;
        disable_nagle = false;
        DefaultTimeout = 0;
        try {
            string = System.getProperty("http.proxyHost");
            if (string == null) {
                throw new Exception();
            }
            int n2 = Integer.getInteger("http.proxyPort", -1);
            HTTPConnection.setProxyServer(string, n2);
        }
        catch (Exception exception) {
            try {
                if (Boolean.getBoolean("proxySet")) {
                    String string2 = System.getProperty("proxyHost");
                    n = Integer.getInteger("proxyPort", -1);
                    HTTPConnection.setProxyServer(string2, n);
                }
            }
            catch (Exception exception2) {
                Default_Proxy_Host = null;
            }
        }
        try {
            Default_Tunnel_Host = System.getProperty("HTTPClient.tunnelHost");
            if (Default_Tunnel_Host != null) {
                Default_Tunnel_Host = Default_Tunnel_Host.trim().toLowerCase();
            }
            Default_Tunnel_Port = Integer.getInteger("HTTPClient.tunnelPort");
        }
        catch (Exception exception) {
            Default_Tunnel_Host = null;
        }
        try {
            string = System.getProperty("HTTPClient.nonProxyHosts");
            if (string == null) {
                string = System.getProperty("http.nonProxyHosts");
            }
            String[] arrstring = Util.splitProperty(string);
            for (n = 0; n < arrstring.length; ++n) {
                HTTPConnection.dontProxyFor(arrstring[n]);
            }
        }
        catch (Exception exception) {
            // empty catch block
        }
        try {
            string = System.getProperty("HTTPClient.socksHost");
            if (string != null && string.length() > 0) {
                int n3 = Integer.getInteger("HTTPClient.socksPort", -1);
                n = Integer.getInteger("HTTPClient.socksVersion", -1);
                if (n == -1) {
                    HTTPConnection.setSocksServer(string, n3);
                } else {
                    HTTPConnection.setSocksServer(string, n3, n);
                }
            }
        }
        catch (Exception exception) {
            Default_Socks_client = null;
        }
        string = "HTTPClient.RetryModule|HTTPClient.CookieModule|HTTPClient.RedirectionModule|HTTPClient.AuthorizationModule|HTTPClient.DefaultModule|HTTPClient.TransferEncodingModule|HTTPClient.ContentMD5Module|HTTPClient.ContentEncodingModule";
        boolean bl = false;
        try {
            string = System.getProperty("HTTPClient.Modules", string);
        }
        catch (SecurityException securityException) {
            bl = true;
        }
        DefaultModuleList = new Vector();
        String[] arrstring = Util.splitProperty(string);
        for (int i = 0; i < arrstring.length; ++i) {
            try {
                DefaultModuleList.addElement(Class.forName(arrstring[i]));
                continue;
            }
            catch (ClassNotFoundException classNotFoundException) {
                if (bl) continue;
                throw new NoClassDefFoundError(classNotFoundException.getMessage());
            }
        }
        try {
            NeverPipeline = Boolean.getBoolean("HTTPClient.disablePipelining");
        }
        catch (Exception exception) {
            // empty catch block
        }
        try {
            disable_nagle = Boolean.getBoolean("HTTPClient.disableNagle");
        }
        catch (Exception exception) {
            // empty catch block
        }
        try {
            force_1_0 = Boolean.getBoolean("HTTPClient.forceHTTP_1.0");
        }
        catch (Exception exception) {
            // empty catch block
        }
    }
}

