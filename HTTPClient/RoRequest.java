/*
 * Decompiled with CFR 0.150.
 */
package HTTPClient;

import HTTPClient.HTTPConnection;
import HTTPClient.HttpOutputStream;
import HTTPClient.NVPair;

public interface RoRequest {
    public HTTPConnection getConnection();

    public String getMethod();

    public String getRequestURI();

    public NVPair[] getHeaders();

    public byte[] getData();

    public HttpOutputStream getStream();
}

