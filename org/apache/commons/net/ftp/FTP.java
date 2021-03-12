/*
 * Decompiled with CFR 0.150.
 */
package org.apache.commons.net.ftp;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.Vector;
import org.apache.commons.net.MalformedServerReplyException;
import org.apache.commons.net.ProtocolCommandListener;
import org.apache.commons.net.ProtocolCommandSupport;
import org.apache.commons.net.ftp.FTPCommand;
import org.apache.commons.net.ftp.FTPConnectionClosedException;
import org.apache.commons.net.ftp.FTPReply;
import org.apache.commons.net.telnet.TelnetClient;

public class FTP
extends TelnetClient {
    public static final int DEFAULT_DATA_PORT = 20;
    public static final int DEFAULT_PORT = 21;
    public static final int ASCII_FILE_TYPE = 0;
    public static final int EBCDIC_FILE_TYPE = 1;
    public static final int IMAGE_FILE_TYPE = 2;
    public static final int BINARY_FILE_TYPE = 2;
    public static final int LOCAL_FILE_TYPE = 3;
    public static final int NON_PRINT_TEXT_FORMAT = 4;
    public static final int TELNET_TEXT_FORMAT = 5;
    public static final int CARRIAGE_CONTROL_TEXT_FORMAT = 6;
    public static final int FILE_STRUCTURE = 7;
    public static final int RECORD_STRUCTURE = 8;
    public static final int PAGE_STRUCTURE = 9;
    public static final int STREAM_TRANSFER_MODE = 10;
    public static final int BLOCK_TRANSFER_MODE = 11;
    public static final int COMPRESSED_TRANSFER_MODE = 12;
    public static final String DEFAULT_CONTROL_ENCODING = "ISO-8859-1";
    private static final String __modes = "ABILNTCFRPSBC";
    private StringBuffer __commandBuffer;
    BufferedReader _controlInput;
    BufferedWriter _controlOutput;
    int _replyCode;
    Vector _replyLines;
    boolean _newReplyString;
    String _replyString;
    String _controlEncoding;
    protected ProtocolCommandSupport _commandSupport_;

    public FTP() {
        this.setDefaultPort(21);
        this.__commandBuffer = new StringBuffer();
        this._replyLines = new Vector();
        this._newReplyString = false;
        this._replyString = null;
        this._commandSupport_ = new ProtocolCommandSupport(this);
        this._controlEncoding = DEFAULT_CONTROL_ENCODING;
    }

    private void __getReply() throws IOException {
        this._newReplyString = true;
        this._replyLines.setSize(0);
        String string = this._controlInput.readLine();
        if (string == null) {
            throw new FTPConnectionClosedException("Connection closed without indication.");
        }
        int n = string.length();
        if (n < 3) {
            throw new MalformedServerReplyException("Truncated server reply: " + string);
        }
        try {
            String string2 = string.substring(0, 3);
            this._replyCode = Integer.parseInt(string2);
        }
        catch (NumberFormatException numberFormatException) {
            throw new MalformedServerReplyException("Could not parse response code.\nServer Reply: " + string);
        }
        this._replyLines.addElement(string);
        if (n > 3 && string.charAt(3) == '-') {
            do {
                if ((string = this._controlInput.readLine()) == null) {
                    throw new FTPConnectionClosedException("Connection closed without indication.");
                }
                this._replyLines.addElement(string);
            } while (string.length() < 4 || string.charAt(3) == '-' || !Character.isDigit(string.charAt(0)));
        }
        if (this._commandSupport_.getListenerCount() > 0) {
            this._commandSupport_.fireReplyReceived(this._replyCode, this.getReplyString());
        }
        if (this._replyCode == 421) {
            throw new FTPConnectionClosedException("FTP response 421 received.  Server closed connection.");
        }
    }

    protected void _connectAction_() throws IOException {
        super._connectAction_();
        this._controlInput = new BufferedReader(new InputStreamReader(this.getInputStream(), this.getControlEncoding()));
        this._controlOutput = new BufferedWriter(new OutputStreamWriter(this.getOutputStream(), this.getControlEncoding()));
        this.__getReply();
        if (FTPReply.isPositivePreliminary(this._replyCode)) {
            this.__getReply();
        }
    }

    public void setControlEncoding(String string) {
        this._controlEncoding = string;
    }

    public String getControlEncoding() {
        return this._controlEncoding;
    }

    public void addProtocolCommandListener(ProtocolCommandListener protocolCommandListener) {
        this._commandSupport_.addProtocolCommandListener(protocolCommandListener);
    }

    public void removeProtocolCommandListener(ProtocolCommandListener protocolCommandListener) {
        this._commandSupport_.removeProtocolCommandListener(protocolCommandListener);
    }

    public void disconnect() throws IOException {
        super.disconnect();
        this._controlInput = null;
        this._controlOutput = null;
        this._replyLines.setSize(0);
        this._newReplyString = false;
        this._replyString = null;
    }

    public int sendCommand(String string, String string2) throws IOException {
        String string3;
        this.__commandBuffer.setLength(0);
        this.__commandBuffer.append(string);
        if (string2 != null) {
            this.__commandBuffer.append(' ');
            this.__commandBuffer.append(string2);
        }
        this.__commandBuffer.append("\r\n");
        try {
            string3 = this.__commandBuffer.toString();
            this._controlOutput.write(string3);
            this._controlOutput.flush();
        }
        catch (SocketException socketException) {
            if (!this.isConnected() || !this.socketIsConnected(this._socket_)) {
                throw new FTPConnectionClosedException("Connection unexpectedly closed.");
            }
            throw socketException;
        }
        if (this._commandSupport_.getListenerCount() > 0) {
            this._commandSupport_.fireCommandSent(string, string3);
        }
        this.__getReply();
        return this._replyCode;
    }

    private boolean socketIsConnected(Socket socket) {
        if (socket == null) {
            return false;
        }
        try {
            Method method = socket.getClass().getMethod("isConnected", null);
            return (Boolean)method.invoke(socket, null);
        }
        catch (NoSuchMethodException noSuchMethodException) {
            return true;
        }
        catch (IllegalAccessException illegalAccessException) {
            return true;
        }
        catch (InvocationTargetException invocationTargetException) {
            return true;
        }
    }

    public int sendCommand(int n, String string) throws IOException {
        return this.sendCommand(FTPCommand._commands[n], string);
    }

    public int sendCommand(String string) throws IOException {
        return this.sendCommand(string, null);
    }

    public int sendCommand(int n) throws IOException {
        return this.sendCommand(n, null);
    }

    public int getReplyCode() {
        return this._replyCode;
    }

    public int getReply() throws IOException {
        this.__getReply();
        return this._replyCode;
    }

    public String[] getReplyStrings() {
        Object[] arrobject = new String[this._replyLines.size()];
        this._replyLines.copyInto(arrobject);
        return arrobject;
    }

    public String getReplyString() {
        if (!this._newReplyString) {
            return this._replyString;
        }
        StringBuffer stringBuffer = new StringBuffer(256);
        Enumeration enumeration = this._replyLines.elements();
        while (enumeration.hasMoreElements()) {
            stringBuffer.append((String)enumeration.nextElement());
            stringBuffer.append("\r\n");
        }
        this._newReplyString = false;
        this._replyString = stringBuffer.toString();
        return this._replyString;
    }

    public int user(String string) throws IOException {
        return this.sendCommand(0, string);
    }

    public int pass(String string) throws IOException {
        return this.sendCommand(1, string);
    }

    public int acct(String string) throws IOException {
        return this.sendCommand(2, string);
    }

    public int abor() throws IOException {
        return this.sendCommand(21);
    }

    public int cwd(String string) throws IOException {
        return this.sendCommand(3, string);
    }

    public int cdup() throws IOException {
        return this.sendCommand(4);
    }

    public int quit() throws IOException {
        return this.sendCommand(7);
    }

    public int rein() throws IOException {
        return this.sendCommand(6);
    }

    public int smnt(String string) throws IOException {
        return this.sendCommand(5, string);
    }

    public int port(InetAddress inetAddress, int n) throws IOException {
        StringBuffer stringBuffer = new StringBuffer(24);
        stringBuffer.append(inetAddress.getHostAddress().replace('.', ','));
        int n2 = n >>> 8;
        stringBuffer.append(',');
        stringBuffer.append(n2);
        stringBuffer.append(',');
        n2 = n & 0xFF;
        stringBuffer.append(n2);
        return this.sendCommand(8, stringBuffer.toString());
    }

    public int pasv() throws IOException {
        return this.sendCommand(9);
    }

    public int type(int n, int n2) throws IOException {
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append(__modes.charAt(n));
        stringBuffer.append(' ');
        if (n == 3) {
            stringBuffer.append(n2);
        } else {
            stringBuffer.append(__modes.charAt(n2));
        }
        return this.sendCommand(10, stringBuffer.toString());
    }

    public int type(int n) throws IOException {
        return this.sendCommand(10, __modes.substring(n, n + 1));
    }

    public int stru(int n) throws IOException {
        return this.sendCommand(11, __modes.substring(n, n + 1));
    }

    public int mode(int n) throws IOException {
        return this.sendCommand(12, __modes.substring(n, n + 1));
    }

    public int retr(String string) throws IOException {
        return this.sendCommand(13, string);
    }

    public int stor(String string) throws IOException {
        return this.sendCommand(14, string);
    }

    public int stou() throws IOException {
        return this.sendCommand(15);
    }

    public int stou(String string) throws IOException {
        return this.sendCommand(15, string);
    }

    public int appe(String string) throws IOException {
        return this.sendCommand(16, string);
    }

    public int allo(int n) throws IOException {
        return this.sendCommand(17, Integer.toString(n));
    }

    public int allo(int n, int n2) throws IOException {
        return this.sendCommand(17, Integer.toString(n) + " R " + Integer.toString(n2));
    }

    public int rest(String string) throws IOException {
        return this.sendCommand(18, string);
    }

    public int rnfr(String string) throws IOException {
        return this.sendCommand(19, string);
    }

    public int rnto(String string) throws IOException {
        return this.sendCommand(20, string);
    }

    public int dele(String string) throws IOException {
        return this.sendCommand(22, string);
    }

    public int rmd(String string) throws IOException {
        return this.sendCommand(23, string);
    }

    public int mkd(String string) throws IOException {
        return this.sendCommand(24, string);
    }

    public int pwd() throws IOException {
        return this.sendCommand(25);
    }

    public int list() throws IOException {
        return this.sendCommand(26);
    }

    public int list(String string) throws IOException {
        return this.sendCommand(26, string);
    }

    public int nlst() throws IOException {
        return this.sendCommand(27);
    }

    public int nlst(String string) throws IOException {
        return this.sendCommand(27, string);
    }

    public int site(String string) throws IOException {
        return this.sendCommand(28, string);
    }

    public int syst() throws IOException {
        return this.sendCommand(29);
    }

    public int stat() throws IOException {
        return this.sendCommand(30);
    }

    public int stat(String string) throws IOException {
        return this.sendCommand(30, string);
    }

    public int help() throws IOException {
        return this.sendCommand(31);
    }

    public int help(String string) throws IOException {
        return this.sendCommand(31, string);
    }

    public int noop() throws IOException {
        return this.sendCommand(32);
    }
}

