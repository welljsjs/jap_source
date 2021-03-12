/*
 * Decompiled with CFR 0.150.
 */
package anon.mixminion;

import anon.mixminion.message.ReplyBlock;
import java.io.IOException;
import java.io.LineNumberReader;
import java.io.StringReader;
import java.util.Vector;

public class EMail {
    private String[] m_receiver = new String[1];
    private String m_payload = null;
    private Vector m_replyblocks = new Vector();
    private String m_type = "";
    private String m_multipartid = "";

    public EMail(String[] arrstring, String string) {
        if (this.testonEncrypted(string)) {
            this.m_type = "ENC";
            this.m_payload = string;
        } else {
            try {
                this.m_replyblocks = ReplyBlock.parseReplyBlocks(string, null);
            }
            catch (IOException iOException) {
                iOException.printStackTrace();
            }
            if (this.m_replyblocks.size() > 0) {
                this.m_type = "RPL";
                this.m_receiver[0] = "anonymous@fragmented.de";
                try {
                    string = ReplyBlock.removeRepyBlocks(string);
                }
                catch (IOException iOException) {
                    iOException.printStackTrace();
                }
            } else {
                this.m_type = "NOR";
                this.m_receiver = arrstring;
            }
            try {
                this.m_payload = this.trimPayload(string);
            }
            catch (IOException iOException) {
                iOException.printStackTrace();
            }
        }
    }

    private boolean testonEncrypted(String string) {
        block3: {
            LineNumberReader lineNumberReader = new LineNumberReader(new StringReader(string));
            String string2 = "start";
            do {
                try {
                    string2 = lineNumberReader.readLine();
                }
                catch (IOException iOException) {
                    break block3;
                }
                if (string2 == null) break block3;
            } while (!string2.startsWith("Message-type: encrypted"));
            return true;
        }
        return false;
    }

    public String[] getReceiver() {
        return this.m_receiver;
    }

    public String getPayload() {
        return this.m_payload;
    }

    public void addRBtoPayload(String string) {
        this.m_payload = this.m_multipartid.equals("") ? this.m_payload + string : this.m_payload.substring(0, this.m_payload.indexOf("--" + this.m_multipartid + "--")) + "\n--" + this.m_multipartid + "\nContent-Type: text/plain; charset=ISO-8859-15\n" + "Content-Transfer-Encoding: 7bit\n" + string + "\n--" + this.m_multipartid + "--";
    }

    public String getType() {
        return this.m_type;
    }

    public Vector getReplyBlocks() {
        return this.m_replyblocks;
    }

    public String toString() {
        String string = "";
        for (int i = 0; i < this.m_receiver.length; ++i) {
            string = string + "[" + this.m_receiver[i] + "]\n";
        }
        string = string + this.m_payload;
        return string;
    }

    private String trimPayload(String string) throws IOException {
        String string2 = "";
        String string3 = "";
        String string4 = "";
        if (this.m_type.equals("NOR")) {
            string3 = string3 + "\nMessage created with JAP/Mixminion Anonymous Mailing\n\n";
            string4 = "- ";
        }
        LineNumberReader lineNumberReader = new LineNumberReader(new StringReader(string));
        String string5 = lineNumberReader.readLine();
        while (!string5.startsWith("Subject")) {
            string5 = lineNumberReader.readLine();
        }
        string2 = string2 + "Titel: " + string5.substring(9) + "\n";
        while (string5.length() > 0) {
            string5 = lineNumberReader.readLine();
            if (!string5.startsWith("Content-Type: multipart/mixed;")) continue;
            string5 = lineNumberReader.readLine();
            string3 = string3 + "MIME-Version: 1.0\n" + string2 + "Content-Type: multipart/mixed;\n" + string5.substring(0, 11) + string4 + string5.substring(11) + "\n";
            this.m_multipartid = string5.substring(11, string5.length() - 1);
        }
        if (this.m_multipartid.equals("")) {
            string3 = string3 + string2;
        }
        while (string5 != null) {
            string3 = string3 + string5 + "\n";
            string5 = lineNumberReader.readLine();
        }
        return string3;
    }
}

