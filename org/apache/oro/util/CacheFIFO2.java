/*
 * Decompiled with CFR 0.150.
 */
package org.apache.oro.util;

import org.apache.oro.util.GenericCache;
import org.apache.oro.util.GenericCacheEntry;

public final class CacheFIFO2
extends GenericCache {
    private int __current = 0;
    private boolean[] __tryAgain = new boolean[this._cache.length];

    public CacheFIFO2(int n) {
        super(n);
    }

    public CacheFIFO2() {
        this(20);
    }

    public synchronized Object getElement(Object object) {
        Object v = this._table.get(object);
        if (v != null) {
            GenericCacheEntry genericCacheEntry = (GenericCacheEntry)v;
            this.__tryAgain[genericCacheEntry._index] = true;
            return genericCacheEntry._value;
        }
        return null;
    }

    public final synchronized void addElement(Object object, Object object2) {
        int n;
        Object v = this._table.get(object);
        if (v != null) {
            GenericCacheEntry genericCacheEntry = (GenericCacheEntry)v;
            genericCacheEntry._value = object2;
            genericCacheEntry._key = object;
            this.__tryAgain[genericCacheEntry._index] = true;
            return;
        }
        if (!this.isFull()) {
            n = this._numEntries++;
        } else {
            n = this.__current;
            while (this.__tryAgain[n]) {
                this.__tryAgain[n] = false;
                if (++n < this.__tryAgain.length) continue;
                n = 0;
            }
            this.__current = n + 1;
            if (this.__current >= this._cache.length) {
                this.__current = 0;
            }
            this._table.remove(this._cache[n]._key);
        }
        this._cache[n]._value = object2;
        this._cache[n]._key = object;
        this._table.put(object, this._cache[n]);
    }
}

