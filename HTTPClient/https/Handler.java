/*
 * Decompiled with CFR 0.150.
 */
package HTTPClient.https;

import HTTPClient.HTTPConnection;
import HTTPClient.HttpURLConnection;
import HTTPClient.ProtocolNotSuppException;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;

public class Handler
extends URLStreamHandler {
    public Handler() throws ProtocolNotSuppException {
        new HTTPConnection("https", "", -1);
    }

    public URLConnection openConnection(URL uRL) throws IOException, ProtocolNotSuppException {
        return new HttpURLConnection(uRL);
    }
}

