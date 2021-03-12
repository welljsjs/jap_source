/*
 * Decompiled with CFR 0.150.
 */
package org.apache.oro.text.regex;

import java.util.Stack;
import org.apache.oro.text.regex.MatchResult;
import org.apache.oro.text.regex.OpCode;
import org.apache.oro.text.regex.Pattern;
import org.apache.oro.text.regex.PatternMatcher;
import org.apache.oro.text.regex.PatternMatcherInput;
import org.apache.oro.text.regex.Perl5MatchResult;
import org.apache.oro.text.regex.Perl5Pattern;
import org.apache.oro.text.regex.Perl5Repetition;

public final class Perl5Matcher
implements PatternMatcher {
    private static final char __EOS = '\uffff';
    private static final int __INITIAL_NUM_OFFSETS = 20;
    private boolean __multiline = false;
    private boolean __lastSuccess = false;
    private boolean __caseInsensitive = false;
    private char __previousChar;
    private char[] __input;
    private char[] __originalInput;
    private Perl5Repetition __currentRep;
    private int __numParentheses;
    private int __bol;
    private int __eol;
    private int __currentOffset;
    private int __endOffset;
    private char[] __program;
    private int __expSize;
    private int __inputOffset;
    private int __lastParen;
    private int[] __beginMatchOffsets;
    private int[] __endMatchOffsets;
    private Stack __stack = new Stack();
    private Perl5MatchResult __lastMatchResult = null;
    private static final int __DEFAULT_LAST_MATCH_END_OFFSET = -100;
    private int __lastMatchInputEndOffset = -100;

    private static boolean __compare(char[] arrc, int n, char[] arrc2, int n2, int n3) {
        int n4 = 0;
        while (n4 < n3) {
            if (n >= arrc.length) {
                return false;
            }
            if (n2 >= arrc2.length) {
                return false;
            }
            if (arrc[n] != arrc2[n2]) {
                return false;
            }
            ++n4;
            ++n;
            ++n2;
        }
        return true;
    }

    private static int __findFirst(char[] arrc, int n, int n2, char[] arrc2) {
        if (arrc.length == 0) {
            return n2;
        }
        char c = arrc2[0];
        while (n < n2) {
            if (c == arrc[n]) {
                int n3;
                int n4 = n;
                for (n3 = 0; n < n2 && n3 < arrc2.length && arrc2[n3] == arrc[n]; ++n3, ++n) {
                }
                n = n4;
                if (n3 >= arrc2.length) break;
            }
            ++n;
        }
        return n;
    }

    private void __pushState(int n) {
        int n2 = 3 * (this.__expSize - n);
        int[] arrn = n2 <= 0 ? new int[3] : new int[n2 + 3];
        arrn[0] = this.__expSize;
        arrn[1] = this.__lastParen;
        arrn[2] = this.__inputOffset;
        int n3 = this.__expSize;
        while (n3 > n) {
            arrn[n2] = this.__endMatchOffsets[n3];
            arrn[n2 + 1] = this.__beginMatchOffsets[n3];
            arrn[n2 + 2] = n3--;
            n2 -= 3;
        }
        this.__stack.push(arrn);
    }

    private void __popState() {
        int n;
        int[] arrn = (int[])this.__stack.pop();
        this.__expSize = arrn[0];
        this.__lastParen = arrn[1];
        this.__inputOffset = arrn[2];
        for (int i = 3; i < arrn.length; i += 3) {
            n = arrn[i + 2];
            this.__beginMatchOffsets[n] = arrn[i + 1];
            if (n > this.__lastParen) continue;
            this.__endMatchOffsets[n] = arrn[i];
        }
        for (n = this.__lastParen + 1; n <= this.__numParentheses; ++n) {
            if (n > this.__expSize) {
                this.__beginMatchOffsets[n] = -1;
            }
            this.__endMatchOffsets[n] = -1;
        }
    }

    private void __initInterpreterGlobals(Perl5Pattern perl5Pattern, char[] arrc, int n, int n2, int n3) {
        this.__caseInsensitive = perl5Pattern._isCaseInsensitive;
        this.__input = arrc;
        this.__endOffset = n2;
        this.__currentRep = new Perl5Repetition();
        this.__currentRep._numInstances = 0;
        this.__currentRep._lastRepetition = null;
        this.__program = perl5Pattern._program;
        this.__stack.setSize(0);
        if (n3 == n || n3 <= 0) {
            this.__previousChar = (char)10;
        } else {
            this.__previousChar = arrc[n3 - 1];
            if (!this.__multiline && this.__previousChar == '\n') {
                this.__previousChar = '\u0000';
            }
        }
        this.__numParentheses = perl5Pattern._numParentheses;
        this.__currentOffset = n3;
        this.__bol = n;
        this.__eol = n2;
        n2 = this.__numParentheses + 1;
        if (this.__beginMatchOffsets == null || n2 > this.__beginMatchOffsets.length) {
            if (n2 < 20) {
                n2 = 20;
            }
            this.__beginMatchOffsets = new int[n2];
            this.__endMatchOffsets = new int[n2];
        }
    }

    private void __setLastMatchResult() {
        int n = 0;
        this.__lastMatchResult = new Perl5MatchResult(this.__numParentheses + 1);
        if (this.__endMatchOffsets[0] > this.__originalInput.length) {
            throw new ArrayIndexOutOfBoundsException();
        }
        this.__lastMatchResult._matchBeginOffset = this.__beginMatchOffsets[0];
        while (this.__numParentheses >= 0) {
            int n2 = this.__beginMatchOffsets[this.__numParentheses];
            this.__lastMatchResult._beginGroupOffset[this.__numParentheses] = n2 >= 0 ? n2 - this.__lastMatchResult._matchBeginOffset : -1;
            n2 = this.__endMatchOffsets[this.__numParentheses];
            if (n2 >= 0) {
                this.__lastMatchResult._endGroupOffset[this.__numParentheses] = n2 - this.__lastMatchResult._matchBeginOffset;
                if (n2 > n && n2 <= this.__originalInput.length) {
                    n = n2;
                }
            } else {
                this.__lastMatchResult._endGroupOffset[this.__numParentheses] = -1;
            }
            --this.__numParentheses;
        }
        this.__lastMatchResult._match = new String(this.__originalInput, this.__beginMatchOffsets[0], n - this.__beginMatchOffsets[0]);
        this.__originalInput = null;
    }

    private boolean __interpret(Perl5Pattern perl5Pattern, char[] arrc, int n, int n2, int n3) {
        boolean bl;
        block82: {
            int n4;
            int n5;
            block87: {
                boolean bl2;
                block86: {
                    char[] arrc2;
                    block85: {
                        block83: {
                            block84: {
                                n5 = 0;
                                n4 = 0;
                                this.__initInterpreterGlobals(perl5Pattern, arrc, n, n2, n3);
                                bl = false;
                                arrc2 = perl5Pattern._mustString;
                                if (arrc2 == null || (perl5Pattern._anchor & 3) != 0 && (!this.__multiline && (perl5Pattern._anchor & 2) == 0 || perl5Pattern._back < 0)) break block83;
                                this.__currentOffset = Perl5Matcher.__findFirst(this.__input, this.__currentOffset, n2, arrc2);
                                if (this.__currentOffset < n2) break block84;
                                if ((perl5Pattern._options & 0x8000) == 0) {
                                    ++perl5Pattern._mustUtility;
                                }
                                bl = false;
                                break block82;
                            }
                            if (perl5Pattern._back >= 0) {
                                this.__currentOffset -= perl5Pattern._back;
                                if (this.__currentOffset < n3) {
                                    this.__currentOffset = n3;
                                }
                                n5 = perl5Pattern._back + arrc2.length;
                            } else if (!perl5Pattern._isExpensive && (perl5Pattern._options & 0x8000) == 0 && --perl5Pattern._mustUtility < 0) {
                                perl5Pattern._mustString = null;
                                arrc2 = null;
                                this.__currentOffset = n3;
                            } else {
                                this.__currentOffset = n3;
                                n5 = arrc2.length;
                            }
                        }
                        if ((perl5Pattern._anchor & 3) == 0) break block85;
                        if (this.__currentOffset == n && this.__tryExpression(n)) {
                            bl = true;
                        } else if (this.__multiline || (perl5Pattern._anchor & 2) != 0 || (perl5Pattern._anchor & 8) != 0) {
                            if (n5 > 0) {
                                n4 = n5 - 1;
                            }
                            n2 -= n4;
                            if (this.__currentOffset > n3) {
                                --this.__currentOffset;
                            }
                            while (this.__currentOffset < n2) {
                                if (this.__input[this.__currentOffset++] != '\n' || this.__currentOffset >= n2 || !this.__tryExpression(this.__currentOffset)) continue;
                                bl = true;
                                break block82;
                            }
                        }
                        break block82;
                    }
                    if (perl5Pattern._startString == null) break block86;
                    arrc2 = perl5Pattern._startString;
                    if ((perl5Pattern._anchor & 4) != 0) {
                        char c = arrc2[0];
                        while (this.__currentOffset < n2) {
                            if (c == this.__input[this.__currentOffset]) {
                                if (this.__tryExpression(this.__currentOffset)) {
                                    bl = true;
                                    break block82;
                                }
                                ++this.__currentOffset;
                                while (this.__currentOffset < n2 && this.__input[this.__currentOffset] == c) {
                                    ++this.__currentOffset;
                                }
                            }
                            ++this.__currentOffset;
                        }
                    } else {
                        while ((this.__currentOffset = Perl5Matcher.__findFirst(this.__input, this.__currentOffset, n2, arrc2)) < n2) {
                            if (this.__tryExpression(this.__currentOffset)) {
                                bl = true;
                                break block82;
                            }
                            ++this.__currentOffset;
                        }
                    }
                    break block82;
                }
                int n6 = perl5Pattern._startClassOffset;
                if (n6 == -1) break block87;
                boolean bl3 = bl2 = (perl5Pattern._anchor & 4) == 0;
                if (n5 > 0) {
                    n4 = n5 - 1;
                }
                n2 -= n4;
                boolean bl4 = true;
                char c = this.__program[n6];
                block0 : switch (c) {
                    case '\t': {
                        n6 = OpCode._getOperand(n6);
                        while (this.__currentOffset < n2) {
                            char c2 = this.__input[this.__currentOffset];
                            if (c2 < '\u0100' && (this.__program[n6 + (c2 >> 4)] & 1 << (c2 & 0xF)) == 0) {
                                if (bl4 && this.__tryExpression(this.__currentOffset)) {
                                    bl = true;
                                    break block0;
                                }
                                bl4 = bl2;
                            } else {
                                bl4 = true;
                            }
                            ++this.__currentOffset;
                        }
                        break block82;
                    }
                    case '#': 
                    case '$': {
                        n6 = OpCode._getOperand(n6);
                        while (this.__currentOffset < n2) {
                            char c3 = this.__input[this.__currentOffset];
                            if (this.__matchUnicodeClass(c3, this.__program, n6, c)) {
                                if (bl4 && this.__tryExpression(this.__currentOffset)) {
                                    bl = true;
                                    break block0;
                                }
                                bl4 = bl2;
                            } else {
                                bl4 = true;
                            }
                            ++this.__currentOffset;
                        }
                        break block82;
                    }
                    case '\u0014': {
                        char c4;
                        if (n5 > 0) {
                            ++n4;
                            --n2;
                        }
                        if (this.__currentOffset != n) {
                            c4 = this.__input[this.__currentOffset - 1];
                            bl4 = OpCode._isWordCharacter(c4);
                        } else {
                            bl4 = OpCode._isWordCharacter(this.__previousChar);
                        }
                        while (this.__currentOffset < n2) {
                            c4 = this.__input[this.__currentOffset];
                            if (bl4 != OpCode._isWordCharacter(c4)) {
                                boolean bl5 = bl4 = !bl4;
                                if (this.__tryExpression(this.__currentOffset)) {
                                    bl = true;
                                    break block0;
                                }
                            }
                            ++this.__currentOffset;
                        }
                        if ((n5 > 0 || bl4) && this.__tryExpression(this.__currentOffset)) {
                            bl = true;
                            break;
                        }
                        break block82;
                    }
                    case '\u0015': {
                        char c5;
                        if (n5 > 0) {
                            ++n4;
                            --n2;
                        }
                        if (this.__currentOffset != n) {
                            c5 = this.__input[this.__currentOffset - 1];
                            bl4 = OpCode._isWordCharacter(c5);
                        } else {
                            bl4 = OpCode._isWordCharacter(this.__previousChar);
                        }
                        while (this.__currentOffset < n2) {
                            c5 = this.__input[this.__currentOffset];
                            if (bl4 != OpCode._isWordCharacter(c5)) {
                                bl4 = !bl4;
                            } else if (this.__tryExpression(this.__currentOffset)) {
                                bl = true;
                                break block0;
                            }
                            ++this.__currentOffset;
                        }
                        if ((n5 > 0 || !bl4) && this.__tryExpression(this.__currentOffset)) {
                            bl = true;
                            break;
                        }
                        break block82;
                    }
                    case '\u0012': {
                        while (this.__currentOffset < n2) {
                            char c6 = this.__input[this.__currentOffset];
                            if (OpCode._isWordCharacter(c6)) {
                                if (bl4 && this.__tryExpression(this.__currentOffset)) {
                                    bl = true;
                                    break block0;
                                }
                                bl4 = bl2;
                            } else {
                                bl4 = true;
                            }
                            ++this.__currentOffset;
                        }
                        break block82;
                    }
                    case '\u0013': {
                        while (this.__currentOffset < n2) {
                            char c7 = this.__input[this.__currentOffset];
                            if (!OpCode._isWordCharacter(c7)) {
                                if (bl4 && this.__tryExpression(this.__currentOffset)) {
                                    bl = true;
                                    break block0;
                                }
                                bl4 = bl2;
                            } else {
                                bl4 = true;
                            }
                            ++this.__currentOffset;
                        }
                        break block82;
                    }
                    case '\u0016': {
                        while (this.__currentOffset < n2) {
                            if (Character.isWhitespace(this.__input[this.__currentOffset])) {
                                if (bl4 && this.__tryExpression(this.__currentOffset)) {
                                    bl = true;
                                    break block0;
                                }
                                bl4 = bl2;
                            } else {
                                bl4 = true;
                            }
                            ++this.__currentOffset;
                        }
                        break block82;
                    }
                    case '\u0017': {
                        while (this.__currentOffset < n2) {
                            if (!Character.isWhitespace(this.__input[this.__currentOffset])) {
                                if (bl4 && this.__tryExpression(this.__currentOffset)) {
                                    bl = true;
                                    break block0;
                                }
                                bl4 = bl2;
                            } else {
                                bl4 = true;
                            }
                            ++this.__currentOffset;
                        }
                        break block82;
                    }
                    case '\u0018': {
                        while (this.__currentOffset < n2) {
                            if (Character.isDigit(this.__input[this.__currentOffset])) {
                                if (bl4 && this.__tryExpression(this.__currentOffset)) {
                                    bl = true;
                                    break block0;
                                }
                                bl4 = bl2;
                            } else {
                                bl4 = true;
                            }
                            ++this.__currentOffset;
                        }
                        break block82;
                    }
                    case '\u0019': {
                        while (this.__currentOffset < n2) {
                            if (!Character.isDigit(this.__input[this.__currentOffset])) {
                                if (bl4 && this.__tryExpression(this.__currentOffset)) {
                                    bl = true;
                                    break block0;
                                }
                                bl4 = bl2;
                            } else {
                                bl4 = true;
                            }
                            ++this.__currentOffset;
                        }
                        break block0;
                    }
                }
                break block82;
            }
            if (n5 > 0) {
                n4 = n5 - 1;
            }
            n2 -= n4;
            do {
                if (!this.__tryExpression(this.__currentOffset)) continue;
                bl = true;
                break;
            } while (this.__currentOffset++ < n2);
        }
        this.__lastSuccess = bl;
        this.__lastMatchResult = null;
        return bl;
    }

    private boolean __matchUnicodeClass(char c, char[] arrc, int n, char c2) {
        boolean bl;
        boolean bl2 = bl = c2 == '#';
        while (arrc[n] != '\u0000') {
            if (arrc[n] == '%') {
                if (c >= arrc[++n] && c <= arrc[n + 1]) {
                    return bl;
                }
                n += 2;
                continue;
            }
            if (arrc[n] == '1') {
                int n2 = ++n;
                ++n;
                if (arrc[n2] != c) continue;
                return bl;
            }
            bl = arrc[n] == '/' ? bl : !bl;
            int n3 = ++n;
            ++n;
            switch (arrc[n3]) {
                case '\u0012': {
                    if (!OpCode._isWordCharacter(c)) break;
                    return bl;
                }
                case '\u0013': {
                    if (OpCode._isWordCharacter(c)) break;
                    return bl;
                }
                case '\u0016': {
                    if (!Character.isWhitespace(c)) break;
                    return bl;
                }
                case '\u0017': {
                    if (Character.isWhitespace(c)) break;
                    return bl;
                }
                case '\u0018': {
                    if (!Character.isDigit(c)) break;
                    return bl;
                }
                case '\u0019': {
                    if (Character.isDigit(c)) break;
                    return bl;
                }
                case '2': {
                    if (!Character.isLetterOrDigit(c)) break;
                    return bl;
                }
                case '&': {
                    if (!Character.isLetter(c)) break;
                    return bl;
                }
                case '\'': {
                    if (!Character.isSpaceChar(c)) break;
                    return bl;
                }
                case '(': {
                    if (!Character.isISOControl(c)) break;
                    return bl;
                }
                case '*': {
                    if (Character.isLowerCase(c)) {
                        return bl;
                    }
                    if (!this.__caseInsensitive || !Character.isUpperCase(c)) break;
                    return bl;
                }
                case '-': {
                    if (Character.isUpperCase(c)) {
                        return bl;
                    }
                    if (!this.__caseInsensitive || !Character.isLowerCase(c)) break;
                    return bl;
                }
                case '+': {
                    if (Character.isSpaceChar(c)) {
                        return bl;
                    }
                }
                case ')': {
                    if (Character.isLetterOrDigit(c)) {
                        return bl;
                    }
                }
                case ',': {
                    switch (Character.getType(c)) {
                        case 20: 
                        case 21: 
                        case 22: 
                        case 23: 
                        case 24: 
                        case 25: 
                        case 26: 
                        case 27: {
                            return bl;
                        }
                    }
                    break;
                }
                case '.': {
                    if (!(c >= '0' && c <= '9' || c >= 'a' && c <= 'f') && (c < 'A' || c > 'F')) break;
                    return bl;
                }
                case '3': {
                    if (c >= '\u0080') break;
                    return bl;
                }
            }
        }
        return !bl;
    }

    private boolean __tryExpression(int n) {
        this.__inputOffset = n;
        this.__lastParen = 0;
        this.__expSize = 0;
        if (this.__numParentheses > 0) {
            for (int i = 0; i <= this.__numParentheses; ++i) {
                this.__beginMatchOffsets[i] = -1;
                this.__endMatchOffsets[i] = -1;
            }
        }
        if (this.__match(1)) {
            this.__beginMatchOffsets[0] = n;
            this.__endMatchOffsets[0] = this.__inputOffset;
            return true;
        }
        return false;
    }

    private int __repeat(int n, int n2) {
        int n3 = this.__inputOffset;
        int n4 = this.__eol;
        if (n2 != 65535 && n2 < n4 - n3) {
            n4 = n3 + n2;
        }
        int n5 = OpCode._getOperand(n);
        char c = this.__program[n];
        switch (c) {
            case '\u0007': {
                while (n3 < n4 && this.__input[n3] != '\n') {
                    ++n3;
                }
                break;
            }
            case '\b': {
                n3 = n4;
                break;
            }
            case '\u000e': {
                while (n3 < n4 && this.__program[++n5] == this.__input[n3]) {
                    ++n3;
                }
                break;
            }
            case '\t': {
                char c2;
                if (n3 >= n4 || (c2 = this.__input[n3]) >= '\u0100') break;
                while (c2 < '\u0100' && (this.__program[n5 + (c2 >> 4)] & 1 << (c2 & 0xF)) == 0 && ++n3 < n4) {
                    c2 = this.__input[n3];
                }
                break;
            }
            case '#': 
            case '$': {
                if (n3 >= n4) break;
                char c3 = this.__input[n3];
                while (this.__matchUnicodeClass(c3, this.__program, n5, c) && ++n3 < n4) {
                    c3 = this.__input[n3];
                }
                break;
            }
            case '\u0012': {
                while (n3 < n4 && OpCode._isWordCharacter(this.__input[n3])) {
                    ++n3;
                }
                break;
            }
            case '\u0013': {
                while (n3 < n4 && !OpCode._isWordCharacter(this.__input[n3])) {
                    ++n3;
                }
                break;
            }
            case '\u0016': {
                while (n3 < n4 && Character.isWhitespace(this.__input[n3])) {
                    ++n3;
                }
                break;
            }
            case '\u0017': {
                while (n3 < n4 && !Character.isWhitespace(this.__input[n3])) {
                    ++n3;
                }
                break;
            }
            case '\u0018': {
                while (n3 < n4 && Character.isDigit(this.__input[n3])) {
                    ++n3;
                }
                break;
            }
            case '\u0019': {
                while (n3 < n4 && !Character.isDigit(this.__input[n3])) {
                    ++n3;
                }
                break;
            }
        }
        int n6 = n3 - this.__inputOffset;
        this.__inputOffset = n3;
        return n6;
    }

    private boolean __match(int n) {
        boolean bl = true;
        boolean bl2 = false;
        int n2 = this.__inputOffset;
        bl = n2 < this.__endOffset;
        char c = bl ? this.__input[n2] : (char)'\uffff';
        int n3 = n;
        int n4 = this.__program.length;
        while (n3 < n4) {
            int n5 = OpCode._getNext(this.__program, n3);
            char c2 = this.__program[n3];
            switch (c2) {
                case '\u0001': {
                    if (n2 == this.__bol ? this.__previousChar == '\n' : this.__multiline && (bl || n2 < this.__eol) && this.__input[n2 - 1] == '\n') break;
                    return false;
                }
                case '\u0002': {
                    if (n2 == this.__bol ? this.__previousChar == '\n' : (bl || n2 < this.__eol) && this.__input[n2 - 1] == '\n') break;
                    return false;
                }
                case '\u0003': {
                    if (n2 == this.__bol && this.__previousChar == '\n') break;
                    return false;
                }
                case '\u001e': {
                    if (n2 == this.__bol) break;
                    return true;
                }
                case '\u0004': {
                    if ((bl || n2 < this.__eol) && c != '\n') {
                        return false;
                    }
                    if (this.__multiline || this.__eol - n2 <= 1) break;
                    return false;
                }
                case '\u0005': {
                    if (!bl && n2 >= this.__eol || c == 10) break;
                    return false;
                }
                case '\u0006': {
                    if ((bl || n2 < this.__eol) && c != '\n') {
                        return false;
                    }
                    if (this.__eol - n2 <= 1) break;
                    return false;
                }
                case '\b': {
                    if (!bl && n2 >= this.__eol) {
                        return false;
                    }
                    bl = ++n2 < this.__endOffset;
                    c = bl ? this.__input[n2] : (char)'\uffff';
                    break;
                }
                case '\u0007': {
                    if (!bl && n2 >= this.__eol || c == '\n') {
                        return false;
                    }
                    bl = ++n2 < this.__endOffset;
                    c = bl ? this.__input[n2] : (char)'\uffff';
                    break;
                }
                case '\u000e': {
                    int n6 = OpCode._getOperand(n3);
                    int n7 = this.__program[n6++];
                    if (this.__program[n6] != c) {
                        return false;
                    }
                    if (this.__eol - n2 < n7) {
                        return false;
                    }
                    if (n7 > 1 && !Perl5Matcher.__compare(this.__program, n6, this.__input, n2, n7)) {
                        return false;
                    }
                    bl = (n2 += n7) < this.__endOffset;
                    c = bl ? this.__input[n2] : (char)'\uffff';
                    break;
                }
                case '\t': {
                    int n6 = OpCode._getOperand(n3);
                    if (c == '\uffff' && bl) {
                        c = this.__input[n2];
                    }
                    if (c >= '\u0100' || (this.__program[n6 + (c >> 4)] & 1 << (c & 0xF)) != 0) {
                        return false;
                    }
                    if (!bl && n2 >= this.__eol) {
                        return false;
                    }
                    bl = ++n2 < this.__endOffset;
                    c = bl ? this.__input[n2] : (char)'\uffff';
                    break;
                }
                case '#': 
                case '$': {
                    int n6 = OpCode._getOperand(n3);
                    if (c == '\uffff' && bl) {
                        c = this.__input[n2];
                    }
                    if (!this.__matchUnicodeClass(c, this.__program, n6, c2)) {
                        return false;
                    }
                    if (!bl && n2 >= this.__eol) {
                        return false;
                    }
                    bl = ++n2 < this.__endOffset;
                    c = bl ? this.__input[n2] : (char)'\uffff';
                    break;
                }
                case '\u0012': {
                    if (!bl) {
                        return false;
                    }
                    if (!OpCode._isWordCharacter(c)) {
                        return false;
                    }
                    bl = ++n2 < this.__endOffset;
                    c = bl ? this.__input[n2] : (char)'\uffff';
                    break;
                }
                case '\u0013': {
                    if (!bl && n2 >= this.__eol) {
                        return false;
                    }
                    if (OpCode._isWordCharacter(c)) {
                        return false;
                    }
                    bl = ++n2 < this.__endOffset;
                    c = bl ? this.__input[n2] : (char)'\uffff';
                    break;
                }
                case '\u0014': 
                case '\u0015': {
                    boolean bl3 = n2 == this.__bol ? OpCode._isWordCharacter(this.__previousChar) : OpCode._isWordCharacter(this.__input[n2 - 1]);
                    boolean bl4 = OpCode._isWordCharacter(c);
                    if (bl3 == bl4 != (this.__program[n3] == '\u0014')) break;
                    return false;
                }
                case '\u0016': {
                    if (!bl && n2 >= this.__eol) {
                        return false;
                    }
                    if (!Character.isWhitespace(c)) {
                        return false;
                    }
                    bl = ++n2 < this.__endOffset;
                    c = bl ? this.__input[n2] : (char)'\uffff';
                    break;
                }
                case '\u0017': {
                    if (!bl) {
                        return false;
                    }
                    if (Character.isWhitespace(c)) {
                        return false;
                    }
                    bl = ++n2 < this.__endOffset;
                    c = bl ? this.__input[n2] : (char)'\uffff';
                    break;
                }
                case '\u0018': {
                    if (!Character.isDigit(c)) {
                        return false;
                    }
                    bl = ++n2 < this.__endOffset;
                    c = bl ? this.__input[n2] : (char)'\uffff';
                    break;
                }
                case '\u0019': {
                    if (!bl && n2 >= this.__eol) {
                        return false;
                    }
                    if (Character.isDigit(c)) {
                        return false;
                    }
                    bl = ++n2 < this.__endOffset;
                    c = bl ? this.__input[n2] : (char)'\uffff';
                    break;
                }
                case '\u001a': {
                    int n8 = OpCode._getArg1(this.__program, n3);
                    int n6 = this.__beginMatchOffsets[n8];
                    if (n6 == -1) {
                        return false;
                    }
                    if (this.__endMatchOffsets[n8] == -1) {
                        return false;
                    }
                    if (n6 == this.__endMatchOffsets[n8]) break;
                    if (this.__input[n6] != c) {
                        return false;
                    }
                    int n7 = this.__endMatchOffsets[n8] - n6;
                    if (n2 + n7 > this.__eol) {
                        return false;
                    }
                    if (n7 > 1 && !Perl5Matcher.__compare(this.__input, n6, this.__input, n2, n7)) {
                        return false;
                    }
                    bl = (n2 += n7) < this.__endOffset;
                    c = bl ? this.__input[n2] : (char)'\uffff';
                    break;
                }
                case '\u000f': {
                    break;
                }
                case '\r': {
                    break;
                }
                case '\u001b': {
                    int n8 = OpCode._getArg1(this.__program, n3);
                    this.__beginMatchOffsets[n8] = n2;
                    if (n8 <= this.__expSize) break;
                    this.__expSize = n8;
                    break;
                }
                case '\u001c': {
                    int n8 = OpCode._getArg1(this.__program, n3);
                    this.__endMatchOffsets[n8] = n2;
                    if (n8 <= this.__lastParen) break;
                    this.__lastParen = n8;
                    break;
                }
                case '\u000b': {
                    Perl5Repetition perl5Repetition = new Perl5Repetition();
                    perl5Repetition._lastRepetition = this.__currentRep;
                    this.__currentRep = perl5Repetition;
                    perl5Repetition._parenFloor = this.__lastParen;
                    perl5Repetition._numInstances = -1;
                    perl5Repetition._min = OpCode._getArg1(this.__program, n3);
                    perl5Repetition._max = OpCode._getArg2(this.__program, n3);
                    perl5Repetition._scan = OpCode._getNextOperator(n3) + 2;
                    perl5Repetition._next = n5;
                    perl5Repetition._minMod = bl2;
                    perl5Repetition._lastLocation = -1;
                    this.__inputOffset = n2;
                    bl2 = this.__match(OpCode._getPrevOperator(n5));
                    this.__currentRep = perl5Repetition._lastRepetition;
                    return bl2;
                }
                case '\"': {
                    int n7;
                    Perl5Repetition perl5Repetition = this.__currentRep;
                    int n8 = perl5Repetition._numInstances + 1;
                    this.__inputOffset = n2;
                    if (n2 == perl5Repetition._lastLocation) {
                        this.__currentRep = perl5Repetition._lastRepetition;
                        n7 = this.__currentRep._numInstances;
                        if (this.__match(perl5Repetition._next)) {
                            return true;
                        }
                        this.__currentRep._numInstances = n7;
                        this.__currentRep = perl5Repetition;
                        return false;
                    }
                    if (n8 < perl5Repetition._min) {
                        perl5Repetition._numInstances = n8;
                        perl5Repetition._lastLocation = n2;
                        if (this.__match(perl5Repetition._scan)) {
                            return true;
                        }
                        perl5Repetition._numInstances = n8 - 1;
                        return false;
                    }
                    if (perl5Repetition._minMod) {
                        this.__currentRep = perl5Repetition._lastRepetition;
                        n7 = this.__currentRep._numInstances;
                        if (this.__match(perl5Repetition._next)) {
                            return true;
                        }
                        this.__currentRep._numInstances = n7;
                        this.__currentRep = perl5Repetition;
                        if (n8 >= perl5Repetition._max) {
                            return false;
                        }
                        this.__inputOffset = n2;
                        perl5Repetition._numInstances = n8;
                        perl5Repetition._lastLocation = n2;
                        if (this.__match(perl5Repetition._scan)) {
                            return true;
                        }
                        perl5Repetition._numInstances = n8 - 1;
                        return false;
                    }
                    if (n8 < perl5Repetition._max) {
                        this.__pushState(perl5Repetition._parenFloor);
                        perl5Repetition._numInstances = n8;
                        perl5Repetition._lastLocation = n2;
                        if (this.__match(perl5Repetition._scan)) {
                            return true;
                        }
                        this.__popState();
                        this.__inputOffset = n2;
                    }
                    this.__currentRep = perl5Repetition._lastRepetition;
                    n7 = this.__currentRep._numInstances;
                    if (this.__match(perl5Repetition._next)) {
                        return true;
                    }
                    perl5Repetition._numInstances = n7;
                    this.__currentRep = perl5Repetition;
                    perl5Repetition._numInstances = n8 - 1;
                    return false;
                }
                case '\f': {
                    int n8;
                    if (this.__program[n5] != '\f') {
                        n5 = OpCode._getNextOperator(n3);
                        break;
                    }
                    int n9 = this.__lastParen;
                    do {
                        this.__inputOffset = n2;
                        if (this.__match(OpCode._getNextOperator(n3))) {
                            return true;
                        }
                        for (n8 = this.__lastParen; n8 > n9; --n8) {
                            this.__endMatchOffsets[n8] = -1;
                        }
                        this.__lastParen = n8;
                    } while ((n3 = OpCode._getNext(this.__program, n3)) != -1 && this.__program[n3] == '\f');
                    return false;
                }
                case '\u001d': {
                    bl2 = true;
                    break;
                }
                case '\n': 
                case '\u0010': 
                case '\u0011': {
                    int n8;
                    int n7;
                    int n6;
                    if (c2 == '\n') {
                        n7 = OpCode._getArg1(this.__program, n3);
                        n8 = OpCode._getArg2(this.__program, n3);
                        n3 = OpCode._getNextOperator(n3) + 2;
                    } else if (c2 == '\u0010') {
                        n7 = 0;
                        n8 = 65535;
                        n3 = OpCode._getNextOperator(n3);
                    } else {
                        n7 = 1;
                        n8 = 65535;
                        n3 = OpCode._getNextOperator(n3);
                    }
                    if (this.__program[n5] == '\u000e') {
                        c = this.__program[OpCode._getOperand(n5) + 1];
                        n6 = 0;
                    } else {
                        c = '\uffff';
                        n6 = -1000;
                    }
                    this.__inputOffset = n2;
                    if (bl2) {
                        bl2 = false;
                        if (n7 > 0 && this.__repeat(n3, n7) < n7) {
                            return false;
                        }
                        while (n8 >= n7 || n8 == 65535 && n7 > 0) {
                            if ((n6 == -1000 || this.__inputOffset >= this.__endOffset || this.__input[this.__inputOffset] == c) && this.__match(n5)) {
                                return true;
                            }
                            this.__inputOffset = n2 + n7;
                            if (this.__repeat(n3, 1) != 0) {
                                this.__inputOffset = n2 + ++n7;
                                continue;
                            }
                            return false;
                        }
                    } else {
                        if (n7 < (n8 = this.__repeat(n3, n8)) && OpCode._opType[this.__program[n5]] == '\u0004' && (!this.__multiline && this.__program[n5] != '\u0005' || this.__program[n5] == '\u0006')) {
                            n7 = n8;
                        }
                        while (n8 >= n7) {
                            if ((n6 == -1000 || this.__inputOffset >= this.__endOffset || this.__input[this.__inputOffset] == c) && this.__match(n5)) {
                                return true;
                            }
                            this.__inputOffset = n2 + --n8;
                        }
                    }
                    return false;
                }
                case '\u0000': 
                case '!': {
                    this.__inputOffset = n2;
                    return this.__inputOffset != this.__lastMatchInputEndOffset;
                }
                case '\u001f': {
                    this.__inputOffset = n2;
                    n3 = OpCode._getNextOperator(n3);
                    if (this.__match(n3)) break;
                    return false;
                }
                case ' ': {
                    this.__inputOffset = n2;
                    n3 = OpCode._getNextOperator(n3);
                    if (!this.__match(n3)) break;
                    return false;
                }
            }
            n3 = n5;
        }
        return false;
    }

    public void setMultiline(boolean bl) {
        this.__multiline = bl;
    }

    public boolean isMultiline() {
        return this.__multiline;
    }

    char[] _toLower(char[] arrc) {
        char[] arrc2 = new char[arrc.length];
        System.arraycopy(arrc, 0, arrc2, 0, arrc.length);
        arrc = arrc2;
        for (int i = 0; i < arrc.length; ++i) {
            if (!Character.isUpperCase(arrc[i])) continue;
            arrc[i] = Character.toLowerCase(arrc[i]);
        }
        return arrc;
    }

    public boolean matchesPrefix(char[] arrc, Pattern pattern, int n) {
        Perl5Pattern perl5Pattern = (Perl5Pattern)pattern;
        this.__originalInput = arrc;
        if (perl5Pattern._isCaseInsensitive) {
            arrc = this._toLower(arrc);
        }
        this.__initInterpreterGlobals(perl5Pattern, arrc, 0, arrc.length, n);
        this.__lastSuccess = this.__tryExpression(n);
        this.__lastMatchResult = null;
        return this.__lastSuccess;
    }

    public boolean matchesPrefix(char[] arrc, Pattern pattern) {
        return this.matchesPrefix(arrc, pattern, 0);
    }

    public boolean matchesPrefix(String string, Pattern pattern) {
        return this.matchesPrefix(string.toCharArray(), pattern, 0);
    }

    public boolean matchesPrefix(PatternMatcherInput patternMatcherInput, Pattern pattern) {
        char[] arrc;
        Perl5Pattern perl5Pattern = (Perl5Pattern)pattern;
        this.__originalInput = patternMatcherInput._originalBuffer;
        if (perl5Pattern._isCaseInsensitive) {
            if (patternMatcherInput._toLowerBuffer == null) {
                patternMatcherInput._toLowerBuffer = this._toLower(this.__originalInput);
            }
            arrc = patternMatcherInput._toLowerBuffer;
        } else {
            arrc = this.__originalInput;
        }
        this.__initInterpreterGlobals(perl5Pattern, arrc, patternMatcherInput._beginOffset, patternMatcherInput._endOffset, patternMatcherInput._currentOffset);
        this.__lastSuccess = this.__tryExpression(patternMatcherInput._currentOffset);
        this.__lastMatchResult = null;
        return this.__lastSuccess;
    }

    public boolean matches(char[] arrc, Pattern pattern) {
        Perl5Pattern perl5Pattern = (Perl5Pattern)pattern;
        this.__originalInput = arrc;
        if (perl5Pattern._isCaseInsensitive) {
            arrc = this._toLower(arrc);
        }
        this.__initInterpreterGlobals(perl5Pattern, arrc, 0, arrc.length, 0);
        this.__lastSuccess = this.__tryExpression(0) && this.__endMatchOffsets[0] == arrc.length;
        this.__lastMatchResult = null;
        return this.__lastSuccess;
    }

    public boolean matches(String string, Pattern pattern) {
        return this.matches(string.toCharArray(), pattern);
    }

    public boolean matches(PatternMatcherInput patternMatcherInput, Pattern pattern) {
        char[] arrc;
        Perl5Pattern perl5Pattern = (Perl5Pattern)pattern;
        this.__originalInput = patternMatcherInput._originalBuffer;
        if (perl5Pattern._isCaseInsensitive) {
            if (patternMatcherInput._toLowerBuffer == null) {
                patternMatcherInput._toLowerBuffer = this._toLower(this.__originalInput);
            }
            arrc = patternMatcherInput._toLowerBuffer;
        } else {
            arrc = this.__originalInput;
        }
        this.__initInterpreterGlobals(perl5Pattern, arrc, patternMatcherInput._beginOffset, patternMatcherInput._endOffset, patternMatcherInput._beginOffset);
        this.__lastMatchResult = null;
        if (this.__tryExpression(patternMatcherInput._beginOffset) && (this.__endMatchOffsets[0] == patternMatcherInput._endOffset || patternMatcherInput.length() == 0 || patternMatcherInput._beginOffset == patternMatcherInput._endOffset)) {
            this.__lastSuccess = true;
            return true;
        }
        this.__lastSuccess = false;
        return false;
    }

    public boolean contains(String string, Pattern pattern) {
        return this.contains(string.toCharArray(), pattern);
    }

    public boolean contains(char[] arrc, Pattern pattern) {
        Perl5Pattern perl5Pattern = (Perl5Pattern)pattern;
        this.__originalInput = arrc;
        if (perl5Pattern._isCaseInsensitive) {
            arrc = this._toLower(arrc);
        }
        return this.__interpret(perl5Pattern, arrc, 0, arrc.length, 0);
    }

    public boolean contains(PatternMatcherInput patternMatcherInput, Pattern pattern) {
        char[] arrc;
        if (patternMatcherInput._currentOffset > patternMatcherInput._endOffset) {
            return false;
        }
        Perl5Pattern perl5Pattern = (Perl5Pattern)pattern;
        this.__originalInput = patternMatcherInput._originalBuffer;
        this.__originalInput = patternMatcherInput._originalBuffer;
        if (perl5Pattern._isCaseInsensitive) {
            if (patternMatcherInput._toLowerBuffer == null) {
                patternMatcherInput._toLowerBuffer = this._toLower(this.__originalInput);
            }
            arrc = patternMatcherInput._toLowerBuffer;
        } else {
            arrc = this.__originalInput;
        }
        this.__lastMatchInputEndOffset = patternMatcherInput.getMatchEndOffset();
        boolean bl = this.__interpret(perl5Pattern, arrc, patternMatcherInput._beginOffset, patternMatcherInput._endOffset, patternMatcherInput._currentOffset);
        if (bl) {
            patternMatcherInput.setCurrentOffset(this.__endMatchOffsets[0]);
            patternMatcherInput.setMatchOffsets(this.__beginMatchOffsets[0], this.__endMatchOffsets[0]);
        } else {
            patternMatcherInput.setCurrentOffset(patternMatcherInput._endOffset + 1);
        }
        this.__lastMatchInputEndOffset = -100;
        return bl;
    }

    public MatchResult getMatch() {
        if (!this.__lastSuccess) {
            return null;
        }
        if (this.__lastMatchResult == null) {
            this.__setLastMatchResult();
        }
        return this.__lastMatchResult;
    }
}

