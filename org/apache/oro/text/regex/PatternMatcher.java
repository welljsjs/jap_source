/*
 * Decompiled with CFR 0.150.
 */
package org.apache.oro.text.regex;

import org.apache.oro.text.regex.MatchResult;
import org.apache.oro.text.regex.Pattern;
import org.apache.oro.text.regex.PatternMatcherInput;

public interface PatternMatcher {
    public boolean matchesPrefix(char[] var1, Pattern var2, int var3);

    public boolean matchesPrefix(String var1, Pattern var2);

    public boolean matchesPrefix(char[] var1, Pattern var2);

    public boolean matchesPrefix(PatternMatcherInput var1, Pattern var2);

    public boolean matches(String var1, Pattern var2);

    public boolean matches(char[] var1, Pattern var2);

    public boolean matches(PatternMatcherInput var1, Pattern var2);

    public boolean contains(String var1, Pattern var2);

    public boolean contains(char[] var1, Pattern var2);

    public boolean contains(PatternMatcherInput var1, Pattern var2);

    public MatchResult getMatch();
}

