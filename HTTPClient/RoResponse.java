/*
 * Decompiled with CFR 0.150.
 */
package HTTPClient;

import java.io.IOException;
import java.io.InputStream;
import java.util.Date;

public interface RoResponse {
    public int getStatusCode() throws IOException;

    public String getReasonLine() throws IOException;

    public String getVersion() throws IOException;

    public String getHeader(String var1) throws IOException;

    public int getHeaderAsInt(String var1) throws IOException, NumberFormatException;

    public Date getHeaderAsDate(String var1) throws IOException, IllegalArgumentException;

    public String getTrailer(String var1) throws IOException;

    public int getTrailerAsInt(String var1) throws IOException, NumberFormatException;

    public Date getTrailerAsDate(String var1) throws IOException, IllegalArgumentException;

    public byte[] getData() throws IOException;

    public InputStream getInputStream() throws IOException;

    public boolean retryRequest();
}

