/*
 * Decompiled with CFR 0.150.
 */
package jap.forward;

import anon.forward.server.ForwardServerManager;
import anon.infoservice.InfoServiceHolder;
import anon.infoservice.MixCascade;
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

public final class JAPRoutingUseableMixCascades
extends Observable
implements Observer,
Runnable {
    private static final long MIXCASCADELIST_UPDATE_INTERVAL = 600000L;
    Hashtable m_allowedMixCascades = new Hashtable();
    boolean m_allowAllAvailableCascades = true;
    Hashtable m_currentlyRunningMixCascades = new Hashtable();
    Thread m_updateMixCascadesListThread = null;

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void update(Observable observable, Object object) {
        block10: {
            if (observable == JAPModel.getInstance().getRoutingSettings()) {
                try {
                    if (((JAPRoutingMessage)object).getMessageCode() != 1) break block10;
                    JAPRoutingUseableMixCascades jAPRoutingUseableMixCascades = this;
                    synchronized (jAPRoutingUseableMixCascades) {
                        if (JAPModel.getInstance().getRoutingSettings().getRoutingMode() == 2 || JAPModel.getInstance().getRoutingSettings().getForwarderAddress().getTransportIdentifier().equals("local")) {
                            if (this.m_updateMixCascadesListThread == null) {
                                this.startMixCascadesListUpdateThread();
                            }
                        } else if (this.m_updateMixCascadesListThread != null) {
                            this.stopMixCascadesListUpdateThread();
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
    public void setAllowedMixCascades(Vector vector) {
        Object object = this.m_allowedMixCascades;
        synchronized (object) {
            this.m_allowedMixCascades.clear();
            Enumeration enumeration = vector.elements();
            while (enumeration.hasMoreElements()) {
                MixCascade mixCascade = (MixCascade)enumeration.nextElement();
                this.m_allowedMixCascades.put(mixCascade.getId(), mixCascade);
            }
        }
        object = this;
        synchronized (object) {
            if (this.m_updateMixCascadesListThread != null && !this.m_allowAllAvailableCascades) {
                this.updateUseableCascadesDatabase();
            }
            this.setChanged();
            this.notifyObservers(new JAPRoutingMessage(10));
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void addToAllowedMixCascades(MixCascade mixCascade) {
        if (mixCascade != null) {
            Object object = this.m_allowedMixCascades;
            synchronized (object) {
                this.m_allowedMixCascades.put(mixCascade.getId(), mixCascade);
            }
            object = this;
            synchronized (object) {
                if (this.m_updateMixCascadesListThread != null && !this.m_allowAllAvailableCascades) {
                    this.updateUseableCascadesDatabase();
                }
                this.setChanged();
                this.notifyObservers(new JAPRoutingMessage(10));
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void removeFromAllowedMixCascades(String string) {
        if (string != null) {
            boolean bl = false;
            Object object = this.m_allowedMixCascades;
            synchronized (object) {
                if (this.m_allowedMixCascades.remove(string) != null) {
                    bl = true;
                }
            }
            if (bl) {
                object = this;
                synchronized (object) {
                    if (this.m_updateMixCascadesListThread != null && !this.m_allowAllAvailableCascades) {
                        this.updateUseableCascadesDatabase();
                    }
                    this.setChanged();
                    this.notifyObservers(new JAPRoutingMessage(10));
                }
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public Vector getAllowedMixCascades() {
        Vector vector = new Vector();
        Hashtable hashtable = this.m_allowedMixCascades;
        synchronized (hashtable) {
            Enumeration enumeration = this.m_allowedMixCascades.elements();
            while (enumeration.hasMoreElements()) {
                vector.addElement(enumeration.nextElement());
            }
        }
        return vector;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void setAllowAllAvailableMixCascades(boolean bl) {
        JAPRoutingUseableMixCascades jAPRoutingUseableMixCascades = this;
        synchronized (jAPRoutingUseableMixCascades) {
            if (this.m_allowAllAvailableCascades != bl) {
                this.m_allowAllAvailableCascades = bl;
                if (this.m_updateMixCascadesListThread != null) {
                    this.updateUseableCascadesDatabase();
                }
                this.setChanged();
                this.notifyObservers(new JAPRoutingMessage(9));
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public boolean getAllowAllAvailableMixCascades() {
        boolean bl = false;
        JAPRoutingUseableMixCascades jAPRoutingUseableMixCascades = this;
        synchronized (jAPRoutingUseableMixCascades) {
            bl = this.m_allowAllAvailableCascades;
        }
        return bl;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public Element getSettingsAsXml(Document document) {
        Element element = document.createElement("AllowedMixCascadesSettings");
        Element element2 = document.createElement("AllowAllAvailableMixCascades");
        Element element3 = document.createElement("AllowedMixCascades");
        JAPRoutingUseableMixCascades jAPRoutingUseableMixCascades = this;
        synchronized (jAPRoutingUseableMixCascades) {
            XMLUtil.setValue((Node)element2, this.getAllowAllAvailableMixCascades());
            Enumeration enumeration = this.getAllowedMixCascades().elements();
            while (enumeration.hasMoreElements()) {
                element3.appendChild(((MixCascade)enumeration.nextElement()).toXmlElement(document));
            }
        }
        element.appendChild(element2);
        element.appendChild(element3);
        return element;
    }

    public boolean loadSettingsFromXml(Element element) {
        boolean bl = true;
        Element element2 = (Element)XMLUtil.getFirstChildByName(element, "AllowAllAvailableMixCascades");
        if (element2 == null) {
            LogHolder.log(3, LogType.MISC, "JAPRoutingUseableMixCascades: loadSettingsFromXml: Error in XML structure (AllowAllAvailableMixCascades node): Using default setting.");
            bl = false;
        } else {
            this.setAllowAllAvailableMixCascades(XMLUtil.parseValue((Node)element2, this.getAllowAllAvailableMixCascades()));
        }
        Element element3 = (Element)XMLUtil.getFirstChildByName(element, "AllowedMixCascades");
        if (element3 == null) {
            LogHolder.log(3, LogType.MISC, "Error in XML structure (AllowedMixCascades node): Skip loading of allowed mixcascades.");
            bl = false;
        } else {
            NodeList nodeList = element3.getElementsByTagName("MixCascade");
            Vector<MixCascade> vector = new Vector<MixCascade>();
            for (int i = 0; i < nodeList.getLength(); ++i) {
                Element element4 = (Element)nodeList.item(i);
                try {
                    MixCascade mixCascade = new MixCascade(element4);
                    vector.addElement(mixCascade);
                    continue;
                }
                catch (Exception exception) {
                    LogHolder.log(3, LogType.MISC, "Error while loading one allowed MixCascade: Skipping this MixCascade (" + exception.toString() + ").");
                    bl = false;
                }
            }
            this.setAllowedMixCascades(vector);
        }
        return bl;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void run() {
        Hashtable hashtable;
        boolean bl = false;
        while (!bl && !Thread.currentThread().isInterrupted()) {
            hashtable = InfoServiceHolder.getInstance().getMixCascades();
            if (hashtable == null) {
                hashtable = new Hashtable();
            }
            Object object = this.m_currentlyRunningMixCascades;
            synchronized (object) {
                this.m_currentlyRunningMixCascades.clear();
                Enumeration enumeration = hashtable.elements();
                while (enumeration.hasMoreElements()) {
                    MixCascade mixCascade = (MixCascade)enumeration.nextElement();
                    this.m_currentlyRunningMixCascades.put(mixCascade.getId(), mixCascade);
                }
            }
            this.updateUseableCascadesDatabase();
            object = this.m_updateMixCascadesListThread;
            synchronized (object) {
                bl = Thread.interrupted();
                if (!bl) {
                    try {
                        this.m_updateMixCascadesListThread.wait(600000L);
                    }
                    catch (Exception exception) {
                        bl = true;
                    }
                }
            }
        }
        hashtable = this.m_currentlyRunningMixCascades;
        synchronized (hashtable) {
            this.m_currentlyRunningMixCascades.clear();
        }
        ForwardServerManager.getInstance().getAllowedCascadesDatabase().removeAllCascades();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void updateUseableCascadesDatabase() {
        Hashtable hashtable = this.m_currentlyRunningMixCascades;
        synchronized (hashtable) {
            Hashtable hashtable2 = this.m_allowedMixCascades;
            synchronized (hashtable2) {
                Object object;
                boolean bl = this.m_allowAllAvailableCascades;
                Enumeration enumeration = this.m_currentlyRunningMixCascades.elements();
                while (enumeration.hasMoreElements()) {
                    object = (MixCascade)enumeration.nextElement();
                    if (bl) {
                        ForwardServerManager.getInstance().getAllowedCascadesDatabase().addCascade((MixCascade)object);
                        continue;
                    }
                    if (!this.m_allowedMixCascades.containsKey(((MixCascade)object).getId())) continue;
                    ForwardServerManager.getInstance().getAllowedCascadesDatabase().addCascade((MixCascade)object);
                }
                object = ForwardServerManager.getInstance().getAllowedCascadesDatabase().getEntryList().elements();
                while (object.hasMoreElements()) {
                    MixCascade mixCascade = (MixCascade)object.nextElement();
                    if (this.m_currentlyRunningMixCascades.containsKey(mixCascade.getId())) {
                        if (bl || this.m_allowedMixCascades.containsKey(mixCascade.getId())) continue;
                        ForwardServerManager.getInstance().getAllowedCascadesDatabase().removeCascade(mixCascade.getId());
                        continue;
                    }
                    ForwardServerManager.getInstance().getAllowedCascadesDatabase().removeCascade(mixCascade.getId());
                }
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void startMixCascadesListUpdateThread() {
        JAPRoutingUseableMixCascades jAPRoutingUseableMixCascades = this;
        synchronized (jAPRoutingUseableMixCascades) {
            if (this.m_updateMixCascadesListThread == null) {
                LogHolder.log(6, LogType.MISC, "JAPRoutingUseableMixCascades: startMixCascadesListUpdateThread: The mixcascade management thread of the forwarding server is started.");
                this.m_updateMixCascadesListThread = new Thread(this);
                this.m_updateMixCascadesListThread.setDaemon(true);
                this.m_updateMixCascadesListThread.start();
            } else {
                LogHolder.log(6, LogType.MISC, "JAPRoutingUseableMixCascades: startMixCascadesListUpdateThread: The mixcascade management thread of the forwarding server was already started.");
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void stopMixCascadesListUpdateThread() {
        LogHolder.log(6, LogType.MISC, "JAPRoutingUseableMixCascades: stopMixCascadesListUpdateThread: Shutdown the mixcascade management thread of the forwarding server...");
        JAPRoutingUseableMixCascades jAPRoutingUseableMixCascades = this;
        synchronized (jAPRoutingUseableMixCascades) {
            if (this.m_updateMixCascadesListThread != null) {
                Thread thread = this.m_updateMixCascadesListThread;
                synchronized (thread) {
                    this.m_updateMixCascadesListThread.interrupt();
                }
                try {
                    this.m_updateMixCascadesListThread.join();
                    LogHolder.log(6, LogType.MISC, "JAPRoutingUseableMixCascades: stopMixCascadesListUpdateThread: Mixcascade management thread of the forwarding server halted.");
                }
                catch (Exception exception) {
                    // empty catch block
                }
                this.m_updateMixCascadesListThread = null;
            } else {
                LogHolder.log(6, LogType.MISC, "JAPRoutingUseableMixCascades: stopMixCascadesListUpdateThread: The mixcascade management thread of the forwarding server was not running.");
            }
        }
    }
}

