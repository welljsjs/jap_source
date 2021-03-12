/*
 * Decompiled with CFR 0.150.
 */
package HTTPClient;

import HTTPClient.AuthSchemeNotImplException;
import HTTPClient.AuthorizationInfo;
import HTTPClient.GlobalConstants;
import HTTPClient.NVPair;
import HTTPClient.SocksException;
import HTTPClient.Util;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;

public class SocksClient
implements GlobalConstants {
    private String socks_host;
    private int socks_port;
    private int socks_version;
    private static final byte CONNECT = 1;
    private static final byte BIND = 2;
    private static final byte UDP_ASS = 3;
    private static final byte NO_AUTH = 0;
    private static final byte GSSAPI = 1;
    private static final byte USERPWD = 2;
    private static final byte NO_ACC = -1;
    private static final byte IP_V4 = 1;
    private static final byte DMNAME = 3;
    private static final byte IP_V6 = 4;
    private boolean v4A = false;
    private byte[] user = null;

    public SocksClient(String string, int n) {
        this.socks_host = string;
        this.socks_port = n;
        this.socks_version = -1;
    }

    SocksClient(String string, int n, int n2) throws SocksException {
        this.socks_host = string;
        this.socks_port = n;
        if (n2 != 4 && n2 != 5) {
            throw new SocksException("SOCKS Version not supported: " + n2);
        }
        this.socks_version = n2;
    }

    public Socket getSocket(String string, int n) throws IOException {
        Socket socket = null;
        try {
            socket = SocksClient.connect(this.socks_host, this.socks_port);
            InputStream inputStream = socket.getInputStream();
            OutputStream outputStream = socket.getOutputStream();
            switch (this.socks_version) {
                case 4: {
                    this.v4ProtExchg(inputStream, outputStream, string, n);
                    break;
                }
                case 5: {
                    this.v5ProtExchg(inputStream, outputStream, string, n);
                    break;
                }
                case -1: {
                    try {
                        this.v4ProtExchg(inputStream, outputStream, string, n);
                        this.socks_version = 4;
                    }
                    catch (SocksException socksException) {
                        socket.close();
                        socket = SocksClient.connect(this.socks_host, this.socks_port);
                        inputStream = socket.getInputStream();
                        outputStream = socket.getOutputStream();
                        this.v5ProtExchg(inputStream, outputStream, string, n);
                        this.socks_version = 5;
                    }
                    break;
                }
                default: {
                    throw new Error("SocksClient internal error: unknown version " + this.socks_version);
                }
            }
            return socket;
        }
        catch (IOException iOException) {
            if (socket != null) {
                try {
                    socket.close();
                }
                catch (IOException iOException2) {
                    // empty catch block
                }
            }
            throw iOException;
        }
    }

    private static final Socket connect(String string, int n) throws IOException {
        InetAddress[] arrinetAddress = InetAddress.getAllByName(string);
        for (int i = 0; i < arrinetAddress.length; ++i) {
            try {
                return new Socket(arrinetAddress[i], n);
            }
            catch (SocketException socketException) {
                if (i < arrinetAddress.length - 1) continue;
                throw socketException;
            }
        }
        return null;
    }

    private void v4ProtExchg(InputStream inputStream, OutputStream outputStream, String string, int n) throws SocksException, IOException {
        Object object;
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream(100);
        byte[] arrby = new byte[]{0, 0, 0, 42};
        if (!this.v4A) {
            try {
                arrby = InetAddress.getByName(string).getAddress();
            }
            catch (UnknownHostException unknownHostException) {
                this.v4A = true;
            }
            catch (SecurityException securityException) {
                this.v4A = true;
            }
        }
        if (this.user == null) {
            try {
                object = System.getProperty("user.name", "");
            }
            catch (SecurityException securityException) {
                object = "";
            }
            this.user = new byte[((String)object).length() + 1];
            System.arraycopy(((String)object).getBytes(), 0, this.user, 0, ((String)object).length());
            this.user[object.length()] = 0;
        }
        byteArrayOutputStream.reset();
        byteArrayOutputStream.write(4);
        byteArrayOutputStream.write(1);
        byteArrayOutputStream.write(n >> 8 & 0xFF);
        byteArrayOutputStream.write(n & 0xFF);
        byteArrayOutputStream.write(arrby, 0, arrby.length);
        byteArrayOutputStream.write(this.user, 0, this.user.length);
        if (this.v4A) {
            object = string.getBytes();
            byteArrayOutputStream.write((byte[])object, 0, ((Object)object).length);
            byteArrayOutputStream.write(0);
        }
        byteArrayOutputStream.writeTo(outputStream);
        int n2 = inputStream.read();
        if (n2 == -1) {
            throw new SocksException("Connection refused by server");
        }
        if (n2 == 4 && n2 != 0) {
            throw new SocksException("Received invalid version: " + n2 + "; expected: 0");
        }
        int n3 = inputStream.read();
        switch (n3) {
            case 90: {
                break;
            }
            case 91: {
                throw new SocksException("Connection request rejected");
            }
            case 92: {
                throw new SocksException("Connection request rejected: can't connect to identd");
            }
            case 93: {
                throw new SocksException("Connection request rejected: identd reports different user-id from " + new String(this.user));
            }
            default: {
                throw new SocksException("Connection request rejected: unknown error " + n3);
            }
        }
        byte[] arrby2 = new byte[6];
        int n4 = 0;
        for (int i = 0; i < arrby2.length && (n4 = inputStream.read(arrby2, 0, arrby2.length - i)) != -1; i += n4) {
        }
    }

    private void v5ProtExchg(InputStream inputStream, OutputStream outputStream, String string, int n) throws SocksException, IOException {
        int n2;
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream(100);
        byteArrayOutputStream.reset();
        byteArrayOutputStream.write(5);
        byteArrayOutputStream.write(2);
        byteArrayOutputStream.write(0);
        byteArrayOutputStream.write(2);
        byteArrayOutputStream.writeTo(outputStream);
        int n3 = inputStream.read();
        if (n3 == -1) {
            throw new SocksException("Connection refused by server");
        }
        if (n3 != 5) {
            throw new SocksException("Received invalid version: " + n3 + "; expected: 5");
        }
        int n4 = inputStream.read();
        switch (n4) {
            case 0: {
                break;
            }
            case 1: {
                this.negotiate_gssapi(inputStream, outputStream);
                break;
            }
            case 2: {
                this.negotiate_userpwd(inputStream, outputStream);
                break;
            }
            case -1: {
                throw new SocksException("Server unwilling to accept any standard authentication methods");
            }
            default: {
                throw new SocksException("Cannot handle authentication method " + n4);
            }
        }
        byteArrayOutputStream.reset();
        byteArrayOutputStream.write(5);
        byteArrayOutputStream.write(1);
        byteArrayOutputStream.write(0);
        byteArrayOutputStream.write(3);
        byteArrayOutputStream.write(string.length() & 0xFF);
        byte[] arrby = string.getBytes();
        byteArrayOutputStream.write(arrby, 0, arrby.length);
        byteArrayOutputStream.write(n >> 8 & 0xFF);
        byteArrayOutputStream.write(n & 0xFF);
        byteArrayOutputStream.writeTo(outputStream);
        n3 = inputStream.read();
        if (n3 != 5) {
            throw new SocksException("Received invalid version: " + n3 + "; expected: 5");
        }
        int n5 = inputStream.read();
        switch (n5) {
            case 0: {
                break;
            }
            case 1: {
                throw new SocksException("General SOCKS server failure");
            }
            case 2: {
                throw new SocksException("Connection not allowed");
            }
            case 3: {
                throw new SocksException("Network unreachable");
            }
            case 4: {
                throw new SocksException("Host unreachable");
            }
            case 5: {
                throw new SocksException("Connection refused");
            }
            case 6: {
                throw new SocksException("TTL expired");
            }
            case 7: {
                throw new SocksException("Command not supported");
            }
            case 8: {
                throw new SocksException("Address type not supported");
            }
            default: {
                throw new SocksException("Unknown reply received from server: " + n5);
            }
        }
        inputStream.read();
        int n6 = inputStream.read();
        switch (n6) {
            case 4: {
                n2 = 16;
                break;
            }
            case 1: {
                n2 = 4;
                break;
            }
            case 3: {
                n2 = inputStream.read();
                break;
            }
            default: {
                throw new SocksException("Invalid address type received from server: " + n6);
            }
        }
        byte[] arrby2 = new byte[n2 + 2];
        int n7 = 0;
        for (int i = 0; i < arrby2.length && (n7 = inputStream.read(arrby2, 0, arrby2.length - i)) != -1; i += n7) {
        }
    }

    private void negotiate_gssapi(InputStream inputStream, OutputStream outputStream) throws SocksException, IOException {
        throw new SocksException("GSSAPI authentication protocol not implemented");
    }

    private void negotiate_userpwd(InputStream inputStream, OutputStream outputStream) throws SocksException, IOException {
        AuthorizationInfo authorizationInfo;
        try {
            authorizationInfo = AuthorizationInfo.getAuthorization(this.socks_host, this.socks_port, "SOCKS5", "USER/PASS", false, true);
        }
        catch (AuthSchemeNotImplException authSchemeNotImplException) {
            authorizationInfo = null;
        }
        if (authorizationInfo == null) {
            throw new SocksException("No Authorization info for SOCKS found (server requested username/password).");
        }
        NVPair[] arrnVPair = authorizationInfo.getParams();
        if (arrnVPair == null || arrnVPair.length == 0) {
            throw new SocksException("No Username/Password found in authorization info for SOCKS.");
        }
        String string = arrnVPair[0].getName();
        String string2 = arrnVPair[0].getValue();
        byte[] arrby = new byte[2 + string.length() + 1 + string2.length()];
        arrby[0] = 1;
        arrby[1] = (byte)string.length();
        Util.getBytes(string, arrby[1], arrby, 2);
        arrby[2 + arrby[1]] = (byte)string2.length();
        Util.getBytes(string2, arrby[2 + arrby[1]], arrby, 2 + arrby[1] + 1);
        outputStream.write(arrby);
        int n = inputStream.read();
        if (n != 1) {
            throw new SocksException("Wrong version received in username/password subnegotiation response: " + n + "; expected: 1");
        }
        int n2 = inputStream.read();
        if (n2 != 0) {
            throw new SocksException("Username/Password authentication failed; status: " + n2);
        }
    }

    public String toString() {
        return this.getClass().getName() + "[" + this.socks_host + ":" + this.socks_port + "]";
    }
}

