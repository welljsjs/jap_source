/*
 * Decompiled with CFR 0.150.
 */
package anon.mixminion;

import anon.shared.AbstractChannel;
import java.io.IOException;
import java.io.LineNumberReader;
import java.io.StringReader;
import java.util.Vector;

public class MixminionPOPChannel
extends AbstractChannel {
    private int m_state = 0;
    private Vector m_messages = new Vector();
    private String[] m_deleted = null;

    public MixminionPOPChannel() {
        try {
            this.m_deleted = new String[this.m_messages.size()];
            for (int i = 0; i < this.m_messages.size(); ++i) {
                this.m_deleted[i] = (String)this.m_messages.elementAt(i);
            }
            String string = "+OK JAP POP3 server ready\r\n";
            this.toClient(string);
        }
        catch (IOException iOException) {
            iOException.printStackTrace();
        }
    }

    protected void close_impl() {
    }

    protected void toClient(String string) throws IOException {
        this.recv(string.getBytes(), 0, string.length());
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    protected void send(byte[] arrby, int n) throws IOException {
        String string = new String(arrby, 0, n);
        if (this.m_state == 0) {
            if (string.toUpperCase().startsWith("USER")) {
                this.m_state = 1;
                this.toClient("+OK\r\n");
                return;
            } else {
                if (!string.toUpperCase().startsWith("AUTH") && !string.toUpperCase().startsWith("CAPA")) throw new RuntimeException("(State=" + this.m_state + ") Didn't understand this Command '" + string + "'");
                this.toClient("-ERR unrecognized\r\n");
            }
            return;
        } else if (this.m_state == 1) {
            if (!string.toUpperCase().startsWith("PASS")) throw new RuntimeException("(State=" + this.m_state + ") Didn't understand this Command '" + string + "'");
            this.m_state = 2;
            this.toClient("+OK\r\n");
            return;
        } else if (this.m_state == 2) {
            if (string.toUpperCase().startsWith("STAT")) {
                int n2 = 0;
                for (int i = 0; i < this.m_messages.size(); ++i) {
                    n2 += ((String)this.m_messages.elementAt(i)).getBytes().length;
                }
                this.toClient("+OK " + this.m_messages.size() + " " + n2 + "\r\n");
                return;
            } else if (string.toUpperCase().startsWith("LIST")) {
                this.m_state = 3;
                this.toClient("+OK " + this.m_messages.size() + " messages" + "\r\n");
                for (int i = 0; i < this.m_messages.size(); ++i) {
                    this.toClient(i + 1 + " " + ((String)this.m_messages.elementAt(i)).getBytes().length + "\r\n");
                }
                this.toClient(".\r\n");
                return;
            } else {
                if (!string.toUpperCase().startsWith("QUIT")) throw new RuntimeException("(State=" + this.m_state + ") Didn't understand this Command '" + string + "'");
                this.toClient("+OK\r\n");
            }
            return;
        } else {
            if (this.m_state != 3) return;
            if (string.startsWith("UIDL") || string.startsWith("XTND")) {
                this.toClient("-ERR unrecognized\r\n");
                return;
            } else if (string.startsWith("TOP")) {
                int n3 = Integer.parseInt(string.substring(4, 5));
                int n4 = 1;
                System.out.println("id: " + n3 + " lines: " + n4);
                this.toClient("+OK " + ((String)this.m_messages.elementAt(n3 - 1)).getBytes().length + " octets\r\n");
                LineNumberReader lineNumberReader = new LineNumberReader(new StringReader((String)this.m_messages.elementAt(n3 - 1)));
                String string2 = lineNumberReader.readLine();
                for (int i = 0; i < 4 + n4 || string2 == null; ++i) {
                    this.toClient(string2 + "\r\n");
                    string2 = lineNumberReader.readLine();
                }
                this.toClient(".\r\n");
                return;
            } else if (string.startsWith("RETR")) {
                int n5 = 1;
                this.toClient("+OK " + ((String)this.m_messages.elementAt(n5 - 1)).getBytes().length + " octets\r\n");
                LineNumberReader lineNumberReader = new LineNumberReader(new StringReader((String)this.m_messages.elementAt(n5 - 1)));
                String string3 = lineNumberReader.readLine();
                while (string3 != null) {
                    this.toClient(string3 + "\r\n");
                    string3 = lineNumberReader.readLine();
                }
                this.toClient(".\r\n");
                return;
            } else if (string.startsWith("DELE")) {
                int n6 = 1;
                this.m_deleted[n6 - 1] = null;
                this.toClient("+OK message " + n6 + " deleted\r\n");
                return;
            } else {
                if (!string.toUpperCase().startsWith("QUIT")) throw new RuntimeException("(State=" + this.m_state + ") Didn't understand this Command '" + string + "'");
                this.m_messages = new Vector();
                for (int i = 0; i < this.m_deleted.length; ++i) {
                    if (this.m_deleted[i] == null) continue;
                    this.m_messages.addElement(this.m_deleted[i]);
                }
                if (this.m_messages.size() == 0) {
                    this.m_messages = null;
                }
                this.toClient("+OK\r\n");
            }
        }
    }

    public int getOutputBlockSize() {
        return 1000;
    }
}

