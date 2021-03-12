/*
 * Decompiled with CFR 0.150.
 */
package org.bouncycastle.util;

public class Integers {
    public static int rotateLeft(int n, int n2) {
        return n << n2 ^ n >>> -n2;
    }

    public static int rotateRight(int n, int n2) {
        return n >>> n2 ^ n << -n2;
    }

    public static Integer valueOf(int n) {
        return new Integer(n);
    }
}

