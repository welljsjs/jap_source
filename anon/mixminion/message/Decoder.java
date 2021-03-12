/*
 * Decompiled with CFR 0.150.
 */
package anon.mixminion.message;

import anon.mixminion.message.FragmentContainer;
import anon.mixminion.message.Keyring;
import anon.mixminion.message.MixMinionCryptoUtil;
import anon.util.Base64;
import anon.util.ByteArrayUtil;
import java.io.IOException;
import java.io.LineNumberReader;
import java.io.StringReader;
import java.util.Vector;

public class Decoder {
    private final int KEY_LEN = 16;
    private final int MAXHOPS = 20;
    private final int PACKETSIZE = 28625;
    private String m_message;
    private String m_password;

    public Decoder(String string, String string2) {
        this.m_message = string;
        this.m_password = string2;
    }

    public String decode() throws IOException {
        Vector vector = new Keyring(this.m_password).getUserSecrets();
        byte[] arrby = new byte[]{};
        String string = "";
        String string2 = "";
        boolean bl = false;
        boolean bl2 = false;
        LineNumberReader lineNumberReader = new LineNumberReader(new StringReader(this.m_message));
        String string3 = lineNumberReader.readLine();
        while (!string3.startsWith("-----BEGIN TYPE III ANONYMOUS MESSAGE-----")) {
            string3 = lineNumberReader.readLine();
            if (string3.intern() != ".") continue;
            return null;
        }
        string3 = lineNumberReader.readLine();
        if (!(string3 = string3.substring(14)).equals("encrypted")) {
            return null;
        }
        string3 = lineNumberReader.readLine();
        arrby = Base64.decode(string3.substring(17));
        string3 = lineNumberReader.readLine();
        string3 = lineNumberReader.readLine();
        while (!string3.startsWith("-----END TYPE III ANONYMOUS MESSAGE-----")) {
            string = string + string3 + "\n";
            string3 = lineNumberReader.readLine();
        }
        byte[] arrby2 = Base64.decode(string);
        block2: for (int i = 0; i < vector.size(); ++i) {
            byte[] arrby3 = (byte[])vector.elementAt(i);
            byte[] arrby4 = ByteArrayUtil.conc(arrby, arrby3, "Validate".getBytes());
            if (MixMinionCryptoUtil.hash(arrby4)[19] != 0) continue;
            byte[] arrby5 = ByteArrayUtil.copy(MixMinionCryptoUtil.hash(ByteArrayUtil.conc(arrby, arrby3, "Generate".getBytes())), 0, 16);
            byte[] arrby6 = MixMinionCryptoUtil.createPRNG(arrby5, 320);
            byte[] arrby7 = arrby2;
            for (int j = 0; j < 20; ++j) {
                byte[] arrby8 = MixMinionCryptoUtil.hash(ByteArrayUtil.conc(ByteArrayUtil.copy(arrby6, j * 16, 16), "PAYLOAD ENCRYPT".getBytes()));
                arrby7 = MixMinionCryptoUtil.SPRP_Encrypt(arrby8, arrby7);
                if (this.testPayload(arrby7) == 1) {
                    int n = this.byteToInt(ByteArrayUtil.copy(arrby7, 0, 2), 0);
                    arrby7 = ByteArrayUtil.copy(arrby7, 22, n);
                    arrby7 = MixMinionCryptoUtil.decompressData(arrby7);
                    string2 = new String(arrby7);
                    bl = true;
                    continue block2;
                }
                if (this.testPayload(arrby7) != 2) continue;
                System.out.println("Fragment");
                bl2 = true;
                string2 = this.trytoReassemble(arrby7);
                if (string2 == null) continue;
                bl = true;
            }
        }
        String string4 = "";
        if (!bl) {
            string2 = bl2 ? (string4 = "From: JAP-Decoder\nTo: local user\nSubject: Fragment\n\nFuer die Decodierung der Nachricht werden weitere Fragmente benoetigt.\n\n") : "From: JAP-Decoder\nTo: local user\nSubject: Fehler\n\nLeider konnte nichts decodiert werden.\n";
        } else {
            string4 = "From: JAP-Decoder\n";
            lineNumberReader = new LineNumberReader(new StringReader(string2));
            string3 = lineNumberReader.readLine();
            if (string3.startsWith("MIME")) {
                string4 = string4 + string3 + "\n";
                string3 = lineNumberReader.readLine();
            }
            string4 = string4 + "To: local user\n";
            string3 = "Subject: " + string3.substring(7);
            while (string3 != null) {
                string4 = string4 + string3 + "\n";
                string3 = lineNumberReader.readLine();
            }
            string2 = string4;
        }
        return string2;
    }

