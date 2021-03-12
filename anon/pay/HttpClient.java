/*
 * Decompiled with CFR 0.150.
 */
package anon.pay;

import anon.pay.xml.XMLDescription;
import anon.util.XMLParseException;
import anon.util.XMLUtil;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import logging.LogHolder;
import logging.LogType;
import org.w3c.dom.Document;

public final class HttpClient {
    private BufferedReader m_reader;
    private BufferedOutputStream m_OS;
    private Socket m_socket;
    static /* synthetic */ Class class$java$net$Socket;

    public HttpClient(Socket socket) throws IOException {
        this.m_socket = socket;
        this.m_reader = new BufferedReader(new InputStreamReader(this.m_socket.getInputStream()));
        this.m_OS = new BufferedOutputStream(this.m_socket.getOutputStream(), 4096);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Unable to fully structure code
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     * Lifted jumps to return sites
     */
    public void close() throws IOException, XMLParseException {
        try {
            this.writeRequest("GET", "close", null);
            this.readAnswer();
            var2_1 = null;
            var3_3 = true;
            var4_5 = false;
            try {
                var3_3 = (Boolean)(HttpClient.class$java$net$Socket == null ? (HttpClient.class$java$net$Socket = HttpClient.class$("java.net.Socket")) : HttpClient.class$java$net$Socket).getMethod("isConnected", null).invoke(this.m_socket, null);
                var4_5 = (Boolean)(HttpClient.class$java$net$Socket == null ? (HttpClient.class$java$net$Socket = HttpClient.class$("java.net.Socket")) : HttpClient.class$java$net$Socket).getMethod("isClosed", null).invoke(this.m_socket, null);
            }
            catch (Exception var5_7) {
                // empty catch block
            }
            try {
                if (this.m_socket != null && !var3_3) {
                    // empty if block
                }
            }
            catch (Exception var5_7) {
                LogHolder.log(3, LogType.NET, var5_7);
            }
            if (this.m_socket == null) return;
            if (var4_5 != false) return;
            try {
                this.m_socket.close();
                return;
            }
            catch (Exception var5_7) {
                LogHolder.log(7, LogType.NET, var5_7);
                return;
            }
        }
        catch (Throwable var1_9) {
            block16: {
                block15: {
                    var2_2 = null;
                    var3_4 = true;
                    var4_6 = false;
                    ** try [egrp 1[TRYBLOCK] [2 : 32->132)] { 
lbl37:
                    // 1 sources

                    var3_4 = (Boolean)(HttpClient.class$java$net$Socket == null ? (HttpClient.class$java$net$Socket = HttpClient.class$("java.net.Socket")) : HttpClient.class$java$net$Socket).getMethod("isConnected", null).invoke(this.m_socket, null);
                    var4_6 = (Boolean)(HttpClient.class$java$net$Socket == null ? (HttpClient.class$java$net$Socket = HttpClient.class$("java.net.Socket")) : HttpClient.class$java$net$Socket).getMethod("isClosed", null).invoke(this.m_socket, null);
                    break block15;
lbl40:
                    // 1 sources

                    catch (Exception var5_8) {
                        // empty catch block
                    }
                }
                ** try [egrp 2[TRYBLOCK] [3 : 134->148)] { 
lbl44:
                // 1 sources

                if (this.m_socket != null && !var3_4) {
                    // empty if block
                }
                break block16;
lbl47:
                // 1 sources

                catch (Exception var5_8) {
                    LogHolder.log(3, LogType.NET, var5_8);
                }
            }
            if (this.m_socket == null) throw var1_9;
            if (var4_6 != false) throw var1_9;
            ** try [egrp 3[TRYBLOCK] [4 : 171->181)] { 
lbl53:
            // 1 sources

            this.m_socket.close();
            throw var1_9;
lbl55:
            // 1 sources

            catch (Exception var5_8) {
                LogHolder.log(7, LogType.NET, var5_8);
            }
            throw var1_9;
        }
    }

    public void writeRequest(String string, String string2, String string3) throws IOException {
        this.m_OS.write((string + " /" + string2 + " HTTP/1.1\r\n").getBytes());
        if (string.equals("POST")) {
            this.m_OS.write(("Content-Length: " + string3.length() + "\r\n").getBytes());
            this.m_OS.write("\r\n".getBytes());
            this.m_OS.write(string3.getBytes());
        } else {
            this.m_OS.write("\r\n".getBytes());
        }
        this.m_OS.flush();
    }

    public Document readAnswer() throws IOException, XMLParseException {
        int n = -1;
        char[] arrc = null;
        String string = this.m_reader.readLine();
        if (string == null) {
            throw new IOException("No answer received");
        }
        int n2 = string.indexOf(" ");
        if (n2 == -1) {
            throw new IOException("Wrong Header");
        }
        if ((n2 = (string = string.substring(n2 + 1)).indexOf(" ")) == -1) {
            throw new IOException("Wrong Header");
        }
        String string2 = string.substring(0, n2);
        String string3 = string.substring(n2 + 1);
        string = this.m_reader.readLine();
        while (string != null && !string.equals("")) {
            n2 = string.indexOf(" ");
            if (n2 == -1) {
                throw new IOException("Wrong Header: " + string);
            }
            String string4 = string.substring(0, n2);
            String string5 = string.substring(n2 + 1).trim();
            if (string4.equalsIgnoreCase("Content-length:")) {
                try {
                    n = Integer.parseInt(string5);
                }
                catch (NumberFormatException numberFormatException) {
                    throw new IOException("Error: received invalid value for header Content-length: " + string5);
                }
            }
            string = this.m_reader.readLine();
        }
        if (n > 0) {
            arrc = new char[n];
            int n3 = 0;
            int n4 = 0;
            while ((n4 = this.m_reader.read(arrc, n3, n - n3)) != -1 && (n3 += n4) < n) {
            }
        }
        if (!string2.equals("200")) {
            if (string2.equals("409")) {
                String string6;
                try {
                    XMLDescription xMLDescription = new XMLDescription(arrc);
                    string6 = xMLDescription.getDescription();
                }
                catch (Exception exception) {
                    string6 = "Unkown Error";
                }
                throw new IOException(string6);
            }
            throw new IOException(string3);
        }
        return XMLUtil.toXMLDocument(arrc);
    }

    static /* synthetic */ Class class$(String string) {
        try {
            return Class.forName(string);
        }
        catch (ClassNotFoundException classNotFoundException) {
            throw new NoClassDefFoundError(classNotFoundException.getMessage());
        }
    }
}

