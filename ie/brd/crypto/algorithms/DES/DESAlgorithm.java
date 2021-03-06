/*
 * Decompiled with CFR 0.150.
 */
package ie.brd.crypto.algorithms.DES;

public class DESAlgorithm {
    private static final int DES_KEY_SZ = 8;
    private boolean des_check_key = true;
    private int[] ll = new int[2];
    private static final byte[][] weak_keys = new byte[][]{{1, 1, 1, 1, 1, 1, 1, 1}, {-2, -2, -2, -2, -2, -2, -2, -2}, {31, 31, 31, 31, 31, 31, 31, 31}, {-32, -32, -32, -32, -32, -32, -32, -32}, {1, -2, 1, -2, 1, -2, 1, -2}, {-2, 1, -2, 1, -2, 1, -2, 1}, {31, -32, 31, -32, 14, -15, 14, -15}, {-32, 31, -32, 31, -15, 14, -15, 14}, {1, -32, 1, -32, 1, -15, 1, -15}, {-32, 1, -32, 1, -15, 1, -15, 1}, {31, -2, 31, -2, 14, -2, 14, -2}, {-2, 31, -2, 31, -2, 14, -2, 14}, {1, 31, 1, 31, 1, 14, 1, 14}, {31, 1, 31, 1, 14, 1, 14, 1}, {-32, -2, -32, -2, -15, -2, -15, -2}, {-2, -32, -2, -32, -2, -15, -2, -15}};
    private static final byte[] odd_parity = new byte[]{1, 1, 2, 2, 4, 4, 7, 7, 8, 8, 11, 11, 13, 13, 14, 14, 16, 16, 19, 19, 21, 21, 22, 22, 25, 25, 26, 26, 28, 28, 31, 31, 32, 32, 35, 35, 37, 37, 38, 38, 41, 41, 42, 42, 44, 44, 47, 47, 49, 49, 50, 50, 52, 52, 55, 55, 56, 56, 59, 59, 61, 61, 62, 62, 64, 64, 67, 67, 69, 69, 70, 70, 73, 73, 74, 74, 76, 76, 79, 79, 81, 81, 82, 82, 84, 84, 87, 87, 88, 88, 91, 91, 93, 93, 94, 94, 97, 97, 98, 98, 100, 100, 103, 103, 104, 104, 107, 107, 109, 109, 110, 110, 112, 112, 115, 115, 117, 117, 118, 118, 121, 121, 122, 122, 124, 124, 127, 127, -128, -128, -125, -125, -123, -123, -122, -122, -119, -119, -118, -118, -116, -116, -113, -113, -111, -111, -110, -110, -108, -108, -105, -105, -104, -104, -101, -101, -99, -99, -98, -98, -95, -95, -94, -94, -92, -92, -89, -89, -88, -88, -85, -85, -83, -83, -82, -82, -80, -80, -77, -77, -75, -75, -74, -74, -71, -71, -70, -70, -68, -68, -65, -65, -63, -63, -62, -62, -60, -60, -57, -57, -56, -56, -53, -53, -51, -51, -50, -50, -48, -48, -45, -45, -43, -43, -42, -42, -39, -39, -38, -38, -36, -36, -33, -33, -32, -32, -29, -29, -27, -27, -26, -26, -23, -23, -22, -22, -20, -20, -17, -17, -15, -15, -14, -14, -12, -12, -9, -9, -8, -8, -5, -5, -3, -3, -2, -2};
    private static final boolean[] shifts2 = new boolean[]{false, false, true, true, true, true, true, true, false, true, true, true, true, true, true, false};
    private static final int[][] des_skb = new int[][]{{0, 16, 0x20000000, 0x20000010, 65536, 65552, 0x20010000, 0x20010010, 2048, 2064, 0x20000800, 536872976, 67584, 67600, 536938496, 536938512, 32, 48, 0x20000020, 0x20000030, 65568, 65584, 0x20010020, 536936496, 2080, 2096, 0x20000820, 536873008, 67616, 67632, 536938528, 536938544, 524288, 524304, 0x20080000, 537395216, 589824, 589840, 0x20090000, 537460752, 526336, 526352, 0x20080800, 537397264, 591872, 591888, 537462784, 537462800, 524320, 524336, 0x20080020, 537395248, 589856, 589872, 0x20090020, 537460784, 526368, 526384, 0x20080820, 537397296, 591904, 591920, 537462816, 537462832}, {0, 0x2000000, 8192, 0x2002000, 0x200000, 0x2200000, 0x202000, 0x2202000, 4, 0x2000004, 8196, 0x2002004, 0x200004, 0x2200004, 0x202004, 0x2202004, 1024, 0x2000400, 9216, 0x2002400, 0x200400, 0x2200400, 0x202400, 0x2202400, 1028, 0x2000404, 9220, 0x2002404, 0x200404, 0x2200404, 0x202404, 0x2202404, 0x10000000, 0x12000000, 0x10002000, 0x12002000, 0x10200000, 0x12200000, 0x10202000, 0x12202000, 0x10000004, 301989892, 268443652, 301998084, 270532612, 304087044, 270540804, 304095236, 0x10000400, 301990912, 268444672, 301999104, 270533632, 304088064, 270541824, 304096256, 0x10000404, 301990916, 268444676, 301999108, 270533636, 304088068, 270541828, 304096260}, {0, 1, 262144, 262145, 0x1000000, 0x1000001, 0x1040000, 0x1040001, 2, 3, 262146, 262147, 0x1000002, 0x1000003, 17039362, 17039363, 512, 513, 262656, 262657, 0x1000200, 0x1000201, 17039872, 17039873, 514, 515, 262658, 262659, 0x1000202, 16777731, 17039874, 17039875, 0x8000000, 0x8000001, 0x8040000, 134479873, 0x9000000, 0x9000001, 0x9040000, 151257089, 0x8000002, 0x8000003, 134479874, 134479875, 0x9000002, 0x9000003, 151257090, 151257091, 0x8000200, 134218241, 134480384, 134480385, 0x9000200, 150995457, 151257600, 151257601, 0x8000202, 134218243, 134480386, 134480387, 0x9000202, 150995459, 151257602, 151257603}, {0, 0x100000, 256, 0x100100, 8, 0x100008, 264, 0x100108, 4096, 0x101000, 4352, 0x101100, 4104, 0x101008, 4360, 0x101108, 0x4000000, 0x4100000, 0x4000100, 0x4100100, 0x4000008, 68157448, 67109128, 68157704, 0x4001000, 0x4101000, 0x4001100, 0x4101100, 67112968, 68161544, 67113224, 68161800, 131072, 0x120000, 131328, 0x120100, 131080, 1179656, 131336, 1179912, 135168, 0x121000, 135424, 0x121100, 135176, 1183752, 135432, 1184008, 0x4020000, 68288512, 67240192, 68288768, 67239944, 68288520, 67240200, 68288776, 67244032, 68292608, 67244288, 68292864, 67244040, 68292616, 67244296, 68292872}, {0, 0x10000000, 65536, 0x10010000, 4, 0x10000004, 65540, 0x10010004, 0x20000000, 0x30000000, 0x20010000, 0x30010000, 0x20000004, 0x30000004, 536936452, 805371908, 0x100000, 0x10100000, 0x110000, 0x10110000, 0x100004, 0x10100004, 0x110004, 0x10110004, 0x20100000, 0x30100000, 0x20110000, 0x30110000, 537919492, 806354948, 537985028, 806420484, 4096, 0x10001000, 69632, 0x10011000, 4100, 0x10001004, 69636, 0x10011004, 0x20001000, 0x30001000, 0x20011000, 0x30011000, 536875012, 805310468, 536940548, 805376004, 0x101000, 0x10101000, 0x111000, 0x10111000, 0x101004, 0x10101004, 0x111004, 0x10111004, 0x20101000, 0x30101000, 0x20111000, 0x30111000, 537923588, 806359044, 537989124, 806424580}, {0, 0x8000000, 8, 0x8000008, 1024, 0x8000400, 1032, 0x8000408, 131072, 0x8020000, 131080, 0x8020008, 132096, 134349824, 132104, 134349832, 1, 0x8000001, 9, 0x8000009, 1025, 134218753, 1033, 134218761, 131073, 134348801, 131081, 134348809, 132097, 134349825, 132105, 134349833, 0x2000000, 0xA000000, 0x2000008, 0xA000008, 0x2000400, 0xA000400, 33555464, 167773192, 0x2020000, 0xA020000, 0x2020008, 167903240, 0x2020400, 167904256, 33686536, 167904264, 0x2000001, 0xA000001, 0x2000009, 0xA000009, 33555457, 167773185, 33555465, 167773193, 0x2020001, 167903233, 0x2020009, 167903241, 33686529, 167904257, 33686537, 167904265}, {0, 256, 524288, 524544, 0x1000000, 0x1000100, 0x1080000, 0x1080100, 16, 272, 524304, 524560, 0x1000010, 0x1000110, 0x1080010, 0x1080110, 0x200000, 0x200100, 0x280000, 2621696, 0x1200000, 0x1200100, 19398656, 19398912, 0x200010, 0x200110, 2621456, 2621712, 0x1200010, 0x1200110, 19398672, 19398928, 512, 768, 524800, 525056, 0x1000200, 0x1000300, 17302016, 17302272, 528, 784, 524816, 525072, 0x1000210, 0x1000310, 17302032, 17302288, 0x200200, 0x200300, 0x280200, 2622208, 0x1200200, 18875136, 19399168, 19399424, 0x200210, 2097936, 2621968, 2622224, 0x1200210, 18875152, 19399184, 19399440}, {0, 0x4000000, 262144, 0x4040000, 2, 0x4000002, 262146, 0x4040002, 8192, 0x4002000, 270336, 0x4042000, 8194, 0x4002002, 270338, 0x4042002, 32, 0x4000020, 262176, 0x4040020, 34, 0x4000022, 262178, 0x4040022, 8224, 0x4002020, 270368, 0x4042020, 8226, 0x4002022, 270370, 0x4042022, 2048, 0x4000800, 264192, 0x4040800, 2050, 67110914, 264194, 67373058, 10240, 67119104, 272384, 67381248, 10242, 67119106, 272386, 67381250, 2080, 67110944, 264224, 67373088, 2082, 67110946, 264226, 67373090, 10272, 67119136, 272416, 67381280, 10274, 67119138, 272418, 67381282}};
    private static final int[][] des_SPtrans = new int[][]{{0x820200, 131072, -2139095040, -2138963456, 0x800000, -2147352064, -2147352576, -2139095040, -2147352064, 0x820200, 0x820000, -2147483136, -2139094528, 0x800000, 0, -2147352576, 131072, Integer.MIN_VALUE, 0x800200, 131584, -2138963456, 0x820000, -2147483136, 0x800200, Integer.MIN_VALUE, 512, 131584, -2138963968, 512, -2139094528, -2138963968, 0, 0, -2138963456, 0x800200, -2147352576, 0x820200, 131072, -2147483136, 0x800200, -2138963968, 512, 131584, -2139095040, -2147352064, Integer.MIN_VALUE, -2139095040, 0x820000, -2138963456, 131584, 0x820000, -2139094528, 0x800000, -2147483136, -2147352576, 0, 131072, 0x800000, -2139094528, 0x820200, Integer.MIN_VALUE, -2138963968, 512, -2147352064}, {268705796, 0, 270336, 0x10040000, 0x10000004, 8196, 0x10002000, 270336, 8192, 0x10040004, 4, 0x10002000, 262148, 268705792, 0x10040000, 4, 262144, 268443652, 0x10040004, 8192, 270340, 0x10000000, 0, 262148, 268443652, 270340, 268705792, 0x10000004, 0x10000000, 262144, 8196, 268705796, 262148, 268705792, 0x10002000, 270340, 268705796, 262148, 0x10000004, 0, 0x10000000, 8196, 262144, 0x10040004, 8192, 0x10000000, 270340, 268443652, 268705792, 8192, 0, 0x10000004, 4, 268705796, 270336, 0x10040000, 0x10040004, 262144, 8196, 0x10002000, 268443652, 4, 0x10040000, 270336}, {0x41000000, 0x1010040, 64, 0x41000040, 0x40010000, 0x1000000, 0x41000040, 65600, 0x1000040, 65536, 0x1010000, 0x40000000, 0x41010040, 0x40000040, 0x40000000, 0x41010000, 0, 0x40010000, 0x1010040, 64, 0x40000040, 0x41010040, 65536, 0x41000000, 0x41010000, 0x1000040, 0x40010040, 0x1010000, 65600, 0, 0x1000000, 0x40010040, 0x1010040, 64, 0x40000000, 65536, 0x40000040, 0x40010000, 0x1010000, 0x41000040, 0, 0x1010040, 65600, 0x41010000, 0x40010000, 0x1000000, 0x41010040, 0x40000000, 0x40010040, 0x41000000, 0x1000000, 0x41010040, 65536, 0x1000040, 0x41000040, 65600, 0x1000040, 0, 0x41010000, 0x40000040, 0x41000000, 0x40010040, 64, 0x1010000}, {1049602, 0x4000400, 2, 68158466, 0, 0x4100000, 0x4000402, 0x100002, 0x4100400, 0x4000002, 0x4000000, 1026, 0x4000002, 1049602, 0x100000, 0x4000000, 68157442, 0x100400, 1024, 2, 0x100400, 0x4000402, 0x4100000, 1024, 1026, 0, 0x100002, 0x4100400, 0x4000400, 68157442, 68158466, 0x100000, 68157442, 1026, 0x100000, 0x4000002, 0x100400, 0x4000400, 2, 0x4100000, 0x4000402, 0, 1024, 0x100002, 0, 68157442, 0x4100400, 1024, 0x4000000, 68158466, 1049602, 0x100000, 68158466, 2, 0x4000400, 1049602, 0x100002, 0x100400, 0x4100000, 0x4000402, 1026, 0x4000000, 0x4000002, 0x4100400}, {0x2000000, 16384, 256, 33571080, 33570824, 0x2000100, 16648, 0x2004000, 16384, 8, 0x2000008, 16640, 33554696, 33570824, 33571072, 0, 16640, 0x2000000, 16392, 264, 0x2000100, 16648, 0, 0x2000008, 8, 33554696, 33571080, 16392, 0x2004000, 256, 264, 33571072, 33571072, 33554696, 16392, 0x2004000, 16384, 8, 0x2000008, 0x2000100, 0x2000000, 16640, 33571080, 0, 16648, 0x2000000, 256, 16392, 33554696, 256, 0, 33571080, 33570824, 33571072, 264, 16384, 16640, 33570824, 0x2000100, 264, 8, 16648, 0x2004000, 0x2000008}, {0x20000010, 524304, 0, 0x20080800, 524304, 2048, 536872976, 524288, 2064, 537397264, 526336, 0x20000000, 0x20000800, 0x20000010, 0x20080000, 526352, 524288, 536872976, 537395216, 0, 2048, 16, 0x20080800, 537395216, 537397264, 0x20080000, 0x20000000, 2064, 16, 526336, 526352, 0x20000800, 2064, 0x20000000, 0x20000800, 526352, 0x20080800, 524304, 0, 0x20000800, 0x20000000, 2048, 537395216, 524288, 524304, 537397264, 526336, 16, 537397264, 526336, 524288, 536872976, 0x20000010, 0x20080000, 526352, 0, 2048, 0x20000010, 536872976, 0x20080800, 0x20080000, 2064, 16, 537395216}, {4096, 128, 0x400080, 0x400001, 4198529, 4097, 4224, 0, 0x400000, 4194433, 129, 0x401000, 1, 4198528, 0x401000, 129, 4194433, 4096, 4097, 4198529, 0, 0x400080, 0x400001, 4224, 0x401001, 4225, 4198528, 1, 4225, 0x401001, 128, 0x400000, 4225, 0x401000, 0x401001, 129, 4096, 128, 0x400000, 0x401001, 4194433, 4225, 4224, 0, 128, 0x400001, 1, 0x400080, 0, 4194433, 0x400080, 4224, 129, 4096, 4198529, 0x400000, 4198528, 1, 4097, 4198529, 0x400001, 4198528, 0x401000, 4097}, {0x8200020, 0x8208000, 32800, 0, 0x8008000, 0x200020, 0x8200000, 0x8208020, 32, 0x8000000, 0x208000, 32800, 0x208020, 0x8008020, 0x8000020, 0x8200000, 32768, 0x208020, 0x200020, 0x8008000, 0x8208020, 0x8000020, 0, 0x208000, 0x8000000, 0x200000, 0x8008020, 0x8200020, 0x200000, 32768, 0x8208000, 32, 0x200000, 32768, 0x8000020, 0x8208020, 32800, 0x8000000, 0, 0x208000, 0x8200020, 0x8008020, 0x8008000, 0x200020, 0x8208000, 32, 0x200020, 0x8008000, 0x8208020, 0x200000, 0x8200000, 0x8000020, 0x208000, 32800, 0x8008020, 0x8200000, 32, 0x8208000, 0x208020, 0, 0x8000000, 0x8200020, 32768, 0x208020}};

