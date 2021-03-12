/*
 * Decompiled with CFR 0.150.
 */
package org.apache.oro.text.perl;

import java.util.Vector;
import org.apache.oro.text.PatternCache;
import org.apache.oro.text.PatternCacheLRU;
import org.apache.oro.text.perl.MalformedPerl5PatternException;
import org.apache.oro.text.perl.ParsedSubstitutionEntry;
import org.apache.oro.text.regex.MalformedPatternException;
import org.apache.oro.text.regex.MatchResult;
import org.apache.oro.text.regex.Pattern;
import org.apache.oro.text.regex.PatternMatcher;
import org.apache.oro.text.regex.PatternMatcherInput;
import org.apache.oro.text.regex.Perl5Compiler;
import org.apache.oro.text.regex.Perl5Matcher;
import org.apache.oro.text.regex.Perl5Substitution;
import org.apache.oro.text.regex.Substitution;
import org.apache.oro.text.regex.Util;
import org.apache.oro.util.Cache;
import org.apache.oro.util.CacheLRU;

public final class Perl5Util
implements MatchResult {
    private static final String __matchExpression = "m?(\\W)(.*)\\1([imsx]*)";
    private PatternCache __patternCache;
    private Cache __expressionCache;
    private Perl5Matcher __matcher;
    private Pattern __matchPattern;
    private MatchResult __lastMatch;
    private Vector __splitList = new Vector();
    private Object __originalInput;
    private int __inputBeginOffset;
    private int __inputEndOffset;
    private static final String __nullString = "";
    public static final int SPLIT_ALL = 0;

    public Perl5Util(PatternCache patternCache) {
        this.__matcher = new Perl5Matcher();
        this.__patternCache = patternCache;
        this.__expressionCache = new CacheLRU(patternCache.capacity());
        this.__compilePatterns();
    }

    public Perl5Util() {
        this(new PatternCacheLRU());
    }

    private void __compilePatterns() {
        Perl5Compiler perl5Compiler = new Perl5Compiler();
        try {
            this.__matchPattern = perl5Compiler.compile(__matchExpression, 16);
        }
        catch (MalformedPatternException malformedPatternException) {
            throw new RuntimeException(malformedPatternException.getMessage());
        }
    }

    private Pattern __parseMatchExpression(String string) throws MalformedPerl5PatternException {
        Object object = this.__expressionCache.getElement(string);
        try {
            if (object != null) {
                return (Pattern)object;
            }
        }
        catch (ClassCastException classCastException) {
            // empty catch block
        }
        if (!this.__matcher.matches(string, this.__matchPattern)) {
            throw new MalformedPerl5PatternException("Invalid expression: " + string);
        }
        MatchResult matchResult = this.__matcher.getMatch();
        String string2 = matchResult.group(2);
        int n = 0;
        String string3 = matchResult.group(3);
        if (string3 != null) {
            int n2 = string3.length();
            block8: while (n2-- > 0) {
                switch (string3.charAt(n2)) {
                    case 'i': {
                        n |= 1;
                        continue block8;
                    }
                    case 'm': {
                        n |= 8;
                        continue block8;
                    }
                    case 's': {
                        n |= 0x10;
                        continue block8;
                    }
                    case 'x': {
                        n |= 0x20;
                        continue block8;
                    }
                }
                throw new MalformedPerl5PatternException("Invalid options: " + string3);
            }
        }
        Pattern pattern = this.__patternCache.getPattern(string2, n);
        this.__expressionCache.addElement(string, pattern);
        return pattern;
    }

    public synchronized boolean match(String string, char[] arrc) throws MalformedPerl5PatternException {
        this.__parseMatchExpression(string);
        boolean bl = this.__matcher.contains(arrc, this.__parseMatchExpression(string));
        if (bl) {
            this.__lastMatch = this.__matcher.getMatch();
            this.__originalInput = arrc;
            this.__inputBeginOffset = 0;
            this.__inputEndOffset = arrc.length;
        }
        return bl;
    }

    public synchronized boolean match(String string, String string2) throws MalformedPerl5PatternException {
        return this.match(string, string2.toCharArray());
    }

    public synchronized boolean match(String string, PatternMatcherInput patternMatcherInput) throws MalformedPerl5PatternException {
        boolean bl = this.__matcher.contains(patternMatcherInput, this.__parseMatchExpression(string));
        if (bl) {
            this.__lastMatch = this.__matcher.getMatch();
            this.__originalInput = patternMatcherInput.getInput();
            this.__inputBeginOffset = patternMatcherInput.getBeginOffset();
            this.__inputEndOffset = patternMatcherInput.getEndOffset();
        }
        return bl;
    }

    public synchronized MatchResult getMatch() {
        return this.__lastMatch;
    }

    public synchronized int substitute(StringBuffer stringBuffer, String string, String string2) throws MalformedPerl5PatternException {
        int n;
        char[] arrc;
        block23: {
            Object object = this.__expressionCache.getElement(string);
            if (object != null) {
                ParsedSubstitutionEntry parsedSubstitutionEntry;
                try {
                    parsedSubstitutionEntry = (ParsedSubstitutionEntry)object;
                }
                catch (ClassCastException classCastException) {
                    break block23;
                }
                int n2 = Util.substitute(stringBuffer, (PatternMatcher)this.__matcher, parsedSubstitutionEntry._pattern, (Substitution)parsedSubstitutionEntry._substitution, string2, parsedSubstitutionEntry._numSubstitutions);
                this.__lastMatch = this.__matcher.getMatch();
                return n2;
            }
        }
        if ((arrc = string.toCharArray()).length < 4 || arrc[0] != 's' || Character.isLetterOrDigit(arrc[1]) || arrc[1] == '-') {
            throw new MalformedPerl5PatternException("Invalid expression: " + string);
        }
        char c = arrc[1];
        int n3 = 2;
        int n4 = -1;
        int n5 = -1;
        boolean bl = false;
        for (n = n3; n < arrc.length; ++n) {
            if (arrc[n] == '\\') {
                bl = !bl;
                continue;
            }
            if (arrc[n] == c && !bl) {
                n5 = n;
                break;
            }
            if (!bl) continue;
            bl = !bl;
        }
        if (n5 == -1 || n5 == arrc.length - 1) {
            throw new MalformedPerl5PatternException("Invalid expression: " + string);
        }
        bl = false;
        boolean bl2 = true;
        StringBuffer stringBuffer2 = new StringBuffer(arrc.length - n5);
        for (n = n5 + 1; n < arrc.length; ++n) {
            if (arrc[n] == '\\') {
                boolean bl3 = bl = !bl;
                if (bl && n + 1 < arrc.length && arrc[n + 1] == c && string.lastIndexOf(c, arrc.length - 1) != n + 1) {
                    bl2 = false;
                    continue;
                }
            } else {
                if (arrc[n] == c && bl2) {
                    n4 = n;
                    break;
                }
                bl = false;
                bl2 = true;
            }
            stringBuffer2.append(arrc[n]);
        }
        if (n4 == -1) {
            throw new MalformedPerl5PatternException("Invalid expression: " + string);
        }
        int n6 = 0;
        int n7 = 1;
        int n8 = c != '\'' ? 0 : -1;
        block12: for (n = n4 + 1; n < arrc.length; ++n) {
            switch (arrc[n]) {
                case 'i': {
                    n6 |= 1;
                    continue block12;
                }
                case 'm': {
                    n6 |= 8;
                    continue block12;
                }
                case 's': {
                    n6 |= 0x10;
                    continue block12;
                }
                case 'x': {
                    n6 |= 0x20;
                    continue block12;
                }
                case 'g': {
                    n7 = -1;
                    continue block12;
                }
                case 'o': {
                    n8 = 1;
                    continue block12;
                }
                default: {
                    throw new MalformedPerl5PatternException("Invalid option: " + arrc[n]);
                }
            }
        }
        Pattern pattern = this.__patternCache.getPattern(new String(arrc, n3, n5 - n3), n6);
        Perl5Substitution perl5Substitution = new Perl5Substitution(stringBuffer2.toString(), n8);
        ParsedSubstitutionEntry parsedSubstitutionEntry = new ParsedSubstitutionEntry(pattern, perl5Substitution, n7);
        this.__expressionCache.addElement(string, parsedSubstitutionEntry);
        int n9 = Util.substitute(stringBuffer, (PatternMatcher)this.__matcher, pattern, (Substitution)perl5Substitution, string2, n7);
        this.__lastMatch = this.__matcher.getMatch();
        return n9;
    }

    public synchronized String substitute(String string, String string2) throws MalformedPerl5PatternException {
        StringBuffer stringBuffer = new StringBuffer();
        this.substitute(stringBuffer, string, string2);
        return stringBuffer.toString();
    }

    public synchronized void split(Vector vector, String string, String string2, int n) throws MalformedPerl5PatternException {
        String string3;
        MatchResult matchResult = null;
        Pattern pattern = this.__parseMatchExpression(string);
        PatternMatcherInput patternMatcherInput = new PatternMatcherInput(string2);
        int n2 = 0;
        while (--n != 0 && this.__matcher.contains(patternMatcherInput, pattern)) {
            matchResult = this.__matcher.getMatch();
            this.__splitList.addElement(string2.substring(n2, matchResult.beginOffset(0)));
            int n3 = matchResult.groups();
            if (n3 > 1) {
                for (int i = 1; i < n3; ++i) {
                    String string4 = matchResult.group(i);
                    if (string4 == null || string4.length() <= 0) continue;
                    this.__splitList.addElement(string4);
                }
            }
            n2 = matchResult.endOffset(0);
        }
        this.__splitList.addElement(string2.substring(n2, string2.length()));
        while (!this.__splitList.isEmpty() && (string3 = (String)this.__splitList.lastElement()).length() == 0) {
            this.__splitList.removeElementAt(this.__splitList.size() - 1);
        }
        for (int i = 0; i < this.__splitList.size(); ++i) {
            vector.addElement(this.__splitList.elementAt(i));
        }
        this.__splitList.removeAllElements();
        this.__lastMatch = matchResult;
    }

    public synchronized void split(Vector vector, String string, String string2) throws MalformedPerl5PatternException {
        this.split(vector, string, string2, 0);
    }

    public synchronized void split(Vector vector, String string) throws MalformedPerl5PatternException {
        this.split(vector, "/\\s+/", string);
    }

    public synchronized Vector split(String string, String string2, int n) throws MalformedPerl5PatternException {
        Vector vector = new Vector(20);
        this.split(vector, string, string2, n);
        return vector;
    }

    public synchronized Vector split(String string, String string2) throws MalformedPerl5PatternException {
        return this.split(string, string2, 0);
    }

    public synchronized Vector split(String string) throws MalformedPerl5PatternException {
        return this.split("/\\s+/", string);
    }

    public synchronized int length() {
        return this.__lastMatch.length();
    }

    public synchronized int groups() {
        return this.__lastMatch.groups();
    }

    public synchronized String group(int n) {
        return this.__lastMatch.group(n);
    }

    public synchronized int begin(int n) {
        return this.__lastMatch.begin(n);
    }

    public synchronized int end(int n) {
        return this.__lastMatch.end(n);
    }

    public synchronized int beginOffset(int n) {
        return this.__lastMatch.beginOffset(n);
    }

    public synchronized int endOffset(int n) {
        return this.__lastMatch.endOffset(n);
    }

    public synchronized String toString() {
        if (this.__lastMatch == null) {
            return null;
        }
        return this.__lastMatch.toString();
    }

    public synchronized String preMatch() {
        if (this.__originalInput == null) {
            return __nullString;
        }
        int n = this.__lastMatch.beginOffset(0);
        if (n <= 0) {
            return __nullString;
        }
        if (this.__originalInput instanceof char[]) {
            char[] arrc = (char[])this.__originalInput;
            if (n > arrc.length) {
                n = arrc.length;
            }
            return new String(arrc, this.__inputBeginOffset, n);
        }
        if (this.__originalInput instanceof String) {
            String string = (String)this.__originalInput;
            if (n > string.length()) {
                n = string.length();
            }
            return string.substring(this.__inputBeginOffset, n);
        }
        return __nullString;
    }

    public synchronized String postMatch() {
        if (this.__originalInput == null) {
            return __nullString;
        }
        int n = this.__lastMatch.endOffset(0);
        if (n < 0) {
            return __nullString;
        }
        if (this.__originalInput instanceof char[]) {
            char[] arrc = (char[])this.__originalInput;
            if (n >= arrc.length) {
                return __nullString;
            }
            return new String(arrc, n, this.__inputEndOffset - n);
        }
        if (this.__originalInput instanceof String) {
            String string = (String)this.__originalInput;
            if (n >= string.length()) {
                return __nullString;
            }
            return string.substring(n, this.__inputEndOffset);
        }
        return __nullString;
    }

    public synchronized char[] preMatchCharArray() {
        char[] arrc = null;
        if (this.__originalInput == null) {
            return null;
        }
        int n = this.__lastMatch.beginOffset(0);
        if (n <= 0) {
            return null;
        }
        if (this.__originalInput instanceof char[]) {
            char[] arrc2 = (char[])this.__originalInput;
            if (n >= arrc2.length) {
                n = arrc2.length;
            }
            arrc = new char[n - this.__inputBeginOffset];
            System.arraycopy(arrc2, this.__inputBeginOffset, arrc, 0, arrc.length);
        } else if (this.__originalInput instanceof String) {
            String string = (String)this.__originalInput;
            if (n >= string.length()) {
                n = string.length();
            }
            arrc = new char[n - this.__inputBeginOffset];
            string.getChars(this.__inputBeginOffset, n, arrc, 0);
        }
        return arrc;
    }

    public synchronized char[] postMatchCharArray() {
        char[] arrc = null;
        if (this.__originalInput == null) {
            return null;
        }
        int n = this.__lastMatch.endOffset(0);
        if (n < 0) {
            return null;
        }
        if (this.__originalInput instanceof char[]) {
            char[] arrc2 = (char[])this.__originalInput;
            if (n >= arrc2.length) {
                return null;
            }
            int n2 = this.__inputEndOffset - n;
            arrc = new char[n2];
            System.arraycopy(arrc2, n, arrc, 0, n2);
        } else if (this.__originalInput instanceof String) {
            String string = (String)this.__originalInput;
            if (n >= this.__inputEndOffset) {
                return null;
            }
            arrc = new char[this.__inputEndOffset - n];
            string.getChars(n, this.__inputEndOffset, arrc, 0);
        }
        return arrc;
    }
}

