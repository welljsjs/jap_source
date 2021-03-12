/*
 * Decompiled with CFR 0.150.
 */
package anon.mixminion;

import anon.AnonServerDescription;

public class MixminionServiceDescription
implements AnonServerDescription {
    private int m_iRouteLen;
    private String m_myEmail;

    public MixminionServiceDescription(int n, String string) {
        this.setRouteLen(n);
        this.m_myEmail = string;
    }

    public int getRouteLen() {
        return this.m_iRouteLen;
    }

    public void setRouteLen(int n) {
        if (n >= 2 && n <= 10) {
            this.m_iRouteLen = n;
        }
    }

    public String getMyEmail() {
        return this.m_myEmail;
    }
}

