/*
 * Decompiled with CFR 0.150.
 */
package anon.proxy;

import anon.infoservice.HTTPConnectionFactory;
import anon.infoservice.ListenerInterface;
import anon.proxy.DirectProxy;
import anon.proxy.DirectProxyResponse;
import anon.shared.ProxyConnection;
import anon.util.Util;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PushbackInputStream;
import java.net.Socket;
import java.net.SocketException;
import java.net.URL;
import java.net.UnknownHostException;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.util.StringTokenizer;
import logging.LogHolder;
import logging.LogType;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;

final class DirectProxyConnection {
    private Socket m_clientSocket;
    private InputStream m_socketInputStream;
    private int m_threadNumber;
    private static int m_threadCount;
    private InputStream m_inputStream = null;
    private String m_requestLine = null;
    private String m_strMethod = "";
    private String m_strURI = "";
    private String m_strProtocol = "";
    private String m_strVersion = "";
    private String m_strHost = "";
    private String m_strFile = "";
    private int m_iPort = -1;
    private static DateFormat m_DateFormat;
    private static NumberFormat m_NumberFormat;
    private DirectProxy m_parentProxy;
    private static final Object SYNC_SINGLE_CONNECTION;

    public DirectProxyConnection(Socket socket, InputStream inputStream, DirectProxy directProxy) {
        this.m_parentProxy = directProxy;
        this.m_clientSocket = socket;
        this.m_socketInputStream = inputStream;
        this.handleRequest(null);
    }

    private static String readLine(InputStream inputStream, byte[] arrby, int[] arrn) throws Exception {
        String string = "";
        arrn[0] = 0;
        int n = inputStream.read();
        if (arrby.length > arrn[0]) {
            arrby[arrn[0]] = (byte)n;
            arrn[0] = arrn[0] + 1;
        }
        while (n != 10 && n != -1) {
            if (n != 13) {
                string = string + (char)n;
            }
            if (inputStream.available() <= 0) {
                return null;
            }
            n = inputStream.read();
            if (arrby.length <= arrn[0]) continue;
            arrby[arrn[0]] = (byte)n;
            arrn[0] = arrn[0] + 1;
        }
        return string;
    }

    public static String readLine(InputStream inputStream) throws Exception {
        String string = "";
        int n = inputStream.read();
        while (n != 10 && n != -1) {
            if (n != 13) {
                string = string + (char)n;
            }
            n = inputStream.read();
        }
        return string;
    }

    public static DirectProxy.RequestInfo getURI(PushbackInputStream pushbackInputStream, int n) {
        Object object;
        Object object2;
        if (pushbackInputStream == null) {
            return null;
        }
        DirectProxy.RequestInfo requestInfo = null;
        DataInputStream dataInputStream = new DataInputStream(pushbackInputStream);
        byte[] arrby = new byte[n];
        int[] arrn = new int[]{0};
        try {
            object2 = DirectProxyConnection.readLine(dataInputStream, arrby, arrn);
            if (object2 == null) {
                return null;
            }
            object = new StringTokenizer((String)object2);
            String string = ((StringTokenizer)object).nextToken();
            requestInfo = DirectProxy.parseDomain(((StringTokenizer)object).nextToken(), true, string);
        }
        catch (Exception exception) {
            LogHolder.log(3, LogType.NET, exception);
            return null;
        }
        if (arrn[0] > 0) {
            try {
                object2 = new ByteArrayOutputStream();
                object = new DataOutputStream((OutputStream)object2);
                ((DataOutputStream)object).write(arrby, 0, arrn[0]);
                ((DataOutputStream)object).flush();
                pushbackInputStream.unread(((ByteArrayOutputStream)object2).toByteArray());
            }
            catch (Exception exception) {
                LogHolder.log(2, LogType.NET, "Could not unread request line!", exception);
            }
        }
        return requestInfo;
    }

