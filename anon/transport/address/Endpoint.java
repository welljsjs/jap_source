/*
 * Decompiled with CFR 0.150.
 */
package anon.transport.address;

import anon.transport.address.AddressParameter;
import anon.transport.address.IAddress;
import anon.transport.address.MalformedURNException;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.StringTokenizer;

public class Endpoint {
    protected String m_transportIdentifier;
    protected Hashtable m_paramters;

    public String getTransportIdentifier() {
        return this.m_transportIdentifier;
    }

    public static String toURN(IAddress iAddress) {
        String string = iAddress.getTransportIdentifier();
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("urn:endpoint:");
        stringBuffer.append(string);
        AddressParameter[] arraddressParameter = iAddress.getAllParameters();
        int n = arraddressParameter.length;
        for (int i = 0; i < n; ++i) {
            stringBuffer.append(":");
            stringBuffer.append(arraddressParameter[i].getName());
            stringBuffer.append("(");
            stringBuffer.append(arraddressParameter[i].getValue());
            stringBuffer.append(")");
        }
        return stringBuffer.toString();
    }

    public Endpoint(String string) throws MalformedURNException {
        StringTokenizer stringTokenizer = new StringTokenizer(string, ":");
        String[] arrstring = new String[stringTokenizer.countTokens()];
        int n = 0;
        while (stringTokenizer.hasMoreElements()) {
            arrstring[n++] = stringTokenizer.nextToken();
        }
        if (arrstring.length < 3) {
            throw new MalformedURNException("A valid Endpoint needs at least 3 Components");
        }
        if (!arrstring[0].equals("urn")) {
            throw new MalformedURNException("URN must start with \"urn:\"");
        }
        if (!arrstring[1].equals("endpoint")) {
            throw new MalformedURNException("Can only handle Endpoint-Namespace. Is " + arrstring[1]);
        }
        this.m_transportIdentifier = arrstring[2];
        this.m_paramters = new Hashtable();
        int n2 = arrstring.length;
        for (n = 3; n < n2; ++n) {
            int n3 = arrstring[n].indexOf("(");
            int n4 = arrstring[n].length() - 1;
            String string2 = arrstring[n].substring(0, n3);
            String string3 = arrstring[n].substring(++n3, n4);
            this.m_paramters.put(string2, new AddressParameter(string2, string3));
        }
    }

    public String getParameter(String string) {
        AddressParameter addressParameter = (AddressParameter)this.m_paramters.get(string);
        return addressParameter == null ? null : addressParameter.getValue();
    }

    public AddressParameter[] getAllParameters() {
        AddressParameter[] arraddressParameter = new AddressParameter[this.m_paramters.size()];
        Enumeration enumeration = this.m_paramters.elements();
        int n = 0;
        while (enumeration.hasMoreElements()) {
            arraddressParameter[n++] = (AddressParameter)enumeration.nextElement();
        }
        return arraddressParameter;
    }
}

