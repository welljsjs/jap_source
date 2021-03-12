/*
 * Decompiled with CFR 0.150.
 */
package anon.client.crypto;

import org.bouncycastle.crypto.engines.AESFastEngine;
import org.bouncycastle.crypto.params.KeyParameter;

public final class SymCipher {
    AESFastEngine m_aesEngine1 = new AESFastEngine();
    AESFastEngine m_aesEngine2 = new AESFastEngine();
    byte[] m_iv1 = new byte[16];
    byte[] m_iv2 = null;
    byte[] m_aesKeys = null;

    public SymCipher() {
        int n;
        for (n = 0; n < 16; ++n) {
            this.m_iv1[n] = 0;
        }
        this.m_iv2 = new byte[16];
        for (n = 0; n < 16; ++n) {
            this.m_iv2[n] = 0;
        }
    }

    public synchronized int setEncryptionKeyAES(byte[] arrby) {
        return this.setEncryptionKeyAES(arrby, 0, 16);
    }

    public synchronized int setEncryptionKeyAES(byte[] arrby, int n, int n2) {
        try {
            this.m_aesKeys = new byte[16];
            System.arraycopy(arrby, n, this.m_aesKeys, 0, 16);
            this.m_aesEngine1.init(true, new KeyParameter(this.m_aesKeys));
            this.m_aesEngine2.init(true, new KeyParameter(this.m_aesKeys));
            if (n2 == 16) {
                for (int i = 0; i < 16; ++i) {
                    this.m_iv1[i] = 0;
                    this.m_iv2[i] = 0;
                }
            } else {
                for (int i = 0; i < 16; ++i) {
                    this.m_iv1[i] = arrby[i + 16 + n];
                    this.m_iv2[i] = arrby[i + 16 + n];
                }
            }
            return 0;
        }
        catch (Exception exception) {
            this.m_aesKeys = null;
            return -1;
        }
    }

    public synchronized int setEncryptionKeysAES(byte[] arrby) {
        try {
            if (arrby.length == 16) {
                return this.setEncryptionKeyAES(arrby);
            }
            this.m_aesKeys = new byte[32];
            System.arraycopy(arrby, 0, this.m_aesKeys, 0, 32);
            this.m_aesEngine1.init(true, new KeyParameter(this.m_aesKeys, 0, 16));
            this.m_aesEngine2.init(true, new KeyParameter(this.m_aesKeys, 16, 16));
            for (int i = 0; i < 16; ++i) {
                this.m_iv1[i] = 0;
                this.m_iv2[i] = 0;
            }
            return 0;
        }
        catch (Exception exception) {
            this.m_aesKeys = null;
            return -1;
        }
    }

    public byte[] getKeys() {
        return this.m_aesKeys;
    }

