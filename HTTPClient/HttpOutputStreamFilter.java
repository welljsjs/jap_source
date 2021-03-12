/*
 * Decompiled with CFR 0.150.
 */
package HTTPClient;

import HTTPClient.RoRequest;
import java.io.OutputStream;

public interface HttpOutputStreamFilter {
    public OutputStream pushStream(OutputStream var1, RoRequest var2);
}

