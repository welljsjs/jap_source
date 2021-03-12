/*
 * Decompiled with CFR 0.150.
 */
package HTTPClient;

import HTTPClient.SocksClient;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;

final class EstablishConnection
extends Thread {
    String actual_host;
    int actual_port;
    IOException exception;
    Socket sock;
    SocksClient Socks_client;
    boolean close;

    EstablishConnection(String string, int n, SocksClient socksClient) {
        super("EstablishConnection (" + string + ":" + n + ")");
        try {
            this.setDaemon(true);
        }
        catch (SecurityException securityException) {
            // empty catch block
        }
        this.actual_host = string;
        this.actual_port = n;
        this.Socks_client = socksClient;
        this.exception = null;
        this.sock = null;
        this.close = false;
    }

    public void run() {
        block10: {
            try {
                if (this.Socks_client != null) {
                    this.sock = this.Socks_client.getSocket(this.actual_host, this.actual_port);
                    break block10;
                }
                InetAddress[] arrinetAddress = InetAddress.getAllByName(this.actual_host);
                for (int i = 0; i < arrinetAddress.length; ++i) {
                    try {
                        this.sock = new Socket(arrinetAddress[i], this.actual_port);
                        break;
                    }
                    catch (SocketException socketException) {
                        if (i != arrinetAddress.length - 1 && !this.close) continue;
                        this.exception = socketException;
                        break;
                    }
                }
            }
            catch (IOException iOException) {
                this.exception = iOException;
            }
            catch (Exception exception) {
                this.exception = new IOException("UnknownIOExcpetion in EstablishConnection: " + exception.getMessage());
            }
        }
        if (this.close) {
            try {
                this.sock.close();
            }
            catch (Exception exception) {
                // empty catch block
            }
            this.sock = null;
        }
    }

    IOException getException() {
        return this.exception;
    }

    Socket getSocket() {
        return this.sock;
    }

    void forget() {
        this.close = true;
    }
}