    public synchronized void setIV2(byte[] arrby) {
        for (int i = 0; i < 16; ++i) {
            this.m_iv2[i] = arrby[i];
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public int encryptAES1(byte[] arrby, int n, byte[] arrby2, int n2, int n3) {
        AESFastEngine aESFastEngine;
        n3 = n + n3;
        while (n < n3 - 15) {
            aESFastEngine = this.m_aesEngine1;
            synchronized (aESFastEngine) {
                this.m_aesEngine1.processBlock(this.m_iv1, 0, this.m_iv1, 0);
            }
            arrby2[n2++] = (byte)(arrby[n++] ^ this.m_iv1[0]);
            arrby2[n2++] = (byte)(arrby[n++] ^ this.m_iv1[1]);
            arrby2[n2++] = (byte)(arrby[n++] ^ this.m_iv1[2]);
            arrby2[n2++] = (byte)(arrby[n++] ^ this.m_iv1[3]);
            arrby2[n2++] = (byte)(arrby[n++] ^ this.m_iv1[4]);
            arrby2[n2++] = (byte)(arrby[n++] ^ this.m_iv1[5]);
            arrby2[n2++] = (byte)(arrby[n++] ^ this.m_iv1[6]);
            arrby2[n2++] = (byte)(arrby[n++] ^ this.m_iv1[7]);
            arrby2[n2++] = (byte)(arrby[n++] ^ this.m_iv1[8]);
            arrby2[n2++] = (byte)(arrby[n++] ^ this.m_iv1[9]);
            arrby2[n2++] = (byte)(arrby[n++] ^ this.m_iv1[10]);
            arrby2[n2++] = (byte)(arrby[n++] ^ this.m_iv1[11]);
            arrby2[n2++] = (byte)(arrby[n++] ^ this.m_iv1[12]);
            arrby2[n2++] = (byte)(arrby[n++] ^ this.m_iv1[13]);
            arrby2[n2++] = (byte)(arrby[n++] ^ this.m_iv1[14]);
            arrby2[n2++] = (byte)(arrby[n++] ^ this.m_iv1[15]);
        }
        if (n < n3) {
            aESFastEngine = this.m_aesEngine1;
            synchronized (aESFastEngine) {
                this.m_aesEngine1.processBlock(this.m_iv1, 0, this.m_iv1, 0);
            }
            n3 -= n;
            for (int i = 0; i < n3; ++i) {
                arrby2[n2++] = (byte)(arrby[n++] ^ this.m_iv1[i]);
            }
        }
        return 0;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public int encryptAES2(byte[] arrby) {
        AESFastEngine aESFastEngine;
        int n = 0;
        int n2 = arrby.length;
        while (n < n2 - 15) {
            aESFastEngine = this.m_aesEngine2;
            synchronized (aESFastEngine) {
                this.m_aesEngine2.processBlock(this.m_iv2, 0, this.m_iv2, 0);
            }
            int n3 = n++;
            arrby[n3] = (byte)(arrby[n3] ^ this.m_iv2[0]);
            int n4 = n++;
            arrby[n4] = (byte)(arrby[n4] ^ this.m_iv2[1]);
            int n5 = n++;
            arrby[n5] = (byte)(arrby[n5] ^ this.m_iv2[2]);
            int n6 = n++;
            arrby[n6] = (byte)(arrby[n6] ^ this.m_iv2[3]);
            int n7 = n++;
            arrby[n7] = (byte)(arrby[n7] ^ this.m_iv2[4]);
            int n8 = n++;
            arrby[n8] = (byte)(arrby[n8] ^ this.m_iv2[5]);
            int n9 = n++;
            arrby[n9] = (byte)(arrby[n9] ^ this.m_iv2[6]);
            int n10 = n++;
            arrby[n10] = (byte)(arrby[n10] ^ this.m_iv2[7]);
            int n11 = n++;
            arrby[n11] = (byte)(arrby[n11] ^ this.m_iv2[8]);
            int n12 = n++;
            arrby[n12] = (byte)(arrby[n12] ^ this.m_iv2[9]);
            int n13 = n++;
            arrby[n13] = (byte)(arrby[n13] ^ this.m_iv2[10]);
            int n14 = n++;
            arrby[n14] = (byte)(arrby[n14] ^ this.m_iv2[11]);
            int n15 = n++;
            arrby[n15] = (byte)(arrby[n15] ^ this.m_iv2[12]);
            int n16 = n++;
            arrby[n16] = (byte)(arrby[n16] ^ this.m_iv2[13]);
            int n17 = n++;
            arrby[n17] = (byte)(arrby[n17] ^ this.m_iv2[14]);
            int n18 = n++;
            arrby[n18] = (byte)(arrby[n18] ^ this.m_iv2[15]);
        }
        if (n < n2) {
            aESFastEngine = this.m_aesEngine2;
            synchronized (aESFastEngine) {
                this.m_aesEngine2.processBlock(this.m_iv2, 0, this.m_iv2, 0);
            }
            n2 -= n;
            for (int i = 0; i < n2; ++i) {
                int n19 = n++;
                arrby[n19] = (byte)(arrby[n19] ^ this.m_iv2[i]);
            }
        }
        return 0;
    }
}

