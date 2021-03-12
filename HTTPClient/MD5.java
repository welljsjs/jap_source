/*
 * Decompiled with CFR 0.150.
 */
package HTTPClient;

class MD5 {
    MD5State state;
    MD5State finals;
    static byte[] padding = new byte[]{-128, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};

    public synchronized void Init() {
        this.state = new MD5State();
        this.finals = null;
    }

    public MD5() {
        this.Init();
    }

    public MD5(Object object) {
        this();
        this.Update(object.toString());
    }

    private static final int rotate_left(int n, int n2) {
        return n << n2 | n >>> 32 - n2;
    }

    private static final int FF(int n, int n2, int n3, int n4, int n5, int n6, int n7) {
        return MD5.rotate_left(n += (n2 & n3 | ~n2 & n4) + n5 + n7, n6) + n2;
    }

    private static final int GG(int n, int n2, int n3, int n4, int n5, int n6, int n7) {
        return MD5.rotate_left(n += (n2 & n4 | n3 & ~n4) + n5 + n7, n6) + n2;
    }

    private static final int HH(int n, int n2, int n3, int n4, int n5, int n6, int n7) {
        return MD5.rotate_left(n += (n2 ^ n3 ^ n4) + n5 + n7, n6) + n2;
    }

    private static final int II(int n, int n2, int n3, int n4, int n5, int n6, int n7) {
        return MD5.rotate_left(n += (n3 ^ (n2 | ~n4)) + n5 + n7, n6) + n2;
    }

    private static final int[] Decode(byte[] arrby, int n, int n2) {
        int[] arrn = new int[16];
        int n3 = 0;
        for (int i = 0; i < n; i += 4) {
            arrn[n3] = arrby[i + n2] & 0xFF | (arrby[i + 1 + n2] & 0xFF) << 8 | (arrby[i + 2 + n2] & 0xFF) << 16 | (arrby[i + 3 + n2] & 0xFF) << 24;
            ++n3;
        }
        return arrn;
    }

