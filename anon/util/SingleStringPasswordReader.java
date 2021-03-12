/*
 * Decompiled with CFR 0.150.
 */
package anon.util;

import anon.util.IMiscPasswordReader;

public class SingleStringPasswordReader
implements IMiscPasswordReader {
    private char[] m_password = null;

    public SingleStringPasswordReader(String string) {
        if (string == null) {
            this.m_password = null;
            return;
        }
        this.m_password = string.toCharArray();
    }

    public SingleStringPasswordReader(char[] arrc) {
        if (arrc == null) {
            this.m_password = null;
            return;
        }
        this.m_password = new char[arrc.length];
        System.arraycopy(arrc, 0, this.m_password, 0, arrc.length);
    }

    public String readPassword(Object object) {
        if (this.m_password == null) {
            return null;
        }
        String string = new String(this.m_password);
        this.clear();
        return string;
    }

    public void clear() {
        if (this.m_password != null) {
            for (int i = 0; i < this.m_password.length; ++i) {
                this.m_password[i] = '\u0000';
            }
            this.m_password = null;
        }
    }
}

