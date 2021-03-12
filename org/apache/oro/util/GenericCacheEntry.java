/*
 * Decompiled with CFR 0.150.
 */
package org.apache.oro.util;

import java.io.Serializable;

final class GenericCacheEntry
implements Serializable {
    int _index;
    Object _value;
    Object _key;

    GenericCacheEntry(int n) {
        this._index = n;
        this._value = null;
        this._key = null;
    }
}

