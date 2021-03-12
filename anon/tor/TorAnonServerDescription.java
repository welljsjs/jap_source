/*
 * Decompiled with CFR 0.150.
 */
package anon.tor;

import anon.AnonServerDescription;

public class TorAnonServerDescription
implements AnonServerDescription {
    private int m_iTorDirServerPort;
    private String m_strTorDirServerAddr;
    private final boolean m_bUseInfoService;
    private final boolean m_bStartCircuitsAtStartup;
    private int m_iMaxRouteLen = 5;
    private int m_iMinRouteLen = 2;
    private int m_iMaxConnectionsPerRoute = 1000;

    public TorAnonServerDescription() {
        this.m_strTorDirServerAddr = "moria.seul.org";
        this.m_iTorDirServerPort = 9031;
        this.m_bUseInfoService = false;
        this.m_bStartCircuitsAtStartup = false;
    }

    public TorAnonServerDescription(boolean bl) {
        this(bl, false);
    }

    public TorAnonServerDescription(boolean bl, boolean bl2) {
        if (bl) {
            this.m_strTorDirServerAddr = null;
            this.m_iTorDirServerPort = -1;
            this.m_bUseInfoService = true;
        } else {
            this.m_strTorDirServerAddr = "moria.seul.org";
            this.m_iTorDirServerPort = 9031;
            this.m_bUseInfoService = false;
        }
        this.m_bStartCircuitsAtStartup = bl2;
    }

    public TorAnonServerDescription(String string, int n, boolean bl) {
        this.m_strTorDirServerAddr = string;
        this.m_iTorDirServerPort = n;
        this.m_bUseInfoService = false;
        this.m_bStartCircuitsAtStartup = bl;
    }

    public void setTorDirServer(String string, int n) {
        this.m_strTorDirServerAddr = string;
        this.m_iTorDirServerPort = n;
    }

    public String getTorDirServerAddr() {
        return this.m_strTorDirServerAddr;
    }

    public int getTorDirServerPort() {
        return this.m_iTorDirServerPort;
    }

    public boolean useInfoService() {
        return this.m_bUseInfoService;
    }

    public boolean startCircuitsAtStartup() {
        return this.m_bStartCircuitsAtStartup;
    }

    public void setMaxRouteLen(int n) {
        this.m_iMaxRouteLen = n;
    }

    public int getMaxRouteLen() {
        return this.m_iMaxRouteLen;
    }

    public void setMinRouteLen(int n) {
        this.m_iMinRouteLen = n;
    }

    public int getMinRouteLen() {
        return this.m_iMinRouteLen;
    }

    public void setMaxConnectionsPerRoute(int n) {
        this.m_iMaxConnectionsPerRoute = n;
    }

    public int getMaxConnectionsPerRoute() {
        return this.m_iMaxConnectionsPerRoute;
    }
}

