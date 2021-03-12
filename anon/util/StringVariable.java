/*
 * Decompiled with CFR 0.150.
 */
package anon.util;

public class StringVariable {
    private String m_string;

    public StringVariable(String string) {
        this.m_string = string;
    }

    public StringVariable() {
        this.m_string = null;
    }

    public void set(String string) {
        this.m_string = string;
    }

    public String get() {
        return this.m_string;
    }

    public String toString() {
        return this.m_string;
    }
}

