/*
 * Decompiled with CFR 0.150.
 */
package com.skype;

import java.util.Hashtable;

class SkypeObject {
    private Hashtable userDataMap = new Hashtable();

    SkypeObject() {
    }

    public final Object getData(String string) {
        return this.userDataMap.get(string);
    }

    public final void setData(String string, Object object) {
        this.userDataMap.put(string, object);
    }

    void copyFrom(Object object) {
        if (object instanceof SkypeObject) {
            this.userDataMap.putAll(((SkypeObject)object).userDataMap);
        }
    }
}

