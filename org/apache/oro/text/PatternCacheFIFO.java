/*
 * Decompiled with CFR 0.150.
 */
package org.apache.oro.text;

import org.apache.oro.text.GenericPatternCache;
import org.apache.oro.text.regex.PatternCompiler;
import org.apache.oro.text.regex.Perl5Compiler;
import org.apache.oro.util.CacheFIFO;

public final class PatternCacheFIFO
extends GenericPatternCache {
    public PatternCacheFIFO(int n, PatternCompiler patternCompiler) {
        super(new CacheFIFO(n), patternCompiler);
    }

    public PatternCacheFIFO(PatternCompiler patternCompiler) {
        this(20, patternCompiler);
    }

    public PatternCacheFIFO(int n) {
        this(n, (PatternCompiler)new Perl5Compiler());
    }

    public PatternCacheFIFO() {
        this(20);
    }
}

