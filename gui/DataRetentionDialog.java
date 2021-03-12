/*
 * Decompiled with CFR 0.150.
 */
package gui;

import anon.infoservice.DataRetentionInformation;
import anon.infoservice.MixCascade;
import anon.util.JAPMessages;
import anon.util.XMLDuration;
import gui.dialog.JAPDialog;
import java.awt.Component;
import java.net.URL;
import java.util.Enumeration;

public class DataRetentionDialog {
    public static final String MSG_DATA_RETENTION_EXPLAIN_SHORT = (class$gui$DataRetentionDialog == null ? (class$gui$DataRetentionDialog = DataRetentionDialog.class$("gui.DataRetentionDialog")) : class$gui$DataRetentionDialog).getName() + "_explainShort";
    public static final String MSG_DATA_RETENTION_MIX_EXPLAIN_SHORT = (class$gui$DataRetentionDialog == null ? (class$gui$DataRetentionDialog = DataRetentionDialog.class$("gui.DataRetentionDialog")) : class$gui$DataRetentionDialog).getName() + "_explainShortMix";
    public static final String MSG_DATA_RETENTION_EXPLAIN = (class$gui$DataRetentionDialog == null ? (class$gui$DataRetentionDialog = DataRetentionDialog.class$("gui.DataRetentionDialog")) : class$gui$DataRetentionDialog).getName() + "_explain";
    public static final String MSG_RETENTION_PERIOD = (class$gui$DataRetentionDialog == null ? (class$gui$DataRetentionDialog = DataRetentionDialog.class$("gui.DataRetentionDialog")) : class$gui$DataRetentionDialog).getName() + "_retentionPeriod";
    public static final String MSG_INFO_TITLE = (class$gui$DataRetentionDialog == null ? (class$gui$DataRetentionDialog = DataRetentionDialog.class$("gui.DataRetentionDialog")) : class$gui$DataRetentionDialog).getName() + "_info";
    public static final String MSG_NO_LOGS = (class$gui$DataRetentionDialog == null ? (class$gui$DataRetentionDialog = DataRetentionDialog.class$("gui.DataRetentionDialog")) : class$gui$DataRetentionDialog).getName() + "_noLogs";
    public static final String MSG_ENTRY_MIX_STORES = (class$gui$DataRetentionDialog == null ? (class$gui$DataRetentionDialog = DataRetentionDialog.class$("gui.DataRetentionDialog")) : class$gui$DataRetentionDialog).getName() + "_entryMixStores";
    public static final String MSG_CASCADE_STORES = (class$gui$DataRetentionDialog == null ? (class$gui$DataRetentionDialog = DataRetentionDialog.class$("gui.DataRetentionDialog")) : class$gui$DataRetentionDialog).getName() + "_cascadeStores";
    public static final String MSG_MIX_STORES = (class$gui$DataRetentionDialog == null ? (class$gui$DataRetentionDialog = DataRetentionDialog.class$("gui.DataRetentionDialog")) : class$gui$DataRetentionDialog).getName() + "_mixStores";
    public static final String MSG_NO_CHANCE = (class$gui$DataRetentionDialog == null ? (class$gui$DataRetentionDialog = DataRetentionDialog.class$("gui.DataRetentionDialog")) : class$gui$DataRetentionDialog).getName() + "_noChance";
    public static final String MSG_NO_TARGET_ADDRESSES = (class$gui$DataRetentionDialog == null ? (class$gui$DataRetentionDialog = DataRetentionDialog.class$("gui.DataRetentionDialog")) : class$gui$DataRetentionDialog).getName() + "_noTargetAdresses";
    public static final String MSG_IN_THE_SCOPE = (class$gui$DataRetentionDialog == null ? (class$gui$DataRetentionDialog = DataRetentionDialog.class$("gui.DataRetentionDialog")) : class$gui$DataRetentionDialog).getName() + "_inTheScope";
    public static final String MSG_WHETHER_CONNECTED = (class$gui$DataRetentionDialog == null ? (class$gui$DataRetentionDialog = DataRetentionDialog.class$("gui.DataRetentionDialog")) : class$gui$DataRetentionDialog).getName() + "_whetherConnected";
    public static final String MSG_WHICH_TARGETED = (class$gui$DataRetentionDialog == null ? (class$gui$DataRetentionDialog = DataRetentionDialog.class$("gui.DataRetentionDialog")) : class$gui$DataRetentionDialog).getName() + "_whichTargeted";
    public static final String MSG_WHETHER_TARGETED = (class$gui$DataRetentionDialog == null ? (class$gui$DataRetentionDialog = DataRetentionDialog.class$("gui.DataRetentionDialog")) : class$gui$DataRetentionDialog).getName() + "_whetherTargeted";
    public static final String MSG_WHETHER_USED = (class$gui$DataRetentionDialog == null ? (class$gui$DataRetentionDialog = DataRetentionDialog.class$("gui.DataRetentionDialog")) : class$gui$DataRetentionDialog).getName() + "_whetherUsed";
    static /* synthetic */ Class class$gui$DataRetentionDialog;
    static /* synthetic */ Class class$anon$infoservice$DataRetentionInformation;

