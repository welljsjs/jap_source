/*
 * Decompiled with CFR 0.150.
 */
package org.apache.oro.text.regex;

import org.apache.oro.text.regex.MatchResult;
import org.apache.oro.text.regex.Pattern;
import org.apache.oro.text.regex.PatternMatcher;
import org.apache.oro.text.regex.PatternMatcherInput;

public interface Substitution {
    public void appendSubstitution(StringBuffer var1, MatchResult var2, int var3, PatternMatcherInput var4, PatternMatcher var5, Pattern var6);
}