    private void Transform(MD5State mD5State, byte[] arrby, int n) {
        int n2 = mD5State.state[0];
        int n3 = mD5State.state[1];
        int n4 = mD5State.state[2];
        int n5 = mD5State.state[3];
        int[] arrn = MD5.Decode(arrby, 64, n);
        n2 = MD5.FF(n2, n3, n4, n5, arrn[0], 7, -680876936);
        n5 = MD5.FF(n5, n2, n3, n4, arrn[1], 12, -389564586);
        n4 = MD5.FF(n4, n5, n2, n3, arrn[2], 17, 606105819);
        n3 = MD5.FF(n3, n4, n5, n2, arrn[3], 22, -1044525330);
        n2 = MD5.FF(n2, n3, n4, n5, arrn[4], 7, -176418897);
        n5 = MD5.FF(n5, n2, n3, n4, arrn[5], 12, 1200080426);
        n4 = MD5.FF(n4, n5, n2, n3, arrn[6], 17, -1473231341);
        n3 = MD5.FF(n3, n4, n5, n2, arrn[7], 22, -45705983);
        n2 = MD5.FF(n2, n3, n4, n5, arrn[8], 7, 1770035416);
        n5 = MD5.FF(n5, n2, n3, n4, arrn[9], 12, -1958414417);
        n4 = MD5.FF(n4, n5, n2, n3, arrn[10], 17, -42063);
        n3 = MD5.FF(n3, n4, n5, n2, arrn[11], 22, -1990404162);
        n2 = MD5.FF(n2, n3, n4, n5, arrn[12], 7, 1804603682);
        n5 = MD5.FF(n5, n2, n3, n4, arrn[13], 12, -40341101);
        n4 = MD5.FF(n4, n5, n2, n3, arrn[14], 17, -1502002290);
        n3 = MD5.FF(n3, n4, n5, n2, arrn[15], 22, 1236535329);
        n2 = MD5.GG(n2, n3, n4, n5, arrn[1], 5, -165796510);
        n5 = MD5.GG(n5, n2, n3, n4, arrn[6], 9, -1069501632);
        n4 = MD5.GG(n4, n5, n2, n3, arrn[11], 14, 643717713);
        n3 = MD5.GG(n3, n4, n5, n2, arrn[0], 20, -373897302);
        n2 = MD5.GG(n2, n3, n4, n5, arrn[5], 5, -701558691);
        n5 = MD5.GG(n5, n2, n3, n4, arrn[10], 9, 38016083);
        n4 = MD5.GG(n4, n5, n2, n3, arrn[15], 14, -660478335);
        n3 = MD5.GG(n3, n4, n5, n2, arrn[4], 20, -405537848);
        n2 = MD5.GG(n2, n3, n4, n5, arrn[9], 5, 568446438);
        n5 = MD5.GG(n5, n2, n3, n4, arrn[14], 9, -1019803690);
        n4 = MD5.GG(n4, n5, n2, n3, arrn[3], 14, -187363961);
        n3 = MD5.GG(n3, n4, n5, n2, arrn[8], 20, 1163531501);
        n2 = MD5.GG(n2, n3, n4, n5, arrn[13], 5, -1444681467);
        n5 = MD5.GG(n5, n2, n3, n4, arrn[2], 9, -51403784);
        n4 = MD5.GG(n4, n5, n2, n3, arrn[7], 14, 1735328473);
        n3 = MD5.GG(n3, n4, n5, n2, arrn[12], 20, -1926607734);
        n2 = MD5.HH(n2, n3, n4, n5, arrn[5], 4, -378558);
        n5 = MD5.HH(n5, n2, n3, n4, arrn[8], 11, -2022574463);
        n4 = MD5.HH(n4, n5, n2, n3, arrn[11], 16, 1839030562);
        n3 = MD5.HH(n3, n4, n5, n2, arrn[14], 23, -35309556);
        n2 = MD5.HH(n2, n3, n4, n5, arrn[1], 4, -1530992060);
        n5 = MD5.HH(n5, n2, n3, n4, arrn[4], 11, 1272893353);
        n4 = MD5.HH(n4, n5, n2, n3, arrn[7], 16, -155497632);
        n3 = MD5.HH(n3, n4, n5, n2, arrn[10], 23, -1094730640);
        n2 = MD5.HH(n2, n3, n4, n5, arrn[13], 4, 681279174);
        n5 = MD5.HH(n5, n2, n3, n4, arrn[0], 11, -358537222);
        n4 = MD5.HH(n4, n5, n2, n3, arrn[3], 16, -722521979);
        n3 = MD5.HH(n3, n4, n5, n2, arrn[6], 23, 76029189);
        n2 = MD5.HH(n2, n3, n4, n5, arrn[9], 4, -640364487);
        n5 = MD5.HH(n5, n2, n3, n4, arrn[12], 11, -421815835);
        n4 = MD5.HH(n4, n5, n2, n3, arrn[15], 16, 530742520);
        n3 = MD5.HH(n3, n4, n5, n2, arrn[2], 23, -995338651);
        n2 = MD5.II(n2, n3, n4, n5, arrn[0], 6, -198630844);
        n5 = MD5.II(n5, n2, n3, n4, arrn[7], 10, 1126891415);
        n4 = MD5.II(n4, n5, n2, n3, arrn[14], 15, -1416354905);
        n3 = MD5.II(n3, n4, n5, n2, arrn[5], 21, -57434055);
        n2 = MD5.II(n2, n3, n4, n5, arrn[12], 6, 1700485571);
        n5 = MD5.II(n5, n2, n3, n4, arrn[3], 10, -1894986606);
        n4 = MD5.II(n4, n5, n2, n3, arrn[10], 15, -1051523);
        n3 = MD5.II(n3, n4, n5, n2, arrn[1], 21, -2054922799);
        n2 = MD5.II(n2, n3, n4, n5, arrn[8], 6, 1873313359);
        n5 = MD5.II(n5, n2, n3, n4, arrn[15], 10, -30611744);
        n4 = MD5.II(n4, n5, n2, n3, arrn[6], 15, -1560198380);
        n3 = MD5.II(n3, n4, n5, n2, arrn[13], 21, 1309151649);
        n2 = MD5.II(n2, n3, n4, n5, arrn[4], 6, -145523070);
        n5 = MD5.II(n5, n2, n3, n4, arrn[11], 10, -1120210379);
        n4 = MD5.II(n4, n5, n2, n3, arrn[2], 15, 718787259);
        n3 = MD5.II(n3, n4, n5, n2, arrn[9], 21, -343485551);
        mD5State.state[0] = mD5State.state[0] + n2;
        mD5State.state[1] = mD5State.state[1] + n3;
        mD5State.state[2] = mD5State.state[2] + n4;
        mD5State.state[3] = mD5State.state[3] + n5;
    }

