/*
 * Decompiled with CFR 0.150.
 */
package org.apache.commons.net.telnet;

import java.io.IOException;
import java.io.OutputStream;
import org.apache.commons.net.telnet.TelnetClient;
import org.apache.commons.net.telnet.TelnetOption;

final class TelnetOutputStream
extends OutputStream {
    private TelnetClient __client;
    private boolean __convertCRtoCRLF = true;
    private boolean __lastWasCR = false;

    TelnetOutputStream(TelnetClient telnetClient) {
        this.__client = telnetClient;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void write(int n) throws IOException {
        TelnetClient telnetClient = this.__client;
        synchronized (telnetClient) {
            n &= 0xFF;
            if (this.__client._requestedWont(TelnetOption.BINARY)) {
                if (this.__lastWasCR) {
                    if (this.__convertCRtoCRLF) {
                        this.__client._sendByte(10);
                        if (n == 10) {
                            this.__lastWasCR = false;
                            return;
                        }
                    } else if (n != 10) {
                        this.__client._sendByte(0);
                    }
                }
                this.__lastWasCR = false;
                switch (n) {
                    case 13: {
                        this.__client._sendByte(13);
                        this.__lastWasCR = true;
                        break;
                    }
                    case 255: {
                        this.__client._sendByte(255);
                        this.__client._sendByte(255);
                        break;
                    }
                    default: {
                        this.__client._sendByte(n);
                        break;
                    }
                }
            } else if (n == 255) {
                this.__client._sendByte(n);
                this.__client._sendByte(255);
            } else {
                this.__client._sendByte(n);
            }
        }
    }

    public void write(byte[] arrby) throws IOException {
        this.write(arrby, 0, arrby.length);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void write(byte[] arrby, int n, int n2) throws IOException {
        TelnetClient telnetClient = this.__client;
        synchronized (telnetClient) {
            while (n2-- > 0) {
                this.write(arrby[n++]);
            }
        }
    }

    public void flush() throws IOException {
        this.__client._flushOutputStream();
    }

    public void close() throws IOException {
        this.__client._closeOutputStream();
    }
}

