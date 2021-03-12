/*
 * Decompiled with CFR 0.150.
 */
package jap;

import anon.infoservice.AbstractDatabaseEntry;
import anon.infoservice.Database;
import anon.infoservice.InfoServiceDBEntry;
import anon.infoservice.InfoServiceHolder;
import jap.IJAPConfSavePoint;
import jap.JAPController;
import jap.JAPModel;
import java.util.Enumeration;
import java.util.Vector;
import logging.LogHolder;
import logging.LogType;

public class JAPConfInfoServiceSavePoint
implements IJAPConfSavePoint {
    private Vector m_knownInfoServices;
    private InfoServiceDBEntry m_preferredInfoService;
    private boolean m_automaticInfoServiceRequestsDisabled;
    private boolean m_automaticInfoServiceChanges;
    static /* synthetic */ Class class$anon$infoservice$InfoServiceDBEntry;

    public void createSavePoint() {
        this.m_knownInfoServices = Database.getInstance(class$anon$infoservice$InfoServiceDBEntry == null ? (class$anon$infoservice$InfoServiceDBEntry = JAPConfInfoServiceSavePoint.class$("anon.infoservice.InfoServiceDBEntry")) : class$anon$infoservice$InfoServiceDBEntry).getEntryList();
        this.m_preferredInfoService = InfoServiceHolder.getInstance().getPreferredInfoService();
        this.m_automaticInfoServiceRequestsDisabled = JAPModel.isInfoServiceDisabled();
        this.m_automaticInfoServiceChanges = InfoServiceHolder.getInstance().isChangeInfoServices();
    }

    public void restoreSavePoint() {
        Object object;
        Enumeration enumeration = this.m_knownInfoServices.elements();
        while (enumeration.hasMoreElements()) {
            object = (InfoServiceDBEntry)enumeration.nextElement();
            Database.getInstance(class$anon$infoservice$InfoServiceDBEntry == null ? JAPConfInfoServiceSavePoint.class$("anon.infoservice.InfoServiceDBEntry") : class$anon$infoservice$InfoServiceDBEntry).update((AbstractDatabaseEntry)object);
        }
        object = Database.getInstance(class$anon$infoservice$InfoServiceDBEntry == null ? (class$anon$infoservice$InfoServiceDBEntry = JAPConfInfoServiceSavePoint.class$("anon.infoservice.InfoServiceDBEntry")) : class$anon$infoservice$InfoServiceDBEntry).getEntryList().elements();
        while (object.hasMoreElements()) {
            InfoServiceDBEntry infoServiceDBEntry = (InfoServiceDBEntry)object.nextElement();
            if (this.m_knownInfoServices.contains(infoServiceDBEntry)) continue;
            Database.getInstance(class$anon$infoservice$InfoServiceDBEntry == null ? JAPConfInfoServiceSavePoint.class$("anon.infoservice.InfoServiceDBEntry") : class$anon$infoservice$InfoServiceDBEntry).remove(infoServiceDBEntry);
        }
        InfoServiceHolder.getInstance().setPreferredInfoService(this.m_preferredInfoService);
        JAPController.getInstance().setInfoServiceDisabled(this.m_automaticInfoServiceRequestsDisabled);
        InfoServiceHolder.getInstance().setChangeInfoServices(this.m_automaticInfoServiceChanges);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void restoreDefaults() {
        InfoServiceHolder infoServiceHolder = InfoServiceHolder.getInstance();
        synchronized (infoServiceHolder) {
            Database.getInstance(class$anon$infoservice$InfoServiceDBEntry == null ? (class$anon$infoservice$InfoServiceDBEntry = JAPConfInfoServiceSavePoint.class$("anon.infoservice.InfoServiceDBEntry")) : class$anon$infoservice$InfoServiceDBEntry).removeAll();
            try {
                InfoServiceDBEntry[] arrinfoServiceDBEntry = JAPController.createDefaultInfoServices();
                for (int i = 0; i < arrinfoServiceDBEntry.length; ++i) {
                    Database.getInstance(class$anon$infoservice$InfoServiceDBEntry == null ? JAPConfInfoServiceSavePoint.class$("anon.infoservice.InfoServiceDBEntry") : class$anon$infoservice$InfoServiceDBEntry).update(arrinfoServiceDBEntry[i]);
                }
                InfoServiceHolder.getInstance().setPreferredInfoService(arrinfoServiceDBEntry[0]);
            }
            catch (Exception exception) {
                LogHolder.log(2, LogType.MISC, "Cannot create the default infoservice.");
            }
            JAPController.getInstance().setInfoServiceDisabled(false);
            InfoServiceHolder.getInstance().setChangeInfoServices(true);
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

