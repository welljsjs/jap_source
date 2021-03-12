/*
 * Decompiled with CFR 0.150.
 */
package org.apache.oro.text;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.Writer;
import java.util.Vector;
import org.apache.oro.text.DefaultMatchAction;
import org.apache.oro.text.MatchAction;
import org.apache.oro.text.MatchActionInfo;
import org.apache.oro.text.regex.MalformedPatternException;
import org.apache.oro.text.regex.Pattern;
import org.apache.oro.text.regex.PatternCompiler;
import org.apache.oro.text.regex.PatternMatcher;
import org.apache.oro.text.regex.Perl5Compiler;
import org.apache.oro.text.regex.Perl5Matcher;
import org.apache.oro.text.regex.Util;

public final class MatchActionProcessor {
    private Pattern __fieldSeparator = null;
    private PatternCompiler __compiler;
    private PatternMatcher __matcher;
    private Vector __patterns = new Vector();
    private Vector __actions = new Vector();
    private MatchAction __defaultAction = new DefaultMatchAction();

    public MatchActionProcessor(PatternCompiler patternCompiler, PatternMatcher patternMatcher) {
        this.__compiler = patternCompiler;
        this.__matcher = patternMatcher;
    }

    public MatchActionProcessor() {
        this(new Perl5Compiler(), new Perl5Matcher());
    }

    public void addAction(String string, int n, MatchAction matchAction) throws MalformedPatternException {
        if (string != null) {
            this.__patterns.addElement(this.__compiler.compile(string, n));
        } else {
            this.__patterns.addElement(null);
        }
        this.__actions.addElement(matchAction);
    }

    public void addAction(String string, int n) throws MalformedPatternException {
        this.addAction(string, n, this.__defaultAction);
    }

    public void addAction(String string) throws MalformedPatternException {
        this.addAction(string, 0);
    }

    public void addAction(String string, MatchAction matchAction) throws MalformedPatternException {
        this.addAction(string, 0, matchAction);
    }

    public void setFieldSeparator(String string, int n) throws MalformedPatternException {
        if (string == null) {
            this.__fieldSeparator = null;
            return;
        }
        this.__fieldSeparator = this.__compiler.compile(string, n);
    }

    public void setFieldSeparator(String string) throws MalformedPatternException {
        this.setFieldSeparator(string, 0);
    }

    public void processMatches(InputStream inputStream, OutputStream outputStream, String string) throws IOException {
        this.processMatches(new InputStreamReader(inputStream, string), new OutputStreamWriter(outputStream));
    }

    public void processMatches(InputStream inputStream, OutputStream outputStream) throws IOException {
        this.processMatches(new InputStreamReader(inputStream), new OutputStreamWriter(outputStream));
    }

    public void processMatches(Reader reader, Writer writer) throws IOException {
        LineNumberReader lineNumberReader = new LineNumberReader(reader);
        PrintWriter printWriter = new PrintWriter(writer);
        MatchActionInfo matchActionInfo = new MatchActionInfo();
        Vector vector = new Vector();
        matchActionInfo.matcher = this.__matcher;
        matchActionInfo.fieldSeparator = this.__fieldSeparator;
        matchActionInfo.input = lineNumberReader;
        matchActionInfo.output = printWriter;
        matchActionInfo.fields = null;
        int n = this.__patterns.size();
        matchActionInfo.lineNumber = 0;
        while ((matchActionInfo.line = lineNumberReader.readLine()) != null) {
            matchActionInfo.charLine = matchActionInfo.line.toCharArray();
            for (int i = 0; i < n; ++i) {
                MatchAction matchAction;
                Object e = this.__patterns.elementAt(i);
                if (e != null) {
                    Pattern pattern = (Pattern)this.__patterns.elementAt(i);
                    if (!this.__matcher.contains(matchActionInfo.charLine, pattern)) continue;
                    matchActionInfo.match = this.__matcher.getMatch();
                    matchActionInfo.lineNumber = lineNumberReader.getLineNumber();
                    matchActionInfo.pattern = pattern;
                    if (this.__fieldSeparator != null) {
                        vector.removeAllElements();
                        Util.split(vector, this.__matcher, this.__fieldSeparator, matchActionInfo.line);
                        matchActionInfo.fields = vector;
                    } else {
                        matchActionInfo.fields = null;
                    }
                    matchAction = (MatchAction)this.__actions.elementAt(i);
                    matchAction.processMatch(matchActionInfo);
                    continue;
                }
                matchActionInfo.match = null;
                matchActionInfo.lineNumber = lineNumberReader.getLineNumber();
                if (this.__fieldSeparator != null) {
                    vector.removeAllElements();
                    Util.split(vector, this.__matcher, this.__fieldSeparator, matchActionInfo.line);
                    matchActionInfo.fields = vector;
                } else {
                    matchActionInfo.fields = null;
                }
                matchAction = (MatchAction)this.__actions.elementAt(i);
                matchAction.processMatch(matchActionInfo);
            }
        }
        printWriter.flush();
        lineNumberReader.close();
    }
}

