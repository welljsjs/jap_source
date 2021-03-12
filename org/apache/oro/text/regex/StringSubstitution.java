/*
 * Decompiled with CFR 0.150.
 */
package org.apache.oro.text.regex;

import org.apache.oro.text.regex.MatchResult;
import org.apache.oro.text.regex.Pattern;
import org.apache.oro.text.regex.PatternMatcher;
import org.apache.oro.text.regex.PatternMatcherInput;
import org.apache.oro.text.regex.Substitution;

public class StringSubstitution
implements Substitution {
    int _subLength;
    String _substitution;

    public StringSubstitution() {
        this("");
    }

    public StringSubstitution(String string) {
        this.setSubstitution(string);
    }

    public void setSubstitution(String string) {
        this._substitution = string;
        this._subLength = string.length();
    }

    public String getSubstitution() {
        return this._substitution;
    }

    public String toString() {
        return this.getSubstitution();
    }

    public void appendSubstitution(StringBuffer stringBuffer, MatchResult matchResult, int n, PatternMatcherInput patternMatcherInput, PatternMatcher patternMatcher, Pattern pattern) {
        if (this._subLength == 0) {
            return;
        }
        stringBuffer.append(this._substitution);
    }
}