    public void Update(MD5State mD5State, byte[] arrby, int n, int n2) {
        int n3;
        this.finals = null;
        if (n2 - n > arrby.length) {
            n2 = arrby.length - n;
        }
        int n4 = mD5State.count[0] >>> 3 & 0x3F;
        mD5State.count[0] = mD5State.count[0] + (n2 << 3);
        if (mD5State.count[0] < n2 << 3) {
            mD5State.count[1] = mD5State.count[1] + 1;
        }
        mD5State.count[1] = mD5State.count[1] + (n2 >>> 29);
        int n5 = 64 - n4;
        if (n2 >= n5) {
            for (n3 = 0; n3 < n5; ++n3) {
                mD5State.buffer[n3 + n4] = arrby[n3 + n];
            }
            this.Transform(mD5State, mD5State.buffer, 0);
            n3 = n5;
            while (n3 + 63 < n2) {
                this.Transform(mD5State, arrby, n3);
                n3 += 64;
            }
            n4 = 0;
        } else {
            n3 = 0;
        }
        if (n3 < n2) {
            int n6 = n3;
            while (n3 < n2) {
                mD5State.buffer[n4 + n3 - n6] = arrby[n3 + n];
                ++n3;
            }
        }
    }

    public void Update(byte[] arrby, int n, int n2) {
        this.Update(this.state, arrby, n, n2);
    }

    public void Update(byte[] arrby, int n) {
        this.Update(this.state, arrby, 0, n);
    }

    public void Update(byte[] arrby) {
        this.Update(arrby, 0, arrby.length);
    }

    public void Update(byte by) {
        byte[] arrby = new byte[]{by};
        this.Update(arrby, 1);
    }

    public void Update(String string) {
        byte[] arrby = string.getBytes();
        this.Update(arrby, arrby.length);
    }

    public void Update(int n) {
        this.Update((byte)(n & 0xFF));
    }

    private byte[] Encode(int[] arrn, int n) {
        byte[] arrby = new byte[n];
        int n2 = 0;
        for (int i = 0; i < n; i += 4) {
            arrby[i] = (byte)(arrn[n2] & 0xFF);
            arrby[i + 1] = (byte)(arrn[n2] >>> 8 & 0xFF);
            arrby[i + 2] = (byte)(arrn[n2] >>> 16 & 0xFF);
            arrby[i + 3] = (byte)(arrn[n2] >>> 24 & 0xFF);
            ++n2;
        }
        return arrby;
    }

    public synchronized byte[] Final() {
        if (this.finals == null) {
            MD5State mD5State = new MD5State(this.state);
            byte[] arrby = this.Encode(mD5State.count, 8);
            int n = mD5State.count[0] >>> 3 & 0x3F;
            int n2 = n < 56 ? 56 - n : 120 - n;
            this.Update(mD5State, padding, 0, n2);
            this.Update(mD5State, arrby, 0, 8);
            this.finals = mD5State;
        }
        return this.Encode(this.finals.state, 16);
    }

    public static String asHex(byte[] arrby) {
        StringBuffer stringBuffer = new StringBuffer(arrby.length * 2);
        for (int i = 0; i < arrby.length; ++i) {
            if ((arrby[i] & 0xFF) < 16) {
                stringBuffer.append("0");
            }
            stringBuffer.append(Integer.toString(arrby[i] & 0xFF, 16));
        }
        return stringBuffer.toString();
    }

    public String asHex() {
        return MD5.asHex(this.Final());
    }

    private class MD5State {
        int[] state;
        int[] count;
        byte[] buffer = new byte[64];

        public MD5State() {
            this.count = new int[2];
            this.state = new int[4];
            this.state[0] = 1732584193;
            this.state[1] = -271733879;
            this.state[2] = -1732584194;
            this.state[3] = 271733878;
            this.count[1] = 0;
            this.count[0] = 0;
        }

        public MD5State(MD5State mD5State) {
            this();
            int n;
            for (n = 0; n < this.buffer.length; ++n) {
                this.buffer[n] = mD5State.buffer[n];
            }
            for (n = 0; n < this.state.length; ++n) {
                this.state[n] = mD5State.state[n];
            }
            for (n = 0; n < this.count.length; ++n) {
                this.count[n] = mD5State.count[n];
            }
        }
    }
}

