/*
 * Decompiled with CFR 0.150.
 */
package org.apache.oro.text.awk;

import org.apache.oro.text.regex.MatchResult;

final class AwkMatchResult
implements MatchResult {
    private int __matchBeginOffset;
    private int __length;
    private String __match;

    AwkMatchResult(String string, int n) {
        this.__match = string;
        this.__length = string.length();
        this.__matchBeginOffset = n;
    }

    void _incrementMatchBeginOffset(int n) {
        this.__matchBeginOffset += n;
    }

    public int length() {
        return this.__length;
    }

    public int groups() {
        return 1;
    }

    public String group(int n) {
        return n == 0 ? this.__match : null;
    }

    public int begin(int n) {
        return n == 0 ? 0 : -1;
    }

    public int end(int n) {
        return n == 0 ? this.__length : -1;
    }

    public int beginOffset(int n) {
        return n == 0 ? this.__matchBeginOffset : -1;
    }

    public int endOffset(int n) {
        return n == 0 ? this.__matchBeginOffset + this.__length : -1;
    }

    public String toString() {
        return this.group(0);
    }
}

