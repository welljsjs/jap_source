/*
 * Decompiled with CFR 0.150.
 */
package org.apache.oro.text.regex;

import org.apache.oro.text.regex.MatchResult;
import org.apache.oro.text.regex.Pattern;
import org.apache.oro.text.regex.PatternMatcher;
import org.apache.oro.text.regex.PatternMatcherInput;
import org.apache.oro.text.regex.StringSubstitution;

public class Perl5Substitution
extends StringSubstitution {
    public static final int INTERPOLATE_ALL = 0;
    public static final int INTERPOLATE_NONE = -1;
    private static final int __OPCODE_STORAGE_SIZE = 32;
    private static final int __MAX_GROUPS = 65535;
    static final int _OPCODE_COPY = -1;
    static final int _OPCODE_LOWERCASE_CHAR = -2;
    static final int _OPCODE_UPPERCASE_CHAR = -3;
    static final int _OPCODE_LOWERCASE_MODE = -4;
    static final int _OPCODE_UPPERCASE_MODE = -5;
    static final int _OPCODE_ENDCASE_MODE = -6;
    int _numInterpolations;
    int[] _subOpcodes;
    int _subOpcodesCount;
    char[] _substitutionChars;
    transient String _lastInterpolation;

    private static final boolean __isInterpolationCharacter(char c) {
        return Character.isDigit(c) || c == '&';
    }

    private void __addElement(int n) {
        int n2 = this._subOpcodes.length;
        if (this._subOpcodesCount == n2) {
            int[] arrn = new int[n2 + 32];
            System.arraycopy(this._subOpcodes, 0, arrn, 0, n2);
            this._subOpcodes = arrn;
        }
        this._subOpcodes[this._subOpcodesCount++] = n;
    }

    private void __parseSubs(String string) {
        this._substitutionChars = string.toCharArray();
        char[] arrc = this._substitutionChars;
        int n = arrc.length;
        this._subOpcodes = new int[32];
        this._subOpcodesCount = 0;
        int n2 = 0;
        int n3 = -1;
        boolean bl = false;
        boolean bl2 = false;
        boolean bl3 = false;
        for (int i = 0; i < n; ++i) {
            char c = arrc[i];
            int n4 = i + 1;
            if (bl) {
                int n5 = Character.digit(c, 10);
                if (n5 > -1) {
                    if (n2 <= 65535) {
                        n2 *= 10;
                        n2 += n5;
                    }
                    if (n4 != n) continue;
                    this.__addElement(n2);
                    continue;
                }
                if (c == '&' && arrc[i - 1] == '$') {
                    this.__addElement(0);
                    n2 = 0;
                    bl = false;
                    continue;
                }
                this.__addElement(n2);
                n2 = 0;
                bl = false;
            }
            if (c != '$' && c != '\\' || bl2) {
                bl2 = false;
                if (n3 < 0) {
                    n3 = i;
                    this.__addElement(-1);
                    this.__addElement(n3);
                }
                if (n4 != n) continue;
                this.__addElement(n4 - n3);
                continue;
            }
            if (n3 >= 0) {
                this.__addElement(i - n3);
                n3 = -1;
            }
            if (n4 == n) continue;
            char c2 = arrc[n4];
            if (c == '$') {
                bl = Perl5Substitution.__isInterpolationCharacter(c2);
                continue;
            }
            if (c != '\\') continue;
            if (c2 == 'l') {
                if (bl3) continue;
                this.__addElement(-2);
                ++i;
                continue;
            }
            if (c2 == 'u') {
                if (bl3) continue;
                this.__addElement(-3);
                ++i;
                continue;
            }
            if (c2 == 'L') {
                this.__addElement(-4);
                ++i;
                bl3 = true;
                continue;
            }
            if (c2 == 'U') {
                this.__addElement(-5);
                ++i;
                bl3 = true;
                continue;
            }
            if (c2 == 'E') {
                this.__addElement(-6);
                ++i;
                bl3 = false;
                continue;
            }
            bl2 = true;
        }
    }

    String _finalInterpolatedSub(MatchResult matchResult) {
        StringBuffer stringBuffer = new StringBuffer(10);
        this._calcSub(stringBuffer, matchResult);
        return stringBuffer.toString();
    }

    void _calcSub(StringBuffer stringBuffer, MatchResult matchResult) {
        int[] arrn = this._subOpcodes;
        int n = 0;
        char[] arrc = this._substitutionChars;
        char[] arrc2 = matchResult.group(0).toCharArray();
        int n2 = this._subOpcodesCount;
        for (int i = 0; i < n2; ++i) {
            char[] arrc3;
            int n3;
            int n4;
            int n5;
            int n6 = arrn[i];
            if (n6 >= 0 && n6 < matchResult.groups()) {
                int n7;
                n5 = matchResult.begin(n6);
                if (n5 < 0 || (n4 = matchResult.end(n6)) < 0 || n5 >= (n7 = matchResult.length()) || n4 > n7 || n5 >= n4) continue;
                n3 = n4 - n5;
                arrc3 = arrc2;
            } else if (n6 == -1) {
                if (++i >= n2) continue;
                n5 = arrn[i];
                if (++i >= n2) continue;
                n3 = arrn[i];
                arrc3 = arrc;
            } else {
                if (n6 == -2 || n6 == -3) {
                    if (n == -4 || n == -5) continue;
                    n = n6;
                    continue;
                }
                if (n6 == -4 || n6 == -5) {
                    n = n6;
                    continue;
                }
                if (n6 != -6) continue;
                n = 0;
                continue;
            }
            if (n == -2) {
                stringBuffer.append(Character.toLowerCase(arrc3[n5++]));
                stringBuffer.append(arrc3, n5, --n3);
                n = 0;
                continue;
            }
            if (n == -3) {
                stringBuffer.append(Character.toUpperCase(arrc3[n5++]));
                stringBuffer.append(arrc3, n5, --n3);
                n = 0;
                continue;
            }
            if (n == -4) {
                n4 = n5 + n3;
                while (n5 < n4) {
                    stringBuffer.append(Character.toLowerCase(arrc3[n5++]));
                }
                continue;
            }
            if (n == -5) {
                n4 = n5 + n3;
                while (n5 < n4) {
                    stringBuffer.append(Character.toUpperCase(arrc3[n5++]));
                }
                continue;
            }
            stringBuffer.append(arrc3, n5, n3);
        }
    }

    public Perl5Substitution() {
        this("", 0);
    }

    public Perl5Substitution(String string) {
        this(string, 0);
    }

    public Perl5Substitution(String string, int n) {
        this.setSubstitution(string, n);
    }

    public void setSubstitution(String string) {
        this.setSubstitution(string, 0);
    }

    public void setSubstitution(String string, int n) {
        super.setSubstitution(string);
        this._numInterpolations = n;
        if (n != -1 && (string.indexOf(36) != -1 || string.indexOf(92) != -1)) {
            this.__parseSubs(string);
        } else {
            this._subOpcodes = null;
        }
        this._lastInterpolation = null;
    }

    public void appendSubstitution(StringBuffer stringBuffer, MatchResult matchResult, int n, PatternMatcherInput patternMatcherInput, PatternMatcher patternMatcher, Pattern pattern) {
        if (this._subOpcodes == null) {
            super.appendSubstitution(stringBuffer, matchResult, n, patternMatcherInput, patternMatcher, pattern);
            return;
        }
        if (this._numInterpolations < 1 || n < this._numInterpolations) {
            this._calcSub(stringBuffer, matchResult);
        } else {
            if (n == this._numInterpolations) {
                this._lastInterpolation = this._finalInterpolatedSub(matchResult);
            }
            stringBuffer.append(this._lastInterpolation);
        }
    }
}

