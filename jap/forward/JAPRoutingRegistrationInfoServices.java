/*
 * Decompiled with CFR 0.150.
 */
package jap.forward;

import anon.forward.server.ServerSocketPropagandist;
import anon.infoservice.Database;
import anon.infoservice.InfoServiceDBEntry;
import anon.infoservice.InfoServiceHolder;
import anon.util.XMLUtil;
import jap.JAPModel;
import jap.forward.JAPRoutingMessage;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Observable;
import java.util.Observer;
import java.util.Vector;
import logging.LogHolder;
import logging.LogType;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class JAPRoutingRegistrationInfoServices
extends Observable
implements Observer,
Runnable {
    private static final long INFOSERVICELIST_UPDATE_INTERVAL = 600000L;
    Hashtable m_registrationInfoServices = new Hashtable();
    boolean m_registerAtAllAvailableInfoServices = true;
    boolean m_propagandaIsRunning = false;
    Vector m_runningInfoServiceRegistrations = new Vector();
    Thread m_updateInfoServiceListThread = null;
    static /* synthetic */ Class class$anon$infoservice$InfoServiceDBEntry;

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void update(Observable observable, Object object) {
        block23: {
            if (observable == JAPModel.getInstance().getRoutingSettings()) {
                try {
                    Vector vector;
                    JAPRoutingRegistrationInfoServices jAPRoutingRegistrationInfoServices;
                    if (((JAPRoutingMessage)object).getMessageCode() == 2) {
                        jAPRoutingRegistrationInfoServices = this;
                        synchronized (jAPRoutingRegistrationInfoServices) {
                            vector = this.m_runningInfoServiceRegistrations;
                            synchronized (vector) {
                                Enumeration enumeration = ((Vector)((JAPRoutingMessage)object).getMessageData()).elements();
                                while (enumeration.hasMoreElements()) {
                                    InfoServiceDBEntry infoServiceDBEntry = ((ServerSocketPropagandist)enumeration.nextElement()).getInfoService();
                                    if (this.m_runningInfoServiceRegistrations.contains(infoServiceDBEntry.getId())) continue;
                                    this.m_runningInfoServiceRegistrations.addElement(infoServiceDBEntry.getId());
                                }
                            }
                        }
                    }
                    if (((JAPRoutingMessage)object).getMessageCode() == 4) {
                        jAPRoutingRegistrationInfoServices = this;
                        synchronized (jAPRoutingRegistrationInfoServices) {
                            this.m_propagandaIsRunning = true;
                            if (this.m_registerAtAllAvailableInfoServices) {
                                this.startInfoServiceListUpdateThread();
                            }
                        }
                    }
                    if (((JAPRoutingMessage)object).getMessageCode() != 5) break block23;
                    jAPRoutingRegistrationInfoServices = this;
                    synchronized (jAPRoutingRegistrationInfoServices) {
                        if (this.m_registerAtAllAvailableInfoServices) {
                            this.stopInfoServiceListUpdateThread();
                        }
                        this.m_propagandaIsRunning = false;
                        vector = this.m_runningInfoServiceRegistrations;
                        synchronized (vector) {
                            this.m_runningInfoServiceRegistrations.removeAllElements();
                        }
                    }
                }
                catch (Exception exception) {
                    // empty catch block
                }
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void setRegistrationInfoServices(Vector vector) {
        Object object;
        Object object2;
        Object object3 = this.m_registrationInfoServices;
        synchronized (object3) {
            this.m_registrationInfoServices.clear();
            object2 = vector.elements();
            while (object2.hasMoreElements()) {
                object = (InfoServiceDBEntry)object2.nextElement();
                if (!((InfoServiceDBEntry)object).hasPrimaryForwarderList()) continue;
                this.m_registrationInfoServices.put(((InfoServiceDBEntry)object).getId(), object);
            }
        }
        object3 = this;
        synchronized (object3) {
            if (!this.m_registerAtAllAvailableInfoServices) {
                object2 = this.m_runningInfoServiceRegistrations;
                synchronized (object2) {
                    object = this.m_registrationInfoServices.elements();
                    while (object.hasMoreElements()) {
                        InfoServiceDBEntry infoServiceDBEntry = (InfoServiceDBEntry)object.nextElement();
                        if (this.m_runningInfoServiceRegistrations.contains(infoServiceDBEntry.getId())) continue;
                        JAPModel.getInstance().getRoutingSettings().addPropagandaInstance(infoServiceDBEntry);
                        if (!this.m_propagandaIsRunning) continue;
                        this.m_runningInfoServiceRegistrations.addElement(infoServiceDBEntry.getId());
                    }
                }
            }
            this.setChanged();
            this.notifyObservers(new JAPRoutingMessage(12));
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void addToRegistrationInfoServices(InfoServiceDBEntry infoServiceDBEntry) {
        if (infoServiceDBEntry != null && infoServiceDBEntry.hasPrimaryForwarderList()) {
            Object object = this.m_registrationInfoServices;
            synchronized (object) {
                this.m_registrationInfoServices.put(infoServiceDBEntry.getId(), infoServiceDBEntry);
            }
            object = this;
            synchronized (object) {
                if (!this.m_registerAtAllAvailableInfoServices) {
                    Vector vector = this.m_runningInfoServiceRegistrations;
                    synchronized (vector) {
                        if (!this.m_runningInfoServiceRegistrations.contains(infoServiceDBEntry.getId())) {
                            JAPModel.getInstance().getRoutingSettings().addPropagandaInstance(infoServiceDBEntry);
                            if (this.m_propagandaIsRunning) {
                                this.m_runningInfoServiceRegistrations.addElement(infoServiceDBEntry.getId());
                            }
                        }
                    }
                }
                this.setChanged();
                this.notifyObservers(new JAPRoutingMessage(12));
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void removeFromRegistrationInfoServices(String string) {
        if (string != null) {
            boolean bl = false;
            Object object = this.m_registrationInfoServices;
            synchronized (object) {
                if (this.m_registrationInfoServices.remove(string) != null) {
                    bl = true;
                }
            }
            if (bl) {
                object = this;
                synchronized (object) {
                    this.setChanged();
                    this.notifyObservers(new JAPRoutingMessage(12));
                }
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public Vector getRegistrationInfoServices() {
        Vector vector = new Vector();
        Hashtable hashtable = this.m_registrationInfoServices;
        synchronized (hashtable) {
            Enumeration enumeration = this.m_registrationInfoServices.elements();
            while (enumeration.hasMoreElements()) {
                vector.addElement(enumeration.nextElement());
            }
        }
        return vector;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public Vector getRegistrationInfoServicesForStartup() {
        Vector vector = new Vector();
        JAPRoutingRegistrationInfoServices jAPRoutingRegistrationInfoServices = this;
        synchronized (jAPRoutingRegistrationInfoServices) {
            vector = this.m_registerAtAllAvailableInfoServices ? InfoServiceHolder.getInstance().getInfoservicesWithForwarderList() : this.getRegistrationInfoServices();
        }
        return vector;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void setRegisterAtAllAvailableInfoServices(boolean bl) {
        JAPRoutingRegistrationInfoServices jAPRoutingRegistrationInfoServices = this;
        synchronized (jAPRoutingRegistrationInfoServices) {
            if (this.m_registerAtAllAvailableInfoServices != bl) {
                this.m_registerAtAllAvailableInfoServices = bl;
                if (this.m_propagandaIsRunning) {
                    if (bl) {
                        this.startInfoServiceListUpdateThread();
                    } else {
                        this.stopInfoServiceListUpdateThread();
                    }
                }
                this.setChanged();
                this.notifyObservers(new JAPRoutingMessage(11));
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public boolean getRegisterAtAllAvailableInfoServices() {
        boolean bl = false;
        JAPRoutingRegistrationInfoServices jAPRoutingRegistrationInfoServices = this;
        synchronized (jAPRoutingRegistrationInfoServices) {
            bl = this.m_registerAtAllAvailableInfoServices;
        }
        return bl;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public Element getSettingsAsXml(Document document) {
        Element element = document.createElement("InfoServiceRegistrationSettings");
        Element element2 = document.createElement("UseAllPrimaryInfoServices");
        Element element3 = document.createElement("RegistrationInfoServices");
        JAPRoutingRegistrationInfoServices jAPRoutingRegistrationInfoServices = this;
        synchronized (jAPRoutingRegistrationInfoServices) {
            XMLUtil.setValue((Node)element2, this.getRegisterAtAllAvailableInfoServices());
            Enumeration enumeration = this.getRegistrationInfoServices().elements();
            while (enumeration.hasMoreElements()) {
                element3.appendChild(((InfoServiceDBEntry)enumeration.nextElement()).toXmlElement(document));
            }
        }
        element.appendChild(element2);
        element.appendChild(element3);
        return element;
    }

    public boolean loadSettingsFromXml(Element element) {
        boolean bl = true;
        Element element2 = (Element)XMLUtil.getFirstChildByName(element, "UseAllPrimaryInfoServices");
        if (element2 == null) {
            LogHolder.log(3, LogType.MISC, "JAPRoutingRegistrationInfoServices: loadSettingsFromXml: Error in XML structure (UseAllPrimaryInfoServices node): Using default setting.");
            bl = false;
        } else {
            this.setRegisterAtAllAvailableInfoServices(XMLUtil.parseValue((Node)element2, this.getRegisterAtAllAvailableInfoServices()));
        }
        Element element3 = (Element)XMLUtil.getFirstChildByName(element, "RegistrationInfoServices");
        if (element3 == null) {
            LogHolder.log(3, LogType.MISC, "JAPRoutingRegistrationInfoServices: loadSettingsFromXml: Error in XML structure (RegistrationInfoServices node): Skip loading of registration infoservices.");
            bl = false;
        } else {
            NodeList nodeList = element3.getElementsByTagName("InfoService");
            Vector<InfoServiceDBEntry> vector = new Vector<InfoServiceDBEntry>();
            for (int i = 0; i < nodeList.getLength(); ++i) {
                Element element4 = (Element)nodeList.item(i);
                try {
                    InfoServiceDBEntry infoServiceDBEntry = new InfoServiceDBEntry(element4, Long.MAX_VALUE);
                    InfoServiceDBEntry infoServiceDBEntry2 = (InfoServiceDBEntry)Database.getInstance(class$anon$infoservice$InfoServiceDBEntry == null ? JAPRoutingRegistrationInfoServices.class$("anon.infoservice.InfoServiceDBEntry") : class$anon$infoservice$InfoServiceDBEntry).getEntryById(infoServiceDBEntry.getId());
                    if (infoServiceDBEntry2 != null) {
                        infoServiceDBEntry = infoServiceDBEntry2;
                    }
                    if (infoServiceDBEntry.hasPrimaryForwarderList()) {
                        vector.addElement(infoServiceDBEntry);
                        continue;
                    }
                    LogHolder.log(3, LogType.MISC, "JAPRoutingRegistrationInfoServices: loadSettingsFromXml: Error while loading one registration InfoService: The InfoService " + infoServiceDBEntry.getName() + " has no primary forwarder list: Skipping this infoservice.");
                    bl = false;
                    continue;
                }
                catch (Exception exception) {
                    LogHolder.log(3, LogType.MISC, "JAPRoutingRegistrationInfoServices: loadSettingsFromXml: Error while loading one registration InfoService: Skipping this infoservice (" + exception.toString() + ").");
                    bl = false;
                }
            }
            this.setRegistrationInfoServices(vector);
        }
        return bl;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void run() {
        boolean bl = false;
        while (!bl) {
            Object object = this.m_updateInfoServiceListThread;
            synchronized (object) {
                bl = Thread.interrupted();
                if (!bl) {
                    try {
                        this.m_updateInfoServiceListThread.wait(600000L);
                    }
                    catch (Exception exception) {
                        bl = true;
                    }
                }
            }
            if (bl || (object = InfoServiceHolder.getInstance().getInfoServices()) == null) continue;
            Enumeration enumeration = ((Hashtable)object).elements();
            while (enumeration.hasMoreElements()) {
                InfoServiceDBEntry infoServiceDBEntry = (InfoServiceDBEntry)enumeration.nextElement();
                if (!infoServiceDBEntry.hasPrimaryForwarderList()) continue;
                Vector vector = this.m_runningInfoServiceRegistrations;
                synchronized (vector) {
                    if (!this.m_runningInfoServiceRegistrations.contains(infoServiceDBEntry.getId())) {
                        JAPModel.getInstance().getRoutingSettings().addPropagandaInstance(infoServiceDBEntry);
                        this.m_runningInfoServiceRegistrations.addElement(infoServiceDBEntry.getId());
                    }
                }
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void startInfoServiceListUpdateThread() {
        JAPRoutingRegistrationInfoServices jAPRoutingRegistrationInfoServices = this;
        synchronized (jAPRoutingRegistrationInfoServices) {
            if (this.m_updateInfoServiceListThread == null) {
                LogHolder.log(6, LogType.MISC, "JAPRoutingRegistrationInfoServices: startInfoServiceListUpdateThread: The infoservice registration management thread is started.");
                this.m_updateInfoServiceListThread = new Thread(this);
                this.m_updateInfoServiceListThread.setDaemon(true);
                this.m_updateInfoServiceListThread.start();
            } else {
                LogHolder.log(6, LogType.MISC, "JAPRoutingRegistrationInfoServices: startInfoServiceListUpdateThread: The infoservice registration management thread was already started.");
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void stopInfoServiceListUpdateThread() {
        LogHolder.log(6, LogType.MISC, "JAPRoutingRegistrationInfoServices: stopInfoServiceListUpdateThread: Shutdown the infoservice registration management thread...");
        JAPRoutingRegistrationInfoServices jAPRoutingRegistrationInfoServices = this;
        synchronized (jAPRoutingRegistrationInfoServices) {
            if (this.m_updateInfoServiceListThread != null) {
                Thread thread = this.m_updateInfoServiceListThread;
                synchronized (thread) {
                    this.m_updateInfoServiceListThread.interrupt();
                }
                try {
                    this.m_updateInfoServiceListThread.join();
                    LogHolder.log(6, LogType.MISC, "JAPRoutingRegistrationInfoServices: stopInfoServiceListUpdateThread: Infoservice registration management thread halted.");
                }
                catch (Exception exception) {
                    // empty catch block
                }
                this.m_updateInfoServiceListThread = null;
            } else {
                LogHolder.log(6, LogType.MISC, "JAPRoutingRegistrationInfoServices: stopInfoServiceListUpdateThread: Infoservice registration management thread was not running.");
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

