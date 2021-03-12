/*
 * Decompiled with CFR 0.150.
 */
package org.apache.oro.text;

import org.apache.oro.text.MalformedCachePatternException;
import org.apache.oro.text.regex.MalformedPatternException;
import org.apache.oro.text.regex.Pattern;

public interface PatternCache {
    public Pattern addPattern(String var1) throws MalformedPatternException;

    public Pattern addPattern(String var1, int var2) throws MalformedPatternException;

    public Pattern getPattern(String var1) throws MalformedCachePatternException;

    public Pattern getPattern(String var1, int var2) throws MalformedCachePatternException;

    public int size();

    public int capacity();
}

