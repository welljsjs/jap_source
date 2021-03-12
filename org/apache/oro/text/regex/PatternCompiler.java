/*
 * Decompiled with CFR 0.150.
 */
package org.apache.oro.text.regex;

import org.apache.oro.text.regex.MalformedPatternException;
import org.apache.oro.text.regex.Pattern;

public interface PatternCompiler {
    public Pattern compile(String var1) throws MalformedPatternException;

    public Pattern compile(String var1, int var2) throws MalformedPatternException;

    public Pattern compile(char[] var1) throws MalformedPatternException;

    public Pattern compile(char[] var1, int var2) throws MalformedPatternException;
}

