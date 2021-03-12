/*
 * Decompiled with CFR 0.150.
 */
package org.apache.oro.util;

import org.apache.oro.util.GenericCache;
import org.apache.oro.util.GenericCacheEntry;

public final class CacheFIFO
extends GenericCache {
    private int __curent = 0;

    public CacheFIFO(int n) {
        super(n);
    }

    public CacheFIFO() {
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
            n = this.__curent++;
            if (this.__curent >= this._cache.length) {
                this.__curent = 0;
            }
            this._table.remove(this._cache[n]._key);
        }
        this._cache[n]._value = object2;
        this._cache[n]._key = object;
        this._table.put(object, this._cache[n]);
    }
}

