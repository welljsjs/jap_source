/*
 * Decompiled with CFR 0.150.
 */
package anon.util;

public class IntegerVariable {
    private int m_integer;

    public IntegerVariable(int n) {
        this.m_integer = n;
    }

    public void set(int n) {
        this.m_integer = n;
    }

    public int get() {
        return this.m_integer;
    }

    public int intValue() {
        return this.m_integer;
    }

    public String toString() {
        return new Integer(this.m_integer).toString();
    }
}

