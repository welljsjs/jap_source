/*
 * Decompiled with CFR 0.150.
 */
package org.apache.oro.util;

import org.apache.oro.util.GenericCache;
import org.apache.oro.util.GenericCacheEntry;

public final class CacheLRU
extends GenericCache {
    private int __head = 0;
    private int __tail = 0;
    private int[] __next = new int[this._cache.length];
    private int[] __prev = new int[this._cache.length];

    public CacheLRU(int n) {
        super(n);
        for (int i = 0; i < this.__next.length; ++i) {
            this.__prev[i] = -1;
            this.__next[i] = -1;
        }
    }

    public CacheLRU() {
        this(20);
    }

    private void __moveToFront(int n) {
        if (this.__head != n) {
            int n2 = this.__next[n];
            int n3 = this.__prev[n];
            this.__next[n3] = n2;
            if (n2 >= 0) {
                this.__prev[n2] = n3;
            } else {
                this.__tail = n3;
            }
            this.__prev[n] = -1;
            this.__next[n] = this.__head;
            this.__prev[this.__head] = n;
            this.__head = n;
        }
    }

    public synchronized Object getElement(Object object) {
        Object v = this._table.get(object);
        if (v != null) {
            GenericCacheEntry genericCacheEntry = (GenericCacheEntry)v;
            this.__moveToFront(genericCacheEntry._index);
            return genericCacheEntry._value;
        }
        return null;
    }

    public final synchronized void addElement(Object object, Object object2) {
        Object v = this._table.get(object);
        if (v != null) {
            GenericCacheEntry genericCacheEntry = (GenericCacheEntry)v;
            genericCacheEntry._value = object2;
            genericCacheEntry._key = object;
            this.__moveToFront(genericCacheEntry._index);
            return;
        }
        if (!this.isFull()) {
            if (this._numEntries > 0) {
                this.__prev[this._numEntries] = this.__tail;
                this.__next[this._numEntries] = -1;
                this.__moveToFront(this._numEntries);
            }
            ++this._numEntries;
        } else {
            this._table.remove(this._cache[this.__tail]._key);
            this.__moveToFront(this.__tail);
        }
        this._cache[this.__head]._value = object2;
        this._cache[this.__head]._key = object;
        this._table.put(object, this._cache[this.__head]);
    }
}