    public DESAlgorithm(boolean bl) {
        this.des_check_key = bl;
    }

    public final void des_set_odd_parity(byte[] arrby) {
        for (int i = 0; i < 8; ++i) {
            arrby[i] = odd_parity[arrby[i] & 0xFF];
        }
    }

    public final boolean check_parity(byte[] arrby) {
        for (int i = 0; i < 8; ++i) {
            if (arrby[i] == odd_parity[arrby[i] & 0xFF]) continue;
            return false;
        }
        return true;
    }

    public final boolean des_is_weak_key(byte[] arrby) {
        int n = 0;
        if (n < weak_keys.length) {
            for (int i = 0; i < 8; ++i) {
                if (weak_keys[n][i] == arrby[i]) continue;
            }
            return true;
        }
        return false;
    }

    private final int Get32bits(byte[] arrby, int n) {
        return ((arrby[n + 3] & 0xFF) << 24) + ((arrby[n + 2] & 0xFF) << 16) + ((arrby[n + 1] & 0xFF) << 8) + (arrby[n] & 0xFF);
    }

    public final void des_set_key(byte[] arrby, int[] arrn) throws SecurityException {
        if (this.des_check_key) {
            if (!this.check_parity(arrby)) {
                throw new SecurityException("des_set_key attempted with incorrect parity");
            }
            if (this.des_is_weak_key(arrby)) {
                throw new SecurityException("des_set_key attempted with weak key");
            }
        }
        int n = 0;
        int n2 = 0;
        int n3 = this.Get32bits(arrby, n);
        int n4 = this.Get32bits(arrby, n + 4);
        int n5 = (n4 >>> 4 ^ n3) & 0xF0F0F0F;
        n3 ^= n5;
        n4 ^= n5 << 4;
        n5 = (n3 << 18 ^ n3) & 0xCCCC0000;
        n3 = n3 ^ n5 ^ n5 >>> 18;
        n5 = (n4 << 18 ^ n4) & 0xCCCC0000;
        n4 = n4 ^ n5 ^ n5 >>> 18;
        n5 = (n4 >>> 1 ^ n3) & 0x55555555;
        n3 ^= n5;
        n4 ^= n5 << 1;
        n5 = (n3 >>> 8 ^ n4) & 0xFF00FF;
        n4 ^= n5;
        n3 ^= n5 << 8;
        n5 = (n4 >>> 1 ^ n3) & 0x55555555;
        n4 ^= n5 << 1;
        n4 = (n4 & 0xFF) << 16 | n4 & 0xFF00 | (n4 & 0xFF0000) >>> 16 | ((n3 ^= n5) & 0xF0000000) >>> 4;
        n3 &= 0xFFFFFFF;
        for (int i = 0; i < 16; ++i) {
            if (shifts2[i]) {
                n3 = n3 >>> 2 | n3 << 26;
                n4 = n4 >>> 2 | n4 << 26;
            } else {
                n3 = n3 >>> 1 | n3 << 27;
                n4 = n4 >>> 1 | n4 << 27;
            }
            int n6 = des_skb[0][(n3 &= 0xFFFFFFF) & 0x3F] | des_skb[1][n3 >>> 6 & 3 | n3 >>> 7 & 0x3C] | des_skb[2][n3 >>> 13 & 0xF | n3 >>> 14 & 0x30] | des_skb[3][n3 >>> 20 & 1 | n3 >>> 21 & 6 | n3 >>> 22 & 0x38];
            n5 = des_skb[4][(n4 &= 0xFFFFFFF) & 0x3F] | des_skb[5][n4 >>> 7 & 3 | n4 >>> 8 & 0x3C] | des_skb[6][n4 >>> 15 & 0x3F] | des_skb[7][n4 >>> 21 & 0xF | n4 >>> 22 & 0x30];
            arrn[n2++] = (n5 << 16 | n6 & 0xFFFF) & 0xFFFFFFFF;
            n6 = n6 >>> 16 | n5 & 0xFFFF0000;
            n6 = n6 << 4 | n6 >>> 28;
            arrn[n2++] = n6 & 0xFFFFFFFF;
        }
    }

