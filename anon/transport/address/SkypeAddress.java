/*
 * Decompiled with CFR 0.150.
 */
package anon.transport.address;

import anon.transport.address.AddressMappingException;
import anon.transport.address.AddressParameter;
import anon.transport.address.Endpoint;
import anon.transport.address.IAddress;

public class SkypeAddress
implements IAddress {
    public static final String TRANSPORT_IDENTIFIER = "skype";
    private static final String USER_PARAMETER = "user";
    private static final String APP_PARAMETER = "application";
    protected String m_user;
    protected String m_app;

    public SkypeAddress(String string, String string2) {
        this.m_user = string;
        this.m_app = string2;
    }

    public SkypeAddress(Endpoint endpoint) throws AddressMappingException {
        this.m_user = endpoint.getParameter(USER_PARAMETER);
        if (this.m_user == null) {
            throw new AddressMappingException("User-ID Parameter is missing");
        }
        this.m_app = endpoint.getParameter(APP_PARAMETER);
        if (this.m_app == null) {
            throw new AddressMappingException("Applicationname Parameter is missing");
        }
    }

    public String getUserID() {
        return this.m_user;
    }

    public String getApplicationName() {
        return this.m_app;
    }

    public String getTransportIdentifier() {
        return TRANSPORT_IDENTIFIER;
    }

    public AddressParameter[] getAllParameters() {
        AddressParameter[] arraddressParameter = new AddressParameter[]{new AddressParameter(USER_PARAMETER, this.m_user), new AddressParameter(APP_PARAMETER, this.m_app)};
        return arraddressParameter;
    }

    public boolean equals(Object object) {
        if (object == null) {
            return false;
        }
        if (!(object instanceof SkypeAddress)) {
            return false;
        }
        SkypeAddress skypeAddress = (SkypeAddress)object;
        boolean bl = true;
        bl = this.m_user != null ? (bl &= this.m_user.equals(skypeAddress.getUserID())) : (bl &= skypeAddress.getUserID() == null);
        bl = this.m_app != null ? (bl &= this.m_app.equals(skypeAddress.getUserID())) : (bl &= skypeAddress.getApplicationName() == null);
        return bl;
    }
}

