/*
 * Decompiled with CFR 0.150.
 */
package org.apache.oro.text;

import org.apache.oro.text.MalformedCachePatternException;
import org.apache.oro.text.PatternCache;
import org.apache.oro.text.regex.MalformedPatternException;
import org.apache.oro.text.regex.Pattern;
import org.apache.oro.text.regex.PatternCompiler;
import org.apache.oro.util.Cache;

public abstract class GenericPatternCache
implements PatternCache {
    PatternCompiler _compiler;
    Cache _cache;
    public static final int DEFAULT_CAPACITY = 20;

    GenericPatternCache(Cache cache, PatternCompiler patternCompiler) {
        this._cache = cache;
        this._compiler = patternCompiler;
    }

    public final synchronized Pattern addPattern(String string, int n) throws MalformedPatternException {
        Pattern pattern;
        Object object = this._cache.getElement(string);
        if (object != null && (pattern = (Pattern)object).getOptions() == n) {
            return pattern;
        }
        pattern = this._compiler.compile(string, n);
        this._cache.addElement(string, pattern);
        return pattern;
    }

    public final synchronized Pattern addPattern(String string) throws MalformedPatternException {
        return this.addPattern(string, 0);
    }

    public final synchronized Pattern getPattern(String string, int n) throws MalformedCachePatternException {
        Pattern pattern = null;
        try {
            pattern = this.addPattern(string, n);
        }
        catch (MalformedPatternException malformedPatternException) {
            throw new MalformedCachePatternException("Invalid expression: " + string + "\n" + malformedPatternException.getMessage());
        }
        return pattern;
    }

    public final synchronized Pattern getPattern(String string) throws MalformedCachePatternException {
        return this.getPattern(string, 0);
    }

    public final int size() {
        return this._cache.size();
    }

    public final int capacity() {
        return this._cache.capacity();
    }
}

