/*
 * Decompiled with CFR 0.150.
 */
package anon.proxy;

import anon.infoservice.HttpResponseStructure;
import anon.proxy.ProxyCallbackNotProcessableException;

public class HTTPHeaderParseException
extends ProxyCallbackNotProcessableException {
    private int errorCode = 0;
    private String statusMessage = "";
    private String errorDescription = "";

    public HTTPHeaderParseException(int n, int n2) {
        this.errorCode = n;
        if (n2 == 0) {
            switch (n) {
                default: 
            }
            this.statusMessage = "400 Bad Request";
        } else {
            switch (n) {
                default: 
            }
            this.statusMessage = "500 Internal Server Error";
        }
    }

    public HTTPHeaderParseException(int n, int n2, String string) {
        this(n, n2);
        this.errorDescription = string;
    }

    public String getMessage() {
        String string = "<html><head><title>" + this.statusMessage + "</title></head>" + "<body><h1>" + this.statusMessage + "</h1>" + (this.errorDescription.equals("") ? "" : "<p>" + this.errorDescription + "</p>") + "</html>";
        return string;
    }

    public byte[] getErrorResponse() {
        return new HttpResponseStructure(this.errorCode, this.getMessage()).getResponseData();
    }
}

