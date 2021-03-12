/*
 * Decompiled with CFR 0.150.
 */
package org.apache.oro.text.regex;

import java.util.Hashtable;
import org.apache.oro.text.regex.CharStringPointer;
import org.apache.oro.text.regex.MalformedPatternException;
import org.apache.oro.text.regex.OpCode;
import org.apache.oro.text.regex.Pattern;
import org.apache.oro.text.regex.PatternCompiler;
import org.apache.oro.text.regex.Perl5Pattern;

public final class Perl5Compiler
implements PatternCompiler {
    private static final int __WORSTCASE = 0;
    private static final int __NONNULL = 1;
    private static final int __SIMPLE = 2;
    private static final int __SPSTART = 4;
    private static final int __TRYAGAIN = 8;
    private static final char __CASE_INSENSITIVE = '\u0001';
    private static final char __GLOBAL = '\u0002';
    private static final char __KEEP = '\u0004';
    private static final char __MULTILINE = '\b';
    private static final char __SINGLELINE = '\u0010';
    private static final char __EXTENDED = ' ';
    private static final char __READ_ONLY = '\u8000';
    private static final String __HEX_DIGIT = "0123456789abcdef0123456789ABCDEFx";
    private CharStringPointer __input;
    private boolean __sawBackreference;
    private char[] __modifierFlags = new char[]{'\u0000'};
    private int __numParentheses;
    private int __programSize;
    private int __cost;
    private char[] __program;
    private static final Hashtable __hashPOSIX = new Hashtable();
    public static final int DEFAULT_MASK = 0;
    public static final int CASE_INSENSITIVE_MASK = 1;
    public static final int MULTILINE_MASK = 8;
    public static final int SINGLELINE_MASK = 16;
    public static final int EXTENDED_MASK = 32;
    public static final int READ_ONLY_MASK = 32768;

    public static final String quotemeta(char[] arrc) {
        StringBuffer stringBuffer = new StringBuffer(2 * arrc.length);
        for (int i = 0; i < arrc.length; ++i) {
            if (!OpCode._isWordCharacter(arrc[i])) {
                stringBuffer.append('\\');
            }
            stringBuffer.append(arrc[i]);
        }
        return stringBuffer.toString();
    }

    public static final String quotemeta(String string) {
        return Perl5Compiler.quotemeta(string.toCharArray());
    }

    private static boolean __isSimpleRepetitionOp(char c) {
        return c == '*' || c == '+' || c == '?';
    }

    private static boolean __isComplexRepetitionOp(char[] arrc, int n) {
        if (n < arrc.length && n >= 0) {
            return arrc[n] == '*' || arrc[n] == '+' || arrc[n] == '?' || arrc[n] == '{' && Perl5Compiler.__parseRepetition(arrc, n);
        }
        return false;
    }

    private static boolean __parseRepetition(char[] arrc, int n) {
        if (arrc[n] != '{') {
            return false;
        }
        if (++n >= arrc.length || !Character.isDigit(arrc[n])) {
            return false;
        }
        while (n < arrc.length && Character.isDigit(arrc[n])) {
            ++n;
        }
        if (n < arrc.length && arrc[n] == ',') {
            ++n;
        }
        while (n < arrc.length && Character.isDigit(arrc[n])) {
            ++n;
        }
        return n < arrc.length && arrc[n] == '}';
    }

    private static int __parseHex(char[] arrc, int n, int n2, int[] arrn) {
        int n3;
        int n4 = 0;
        arrn[0] = 0;
        while (n < arrc.length && n2-- > 0 && (n3 = __HEX_DIGIT.indexOf(arrc[n])) != -1) {
            n4 <<= 4;
            n4 |= n3 & 0xF;
            ++n;
            arrn[0] = arrn[0] + 1;
        }
        return n4;
    }

    private static int __parseOctal(char[] arrc, int n, int n2, int[] arrn) {
        int n3 = 0;
        arrn[0] = 0;
        while (n < arrc.length && n2 > 0 && arrc[n] >= '0' && arrc[n] <= '7') {
            n3 <<= 3;
            n3 |= arrc[n] - 48;
            --n2;
            ++n;
            arrn[0] = arrn[0] + 1;
        }
        return n3;
    }

    private static void __setModifierFlag(char[] arrc, char c) {
        switch (c) {
            case 'i': {
                arrc[0] = (char)(arrc[0] | '\u0001');
                return;
            }
            case 'g': {
                arrc[0] = (char)(arrc[0] | 2);
                return;
            }
            case 'o': {
                arrc[0] = (char)(arrc[0] | 4);
                return;
            }
            case 'm': {
                arrc[0] = (char)(arrc[0] | 8);
                return;
            }
            case 's': {
                arrc[0] = (char)(arrc[0] | 0x10);
                return;
            }
            case 'x': {
                arrc[0] = (char)(arrc[0] | 0x20);
                return;
            }
        }
    }

    private void __emitCode(char c) {
        if (this.__program != null) {
            this.__program[this.__programSize] = c;
        }
        ++this.__programSize;
    }

    private int __emitNode(char c) {
        int n = this.__programSize;
        if (this.__program == null) {
            this.__programSize += 2;
        } else {
            this.__program[this.__programSize++] = c;
            this.__program[this.__programSize++] = '\u0000';
        }
        return n;
    }

    private int __emitArgNode(char c, char c2) {
        int n = this.__programSize;
        if (this.__program == null) {
            this.__programSize += 3;
        } else {
            this.__program[this.__programSize++] = c;
            this.__program[this.__programSize++] = '\u0000';
            this.__program[this.__programSize++] = c2;
        }
        return n;
    }

    private void __programInsertOperator(char c, int n) {
        int n2;
        int n3 = n2 = OpCode._opType[c] == '\n' ? 2 : 0;
        if (this.__program == null) {
            this.__programSize += 2 + n2;
            return;
        }
        int n4 = this.__programSize;
        this.__programSize += 2 + n2;
        int n5 = this.__programSize;
        while (n4 > n) {
            this.__program[--n5] = this.__program[--n4];
        }
        this.__program[n++] = c;
        this.__program[n++] = '\u0000';
        while (n2-- > 0) {
            this.__program[n++] = '\u0000';
        }
    }

    private void __programAddTail(int n, int n2) {
        int n3;
        if (this.__program == null || n == -1) {
            return;
        }
        int n4 = n;
        while ((n3 = OpCode._getNext(this.__program, n4)) != -1) {
            n4 = n3;
        }
        int n5 = this.__program[n4] == '\r' ? n4 - n2 : n2 - n4;
        this.__program[n4 + 1] = (char)n5;
    }

    private void __programAddOperatorTail(int n, int n2) {
        if (this.__program == null || n == -1 || OpCode._opType[this.__program[n]] != '\f') {
            return;
        }
        this.__programAddTail(OpCode._getNextOperator(n), n2);
    }

    private char __getNextChar() {
        char c = this.__input._postIncrement();
        while (true) {
            char c2;
            if ((c2 = this.__input._getValue()) == '(' && this.__input._getValueRelative(1) == '?' && this.__input._getValueRelative(2) == '#') {
                while (c2 != '\uffff' && c2 != ')') {
                    c2 = this.__input._increment();
                }
                this.__input._increment();
                continue;
            }
            if ((this.__modifierFlags[0] & 0x20) == 0) break;
            if (Character.isWhitespace(c2)) {
                this.__input._increment();
                continue;
            }
            if (c2 != '#') break;
            while (c2 != '\uffff' && c2 != '\n') {
                c2 = this.__input._increment();
            }
            this.__input._increment();
        }
        return c;
    }

    private int __parseAlternation(int[] arrn) throws MalformedPatternException {
        int n = 0;
        arrn[0] = 0;
        int n2 = this.__emitNode('\f');
        int n3 = -1;
        if (this.__input._getOffset() == 0) {
            this.__input._setOffset(-1);
            this.__getNextChar();
        } else {
            this.__input._decrement();
            this.__getNextChar();
        }
        char c = this.__input._getValue();
        while (c != '\uffff' && c != '|' && c != ')') {
            n &= 0xFFFFFFF7;
            int n4 = this.__parseBranch(arrn);
            if (n4 == -1) {
                if ((n & 8) != 0) {
                    c = this.__input._getValue();
                    continue;
                }
                return -1;
            }
            arrn[0] = arrn[0] | n & 1;
            if (n3 == -1) {
                arrn[0] = arrn[0] | n & 4;
            } else {
                ++this.__cost;
                this.__programAddTail(n3, n4);
            }
            n3 = n4;
            c = this.__input._getValue();
        }
        if (n3 == -1) {
            this.__emitNode('\u000f');
        }
        return n2;
    }

    /*
     * Unable to fully structure code
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     * Lifted jumps to return sites
     */
    private int __parseAtom(int[] var1_1) throws MalformedPatternException {
        var5_2 = new int[]{0};
        var1_1[0] = 0;
        var2_3 = false;
        var4_4 = -1;
        block50: while (true) {
            var3_5 = this.__input._getValue();
            switch (var3_5) {
                case '^': {
                    this.__getNextChar();
                    if ((this.__modifierFlags[0] & 8) != 0) {
                        var4_4 = this.__emitNode('\u0002');
                        break block50;
                    }
                    if ((this.__modifierFlags[0] & 16) != 0) {
                        var4_4 = this.__emitNode('\u0003');
                        break block50;
                    }
                    var4_4 = this.__emitNode('\u0001');
                    break block50;
                }
                case '$': {
                    this.__getNextChar();
                    if ((this.__modifierFlags[0] & 8) != 0) {
                        var4_4 = this.__emitNode('\u0005');
                        break block50;
                    }
                    if ((this.__modifierFlags[0] & 16) != 0) {
                        var4_4 = this.__emitNode('\u0006');
                        break block50;
                    }
                    var4_4 = this.__emitNode('\u0004');
                    break block50;
                }
                case '.': {
                    this.__getNextChar();
                    var4_4 = (this.__modifierFlags[0] & 16) != 0 ? this.__emitNode('\b') : this.__emitNode('\u0007');
                    ++this.__cost;
                    var1_1[0] = var1_1[0] | 3;
                    break block50;
                }
                case '[': {
                    this.__input._increment();
                    var4_4 = this.__parseUnicodeClass();
                    var1_1[0] = var1_1[0] | 3;
                    break block50;
                }
                case '(': {
                    this.__getNextChar();
                    var4_4 = this.__parseExpression(true, var5_2);
                    if (var4_4 == -1) {
                        if ((var5_2[0] & 8) == 0) return -1;
                        continue block50;
                    }
                    var1_1[0] = var1_1[0] | var5_2[0] & 5;
                    break block50;
                }
                case ')': 
                case '|': {
                    if ((var5_2[0] & 8) == 0) throw new MalformedPatternException("Error in expression at " + this.__input._toString(this.__input._getOffset()));
                    var1_1[0] = var1_1[0] | 8;
                    return -1;
                }
                case '*': 
                case '+': 
                case '?': {
                    throw new MalformedPatternException("?+* follows nothing in expression");
                }
                case '\\': {
                    var3_5 = this.__input._increment();
                    switch (var3_5) {
                        case 'A': {
                            var4_4 = this.__emitNode('\u0003');
                            var1_1[0] = var1_1[0] | 2;
                            this.__getNextChar();
                            break block50;
                        }
                        case 'G': {
                            var4_4 = this.__emitNode('\u001e');
                            var1_1[0] = var1_1[0] | 2;
                            this.__getNextChar();
                            break block50;
                        }
                        case 'Z': {
                            var4_4 = this.__emitNode('\u0006');
                            var1_1[0] = var1_1[0] | 2;
                            this.__getNextChar();
                            break block50;
                        }
                        case 'w': {
                            var4_4 = this.__emitNode('\u0012');
                            var1_1[0] = var1_1[0] | 3;
                            this.__getNextChar();
                            break block50;
                        }
                        case 'W': {
                            var4_4 = this.__emitNode('\u0013');
                            var1_1[0] = var1_1[0] | 3;
                            this.__getNextChar();
                            break block50;
                        }
                        case 'b': {
                            var4_4 = this.__emitNode('\u0014');
                            var1_1[0] = var1_1[0] | 2;
                            this.__getNextChar();
                            break block50;
                        }
                        case 'B': {
                            var4_4 = this.__emitNode('\u0015');
                            var1_1[0] = var1_1[0] | 2;
                            this.__getNextChar();
                            break block50;
                        }
                        case 's': {
                            var4_4 = this.__emitNode('\u0016');
                            var1_1[0] = var1_1[0] | 3;
                            this.__getNextChar();
                            break block50;
                        }
                        case 'S': {
                            var4_4 = this.__emitNode('\u0017');
                            var1_1[0] = var1_1[0] | 3;
                            this.__getNextChar();
                            break block50;
                        }
                        case 'd': {
                            var4_4 = this.__emitNode('\u0018');
                            var1_1[0] = var1_1[0] | 3;
                            this.__getNextChar();
                            break block50;
                        }
                        case 'D': {
                            var4_4 = this.__emitNode('\u0019');
                            var1_1[0] = var1_1[0] | 3;
                            this.__getNextChar();
                            break block50;
                        }
                        case '0': 
                        case 'a': 
                        case 'c': 
                        case 'e': 
                        case 'f': 
                        case 'n': 
                        case 'r': 
                        case 't': 
                        case 'x': {
                            var2_3 = true;
                            break block50;
                        }
                        case '1': 
                        case '2': 
                        case '3': 
                        case '4': 
                        case '5': 
                        case '6': 
                        case '7': 
                        case '8': 
                        case '9': {
                            var7_6 = new StringBuffer(10);
                            var6_8 = '\u0000';
                            var3_5 = this.__input._getValueRelative(var6_8);
                            while (Character.isDigit(var3_5)) {
                                var7_6.append(var3_5);
                                var3_5 = this.__input._getValueRelative(++var6_8);
                            }
                            try {
                                var6_8 = Integer.parseInt(var7_6.toString());
                            }
                            catch (NumberFormatException var8_9) {
                                throw new MalformedPatternException("Unexpected number format exception.  Please report this bug.NumberFormatException message: " + var8_9.getMessage());
                            }
                            if (var6_8 > '\t' && var6_8 >= this.__numParentheses) {
                                var2_3 = true;
                                break block50;
                            }
                            if (var6_8 >= this.__numParentheses) {
                                throw new MalformedPatternException("Invalid backreference: \\" + var6_8);
                            }
                            this.__sawBackreference = true;
                            var4_4 = this.__emitArgNode('\u001a', var6_8);
                            var1_1[0] = var1_1[0] | 1;
                            var3_5 = this.__input._getValue();
                            while (Character.isDigit(var3_5)) {
                                var3_5 = this.__input._increment();
                            }
                            this.__input._decrement();
                            this.__getNextChar();
                            break block50;
                        }
                        case '\u0000': 
                        case '\uffff': {
                            if (!this.__input._isAtEnd()) break;
                            throw new MalformedPatternException("Trailing \\ in expression.");
                        }
                    }
                    var2_3 = true;
                    break block50;
                }
                case '#': {
                    if ((this.__modifierFlags[0] & 32) != 0) {
                        while (!this.__input._isAtEnd() && this.__input._getValue() != '\n') {
                            this.__input._increment();
                        }
                        if (this.__input._isAtEnd()) ** break;
                        continue block50;
                    }
                }
                default: {
                    this.__input._increment();
                    var2_3 = true;
                    break block50;
                }
            }
            break;
        }
        if (var2_3 == false) return var4_4;
        var4_4 = this.__emitNode('\u000e');
        this.__emitCode('\uffff');
        var8_10 = this.__input._getOffset() - 1;
        var9_11 = this.__input._getLength();
        block54: for (var7_7 = 0; var7_7 < 127 && var8_10 < var9_11; ++var7_7) {
            var10_12 = var8_10;
            var3_5 = this.__input._getValue(var8_10);
            switch (var3_5) {
                case '$': 
                case '(': 
                case ')': 
                case '.': 
                case '[': 
                case '^': 
                case '|': {
                    break block54;
                }
                case '\\': {
                    var3_5 = this.__input._getValue(++var8_10);
                    switch (var3_5) {
                        case 'A': 
                        case 'B': 
                        case 'D': 
                        case 'G': 
                        case 'S': 
                        case 'W': 
                        case 'Z': 
                        case 'b': 
                        case 'd': 
                        case 's': 
                        case 'w': {
                            --var8_10;
                            break block54;
                        }
                        case 'n': {
                            var6_8 = '\n';
                            ++var8_10;
                            break;
                        }
                        case 'r': {
                            var6_8 = '\r';
                            ++var8_10;
                            break;
                        }
                        case 't': {
                            var6_8 = '\t';
                            ++var8_10;
                            break;
                        }
                        case 'f': {
                            var6_8 = '\f';
                            ++var8_10;
                            break;
                        }
                        case 'e': {
                            var6_8 = '\u001b';
                            ++var8_10;
                            break;
                        }
                        case 'a': {
                            var6_8 = '\u0007';
                            ++var8_10;
                            break;
                        }
                        case 'x': {
                            var11_13 = new int[1];
                            var6_8 = (char)Perl5Compiler.__parseHex(this.__input._array, ++var8_10, 2, var11_13);
                            var8_10 += var11_13[0];
                            break;
                        }
                        case 'c': {
                            v0 = ++var8_10;
                            ++var8_10;
                            var6_8 = this.__input._getValue(v0);
                            if (Character.isLowerCase(var6_8)) {
                                var6_8 = Character.toUpperCase(var6_8);
                            }
                            var6_8 = (char)(var6_8 ^ 64);
                            break;
                        }
                        case '0': 
                        case '1': 
                        case '2': 
                        case '3': 
                        case '4': 
                        case '5': 
                        case '6': 
                        case '7': 
                        case '8': 
                        case '9': {
                            var12_14 = false;
                            var3_5 = this.__input._getValue(var8_10);
                            if (var3_5 == '0') {
                                var12_14 = true;
                            }
                            if (Character.isDigit(var3_5 = this.__input._getValue(var8_10 + 1))) {
                                var14_16 = new StringBuffer(10);
                                var13_15 = var8_10;
                                var3_5 = this.__input._getValue(var13_15);
                                while (Character.isDigit(var3_5)) {
                                    var14_16.append(var3_5);
                                    var3_5 = this.__input._getValue(++var13_15);
                                }
                                try {
                                    var13_15 = Integer.parseInt(var14_16.toString());
                                }
                                catch (NumberFormatException var15_17) {
                                    throw new MalformedPatternException("Unexpected number format exception.  Please report this bug.NumberFormatException message: " + var15_17.getMessage());
                                }
                                if (!var12_14) {
                                    v1 = var12_14 = var13_15 >= this.__numParentheses;
                                }
                            }
                            if (var12_14) {
                                var11_13 = new int[1];
                                var6_8 = (char)Perl5Compiler.__parseOctal(this.__input._array, var8_10, 3, var11_13);
                                var8_10 += var11_13[0];
                                break;
                            }
                            --var8_10;
                            break block54;
                        }
                        case '\u0000': 
                        case '\uffff': {
                            if (var8_10 >= var9_11) {
                                throw new MalformedPatternException("Trailing \\ in expression.");
                            }
                        }
                        default: {
                            var6_8 = this.__input._getValue(var8_10++);
                            break;
                        }
                    }
                    break;
                }
                case '#': {
                    if ((this.__modifierFlags[0] & 32) != 0) {
                        while (var8_10 < var9_11 && this.__input._getValue(var8_10) != '\n') {
                            ++var8_10;
                        }
                    }
                }
                case '\t': 
                case '\n': 
                case '\u000b': 
                case '\f': 
                case '\r': 
                case ' ': {
                    if ((this.__modifierFlags[0] & 32) != 0) {
                        ++var8_10;
                        --var7_7;
                        continue block54;
                    }
                }
                default: {
                    var6_8 = this.__input._getValue(var8_10++);
                }
            }
            if ((this.__modifierFlags[0] & '\u0001') != 0 && Character.isUpperCase(var6_8)) {
                var6_8 = Character.toLowerCase(var6_8);
            }
            if (var8_10 < var9_11 && Perl5Compiler.__isComplexRepetitionOp(this.__input._array, var8_10)) {
                if (var7_7 > 0) {
                    var8_10 = var10_12;
                    break;
                }
                ++var7_7;
                this.__emitCode(var6_8);
                break;
            }
            this.__emitCode(var6_8);
        }
        this.__input._setOffset(var8_10 - 1);
        this.__getNextChar();
        if (var7_7 < 0) {
            throw new MalformedPatternException("Unexpected compilation failure.  Please report this bug!");
        }
        if (var7_7 > 0) {
            var1_1[0] = var1_1[0] | 1;
        }
        if (var7_7 == 1) {
            var1_1[0] = var1_1[0] | 2;
        }
        if (this.__program != null) {
            this.__program[OpCode._getOperand((int)var4_4)] = (char)var7_7;
        }
        this.__emitCode('\uffff');
        return var4_4;
    }

    private int __parseUnicodeClass() throws MalformedPatternException {
        int n;
        boolean bl = false;
        char c = '\uffff';
        int[] arrn = new int[]{0};
        boolean[] arrbl = new boolean[]{false};
        if (this.__input._getValue() == '^') {
            n = this.__emitNode('$');
            this.__input._increment();
        } else {
            n = this.__emitNode('#');
        }
        char c2 = this.__input._getValue();
        boolean bl2 = c2 == ']' || c2 == '-';
        while (!this.__input._isAtEnd() && (c2 = this.__input._getValue()) != ']' || bl2) {
            bl2 = false;
            boolean bl3 = false;
            this.__input._increment();
            if (c2 == '\\' || c2 == '[') {
                if (c2 == '\\') {
                    c2 = this.__input._postIncrement();
                } else {
                    char c3 = this.__parsePOSIX(arrbl);
                    if (c3 != '\u0000') {
                        bl3 = true;
                        c2 = c3;
                    }
                }
                if (!bl3) {
                    switch (c2) {
                        case 'w': {
                            bl3 = true;
                            c2 = '\u0012';
                            c = '\uffff';
                            break;
                        }
                        case 'W': {
                            bl3 = true;
                            c2 = '\u0013';
                            c = '\uffff';
                            break;
                        }
                        case 's': {
                            bl3 = true;
                            c2 = '\u0016';
                            c = '\uffff';
                            break;
                        }
                        case 'S': {
                            bl3 = true;
                            c2 = '\u0017';
                            c = '\uffff';
                            break;
                        }
                        case 'd': {
                            bl3 = true;
                            c2 = '\u0018';
                            c = '\uffff';
                            break;
                        }
                        case 'D': {
                            bl3 = true;
                            c2 = '\u0019';
                            c = '\uffff';
                            break;
                        }
                        case 'n': {
                            c2 = '\n';
                            break;
                        }
                        case 'r': {
                            c2 = '\r';
                            break;
                        }
                        case 't': {
                            c2 = '\t';
                            break;
                        }
                        case 'f': {
                            c2 = '\f';
                            break;
                        }
                        case 'b': {
                            c2 = '\b';
                            break;
                        }
                        case 'e': {
                            c2 = '\u001b';
                            break;
                        }
                        case 'a': {
                            c2 = '\u0007';
                            break;
                        }
                        case 'x': {
                            c2 = (char)Perl5Compiler.__parseHex(this.__input._array, this.__input._getOffset(), 2, arrn);
                            this.__input._increment(arrn[0]);
                            break;
                        }
                        case 'c': {
                            c2 = this.__input._postIncrement();
                            if (Character.isLowerCase(c2)) {
                                c2 = Character.toUpperCase(c2);
                            }
                            c2 = (char)(c2 ^ 0x40);
                            break;
                        }
                        case '0': 
                        case '1': 
                        case '2': 
                        case '3': 
                        case '4': 
                        case '5': 
                        case '6': 
                        case '7': 
                        case '8': 
                        case '9': {
                            c2 = (char)Perl5Compiler.__parseOctal(this.__input._array, this.__input._getOffset() - 1, 3, arrn);
                            this.__input._increment(arrn[0] - 1);
                            break;
                        }
                    }
                }
            }
            if (bl) {
                if (c > c2) {
                    throw new MalformedPatternException("Invalid [] range in expression.");
                }
                bl = false;
            } else {
                c = c2;
                if (!bl3 && this.__input._getValue() == '-' && this.__input._getOffset() + 1 < this.__input._getLength() && this.__input._getValueRelative(1) != ']') {
                    this.__input._increment();
                    bl = true;
                    continue;
                }
            }
            if (c == c2) {
                if (bl3) {
                    if (!arrbl[0]) {
                        this.__emitCode('/');
                    } else {
                        this.__emitCode('0');
                    }
                } else {
                    this.__emitCode('1');
                }
                this.__emitCode(c2);
                if ((this.__modifierFlags[0] & '\u0001') != 0 && Character.isUpperCase(c2) && Character.isUpperCase(c)) {
                    --this.__programSize;
                    this.__emitCode(Character.toLowerCase(c2));
                }
            }
            if (c < c2) {
                this.__emitCode('%');
                this.__emitCode(c);
                this.__emitCode(c2);
                if ((this.__modifierFlags[0] & '\u0001') != 0 && Character.isUpperCase(c2) && Character.isUpperCase(c)) {
                    this.__programSize -= 2;
                    this.__emitCode(Character.toLowerCase(c));
                    this.__emitCode(Character.toLowerCase(c2));
                }
                c = '\uffff';
                bl = false;
            }
            c = c2;
        }
        if (this.__input._getValue() != ']') {
            throw new MalformedPatternException("Unmatched [] in expression.");
        }
        this.__getNextChar();
        this.__emitCode('\u0000');
        return n;
    }

    private char __parsePOSIX(boolean[] arrbl) throws MalformedPatternException {
        char c;
        int n = this.__input._getOffset();
        int n2 = this.__input._getLength();
        int n3 = n;
        if ((c = this.__input._getValue(n3++)) != ':') {
            return '\u0000';
        }
        if (this.__input._getValue(n3) == '^') {
            arrbl[0] = true;
            ++n3;
        } else {
            arrbl[0] = false;
        }
        StringBuffer stringBuffer = new StringBuffer();
        try {
            while ((c = this.__input._getValue(n3++)) != ':' && n3 < n2) {
                stringBuffer.append(c);
            }
        }
        catch (Exception exception) {
            return '\u0000';
        }
        if (this.__input._getValue(n3++) != ']') {
            return '\u0000';
        }
        Object v = __hashPOSIX.get(stringBuffer.toString());
        if (v == null) {
            return '\u0000';
        }
        this.__input._setOffset(n3);
        return ((Character)v).charValue();
    }

    private int __parseBranch(int[] arrn) throws MalformedPatternException {
        boolean bl = false;
        boolean bl2 = false;
        int[] arrn2 = new int[]{0};
        int n = 0;
        int n2 = 65535;
        int n3 = this.__parseAtom(arrn2);
        if (n3 == -1) {
            if ((arrn2[0] & 8) != 0) {
                arrn[0] = arrn[0] | 8;
            }
            return -1;
        }
        char c = this.__input._getValue();
        if (c == '(' && this.__input._getValueRelative(1) == '?' && this.__input._getValueRelative(2) == '#') {
            while (c != '\uffff' && c != ')') {
                c = this.__input._increment();
            }
            if (c != '\uffff') {
                this.__getNextChar();
                c = this.__input._getValue();
            }
        }
        if (c == '{' && Perl5Compiler.__parseRepetition(this.__input._array, this.__input._getOffset())) {
            int n4;
            int n5 = this.__input._getOffset() + 1;
            int n6 = n4 = this.__input._getLength();
            char c2 = this.__input._getValue(n5);
            while (Character.isDigit(c2) || c2 == ',') {
                if (c2 == ',') {
                    if (n6 != n4) break;
                    n6 = n5;
                }
                c2 = this.__input._getValue(++n5);
            }
            if (c2 == '}') {
                StringBuffer stringBuffer = new StringBuffer(10);
                if (n6 == n4) {
                    n6 = n5;
                }
                this.__input._increment();
                int n7 = this.__input._getOffset();
                c2 = this.__input._getValue(n7);
                while (Character.isDigit(c2)) {
                    stringBuffer.append(c2);
                    c2 = this.__input._getValue(++n7);
                }
                try {
                    n = Integer.parseInt(stringBuffer.toString());
                }
                catch (NumberFormatException numberFormatException) {
                    throw new MalformedPatternException("Unexpected number format exception.  Please report this bug.NumberFormatException message: " + numberFormatException.getMessage());
                }
                c2 = this.__input._getValue(n6);
                n6 = c2 == ',' ? ++n6 : this.__input._getOffset();
                n7 = n6;
                stringBuffer = new StringBuffer(10);
                c2 = this.__input._getValue(n7);
                while (Character.isDigit(c2)) {
                    stringBuffer.append(c2);
                    c2 = this.__input._getValue(++n7);
                }
                try {
                    if (n7 != n6) {
                        n2 = Integer.parseInt(stringBuffer.toString());
                    }
                }
                catch (NumberFormatException numberFormatException) {
                    throw new MalformedPatternException("Unexpected number format exception.  Please report this bug.NumberFormatException message: " + numberFormatException.getMessage());
                }
                if (n2 == 0 && this.__input._getValue(n6) != '0') {
                    n2 = 65535;
                }
                this.__input._setOffset(n5);
                this.__getNextChar();
                bl = true;
                bl2 = true;
            }
        }
        if (!bl) {
            bl2 = false;
            if (!Perl5Compiler.__isSimpleRepetitionOp(c)) {
                arrn[0] = arrn2[0];
                return n3;
            }
            this.__getNextChar();
            int n8 = arrn[0] = c != '+' ? 4 : 1;
            if (c == '*' && (arrn2[0] & 2) != 0) {
                this.__programInsertOperator('\u0010', n3);
                this.__cost += 4;
            } else if (c == '*') {
                n = 0;
                bl2 = true;
            } else if (c == '+' && (arrn2[0] & 2) != 0) {
                this.__programInsertOperator('\u0011', n3);
                this.__cost += 3;
            } else if (c == '+') {
                n = 1;
                bl2 = true;
            } else if (c == '?') {
                n = 0;
                n2 = 1;
                bl2 = true;
            }
        }
        if (bl2) {
            if ((arrn2[0] & 2) != 0) {
                this.__cost += (2 + this.__cost) / 2;
                this.__programInsertOperator('\n', n3);
            } else {
                this.__cost += 4 + this.__cost;
                this.__programAddTail(n3, this.__emitNode('\"'));
                this.__programInsertOperator('\u000b', n3);
                this.__programAddTail(n3, this.__emitNode('\u000f'));
            }
            if (n > 0) {
                arrn[0] = 1;
            }
            if (n2 != 0 && n2 < n) {
                throw new MalformedPatternException("Invalid interval {" + n + "," + n2 + "}");
            }
            if (this.__program != null) {
                this.__program[n3 + 2] = (char)n;
                this.__program[n3 + 3] = (char)n2;
            }
        }
        if (this.__input._getValue() == '?') {
            this.__getNextChar();
            this.__programInsertOperator('\u001d', n3);
            this.__programAddTail(n3, n3 + 2);
        }
        if (Perl5Compiler.__isComplexRepetitionOp(this.__input._array, this.__input._getOffset())) {
            throw new MalformedPatternException("Nested repetitions *?+ in expression");
        }
        return n3;
    }

    private int __parseExpression(boolean bl, int[] arrn) throws MalformedPatternException {
        int n;
        char c;
        int[] arrn2;
        int n2;
        int n3;
        block31: {
            block29: {
                block30: {
                    char c2;
                    char[] arrc = new char[]{'\u0000'};
                    char[] arrc2 = new char[]{'\u0000'};
                    n3 = -1;
                    n2 = 0;
                    arrn2 = new int[]{0};
                    String string = "iogmsx-";
                    char[] arrc3 = arrc;
                    arrn[0] = 1;
                    if (!bl) break block29;
                    c = '\u0001';
                    if (this.__input._getValue() != '?') break block30;
                    this.__input._increment();
                    c = c2 = this.__input._postIncrement();
                    switch (c2) {
                        case '!': 
                        case ':': 
                        case '=': {
                            break block31;
                        }
                        case '#': {
                            c2 = this.__input._getValue();
                            while (c2 != '\uffff' && c2 != ')') {
                                c2 = this.__input._increment();
                            }
                            if (c2 != ')') {
                                throw new MalformedPatternException("Sequence (?#... not terminated");
                            }
                            this.__getNextChar();
                            arrn[0] = 8;
                            return -1;
                        }
                        default: {
                            this.__input._decrement();
                            c2 = this.__input._getValue();
                            while (c2 != '\uffff' && string.indexOf(c2) != -1) {
                                if (c2 == '-') {
                                    arrc3 = arrc2;
                                } else {
                                    Perl5Compiler.__setModifierFlag(arrc3, c2);
                                }
                                c2 = this.__input._increment();
                            }
                            this.__modifierFlags[0] = (char)(this.__modifierFlags[0] | arrc[0]);
                            this.__modifierFlags[0] = (char)(this.__modifierFlags[0] & ~arrc2[0]);
                            if (c2 != ')') {
                                throw new MalformedPatternException("Sequence (?" + c2 + "...) not recognized");
                            }
                            this.__getNextChar();
                            arrn[0] = 8;
                            return -1;
                        }
                    }
                }
                n2 = this.__numParentheses++;
                n3 = this.__emitArgNode('\u001b', (char)n2);
                break block31;
            }
            c = '\u0000';
        }
        int n4 = this.__parseAlternation(arrn2);
        if (n4 == -1) {
            return -1;
        }
        if (n3 != -1) {
            this.__programAddTail(n3, n4);
        } else {
            n3 = n4;
        }
        if ((arrn2[0] & 1) == 0) {
            arrn[0] = arrn[0] & 0xFFFFFFFE;
        }
        arrn[0] = arrn[0] | arrn2[0] & 4;
        while (this.__input._getValue() == '|') {
            this.__getNextChar();
            n4 = this.__parseAlternation(arrn2);
            if (n4 == -1) {
                return -1;
            }
            this.__programAddTail(n3, n4);
            if ((arrn2[0] & 1) == 0) {
                arrn[0] = arrn[0] & 0xFFFFFFFE;
            }
            arrn[0] = arrn[0] | arrn2[0] & 4;
        }
        switch (c) {
            case ':': {
                n = this.__emitNode('\u000f');
                break;
            }
            case '\u0001': {
                n = this.__emitArgNode('\u001c', (char)n2);
                break;
            }
            case '!': 
            case '=': {
                n = this.__emitNode('!');
                arrn[0] = arrn[0] & 0xFFFFFFFE;
                break;
            }
            default: {
                n = this.__emitNode('\u0000');
            }
        }
        this.__programAddTail(n3, n);
        n4 = n3;
        while (n4 != -1) {
            this.__programAddOperatorTail(n4, n);
            n4 = OpCode._getNext(this.__program, n4);
        }
        if (c == '=') {
            this.__programInsertOperator('\u001f', n3);
            this.__programAddTail(n3, this.__emitNode('\u000f'));
        } else if (c == '!') {
            this.__programInsertOperator(' ', n3);
            this.__programAddTail(n3, this.__emitNode('\u000f'));
        }
        if (c != '\u0000' && (this.__input._isAtEnd() || this.__getNextChar() != ')')) {
            throw new MalformedPatternException("Unmatched parentheses.");
        }
        if (c == '\u0000' && !this.__input._isAtEnd()) {
            if (this.__input._getValue() == ')') {
                throw new MalformedPatternException("Unmatched parentheses.");
            }
            throw new MalformedPatternException("Unreached characters at end of expression.  Please report this bug!");
        }
        return n3;
    }

    public Pattern compile(char[] arrc, int n) throws MalformedPatternException {
        String string;
        String string2;
        Perl5Pattern perl5Pattern;
        int n2;
        int n3;
        block44: {
            int[] arrn = new int[]{0};
            boolean bl = false;
            boolean bl2 = false;
            n3 = 0;
            this.__input = new CharStringPointer(arrc);
            n2 = n & 1;
            this.__modifierFlags[0] = (char)n;
            this.__sawBackreference = false;
            this.__numParentheses = 1;
            this.__programSize = 0;
            this.__cost = 0;
            this.__program = null;
            this.__emitCode('\u0000');
            if (this.__parseExpression(false, arrn) == -1) {
                throw new MalformedPatternException("Unknown compilation error.");
            }
            if (this.__programSize >= 65534) {
                throw new MalformedPatternException("Expression is too large.");
            }
            this.__program = new char[this.__programSize];
            perl5Pattern = new Perl5Pattern();
            perl5Pattern._program = this.__program;
            perl5Pattern._expression = new String(arrc);
            this.__input._setOffset(0);
            this.__numParentheses = 1;
            this.__programSize = 0;
            this.__cost = 0;
            this.__emitCode('\u0000');
            if (this.__parseExpression(false, arrn) == -1) {
                throw new MalformedPatternException("Unknown compilation error.");
            }
            n2 = this.__modifierFlags[0] & '\u0001';
            perl5Pattern._isExpensive = this.__cost >= 10;
            perl5Pattern._startClassOffset = -1;
            perl5Pattern._anchor = 0;
            perl5Pattern._back = -1;
            perl5Pattern._options = n;
            perl5Pattern._startString = null;
            perl5Pattern._mustString = null;
            string2 = null;
            string = null;
            int n4 = 1;
            if (this.__program[OpCode._getNext(this.__program, n4)] != '\u0000') break block44;
            int n5 = n4 = OpCode._getNextOperator(n4);
            char c = this.__program[n5];
            while (true) {
                block46: {
                    block45: {
                        if (c != '\u001b') break block45;
                        bl = true;
                        if (true) break block46;
                    }
                    if ((c != '\f' || this.__program[OpCode._getNext(this.__program, n5)] == '\f') && c != '\u0011' && c != '\u001d' && (OpCode._opType[c] != '\n' || OpCode._getArg1(this.__program, n5) <= '\u0000')) break;
                }
                if (c == '\u0011') {
                    bl2 = true;
                } else {
                    n5 += OpCode._operandLength[c];
                }
                n5 = OpCode._getNextOperator(n5);
                c = this.__program[n5];
            }
            boolean bl3 = true;
            while (bl3) {
                bl3 = false;
                c = this.__program[n5];
                if (c == '\u000e') {
                    string = new String(this.__program, OpCode._getOperand(n5 + 1), (int)this.__program[OpCode._getOperand(n5)]);
                    continue;
                }
                if (OpCode._isInArray(c, OpCode._opLengthOne, 2)) {
                    perl5Pattern._startClassOffset = n5;
                    continue;
                }
                if (c == '\u0014' || c == '\u0015') {
                    perl5Pattern._startClassOffset = n5;
                    continue;
                }
                if (OpCode._opType[c] == '\u0001') {
                    perl5Pattern._anchor = c == '\u0001' ? 1 : (c == '\u0002' ? 2 : 3);
                    n5 = OpCode._getNextOperator(n5);
                    bl3 = true;
                    continue;
                }
                if (c != '\u0010' || OpCode._opType[this.__program[OpCode._getNextOperator(n5)]] != '\u0007' || (perl5Pattern._anchor & 3) == 0) continue;
                perl5Pattern._anchor = 11;
                n5 = OpCode._getNextOperator(n5);
                bl3 = true;
            }
            if (!(!bl2 || bl && this.__sawBackreference)) {
                perl5Pattern._anchor |= 4;
            }
            StringBuffer stringBuffer = new StringBuffer();
            StringBuffer stringBuffer2 = new StringBuffer();
            int n6 = 0;
            n3 = 0;
            int n7 = 0;
            int n8 = 0;
            int n9 = 0;
            while (n4 > 0 && (c = this.__program[n4]) != '\u0000') {
                if (c == '\f') {
                    if (this.__program[OpCode._getNext(this.__program, n4)] == '\f') {
                        n7 = -30000;
                        while (this.__program[n4] == '\f') {
                            n4 = OpCode._getNext(this.__program, n4);
                        }
                        continue;
                    }
                    n4 = OpCode._getNextOperator(n4);
                    continue;
                }
                if (c == ' ') {
                    n7 = -30000;
                    n4 = OpCode._getNext(this.__program, n4);
                    continue;
                }
                if (c == '\u000e') {
                    int n10;
                    n5 = n4;
                    while (this.__program[n10 = OpCode._getNext(this.__program, n4)] == '\u001c') {
                        n4 = n10;
                    }
                    n3 += this.__program[OpCode._getOperand(n5)];
                    n10 = this.__program[OpCode._getOperand(n5)];
                    if (n7 - n8 == n6) {
                        stringBuffer.append(new String(this.__program, OpCode._getOperand(n5) + 1, n10));
                        n6 += n10;
                        n7 += n10;
                        n5 = OpCode._getNext(this.__program, n4);
                    } else if (n10 >= n6 + (n7 >= 0 ? 1 : 0)) {
                        n6 = n10;
                        stringBuffer = new StringBuffer(new String(this.__program, OpCode._getOperand(n5) + 1, n10));
                        n8 = n7;
                        n7 += n6;
                        n5 = OpCode._getNext(this.__program, n4);
                    } else {
                        n7 += n10;
                    }
                } else if (OpCode._isInArray(c, OpCode._opLengthVaries, 0)) {
                    n7 = -30000;
                    n6 = 0;
                    if (stringBuffer.length() > stringBuffer2.length()) {
                        stringBuffer2 = stringBuffer;
                        n9 = n8;
                    }
                    stringBuffer = new StringBuffer();
                    if (c == '\u0011' && OpCode._isInArray(this.__program[OpCode._getNextOperator(n4)], OpCode._opLengthOne, 0)) {
                        ++n3;
                    } else if (OpCode._opType[c] == '\n' && OpCode._isInArray(this.__program[OpCode._getNextOperator(n4) + 2], OpCode._opLengthOne, 0)) {
                        n3 += OpCode._getArg1(this.__program, n4);
                    }
                } else if (OpCode._isInArray(c, OpCode._opLengthOne, 0)) {
                    ++n7;
                    ++n3;
                    n6 = 0;
                    if (stringBuffer.length() > stringBuffer2.length()) {
                        stringBuffer2 = stringBuffer;
                        n9 = n8;
                    }
                    stringBuffer = new StringBuffer();
                }
                n4 = OpCode._getNext(this.__program, n4);
            }
            if (stringBuffer.length() + (OpCode._opType[this.__program[n5]] == '\u0004' ? 1 : 0) > stringBuffer2.length()) {
                stringBuffer2 = stringBuffer;
                n9 = n8;
            } else {
                stringBuffer = new StringBuffer();
            }
            if (stringBuffer2.length() > 0 && string == null) {
                string2 = stringBuffer2.toString();
                if (n9 < 0) {
                    n9 = -1;
                }
                perl5Pattern._back = n9;
            } else {
                stringBuffer2 = null;
            }
        }
        perl5Pattern._isCaseInsensitive = (n2 & 1) != 0;
        perl5Pattern._numParentheses = this.__numParentheses - 1;
        perl5Pattern._minLength = n3;
        if (string2 != null) {
            perl5Pattern._mustString = string2.toCharArray();
            perl5Pattern._mustUtility = 100;
        }
        if (string != null) {
            perl5Pattern._startString = string.toCharArray();
        }
        return perl5Pattern;
    }

    public Pattern compile(char[] arrc) throws MalformedPatternException {
        return this.compile(arrc, 0);
    }

    public Pattern compile(String string) throws MalformedPatternException {
        return this.compile(string.toCharArray(), 0);
    }

    public Pattern compile(String string, int n) throws MalformedPatternException {
        return this.compile(string.toCharArray(), n);
    }

    static {
        __hashPOSIX.put("alnum", new Character('2'));
        __hashPOSIX.put("word", new Character('\u0012'));
        __hashPOSIX.put("alpha", new Character('&'));
        __hashPOSIX.put("blank", new Character('\''));
        __hashPOSIX.put("cntrl", new Character('('));
        __hashPOSIX.put("digit", new Character('\u0018'));
        __hashPOSIX.put("graph", new Character(')'));
        __hashPOSIX.put("lower", new Character('*'));
        __hashPOSIX.put("print", new Character('+'));
        __hashPOSIX.put("punct", new Character(','));
        __hashPOSIX.put("space", new Character('\u0016'));
        __hashPOSIX.put("upper", new Character('-'));
        __hashPOSIX.put("xdigit", new Character('.'));
        __hashPOSIX.put("ascii", new Character('3'));
    }
}

