/*
 * Decompiled with CFR 0.150.
 */
package HTTPClient;

import java.io.FileInputStream;

class MD4 {
    private static final byte[] padding = new byte[]{-128, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
    private MD4State state = new MD4State();
    private byte[] final_hash;

    public MD4() {
    }

    public MD4(byte[] arrby) {
        this();
        this.update(arrby);
    }

    public void update(byte[] arrby, int n, int n2) {
        if (this.final_hash != null) {
            throw new IllegalStateException("Hash already terminated");
        }
        int n3 = (int)(this.state.count >>> 3 & 0x3FL);
        int n4 = 64 - n3;
        this.state.count += (long)(n2 << 3);
        if (n2 >= n4) {
            System.arraycopy(arrby, n, this.state.buffer, n3, n4);
            this.transform(this.state.buffer, 0);
            int n5 = n + n2;
            n += n4;
            while (n < n5 - 63) {
                this.transform(arrby, n);
                n += 64;
            }
            n3 = 0;
            n2 = n5 - n;
        }
        System.arraycopy(arrby, n, this.state.buffer, n3, n2);
    }

    public void update(byte[] arrby) {
        this.update(arrby, 0, arrby.length);
    }

    private static final int rotate_left(int n, int n2) {
        return n << n2 | n >>> 32 - n2;
    }

    private static final int FF(int n, int n2, int n3, int n4, int n5, int n6) {
        return MD4.rotate_left(n += (n2 & n3 | ~n2 & n4) + n5, n6);
    }

    private static final int GG(int n, int n2, int n3, int n4, int n5, int n6) {
        return MD4.rotate_left(n += (n2 & n3 | n2 & n4 | n3 & n4) + n5 + 1518500249, n6);
    }

    private static final int HH(int n, int n2, int n3, int n4, int n5, int n6) {
        return MD4.rotate_left(n += (n2 ^ n3 ^ n4) + n5 + 1859775393, n6);
    }

    private void transform(byte[] arrby, int n) {
        int n2 = this.state.state[0];
        int n3 = this.state.state[1];
        int n4 = this.state.state[2];
        int n5 = this.state.state[3];
        int[] arrn = MD4.decode(arrby, n, 64);
        n2 = MD4.FF(n2, n3, n4, n5, arrn[0], 3);
        n5 = MD4.FF(n5, n2, n3, n4, arrn[1], 7);
        n4 = MD4.FF(n4, n5, n2, n3, arrn[2], 11);
        n3 = MD4.FF(n3, n4, n5, n2, arrn[3], 19);
        n2 = MD4.FF(n2, n3, n4, n5, arrn[4], 3);
        n5 = MD4.FF(n5, n2, n3, n4, arrn[5], 7);
        n4 = MD4.FF(n4, n5, n2, n3, arrn[6], 11);
        n3 = MD4.FF(n3, n4, n5, n2, arrn[7], 19);
        n2 = MD4.FF(n2, n3, n4, n5, arrn[8], 3);
        n5 = MD4.FF(n5, n2, n3, n4, arrn[9], 7);
        n4 = MD4.FF(n4, n5, n2, n3, arrn[10], 11);
        n3 = MD4.FF(n3, n4, n5, n2, arrn[11], 19);
        n2 = MD4.FF(n2, n3, n4, n5, arrn[12], 3);
        n5 = MD4.FF(n5, n2, n3, n4, arrn[13], 7);
        n4 = MD4.FF(n4, n5, n2, n3, arrn[14], 11);
        n3 = MD4.FF(n3, n4, n5, n2, arrn[15], 19);
        n2 = MD4.GG(n2, n3, n4, n5, arrn[0], 3);
        n5 = MD4.GG(n5, n2, n3, n4, arrn[4], 5);
        n4 = MD4.GG(n4, n5, n2, n3, arrn[8], 9);
        n3 = MD4.GG(n3, n4, n5, n2, arrn[12], 13);
        n2 = MD4.GG(n2, n3, n4, n5, arrn[1], 3);
        n5 = MD4.GG(n5, n2, n3, n4, arrn[5], 5);
        n4 = MD4.GG(n4, n5, n2, n3, arrn[9], 9);
        n3 = MD4.GG(n3, n4, n5, n2, arrn[13], 13);
        n2 = MD4.GG(n2, n3, n4, n5, arrn[2], 3);
        n5 = MD4.GG(n5, n2, n3, n4, arrn[6], 5);
        n4 = MD4.GG(n4, n5, n2, n3, arrn[10], 9);
        n3 = MD4.GG(n3, n4, n5, n2, arrn[14], 13);
        n2 = MD4.GG(n2, n3, n4, n5, arrn[3], 3);
        n5 = MD4.GG(n5, n2, n3, n4, arrn[7], 5);
        n4 = MD4.GG(n4, n5, n2, n3, arrn[11], 9);
        n3 = MD4.GG(n3, n4, n5, n2, arrn[15], 13);
        n2 = MD4.HH(n2, n3, n4, n5, arrn[0], 3);
        n5 = MD4.HH(n5, n2, n3, n4, arrn[8], 9);
        n4 = MD4.HH(n4, n5, n2, n3, arrn[4], 11);
        n3 = MD4.HH(n3, n4, n5, n2, arrn[12], 15);
        n2 = MD4.HH(n2, n3, n4, n5, arrn[2], 3);
        n5 = MD4.HH(n5, n2, n3, n4, arrn[10], 9);
        n4 = MD4.HH(n4, n5, n2, n3, arrn[6], 11);
        n3 = MD4.HH(n3, n4, n5, n2, arrn[14], 15);
        n2 = MD4.HH(n2, n3, n4, n5, arrn[1], 3);
        n5 = MD4.HH(n5, n2, n3, n4, arrn[9], 9);
        n4 = MD4.HH(n4, n5, n2, n3, arrn[5], 11);
        n3 = MD4.HH(n3, n4, n5, n2, arrn[13], 15);
        n2 = MD4.HH(n2, n3, n4, n5, arrn[3], 3);
        n5 = MD4.HH(n5, n2, n3, n4, arrn[11], 9);
        n4 = MD4.HH(n4, n5, n2, n3, arrn[7], 11);
        n3 = MD4.HH(n3, n4, n5, n2, arrn[15], 15);
        this.state.state[0] = this.state.state[0] + n2;
        this.state.state[1] = this.state.state[1] + n3;
        this.state.state[2] = this.state.state[2] + n4;
        this.state.state[3] = this.state.state[3] + n5;
    }

    public byte[] getHash() {
        if (this.final_hash != null) {
            return this.final_hash;
        }
        int[] arrn = new int[]{(int)(this.state.count & 0xFFFFFFFFFFFFFFFFL), (int)(this.state.count >> 32)};
        int n = (int)(this.state.count >>> 3 & 0x3FL);
        int n2 = n < 56 ? 56 - n : 120 - n;
        this.update(padding, 0, n2);
        this.update(MD4.encode(arrn, 0, 2), 0, 8);
        this.final_hash = MD4.encode(this.state.state, 0, 4);
        return this.final_hash;
    }

    private static final int[] decode(byte[] arrby, int n, int n2) {
        int n3 = n2 >>> 2;
        int[] arrn = new int[n3];
        int n4 = n;
        for (int i = 0; i < n3; ++i) {
            arrn[i] = arrby[n4++] & 0xFF | (arrby[n4++] & 0xFF) << 8 | (arrby[n4++] & 0xFF) << 16 | (arrby[n4++] & 0xFF) << 24;
        }
        return arrn;
    }

    private static final byte[] encode(int[] arrn, int n, int n2) {
        int n3 = n2 << 2;
        byte[] arrby = new byte[n3];
        int n4 = 0;
        int n5 = n;
        while (n4 < n3) {
            int n6 = arrn[n5];
            arrby[n4++] = (byte)(n6 & 0xFF);
            arrby[n4++] = (byte)(n6 >> 8 & 0xFF);
            arrby[n4++] = (byte)(n6 >> 16 & 0xFF);
            arrby[n4++] = (byte)(n6 >> 24 & 0xFF);
            ++n5;
        }
        return arrby;
    }

    public String toString() {
        byte[] arrby = this.getHash();
        StringBuffer stringBuffer = new StringBuffer(arrby.length * 2);
        for (int i = 0; i < arrby.length; ++i) {
            int n = arrby[i] & 0xFF;
            if (n < 16) {
                stringBuffer.append("0");
            }
            stringBuffer.append(Integer.toString(n, 16));
        }
        return stringBuffer.toString();
    }

    public static void main(String[] arrstring) throws Exception {
        if (arrstring.length > 0) {
            if (arrstring[0].equals("-s")) {
                byte[] arrby = arrstring[1].getBytes();
                System.out.println("MD4 (\"" + arrstring[1] + "\") = " + new MD4(arrby));
            } else if (arrstring[0].equals("-x")) {
                MD4.md4TestSuite();
            } else {
                int n;
                FileInputStream fileInputStream = new FileInputStream(arrstring[0]);
                byte[] arrby = new byte[10000];
                MD4 mD4 = new MD4();
                while ((n = fileInputStream.read(arrby)) > 0) {
                    mD4.update(arrby, 0, n);
                }
                System.out.println("MD4 (\"" + arrstring[0] + "\") = " + mD4);
            }
        } else {
            System.out.println("Usage: -s <string>: print md4 hash of string");
            System.out.println("       -x: run md4 test suite");
            System.out.println("       <file>: print md4 hash of file");
        }
    }

    private static final void md4TestSuite() {
        String[][] arrarrstring = new String[][]{{"", "31d6cfe0d16ae931b73c59d7e0c089c0"}, {"a", "bde52cb31de33e46245e05fbdbd6fb24"}, {"abc", "a448017aaf21d8525fc10ae87aa6729d"}, {"message digest", "d9130a8164549fe818874806e1c7014b"}, {"abcdefghijklmnopqrstuvwxyz", "d79e1c308aa5bbcdeea8ed63df412da9"}, {"ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789", "043f8582f241db351ce627e153e7f0e4"}, {"12345678901234567890123456789012345678901234567890123456789012345678901234567890", "e33b4ddc9c38f2199c3e7b164fcc0536"}};
        for (int i = 0; i < arrarrstring.length; ++i) {
            byte[] arrby = arrarrstring[i][0].getBytes();
            if (new MD4(arrby).toString().equals(arrarrstring[i][1])) continue;
            System.err.println("Test failed!");
            System.err.println("Input string: \"" + arrarrstring[i][0] + "\"");
            System.err.println("Calculated: " + new MD4(arrby));
            System.err.println("Expected:   " + arrarrstring[i][1]);
            System.exit(1);
        }
        System.out.println("All tests passed successfuly");
    }

    private class MD4State {
        int[] state;
        long count;
        byte[] buffer = new byte[64];

        MD4State() {
            this.state = new int[4];
            this.state[0] = 1732584193;
            this.state[1] = -271733879;
            this.state[2] = -1732584194;
            this.state[3] = 271733878;
            this.count = 0L;
        }
    }
}

