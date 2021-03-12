/*
 * Decompiled with CFR 0.150.
 */
package anon.infoservice;

import org.w3c.dom.Document;

public final class HttpRequestStructure {
    public static final int HTTP_COMMAND_GET = 0;
    public static final int HTTP_COMMAND_POST = 1;
    private int m_httpCommand;
    private String m_httpFileName;
    private Document m_postDocument;

    private HttpRequestStructure(int n, String string, Document document) {
        this.m_httpCommand = n;
        this.m_httpFileName = string;
        this.m_postDocument = document;
    }

    public static HttpRequestStructure createGetRequest(String string) {
        return new HttpRequestStructure(0, string, null);
    }

    public static HttpRequestStructure createPostRequest(String string, Document document) {
        return new HttpRequestStructure(1, string, document);
    }

    public int getRequestCommand() {
        return this.m_httpCommand;
    }

    public String getRequestFileName() {
        return this.m_httpFileName;
    }

    public Document getRequestPostDocument() {
        return this.m_postDocument;
    }
}

