/*
 * Decompiled with CFR 0.150.
 */
package anon.tor.ordescription;

import HTTPClient.HTTPConnection;
import HTTPClient.HTTPResponse;
import anon.infoservice.HTTPConnectionFactory;
import anon.infoservice.ListenerInterface;
import anon.tor.ordescription.ORListFetcher;
import logging.LogHolder;
import logging.LogType;

public final class PlainORListFetcher
implements ORListFetcher {
    private String m_ORListServer;
    private int m_ORListPort;

    public PlainORListFetcher(String string, int n) {
        this.m_ORListServer = string;
        this.m_ORListPort = n;
    }

    public byte[] getRouterStatus() {
        return this.getDocument("/tor/server/authority.z");
    }

    public byte[] getDescriptor(String string) {
        return this.getDocument("/tor/server/d/" + string + ".z");
    }

    public byte[] getDescriptorByFingerprint(String string) {
        return this.getDocument("/tor/server/fp/" + string + ".z");
    }

    public byte[] getAllDescriptors() {
        return this.getDocument("/tor/server/all.z");
    }

    public byte[] getStatus(String string) {
        return this.getDocument("/tor/server/fp/" + string + ".z");
    }

    private byte[] getDocument(String string) {
        try {
            LogHolder.log(7, LogType.TOR, "fetching " + string + " from directory server");
            HTTPConnection hTTPConnection = HTTPConnectionFactory.getInstance().createHTTPConnection(new ListenerInterface(this.m_ORListServer, this.m_ORListPort), 1, true);
            HTTPResponse hTTPResponse = hTTPConnection.Get(string);
            if (hTTPResponse.getStatusCode() != 200) {
                return null;
            }
            byte[] arrby = hTTPResponse.getData();
            if (arrby.length <= 0) {
                return null;
            }
            return arrby;
        }
        catch (Throwable throwable) {
            LogHolder.log(7, LogType.TOR, "error while fetching " + string + " from directory server: " + throwable.getMessage());
            return null;
        }
    }
}

