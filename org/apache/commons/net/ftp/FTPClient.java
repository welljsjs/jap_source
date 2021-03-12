/*
 * Decompiled with CFR 0.150.
 */
package org.apache.commons.net.ftp;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.FilterInputStream;
import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Vector;
import org.apache.commons.net.MalformedServerReplyException;
import org.apache.commons.net.ftp.Configurable;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClientConfig;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPFileEntryParser;
import org.apache.commons.net.ftp.FTPFileList;
import org.apache.commons.net.ftp.FTPFileListParser;
import org.apache.commons.net.ftp.FTPListParseEngine;
import org.apache.commons.net.ftp.FTPReply;
import org.apache.commons.net.ftp.parser.DefaultFTPFileEntryParserFactory;
import org.apache.commons.net.ftp.parser.FTPFileEntryParserFactory;
import org.apache.commons.net.io.FromNetASCIIInputStream;
import org.apache.commons.net.io.SocketInputStream;
import org.apache.commons.net.io.SocketOutputStream;
import org.apache.commons.net.io.ToNetASCIIOutputStream;
import org.apache.commons.net.io.Util;

public class FTPClient
extends FTP
implements Configurable {
    public static final int ACTIVE_LOCAL_DATA_CONNECTION_MODE = 0;
    public static final int ACTIVE_REMOTE_DATA_CONNECTION_MODE = 1;
    public static final int PASSIVE_LOCAL_DATA_CONNECTION_MODE = 2;
    public static final int PASSIVE_REMOTE_DATA_CONNECTION_MODE = 3;
    private int __dataConnectionMode;
    private int __dataTimeout;
    private int __passivePort;
    private String __passiveHost;
    private int __fileType;
    private int __fileFormat;
    private int __fileStructure;
    private int __fileTransferMode;
    private boolean __remoteVerificationEnabled;
    private long __restartOffset;
    private FTPFileEntryParserFactory __parserFactory;
    private int __bufferSize;
    private String __systemName;
    private FTPFileEntryParser __entryParser;
    private FTPClientConfig __configuration;

    public FTPClient() {
        this.__initDefaults();
        this.__dataTimeout = -1;
        this.__remoteVerificationEnabled = true;
        this.__parserFactory = new DefaultFTPFileEntryParserFactory();
        this.__configuration = null;
    }

    private void __initDefaults() {
        this.__dataConnectionMode = 0;
        this.__passiveHost = null;
        this.__passivePort = -1;
        this.__fileType = 0;
        this.__fileStructure = 7;
        this.__fileFormat = 4;
        this.__fileTransferMode = 10;
        this.__restartOffset = 0L;
        this.__systemName = null;
        this.__entryParser = null;
        this.__bufferSize = 1024;
    }

    private String __parsePathname(String string) {
        int n = string.indexOf(34) + 1;
        int n2 = string.indexOf(34, n);
        return string.substring(n, n2);
    }

    private void __parsePassiveModeReply(String string) throws MalformedServerReplyException {
        string = string.substring(string.indexOf(40) + 1, string.indexOf(41)).trim();
        StringBuffer stringBuffer = new StringBuffer(24);
        int n = 0;
        int n2 = string.indexOf(44);
        stringBuffer.append(string.substring(n, n2));
        for (int i = 0; i < 3; ++i) {
            stringBuffer.append('.');
            n = n2 + 1;
            n2 = string.indexOf(44, n);
            stringBuffer.append(string.substring(n, n2));
        }
        n = n2 + 1;
        n2 = string.indexOf(44, n);
        String string2 = string.substring(n, n2);
        String string3 = string.substring(n2 + 1);
        try {
            n2 = Integer.parseInt(string2);
            n = Integer.parseInt(string3);
        }
        catch (NumberFormatException numberFormatException) {
            throw new MalformedServerReplyException("Could not parse passive host information.\nServer Reply: " + string);
        }
        n2 <<= 8;
        this.__passiveHost = stringBuffer.toString();
        this.__passivePort = n2 |= n;
    }

    private boolean __storeFile(int n, String string, InputStream inputStream) throws IOException {
        Socket socket = this._openDataConnection_(n, string);
        if (socket == null) {
            return false;
        }
        FilterOutputStream filterOutputStream = new BufferedOutputStream(socket.getOutputStream(), this.getBufferSize());
        if (this.__fileType == 0) {
            filterOutputStream = new ToNetASCIIOutputStream(filterOutputStream);
        }
        try {
            Util.copyStream(inputStream, filterOutputStream, this.getBufferSize(), -1L, null, false);
        }
        catch (IOException iOException) {
            try {
                socket.close();
            }
            catch (IOException iOException2) {
                // empty catch block
            }
            throw iOException;
        }
        ((OutputStream)filterOutputStream).close();
        socket.close();
        return this.completePendingCommand();
    }

    private OutputStream __storeFileStream(int n, String string) throws IOException {
        Socket socket = this._openDataConnection_(n, string);
        if (socket == null) {
            return null;
        }
        OutputStream outputStream = socket.getOutputStream();
        if (this.__fileType == 0) {
            outputStream = new BufferedOutputStream(outputStream, this.getBufferSize());
            outputStream = new ToNetASCIIOutputStream(outputStream);
        }
        return new SocketOutputStream(socket, outputStream);
    }

    protected Socket _openDataConnection_(int n, String string) throws IOException {
        Socket socket;
        Object object;
        if (this.__dataConnectionMode != 0 && this.__dataConnectionMode != 2) {
            return null;
        }
        if (this.__dataConnectionMode == 0) {
            object = this._socketFactory_.createServerSocket(0, 1, this.getLocalAddress());
            if (!FTPReply.isPositiveCompletion(this.port(this.getLocalAddress(), ((ServerSocket)object).getLocalPort()))) {
                ((ServerSocket)object).close();
                return null;
            }
            if (this.__restartOffset > 0L && !this.restart(this.__restartOffset)) {
                ((ServerSocket)object).close();
                return null;
            }
            if (!FTPReply.isPositivePreliminary(this.sendCommand(n, string))) {
                ((ServerSocket)object).close();
                return null;
            }
            if (this.__dataTimeout >= 0) {
                ((ServerSocket)object).setSoTimeout(this.__dataTimeout);
            }
            socket = ((ServerSocket)object).accept();
            ((ServerSocket)object).close();
        } else {
            if (this.pasv() != 227) {
                return null;
            }
            this.__parsePassiveModeReply((String)this._replyLines.elementAt(0));
            socket = this._socketFactory_.createSocket(this.__passiveHost, this.__passivePort);
            if (this.__restartOffset > 0L && !this.restart(this.__restartOffset)) {
                socket.close();
                return null;
            }
            if (!FTPReply.isPositivePreliminary(this.sendCommand(n, string))) {
                socket.close();
                return null;
            }
        }
        if (this.__remoteVerificationEnabled && !this.verifyRemote(socket)) {
            object = socket.getInetAddress();
            InetAddress inetAddress = this.getRemoteAddress();
            socket.close();
            throw new IOException("Host attempting data connection " + ((InetAddress)object).getHostAddress() + " is not same as server " + inetAddress.getHostAddress());
        }
        if (this.__dataTimeout >= 0) {
            socket.setSoTimeout(this.__dataTimeout);
        }
        return socket;
    }

    protected void _connectAction_() throws IOException {
        super._connectAction_();
        this.__initDefaults();
    }

    public void setDataTimeout(int n) {
        this.__dataTimeout = n;
    }

    public void setParserFactory(FTPFileEntryParserFactory fTPFileEntryParserFactory) {
        this.__parserFactory = fTPFileEntryParserFactory;
    }

    public void disconnect() throws IOException {
        super.disconnect();
        this.__initDefaults();
    }

    public void setRemoteVerificationEnabled(boolean bl) {
        this.__remoteVerificationEnabled = bl;
    }

    public boolean isRemoteVerificationEnabled() {
        return this.__remoteVerificationEnabled;
    }

    public boolean login(String string, String string2) throws IOException {
        this.user(string);
        if (FTPReply.isPositiveCompletion(this._replyCode)) {
            return true;
        }
        if (!FTPReply.isPositiveIntermediate(this._replyCode)) {
            return false;
        }
        return FTPReply.isPositiveCompletion(this.pass(string2));
    }

    public boolean login(String string, String string2, String string3) throws IOException {
        this.user(string);
        if (FTPReply.isPositiveCompletion(this._replyCode)) {
            return true;
        }
        if (!FTPReply.isPositiveIntermediate(this._replyCode)) {
            return false;
        }
        this.pass(string2);
        if (FTPReply.isPositiveCompletion(this._replyCode)) {
            return true;
        }
        if (!FTPReply.isPositiveIntermediate(this._replyCode)) {
            return false;
        }
        return FTPReply.isPositiveCompletion(this.acct(string3));
    }

    public boolean logout() throws IOException {
        return FTPReply.isPositiveCompletion(this.quit());
    }

    public boolean changeWorkingDirectory(String string) throws IOException {
        return FTPReply.isPositiveCompletion(this.cwd(string));
    }

    public boolean changeToParentDirectory() throws IOException {
        return FTPReply.isPositiveCompletion(this.cdup());
    }

    public boolean structureMount(String string) throws IOException {
        return FTPReply.isPositiveCompletion(this.smnt(string));
    }

    boolean reinitialize() throws IOException {
        this.rein();
        if (FTPReply.isPositiveCompletion(this._replyCode) || FTPReply.isPositivePreliminary(this._replyCode) && FTPReply.isPositiveCompletion(this.getReply())) {
            this.__initDefaults();
            return true;
        }
        return false;
    }

    public void enterLocalActiveMode() {
        this.__dataConnectionMode = 0;
        this.__passiveHost = null;
        this.__passivePort = -1;
    }

    public void enterLocalPassiveMode() {
        this.__dataConnectionMode = 2;
        this.__passiveHost = null;
        this.__passivePort = -1;
    }

    public boolean enterRemoteActiveMode(InetAddress inetAddress, int n) throws IOException {
        if (FTPReply.isPositiveCompletion(this.port(inetAddress, n))) {
            this.__dataConnectionMode = 1;
            this.__passiveHost = null;
            this.__passivePort = -1;
            return true;
        }
        return false;
    }

    public boolean enterRemotePassiveMode() throws IOException {
        if (this.pasv() != 227) {
            return false;
        }
        this.__dataConnectionMode = 3;
        this.__parsePassiveModeReply((String)this._replyLines.elementAt(0));
        return true;
    }

    public String getPassiveHost() {
        return this.__passiveHost;
    }

    public int getPassivePort() {
        return this.__passivePort;
    }

    public int getDataConnectionMode() {
        return this.__dataConnectionMode;
    }

    public boolean setFileType(int n) throws IOException {
        if (FTPReply.isPositiveCompletion(this.type(n))) {
            this.__fileType = n;
            this.__fileFormat = 4;
            return true;
        }
        return false;
    }

    public boolean setFileType(int n, int n2) throws IOException {
        if (FTPReply.isPositiveCompletion(this.type(n, n2))) {
            this.__fileType = n;
            this.__fileFormat = n2;
            return true;
        }
        return false;
    }

    public boolean setFileStructure(int n) throws IOException {
        if (FTPReply.isPositiveCompletion(this.stru(n))) {
            this.__fileStructure = n;
            return true;
        }
        return false;
    }

    public boolean setFileTransferMode(int n) throws IOException {
        if (FTPReply.isPositiveCompletion(this.mode(n))) {
            this.__fileTransferMode = n;
            return true;
        }
        return false;
    }

    public boolean remoteRetrieve(String string) throws IOException {
        if (this.__dataConnectionMode == 1 || this.__dataConnectionMode == 3) {
            return FTPReply.isPositivePreliminary(this.retr(string));
        }
        return false;
    }

    public boolean remoteStore(String string) throws IOException {
        if (this.__dataConnectionMode == 1 || this.__dataConnectionMode == 3) {
            return FTPReply.isPositivePreliminary(this.stor(string));
        }
        return false;
    }

    public boolean remoteStoreUnique(String string) throws IOException {
        if (this.__dataConnectionMode == 1 || this.__dataConnectionMode == 3) {
            return FTPReply.isPositivePreliminary(this.stou(string));
        }
        return false;
    }

    public boolean remoteStoreUnique() throws IOException {
        if (this.__dataConnectionMode == 1 || this.__dataConnectionMode == 3) {
            return FTPReply.isPositivePreliminary(this.stou());
        }
        return false;
    }

    public boolean remoteAppend(String string) throws IOException {
        if (this.__dataConnectionMode == 1 || this.__dataConnectionMode == 3) {
            return FTPReply.isPositivePreliminary(this.stor(string));
        }
        return false;
    }

    public boolean completePendingCommand() throws IOException {
        return FTPReply.isPositiveCompletion(this.getReply());
    }

    public boolean retrieveFile(String string, OutputStream outputStream) throws IOException {
        Socket socket = this._openDataConnection_(13, string);
        if (socket == null) {
            return false;
        }
        FilterInputStream filterInputStream = new BufferedInputStream(socket.getInputStream(), this.getBufferSize());
        if (this.__fileType == 0) {
            filterInputStream = new FromNetASCIIInputStream(filterInputStream);
        }
        try {
            Util.copyStream(filterInputStream, outputStream, this.getBufferSize(), -1L, null, false);
        }
        catch (IOException iOException) {
            try {
                socket.close();
            }
            catch (IOException iOException2) {
                // empty catch block
            }
            throw iOException;
        }
        socket.close();
        return this.completePendingCommand();
    }

    public InputStream retrieveFileStream(String string) throws IOException {
        Socket socket = this._openDataConnection_(13, string);
        if (socket == null) {
            return null;
        }
        InputStream inputStream = socket.getInputStream();
        if (this.__fileType == 0) {
            inputStream = new BufferedInputStream(inputStream, this.getBufferSize());
            inputStream = new FromNetASCIIInputStream(inputStream);
        }
        return new SocketInputStream(socket, inputStream);
    }

    public boolean storeFile(String string, InputStream inputStream) throws IOException {
        return this.__storeFile(14, string, inputStream);
    }

    public OutputStream storeFileStream(String string) throws IOException {
        return this.__storeFileStream(14, string);
    }

    public boolean appendFile(String string, InputStream inputStream) throws IOException {
        return this.__storeFile(16, string, inputStream);
    }

    public OutputStream appendFileStream(String string) throws IOException {
        return this.__storeFileStream(16, string);
    }

    public boolean storeUniqueFile(String string, InputStream inputStream) throws IOException {
        return this.__storeFile(15, string, inputStream);
    }

    public OutputStream storeUniqueFileStream(String string) throws IOException {
        return this.__storeFileStream(15, string);
    }

    public boolean storeUniqueFile(InputStream inputStream) throws IOException {
        return this.__storeFile(15, null, inputStream);
    }

    public OutputStream storeUniqueFileStream() throws IOException {
        return this.__storeFileStream(15, null);
    }

    public boolean allocate(int n) throws IOException {
        return FTPReply.isPositiveCompletion(this.allo(n));
    }

    public boolean allocate(int n, int n2) throws IOException {
        return FTPReply.isPositiveCompletion(this.allo(n, n2));
    }

    private boolean restart(long l) throws IOException {
        this.__restartOffset = 0L;
        return FTPReply.isPositiveIntermediate(this.rest(Long.toString(l)));
    }

    public void setRestartOffset(long l) {
        if (l >= 0L) {
            this.__restartOffset = l;
        }
    }

    public long getRestartOffset() {
        return this.__restartOffset;
    }

    public boolean rename(String string, String string2) throws IOException {
        if (!FTPReply.isPositiveIntermediate(this.rnfr(string))) {
            return false;
        }
        return FTPReply.isPositiveCompletion(this.rnto(string2));
    }

    public boolean abort() throws IOException {
        return FTPReply.isPositiveCompletion(this.abor());
    }

    public boolean deleteFile(String string) throws IOException {
        return FTPReply.isPositiveCompletion(this.dele(string));
    }

    public boolean removeDirectory(String string) throws IOException {
        return FTPReply.isPositiveCompletion(this.rmd(string));
    }

    public boolean makeDirectory(String string) throws IOException {
        return FTPReply.isPositiveCompletion(this.mkd(string));
    }

    public String printWorkingDirectory() throws IOException {
        if (this.pwd() != 257) {
            return null;
        }
        return this.__parsePathname((String)this._replyLines.elementAt(0));
    }

    public boolean sendSiteCommand(String string) throws IOException {
        return FTPReply.isPositiveCompletion(this.site(string));
    }

    public String getSystemName() throws IOException {
        if (this.__systemName == null && FTPReply.isPositiveCompletion(this.syst())) {
            this.__systemName = ((String)this._replyLines.elementAt(0)).substring(4);
        }
        return this.__systemName;
    }

    public String listHelp() throws IOException {
        if (FTPReply.isPositiveCompletion(this.help())) {
            return this.getReplyString();
        }
        return null;
    }

    public String listHelp(String string) throws IOException {
        if (FTPReply.isPositiveCompletion(this.help(string))) {
            return this.getReplyString();
        }
        return null;
    }

    public boolean sendNoOp() throws IOException {
        return FTPReply.isPositiveCompletion(this.noop());
    }

    public String[] listNames(String string) throws IOException {
        String string2;
        Socket socket = this._openDataConnection_(27, string);
        if (socket == null) {
            return null;
        }
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream(), this.getControlEncoding()));
        Vector<String> vector = new Vector<String>();
        while ((string2 = bufferedReader.readLine()) != null) {
            vector.addElement(string2);
        }
        bufferedReader.close();
        socket.close();
        if (this.completePendingCommand()) {
            Object[] arrobject = new String[vector.size()];
            vector.copyInto(arrobject);
            return arrobject;
        }
        return null;
    }

    public String[] listNames() throws IOException {
        return this.listNames(null);
    }

    public FTPFile[] listFiles(String string, String string2) throws IOException {
        FTPListParseEngine fTPListParseEngine = this.initiateListParsing(string, string2);
        return fTPListParseEngine.getFiles();
    }

    public FTPFile[] listFiles(String string) throws IOException {
        String string2 = null;
        FTPListParseEngine fTPListParseEngine = this.initiateListParsing(string2, string);
        return fTPListParseEngine.getFiles();
    }

    public FTPFile[] listFiles() throws IOException {
        return this.listFiles((String)null);
    }

    public FTPListParseEngine initiateListParsing() throws IOException {
        return this.initiateListParsing(null);
    }

    public FTPListParseEngine initiateListParsing(String string) throws IOException {
        String string2 = null;
        return this.initiateListParsing(string2, string);
    }

    public FTPListParseEngine initiateListParsing(String string, String string2) throws IOException {
        if (this.__entryParser == null) {
            this.__entryParser = null != string ? this.__parserFactory.createFileEntryParser(string) : (null != this.__configuration ? this.__parserFactory.createFileEntryParser(this.__configuration) : this.__parserFactory.createFileEntryParser(this.getSystemName()));
        }
        return this.initiateListParsing(this.__entryParser, string2);
    }

    private FTPListParseEngine initiateListParsing(FTPFileEntryParser fTPFileEntryParser, String string) throws IOException {
        FTPListParseEngine fTPListParseEngine = new FTPListParseEngine(fTPFileEntryParser);
        Socket socket = this._openDataConnection_(26, string);
        if (socket == null) {
            return fTPListParseEngine;
        }
        fTPListParseEngine.readServerList(socket.getInputStream(), this.getControlEncoding());
        socket.close();
        this.completePendingCommand();
        return fTPListParseEngine;
    }

    public String getStatus() throws IOException {
        if (FTPReply.isPositiveCompletion(this.stat())) {
            return this.getReplyString();
        }
        return null;
    }

    public String getStatus(String string) throws IOException {
        if (FTPReply.isPositiveCompletion(this.stat(string))) {
            return this.getReplyString();
        }
        return null;
    }

    public FTPFile[] listFiles(FTPFileListParser fTPFileListParser, String string) throws IOException {
        Socket socket = this._openDataConnection_(26, string);
        if (socket == null) {
            return new FTPFile[0];
        }
        FTPFile[] arrfTPFile = fTPFileListParser.parseFileList(socket.getInputStream(), this.getControlEncoding());
        socket.close();
        this.completePendingCommand();
        return arrfTPFile;
    }

    public FTPFile[] listFiles(FTPFileListParser fTPFileListParser) throws IOException {
        return this.listFiles(fTPFileListParser, null);
    }

    public FTPFileList createFileList(FTPFileEntryParser fTPFileEntryParser) throws IOException {
        return this.createFileList(null, fTPFileEntryParser);
    }

    public FTPFileList createFileList(String string, FTPFileEntryParser fTPFileEntryParser) throws IOException {
        Socket socket = this._openDataConnection_(26, string);
        if (socket == null) {
            return null;
        }
        FTPFileList fTPFileList = FTPFileList.create(socket.getInputStream(), fTPFileEntryParser);
        socket.close();
        this.completePendingCommand();
        return fTPFileList;
    }

    public void setBufferSize(int n) {
        this.__bufferSize = n;
    }

    public int getBufferSize() {
        return this.__bufferSize;
    }

    public void configure(FTPClientConfig fTPClientConfig) {
        this.__configuration = fTPClientConfig;
    }
}

