/*
 * Decompiled with CFR 0.150.
 */
package jap.forward;

import anon.util.XMLUtil;
import jap.JAPModel;
import jap.forward.JAPRoutingConnectionClass;
import jap.forward.JAPRoutingMessage;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Observable;
import java.util.Vector;
import logging.LogHolder;
import logging.LogType;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class JAPRoutingConnectionClassSelector
extends Observable {
    public static final int CONNECTION_CLASS_ISDN64 = 0;
    public static final int CONNECTION_CLASS_ISDN128 = 1;
    public static final int CONNECTION_CLASS_DSL128 = 2;
    public static final int CONNECTION_CLASS_DSL192 = 3;
    public static final int CONNECTION_CLASS_DSL256 = 4;
    public static final int CONNECTION_CLASS_DSL384 = 5;
    public static final int CONNECTION_CLASS_DSL512 = 6;
    public static final int CONNECTION_CLASS_1MBIT = 7;
    public static final int CONNECTION_CLASS_USER = 8;
    private Hashtable m_connectionClasses = new Hashtable();
    private int m_currentConnectionClass;

    public JAPRoutingConnectionClassSelector() {
        this.m_connectionClasses.put(new Integer(0), new JAPRoutingConnectionClass(0, "routingConnectionClassIsdn64", 8000, 50));
        this.m_connectionClasses.put(new Integer(1), new JAPRoutingConnectionClass(1, "routingConnectionClassIsdn128", 16000, 50));
        this.m_connectionClasses.put(new Integer(2), new JAPRoutingConnectionClass(2, "routingConnectionClassDsl128", 16000, 50));
        this.m_connectionClasses.put(new Integer(3), new JAPRoutingConnectionClass(3, "routingConnectionClassDsl192", 24000, 50));
        this.m_connectionClasses.put(new Integer(4), new JAPRoutingConnectionClass(4, "routingConnectionClassDsl256", 32000, 50));
        this.m_connectionClasses.put(new Integer(5), new JAPRoutingConnectionClass(5, "routingConnectionClassDsl384", 48000, 50));
        this.m_connectionClasses.put(new Integer(6), new JAPRoutingConnectionClass(6, "routingConnectionClassDsl512", 64000, 50));
        this.m_connectionClasses.put(new Integer(7), new JAPRoutingConnectionClass(7, "routingConnectionClass1Mbit", 125000, 50));
        this.m_connectionClasses.put(new Integer(8), new JAPRoutingConnectionClass(8, "routingConnectionClassUser", 16000, 50));
        this.m_currentConnectionClass = 2;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public JAPRoutingConnectionClass getCurrentConnectionClass() {
        JAPRoutingConnectionClass jAPRoutingConnectionClass = null;
        Hashtable hashtable = this.m_connectionClasses;
        synchronized (hashtable) {
            jAPRoutingConnectionClass = (JAPRoutingConnectionClass)this.m_connectionClasses.get(new Integer(this.m_currentConnectionClass));
        }
        return jAPRoutingConnectionClass;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void setCurrentConnectionClass(int n) {
        JAPRoutingConnectionClass jAPRoutingConnectionClass = null;
        Hashtable hashtable = this.m_connectionClasses;
        synchronized (hashtable) {
            jAPRoutingConnectionClass = (JAPRoutingConnectionClass)this.m_connectionClasses.get(new Integer(n));
            if (jAPRoutingConnectionClass != null) {
                boolean bl = false;
                boolean bl2 = false;
                if (this.m_currentConnectionClass != n) {
                    bl = true;
                }
                this.m_currentConnectionClass = n;
                if (JAPModel.getInstance().getRoutingSettings().getBandwidth() != jAPRoutingConnectionClass.getCurrentBandwidth() || JAPModel.getInstance().getRoutingSettings().getAllowedConnections() != jAPRoutingConnectionClass.getMaxSimultaneousConnections()) {
                    bl2 = true;
                }
                JAPModel.getInstance().getRoutingSettings().setBandwidth(jAPRoutingConnectionClass.getCurrentBandwidth());
                JAPModel.getInstance().getRoutingSettings().setAllowedConnections(jAPRoutingConnectionClass.getMaxSimultaneousConnections());
                if (bl) {
                    this.setChanged();
                    this.notifyObservers(new JAPRoutingMessage(6));
                }
                if (bl2) {
                    this.setChanged();
                    this.notifyObservers(new JAPRoutingMessage(7));
                }
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public Vector getConnectionClasses() {
        Vector vector = new Vector();
        Hashtable hashtable = this.m_connectionClasses;
        synchronized (hashtable) {
            Enumeration enumeration = this.m_connectionClasses.elements();
            while (enumeration.hasMoreElements()) {
                vector.addElement(enumeration.nextElement());
            }
        }
        return vector;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public Element getSettingsAsXml(Document document) {
        Element element = document.createElement("ConnectionClassSettings");
        Element element2 = document.createElement("ConnectionClasses");
        Element element3 = document.createElement("CurrentConnectionClass");
        Hashtable hashtable = this.m_connectionClasses;
        synchronized (hashtable) {
            Enumeration enumeration = this.getConnectionClasses().elements();
            while (enumeration.hasMoreElements()) {
                element2.appendChild(((JAPRoutingConnectionClass)enumeration.nextElement()).getSettingsAsXml(document));
            }
            XMLUtil.setValue((Node)element3, this.m_currentConnectionClass);
        }
        element.appendChild(element2);
        element.appendChild(element3);
        return element;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public boolean loadSettingsFromXml(Element element) {
        Object object;
        int n;
        Object object2;
        boolean bl = true;
        Element element2 = (Element)XMLUtil.getFirstChildByName(element, "ConnectionClasses");
        if (element2 == null) {
            LogHolder.log(3, LogType.MISC, "JAPRoutingConnectionClassSelector: loadSettingsFromXml: Error in XML structure (ConnectionClasses node): Using default connection classes.");
            bl = false;
        } else {
            object2 = element2.getElementsByTagName("ConnectionClass");
            for (n = 0; n < object2.getLength(); ++n) {
                object = (Element)object2.item(n);
                Element element3 = (Element)XMLUtil.getFirstChildByName((Node)object, "ClassIdentifier");
                if (element3 == null) {
                    LogHolder.log(3, LogType.MISC, "JAPRoutingConnectionClassSelector: loadSettingsFromXml: Error in XML structure (ClassIdentifier node): Skipping this connection class.");
                    bl = false;
                    continue;
                }
                try {
                    int n2 = Integer.parseInt(XMLUtil.parseValue((Node)element3, "NOT_A_NUMBER"));
                    JAPRoutingConnectionClass jAPRoutingConnectionClass = null;
                    Hashtable hashtable = this.m_connectionClasses;
                    synchronized (hashtable) {
                        jAPRoutingConnectionClass = (JAPRoutingConnectionClass)this.m_connectionClasses.get(new Integer(n2));
                    }
                    if (jAPRoutingConnectionClass != null) {
                        bl = jAPRoutingConnectionClass.loadSettingsFromXml((Element)object);
                        continue;
                    }
                    LogHolder.log(3, LogType.MISC, "JAPRoutingConnectionClassSelector: loadSettingsFromXml: The connection class " + Integer.toString(n2) + " is not known in the system. Skipping the entry.");
                    bl = false;
                    continue;
                }
                catch (Exception exception) {
                    LogHolder.log(3, LogType.MISC, "JAPRoutingConnectionClassSelector: loadSettingsFromXml: Error while loading settings for a connection class. Skipping this class. (" + exception.toString() + ")");
                    bl = false;
                }
            }
        }
        object2 = (Element)XMLUtil.getFirstChildByName(element, "CurrentConnectionClass");
        if (object2 == null) {
            LogHolder.log(3, LogType.MISC, "JAPRoutingConnectionClassSelector: loadSettingsFromXml: Error in XML structure (CurrentConnectionClass node): Using default value.");
            bl = false;
        } else {
            try {
                n = Integer.parseInt(XMLUtil.parseValue((Node)object2, "NOT_A_NUMBER"));
                object = this.m_connectionClasses;
                synchronized (object) {
                    if (this.m_connectionClasses.get(new Integer(n)) != null) {
                        this.setCurrentConnectionClass(n);
                    } else {
                        this.setCurrentConnectionClass(this.m_currentConnectionClass);
                        LogHolder.log(3, LogType.MISC, "JAPRoutingConnectionClassSelector: loadSettingsFromXml: The specified current connection class doesn't exist: Using default value.");
                        bl = false;
                    }
                }
            }
            catch (Exception exception) {
                this.setCurrentConnectionClass(this.m_currentConnectionClass);
                LogHolder.log(3, LogType.MISC, "JAPRoutingConnectionClassSelector: loadSettingsFromXml: Invalid value of the current connection class setting: Using default value. (" + exception.toString() + ")");
                bl = false;
            }
        }
        return bl;
    }
}

