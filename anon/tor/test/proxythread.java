/*
 * Decompiled with CFR 0.150.
 */
package anon.tor.test;

import anon.tor.TorChannel;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class proxythread
implements Runnable {
    private OutputStream torout;
    private InputStream torin;
    private OutputStream out;
    private InputStream in;
    private Socket client;
    private Thread t;
    private TorChannel channel;

    public proxythread(Socket socket, TorChannel torChannel) throws IOException {
        this.torin = torChannel.getInputStream();
        this.torout = torChannel.getOutputStream();
        this.in = socket.getInputStream();
        this.out = socket.getOutputStream();
        this.client = socket;
        this.channel = torChannel;
    }

    public void start() {
        this.t = new Thread((Runnable)this, "Tor proxy thread");
        this.t.start();
    }

    public void stop() {
        try {
            while (this.torin.available() > 0) {
                byte[] arrby = new byte[this.torin.available()];
                int n = this.torin.read(arrby);
                this.out.write(arrby, 0, n);
                this.out.flush();
            }
        }
        catch (Exception exception) {
            // empty catch block
        }
        this.channel.close();
        try {
            this.client.close();
        }
        catch (Exception exception) {
            System.out.println("Fehler beim schliessen des kanals");
        }
        System.out.println("kanal wird geschlossen");
        this.t.stop();
    }

    public void run() {
        while (true) {
            try {
                while (true) {
                    int n;
                    byte[] arrby;
                    if (this.torin.available() > 0) {
                        arrby = new byte[this.torin.available()];
                        n = this.torin.read(arrby);
                        this.out.write(arrby, 0, n);
                        this.out.flush();
                        continue;
                    }
                    while (this.in.available() > 0) {
                        arrby = new byte[this.in.available()];
                        n = this.in.read(arrby);
                        this.torout.write(arrby, 0, n);
                        this.torout.flush();
                    }
                    if (this.channel.isClosedByPeer()) {
                        this.stop();
                    }
                    Thread.sleep(20L);
                }
            }
            catch (Exception exception) {
                System.out.println("Exception catched : " + exception.getLocalizedMessage());
                exception.printStackTrace();
                this.stop();
                continue;
            }
            break;
        }
    }
}

