/*
 * Decompiled with CFR 0.150.
 */
package jap;

import anon.AnonServerDescription;
import anon.error.AnonServiceException;
import anon.infoservice.MixCascade;
import jap.IJAPMainView;
import jap.JAPController;
import jap.JAPViewIconified;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import javax.swing.JPanel;
import logging.LogHolder;
import logging.LogType;

public class ConsoleJAPMainView
implements IJAPMainView {
    public int addStatusMsg(String string, int n, boolean bl) {
        LogHolder.log(1, LogType.MISC, string);
        return 0;
    }

    public boolean isVisible() {
        return true;
    }

    public int addStatusMsg(String string, int n, boolean bl, ActionListener actionListener) {
        LogHolder.log(1, LogType.MISC, string);
        return 0;
    }

    public void doClickOnCascadeChooser() {
    }

    public void updateValues(boolean bl) {
    }

    public boolean isShowingPaymentError() {
        return false;
    }

    public void showConfigDialog() {
    }

    public void showConfigDialog(String string, Object object) {
    }

    public void setVisible(boolean bl) {
        String string = null;
        System.out.println("Type 'exit' to quit or 'save' to save the configuration.");
        while (true) {
            string = null;
            try {
                string = new BufferedReader(new InputStreamReader(System.in)).readLine();
            }
            catch (Throwable throwable) {
                // empty catch block
            }
            if (string == null) {
                try {
                    Thread.sleep(2000L);
                }
                catch (InterruptedException interruptedException) {}
                continue;
            }
            if (string.equals("exit")) break;
            if (string.equals("save")) {
                System.out.println("Saving configuration...");
                if (!JAPController.getInstance().saveConfigFile()) {
                    System.out.println("Configuration saved!");
                } else {
                    System.out.println("Error while saving configuration!");
                }
            }
            System.out.println("Type 'exit' to quit or 'save' to save the configuration.");
        }
        JAPController.goodBye(true);
    }

    public void channelsChanged(int n) {
    }

    public void packetMixed(long l) {
    }

    public void dataChainErrorSignaled(AnonServiceException anonServiceException) {
        LogHolder.log(1, LogType.NET, "Disconnected because the service proxy is not working!", anonServiceException);
    }

    public void integrityErrorSignaled(AnonServiceException anonServiceException) {
    }

    public void disconnected() {
        LogHolder.log(1, LogType.NET, "Disconnected!");
    }

    public void connectionError(AnonServiceException anonServiceException) {
        LogHolder.log(1, LogType.NET, "Disconnected because of connection error!", anonServiceException);
    }

    public void connecting(AnonServerDescription anonServerDescription, boolean bl) {
        if (anonServerDescription instanceof MixCascade) {
            MixCascade mixCascade = (MixCascade)anonServerDescription;
            LogHolder.log(1, LogType.NET, "Connecting to " + mixCascade.getId() + "(" + mixCascade.getMixNames() + ")" + "...");
        } else {
            LogHolder.log(1, LogType.NET, "Connecting...");
        }
    }

    public void connectionEstablished(AnonServerDescription anonServerDescription) {
        if (anonServerDescription instanceof MixCascade) {
            MixCascade mixCascade = (MixCascade)anonServerDescription;
            LogHolder.log(1, LogType.NET, "Connected to " + mixCascade.getId() + "(" + mixCascade.getMixNames() + ")" + "!");
        } else {
            LogHolder.log(1, LogType.NET, "Connected!");
        }
    }

    public void currentServiceChanged(AnonServerDescription anonServerDescription) {
    }

    public void create(boolean bl) {
    }

    public void disableSetAnonMode() {
    }

    public void onUpdateValues() {
    }

    public JPanel getMainPanel() {
        return null;
    }

    public void registerViewIconified(JAPViewIconified jAPViewIconified) {
    }

    public JAPViewIconified getViewIconified() {
        return null;
    }

    public void removeStatusMsg(int n) {
    }

    public void transferedBytes(long l, int n) {
    }
}

