/*
 * Decompiled with CFR 0.150.
 */
package anon.util;

public abstract class AbstractMessage {
    private int m_messageCode;
    private Object m_messageData;

    protected AbstractMessage(int n) {
        this(n, null);
    }

    protected AbstractMessage(int n, Object object) {
        this.m_messageCode = n;
        this.m_messageData = object;
    }

    public int getMessageCode() {
        return this.m_messageCode;
    }

    public Object getMessageData() {
        return this.m_messageData;
    }
}

