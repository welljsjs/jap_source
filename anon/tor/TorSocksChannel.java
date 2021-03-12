/*
 * Decompiled with CFR 0.150.
 */
package anon.tor;

import anon.tor.Circuit;
import anon.tor.Tor;
import anon.tor.TorChannel;
import anon.util.ByteArrayUtil;
import java.io.IOException;
import java.util.Hashtable;
import logging.LogHolder;
import logging.LogType;

public class TorSocksChannel
extends TorChannel {
    private static final int SOCKS_WAIT_FOR_VERSION = 0;
    private static final int SOCKS5_WAIT_FOR_METHODS = 1;
    private static final int SOCKS5_WAIT_FOR_REQUEST = 2;
    private static final int SOCKS4_WAIT_FOR_REQUEST = 3;
    private static final int DATA_MODE = 4;
    private static final int SOCKS_5 = 5;
    private static final int SOCKS_4 = 4;
    private int m_state = 0;
    private int m_version;
    private byte[] m_data = null;
    private Tor m_Tor;

    public TorSocksChannel(Tor tor) throws IOException {
        this.m_Tor = tor;
        LogHolder.log(7, LogType.TOR, "new TorSocksChannel() - object created.");
    }

    protected void send(byte[] arrby, int n) throws IOException {
        switch (this.m_state) {
            case 0: {
                this.state_WaitForVersion(arrby, n);
                break;
            }
            case 1: {
                this.state_WaitForMethods(arrby, n);
                break;
            }
            case 2: {
                this.state_WaitForRequest_Socks5(arrby, n);
                break;
            }
            case 3: {
                this.state_WaitForRequest_Socks4(arrby, n);
                break;
            }
            case 4: {
                super.send(arrby, n);
                break;
            }
            default: {
                throw new IOException("illegal status");
            }
        }
    }

    private void state_WaitForVersion(byte[] arrby, int n) throws IOException {
        if (arrby != null && n > 0) {
            this.m_data = ByteArrayUtil.conc(this.m_data, arrby, n);
        }
        if (this.m_data.length > 1) {
            this.m_version = this.m_data[0];
            if (this.m_version != 5 && this.m_version != 4) {
                this.close();
                throw new IOException("Wrong Sock Protocol number");
            }
            this.m_data = ByteArrayUtil.copy(this.m_data, 1, this.m_data.length - 1);
            this.m_state = this.m_version == 5 ? 1 : 3;
            this.send(null, 0);
        }
    }

    private void state_WaitForMethods(byte[] arrby, int n) throws IOException {
        int n2;
        int n3;
        if (arrby != null && n > 0) {
            this.m_data = ByteArrayUtil.conc(this.m_data, arrby, n);
        }
        if (this.m_data.length > 1 && this.m_data.length >= (n3 = (n2 = this.m_data[0] & 0xFF) + 1)) {
            boolean bl = false;
            byte[] arrby2 = null;
            for (int i = 0; i < n2; ++i) {
                if (this.m_data[i + 1] != 0) continue;
                bl = true;
                arrby2 = new byte[]{5, 0};
                this.m_state = 2;
                break;
            }
            if (!bl) {
                arrby2 = new byte[]{5, -1};
            }
            super.recv(arrby2, 0, arrby2.length);
            if (!bl) {
                return;
            }
            this.m_data = ByteArrayUtil.copy(this.m_data, n3, this.m_data.length - n3);
            if (this.m_data.length > 0) {
                this.send(null, 0);
            }
        }
    }

    private void state_WaitForRequest_Socks4(byte[] arrby, int n) throws IOException {
        if (arrby != null && n > 0) {
            this.m_data = ByteArrayUtil.conc(this.m_data, arrby, n);
        }
        if (this.m_data.length <= 0) {
            return;
        }
        byte by = this.m_data[0];
        if (by != 1) {
            byte[] arrby2 = new byte[]{0, 91, 0, 0, 0, 0, 0, 0};
            this.m_data = null;
            super.recv(arrby2, 0, arrby2.length);
            return;
        }
        if (this.m_data.length >= 8) {
            byte[] arrby3 = null;
            int n2 = 0;
            String string = null;
            int n3 = 1;
            string = Integer.toString(this.m_data[3] & 0xFF) + "." + Integer.toString(this.m_data[4] & 0xFF) + "." + Integer.toString(this.m_data[5] & 0xFF) + "." + Integer.toString(this.m_data[6] & 0xFF);
            n2 = (this.m_data[1] & 0xFF) << 8 | this.m_data[2] & 0xFF;
            n3 += 6;
            int n4 = 7;
            while (n4 < this.m_data.length && this.m_data[n4] != 0) {
                ++n4;
                ++n3;
            }
            if (this.m_data[n4] != 0) {
                return;
            }
            ++n3;
            if (string.startsWith("0.0.0")) {
                ++n4;
                StringBuffer stringBuffer = new StringBuffer();
                while (n4 < this.m_data.length && this.m_data[n4] != 0) {
                    stringBuffer.append((char)this.m_data[n4]);
                    ++n4;
                    ++n3;
                }
                if (this.m_data[n4] != 0) {
                    return;
                }
                ++n3;
                string = stringBuffer.toString();
            }
            boolean bl = false;
            this.setDoNotCloseChannelOnErrorDuringConnect(true);
            Hashtable<Circuit, Circuit> hashtable = new Hashtable<Circuit, Circuit>();
            for (int i = 0; !bl && i < 3; ++i) {
                bl = true;
                Circuit circuit = null;
                try {
                    circuit = this.m_Tor.getCircuitForDestination(string, n2, hashtable);
                    if (circuit == null) {
                        arrby3 = new byte[]{0, 91, 0, 0, 0, 0, 0, 0};
                        super.recv(arrby3, 0, arrby3.length);
                        this.closedByPeer();
                        return;
                    }
                    if (circuit.connectChannel(this, string, n2) == 0) continue;
                    bl = false;
                    hashtable.put(circuit, circuit);
                    continue;
                }
                catch (IOException iOException) {
                    if (circuit != null) {
                        hashtable.put(circuit, circuit);
                    }
                    bl = false;
                }
            }
            if (!bl) {
                arrby3 = new byte[]{0, 91, 0, 0, 0, 0, 0, 0};
                super.recv(arrby3, 0, arrby3.length);
                this.closedByPeer();
                return;
            }
            arrby3 = new byte[]{0, 90, 0, 0, 0, 0, 0, 0};
            super.recv(arrby3, 0, arrby3.length);
            this.m_data = ByteArrayUtil.copy(this.m_data, n3, this.m_data.length - n3);
            this.m_state = 4;
            if (this.m_data.length > 0) {
                this.send(this.m_data, this.m_data.length);
                this.m_data = null;
            }
        }
    }

    private void state_WaitForRequest_Socks5(byte[] arrby, int n) throws IOException {
        if (arrby != null && n > 0) {
            this.m_data = ByteArrayUtil.conc(this.m_data, arrby, n);
        }
        if (this.m_data.length > 6) {
            byte[] arrby2 = null;
            int n2 = 0;
            String string = null;
            byte by = this.m_data[1];
            byte by2 = this.m_data[3];
            int n3 = 0;
            if (by != 1) {
                arrby2 = ByteArrayUtil.conc(new byte[]{5, 7, 0}, ByteArrayUtil.copy(this.m_data, 3, this.m_data.length - 3));
                this.m_data = null;
                super.recv(arrby2, 0, arrby2.length);
                return;
            }
            switch (by2) {
                case 1: {
                    if (this.m_data.length <= 9) break;
                    string = Integer.toString(this.m_data[4] & 0xFF) + "." + Integer.toString(this.m_data[5] & 0xFF) + "." + Integer.toString(this.m_data[6] & 0xFF) + "." + Integer.toString(this.m_data[7] & 0xFF);
                    n2 = (this.m_data[8] & 0xFF) << 8 | this.m_data[9] & 0xFF;
                    n3 = 10;
                    break;
                }
                case 3: {
                    int n4 = this.m_data[4] & 0xFF;
                    if (this.m_data.length < 7 + n4) break;
                    string = new String(this.m_data, 5, n4);
                    n2 = (this.m_data[5 + n4] & 0xFF) << 8 | this.m_data[6 + n4] & 0xFF;
                    n3 = n4 + 7;
                    break;
                }
                default: {
                    arrby2 = ByteArrayUtil.conc(new byte[]{5, 8, 0}, ByteArrayUtil.copy(this.m_data, 3, this.m_data.length - 3));
                    super.recv(arrby2, 0, arrby2.length);
                    this.m_data = null;
                }
            }
            if (string != null) {
                Circuit circuit;
                Hashtable<Circuit, Circuit> hashtable = new Hashtable<Circuit, Circuit>();
                boolean bl = false;
                this.setDoNotCloseChannelOnErrorDuringConnect(true);
                for (int i = 0; i < 3 && (circuit = this.m_Tor.getCircuitForDestination(string, n2, hashtable)) != null; ++i) {
                    if (circuit.connectChannel(this, string, n2) == 0) {
                        bl = true;
                        break;
                    }
                    hashtable.put(circuit, circuit);
                }
                if (!bl) {
                    arrby2 = ByteArrayUtil.conc(new byte[]{5, 1, 0}, ByteArrayUtil.copy(this.m_data, 3, n3 - 3));
                    super.recv(arrby2, 0, arrby2.length);
                    this.closedByPeer();
                    return;
                }
                arrby2 = ByteArrayUtil.conc(new byte[]{5, 0, 0}, ByteArrayUtil.copy(this.m_data, 3, n3 - 3));
                super.recv(arrby2, 0, arrby2.length);
                this.m_data = ByteArrayUtil.copy(this.m_data, n3, this.m_data.length - n3);
                this.m_state = 4;
                if (this.m_data.length > 0) {
                    this.send(this.m_data, this.m_data.length);
                    this.m_data = null;
                }
            }
        }
    }
}

