/*
 * Decompiled with CFR 0.150.
 */
package org.apache.oro.text;

import org.apache.oro.text.GenericPatternCache;
import org.apache.oro.text.regex.PatternCompiler;
import org.apache.oro.text.regex.Perl5Compiler;
import org.apache.oro.util.CacheFIFO2;

public final class PatternCacheFIFO2
extends GenericPatternCache {
    public PatternCacheFIFO2(int n, PatternCompiler patternCompiler) {
        super(new CacheFIFO2(n), patternCompiler);
    }

    public PatternCacheFIFO2(PatternCompiler patternCompiler) {
        this(20, patternCompiler);
    }

    public PatternCacheFIFO2(int n) {
        this(n, (PatternCompiler)new Perl5Compiler());
    }

    public PatternCacheFIFO2() {
        this(20);
    }
}

