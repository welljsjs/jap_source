/*
 * Decompiled with CFR 0.150.
 */
package org.apache.oro.text;

import org.apache.oro.text.regex.MalformedPatternException;
import org.apache.oro.text.regex.Pattern;
import org.apache.oro.text.regex.PatternCompiler;
import org.apache.oro.text.regex.Perl5Compiler;

public final class GlobCompiler
implements PatternCompiler {
    public static final int DEFAULT_MASK = 0;
    public static final int CASE_INSENSITIVE_MASK = 1;
    public static final int STAR_CANNOT_MATCH_NULL_MASK = 2;
    public static final int QUESTION_MATCHES_ZERO_OR_ONE_MASK = 4;
    public static final int READ_ONLY_MASK = 8;
    private Perl5Compiler __perl5Compiler = new Perl5Compiler();

    private static boolean __isPerl5MetaCharacter(char c) {
        return c == '*' || c == '?' || c == '+' || c == '[' || c == ']' || c == '(' || c == ')' || c == '|' || c == '^' || c == '$' || c == '.' || c == '{' || c == '}' || c == '\\';
    }

    private static boolean __isGlobMetaCharacter(char c) {
        return c == '*' || c == '?' || c == '[' || c == ']';
    }

    public static String globToPerl5(char[] arrc, int n) {
        boolean bl = false;
        StringBuffer stringBuffer = new StringBuffer(2 * arrc.length);
        boolean bl2 = false;
        boolean bl3 = (n & 4) != 0;
        bl = (n & 2) != 0;
        block11: for (int i = 0; i < arrc.length; ++i) {
            switch (arrc[i]) {
                case '*': {
                    if (bl2) {
                        stringBuffer.append('*');
                        continue block11;
                    }
                    if (bl) {
                        stringBuffer.append(".+");
                        continue block11;
                    }
                    stringBuffer.append(".*");
                    continue block11;
                }
                case '?': {
                    if (bl2) {
                        stringBuffer.append('?');
                        continue block11;
                    }
                    if (bl3) {
                        stringBuffer.append(".?");
                        continue block11;
                    }
                    stringBuffer.append('.');
                    continue block11;
                }
                case '[': {
                    bl2 = true;
                    stringBuffer.append(arrc[i]);
                    if (i + 1 >= arrc.length) continue block11;
                    switch (arrc[i + 1]) {
                        case '!': 
                        case '^': {
                            stringBuffer.append('^');
                            ++i;
                            continue block11;
                        }
                        case ']': {
                            stringBuffer.append(']');
                            ++i;
                            continue block11;
                        }
                    }
                    continue block11;
                }
                case ']': {
                    bl2 = false;
                    stringBuffer.append(arrc[i]);
                    continue block11;
                }
                case '\\': {
                    stringBuffer.append('\\');
                    if (i == arrc.length - 1) {
                        stringBuffer.append('\\');
                        continue block11;
                    }
                    if (GlobCompiler.__isGlobMetaCharacter(arrc[i + 1])) {
                        stringBuffer.append(arrc[++i]);
                        continue block11;
                    }
                    stringBuffer.append('\\');
                    continue block11;
                }
                default: {
                    if (!bl2 && GlobCompiler.__isPerl5MetaCharacter(arrc[i])) {
                        stringBuffer.append('\\');
                    }
                    stringBuffer.append(arrc[i]);
                }
            }
        }
        return stringBuffer.toString();
    }

    public Pattern compile(char[] arrc, int n) throws MalformedPatternException {
        int n2 = 0;
        if ((n & 1) != 0) {
            n2 |= 1;
        }
        if ((n & 8) != 0) {
            n2 |= 0x8000;
        }
        return this.__perl5Compiler.compile(GlobCompiler.globToPerl5(arrc, n), n2);
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
}

