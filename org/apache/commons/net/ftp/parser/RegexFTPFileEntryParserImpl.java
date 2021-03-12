/*
 * Decompiled with CFR 0.150.
 */
package org.apache.commons.net.ftp.parser;

import org.apache.commons.net.ftp.FTPFileEntryParserImpl;
import org.apache.oro.text.regex.MalformedPatternException;
import org.apache.oro.text.regex.MatchResult;
import org.apache.oro.text.regex.Pattern;
import org.apache.oro.text.regex.PatternMatcher;
import org.apache.oro.text.regex.Perl5Compiler;
import org.apache.oro.text.regex.Perl5Matcher;

public abstract class RegexFTPFileEntryParserImpl
extends FTPFileEntryParserImpl {
    private Pattern pattern = null;
    private MatchResult result = null;
    protected PatternMatcher _matcher_ = null;

    public RegexFTPFileEntryParserImpl(String string) {
        try {
            this._matcher_ = new Perl5Matcher();
            this.pattern = new Perl5Compiler().compile(string);
        }
        catch (MalformedPatternException malformedPatternException) {
            throw new IllegalArgumentException("Unparseable regex supplied:  " + string);
        }
    }

    public boolean matches(String string) {
        this.result = null;
        if (this._matcher_.matches(string.trim(), this.pattern)) {
            this.result = this._matcher_.getMatch();
        }
        return null != this.result;
    }

    public int getGroupCnt() {
        if (this.result == null) {
            return 0;
        }
        return this.result.groups();
    }

    public String group(int n) {
        if (this.result == null) {
            return null;
        }
        return this.result.group(n);
    }

    public String getGroupsAsString() {
        StringBuffer stringBuffer = new StringBuffer();
        for (int i = 1; i <= this.result.groups(); ++i) {
            stringBuffer.append(i).append(") ").append(this.result.group(i)).append(System.getProperty("line.separator"));
        }
        return stringBuffer.toString();
    }
}

