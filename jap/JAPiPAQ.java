/*
 * Decompiled with CFR 0.150.
 */
package jap;

import anon.util.JAPMessages;
import gui.GUIUtils;
import jap.ConsoleSplash;
import jap.JAPController;
import jap.JAPDebug;
import jap.JAPModel;
import jap.JAPNewView;
import java.util.Locale;
import logging.LogHolder;
import logging.LogType;

public final class JAPiPAQ {
    private JAPNewView view;
    private JAPController m_controller;

    public void startJAP(String string) {
        if (!JAPMessages.init("JAPMessages")) {
            GUIUtils.exitWithNoMessagesError("MixConfigMessages");
        }
        JAPModel.getInstance().setSmallDisplay(true);
        this.m_controller = JAPController.getInstance();
        LogHolder.setLogInstance(JAPDebug.getInstance());
        JAPDebug.getInstance().setLogType(LogType.NET + LogType.GUI + LogType.MISC);
        JAPDebug.getInstance().setLogLevel(4);
        this.m_controller.loadConfigFile(string, null);
        this.view = new JAPNewView(JAPModel.getInstance().getProgramName(), this.m_controller);
        this.view.create(false);
        this.m_controller.addJAPObserver(this.view);
        this.m_controller.setView(this.view, new ConsoleSplash());
        this.m_controller.initialRun(null, 0);
        this.view.setSize(240, 300);
        this.view.setLocation(0, 0);
        this.view.setResizable(false);
        this.view.setVisible(true);
    }

    public void setLocale(Locale locale) {
        JAPMessages.setLocale(locale);
    }

    public static void main(String[] arrstring) {
        JAPiPAQ jAPiPAQ = new JAPiPAQ();
        jAPiPAQ.startJAP(null);
    }
}

