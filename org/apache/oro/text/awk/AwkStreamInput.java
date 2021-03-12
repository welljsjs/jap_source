/*
 * Decompiled with CFR 0.150.
 */
package org.apache.oro.text.awk;

import java.io.IOException;
import java.io.Reader;

public final class AwkStreamInput {
    static final int _DEFAULT_BUFFER_INCREMENT = 2048;
    private Reader __searchStream;
    private int __bufferIncrementUnit;
    boolean _endOfStreamReached;
    int _bufferSize;
    int _bufferOffset;
    int _currentOffset;
    char[] _buffer;

    AwkStreamInput() {
        this._currentOffset = 0;
    }

    public AwkStreamInput(Reader reader, int n) {
        this.__searchStream = reader;
        this.__bufferIncrementUnit = n;
        this._buffer = new char[n];
        this._currentOffset = 0;
        this._bufferSize = 0;
        this._bufferOffset = 0;
        this._endOfStreamReached = false;
    }

    public AwkStreamInput(Reader reader) {
        this(reader, 2048);
    }

    int _reallocate(int n) throws IOException {
        if (this._endOfStreamReached) {
            return this._bufferSize;
        }
        int n2 = this._bufferSize - n;
        char[] arrc = new char[n2 + this.__bufferIncrementUnit];
        int n3 = this.__searchStream.read(arrc, n2, this.__bufferIncrementUnit);
        if (n3 <= 0) {
            this._endOfStreamReached = true;
            if (n3 == 0) {
                throw new IOException("read from input stream returned 0 bytes.");
            }
            return this._bufferSize;
        }
        this._bufferOffset += n;
        this._bufferSize = n2 + n3;
        System.arraycopy(this._buffer, n, arrc, 0, n2);
        this._buffer = arrc;
        return n2;
    }

    boolean read() throws IOException {
        this._bufferOffset += this._bufferSize;
        this._bufferSize = this.__searchStream.read(this._buffer);
        this._endOfStreamReached = this._bufferSize == -1;
        return !this._endOfStreamReached;
    }

    public boolean endOfStream() {
        return this._endOfStreamReached;
    }
}

