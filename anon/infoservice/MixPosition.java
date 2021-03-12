/*
 * Decompiled with CFR 0.150.
 */
package anon.infoservice;

public class MixPosition {
    private int m_position;
    private String m_MixId;

    public MixPosition(int n, String string) {
        this.m_position = n;
        this.m_MixId = string;
    }

    public int getPosition() {
        return this.m_position;
    }

    public String getId() {
        return this.m_MixId;
    }

    public String toString() {
        return this.m_MixId;
    }

    public boolean equals(Object object) {
        if (object == null || !(object instanceof MixPosition)) {
            return false;
        }
        return this == object || this.getId().equals(((MixPosition)object).getId());
    }

    public int hashCode() {
        return this.m_MixId.hashCode();
    }
}

