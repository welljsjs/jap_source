/*
 * Decompiled with CFR 0.150.
 */
package org.apache.oro.text.regex;

import java.util.Vector;
import org.apache.oro.text.regex.MatchResult;
import org.apache.oro.text.regex.Pattern;
import org.apache.oro.text.regex.PatternMatcher;
import org.apache.oro.text.regex.PatternMatcherInput;
import org.apache.oro.text.regex.Substitution;

public final class Util {
    public static final int SUBSTITUTE_ALL = -1;
    public static final int SPLIT_ALL = 0;

    private Util() {
    }

    public static void split(Vector vector, PatternMatcher patternMatcher, Pattern pattern, String string, int n) {
        PatternMatcherInput patternMatcherInput = new PatternMatcherInput(string);
        int n2 = 0;
        while (--n != 0 && patternMatcher.contains(patternMatcherInput, pattern)) {
            MatchResult matchResult = patternMatcher.getMatch();
            vector.addElement(string.substring(n2, matchResult.beginOffset(0)));
            n2 = matchResult.endOffset(0);
        }
        vector.addElement(string.substring(n2, string.length()));
    }

    public static void split(Vector vector, PatternMatcher patternMatcher, Pattern pattern, String string) {
        Util.split(vector, patternMatcher, pattern, string, 0);
    }

    public static Vector split(PatternMatcher patternMatcher, Pattern pattern, String string, int n) {
        Vector vector = new Vector(20);
        Util.split(vector, patternMatcher, pattern, string, n);
        return vector;
    }

    public static Vector split(PatternMatcher patternMatcher, Pattern pattern, String string) {
        return Util.split(patternMatcher, pattern, string, 0);
    }

    public static String substitute(PatternMatcher patternMatcher, Pattern pattern, Substitution substitution, String string, int n) {
        PatternMatcherInput patternMatcherInput;
        StringBuffer stringBuffer = new StringBuffer(string.length());
        if (Util.substitute(stringBuffer, patternMatcher, pattern, substitution, patternMatcherInput = new PatternMatcherInput(string), n) != 0) {
            return stringBuffer.toString();
        }
        return string;
    }

    public static String substitute(PatternMatcher patternMatcher, Pattern pattern, Substitution substitution, String string) {
        return Util.substitute(patternMatcher, pattern, substitution, string, 1);
    }

    public static int substitute(StringBuffer stringBuffer, PatternMatcher patternMatcher, Pattern pattern, Substitution substitution, String string, int n) {
        PatternMatcherInput patternMatcherInput = new PatternMatcherInput(string);
        return Util.substitute(stringBuffer, patternMatcher, pattern, substitution, patternMatcherInput, n);
    }

    public static int substitute(StringBuffer stringBuffer, PatternMatcher patternMatcher, Pattern pattern, Substitution substitution, PatternMatcherInput patternMatcherInput, int n) {
        int n2 = 0;
        int n3 = patternMatcherInput.getBeginOffset();
        char[] arrc = patternMatcherInput.getBuffer();
        while (n != 0 && patternMatcher.contains(patternMatcherInput, pattern)) {
            --n;
            stringBuffer.append(arrc, n3, patternMatcherInput.getMatchBeginOffset() - n3);
            substitution.appendSubstitution(stringBuffer, patternMatcher.getMatch(), ++n2, patternMatcherInput, patternMatcher, pattern);
            n3 = patternMatcherInput.getMatchEndOffset();
        }
        stringBuffer.append(arrc, n3, patternMatcherInput.length() - n3);
        return n2;
    }
}