    private void handleRequest(InputStream inputStream) {
        Object object;
        this.m_threadNumber = this.getThreadNumber();
        LogHolder.log(7, LogType.NET, "C(" + this.m_threadNumber + ") - New connection handler started.");
        try {
            this.m_inputStream = inputStream != null ? inputStream : (this.m_socketInputStream != null ? new DataInputStream(this.m_socketInputStream) : new DataInputStream(this.m_clientSocket.getInputStream()));
            this.m_requestLine = DirectProxyConnection.readLine(this.m_inputStream);
            LogHolder.log(7, LogType.NET, "C(" + this.m_threadNumber + ") - RequestLine: >" + this.m_requestLine + "<");
            object = new StringTokenizer(this.m_requestLine);
            this.m_strMethod = ((StringTokenizer)object).nextToken();
            this.m_strURI = ((StringTokenizer)object).nextToken();
            if (((StringTokenizer)object).hasMoreTokens()) {
                this.m_strVersion = ((StringTokenizer)object).nextToken();
            }
        }
        catch (Exception exception) {
            this.badRequest();
            return;
        }
        try {
            if (this.m_strMethod.equalsIgnoreCase("CONNECT")) {
                int n = this.m_strURI.indexOf(58);
                if (n > 0) {
                    this.m_strHost = this.m_strURI.substring(0, n);
                    this.m_iPort = Integer.parseInt(this.m_strURI.substring(n + 1));
                    this.handleCONNECT();
                } else {
                    this.badRequest();
                }
            } else if (this.m_strMethod.equalsIgnoreCase("GET") || this.m_strMethod.equalsIgnoreCase("POST") || this.m_strMethod.equalsIgnoreCase("PUT") || this.m_strMethod.equalsIgnoreCase("DELETE") || this.m_strMethod.equalsIgnoreCase("TRACE") || this.m_strMethod.equalsIgnoreCase("OPTIONS") || this.m_strMethod.equalsIgnoreCase("HEAD")) {
                object = new URL(this.m_strURI);
                this.m_strProtocol = ((URL)object).getProtocol();
                this.m_strHost = ((URL)object).getHost();
                this.m_iPort = ((URL)object).getPort();
                if (this.m_iPort == -1) {
                    this.m_iPort = 80;
                }
                this.m_strFile = ((URL)object).getFile();
                if (this.m_strProtocol.equalsIgnoreCase("http")) {
                    this.handleHTTP(this.m_strMethod.equalsIgnoreCase("POST"));
                } else if (this.m_strProtocol.equalsIgnoreCase("ftp")) {
                    this.handleFTP();
                } else {
                    this.unknownProtocol();
                }
            } else {
                this.badRequest();
            }
        }
        catch (UnknownHostException unknownHostException) {
            this.cannotConnect();
        }
        catch (Exception exception) {
            LogHolder.log(5, LogType.NET, "C(" + this.m_threadNumber + ")", exception);
            this.badRequest();
        }
        try {
            this.m_clientSocket.close();
        }
        catch (Exception exception) {
            LogHolder.log(2, LogType.NET, "C(" + this.m_threadNumber + ") - Exception while closing socket: " + exception);
        }
    }

