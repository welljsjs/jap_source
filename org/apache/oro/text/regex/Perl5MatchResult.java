/*
 * Decompiled with CFR 0.150.
 */
package org.apache.oro.text.regex;

import org.apache.oro.text.regex.MatchResult;

final class Perl5MatchResult
implements MatchResult {
    int _matchBeginOffset;
    int[] _beginGroupOffset;
    int[] _endGroupOffset;
    String _match;

    Perl5MatchResult(int n) {
        this._beginGroupOffset = new int[n];
        this._endGroupOffset = new int[n];
    }

    public int length() {
        int n = this._endGroupOffset[0] - this._beginGroupOffset[0];
        return n > 0 ? n : 0;
    }

    public int groups() {
        return this._beginGroupOffset.length;
    }

    public String group(int n) {
        if (n < this._beginGroupOffset.length) {
            int n2 = this._beginGroupOffset[n];
            int n3 = this._endGroupOffset[n];
            int n4 = this._match.length();
            if (n2 >= 0 && n3 >= 0) {
                if (n2 < n4 && n3 <= n4 && n3 > n2) {
                    return this._match.substring(n2, n3);
                }
                if (n2 <= n3) {
                    return "";
                }
            }
        }
        return null;
    }

    public int begin(int n) {
        if (n < this._beginGroupOffset.length) {
            int n2 = this._beginGroupOffset[n];
            int n3 = this._endGroupOffset[n];
            if (n2 >= 0 && n3 >= 0) {
                return n2;
            }
        }
        return -1;
    }

    public int end(int n) {
        if (n < this._beginGroupOffset.length) {
            int n2 = this._beginGroupOffset[n];
            int n3 = this._endGroupOffset[n];
            if (n2 >= 0 && n3 >= 0) {
                return n3;
            }
        }
        return -1;
    }

    public int beginOffset(int n) {
        if (n < this._beginGroupOffset.length) {
            int n2 = this._beginGroupOffset[n];
            int n3 = this._endGroupOffset[n];
            if (n2 >= 0 && n3 >= 0) {
                return this._matchBeginOffset + n2;
            }
        }
        return -1;
    }

    public int endOffset(int n) {
        if (n < this._endGroupOffset.length) {
            int n2 = this._beginGroupOffset[n];
            int n3 = this._endGroupOffset[n];
            if (n2 >= 0 && n3 >= 0) {
                return this._matchBeginOffset + n3;
            }
        }
        return -1;
    }

    public String toString() {
        return this.group(0);
    }
}

