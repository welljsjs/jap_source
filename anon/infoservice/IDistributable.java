/*
 * Decompiled with CFR 0.150.
 */
package anon.infoservice;

public interface IDistributable {
    public static final String FIELD_HTTP_REQUEST_STRING = "HTTP_REQUEST_STRING";
    public static final String FIELD_HTTP_SERIALS_REQUEST_STRING = "HTTP_SERIALS_REQUEST_STRING";

    public String getId();

    public String getPostFile();

    public int getPostEncoding();

    public byte[] getPostData();
}

