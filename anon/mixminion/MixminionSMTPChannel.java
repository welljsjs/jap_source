/*
 * Decompiled with CFR 0.150.
 */
package anon.mixminion;

import anon.mixminion.EMail;
import anon.mixminion.Mixminion;
import anon.mixminion.PasswordManager;
import anon.mixminion.message.Message;
import anon.shared.AbstractChannel;
import java.io.IOException;
import java.util.Vector;

public class MixminionSMTPChannel
extends AbstractChannel {
    private int m_state = 0;
    private Vector m_receiver = new Vector();
    private String m_text = "";

    public MixminionSMTPChannel() {
        try {
            String string = "220 127.0.0.1 SMTP JAP_MailServer\r\n";
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
            if (string.toUpperCase().startsWith("HELO")) {
                this.m_state = 2;
                this.toClient("250 OK\r\n");
                return;
            } else {
                if (this.m_state != 0) throw new RuntimeException("(State=" + this.m_state + ") Didn't understand this Command '" + string + "'");
                if (!string.toUpperCase().startsWith("EHLO")) throw new RuntimeException("(State=" + this.m_state + ") Didn't understand this Command '" + string + "'");
                this.m_state = 1;
                this.toClient("503\r\n");
            }
            return;
        } else if (this.m_state == 1) {
            if (!string.toUpperCase().startsWith("HELO")) throw new RuntimeException("(State=" + this.m_state + ") Didn't understand this Command '" + string + "'");
            this.m_state = 2;
            this.toClient("250 OK\r\n");
            return;
        } else if (this.m_state == 2) {
            if (!string.toUpperCase().startsWith("MAIL FROM")) throw new RuntimeException("(State=" + this.m_state + ") Didn't understand this Command '" + string + "'");
            this.m_receiver.removeAllElements();
            this.m_text = "";
            this.m_state = 3;
            this.toClient("250 OK\r\n");
            return;
        } else if (this.m_state == 3) {
            if (string.toUpperCase().startsWith("RCPT TO")) {
                String string2 = string.substring(string.indexOf(60) + 1, string.indexOf(62));
                this.m_receiver.addElement(string2);
                this.toClient("250 OK\r\n");
                return;
            } else {
                if (!string.toUpperCase().startsWith("DATA")) throw new RuntimeException("(State=" + this.m_state + ") Didn't understand this Command '" + string + "'");
                this.m_state = 4;
                this.toClient("354 Start mail input; end with <CRLF>.<CRLF>\r\n");
            }
            return;
        } else if (this.m_state == 4) {
            this.m_text = this.m_text + string;
            if (!this.m_text.endsWith("\r\n.\r\n")) return;
            this.m_text = this.m_text.substring(0, this.m_text.length() - 5);
            Object[] arrobject = new String[this.m_receiver.size()];
            this.m_receiver.copyInto(arrobject);
            EMail eMail = new EMail((String[])arrobject, this.m_text);
            boolean bl = true;
            String string3 = Mixminion.getMyEMail();
            if (string3 == "") {
                this.toClient("554 Keine Reply-E-Mail im JAP spezifiziert!\r\n");
                return;
            }
            if (!bl) return;
            boolean bl2 = false;
            Message message = null;
            int n2 = Mixminion.getRouteLen();
            PasswordManager passwordManager = new PasswordManager();
            String string4 = passwordManager.getPassword();
            message = new Message(eMail, n2, string3, string4, 3);
            bl2 = message.send();
            this.m_state = 5;
            if (bl2) {
                this.toClient("250 OK\r\n");
                return;
            } else {
                String string5 = "";
                string5 = message.getDecoded() != null ? "250 OK\r\n" : "554 Fehler beim Versenden der eMail zum MixMinionServer!\r\n";
                this.toClient(string5);
            }
            return;
        } else {
            if (this.m_state != 5) throw new RuntimeException("(State=" + this.m_state + ") This State is not possible");
            if (string.toUpperCase().startsWith("QUIT")) {
                this.m_receiver.addElement(string);
                this.toClient("221 Bye\r\n");
                this.m_state = 99;
                return;
            } else if (string.toUpperCase().startsWith("MAIL FROM")) {
                this.m_receiver.removeAllElements();
                this.m_text = "";
                this.m_state = 3;
                this.toClient("250 OK\r\n");
                return;
            } else {
                if (!string.toUpperCase().startsWith("RSET")) throw new RuntimeException("(State=" + this.m_state + ") Didn't understand this Command '" + string + "'");
                this.m_state = 2;
                this.toClient("250 OK\r\n");
            }
        }
    }

    public int getOutputBlockSize() {
        return 1000;
    }
}