    private int testPayload(byte[] arrby) {
        byte[] arrby2 = ByteArrayUtil.copy(arrby, 2, 20);
        byte[] arrby3 = MixMinionCryptoUtil.hash(ByteArrayUtil.copy(arrby, 22, arrby.length - 22));
        int n = this.byteToInt(ByteArrayUtil.copy(arrby, 0, 2), 0);
        if (ByteArrayUtil.equal(arrby2, arrby3)) {
            return 1;
        }
        arrby2 = MixMinionCryptoUtil.hash(ByteArrayUtil.copy(arrby, 23, arrby.length - 23));
        if (ByteArrayUtil.equal(arrby2, arrby3 = ByteArrayUtil.copy(arrby, 3, 20))) {
            return 2;
        }
        return 0;
    }

    private String trytoReassemble(byte[] arrby) {
        int n;
        String string = null;
        Vector<FragmentContainer> vector = null;
        if (vector == null) {
            vector = new Vector<FragmentContainer>();
        }
        byte[] arrby2 = ByteArrayUtil.copy(arrby, 23, 20);
        int n2 = this.byteToInt(ByteArrayUtil.copy(arrby, 1, 2), 0);
        double d = this.byteToInt(ByteArrayUtil.copy(arrby, 43, 4), 0);
        System.out.println("MessageSize:" + d + " index: " + n2);
        arrby = ByteArrayUtil.copy(arrby, 47, 28625);
        FragmentContainer fragmentContainer = null;
        int n3 = -1;
        for (n = 0; n < vector.size(); ++n) {
            FragmentContainer fragmentContainer2 = (FragmentContainer)vector.elementAt(n);
            if (!ByteArrayUtil.equal(arrby2, fragmentContainer2.getID())) continue;
            fragmentContainer = fragmentContainer2;
            n3 = n;
            break;
        }
        if (n3 == -1) {
            n = (int)Math.ceil(d / 28625.0);
            System.out.println("Numberof: " + n);
            fragmentContainer = new FragmentContainer(arrby2, n);
        }
        if (fragmentContainer.addFragment(arrby, n2)) {
            byte[] arrby3 = fragmentContainer.reassembleMessage();
            arrby3 = ByteArrayUtil.copy(arrby3, 0, (int)d);
            arrby3 = this.unwhiten(arrby3);
            int n4 = this.byteToInt(ByteArrayUtil.copy(arrby3, 3, 1), 0);
            arrby3 = ByteArrayUtil.copy(arrby3, 4 + n4, arrby3.length - 4 - n4);
            arrby3 = MixMinionCryptoUtil.decompressData(arrby3);
            string = new String(arrby3);
        }
        if (n3 == -1) {
            vector.addElement(fragmentContainer);
        } else {
            vector.setElementAt(fragmentContainer, n3);
        }
        return string;
    }

    private int byteToInt(byte[] arrby, int n) {
        int n2 = 0;
        for (int i = 0; i < arrby.length; ++i) {
            int n3 = (arrby.length - 1 - i) * 8;
            n2 += (arrby[i + n] & 0xFF) << n3;
        }
        return n2;
    }

    private byte[] unwhiten(byte[] arrby) {
        byte[] arrby2 = new byte[]{87, 72, 73, 84, 69, 78};
        byte[] arrby3 = ByteArrayUtil.conc(arrby2, "WHITEN".getBytes());
        return MixMinionCryptoUtil.SPRP_Decrypt(MixMinionCryptoUtil.hash(arrby3), arrby);
    }
}

