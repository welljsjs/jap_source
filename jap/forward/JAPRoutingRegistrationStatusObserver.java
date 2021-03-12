/*
 * Decompiled with CFR 0.150.
 */
package jap.forward;

import anon.forward.server.ServerSocketPropagandist;
import jap.JAPModel;
import jap.forward.JAPRoutingMessage;
import java.util.Enumeration;
import java.util.Observable;
import java.util.Observer;
import java.util.Vector;

public class JAPRoutingRegistrationStatusObserver
extends Observable
implements Observer {
    public static final int STATE_DISABLED = 0;
    public static final int STATE_INITIAL_REGISTRATION = 1;
    public static final int STATE_NO_REGISTRATION = 2;
    public static final int STATE_SUCCESSFUL_REGISTRATION = 3;
    public static final int ERROR_NO_ERROR = 0;
    public static final int ERROR_NO_KNOWN_PRIMARY_INFOSERVICES = 1;
    public static final int ERROR_INFOSERVICE_CONNECT_ERROR = 2;
    public static final int ERROR_VERIFICATION_ERROR = 3;
    public static final int ERROR_UNKNOWN_ERROR = 4;
    private Vector m_propagandaInstances = new Vector();
    private int m_currentState = 0;
    private int m_currentErrorCode = 0;
    static /* synthetic */ Class class$anon$forward$server$ServerSocketPropagandist;

    public int getCurrentState() {
        return this.m_currentState;
    }

    public int getCurrentErrorCode() {
        return this.m_currentErrorCode;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void update(Observable observable, Object object) {
        block20: {
            if (observable.getClass().equals(class$anon$forward$server$ServerSocketPropagandist == null ? (class$anon$forward$server$ServerSocketPropagandist = JAPRoutingRegistrationStatusObserver.class$("anon.forward.server.ServerSocketPropagandist")) : class$anon$forward$server$ServerSocketPropagandist)) {
                Vector vector = this.m_propagandaInstances;
                synchronized (vector) {
                    if (this.m_propagandaInstances.contains(observable) && ((ServerSocketPropagandist)observable).getCurrentState() == 3) {
                        observable.deleteObserver(this);
                        this.m_propagandaInstances.removeElement(observable);
                        this.updateCurrentState(false);
                    }
                }
            }
            try {
                JAPRoutingRegistrationStatusObserver jAPRoutingRegistrationStatusObserver;
                if (observable != JAPModel.getInstance().getRoutingSettings()) break block20;
                boolean bl = false;
                if (((JAPRoutingMessage)object).getMessageCode() == 5) {
                    jAPRoutingRegistrationStatusObserver = this;
                    synchronized (jAPRoutingRegistrationStatusObserver) {
                        if (this.m_currentState != 0) {
                            this.m_currentState = 0;
                            this.m_currentErrorCode = 0;
                            bl = true;
                        }
                    }
                }
                if (((JAPRoutingMessage)object).getMessageCode() == 3) {
                    jAPRoutingRegistrationStatusObserver = this;
                    synchronized (jAPRoutingRegistrationStatusObserver) {
                        if (this.m_currentState != 1) {
                            this.m_currentState = 1;
                            this.m_currentErrorCode = 0;
                            bl = true;
                        }
                    }
                }
                if (((JAPRoutingMessage)object).getMessageCode() == 2) {
                    this.updatePropagandaInstancesList((Vector)((JAPRoutingMessage)object).getMessageData());
                    this.updateCurrentState(false);
                }
                if (((JAPRoutingMessage)object).getMessageCode() == 4) {
                    this.updateCurrentState(true);
                }
                if (bl) {
                    this.setChanged();
                    this.notifyObservers(new JAPRoutingMessage(14));
                }
            }
            catch (Exception exception) {
                // empty catch block
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void updateCurrentState(boolean bl) {
        Vector vector = this.m_propagandaInstances;
        synchronized (vector) {
            JAPRoutingRegistrationStatusObserver jAPRoutingRegistrationStatusObserver = this;
            synchronized (jAPRoutingRegistrationStatusObserver) {
                if (this.m_currentState == 2 || this.m_currentState == 3 || this.m_currentState == 1 && bl) {
                    int n = 2;
                    int n2 = 1;
                    if (this.m_propagandaInstances.size() > 0) {
                        n2 = 4;
                        Enumeration enumeration = this.m_propagandaInstances.elements();
                        while (n != 3 && enumeration.hasMoreElements()) {
                            ServerSocketPropagandist serverSocketPropagandist = (ServerSocketPropagandist)enumeration.nextElement();
                            if (serverSocketPropagandist.getCurrentState() == 0) {
                                n = 3;
                                n2 = 0;
                                continue;
                            }
                            if (serverSocketPropagandist.getCurrentState() != 1 && serverSocketPropagandist.getCurrentState() != 2) continue;
                            if (n2 == 4 && serverSocketPropagandist.getCurrentErrorCode() == 2) {
                                n2 = 2;
                            }
                            if (n2 != 4 && n2 != 2 || serverSocketPropagandist.getCurrentErrorCode() != 1) continue;
                            n2 = 3;
                        }
                    }
                    if (n != this.m_currentState || n2 != this.m_currentErrorCode) {
                        this.m_currentState = n;
                        this.m_currentErrorCode = n2;
                        this.setChanged();
                        this.notifyObservers(new JAPRoutingMessage(14));
                    }
                }
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void updatePropagandaInstancesList(Vector vector) {
        Enumeration enumeration = vector.elements();
        Vector vector2 = this.m_propagandaInstances;
        synchronized (vector2) {
            while (enumeration.hasMoreElements()) {
                ServerSocketPropagandist serverSocketPropagandist = (ServerSocketPropagandist)enumeration.nextElement();
                if (this.m_propagandaInstances.contains(serverSocketPropagandist)) continue;
                serverSocketPropagandist.addObserver(this);
                if (serverSocketPropagandist.getCurrentState() != 3) {
                    this.m_propagandaInstances.addElement(serverSocketPropagandist);
                    continue;
                }
                serverSocketPropagandist.deleteObserver(this);
            }
        }
    }

    static /* synthetic */ Class class$(String string) {
        try {
            return Class.forName(string);
        }
        catch (ClassNotFoundException classNotFoundException) {
            throw new NoClassDefFoundError(classNotFoundException.getMessage());
        }
    }
}

