/*
 * Decompiled with CFR 0.150.
 */
package org.apache.commons.net.telnet;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import org.apache.commons.net.io.FromNetASCIIInputStream;
import org.apache.commons.net.io.ToNetASCIIOutputStream;
import org.apache.commons.net.telnet.InvalidTelnetOptionException;
import org.apache.commons.net.telnet.Telnet;
import org.apache.commons.net.telnet.TelnetInputStream;
import org.apache.commons.net.telnet.TelnetNotificationHandler;
import org.apache.commons.net.telnet.TelnetOptionHandler;
import org.apache.commons.net.telnet.TelnetOutputStream;

public class TelnetClient
extends Telnet {
    private InputStream __input = null;
    private OutputStream __output = null;
    protected boolean readerThread = true;

    public TelnetClient() {
        super("VT100");
    }

    public TelnetClient(String string) {
        super(string);
    }

    void _flushOutputStream() throws IOException {
        this._output_.flush();
    }

    void _closeOutputStream() throws IOException {
        this._output_.close();
    }

    protected void _connectAction_() throws IOException {
        super._connectAction_();
        InputStream inputStream = FromNetASCIIInputStream.isConversionRequired() ? new FromNetASCIIInputStream(this._input_) : this._input_;
        TelnetInputStream telnetInputStream = new TelnetInputStream(inputStream, this, this.readerThread);
        if (this.readerThread) {
            telnetInputStream._start();
        }
        this.__input = new BufferedInputStream(telnetInputStream);
        this.__output = new ToNetASCIIOutputStream(new TelnetOutputStream(this));
    }

    public void disconnect() throws IOException {
        this.__input.close();
        this.__output.close();
        super.disconnect();
    }

    public OutputStream getOutputStream() {
        return this.__output;
    }

    public InputStream getInputStream() {
        return this.__input;
    }

    public boolean getLocalOptionState(int n) {
        return this._stateIsWill(n) && this._requestedWill(n);
    }

    public boolean getRemoteOptionState(int n) {
        return this._stateIsDo(n) && this._requestedDo(n);
    }

    public boolean sendAYT(long l) throws IOException, IllegalArgumentException, InterruptedException {
        return this._sendAYT(l);
    }

    public void addOptionHandler(TelnetOptionHandler telnetOptionHandler) throws InvalidTelnetOptionException {
        super.addOptionHandler(telnetOptionHandler);
    }

    public void deleteOptionHandler(int n) throws InvalidTelnetOptionException {
        super.deleteOptionHandler(n);
    }

    public void registerSpyStream(OutputStream outputStream) {
        super._registerSpyStream(outputStream);
    }

    public void stopSpyStream() {
        super._stopSpyStream();
    }

    public void registerNotifHandler(TelnetNotificationHandler telnetNotificationHandler) {
        super.registerNotifHandler(telnetNotificationHandler);
    }

    public void unregisterNotifHandler() {
        super.unregisterNotifHandler();
    }

    public void setReaderThread(boolean bl) {
        this.readerThread = bl;
    }

    public boolean getReaderThread() {
        return this.readerThread;
    }
}

