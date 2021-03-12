/*
 * Decompiled with CFR 0.150.
 */
package org.apache.commons.net.telnet;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import org.apache.commons.net.SocketClient;
import org.apache.commons.net.telnet.InvalidTelnetOptionException;
import org.apache.commons.net.telnet.TelnetNotificationHandler;
import org.apache.commons.net.telnet.TelnetOption;
import org.apache.commons.net.telnet.TelnetOptionHandler;

class Telnet
extends SocketClient {
    static final boolean debug = false;
    static final boolean debugoptions = false;
    static final byte[] _COMMAND_DO = new byte[]{-1, -3};
    static final byte[] _COMMAND_DONT = new byte[]{-1, -2};
    static final byte[] _COMMAND_WILL = new byte[]{-1, -5};
    static final byte[] _COMMAND_WONT = new byte[]{-1, -4};
    static final byte[] _COMMAND_SB = new byte[]{-1, -6};
    static final byte[] _COMMAND_SE = new byte[]{-1, -16};
    static final int _WILL_MASK = 1;
    static final int _DO_MASK = 2;
    static final int _REQUESTED_WILL_MASK = 4;
    static final int _REQUESTED_DO_MASK = 8;
    static final int DEFAULT_PORT = 23;
    int[] _doResponse;
    int[] _willResponse;
    int[] _options;
    protected static final int TERMINAL_TYPE = 24;
    protected static final int TERMINAL_TYPE_SEND = 1;
    protected static final int TERMINAL_TYPE_IS = 0;
    static final byte[] _COMMAND_IS = new byte[]{24, 0};
    private String terminalType = null;
    private TelnetOptionHandler[] optionHandlers;
    static final byte[] _COMMAND_AYT = new byte[]{-1, -10};
    private Object aytMonitor = new Object();
    private boolean aytFlag = true;
    private OutputStream spyStream = null;
    private TelnetNotificationHandler __notifhand = null;

    Telnet() {
        this.setDefaultPort(23);
        this._doResponse = new int[256];
        this._willResponse = new int[256];
        this._options = new int[256];
        this.optionHandlers = new TelnetOptionHandler[256];
    }

    Telnet(String string) {
        this.setDefaultPort(23);
        this._doResponse = new int[256];
        this._willResponse = new int[256];
        this._options = new int[256];
        this.terminalType = string;
        this.optionHandlers = new TelnetOptionHandler[256];
    }

    boolean _stateIsWill(int n) {
        return (this._options[n] & 1) != 0;
    }

    boolean _stateIsWont(int n) {
        return !this._stateIsWill(n);
    }

    boolean _stateIsDo(int n) {
        return (this._options[n] & 2) != 0;
    }

    boolean _stateIsDont(int n) {
        return !this._stateIsDo(n);
    }

    boolean _requestedWill(int n) {
        return (this._options[n] & 4) != 0;
    }

    boolean _requestedWont(int n) {
        return !this._requestedWill(n);
    }

    boolean _requestedDo(int n) {
        return (this._options[n] & 8) != 0;
    }

    boolean _requestedDont(int n) {
        return !this._requestedDo(n);
    }

    void _setWill(int n) {
        int n2 = n;
        this._options[n2] = this._options[n2] | 1;
        if (this._requestedWill(n) && this.optionHandlers[n] != null) {
            this.optionHandlers[n].setWill(true);
            int[] arrn = this.optionHandlers[n].startSubnegotiationLocal();
            if (arrn != null) {
                try {
                    this._sendSubnegotiation(arrn);
                }
                catch (Exception exception) {
                    System.err.println("Exception in option subnegotiation" + exception.getMessage());
                }
            }
        }
    }

    void _setDo(int n) {
        int n2 = n;
        this._options[n2] = this._options[n2] | 2;
        if (this._requestedDo(n) && this.optionHandlers[n] != null) {
            this.optionHandlers[n].setDo(true);
            int[] arrn = this.optionHandlers[n].startSubnegotiationRemote();
            if (arrn != null) {
                try {
                    this._sendSubnegotiation(arrn);
                }
                catch (Exception exception) {
                    System.err.println("Exception in option subnegotiation" + exception.getMessage());
                }
            }
        }
    }

    void _setWantWill(int n) {
        int n2 = n;
        this._options[n2] = this._options[n2] | 4;
    }

    void _setWantDo(int n) {
        int n2 = n;
        this._options[n2] = this._options[n2] | 8;
    }

    void _setWont(int n) {
        int n2 = n;
        this._options[n2] = this._options[n2] & 0xFFFFFFFE;
        if (this.optionHandlers[n] != null) {
            this.optionHandlers[n].setWill(false);
        }
    }

    void _setDont(int n) {
        int n2 = n;
        this._options[n2] = this._options[n2] & 0xFFFFFFFD;
        if (this.optionHandlers[n] != null) {
            this.optionHandlers[n].setDo(false);
        }
    }

    void _setWantWont(int n) {
        int n2 = n;
        this._options[n2] = this._options[n2] & 0xFFFFFFFB;
    }

    void _setWantDont(int n) {
        int n2 = n;
        this._options[n2] = this._options[n2] & 0xFFFFFFF7;
    }

    void _processDo(int n) throws IOException {
        if (this.__notifhand != null) {
            this.__notifhand.receivedNegotiation(1, n);
        }
        boolean bl = false;
        if (this.optionHandlers[n] != null) {
            bl = this.optionHandlers[n].getAcceptLocal();
        } else if (n == 24 && this.terminalType != null && this.terminalType.length() > 0) {
            bl = true;
        }
        if (this._willResponse[n] > 0) {
            int n2 = n;
            this._willResponse[n2] = this._willResponse[n2] - 1;
            if (this._willResponse[n] > 0 && this._stateIsWill(n)) {
                int n3 = n;
                this._willResponse[n3] = this._willResponse[n3] - 1;
            }
        }
        if (this._willResponse[n] == 0) {
            if (this._requestedWont(n)) {
                switch (n) {
                    default: 
                }
                if (bl) {
                    this._setWantWill(n);
                    this._sendWill(n);
                } else {
                    int n4 = n;
                    this._willResponse[n4] = this._willResponse[n4] + 1;
                    this._sendWont(n);
                }
            } else {
                switch (n) {
                    default: 
                }
            }
        }
        this._setWill(n);
    }

    void _processDont(int n) throws IOException {
        if (this.__notifhand != null) {
            this.__notifhand.receivedNegotiation(2, n);
        }
        if (this._willResponse[n] > 0) {
            int n2 = n;
            this._willResponse[n2] = this._willResponse[n2] - 1;
            if (this._willResponse[n] > 0 && this._stateIsWont(n)) {
                int n3 = n;
                this._willResponse[n3] = this._willResponse[n3] - 1;
            }
        }
        if (this._willResponse[n] == 0 && this._requestedWill(n)) {
            switch (n) {
                default: 
            }
            if (this._stateIsWill(n) || this._requestedWill(n)) {
                this._sendWont(n);
            }
            this._setWantWont(n);
        }
        this._setWont(n);
    }

    void _processWill(int n) throws IOException {
        if (this.__notifhand != null) {
            this.__notifhand.receivedNegotiation(3, n);
        }
        boolean bl = false;
        if (this.optionHandlers[n] != null) {
            bl = this.optionHandlers[n].getAcceptRemote();
        }
        if (this._doResponse[n] > 0) {
            int n2 = n;
            this._doResponse[n2] = this._doResponse[n2] - 1;
            if (this._doResponse[n] > 0 && this._stateIsDo(n)) {
                int n3 = n;
                this._doResponse[n3] = this._doResponse[n3] - 1;
            }
        }
        if (this._doResponse[n] == 0 && this._requestedDont(n)) {
            switch (n) {
                default: 
            }
            if (bl) {
                this._setWantDo(n);
                this._sendDo(n);
            } else {
                int n4 = n;
                this._doResponse[n4] = this._doResponse[n4] + 1;
                this._sendDont(n);
            }
        }
        this._setDo(n);
    }

    void _processWont(int n) throws IOException {
        if (this.__notifhand != null) {
            this.__notifhand.receivedNegotiation(4, n);
        }
        if (this._doResponse[n] > 0) {
            int n2 = n;
            this._doResponse[n2] = this._doResponse[n2] - 1;
            if (this._doResponse[n] > 0 && this._stateIsDont(n)) {
                int n3 = n;
                this._doResponse[n3] = this._doResponse[n3] - 1;
            }
        }
        if (this._doResponse[n] == 0 && this._requestedDo(n)) {
            switch (n) {
                default: 
            }
            if (this._stateIsDo(n) || this._requestedDo(n)) {
                this._sendDont(n);
            }
            this._setWantDont(n);
        }
        this._setDont(n);
    }

    void _processSuboption(int[] arrn, int n) throws IOException {
        if (n > 0) {
            if (this.optionHandlers[arrn[0]] != null) {
                int[] arrn2 = this.optionHandlers[arrn[0]].answerSubnegotiation(arrn, n);
                this._sendSubnegotiation(arrn2);
            } else if (n > 1 && arrn[0] == 24 && arrn[1] == 1) {
                this._sendTerminalType();
            }
        }
    }

    final synchronized void _sendTerminalType() throws IOException {
        if (this.terminalType != null) {
            this._output_.write(_COMMAND_SB);
            this._output_.write(_COMMAND_IS);
            this._output_.write(this.terminalType.getBytes());
            this._output_.write(_COMMAND_SE);
            this._output_.flush();
        }
    }

    final synchronized void _sendSubnegotiation(int[] arrn) throws IOException {
        if (arrn != null) {
            byte[] arrby = new byte[arrn.length];
            for (int i = 0; i < arrn.length; ++i) {
                arrby[i] = (byte)arrn[i];
            }
            this._output_.write(_COMMAND_SB);
            this._output_.write(arrby);
            this._output_.write(_COMMAND_SE);
            this._output_.flush();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    final synchronized void _processAYTResponse() {
        if (!this.aytFlag) {
            Object object = this.aytMonitor;
            synchronized (object) {
                this.aytFlag = true;
                try {
                    this.aytMonitor.notifyAll();
                }
                catch (Exception exception) {
                    System.err.println("Exception notifying:" + exception.getMessage());
                }
            }
        }
    }

    protected void _connectAction_() throws IOException {
        int n;
        for (n = 0; n < 256; ++n) {
            this._doResponse[n] = 0;
            this._willResponse[n] = 0;
            this._options[n] = 0;
            if (this.optionHandlers[n] == null) continue;
            this.optionHandlers[n].setDo(false);
            this.optionHandlers[n].setWill(false);
        }
        super._connectAction_();
        this._input_ = new BufferedInputStream(this._input_);
        this._output_ = new BufferedOutputStream(this._output_);
        for (n = 0; n < 256; ++n) {
            if (this.optionHandlers[n] == null) continue;
            if (this.optionHandlers[n].getInitLocal()) {
                try {
                    this._requestWill(this.optionHandlers[n].getOptionCode());
                }
                catch (IOException iOException) {
                    System.err.println("Exception while initializing option: " + iOException.getMessage());
                }
            }
            if (!this.optionHandlers[n].getInitRemote()) continue;
            try {
                this._requestDo(this.optionHandlers[n].getOptionCode());
                continue;
            }
            catch (IOException iOException) {
                System.err.println("Exception while initializing option: " + iOException.getMessage());
            }
        }
    }

    final synchronized void _sendDo(int n) throws IOException {
        this._output_.write(_COMMAND_DO);
        this._output_.write(n);
        this._output_.flush();
    }

    final synchronized void _requestDo(int n) throws IOException {
        if (this._doResponse[n] == 0 && this._stateIsDo(n) || this._requestedDo(n)) {
            return;
        }
        this._setWantDo(n);
        int n2 = n;
        this._doResponse[n2] = this._doResponse[n2] + 1;
        this._sendDo(n);
    }

    final synchronized void _sendDont(int n) throws IOException {
        this._output_.write(_COMMAND_DONT);
        this._output_.write(n);
        this._output_.flush();
    }

    final synchronized void _requestDont(int n) throws IOException {
        if (this._doResponse[n] == 0 && this._stateIsDont(n) || this._requestedDont(n)) {
            return;
        }
        this._setWantDont(n);
        int n2 = n;
        this._doResponse[n2] = this._doResponse[n2] + 1;
        this._sendDont(n);
    }

    final synchronized void _sendWill(int n) throws IOException {
        this._output_.write(_COMMAND_WILL);
        this._output_.write(n);
        this._output_.flush();
    }

    final synchronized void _requestWill(int n) throws IOException {
        if (this._willResponse[n] == 0 && this._stateIsWill(n) || this._requestedWill(n)) {
            return;
        }
        this._setWantWill(n);
        int n2 = n;
        this._doResponse[n2] = this._doResponse[n2] + 1;
        this._sendWill(n);
    }

    final synchronized void _sendWont(int n) throws IOException {
        this._output_.write(_COMMAND_WONT);
        this._output_.write(n);
        this._output_.flush();
    }

    final synchronized void _requestWont(int n) throws IOException {
        if (this._willResponse[n] == 0 && this._stateIsWont(n) || this._requestedWont(n)) {
            return;
        }
        this._setWantWont(n);
        int n2 = n;
        this._doResponse[n2] = this._doResponse[n2] + 1;
        this._sendWont(n);
    }

    final synchronized void _sendByte(int n) throws IOException {
        this._output_.write(n);
        this._spyWrite(n);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    final boolean _sendAYT(long l) throws IOException, IllegalArgumentException, InterruptedException {
        boolean bl = false;
        Object object = this.aytMonitor;
        synchronized (object) {
            Telnet telnet = this;
            synchronized (telnet) {
                this.aytFlag = false;
                this._output_.write(_COMMAND_AYT);
                this._output_.flush();
            }
            try {
                this.aytMonitor.wait(l);
                if (!this.aytFlag) {
                    bl = false;
                    this.aytFlag = true;
                } else {
                    bl = true;
                }
            }
            catch (IllegalMonitorStateException illegalMonitorStateException) {
                System.err.println("Exception processing AYT:" + illegalMonitorStateException.getMessage());
            }
        }
        return bl;
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    void addOptionHandler(TelnetOptionHandler telnetOptionHandler) throws InvalidTelnetOptionException {
        int n = telnetOptionHandler.getOptionCode();
        if (!TelnetOption.isValidOption(n)) throw new InvalidTelnetOptionException("Invalid Option Code", n);
        if (this.optionHandlers[n] != null) throw new InvalidTelnetOptionException("Already registered option", n);
        this.optionHandlers[n] = telnetOptionHandler;
        if (!this.isConnected()) return;
        if (telnetOptionHandler.getInitLocal()) {
            try {
                this._requestWill(n);
            }
            catch (IOException iOException) {
                System.err.println("Exception while initializing option: " + iOException.getMessage());
            }
        }
        if (!telnetOptionHandler.getInitRemote()) return;
        try {
            this._requestDo(n);
            return;
        }
        catch (IOException iOException) {
            System.err.println("Exception while initializing option: " + iOException.getMessage());
        }
    }

    void deleteOptionHandler(int n) throws InvalidTelnetOptionException {
        if (TelnetOption.isValidOption(n)) {
            if (this.optionHandlers[n] == null) {
                throw new InvalidTelnetOptionException("Unregistered option", n);
            }
            TelnetOptionHandler telnetOptionHandler = this.optionHandlers[n];
            this.optionHandlers[n] = null;
            if (telnetOptionHandler.getWill()) {
                try {
                    this._requestWont(n);
                }
                catch (IOException iOException) {
                    System.err.println("Exception while turning off option: " + iOException.getMessage());
                }
            }
            if (telnetOptionHandler.getDo()) {
                try {
                    this._requestDont(n);
                }
                catch (IOException iOException) {
                    System.err.println("Exception while turning off option: " + iOException.getMessage());
                }
            }
        } else {
            throw new InvalidTelnetOptionException("Invalid Option Code", n);
        }
    }

    void _registerSpyStream(OutputStream outputStream) {
        this.spyStream = outputStream;
    }

    void _stopSpyStream() {
        this.spyStream = null;
    }

    void _spyRead(int n) {
        if (this.spyStream != null) {
            try {
                if (n != 13) {
                    this.spyStream.write(n);
                    if (n == 10) {
                        this.spyStream.write(13);
                    }
                    this.spyStream.flush();
                }
            }
            catch (Exception exception) {
                this.spyStream = null;
            }
        }
    }

    void _spyWrite(int n) {
        if (!(this._stateIsDo(TelnetOption.ECHO) && this._requestedDo(TelnetOption.ECHO) || this.spyStream == null)) {
            try {
                this.spyStream.write(n);
                this.spyStream.flush();
            }
            catch (Exception exception) {
                this.spyStream = null;
            }
        }
    }

    public void registerNotifHandler(TelnetNotificationHandler telnetNotificationHandler) {
        this.__notifhand = telnetNotificationHandler;
    }

    public void unregisterNotifHandler() {
        this.__notifhand = null;
    }
}

