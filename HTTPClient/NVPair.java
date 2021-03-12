/*
 * Decompiled with CFR 0.150.
 */
package HTTPClient;

public final class NVPair {
    private String name;
    private String value;
    private boolean m_quoteValue;

    NVPair() {
        this("", "");
    }

    public NVPair(NVPair nVPair) {
        this(nVPair.name, nVPair.value);
    }

    public NVPair(String string, String string2) {
        this(string, string2, true);
    }

    public NVPair(String string, String string2, boolean bl) {
        this.name = string;
        this.value = string2;
        this.m_quoteValue = bl;
    }

    public final String getName() {
        return this.name;
    }

    public final String getValue() {
        return this.value;
    }

    public boolean quoteValue() {
        return this.m_quoteValue;
    }

    public String toString() {
        return this.getClass().getName() + "[name=" + this.name + ",value=" + this.value + "]";
    }
}

