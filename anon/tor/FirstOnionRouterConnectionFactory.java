/*
 * Decompiled with CFR 0.150.
 */
package anon.tor;

import anon.tor.FirstOnionRouterConnection;
import anon.tor.Tor;
import anon.tor.ordescription.ORDescriptor;
import java.util.Vector;
import logging.LogHolder;
import logging.LogType;

public class FirstOnionRouterConnectionFactory {
    private Vector m_firstOnionRouters = new Vector();
    private Tor m_Tor;

    public FirstOnionRouterConnectionFactory(Tor tor) {
        this.m_Tor = tor;
    }

    public synchronized FirstOnionRouterConnection createFirstOnionRouterConnection(ORDescriptor oRDescriptor) {
        FirstOnionRouterConnection firstOnionRouterConnection = null;
        for (int i = 0; i < this.m_firstOnionRouters.size(); ++i) {
            firstOnionRouterConnection = (FirstOnionRouterConnection)this.m_firstOnionRouters.elementAt(i);
            ORDescriptor oRDescriptor2 = firstOnionRouterConnection.getORDescription();
            if (oRDescriptor2.isSimilar(oRDescriptor)) {
                if (firstOnionRouterConnection.isClosed()) break;
                return firstOnionRouterConnection;
            }
            firstOnionRouterConnection = null;
        }
        if (firstOnionRouterConnection == null) {
            firstOnionRouterConnection = new FirstOnionRouterConnection(oRDescriptor, this.m_Tor);
        }
        try {
            firstOnionRouterConnection.connect();
        }
        catch (Exception exception) {
            LogHolder.log(2, LogType.TOR, "Error while connection to first OnionRouter");
            LogHolder.log(2, LogType.TOR, exception);
            return null;
        }
        this.m_firstOnionRouters.addElement(firstOnionRouterConnection);
        return firstOnionRouterConnection;
    }

    public synchronized void closeAll() {
        for (int i = 0; i < this.m_firstOnionRouters.size(); ++i) {
            FirstOnionRouterConnection firstOnionRouterConnection = (FirstOnionRouterConnection)this.m_firstOnionRouters.elementAt(i);
            firstOnionRouterConnection.close();
        }
        this.m_firstOnionRouters.removeAllElements();
    }
}

