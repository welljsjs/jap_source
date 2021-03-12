/*
 * Decompiled with CFR 0.150.
 */
package jap.forward;

import anon.util.XMLUtil;
import jap.IJAPConfSavePoint;
import jap.JAPModel;
import jap.forward.JAPRoutingConnectionClass;
import jap.forward.JAPRoutingSettings;
import java.util.Enumeration;
import java.util.Vector;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class JAPConfForwardingServerSavePoint
implements IJAPConfSavePoint {
    private int m_forwardingServerPort = -1;
    private Element m_connectionClassSettings = null;
    private Element m_availableMixCascadesSettings = null;
    private Element m_registrationInfoServicesSettings = null;

    public void createSavePoint() {
        JAPModel.getInstance().getRoutingSettings();
        this.m_forwardingServerPort = JAPRoutingSettings.getServerPort();
        Document document = XMLUtil.createDocument();
        this.m_connectionClassSettings = JAPModel.getInstance().getRoutingSettings().getConnectionClassSelector().getSettingsAsXml(document);
        this.m_availableMixCascadesSettings = JAPModel.getInstance().getRoutingSettings().getUseableMixCascadesStore().getSettingsAsXml(document);
        this.m_registrationInfoServicesSettings = JAPModel.getInstance().getRoutingSettings().getRegistrationInfoServicesStore().getSettingsAsXml(document);
    }

    public void restoreSavePoint() {
        if (this.m_forwardingServerPort != -1) {
            JAPModel.getInstance().getRoutingSettings().setServerPort(this.m_forwardingServerPort);
        }
        if (this.m_connectionClassSettings != null) {
            JAPModel.getInstance().getRoutingSettings().getConnectionClassSelector().loadSettingsFromXml(this.m_connectionClassSettings);
        }
        if (this.m_availableMixCascadesSettings != null) {
            JAPModel.getInstance().getRoutingSettings().getUseableMixCascadesStore().loadSettingsFromXml(this.m_availableMixCascadesSettings);
        }
        if (this.m_registrationInfoServicesSettings != null) {
            JAPModel.getInstance().getRoutingSettings().getRegistrationInfoServicesStore().loadSettingsFromXml(this.m_registrationInfoServicesSettings);
        }
    }

    public void restoreDefaults() {
        Enumeration enumeration = JAPModel.getInstance().getRoutingSettings().getConnectionClassSelector().getConnectionClasses().elements();
        while (enumeration.hasMoreElements()) {
            JAPRoutingConnectionClass jAPRoutingConnectionClass = (JAPRoutingConnectionClass)enumeration.nextElement();
            if (jAPRoutingConnectionClass.getIdentifier() == 8) {
                jAPRoutingConnectionClass.setMaximumBandwidth(16000);
            }
            jAPRoutingConnectionClass.setRelativeBandwidth(50);
        }
        JAPModel.getInstance().getRoutingSettings().getConnectionClassSelector().setCurrentConnectionClass(2);
        JAPModel.getInstance().getRoutingSettings().getUseableMixCascadesStore().setAllowAllAvailableMixCascades(true);
        JAPModel.getInstance().getRoutingSettings().getUseableMixCascadesStore().setAllowedMixCascades(new Vector());
        JAPModel.getInstance().getRoutingSettings().getRegistrationInfoServicesStore().setRegisterAtAllAvailableInfoServices(true);
        JAPModel.getInstance().getRoutingSettings().getRegistrationInfoServicesStore().setRegistrationInfoServices(new Vector());
    }
}

