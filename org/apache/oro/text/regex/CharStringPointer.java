/*
 * Decompiled with CFR 0.150.
 */
package org.apache.oro.text.regex;

final class CharStringPointer {
    static final char _END_OF_STRING = '\uffff';
    int _offset;
    char[] _array;

    CharStringPointer(char[] arrc, int n) {
        this._array = arrc;
        this._offset = n;
    }

    CharStringPointer(char[] arrc) {
        this(arrc, 0);
    }

    char _getValue() {
        return this._getValue(this._offset);
    }

    char _getValue(int n) {
        if (n < this._array.length && n >= 0) {
            return this._array[n];
        }
        return '\uffff';
    }

    char _getValueRelative(int n) {
        return this._getValue(this._offset + n);
    }

    int _getLength() {
        return this._array.length;
    }

    int _getOffset() {
        return this._offset;
    }

    void _setOffset(int n) {
        this._offset = n;
    }

    boolean _isAtEnd() {
        return this._offset >= this._array.length;
    }

    char _increment(int n) {
        this._offset += n;
        if (this._isAtEnd()) {
            this._offset = this._array.length;
            return '\uffff';
        }
        return this._array[this._offset];
    }

    char _increment() {
        return this._increment(1);
    }

    char _decrement(int n) {
        this._offset -= n;
        if (this._offset < 0) {
            this._offset = 0;
        }
        return this._array[this._offset];
    }

    char _decrement() {
        return this._decrement(1);
    }

    char _postIncrement() {
        char c = this._getValue();
        this._increment();
        return c;
    }

    char _postDecrement() {
        char c = this._getValue();
        this._decrement();
        return c;
    }

    String _toString(int n) {
        return new String(this._array, n, this._array.length - n);
    }

    public String toString() {
        return this._toString(0);
    }
}

