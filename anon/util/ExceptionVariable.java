/*
 * Decompiled with CFR 0.150.
 */
package anon.util;

public class ExceptionVariable {
    private Exception m_exception;

    public ExceptionVariable(Exception exception) {
        this.m_exception = exception;
    }

    public void set(Exception exception) {
        this.m_exception = exception;
    }

    public Exception get() {
        return this.m_exception;
    }
}

