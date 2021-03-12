/*
 * Decompiled with CFR 0.150.
 */
package org.apache.oro.text.regex;

import org.apache.oro.text.regex.OpCode;
import org.apache.oro.text.regex.Perl5Pattern;

public final class Perl5Debug {
    private Perl5Debug() {
    }

    public static String printProgram(Perl5Pattern perl5Pattern) {
        int n = 27;
        char[] arrc = perl5Pattern._program;
        int n2 = 1;
        StringBuffer stringBuffer = new StringBuffer();
        while (n != 0) {
            n = arrc[n2];
            stringBuffer.append(n2);
            Perl5Debug._printOperator(arrc, n2, stringBuffer);
            int n3 = OpCode._getNext(arrc, n2);
            n2 += OpCode._operandLength[n];
            stringBuffer.append("(" + n3 + ")");
            n2 += 2;
            if (n == 9) {
                n2 += 16;
            } else if (n == 35 || n == 36) {
                while (arrc[n2] != '\u0000') {
                    if (arrc[n2] == '%') {
                        n2 += 3;
                        continue;
                    }
                    n2 += 2;
                }
                ++n2;
            } else if (n == 14) {
                ++n2;
                stringBuffer.append(" <");
                while (arrc[n2] != '\uffff') {
                    stringBuffer.append(arrc[n2]);
                    ++n2;
                }
                stringBuffer.append(">");
                ++n2;
            }
            stringBuffer.append('\n');
        }
        if (perl5Pattern._startString != null) {
            stringBuffer.append("start `" + new String(perl5Pattern._startString) + "' ");
        }
        if (perl5Pattern._startClassOffset != -1) {
            stringBuffer.append("stclass `");
            Perl5Debug._printOperator(arrc, perl5Pattern._startClassOffset, stringBuffer);
            stringBuffer.append("' ");
        }
        if ((perl5Pattern._anchor & 3) != 0) {
            stringBuffer.append("anchored ");
        }
        if ((perl5Pattern._anchor & 4) != 0) {
            stringBuffer.append("plus ");
        }
        if ((perl5Pattern._anchor & 8) != 0) {
            stringBuffer.append("implicit ");
        }
        if (perl5Pattern._mustString != null) {
            stringBuffer.append("must have \"" + new String(perl5Pattern._mustString) + "\" back " + perl5Pattern._back + " ");
        }
        stringBuffer.append("minlen " + perl5Pattern._minLength + '\n');
        return stringBuffer.toString();
    }

    static void _printOperator(char[] arrc, int n, StringBuffer stringBuffer) {
        String string = null;
        stringBuffer.append(":");
        switch (arrc[n]) {
            case '\u0001': {
                string = "BOL";
                break;
            }
            case '\u0002': {
                string = "MBOL";
                break;
            }
            case '\u0003': {
                string = "SBOL";
                break;
            }
            case '\u0004': {
                string = "EOL";
                break;
            }
            case '\u0005': {
                string = "MEOL";
                break;
            }
            case '\u0007': {
                string = "ANY";
                break;
            }
            case '\b': {
                string = "SANY";
                break;
            }
            case '\t': {
                string = "ANYOF";
                break;
            }
            case '#': {
                string = "ANYOFUN";
                break;
            }
            case '$': {
                string = "NANYOFUN";
                break;
            }
            case '\f': {
                string = "BRANCH";
                break;
            }
            case '\u000e': {
                string = "EXACTLY";
                break;
            }
            case '\u000f': {
                string = "NOTHING";
                break;
            }
            case '\r': {
                string = "BACK";
                break;
            }
            case '\u0000': {
                string = "END";
                break;
            }
            case '\u0012': {
                string = "ALNUM";
                break;
            }
            case '\u0013': {
                string = "NALNUM";
                break;
            }
            case '\u0014': {
                string = "BOUND";
                break;
            }
            case '\u0015': {
                string = "NBOUND";
                break;
            }
            case '\u0016': {
                string = "SPACE";
                break;
            }
            case '\u0017': {
                string = "NSPACE";
                break;
            }
            case '\u0018': {
                string = "DIGIT";
                break;
            }
            case '\u0019': {
                string = "NDIGIT";
                break;
            }
            case '&': {
                string = "ALPHA";
                break;
            }
            case '\'': {
                string = "BLANK";
                break;
            }
            case '(': {
                string = "CNTRL";
                break;
            }
            case ')': {
                string = "GRAPH";
                break;
            }
            case '*': {
                string = "LOWER";
                break;
            }
            case '+': {
                string = "PRINT";
                break;
            }
            case ',': {
                string = "PUNCT";
                break;
            }
            case '-': {
                string = "UPPER";
                break;
            }
            case '.': {
                string = "XDIGIT";
                break;
            }
            case '2': {
                string = "ALNUMC";
                break;
            }
            case '3': {
                string = "ASCII";
                break;
            }
            case '\n': {
                stringBuffer.append("CURLY {");
                stringBuffer.append((int)OpCode._getArg1(arrc, n));
                stringBuffer.append(',');
                stringBuffer.append((int)OpCode._getArg2(arrc, n));
                stringBuffer.append('}');
                break;
            }
            case '\u000b': {
                stringBuffer.append("CURLYX {");
                stringBuffer.append((int)OpCode._getArg1(arrc, n));
                stringBuffer.append(',');
                stringBuffer.append((int)OpCode._getArg2(arrc, n));
                stringBuffer.append('}');
                break;
            }
            case '\u001a': {
                stringBuffer.append("REF");
                stringBuffer.append((int)OpCode._getArg1(arrc, n));
                break;
            }
            case '\u001b': {
                stringBuffer.append("OPEN");
                stringBuffer.append((int)OpCode._getArg1(arrc, n));
                break;
            }
            case '\u001c': {
                stringBuffer.append("CLOSE");
                stringBuffer.append((int)OpCode._getArg1(arrc, n));
                break;
            }
            case '\u0010': {
                string = "STAR";
                break;
            }
            case '\u0011': {
                string = "PLUS";
                break;
            }
            case '\u001d': {
                string = "MINMOD";
                break;
            }
            case '\u001e': {
                string = "GBOL";
                break;
            }
            case ' ': {
                string = "UNLESSM";
                break;
            }
            case '\u001f': {
                string = "IFMATCH";
                break;
            }
            case '!': {
                string = "SUCCEED";
                break;
            }
            case '\"': {
                string = "WHILEM";
                break;
            }
            default: {
                stringBuffer.append("Operator is unrecognized.  Faulty expression code!");
            }
        }
        if (string != null) {
            stringBuffer.append(string);
        }
    }
}

