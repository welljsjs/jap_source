/*
 * Decompiled with CFR 0.150.
 */
package anon.tor.util;

public final class Base16 {
    public static String encode(byte[] arrby) {
        StringBuffer stringBuffer = new StringBuffer();
        for (int i = 0; i < arrby.length; ++i) {
            int n = 0xF & arrby[i] >> 4;
            stringBuffer.append(Base16.encodeByte((byte)n));
            int n2 = 0xF & arrby[i];
            stringBuffer.append(Base16.encodeByte((byte)n2));
        }
        return stringBuffer.toString();
    }

    private static char encodeByte(byte by) {
        switch (by) {
            case 0: {
                return '0';
            }
            case 1: {
                return '1';
            }
            case 2: {
                return '2';
            }
            case 3: {
                return '3';
            }
            case 4: {
                return '4';
            }
            case 5: {
                return '5';
            }
            case 6: {
                return '6';
            }
            case 7: {
                return '7';
            }
            case 8: {
                return '8';
            }
            case 9: {
                return '9';
            }
            case 10: {
                return 'A';
            }
            case 11: {
                return 'B';
            }
            case 12: {
                return 'C';
            }
            case 13: {
                return 'D';
            }
            case 14: {
                return 'E';
            }
            case 15: {
                return 'F';
            }
        }
        return '0';
    }
}

