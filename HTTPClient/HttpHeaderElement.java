/*
 * Decompiled with CFR 0.150.
 */
package HTTPClient;

import HTTPClient.NVPair;
import HTTPClient.Util;

public class HttpHeaderElement {
    private String name;
    private String value;
    private NVPair[] parameters;

    public HttpHeaderElement(String string) {
        this.name = string;
        this.value = null;
        this.parameters = new NVPair[0];
    }

    public HttpHeaderElement(String string, String string2, NVPair[] arrnVPair) {
        this.name = string;
        this.value = string2;
        if (arrnVPair != null) {
            this.parameters = new NVPair[arrnVPair.length];
            System.arraycopy(arrnVPair, 0, this.parameters, 0, arrnVPair.length);
        } else {
            this.parameters = new NVPair[0];
        }
    }

    public String getName() {
        return this.name;
    }

    public String getValue() {
        return this.value;
    }

    public NVPair[] getParams() {
        return this.parameters;
    }

    public boolean equals(Object object) {
        if (object != null && object instanceof HttpHeaderElement) {
            String string = ((HttpHeaderElement)object).name;
            return this.name.equalsIgnoreCase(string);
        }
        return false;
    }

    public String toString() {
        StringBuffer stringBuffer = new StringBuffer();
        this.appendTo(stringBuffer);
        return stringBuffer.toString();
    }

    public void appendTo(StringBuffer stringBuffer) {
        stringBuffer.append(this.name);
        if (this.value != null) {
            if (Util.needsQuoting(this.value)) {
                stringBuffer.append("=\"");
                stringBuffer.append(Util.quoteString(this.value, "\\\""));
                stringBuffer.append('\"');
            } else {
                stringBuffer.append('=');
                stringBuffer.append(this.value);
            }
        }
        for (int i = 0; i < this.parameters.length; ++i) {
            stringBuffer.append(";");
            stringBuffer.append(this.parameters[i].getName());
            String string = this.parameters[i].getValue();
            if (string == null) continue;
            if (Util.needsQuoting(string)) {
                stringBuffer.append("=\"");
                stringBuffer.append(Util.quoteString(string, "\\\""));
                stringBuffer.append('\"');
                continue;
            }
            stringBuffer.append('=');
            stringBuffer.append(string);
        }
    }
}

