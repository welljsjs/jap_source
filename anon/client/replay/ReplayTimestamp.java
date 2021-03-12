/*
 * Decompiled with CFR 0.150.
 */
package anon.client.replay;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class ReplayTimestamp {
    private static final long SECONDS_PER_INTERVAL = 600L;
    private String m_mixId;
    private long m_replayReferenceTime;

    public ReplayTimestamp(String string, int n, int n2) {
        this.m_mixId = string;
        this.m_replayReferenceTime = System.currentTimeMillis() - ((long)n * 600L + (long)n2) * 1000L;
    }

    public String getMixId() {
        return this.m_mixId;
    }

    public byte[] getCurrentTimestamp() {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        DataOutputStream dataOutputStream = new DataOutputStream(byteArrayOutputStream);
        try {
            dataOutputStream.writeShort((int)((System.currentTimeMillis() - this.m_replayReferenceTime) / 600000L));
            dataOutputStream.flush();
        }
        catch (IOException iOException) {
            // empty catch block
        }
        return byteArrayOutputStream.toByteArray();
    }
}

