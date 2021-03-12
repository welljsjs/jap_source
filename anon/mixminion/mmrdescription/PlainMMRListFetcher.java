/*
 * Decompiled with CFR 0.150.
 */
package anon.mixminion.mmrdescription;

import HTTPClient.HTTPConnection;
import HTTPClient.HTTPResponse;
import anon.mixminion.mmrdescription.MMRListFetcher;
import logging.LogHolder;
import logging.LogType;

public class PlainMMRListFetcher
implements MMRListFetcher {
    private String m_MMRListServer = "mixminion.net";
    private int m_MMRListPort = 80;

    public byte[] getMMRList() {
        try {
            LogHolder.log(7, LogType.MISC, "[UPDATE OR-LIST] Starting update on " + this.m_MMRListServer + ":" + this.m_MMRListPort);
            HTTPConnection hTTPConnection = new HTTPConnection(this.m_MMRListServer, this.m_MMRListPort);
            HTTPResponse hTTPResponse = hTTPConnection.Get("/directory/Directory.gz");
            if (hTTPResponse.getStatusCode() != 200) {
                return null;
            }
            byte[] arrby = hTTPResponse.getData();
            LogHolder.log(7, LogType.MISC, "[UPDATE OR-LIST] Update finished");
            return arrby;
        }
        catch (Throwable throwable) {
            LogHolder.log(7, LogType.MISC, "There was a problem with fetching the available MMRRouters: " + throwable.getMessage());
            return null;
        }
    }
}

