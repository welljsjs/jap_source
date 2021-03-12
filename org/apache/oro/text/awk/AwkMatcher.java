/*
 * Decompiled with CFR 0.150.
 */
package org.apache.oro.text.awk;

import java.io.IOException;
import org.apache.oro.text.awk.AwkMatchResult;
import org.apache.oro.text.awk.AwkPattern;
import org.apache.oro.text.awk.AwkStreamInput;
import org.apache.oro.text.regex.MatchResult;
import org.apache.oro.text.regex.Pattern;
import org.apache.oro.text.regex.PatternMatcher;
import org.apache.oro.text.regex.PatternMatcherInput;

public final class AwkMatcher
implements PatternMatcher {
    private int __lastMatchedBufferOffset;
    private AwkMatchResult __lastMatchResult = null;
    private AwkStreamInput __scratchBuffer;
    private AwkStreamInput __streamSearchBuffer;
    private AwkPattern __awkPattern;
    private int[] __offsets = new int[2];
    private int __beginOffset;

    public AwkMatcher() {
        this.__scratchBuffer = new AwkStreamInput();
        this.__scratchBuffer._endOfStreamReached = true;
    }

    public boolean matchesPrefix(char[] arrc, Pattern pattern, int n) {
        int n2 = -1;
        this.__awkPattern = (AwkPattern)pattern;
        this.__scratchBuffer._buffer = arrc;
        this.__scratchBuffer._bufferSize = arrc.length;
        this.__beginOffset = 0;
        this.__scratchBuffer._bufferOffset = 0;
        this.__scratchBuffer._endOfStreamReached = true;
        this.__streamSearchBuffer = this.__scratchBuffer;
        this.__offsets[0] = n;
        try {
            n2 = this.__streamMatchPrefix();
        }
        catch (IOException iOException) {
            n2 = -1;
        }
        if (n2 < 0) {
            this.__lastMatchResult = null;
            return false;
        }
        this.__lastMatchResult = new AwkMatchResult(new String(arrc, 0, n2), n);
        return true;
    }

    public boolean matchesPrefix(char[] arrc, Pattern pattern) {
        return this.matchesPrefix(arrc, pattern, 0);
    }

    public boolean matchesPrefix(String string, Pattern pattern) {
        return this.matchesPrefix(string.toCharArray(), pattern, 0);
    }

    public boolean matchesPrefix(PatternMatcherInput patternMatcherInput, Pattern pattern) {
        int n = -1;
        this.__awkPattern = (AwkPattern)pattern;
        this.__scratchBuffer._buffer = patternMatcherInput.getBuffer();
        this.__scratchBuffer._bufferOffset = this.__beginOffset = patternMatcherInput.getBeginOffset();
        this.__offsets[0] = patternMatcherInput.getCurrentOffset();
        this.__scratchBuffer._bufferSize = patternMatcherInput.length();
        this.__scratchBuffer._endOfStreamReached = true;
        this.__streamSearchBuffer = this.__scratchBuffer;
        try {
            n = this.__streamMatchPrefix();
        }
        catch (IOException iOException) {
            n = -1;
        }
        if (n < 0) {
            this.__lastMatchResult = null;
            return false;
        }
        this.__lastMatchResult = new AwkMatchResult(new String(this.__scratchBuffer._buffer, this.__offsets[0], n), this.__offsets[0]);
        return true;
    }

    public boolean matches(char[] arrc, Pattern pattern) {
        int n = -1;
        this.__awkPattern = (AwkPattern)pattern;
        this.__scratchBuffer._buffer = arrc;
        this.__scratchBuffer._bufferSize = arrc.length;
        this.__beginOffset = 0;
        this.__scratchBuffer._bufferOffset = 0;
        this.__scratchBuffer._endOfStreamReached = true;
        this.__streamSearchBuffer = this.__scratchBuffer;
        this.__offsets[0] = 0;
        try {
            n = this.__streamMatchPrefix();
        }
        catch (IOException iOException) {
            n = -1;
        }
        if (n != arrc.length) {
            this.__lastMatchResult = null;
            return false;
        }
        this.__lastMatchResult = new AwkMatchResult(new String(arrc, 0, n), 0);
        return true;
    }

    public boolean matches(String string, Pattern pattern) {
        return this.matches(string.toCharArray(), pattern);
    }

    public boolean matches(PatternMatcherInput patternMatcherInput, Pattern pattern) {
        int n = -1;
        this.__awkPattern = (AwkPattern)pattern;
        this.__scratchBuffer._buffer = patternMatcherInput.getBuffer();
        this.__scratchBuffer._bufferSize = patternMatcherInput.length();
        this.__scratchBuffer._bufferOffset = this.__beginOffset = patternMatcherInput.getBeginOffset();
        this.__offsets[0] = patternMatcherInput.getBeginOffset();
        this.__scratchBuffer._endOfStreamReached = true;
        this.__streamSearchBuffer = this.__scratchBuffer;
        try {
            n = this.__streamMatchPrefix();
        }
        catch (IOException iOException) {
            n = -1;
        }
        if (n != this.__scratchBuffer._bufferSize) {
            this.__lastMatchResult = null;
            return false;
        }
        this.__lastMatchResult = new AwkMatchResult(new String(this.__scratchBuffer._buffer, this.__offsets[0], this.__scratchBuffer._bufferSize), this.__offsets[0]);
        return true;
    }

    public boolean contains(char[] arrc, Pattern pattern) {
        this.__awkPattern = (AwkPattern)pattern;
        if (this.__awkPattern._hasBeginAnchor && !this.__awkPattern._fastMap[arrc[0]]) {
            this.__lastMatchResult = null;
            return false;
        }
        this.__scratchBuffer._buffer = arrc;
        this.__scratchBuffer._bufferSize = arrc.length;
        this.__beginOffset = 0;
        this.__scratchBuffer._bufferOffset = 0;
        this.__scratchBuffer._endOfStreamReached = true;
        this.__streamSearchBuffer = this.__scratchBuffer;
        this.__lastMatchedBufferOffset = 0;
        try {
            this._search();
        }
        catch (IOException iOException) {
            // empty catch block
        }
        return this.__lastMatchResult != null;
    }

    public boolean contains(String string, Pattern pattern) {
        return this.contains(string.toCharArray(), pattern);
    }

    public boolean contains(PatternMatcherInput patternMatcherInput, Pattern pattern) {
        this.__awkPattern = (AwkPattern)pattern;
        this.__scratchBuffer._buffer = patternMatcherInput.getBuffer();
        this.__scratchBuffer._bufferOffset = this.__beginOffset = patternMatcherInput.getBeginOffset();
        this.__lastMatchedBufferOffset = patternMatcherInput.getCurrentOffset();
        if (this.__awkPattern._hasBeginAnchor && (this.__beginOffset != this.__lastMatchedBufferOffset || !this.__awkPattern._fastMap[this.__scratchBuffer._buffer[this.__beginOffset]])) {
            this.__lastMatchResult = null;
            return false;
        }
        this.__scratchBuffer._bufferSize = patternMatcherInput.length();
        this.__scratchBuffer._endOfStreamReached = true;
        this.__streamSearchBuffer = this.__scratchBuffer;
        try {
            this._search();
        }
        catch (IOException iOException) {
            // empty catch block
        }
        patternMatcherInput.setCurrentOffset(this.__lastMatchedBufferOffset);
        if (this.__lastMatchResult == null) {
            return false;
        }
        patternMatcherInput.setMatchOffsets(this.__lastMatchResult.beginOffset(0), this.__lastMatchResult.endOffset(0));
        return true;
    }

    public boolean contains(AwkStreamInput awkStreamInput, Pattern pattern) throws IOException {
        this.__awkPattern = (AwkPattern)pattern;
        if (this.__awkPattern._hasBeginAnchor) {
            if (awkStreamInput._bufferOffset == 0) {
                if (awkStreamInput.read() && !this.__awkPattern._fastMap[awkStreamInput._buffer[0]]) {
                    this.__lastMatchResult = null;
                    return false;
                }
            } else {
                this.__lastMatchResult = null;
                return false;
            }
        }
        this.__lastMatchedBufferOffset = awkStreamInput._currentOffset;
        this.__streamSearchBuffer = awkStreamInput;
        this.__beginOffset = 0;
        this._search();
        awkStreamInput._currentOffset = this.__lastMatchedBufferOffset;
        if (this.__lastMatchResult != null) {
            this.__lastMatchResult._incrementMatchBeginOffset(awkStreamInput._bufferOffset);
            return true;
        }
        return false;
    }

    private int __streamMatchPrefix() throws IOException {
        int n;
        int n2 = 1;
        int n3 = -1;
        int n4 = n = this.__offsets[0];
        int n5 = this.__streamSearchBuffer._bufferSize + this.__beginOffset;
        while (n4 < n5) {
            char c = this.__streamSearchBuffer._buffer[n4++];
            if (n2 >= this.__awkPattern._numStates) break;
            int n6 = n2;
            int[] arrn = this.__awkPattern._getStateArray(n2);
            if ((n2 = arrn[c]) == 0) {
                this.__awkPattern._createNewState(n6, c, arrn);
                n2 = arrn[c];
            }
            if (n2 == -1) break;
            if (this.__awkPattern._endStates.get(n2)) {
                n3 = n4;
            }
            if (n4 != n5 || (n4 = this.__streamSearchBuffer._reallocate(n) + this.__beginOffset) == (n5 = this.__streamSearchBuffer._bufferSize + this.__beginOffset)) continue;
            if (n3 != -1) {
                n3 -= n;
            }
            n = 0;
        }
        this.__offsets[0] = n;
        this.__offsets[1] = n3 - 1;
        if (n3 == -1 && this.__awkPattern._matchesNullString) {
            return 0;
        }
        if (this.__awkPattern._hasEndAnchor && (!this.__streamSearchBuffer._endOfStreamReached || n3 < this.__streamSearchBuffer._bufferSize + this.__beginOffset)) {
            return -1;
        }
        return n3 - n;
    }

    void _search() throws IOException {
        this.__lastMatchResult = null;
        while (true) {
            if (this.__lastMatchedBufferOffset >= this.__streamSearchBuffer._bufferSize + this.__beginOffset) {
                if (this.__streamSearchBuffer._endOfStreamReached) {
                    this.__streamSearchBuffer = null;
                    return;
                }
                if (!this.__streamSearchBuffer.read()) {
                    return;
                }
                this.__lastMatchedBufferOffset = 0;
            }
            int n = this.__lastMatchedBufferOffset;
            while (n < this.__streamSearchBuffer._bufferSize + this.__beginOffset) {
                int n2;
                this.__offsets[0] = n;
                if (this.__awkPattern._fastMap[this.__streamSearchBuffer._buffer[n]] && (n2 = this.__streamMatchPrefix()) > -1) {
                    this.__lastMatchResult = new AwkMatchResult(new String(this.__streamSearchBuffer._buffer, this.__offsets[0], n2), this.__offsets[0]);
                    this.__lastMatchedBufferOffset = n2 > 0 ? this.__offsets[1] + 1 : this.__offsets[0] + 1;
                    return;
                }
                if (this.__awkPattern._matchesNullString) {
                    this.__lastMatchResult = new AwkMatchResult(new String(), n);
                    this.__lastMatchedBufferOffset = n + 1;
                    return;
                }
                n = this.__offsets[0] + 1;
            }
            this.__lastMatchedBufferOffset = n;
        }
    }

    public MatchResult getMatch() {
        return this.__lastMatchResult;
    }
}

