/*
 * Decompiled with CFR 0.150.
 */
package org.apache.log4j;

import java.util.Hashtable;

public class MDC {
    static final MDC mdc = new MDC();
    static final int HT_SIZE = 7;

    private MDC() {
    }

    public static Object get(String string) {
        return mdc.get0(string);
    }

    public static void remove(String string) {
        mdc.remove0(string);
    }

    public static Hashtable getContext() {
        return mdc.getContext0();
    }

    private void put0(String string, Object object) {
    }

    private Object get0(String string) {
        return null;
    }

    private void remove0(String string) {
    }

    private Hashtable getContext0() {
        return null;
    }
}

