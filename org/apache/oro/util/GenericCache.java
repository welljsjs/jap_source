/*
 * Decompiled with CFR 0.150.
 */
package org.apache.oro.util;

import java.io.Serializable;
import java.util.Hashtable;
import org.apache.oro.util.Cache;
import org.apache.oro.util.GenericCacheEntry;

public abstract class GenericCache
implements Cache,
Serializable {
    public static final int DEFAULT_CAPACITY = 20;
    int _numEntries = 0;
    GenericCacheEntry[] _cache;
    Hashtable _table;

    GenericCache(int n) {
        this._table = new Hashtable(n);
        this._cache = new GenericCacheEntry[n];
        while (--n >= 0) {
            this._cache[n] = new GenericCacheEntry(n);
        }
    }

    public abstract void addElement(Object var1, Object var2);

    public synchronized Object getElement(Object object) {
        Object v = this._table.get(object);
        if (v != null) {
            return ((GenericCacheEntry)v)._value;
        }
        return null;
    }

    public final int size() {
        return this._numEntries;
    }

    public final int capacity() {
        return this._cache.length;
    }

    public final boolean isFull() {
        return this._numEntries >= this._cache.length;
    }
}

