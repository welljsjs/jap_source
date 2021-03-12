/*
 * Decompiled with CFR 0.150.
 */
package HTTPClient;

import java.io.EOFException;
import java.io.FileInputStream;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;

class UncompressInputStream
extends FilterInputStream {
    byte[] one = new byte[1];
    private static final int TBL_CLEAR = 256;
    private static final int TBL_FIRST = 257;
    private int[] tab_prefix;
    private byte[] tab_suffix;
    private int[] zeros = new int[256];
    private byte[] stack;
    private boolean block_mode;
    private int n_bits;
    private int maxbits;
    private int maxmaxcode;
    private int maxcode;
    private int bitmask;
    private int oldcode;
    private byte finchar;
    private int stackp;
    private int free_ent;
    private byte[] data = new byte[10000];
    private int bit_pos = 0;
    private int end = 0;
    private int got = 0;
    private boolean eof = false;
    private static final int EXTRA = 64;
    private static final int LZW_MAGIC = 8093;
    private static final int MAX_BITS = 16;
    private static final int INIT_BITS = 9;
    private static final int HDR_MAXBITS = 31;
    private static final int HDR_EXTENDED = 32;
    private static final int HDR_FREE = 64;
    private static final int HDR_BLOCK_MODE = 128;
    private static final boolean debug = false;

    public UncompressInputStream(InputStream inputStream) throws IOException {
        super(inputStream);
        this.parse_header();
    }

    public synchronized int read() throws IOException {
        int n = this.in.read(this.one, 0, 1);
        if (n == 1) {
            return this.one[0] & 0xFF;
        }
        return -1;
    }

    public synchronized int read(byte[] arrby, int n, int n2) throws IOException {
        int n3;
        if (this.eof) {
            return -1;
        }
        int n4 = n;
        int[] arrn = this.tab_prefix;
        byte[] arrby2 = this.tab_suffix;
        byte[] arrby3 = this.stack;
        int n5 = this.n_bits;
        int n6 = this.maxcode;
        int n7 = this.maxmaxcode;
        int n8 = this.bitmask;
        int n9 = this.oldcode;
        byte by = this.finchar;
        int n10 = this.stackp;
        int n11 = this.free_ent;
        byte[] arrby4 = this.data;
        int n12 = this.bit_pos;
        int n13 = arrby3.length - n10;
        if (n13 > 0) {
            n3 = n13 >= n2 ? n2 : n13;
            System.arraycopy(arrby3, n10, arrby, n, n3);
            n += n3;
            n2 -= n3;
            n10 += n3;
        }
        if (n2 == 0) {
            this.stackp = n10;
            return n - n4;
        }
        block0: do {
            if (this.end < 64) {
                this.fill();
            }
            int n14 = n3 = this.got > 0 ? this.end - this.end % n5 << 3 : (this.end << 3) - (n5 - 1);
            while (n12 < n3) {
                int n15;
                int n16;
                if (n11 > n6) {
                    n16 = n5 << 3;
                    n12 = n12 - 1 + n16 - (n12 - 1 + n16) % n16;
                    n6 = ++n5 == this.maxbits ? n7 : (1 << n5) - 1;
                    n8 = (1 << n5) - 1;
                    n12 = this.resetbuf(n12);
                    continue block0;
                }
                n16 = n12 >> 3;
                int n17 = (arrby4[n16] & 0xFF | (arrby4[n16 + 1] & 0xFF) << 8 | (arrby4[n16 + 2] & 0xFF) << 16) >> (n12 & 7) & n8;
                n12 += n5;
                if (n9 == -1) {
                    if (n17 >= 256) {
                        throw new IOException("corrupt input: " + n17 + " > 255");
                    }
                    n9 = n17;
                    by = (byte)n9;
                    arrby[n++] = by;
                    --n2;
                    continue;
                }
                if (n17 == 256 && this.block_mode) {
                    System.arraycopy(this.zeros, 0, arrn, 0, this.zeros.length);
                    n11 = 256;
                    n15 = n5 << 3;
                    n12 = n12 - 1 + n15 - (n12 - 1 + n15) % n15;
                    n5 = 9;
                    n8 = n6 = (1 << n5) - 1;
                    n12 = this.resetbuf(n12);
                    continue block0;
                }
                n15 = n17;
                n10 = arrby3.length;
                if (n17 >= n11) {
                    if (n17 > n11) {
                        throw new IOException("corrupt input: code=" + n17 + ", free_ent=" + n11);
                    }
                    arrby3[--n10] = by;
                    n17 = n9;
                }
                while (n17 >= 256) {
                    arrby3[--n10] = arrby2[n17];
                    n17 = arrn[n17];
                }
                by = arrby2[n17];
                arrby[n++] = by;
                n13 = arrby3.length - n10;
                int n18 = n13 >= --n2 ? n2 : n13;
                System.arraycopy(arrby3, n10, arrby, n, n18);
                n += n18;
                n2 -= n18;
                n10 += n18;
                if (n11 < n7) {
                    arrn[n11] = n9;
                    arrby2[n11] = by;
                    ++n11;
                }
                n9 = n15;
                if (n2 != 0) continue;
                this.n_bits = n5;
                this.maxcode = n6;
                this.bitmask = n8;
                this.oldcode = n9;
                this.finchar = by;
                this.stackp = n10;
                this.free_ent = n11;
                this.bit_pos = n12;
                return n - n4;
            }
            n12 = this.resetbuf(n12);
        } while (this.got > 0);
        this.n_bits = n5;
        this.maxcode = n6;
        this.bitmask = n8;
        this.oldcode = n9;
        this.finchar = by;
        this.stackp = n10;
        this.free_ent = n11;
        this.bit_pos = n12;
        this.eof = true;
        return n - n4;
    }

    private final int resetbuf(int n) {
        int n2 = n >> 3;
        System.arraycopy(this.data, n2, this.data, 0, this.end - n2);
        this.end -= n2;
        return 0;
    }

    private final void fill() throws IOException {
        this.got = this.in.read(this.data, this.end, this.data.length - 1 - this.end);
        if (this.got > 0) {
            this.end += this.got;
        }
    }

    public synchronized long skip(long l) throws IOException {
        byte[] arrby = new byte[(int)l];
        int n = this.read(arrby, 0, (int)l);
        if (n > 0) {
            return n;
        }
        return 0L;
    }

    public synchronized int available() throws IOException {
        if (this.eof) {
            return 0;
        }
        return this.in.available();
    }

    private void parse_header() throws IOException {
        int n = this.in.read();
        if (n < 0) {
            throw new EOFException("Failed to read magic number");
        }
        int n2 = (n & 0xFF) << 8;
        n = this.in.read();
        if (n < 0) {
            throw new EOFException("Failed to read magic number");
        }
        if ((n2 += n & 0xFF) != 8093) {
            throw new IOException("Input not in compress format (read magic number 0x" + Integer.toHexString(n2) + ")");
        }
        int n3 = this.in.read();
        if (n3 < 0) {
            throw new EOFException("Failed to read header");
        }
        this.block_mode = (n3 & 0x80) > 0;
        this.maxbits = n3 & 0x1F;
        if (this.maxbits > 16) {
            throw new IOException("Stream compressed with " + this.maxbits + " bits, but can only handle " + 16 + " bits");
        }
        if ((n3 & 0x20) > 0) {
            throw new IOException("Header extension bit set");
        }
        if ((n3 & 0x40) > 0) {
            throw new IOException("Header bit 6 set");
        }
        this.maxmaxcode = 1 << this.maxbits;
        this.n_bits = 9;
        this.bitmask = this.maxcode = (1 << this.n_bits) - 1;
        this.oldcode = -1;
        this.finchar = 0;
        this.free_ent = this.block_mode ? 257 : 256;
        this.tab_prefix = new int[1 << this.maxbits];
        this.tab_suffix = new byte[1 << this.maxbits];
        this.stack = new byte[1 << this.maxbits];
        this.stackp = this.stack.length;
        for (int i = 255; i >= 0; --i) {
            this.tab_suffix[i] = (byte)i;
        }
    }

    public static void main(String[] arrstring) throws Exception {
        int n;
        if (arrstring.length != 1) {
            System.err.println("Usage: UncompressInputStream <file>");
            System.exit(1);
        }
        UncompressInputStream uncompressInputStream = new UncompressInputStream(new FileInputStream(arrstring[0]));
        byte[] arrby = new byte[100000];
        int n2 = 0;
        long l = System.currentTimeMillis();
        while ((n = ((InputStream)uncompressInputStream).read(arrby)) >= 0) {
            System.out.write(arrby, 0, n);
            n2 += n;
        }
        long l2 = System.currentTimeMillis();
        System.err.println("Decompressed " + n2 + " bytes");
        System.err.println("Time: " + (double)(l2 - l) / 1000.0 + " seconds");
    }
}

