/*
 * Decompiled with CFR 0.150.
 */
package anon.pay.xml;

public class XMLAuthenticationSuccess {
    private static final byte[] XML_AUTH_SUCCESS = "<?xml version=\"1.0\" ?><Authentication>Success</Authentication>".getBytes();

    public static byte[] getXMLByteArray() {
        return XML_AUTH_SUCCESS;
    }
}