    private DataRetentionDialog() {
    }

    public static void show(Component component, MixCascade mixCascade) {
        DataRetentionDialog.show(component, mixCascade, -1);
    }

    public static void show(Component component, MixCascade mixCascade, int n) {
        String string = "";
        JAPDialog.AbstractLinkedURLAdapter abstractLinkedURLAdapter = null;
        DataRetentionInformation dataRetentionInformation = null;
        DataRetentionInformation dataRetentionInformation2 = null;
        DataRetentionInformation dataRetentionInformation3 = null;
        DataRetentionInformation dataRetentionInformation4 = null;
        URL uRL = null;
        boolean bl = false;
        boolean bl2 = false;
        if (n >= 0) {
            if (mixCascade != null && mixCascade.getMixInfo(n) != null) {
                dataRetentionInformation = mixCascade.getMixInfo(n).getDataRetentionInformation();
            }
        } else if (mixCascade != null) {
            dataRetentionInformation = mixCascade.getDataRetentionInformation();
        }
        if (dataRetentionInformation == null || mixCascade == null) {
            return;
        }
        uRL = dataRetentionInformation.getURL(JAPMessages.getLocale().getLanguage());
        final URL uRL2 = uRL;
        if (uRL2 != null) {
            abstractLinkedURLAdapter = new JAPDialog.AbstractLinkedURLAdapter(){

                public URL getUrl() {
                    return uRL2;
                }

                public String getMessage() {
                    return JAPMessages.getString(JAPDialog.ILinkedInformation.MSG_MORE_INFO);
                }
            };
        }
        if (mixCascade.getMixInfo(0) != null) {
            dataRetentionInformation2 = mixCascade.getMixInfo(0).getDataRetentionInformation();
            if (n < 0 && dataRetentionInformation.isLogged(1) && dataRetentionInformation2 != null) {
                bl = true;
            }
        }
        dataRetentionInformation4 = mixCascade.getDataRetentionInformation();
        if (mixCascade.getMixInfo(mixCascade.getNumberOfMixes() - 1) != null) {
            dataRetentionInformation3 = mixCascade.getMixInfo(mixCascade.getNumberOfMixes() - 1).getDataRetentionInformation();
        }
        if (n < 0 && (dataRetentionInformation2 == null || dataRetentionInformation2.isLogged(1))) {
            string = string + JAPMessages.getString(MSG_NO_LOGS, "<i>" + mixCascade.getName() + "</i>");
        } else {
            if (bl) {
                string = JAPMessages.getString(MSG_ENTRY_MIX_STORES, "<i>" + mixCascade.getName() + "</i>");
            } else if (n < 0) {
                string = JAPMessages.getString(MSG_CASCADE_STORES, "<i>" + mixCascade.getName() + "</i>");
            } else {
                String string2 = "unknown";
                if (mixCascade.getMixInfo(n).getServiceOperator() != null) {
                    string2 = mixCascade.getMixInfo(n).getServiceOperator().getOrganization();
                }
                string = JAPMessages.getString(MSG_MIX_STORES, new String[]{"<i>" + mixCascade.getMixInfo(n).getName() + "</i>", "<i>" + string2 + "</i>"});
            }
            string = string + "<ul>";
            for (int i = 0; i < DataRetentionInformation.getLoggedElementsLength(); ++i) {
                if (!dataRetentionInformation.isLogged(DataRetentionInformation.getLoggedElementID(i))) {
                    if (n >= 0 || (dataRetentionInformation2 == null || DataRetentionInformation.getLoggedElementID(i) != 32 && DataRetentionInformation.getLoggedElementID(i) != 64 && DataRetentionInformation.getLoggedElementID(i) != 2 && DataRetentionInformation.getLoggedElementID(i) != 4 || !dataRetentionInformation2.isLogged(i)) && (dataRetentionInformation3 == null || DataRetentionInformation.getLoggedElementID(i) != 128 && DataRetentionInformation.getLoggedElementID(i) != 256 && DataRetentionInformation.getLoggedElementID(i) != 512 && DataRetentionInformation.getLoggedElementID(i) != 1024 || !dataRetentionInformation3.isLogged(i))) continue;
                } else if (n >= 0 && mixCascade.getNumberOfOperatorsShown() > 1) {
                    if (n == 0 && (DataRetentionInformation.getLoggedElementID(i) == 128 || DataRetentionInformation.getLoggedElementID(i) == 256 || DataRetentionInformation.getLoggedElementID(i) == 512 || DataRetentionInformation.getLoggedElementID(i) == 1024)) continue;
                    if (n >= 0) {
                        if (DataRetentionInformation.getLoggedElementID(i) == 32) {
                            string = string + "<li>" + JAPMessages.getString((class$anon$infoservice$DataRetentionInformation == null ? DataRetentionDialog.class$("anon.infoservice.DataRetentionInformation") : class$anon$infoservice$DataRetentionInformation).getName() + "_" + "INPUT_SOURCE_IP_ADDRESS_MIX") + "</li>";
                            continue;
                        }
                        if (DataRetentionInformation.getLoggedElementID(i) == 64) {
                            string = string + "<li>" + JAPMessages.getString((class$anon$infoservice$DataRetentionInformation == null ? DataRetentionDialog.class$("anon.infoservice.DataRetentionInformation") : class$anon$infoservice$DataRetentionInformation).getName() + "_" + "INPUT_SOURCE_IP_PORT_MIX") + "</li>";
                            continue;
                        }
                    }
                }
                string = string + "<li>" + JAPMessages.getString((class$anon$infoservice$DataRetentionInformation == null ? DataRetentionDialog.class$("anon.infoservice.DataRetentionInformation") : class$anon$infoservice$DataRetentionInformation).getName() + "_" + DataRetentionInformation.getLoggedElementName(i)) + "</li>";
            }
            string = string + "</ul>";
            string = string + "<p>" + JAPMessages.getString(MSG_RETENTION_PERIOD) + ": ";
            XMLDuration xMLDuration = bl ? dataRetentionInformation2.getDuration() : dataRetentionInformation.getDuration();
            Enumeration enumeration = xMLDuration.getFields();
            while (enumeration.hasMoreElements()) {
                Object e = enumeration.nextElement();
                string = string + xMLDuration.getField(e).intValue() + " " + JAPMessages.getString(XMLDuration.getFieldName(e));
                if (!enumeration.hasMoreElements()) continue;
                string = string + ", ";
            }
            string = string + "</p>";
            string = string + "<br>";
            if (dataRetentionInformation4 != null && dataRetentionInformation2 != null && dataRetentionInformation2.isLogged(32)) {
                string = string + JAPMessages.getString(MSG_IN_THE_SCOPE) + " ";
                if (bl || !dataRetentionInformation4.isLogged(8) || !dataRetentionInformation4.isLogged(16)) {
                    string = dataRetentionInformation2.isLogged(2) || dataRetentionInformation2.isLogged(8) ? string + JAPMessages.getString(MSG_WHETHER_USED, "<i>" + mixCascade.getName() + "</i>") : string + JAPMessages.getString(MSG_WHETHER_CONNECTED, "<i>" + mixCascade.getName() + "</i>");
                } else if (dataRetentionInformation4.isLogged(2)) {
                    if (dataRetentionInformation3.isLogged(1024) || dataRetentionInformation3.isLogged(512)) {
                        bl2 = true;
                        string = string + JAPMessages.getString(MSG_WHICH_TARGETED);
                    } else if (dataRetentionInformation3.isLogged(256)) {
                        string = string + JAPMessages.getString(MSG_WHETHER_TARGETED, "<i>" + mixCascade.getName() + "</i>");
                    }
                } else {
                    string = string + JAPMessages.getString(MSG_WHETHER_CONNECTED, "<i>" + mixCascade.getName() + "</i>");
                }
                if (!bl2) {
                    string = string + " <b>" + JAPMessages.getString(MSG_NO_TARGET_ADDRESSES) + "</b>";
                }
            } else {
                string = string + JAPMessages.getString(MSG_NO_CHANCE, "<i>" + mixCascade.getName() + "</i>");
            }
        }
        JAPDialog.showWarningDialog(component, string, JAPMessages.getString(MSG_INFO_TITLE), (JAPDialog.ILinkedInformation)abstractLinkedURLAdapter);
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

