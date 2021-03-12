/*
 * Decompiled with CFR 0.150.
 */
package org.apache.commons.net.ftp.parser;

import java.util.Hashtable;
import java.util.Vector;
import org.apache.commons.net.ftp.FTPClientConfig;
import org.apache.commons.net.ftp.parser.VMSFTPEntryParser;
import org.apache.oro.text.regex.MalformedPatternException;
import org.apache.oro.text.regex.MatchResult;
import org.apache.oro.text.regex.Pattern;
import org.apache.oro.text.regex.Perl5Compiler;
import org.apache.oro.text.regex.Perl5Matcher;

public class VMSVersioningFTPEntryParser
extends VMSFTPEntryParser {
    private Perl5Matcher _preparse_matcher_;
    private Pattern _preparse_pattern_;
    private static final String PRE_PARSE_REGEX = "(.*);([0-9]+)\\s*.*";

    public VMSVersioningFTPEntryParser() {
        this((FTPClientConfig)null);
    }

    public VMSVersioningFTPEntryParser(FTPClientConfig fTPClientConfig) {
        this.configure(fTPClientConfig);
        try {
            this._preparse_matcher_ = new Perl5Matcher();
            this._preparse_pattern_ = new Perl5Compiler().compile(PRE_PARSE_REGEX);
        }
        catch (MalformedPatternException malformedPatternException) {
            throw new IllegalArgumentException("Unparseable regex supplied:  (.*);([0-9]+)\\s*.*");
        }
    }

    public Vector preParse(Vector vector) {
        NameVersion nameVersion;
        NameVersion nameVersion2;
        String string;
        String string2;
        MatchResult matchResult;
        String string3;
        vector = super.preParse(vector);
        Hashtable<String, NameVersion> hashtable = new Hashtable<String, NameVersion>();
        int n = 0;
        while (n < vector.size()) {
            string3 = ((String)vector.elementAt(n)).trim();
            matchResult = null;
            if (this._preparse_matcher_.matches(string3, this._preparse_pattern_)) {
                matchResult = this._preparse_matcher_.getMatch();
                string2 = matchResult.group(1);
                string = matchResult.group(2);
                nameVersion2 = new NameVersion(string2, string);
                nameVersion = (NameVersion)hashtable.get(string2);
                if (null != nameVersion && nameVersion2.versionNumber < nameVersion.versionNumber) {
                    vector.removeElementAt(n);
                    continue;
                }
                hashtable.put(string2, nameVersion2);
            }
            ++n;
        }
        for (n = vector.size() - 1; n >= 0; --n) {
            string3 = ((String)vector.elementAt(n)).trim();
            matchResult = null;
            if (!this._preparse_matcher_.matches(string3, this._preparse_pattern_)) continue;
            matchResult = this._preparse_matcher_.getMatch();
            string2 = matchResult.group(1);
            string = matchResult.group(2);
            nameVersion2 = new NameVersion(string2, string);
            nameVersion = (NameVersion)hashtable.get(string2);
            if (null == nameVersion || nameVersion2.versionNumber >= nameVersion.versionNumber) continue;
            vector.removeElementAt(n);
        }
        return vector;
    }

    protected boolean isVersioning() {
        return true;
    }

    private class NameVersion {
        String name;
        int versionNumber;

        NameVersion(String string, String string2) {
            this.name = string;
            this.versionNumber = Integer.parseInt(string2);
        }
    }
}

