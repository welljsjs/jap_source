/*
 * Decompiled with CFR 0.150.
 */
package jap.forward;

import anon.util.JAPMessages;
import anon.util.XMLUtil;
import logging.LogHolder;
import logging.LogType;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class JAPRoutingConnectionClass {
    private int m_connectionClassIdentifier;
    private String m_connectionClassName;
    private int m_maximumBandwidth;
    private int m_relativeBandwidth;

    public JAPRoutingConnectionClass(int n, String string, int n2, int n3) {
        this.m_connectionClassIdentifier = n;
        this.m_connectionClassName = string;
        this.m_maximumBandwidth = n2;
        this.setRelativeBandwidth(n3);
    }

    public int getIdentifier() {
        return this.m_connectionClassIdentifier;
    }

    public int getMaximumBandwidth() {
        return this.m_maximumBandwidth;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void setMaximumBandwidth(int n) {
        if (this.m_connectionClassIdentifier == 8) {
            JAPRoutingConnectionClass jAPRoutingConnectionClass = this;
            synchronized (jAPRoutingConnectionClass) {
                this.m_maximumBandwidth = n;
                this.setRelativeBandwidth(this.getRelativeBandwidth());
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public int getCurrentBandwidth() {
        int n = 0;
        JAPRoutingConnectionClass jAPRoutingConnectionClass = this;
        synchronized (jAPRoutingConnectionClass) {
            n = this.m_maximumBandwidth * this.m_relativeBandwidth / 100;
        }
        return n;
    }

    public int getMaxSimultaneousConnections() {
        return this.getCurrentBandwidth() / 4000;
    }

    public int getRelativeBandwidth() {
        return this.m_relativeBandwidth;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void setRelativeBandwidth(int n) {
        JAPRoutingConnectionClass jAPRoutingConnectionClass = this;
        synchronized (jAPRoutingConnectionClass) {
            this.m_relativeBandwidth = n > this.getMinimumRelativeBandwidth() ? n : this.getMinimumRelativeBandwidth();
        }
    }

    public int getMinimumRelativeBandwidth() {
        int n = this.m_maximumBandwidth;
        return (400000 + (n - 1)) / n;
    }

    public String toString() {
        return JAPMessages.getString(this.m_connectionClassName);
    }

    public synchronized Element getSettingsAsXml(Document document) {
        Element element = document.createElement("ConnectionClass");
        Element element2 = document.createElement("ClassIdentifier");
        Element element3 = document.createElement("MaximumBandwidth");
        Element element4 = document.createElement("RelativeBandwidth");
        XMLUtil.setValue((Node)element2, this.getIdentifier());
        XMLUtil.setValue((Node)element3, this.getMaximumBandwidth());
        XMLUtil.setValue((Node)element4, this.getRelativeBandwidth());
        element.appendChild(element2);
        element.appendChild(element3);
        element.appendChild(element4);
        return element;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    public boolean loadSettingsFromXml(Element element) {
        boolean bl = true;
        JAPRoutingConnectionClass jAPRoutingConnectionClass = this;
        synchronized (jAPRoutingConnectionClass) {
            try {
                if (XMLUtil.parseValue(XMLUtil.getFirstChildByName(element, "ClassIdentifier"), this.m_connectionClassIdentifier + 1) != this.m_connectionClassIdentifier) {
                    throw new Exception("JAPRoutingConnectionClass: loadSettingsFromXml: The class identifer doesn't match to this class (class: " + Integer.toString(this.m_connectionClassIdentifier) + ").");
                }
                if (this.m_connectionClassIdentifier == 8) {
                    int n = XMLUtil.parseValue(XMLUtil.getFirstChildByName(element, "MaximumBandwidth"), -1);
                    if (n < 4000) throw new Exception("JAPRoutingConnectionClass: loadSettingsFromXml: Invalid maximum bandwidth value (class: " + Integer.toString(this.m_connectionClassIdentifier) + ").");
                    this.m_maximumBandwidth = n;
                    this.setRelativeBandwidth(50);
                } else if (XMLUtil.parseValue(XMLUtil.getFirstChildByName(element, "MaximumBandwidth"), this.m_maximumBandwidth + 1) != this.m_maximumBandwidth) {
                    throw new Exception("JAPRoutingConnectionClass: loadSettingsFromXml: The maximum bandwidth doesn't match to this class (class: " + Integer.toString(this.m_connectionClassIdentifier) + ").");
                }
            }
            catch (Exception exception) {
                LogHolder.log(3, LogType.NET, "JAPRoutingConnectionClass: loadSettingsFromXml: Loading the settings for this connection class failed: " + exception.toString());
                bl = false;
            }
            bl = true;
            if (!true) return bl;
            Element element2 = (Element)XMLUtil.getFirstChildByName(element, "RelativeBandwidth");
            if (element2 == null) {
                LogHolder.log(3, LogType.MISC, "JAPRoutingConnectionClass: loadSettingsFromXml: Error in XML structure (RelativeBandwidth node for class " + Integer.toString(this.m_connectionClassIdentifier) + "): Using default value.");
                return false;
            }
            int n = XMLUtil.parseValue((Node)element2, -1);
            if (n < this.getMinimumRelativeBandwidth()) {
                LogHolder.log(3, LogType.MISC, "JAPRoutingConnectionClass: loadSettingsFromXml: Invalid relative bandwidth value for class " + Integer.toString(this.m_connectionClassIdentifier) + ": Using default value.");
                return false;
            }
            this.setRelativeBandwidth(n);
            return bl;
        }
    }
}

