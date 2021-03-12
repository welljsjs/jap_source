/*
 * Decompiled with CFR 0.150.
 */
package anon.transport.address;

public class AddressParameter {
    private String m_name;
    private String m_value;

    public AddressParameter(String string, String string2) {
        this.m_name = string;
        this.m_value = string2;
    }

    public AddressParameter(String string) {
        this(string, "");
    }

    public String getName() {
        return this.m_name;
    }

    public String getValue() {
        return this.m_value;
    }

    public int hashCode() {
        return this.m_name.hashCode();
    }
}