    public final void des_ecb_encrypt(byte[] arrby, byte[] arrby2, int[] arrn, boolean bl) {
        int n;
        int n2 = 0;
        int n3 = 0;
        this.ll[0] = n = this.Get32bits(arrby, n2);
        this.ll[1] = n = this.Get32bits(arrby, n2 + 4);
        this.des_encrypt(this.ll, arrn, bl);
        n = this.ll[0];
        arrby2[n3++] = (byte)(n & 0xFF);
        arrby2[n3++] = (byte)(n >>> 8 & 0xFF);
        arrby2[n3++] = (byte)(n >>> 16 & 0xFF);
        arrby2[n3++] = (byte)(n >>> 24 & 0xFF);
        n = this.ll[1];
        arrby2[n3++] = (byte)(n & 0xFF);
        arrby2[n3++] = (byte)(n >>> 8 & 0xFF);
        arrby2[n3++] = (byte)(n >>> 16 & 0xFF);
        arrby2[n3++] = (byte)(n >>> 24 & 0xFF);
        this.ll[1] = 0;
        this.ll[0] = 0;
        n = 0;
    }

    private final int _lrotr(int n) {
        return n >>> 4 | (n & 0xFF) << 28;
    }

    private final void des_encrypt(int[] arrn, int[] arrn2, boolean bl) {
        int n;
        int n2 = arrn[0];
        int n3 = arrn[1];
        int n4 = (n3 >>> 4 ^ n2) & 0xF0F0F0F;
        n2 ^= n4;
        n3 ^= n4 << 4;
        n4 = (n2 >>> 16 ^ n3) & 0xFFFF;
        n3 ^= n4;
        n2 ^= n4 << 16;
        n4 = (n3 >>> 2 ^ n2) & 0x33333333;
        n2 ^= n4;
        n3 ^= n4 << 2;
        n4 = (n2 >>> 8 ^ n3) & 0xFF00FF;
        n3 ^= n4;
        n2 ^= n4 << 8;
        n4 = (n3 >>> 1 ^ n2) & 0x55555555;
        int n5 = (n3 ^= n4 << 1) << 1 | n3 >>> 31;
        n3 = (n2 ^= n4) << 1 | n2 >>> 31;
        n5 &= 0xFFFFFFFF;
        n3 &= 0xFFFFFFFF;
        if (bl) {
            for (int i = 0; i < 32; i += 8) {
                n2 = n3 ^ arrn2[i + 0];
                n = n3 ^ arrn2[i + 0 + 1];
                n = this._lrotr(n);
                n5 ^= des_SPtrans[1][n & 0x3F] | des_SPtrans[3][n >>> 8 & 0x3F] | des_SPtrans[5][n >>> 16 & 0x3F] | des_SPtrans[7][n >>> 24 & 0x3F] | des_SPtrans[0][n2 & 0x3F] | des_SPtrans[2][n2 >>> 8 & 0x3F] | des_SPtrans[4][n2 >>> 16 & 0x3F] | des_SPtrans[6][n2 >>> 24 & 0x3F];
                n2 = n5 ^ arrn2[i + 2];
                n = n5 ^ arrn2[i + 2 + 1];
                n = this._lrotr(n);
                n3 ^= des_SPtrans[1][n & 0x3F] | des_SPtrans[3][n >>> 8 & 0x3F] | des_SPtrans[5][n >>> 16 & 0x3F] | des_SPtrans[7][n >>> 24 & 0x3F] | des_SPtrans[0][n2 & 0x3F] | des_SPtrans[2][n2 >>> 8 & 0x3F] | des_SPtrans[4][n2 >>> 16 & 0x3F] | des_SPtrans[6][n2 >>> 24 & 0x3F];
                n2 = n3 ^ arrn2[i + 4];
                n = n3 ^ arrn2[i + 4 + 1];
                n = this._lrotr(n);
                n5 ^= des_SPtrans[1][n & 0x3F] | des_SPtrans[3][n >>> 8 & 0x3F] | des_SPtrans[5][n >>> 16 & 0x3F] | des_SPtrans[7][n >>> 24 & 0x3F] | des_SPtrans[0][n2 & 0x3F] | des_SPtrans[2][n2 >>> 8 & 0x3F] | des_SPtrans[4][n2 >>> 16 & 0x3F] | des_SPtrans[6][n2 >>> 24 & 0x3F];
                n2 = n5 ^ arrn2[i + 6];
                n = n5 ^ arrn2[i + 6 + 1];
                n = this._lrotr(n);
                n3 ^= des_SPtrans[1][n & 0x3F] | des_SPtrans[3][n >>> 8 & 0x3F] | des_SPtrans[5][n >>> 16 & 0x3F] | des_SPtrans[7][n >>> 24 & 0x3F] | des_SPtrans[0][n2 & 0x3F] | des_SPtrans[2][n2 >>> 8 & 0x3F] | des_SPtrans[4][n2 >>> 16 & 0x3F] | des_SPtrans[6][n2 >>> 24 & 0x3F];
            }
        } else {
            for (int i = 30; i > 0; i -= 8) {
                n2 = n3 ^ arrn2[i - 0];
                n = n3 ^ arrn2[i - 0 + 1];
                n = this._lrotr(n);
                n5 ^= des_SPtrans[1][n & 0x3F] | des_SPtrans[3][n >>> 8 & 0x3F] | des_SPtrans[5][n >>> 16 & 0x3F] | des_SPtrans[7][n >>> 24 & 0x3F] | des_SPtrans[0][n2 & 0x3F] | des_SPtrans[2][n2 >>> 8 & 0x3F] | des_SPtrans[4][n2 >>> 16 & 0x3F] | des_SPtrans[6][n2 >>> 24 & 0x3F];
                n2 = n5 ^ arrn2[i - 2];
                n = n5 ^ arrn2[i - 2 + 1];
                n = this._lrotr(n);
                n3 ^= des_SPtrans[1][n & 0x3F] | des_SPtrans[3][n >>> 8 & 0x3F] | des_SPtrans[5][n >>> 16 & 0x3F] | des_SPtrans[7][n >>> 24 & 0x3F] | des_SPtrans[0][n2 & 0x3F] | des_SPtrans[2][n2 >>> 8 & 0x3F] | des_SPtrans[4][n2 >>> 16 & 0x3F] | des_SPtrans[6][n2 >>> 24 & 0x3F];
                n2 = n3 ^ arrn2[i - 4];
                n = n3 ^ arrn2[i - 4 + 1];
                n = this._lrotr(n);
                n5 ^= des_SPtrans[1][n & 0x3F] | des_SPtrans[3][n >>> 8 & 0x3F] | des_SPtrans[5][n >>> 16 & 0x3F] | des_SPtrans[7][n >>> 24 & 0x3F] | des_SPtrans[0][n2 & 0x3F] | des_SPtrans[2][n2 >>> 8 & 0x3F] | des_SPtrans[4][n2 >>> 16 & 0x3F] | des_SPtrans[6][n2 >>> 24 & 0x3F];
                n2 = n5 ^ arrn2[i - 6];
                n = n5 ^ arrn2[i - 6 + 1];
                n = this._lrotr(n);
                n3 ^= des_SPtrans[1][n & 0x3F] | des_SPtrans[3][n >>> 8 & 0x3F] | des_SPtrans[5][n >>> 16 & 0x3F] | des_SPtrans[7][n >>> 24 & 0x3F] | des_SPtrans[0][n2 & 0x3F] | des_SPtrans[2][n2 >>> 8 & 0x3F] | des_SPtrans[4][n2 >>> 16 & 0x3F] | des_SPtrans[6][n2 >>> 24 & 0x3F];
            }
        }
        n5 = n5 >>> 1 | n5 << 31;
        n3 = n3 >>> 1 | n3 << 31;
        n4 = ((n3 &= 0xFFFFFFFF) >>> 1 ^ (n5 &= 0xFFFFFFFF)) & 0x55555555;
        n5 ^= n4;
        n3 ^= n4 << 1;
        n4 = (n5 >>> 8 ^ n3) & 0xFF00FF;
        n3 ^= n4;
        n5 ^= n4 << 8;
        n4 = (n3 >>> 2 ^ n5) & 0x33333333;
        n5 ^= n4;
        n3 ^= n4 << 2;
        n4 = (n5 >>> 16 ^ n3) & 0xFFFF;
        n3 ^= n4;
        n5 ^= n4 << 16;
        n4 = (n3 >>> 4 ^ n5) & 0xF0F0F0F;
        arrn[0] = n5 ^= n4;
        arrn[1] = n3 ^= n4 << 4;
        n2 = 0;
        n = 0;
        n3 = 0;
        n5 = 0;
    }
}

