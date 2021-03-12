/*
 * Decompiled with CFR 0.150.
 */
package anon.util;

public class ByteArrayUtil {
    public static byte[] conc(byte[] arrby, byte[] arrby2) {
        return ByteArrayUtil.conc(arrby, arrby2, arrby2.length);
    }

    public static byte[] conc(byte[] arrby, byte[] arrby2, byte[] arrby3) {
        byte[] arrby4 = new byte[arrby.length + arrby2.length + arrby3.length];
        System.arraycopy(arrby, 0, arrby4, 0, arrby.length);
        System.arraycopy(arrby2, 0, arrby4, arrby.length, arrby2.length);
        System.arraycopy(arrby3, 0, arrby4, arrby.length + arrby2.length, arrby3.length);
        return arrby4;
    }

    public static byte[] conc(byte[] arrby, byte[] arrby2, byte[] arrby3, byte[] arrby4) {
        byte[] arrby5 = new byte[arrby.length + arrby2.length + arrby3.length + arrby4.length];
        System.arraycopy(arrby, 0, arrby5, 0, arrby.length);
        System.arraycopy(arrby2, 0, arrby5, arrby.length, arrby2.length);
        int n = arrby.length + arrby2.length;
        System.arraycopy(arrby3, 0, arrby5, n, arrby3.length);
        System.arraycopy(arrby4, 0, arrby5, n += arrby3.length, arrby4.length);
        return arrby5;
    }

    public static byte[] conc(byte[] arrby, byte[] arrby2, byte[] arrby3, byte[] arrby4, byte[] arrby5) {
        byte[] arrby6 = new byte[arrby.length + arrby2.length + arrby3.length + arrby4.length + arrby5.length];
        System.arraycopy(arrby, 0, arrby6, 0, arrby.length);
        System.arraycopy(arrby2, 0, arrby6, arrby.length, arrby2.length);
        int n = arrby.length + arrby2.length;
        System.arraycopy(arrby3, 0, arrby6, n, arrby3.length);
        System.arraycopy(arrby4, 0, arrby6, n += arrby3.length, arrby4.length);
        System.arraycopy(arrby5, 0, arrby6, n += arrby4.length, arrby5.length);
        return arrby6;
    }

    public static byte[] conc(byte[] arrby, byte[] arrby2, byte[] arrby3, byte[] arrby4, byte[] arrby5, byte[] arrby6) {
        byte[] arrby7 = new byte[arrby.length + arrby2.length + arrby3.length + arrby4.length + arrby5.length + arrby6.length];
        System.arraycopy(arrby, 0, arrby7, 0, arrby.length);
        System.arraycopy(arrby2, 0, arrby7, arrby.length, arrby2.length);
        int n = arrby.length + arrby2.length;
        System.arraycopy(arrby3, 0, arrby7, n, arrby3.length);
        System.arraycopy(arrby4, 0, arrby7, n += arrby3.length, arrby4.length);
        System.arraycopy(arrby5, 0, arrby7, n += arrby4.length, arrby5.length);
        System.arraycopy(arrby6, 0, arrby7, n += arrby5.length, arrby6.length);
        return arrby7;
    }

    public static byte[] conc(byte[] arrby, byte[] arrby2, int n) {
        if (arrby == null || arrby.length == 0) {
            return ByteArrayUtil.copy(arrby2, 0, n);
        }
        byte[] arrby3 = new byte[arrby.length + n];
        System.arraycopy(arrby, 0, arrby3, 0, arrby.length);
        System.arraycopy(arrby2, 0, arrby3, arrby.length, n);
        return arrby3;
    }

    public static byte[] inttobyte(long l, int n) {
        byte[] arrby = new byte[n];
        for (int i = 0; i < n; ++i) {
            arrby[n - i - 1] = (byte)((l & (long)(255 << i * 8)) >> i * 8);
        }
        return arrby;
    }

    public static byte[] copy(byte[] arrby, int n, int n2) {
        byte[] arrby2 = new byte[n2];
        System.arraycopy(arrby, n, arrby2, 0, n2);
        return arrby2;
    }

    public static boolean equal(byte[] arrby, byte[] arrby2) {
        if (arrby == null && arrby2 == null) {
            return true;
        }
        if (arrby == null || arrby2 == null) {
            return false;
        }
        if (arrby.length != arrby2.length) {
            return false;
        }
        for (int i = 0; i < arrby.length; ++i) {
            if (arrby[i] == arrby2[i]) continue;
            return false;
        }
        return true;
    }

    public static final boolean equal(byte[] arrby, int n, byte[] arrby2, int n2, int n3) {
        if (n3 <= 0) {
            return true;
        }
        if (arrby == null || arrby2 == null || n < 0 || n2 < 0) {
            return false;
        }
        if (n + n3 > arrby.length || n2 + n3 > arrby2.length) {
            return false;
        }
        for (int i = 0; i < n3; ++i) {
            if (arrby[n + i] == arrby2[n2 + i]) continue;
            return false;
        }
        return true;
    }

    public static final boolean equal(char[] arrc, int n, char[] arrc2, int n2, int n3) {
        if (n3 <= 0) {
            return true;
        }
        if (arrc == null || arrc2 == null || n < 0 || n2 < 0) {
            return false;
        }
        if (n + n3 > arrc.length || n2 + n3 > arrc2.length) {
            return false;
        }
        for (int i = 0; i < n3; ++i) {
            if (arrc[n + i] == arrc2[n2 + i]) continue;
            return false;
        }
        return true;
    }

    public static void bzero(byte[] arrby, int n, int n2) {
        for (int i = n; i < n + n2; ++i) {
            arrby[i] = 0;
        }
    }

    public static void bzero(char[] arrc, int n, int n2) {
        for (int i = n; i < n + n2; ++i) {
            arrc[i] = '\u0000';
        }
    }

    public static void byteArrayToCharArray(byte[] arrby, int n, char[] arrc, int n2, int n3) {
        boolean bl = true;
        for (int i = n; i < n + n3; ++i) {
            if (bl) {
                arrc[n2] = (char)arrby[n];
                int n4 = n2;
                arrc[n4] = (char)(arrc[n4] << 8);
                int n5 = n2;
                arrc[n5] = (char)(arrc[n5] & 0xFF00);
                bl = false;
                continue;
            }
            int n6 = n2++;
            arrc[n6] = (char)(arrc[n6] | arrby[n] & 0xFF);
            bl = true;
        }
    }

    public static void charArrayToByteArray(char[] arrc, int n, byte[] arrby, int n2, int n3) {
        boolean bl = true;
        for (int i = n2; i < n2 + n3; ++i) {
            if (bl) {
                arrby[i] = (byte)(arrc[n] >> 8 & 0xFF);
                bl = false;
                continue;
            }
            int n4 = i;
            arrby[n4] = (byte)(arrby[n4] | (byte)(arrc[n] & 0xFF));
            ++n;
            bl = true;
        }
    }
}