    private void responseTemplate(String string, String string2) {
        try {
            BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(this.m_clientSocket.getOutputStream()));
            bufferedWriter.write("HTTP/1.0 " + string + "\r\n");
            bufferedWriter.write("Content-type: text/html\r\n");
            bufferedWriter.write("Pragma: no-cache\r\n");
            bufferedWriter.write("Cache-Control: no-cache\r\n\r\n");
            bufferedWriter.write("<HTML><TITLE>" + string2 + "</TITLE>");
            bufferedWriter.write("<H1>" + string + "</H1>");
            bufferedWriter.write("<P>" + string2 + "</P>");
            bufferedWriter.write("</HTML>\n");
            bufferedWriter.flush();
            bufferedWriter.close();
        }
        catch (SocketException socketException) {
            LogHolder.log(6, LogType.NET, "C(" + this.m_threadNumber + ") - Exception: ", socketException);
        }
        catch (Exception exception) {
            LogHolder.log(2, LogType.NET, "C(" + this.m_threadNumber + ") - Exception: ", exception);
        }
    }

    private void cannotConnect() {
        this.responseTemplate("404 Connection error", "Cannot connect to " + this.m_strHost + ":" + this.m_iPort + ".");
    }

    private void unknownProtocol() {
        this.responseTemplate("501 Not implemented", "Protocol <B>" + this.m_strProtocol + "</B> not implemented, supported or unknown.");
    }

    private void badRequest() {
        this.responseTemplate("400 Bad Request", "Bad request: " + this.m_requestLine);
    }

    private void handleCONNECT() throws Exception {
        int n;
        ProxyConnection proxyConnection = new ProxyConnection(HTTPConnectionFactory.getInstance().createHTTPConnection(new ListenerInterface(this.m_strHost, this.m_iPort), this.m_parentProxy.getProxyInterface()).Connect());
        Socket socket = proxyConnection.getSocket();
        String string = DirectProxyConnection.readLine(this.m_inputStream);
        LogHolder.log(7, LogType.NET, "C(" + this.m_threadNumber + ") - Header: >" + string + "<");
        while (string.length() != 0) {
            string = DirectProxyConnection.readLine(this.m_inputStream);
            LogHolder.log(7, LogType.NET, "C(" + this.m_threadNumber + ") - Header: >" + string + "<");
        }
        OutputStream outputStream = socket.getOutputStream();
        BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(this.m_clientSocket.getOutputStream()));
        bufferedWriter.write("HTTP/1.0 200 Connection established\r\n\r\n");
        bufferedWriter.flush();
        DirectProxyResponse directProxyResponse = new DirectProxyResponse(socket.getInputStream(), this.m_clientSocket.getOutputStream());
        Thread thread = new Thread((Runnable)directProxyResponse, "JAP - DirectProxyResponse");
        thread.setDaemon(true);
        thread.start();
        byte[] arrby = new byte[1000];
        while ((n = this.m_inputStream.read(arrby)) != -1) {
            if (n <= 0) continue;
            outputStream.write(arrby, 0, n);
            outputStream.flush();
        }
        LogHolder.log(7, LogType.NET, "\n");
        LogHolder.log(7, LogType.MISC, "C(" + this.m_threadNumber + ") - Waiting for resonse thread...");
        thread.join();
        LogHolder.log(7, LogType.MISC, "C(" + this.m_threadNumber + ") -                           ...finished!");
        bufferedWriter.close();
        outputStream.close();
        this.m_inputStream.close();
        socket.close();
    }

    private void handleHTTP(boolean bl) throws Exception {
        Exception exception = null;
        Socket socket = null;
        OutputStream outputStream = null;
        boolean bl2 = false;
        try {
            Object object;
            ProxyConnection proxyConnection = null;
            proxyConnection = new ProxyConnection(HTTPConnectionFactory.getInstance().createHTTPConnection(new ListenerInterface(this.m_strHost, this.m_iPort), this.m_parentProxy.getProxyInterface()).Connect());
            socket = proxyConnection.getSocket();
            outputStream = socket.getOutputStream();
            String string = "";
            string = string + this.m_strMethod + " " + this.m_strFile + " " + "HTTP/1.0";
            LogHolder.log(7, LogType.NET, "C(" + this.m_threadNumber + ") - ProtocolString: >" + string + "<");
            outputStream.write((string + "\r\n").getBytes());
            String string2 = DirectProxyConnection.readLine(this.m_inputStream);
            LogHolder.log(7, LogType.NET, "C(" + this.m_threadNumber + ") - Header: >" + string2 + "<");
            long l = 0L;
            int n = 0;
            while (string2.length() != 0) {
                ++n;
                if (!this.filter(string2)) {
                    if (bl && string2.toLowerCase().indexOf("content-length:") >= 0) {
                        object = new StringTokenizer(string2, ":");
                        ((StringTokenizer)object).nextToken();
                        if (((StringTokenizer)object).hasMoreTokens()) {
                            try {
                                l = Long.parseLong(((StringTokenizer)object).nextToken().trim());
                            }
                            catch (Exception exception2) {
                                LogHolder.log(2, LogType.NET, "Could not parse post content length!", exception2);
                            }
                        }
                    }
                    outputStream.write((string2.trim() + "\r\n").getBytes());
                } else {
                    LogHolder.log(7, LogType.NET, "C(" + this.m_threadNumber + ") - Header " + string2 + " filtered");
                }
                string2 = DirectProxyConnection.readLine(this.m_inputStream);
                LogHolder.log(7, LogType.NET, "C(" + this.m_threadNumber + ") - Header: >" + string2 + "<");
            }
            outputStream.write("\r\n".getBytes());
            outputStream.flush();
            object = new DirectProxyResponse(socket.getInputStream(), this.m_clientSocket.getOutputStream());
            Thread thread = new Thread((Runnable)object, "JAP - DirectProxyResponse");
            thread.start();
            LogHolder.log(7, LogType.NET, "C(" + this.m_threadNumber + ") - Headers sent, POST data may follow");
            byte[] arrby = new byte[1000];
            final PushbackInputStream pushbackInputStream = new PushbackInputStream(this.m_inputStream, 1000);
            try {
                int n2;
                while ((n2 = pushbackInputStream.read(arrby)) != -1) {
                    int n3 = n2;
                    if (l > 0L) {
                        if ((long)n2 <= l) {
                            l -= (long)n2;
                        } else {
                            n3 = (int)l;
                            LogHolder.log(4, LogType.NET, "Overbuffered POST: " + (n2 - n3));
                            pushbackInputStream.unread(arrby, n3, n2 - n3);
                            l = 0L;
                        }
                    } else {
                        String string3 = new String(arrby, 0, n2).toUpperCase();
                        if (string3.startsWith("GET") || string3.startsWith("POST") || string3.startsWith("HEAD") || string3.startsWith("PUT") || string3.startsWith("DELETE") || string3.startsWith("TRACE") || string3.startsWith("OPTIONS") || string3.startsWith("CONNECT")) {
                            pushbackInputStream.unread(arrby, 0, n2);
                            Thread thread2 = new Thread(new Runnable(){

                                public void run() {
                                    DirectProxyConnection.this.handleRequest(pushbackInputStream);
                                }
                            });
                            thread2.start();
                            bl2 = true;
                            break;
                        }
                    }
                    if (n3 <= 0) continue;
                    outputStream.write(arrby, 0, n3);
                    outputStream.flush();
                }
            }
            catch (SocketException socketException) {
                LogHolder.log(7, LogType.NET, "Socket seams to be closed.");
            }
            LogHolder.log(7, LogType.MISC, "C(" + this.m_threadNumber + ") - Waiting for resonse thread...");
            thread.join();
            LogHolder.log(7, LogType.MISC, "C(" + this.m_threadNumber + ") -                  ...finished!");
        }
        catch (Exception exception3) {
            exception = exception3;
        }
        Util.closeStream(outputStream);
        if (!bl2) {
            Util.closeStream(this.m_inputStream);
        }
        try {
            socket.close();
        }
        catch (Exception exception4) {
            // empty catch block
        }
        if (exception != null) {
            throw exception;
        }
    }

    private void handleFTP() {
        FTPClient fTPClient = null;
        OutputStream outputStream = null;
        try {
            String string = "</pre></body></html>";
            String string2 = "</pre></h4><hr><pre>";
            outputStream = this.m_clientSocket.getOutputStream();
            fTPClient = new FTPClient();
            fTPClient.setDefaultTimeout(30000);
            fTPClient.connect(this.m_strHost);
            fTPClient.setSoTimeout(30000);
            fTPClient.setDataTimeout(30000);
            fTPClient.login("anonymous", "JAP@xxx.com");
            fTPClient.enterLocalPassiveMode();
            if (fTPClient.changeWorkingDirectory(this.m_strFile)) {
                fTPClient.changeToParentDirectory();
                String string3 = fTPClient.printWorkingDirectory();
                String string4 = this.m_strURI;
                if (!string4.endsWith("/")) {
                    string4 = string4 + "/";
                }
                outputStream.write("HTTP/1.0 200 Ok\n\rContent-Type: text/html\r\n\r\n<html><head><title>FTP directory at ".getBytes());
                outputStream.write(string4.getBytes());
                outputStream.write("</title></head><body><h2>FTP directory at ".getBytes());
                outputStream.write(string4.getBytes());
                outputStream.write(("</h2><hr><pre> DIR  | <A HREF=\"" + string3 + "\">..</A>\n").getBytes());
                FTPFile[] arrfTPFile = fTPClient.listFiles(this.m_strFile);
                if (arrfTPFile == null) {
                    outputStream.write(("No files in Directory!\nServer replied:\n" + fTPClient.getReplyString()).getBytes());
                } else {
                    Object object;
                    int n;
                    int n2 = 0;
                    for (int i = 0; i < arrfTPFile.length; ++i) {
                        if (arrfTPFile[i].getName().length() > n2) {
                            n2 = arrfTPFile[i].getName().length();
                        }
                        for (n = i + 1; n < arrfTPFile.length; ++n) {
                            if (!arrfTPFile[i].isFile() || arrfTPFile[n].isFile()) continue;
                            object = arrfTPFile[i];
                            arrfTPFile[i] = arrfTPFile[n];
                            arrfTPFile[n] = object;
                        }
                    }
                    StringBuffer stringBuffer = new StringBuffer(256);
                    for (n = 0; n < arrfTPFile.length; ++n) {
                        object = arrfTPFile[n].getName();
                        if (((String)object).equals(".") || ((String)object).equals("..")) continue;
                        String string5 = m_NumberFormat.format(arrfTPFile[n].getSize());
                        string5 = "            " + string5;
                        string5 = string5.substring(string5.length() - 12);
                        object = arrfTPFile[n].getName() + "</A>                                        ";
                        object = ((String)object).substring(0, Math.min(n2 + 5, ((String)object).length() - 1));
                        if (arrfTPFile[n].isDirectory() || arrfTPFile[n].isSymbolicLink()) {
                            stringBuffer.append(" DIR  | ");
                            stringBuffer.append("<a href=\"");
                            stringBuffer.append(string4);
                            if (arrfTPFile[n].isSymbolicLink()) {
                                stringBuffer.append(arrfTPFile[n].getLink());
                            } else {
                                stringBuffer.append(arrfTPFile[n].getName());
                            }
                            stringBuffer.append("/\"><b>");
                            stringBuffer.append((String)object);
                            stringBuffer.append("</b></a>\n");
                        } else {
                            stringBuffer.append(" FILE | ");
                            stringBuffer.append("<a href=\"");
                            stringBuffer.append(string4);
                            stringBuffer.append(arrfTPFile[n].getName());
                            stringBuffer.append("\">");
                            stringBuffer.append((String)object);
                            stringBuffer.append(" | ");
                            stringBuffer.append(string5 + " | " + m_DateFormat.format(arrfTPFile[n].getTimestamp().getTime()) + "\n");
                        }
                        outputStream.write(stringBuffer.toString().getBytes());
                        stringBuffer.setLength(0);
                    }
                }
                outputStream.write(string.getBytes());
            } else {
                fTPClient.setFileType(2);
                FTPFile[] arrfTPFile = fTPClient.listFiles(this.m_strFile);
                long l = arrfTPFile[0].getSize();
                outputStream.write(("HTTP/1.0 200 Ok\r\nContent-Type: application/octet-stream\r\nContent-Length: " + Long.toString(l) + "\r\n\r\n").getBytes());
                fTPClient.retrieveFile(this.m_strFile, outputStream);
            }
            outputStream.flush();
            fTPClient.disconnect();
            outputStream.close();
            outputStream = null;
        }
        catch (Exception exception) {
            LogHolder.log(5, LogType.NET, "C(" + this.m_threadNumber + ") - Exception in handleFTP()!", exception);
            try {
                fTPClient.disconnect();
                outputStream.flush();
                outputStream.close();
            }
            catch (Throwable throwable) {
                // empty catch block
            }
        }
    }

    private boolean filter(String string) {
        String[] arrstring = new String[]{"Proxy-Connection", "Pragma", "Connection"};
        for (int i = 0; i < arrstring.length; ++i) {
            if (!string.regionMatches(true, 0, arrstring[i], 0, arrstring[i].length())) continue;
            return true;
        }
        return false;
    }

    private synchronized int getThreadNumber() {
        return m_threadCount++;
    }

    static {
        m_DateFormat = DateFormat.getDateTimeInstance();
        m_NumberFormat = NumberFormat.getInstance();
        SYNC_SINGLE_CONNECTION = new Object();
    }
}

