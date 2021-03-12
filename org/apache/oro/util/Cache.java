/*
 * Decompiled with CFR 0.150.
 */
package org.apache.oro.util;

public interface Cache {
    public void addElement(Object var1, Object var2);

    public Object getElement(Object var1);

    public int size();

    public int capacity();
}

