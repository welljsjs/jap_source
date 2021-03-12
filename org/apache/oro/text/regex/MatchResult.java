/*
 * Decompiled with CFR 0.150.
 */
package org.apache.oro.text.regex;

public interface MatchResult {
    public int length();

    public int groups();

    public String group(int var1);

    public int begin(int var1);

    public int end(int var1);

    public int beginOffset(int var1);

    public int endOffset(int var1);

    public String toString();
}

