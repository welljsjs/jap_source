/*
 * Decompiled with CFR 0.150.
 */
package org.apache.oro.util;

import java.util.Random;
import org.apache.oro.util.GenericCache;
import org.apache.oro.util.GenericCacheEntry;

public final class CacheRandom
extends GenericCache {
    private Random __random = new Random(System.currentTimeMillis());

    public CacheRandom(int n) {
        super(n);
    }

    public CacheRandom() {
        this(20);
    }

    public final synchronized void addElement(Object object, Object object2) {
        int n;
        Object v = this._table.get(object);
        if (v != null) {
            GenericCacheEntry genericCacheEntry = (GenericCacheEntry)v;
            genericCacheEntry._value = object2;
            genericCacheEntry._key = object;
            return;
        }
        if (!this.isFull()) {
            n = this._numEntries++;
        } else {
            n = (int)((float)this._cache.length * this.__random.nextFloat());
            this._table.remove(this._cache[n]._key);
        }
        this._cache[n]._value = object2;
        this._cache[n]._key = object;
        this._table.put(object, this._cache[n]);
    }
}

