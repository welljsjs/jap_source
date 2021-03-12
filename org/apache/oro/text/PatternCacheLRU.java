/*
 * Decompiled with CFR 0.150.
 */
package org.apache.oro.text;

import org.apache.oro.text.GenericPatternCache;
import org.apache.oro.text.regex.PatternCompiler;
import org.apache.oro.text.regex.Perl5Compiler;
import org.apache.oro.util.CacheLRU;

public final class PatternCacheLRU
extends GenericPatternCache {
    public PatternCacheLRU(int n, PatternCompiler patternCompiler) {
        super(new CacheLRU(n), patternCompiler);
    }

    public PatternCacheLRU(PatternCompiler patternCompiler) {
        this(20, patternCompiler);
    }

    public PatternCacheLRU(int n) {
        this(n, (PatternCompiler)new Perl5Compiler());
    }

    public PatternCacheLRU() {
        this(20);
    }
}

