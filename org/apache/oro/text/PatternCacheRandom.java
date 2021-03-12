/*
 * Decompiled with CFR 0.150.
 */
package org.apache.oro.text;

import org.apache.oro.text.GenericPatternCache;
import org.apache.oro.text.regex.PatternCompiler;
import org.apache.oro.text.regex.Perl5Compiler;
import org.apache.oro.util.CacheRandom;

public final class PatternCacheRandom
extends GenericPatternCache {
    public PatternCacheRandom(int n, PatternCompiler patternCompiler) {
        super(new CacheRandom(n), patternCompiler);
    }

    public PatternCacheRandom(PatternCompiler patternCompiler) {
        this(20, patternCompiler);
    }

    public PatternCacheRandom(int n) {
        this(n, (PatternCompiler)new Perl5Compiler());
    }

    public PatternCacheRandom() {
        this(20);
    }
}

