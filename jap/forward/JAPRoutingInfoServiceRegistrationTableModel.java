/*
 * Decompiled with CFR 0.150.
 */
package jap.forward;

import anon.forward.server.ServerSocketPropagandist;
import anon.util.JAPMessages;
import java.util.Enumeration;
import java.util.Observable;
import java.util.Observer;
import java.util.Vector;
import javax.swing.table.AbstractTableModel;

public class JAPRoutingInfoServiceRegistrationTableModel
extends AbstractTableModel
implements Observer {
    private static final long serialVersionUID = 1L;
    private Vector m_propagandaInstances = new Vector();

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void updatePropagandaInstancesList(Vector vector) {
        Enumeration enumeration = vector.elements();
        Vector vector2 = this.m_propagandaInstances;
        synchronized (vector2) {
            int n = 0;
            while (enumeration.hasMoreElements()) {
                ServerSocketPropagandist serverSocketPropagandist = (ServerSocketPropagandist)enumeration.nextElement();
                if (this.m_propagandaInstances.contains(serverSocketPropagandist)) continue;
                serverSocketPropagandist.addObserver(this);
                if (serverSocketPropagandist.getCurrentState() != 3) {
                    this.m_propagandaInstances.addElement(serverSocketPropagandist);
                    ++n;
                    continue;
                }
                serverSocketPropagandist.deleteObserver(this);
            }
            if (n > 0) {
                this.fireTableRowsInserted(this.m_propagandaInstances.size() - n, this.m_propagandaInstances.size() - 1);
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void update(Observable observable, Object object) {
        Vector vector = this.m_propagandaInstances;
        synchronized (vector) {
            if (this.m_propagandaInstances.contains(observable)) {
                if (((ServerSocketPropagandist)observable).getCurrentState() == 3) {
                    observable.deleteObserver(this);
                    int n = this.m_propagandaInstances.indexOf(observable);
                    this.m_propagandaInstances.removeElement(observable);
                    this.fireTableRowsDeleted(n, n);
                } else {
                    int n = this.m_propagandaInstances.indexOf(observable);
                    this.fireTableRowsUpdated(n, n);
                }
            }
        }
    }

    public int getRowCount() {
        return this.m_propagandaInstances.size();
    }

    public int getColumnCount() {
        return 2;
    }

    public String getColumnName(int n) {
        String string = null;
        if (n == 0) {
            string = JAPMessages.getString("routingInfoServiceRegistrationTableColumn0Name");
        }
        if (n == 1) {
            string = JAPMessages.getString("routingInfoServiceRegistrationTableColumn1Name");
        }
        return string;
    }

    public Object getValueAt(int n, int n2) {
        String string = null;
        ServerSocketPropagandist serverSocketPropagandist = (ServerSocketPropagandist)this.m_propagandaInstances.elementAt(n);
        if (n2 == 0) {
            string = serverSocketPropagandist.getInfoService().getName();
        }
        if (n2 == 1) {
            if (serverSocketPropagandist.getCurrentState() == 0) {
                string = JAPMessages.getString("routingInfoServiceRegistrationTableStateRegistrated");
            }
            if (serverSocketPropagandist.getCurrentState() == 1) {
                string = JAPMessages.getString("routingInfoServiceRegistrationTableStateConnecting");
            }
            if (serverSocketPropagandist.getCurrentState() == 2) {
                string = JAPMessages.getString("routingInfoServiceRegistrationTableStateReconnecting");
            }
            if (serverSocketPropagandist.getCurrentState() == 3) {
                string = JAPMessages.getString("routingInfoServiceRegistrationTableStateHalted");
            }
        }
        return string;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void clearPropagandaInstancesTable() {
        Vector vector = this.m_propagandaInstances;
        synchronized (vector) {
            int n = this.m_propagandaInstances.size();
            if (n > 0) {
                Enumeration enumeration = this.m_propagandaInstances.elements();
                while (enumeration.hasMoreElements()) {
                    ServerSocketPropagandist serverSocketPropagandist = (ServerSocketPropagandist)enumeration.nextElement();
                    serverSocketPropagandist.deleteObserver(this);
                }
                this.m_propagandaInstances.removeAllElements();
                this.fireTableRowsDeleted(0, n - 1);
            }
        }
    }
}

