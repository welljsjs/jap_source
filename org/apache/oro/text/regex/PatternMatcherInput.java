/*
 * Decompiled with CFR 0.150.
 */
package org.apache.oro.text.regex;

public final class PatternMatcherInput {
    String _originalStringInput;
    char[] _originalCharInput;
    char[] _originalBuffer;
    char[] _toLowerBuffer;
    int _beginOffset;
    int _endOffset;
    int _currentOffset;
    int _matchBeginOffset = -1;
    int _matchEndOffset = -1;

    public PatternMatcherInput(String string, int n, int n2) {
        this.setInput(string, n, n2);
    }

    public PatternMatcherInput(String string) {
        this(string, 0, string.length());
    }

    public PatternMatcherInput(char[] arrc, int n, int n2) {
        this.setInput(arrc, n, n2);
    }

    public PatternMatcherInput(char[] arrc) {
        this(arrc, 0, arrc.length);
    }

    public int length() {
        return this._endOffset - this._beginOffset;
    }

    public void setInput(String string, int n, int n2) {
        this._originalStringInput = string;
        this._originalCharInput = null;
        this._toLowerBuffer = null;
        this._originalBuffer = string.toCharArray();
        this.setCurrentOffset(n);
        this.setBeginOffset(n);
        this.setEndOffset(this._beginOffset + n2);
    }

    public void setInput(String string) {
        this.setInput(string, 0, string.length());
    }

    public void setInput(char[] arrc, int n, int n2) {
        this._originalStringInput = null;
        this._toLowerBuffer = null;
        this._originalCharInput = arrc;
        this._originalBuffer = arrc;
        this.setCurrentOffset(n);
        this.setBeginOffset(n);
        this.setEndOffset(this._beginOffset + n2);
    }

    public void setInput(char[] arrc) {
        this.setInput(arrc, 0, arrc.length);
    }

    public char charAt(int n) {
        return this._originalBuffer[this._beginOffset + n];
    }

    public String substring(int n, int n2) {
        return new String(this._originalBuffer, this._beginOffset + n, n2 - n);
    }

    public String substring(int n) {
        return new String(this._originalBuffer, n += this._beginOffset, this._endOffset - n);
    }

    public Object getInput() {
        if (this._originalStringInput == null) {
            return this._originalCharInput;
        }
        return this._originalStringInput;
    }

    public char[] getBuffer() {
        return this._originalBuffer;
    }

    public boolean endOfInput() {
        return this._currentOffset >= this._endOffset;
    }

    public int getBeginOffset() {
        return this._beginOffset;
    }

    public int getEndOffset() {
        return this._endOffset;
    }

    public int getCurrentOffset() {
        return this._currentOffset;
    }

    public void setBeginOffset(int n) {
        this._beginOffset = n;
    }

    public void setEndOffset(int n) {
        this._endOffset = n;
    }

    public void setCurrentOffset(int n) {
        this._currentOffset = n;
        this.setMatchOffsets(-1, -1);
    }

    public String toString() {
        return new String(this._originalBuffer, this._beginOffset, this.length());
    }

    public String preMatch() {
        return new String(this._originalBuffer, this._beginOffset, this._matchBeginOffset - this._beginOffset);
    }

    public String postMatch() {
        return new String(this._originalBuffer, this._matchEndOffset, this._endOffset - this._matchEndOffset);
    }

    public String match() {
        return new String(this._originalBuffer, this._matchBeginOffset, this._matchEndOffset - this._matchBeginOffset);
    }

    public void setMatchOffsets(int n, int n2) {
        this._matchBeginOffset = n;
        this._matchEndOffset = n2;
    }

    public int getMatchBeginOffset() {
        return this._matchBeginOffset;
    }

    public int getMatchEndOffset() {
        return this._matchEndOffset;
    }
}

