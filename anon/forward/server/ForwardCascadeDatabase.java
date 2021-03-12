/*
 * Decompiled with CFR 0.150.
 */
package anon.forward.server;

import anon.infoservice.Database;
import anon.infoservice.MixCascade;
import anon.util.XMLUtil;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;
import logging.LogHolder;
import logging.LogType;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class ForwardCascadeDatabase {
    private Hashtable m_allowedCascades = new Hashtable();
    private boolean m_bInitialized = false;
    static /* synthetic */ Class class$anon$infoservice$MixCascade;

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public MixCascade getMixCascadeById(String string) {
        Hashtable hashtable = this.m_allowedCascades;
        synchronized (hashtable) {
            if (!this.m_bInitialized) {
                return (MixCascade)Database.getInstance(class$anon$infoservice$MixCascade == null ? (class$anon$infoservice$MixCascade = ForwardCascadeDatabase.class$("anon.infoservice.MixCascade")) : class$anon$infoservice$MixCascade).getEntryById(string);
            }
            return (MixCascade)this.m_allowedCascades.get(string);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public Element toXmlNode(Document document) {
        Element element = document.createElement("AllowedCascades");
        Hashtable hashtable = this.m_allowedCascades;
        synchronized (hashtable) {
            Enumeration enumeration = !this.m_bInitialized ? Database.getInstance(class$anon$infoservice$MixCascade == null ? (class$anon$infoservice$MixCascade = ForwardCascadeDatabase.class$("anon.infoservice.MixCascade")) : class$anon$infoservice$MixCascade).getEntrySnapshotAsEnumeration() : this.m_allowedCascades.elements();
            while (enumeration.hasMoreElements()) {
                MixCascade mixCascade = (MixCascade)enumeration.nextElement();
                try {
                    element.appendChild(mixCascade.toXmlElement(document));
                }
                catch (Exception exception) {
                    LogHolder.log(2, LogType.MISC, "Cascade: " + mixCascade.getName() + " XML: '" + XMLUtil.toString(mixCascade.toXmlElement(document)) + "'", exception);
                }
            }
        }
        return element;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public Vector getEntryList() {
        Vector vector = new Vector();
        Hashtable hashtable = this.m_allowedCascades;
        synchronized (hashtable) {
            if (!this.m_bInitialized) {
                return Database.getInstance(class$anon$infoservice$MixCascade == null ? (class$anon$infoservice$MixCascade = ForwardCascadeDatabase.class$("anon.infoservice.MixCascade")) : class$anon$infoservice$MixCascade).getEntryList();
            }
            Enumeration enumeration = this.m_allowedCascades.elements();
            while (enumeration.hasMoreElements()) {
                vector.addElement(enumeration.nextElement());
            }
        }
        return vector;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void addCascade(MixCascade mixCascade) {
        Hashtable hashtable = this.m_allowedCascades;
        synchronized (hashtable) {
            this.m_bInitialized = true;
            if (!this.m_allowedCascades.containsKey(mixCascade.getId())) {
                LogHolder.log(6, LogType.MISC, "ForwardCascadeDatabase: addCascade: The mixcascade " + mixCascade.getMixNames() + " was added to the list of useable cascades for the clients.");
            }
            this.m_allowedCascades.put(mixCascade.getId(), mixCascade);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void removeCascade(String string) {
        Hashtable hashtable = this.m_allowedCascades;
        synchronized (hashtable) {
            MixCascade mixCascade = (MixCascade)this.m_allowedCascades.get(string);
            if (mixCascade != null) {
                LogHolder.log(6, LogType.MISC, "ForwardCascadeDatabase: removeCascade: The mixcascade " + mixCascade.getMixNames() + " was removed from the list of useable cascades for the clients.");
            }
            this.m_allowedCascades.remove(string);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void removeAllCascades() {
        Hashtable hashtable = this.m_allowedCascades;
        synchronized (hashtable) {
            LogHolder.log(6, LogType.MISC, "ForwardCascadeDatabase: removeAllCascades: All mixcascades were removed from the list of useable cascades for the clients.");
            this.m_allowedCascades.clear();
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

