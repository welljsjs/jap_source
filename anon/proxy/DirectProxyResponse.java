/*
 * Decompiled with CFR 0.150.
 */
package anon.proxy;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import logging.LogHolder;
import logging.LogType;

final class DirectProxyResponse
implements Runnable {
    private int m_threadNumber;
    private static int ms_threadCount;
    private OutputStream m_outputStream;
    private InputStream m_inputStream;

    public DirectProxyResponse(InputStream inputStream, OutputStream outputStream) {
        this.m_inputStream = inputStream;
        this.m_outputStream = outputStream;
    }

    public void run() {
        this.m_threadNumber = this.getThreadNumber();
        LogHolder.log(7, LogType.NET, "R(" + this.m_threadNumber + ") - Response thread started.");
        try {
            int n;
            byte[] arrby = new byte[1000];
            while ((n = this.m_inputStream.read(arrby)) != -1) {
                if (n <= 0) continue;
                if (LogHolder.isLogged(7, LogType.NET)) {
                    LogHolder.log(7, LogType.NET, "R(" + this.m_threadNumber + ") - " + new String(arrby, 0, n));
                }
                this.m_outputStream.write(arrby, 0, n);
                this.m_outputStream.flush();
            }
            LogHolder.log(7, LogType.NET, "R(" + this.m_threadNumber + ") - EOF from Server.");
        }
        catch (IOException iOException) {
        }
        catch (Exception exception) {
            LogHolder.log(3, LogType.NET, "R(" + this.m_threadNumber + ") - Exception during transmission: " + exception);
        }
        try {
            this.m_inputStream.close();
            this.m_outputStream.close();
        }
        catch (Exception exception) {
            LogHolder.log(2, LogType.NET, "R(" + this.m_threadNumber + ") - Exception while closing: " + exception.toString());
        }
        LogHolder.log(7, LogType.NET, "R(" + this.m_threadNumber + ") - Response thread stopped.");
    }

    private synchronized int getThreadNumber() {
        return ms_threadCount++;
    }
}

