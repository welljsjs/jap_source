/*
 * Decompiled with CFR 0.150.
 */
package anon.util;

public class BooleanVariable {
    private volatile boolean m_boolean;

    public BooleanVariable(boolean bl) {
        this.m_boolean = bl;
    }

    public void set(boolean bl) {
        this.m_boolean = bl;
    }

    public boolean get() {
        return this.m_boolean;
    }

    public boolean isTrue() {
        return this.m_boolean;
    }

    public String toString() {
        return new Boolean(this.m_boolean).toString();
    }
}

