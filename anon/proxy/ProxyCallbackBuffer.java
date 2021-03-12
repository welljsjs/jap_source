/*
 * Decompiled with CFR 0.150.
 */
package anon.proxy;

import java.io.ByteArrayOutputStream;

public class ProxyCallbackBuffer {
    private byte[] chunk = null;
    private int modificationStartOffset = 0;
    private int modificationEndOffset = 0;
    private int payloadLength = 0;
    private int status = 2;

    public ProxyCallbackBuffer() {
        this(new byte[1000]);
    }

    public ProxyCallbackBuffer(byte[] arrby) {
        this(arrby, 0, arrby.length - 1);
    }

    public ProxyCallbackBuffer(byte[] arrby, int n, int n2) {
        this.setChunk(arrby);
        this.setModificationStartOffset(n);
        this.setPayloadLength(n2);
        this.setModificationEndOffset(n2 - 1);
        this.status = 2;
    }

    public ProxyCallbackBuffer(byte[] arrby, int n, int n2, int n3) {
        this.setChunk(arrby);
        this.setModificationStartOffset(n);
        this.setModificationEndOffset(n2);
        this.status = 2;
    }

    public byte[] getChunk() {
        return this.chunk;
    }

    public void setChunk(byte[] arrby) {
        this.chunk = arrby;
        this.modificationStartOffset = 0;
        this.modificationEndOffset = arrby.length - 1;
        this.payloadLength = arrby.length;
    }

    public int getModificationStartOffset() {
        return this.modificationStartOffset;
    }

    public void setModificationStartOffset(int n) {
        if (n < 0 || n > this.chunk.length) {
            throw new ArrayIndexOutOfBoundsException("Illegal modification start index: " + n + " (chunk length: " + this.chunk.length + ")");
        }
        this.modificationStartOffset = n;
    }

    public int getModificationEndOffset() {
        return this.modificationEndOffset;
    }

    public void setModificationEndOffset(int n) {
        if (n < 0 || n > this.chunk.length) {
            throw new ArrayIndexOutOfBoundsException("Illegal modification end index: " + n + " (chunk length: " + this.chunk.length + ")");
        }
        this.modificationEndOffset = n;
    }

    public int getPayloadLength() {
        return this.payloadLength;
    }

    public void setPayloadLength(int n) {
        if (n < 0 || n > this.chunk.length) {
            throw new ArrayIndexOutOfBoundsException("Illegal payload length: " + n);
        }
        this.payloadLength = n;
    }

    public int getStatus() {
        return this.status;
    }

    public void setStatus(int n) {
        if (n < 0 || n > 2) {
            throw new IllegalArgumentException("Illegal status specified: " + n);
        }
        this.status = n;
    }

    public void copyLeadingData(ByteArrayOutputStream byteArrayOutputStream) {
        if (this.modificationStartOffset > 0) {
            byteArrayOutputStream.write(this.chunk, 0, this.modificationStartOffset);
        }
    }

    public void copyLeadingData(byte[] arrby) {
        this.copyLeadingData(arrby, 0);
    }

    public void copyLeadingData(byte[] arrby, int n) {
        if (n + this.modificationStartOffset > arrby.length) {
            throw new ArrayIndexOutOfBoundsException("leading data length " + this.modificationStartOffset + " excceeds destination array");
        }
        if (this.modificationStartOffset > 0) {
            System.arraycopy(this.chunk, 0, arrby, n, this.modificationStartOffset);
        }
    }

    public void copyTrailingData(ByteArrayOutputStream byteArrayOutputStream) {
        int n = this.getTrailingDataLength();
        if (n > 0) {
            byteArrayOutputStream.write(this.chunk, this.modificationEndOffset + 1, n);
        }
    }

    public void copyTrailingData(byte[] arrby, int n) {
        int n2 = this.getTrailingDataLength();
        if (n + n2 > arrby.length) {
            throw new ArrayIndexOutOfBoundsException("trailing data length " + n2 + " excceeds destination array");
        }
        if (n2 > 0) {
            System.arraycopy(this.chunk, this.modificationEndOffset + 1, arrby, n, n2);
        }
    }

    public synchronized byte[] extractModificationData() {
        byte[] arrby = null;
        if (this.modificationStartOffset == 0 && this.modificationEndOffset == 0) {
            arrby = this.chunk;
            this.payloadLength = 0;
            this.modificationEndOffset = 0;
            this.modificationStartOffset = 0;
            this.chunk = new byte[0];
        } else {
            arrby = new byte[this.getModificationDataLength()];
            System.arraycopy(this.chunk, this.modificationStartOffset, arrby, 0, arrby.length);
            this.payloadLength -= arrby.length;
            byte[] arrby2 = new byte[this.chunk.length - arrby.length];
            this.copyLeadingData(arrby2);
            this.copyTrailingData(arrby2, this.modificationStartOffset);
            this.modificationEndOffset = this.modificationStartOffset;
            this.chunk = arrby2;
        }
        return arrby;
    }

    public synchronized void injectModificationData(byte[] arrby) {
        this.injectModificationData(arrby, 0, arrby.length);
    }

    public synchronized void injectModificationData(byte[] arrby, int n, int n2) {
        if (n < 0) {
            throw new IllegalArgumentException("Offset must be >= 0");
        }
        if (n2 < 0) {
            throw new IllegalArgumentException("Length must be >= 0");
        }
        byte[] arrby2 = new byte[this.chunk.length + n2];
        this.copyLeadingData(arrby2);
        this.copyTrailingData(arrby2, this.modificationEndOffset + n2);
        System.arraycopy(arrby, n, arrby2, this.modificationStartOffset, n2);
        System.arraycopy(this.chunk, this.modificationStartOffset, arrby2, n2, this.getModificationDataLength());
        this.modificationEndOffset += n2;
        this.payloadLength += n2;
        this.chunk = arrby2;
    }

    public int getModificationDataLength() {
        return this.modificationEndOffset - this.modificationStartOffset + 1;
    }

    public int getLeadingDataLength() {
        return this.modificationStartOffset;
    }

    public int getTrailingDataLength() {
        return this.payloadLength - (this.modificationEndOffset + 1);
    }
}

