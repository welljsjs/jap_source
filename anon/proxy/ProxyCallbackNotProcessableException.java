/*
 * Decompiled with CFR 0.150.
 */
package anon.proxy;

public class ProxyCallbackNotProcessableException
extends Exception {
    public ProxyCallbackNotProcessableException() {
    }

    public ProxyCallbackNotProcessableException(String string) {
        super(string);
    }

    public byte[] getErrorResponse() {
        return this.getMessage().getBytes();
    }
}

