/*
 * Decompiled with CFR 0.150.
 */
package anon.mixminion.message;

import anon.mixminion.message.MixMinionCryptoUtil;
import anon.util.Base64;
import anon.util.ByteArrayUtil;
import java.io.IOException;
import java.io.LineNumberReader;
import java.io.StringReader;
import java.util.Calendar;
import java.util.Date;
import java.util.Vector;

public class Keyring {
    final int KEY_LEN = 16;
    static final long KEY_LIFETIME = 7776000L;
    private Vector m_mykeys = new Vector();
    private Vector m_expiring = new Vector();
    private String m_password;
    private int m_today;

    public Keyring(String string) {
        this.m_password = string;
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        int n = calendar.get(6);
        int n2 = calendar.get(1);
        this.m_today = ((n2 - 1970 - 1) * 365 + n) * 24 * 60 * 60;
        String string2 = null;
        if (string2 != null) {
            try {
                this.unpackKeyRing(string2);
            }
            catch (IOException iOException) {
                iOException.printStackTrace();
            }
        }
    }

    public Vector getUserSecrets() {
        return this.m_mykeys;
    }

    public byte[] getNewSecret() {
        return this.makeNewKey();
    }

    private String packKeyring() {
        byte[] arrby;
        byte[] arrby2;
        byte[] arrby3;
        byte[] arrby4 = new byte[]{};
        for (int i = 0; i < this.m_mykeys.size(); ++i) {
            arrby3 = new byte[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 0};
            arrby2 = (byte[])this.m_expiring.elementAt(i);
            if ((long)this.byteToInt(arrby2, 0) + 7776000L <= (long)this.m_today) continue;
            arrby = ByteArrayUtil.conc(arrby2, arrby3, (byte[])this.m_mykeys.elementAt(i));
            arrby = ByteArrayUtil.conc(new byte[1], ByteArrayUtil.inttobyte(arrby.length, 2), arrby);
            arrby4 = ByteArrayUtil.conc(arrby4, arrby);
        }
        String string = null;
        arrby3 = "KEYRING2".getBytes();
        arrby2 = new byte[]{18, 8, 32, 16, 52, 86, 7, 19};
        byte[] arrby5 = MixMinionCryptoUtil.randomArray(1024 * this.myceil(arrby4.length, 1024.0) - arrby4.length);
        byte[] arrby6 = ByteArrayUtil.conc(ByteArrayUtil.inttobyte(arrby4.length, 4), arrby4, arrby5);
        byte[] arrby7 = MixMinionCryptoUtil.hash(ByteArrayUtil.conc(arrby6, arrby2, arrby3));
        byte[] arrby8 = ByteArrayUtil.copy(MixMinionCryptoUtil.hash(ByteArrayUtil.conc(arrby2, this.m_password.getBytes(), arrby2)), 0, 16);
        arrby = MixMinionCryptoUtil.Encrypt(arrby8, ByteArrayUtil.conc(arrby6, arrby7));
        string = "-----BEGIN TYPE III KEYRING-----\nVersion: 0.1\n\n" + Base64.encodeBytes(arrby) + "\n-----END TYPE III KEYRING-----";
        return string;
    }

    private void unpackKeyRing(String string) throws IOException {
        byte[] arrby;
        byte[] arrby2;
        String string2 = "";
        LineNumberReader lineNumberReader = new LineNumberReader(new StringReader(string));
        lineNumberReader.readLine();
        lineNumberReader.readLine();
        lineNumberReader.readLine();
        String string3 = lineNumberReader.readLine();
        while (!string3.startsWith("-----END")) {
            string2 = string2 + string3;
            string3 = lineNumberReader.readLine();
        }
        byte[] arrby3 = Base64.decode(string2);
        byte[] arrby4 = new byte[]{18, 8, 32, 16, 52, 86, 7, 19};
        byte[] arrby5 = "KEYRING2".getBytes();
        byte[] arrby6 = ByteArrayUtil.copy(MixMinionCryptoUtil.hash(ByteArrayUtil.conc(arrby4, this.m_password.getBytes(), arrby4)), 0, 16);
        arrby3 = MixMinionCryptoUtil.Encrypt(arrby6, arrby3);
        byte[] arrby7 = ByteArrayUtil.copy(arrby3, arrby3.length - 20, 20);
        if (!ByteArrayUtil.equal(arrby7, arrby2 = MixMinionCryptoUtil.hash(arrby = ByteArrayUtil.conc(arrby3 = ByteArrayUtil.copy(arrby3, 0, arrby3.length - 20), arrby4, arrby5)))) {
            System.out.println("falsches Passwort!");
        }
        byte[] arrby8 = ByteArrayUtil.copy(arrby3, 0, 4);
        int n = this.byteToInt(arrby8, 0);
        arrby3 = ByteArrayUtil.copy(arrby3, 4, n);
        int n2 = 0;
        while (n2 < arrby3.length) {
            if (arrby3[n2] != 0) continue;
            byte[] arrby9 = ByteArrayUtil.copy(arrby3, n2 + 3, 4);
            this.m_expiring.addElement(arrby9);
            byte[] arrby10 = ByteArrayUtil.copy(arrby3, n2 + 17, 20);
            this.m_mykeys.addElement(arrby10);
            n2 += 37;
        }
    }

    private byte[] makeNewKey() {
        byte[] arrby = MixMinionCryptoUtil.randomArray(20);
        byte[] arrby2 = ByteArrayUtil.inttobyte((long)this.m_today + 7776000L, 4);
        this.m_mykeys.addElement(arrby);
        this.m_expiring.addElement(arrby2);
        this.saveKeyRing();
        return arrby;
    }

    public void changeKeyringPW(String string) {
        this.m_password = string;
        this.saveKeyRing();
    }

    private void saveKeyRing() {
    }

    private int byteToInt(byte[] arrby, int n) {
        int n2 = 0;
        for (int i = 0; i < arrby.length; ++i) {
            int n3 = (arrby.length - 1 - i) * 8;
            n2 += (arrby[i + n] & 0xFF) << n3;
        }
        return n2;
    }

    private int myceil(double d, double d2) {
        int n = (int)Math.ceil(d / d2);
        if (n == 0) {
            return 1;
        }
        return n;
    }
}

