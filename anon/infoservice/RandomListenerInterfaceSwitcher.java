/*
 * Decompiled with CFR 0.150.
 */
package anon.infoservice;

import anon.crypto.MyRandom;
import anon.infoservice.ListenerInterface;
import java.util.Vector;

public class RandomListenerInterfaceSwitcher {
    private Vector m_vecListenerInterfaces;
    private Vector m_vecTried = new Vector();
    private Vector m_vecRemaining;
    private Vector m_vecTimeOutPorts;
    private int m_iPreferredPort;
    private int m_iMaxTried;
    private MyRandom m_random;

    public RandomListenerInterfaceSwitcher(Vector vector, int n, int n2, Vector vector2) {
        this.m_vecListenerInterfaces = vector != null ? (Vector)vector.clone() : new Vector();
        this.m_vecTimeOutPorts = vector2 != null ? vector2 : new Vector();
        this.m_iPreferredPort = Math.min(n, 65535);
        this.m_iMaxTried = Math.max(1, n2);
        this.m_random = new MyRandom();
    }

    private boolean containsPort(Vector vector, int n) {
        for (int i = 0; i < vector.size(); ++i) {
            if (!(vector.elementAt(i) instanceof Integer ? (Integer)vector.elementAt(i) == n : ((ListenerInterface)vector.elementAt(i)).getPort() == n)) continue;
            return true;
        }
        return false;
    }

    private ListenerInterface getNextRandomInterface() {
        if (this.m_vecRemaining.size() == 0) {
            return null;
        }
        int n = this.m_random.nextInt(this.m_vecRemaining.size());
        ListenerInterface listenerInterface = (ListenerInterface)this.m_vecRemaining.elementAt(n);
        this.m_vecRemaining.removeElementAt(n);
        return listenerInterface;
    }

    private void resetRandomInterfaces() {
        if (this.m_vecListenerInterfaces == null) {
            return;
        }
        this.m_vecRemaining = (Vector)this.m_vecListenerInterfaces.clone();
        block0: for (int i = 0; i < this.m_vecTried.size(); ++i) {
            for (int j = 0; j < this.m_vecRemaining.size(); ++j) {
                if (((ListenerInterface)this.m_vecRemaining.elementAt(j)).getPort() != ((Integer)this.m_vecTried.elementAt(i)).intValue()) continue;
                this.m_vecRemaining.removeElementAt(j);
                continue block0;
            }
        }
    }

    public synchronized ListenerInterface getNextInterface() {
        ListenerInterface listenerInterface;
        boolean bl = false;
        this.resetRandomInterfaces();
        while (true) {
            if (this.m_vecTried.size() >= this.m_iMaxTried) {
                return null;
            }
            listenerInterface = this.getNextRandomInterface();
            if (listenerInterface == null) {
                ++this.m_iMaxTried;
                if (this.m_iPreferredPort > 0 && !this.m_vecTried.contains(new Integer(this.m_iPreferredPort))) {
                    this.m_vecTried.addElement(new Integer(this.m_iPreferredPort));
                    this.resetRandomInterfaces();
                    continue;
                }
                if (!this.m_vecTried.contains(new Integer(443))) {
                    this.m_vecTried.addElement(new Integer(443));
                    this.resetRandomInterfaces();
                    continue;
                }
                if (!this.m_vecTried.contains(new Integer(80))) {
                    this.m_vecTried.addElement(new Integer(80));
                    this.resetRandomInterfaces();
                    continue;
                }
                return null;
            }
            if (!this.containsPort(this.m_vecTimeOutPorts, this.m_iPreferredPort) && this.m_iPreferredPort > 0 && this.containsPort(this.m_vecListenerInterfaces, this.m_iPreferredPort) && !this.m_vecTried.contains(new Integer(this.m_iPreferredPort))) {
                if (listenerInterface.getPort() != this.m_iPreferredPort) continue;
                bl = true;
            }
            if (!bl && !this.containsPort(this.m_vecTimeOutPorts, 443) && this.containsPort(this.m_vecListenerInterfaces, 443) && !this.m_vecTried.contains(new Integer(443))) {
                if (listenerInterface.getPort() != 443) continue;
                bl = true;
            }
            if (!bl && this.containsPort(this.m_vecListenerInterfaces, 80) && !this.containsPort(this.m_vecTimeOutPorts, 80) && !this.m_vecTried.contains(new Integer(80))) {
                if (listenerInterface.getPort() != 80) continue;
                bl = true;
            }
            this.m_vecTried.addElement(new Integer(listenerInterface.getPort()));
            if (listenerInterface.isValid()) break;
            ++this.m_iMaxTried;
        }
        if (this.m_vecTimeOutPorts.size() >= 2) {
            this.m_vecTimeOutPorts.removeAllElements();
        }
        return listenerInterface;
    }
}

