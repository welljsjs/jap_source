/*
 * Decompiled with CFR 0.150.
 */
package jap.gui;

import anon.platform.AbstractOS;
import anon.util.JAPMessages;
import gui.ClipboardCopier;
import gui.GUIUtils;
import gui.JAPHelpContext;
import gui.help.JAPHelp;
import jap.IJAPMainView;
import jap.JAPController;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.MalformedURLException;
import java.net.URL;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

public class LinkRegistrator {
    private static final String MSG_CLICK_TO_VIEW_HELP = (class$jap$gui$LinkRegistrator == null ? (class$jap$gui$LinkRegistrator = LinkRegistrator.class$("jap.gui.LinkRegistrator")) : class$jap$gui$LinkRegistrator).getName() + ".clickToViewHelp";
    private static final String MSG_RECOMMENDED = (class$jap$gui$LinkRegistrator == null ? (class$jap$gui$LinkRegistrator = LinkRegistrator.class$("jap.gui.LinkRegistrator")) : class$jap$gui$LinkRegistrator).getName() + ".recommended";
    private static final String IMG_ARROW = "arrow46.gif";
    public static final int TYPE_HELP_CONTEXT = 0;
    public static final int TYPE_URL = 1;
    public static final int TYPE_CONFIGURATION = 2;
    public static final int TYPE_E_MAIL = 3;
    public static final String CONF_NETWORK = "CONF_NETWORK";
    public static final String CONF_PAYMENT = "CONF_PAYMENT";
    public static final String CONF_FILTER = "CONF_FILTER";
    public static final String CONF_INFOSERVICE_SETTINGS = "CONF_INFOSERVICE_SETTINGS";
    public static final String CONF_INFOSERVICE = "CONF_INFOSERVICE";
    public static final String CONF_SERVICE = "CONF_SERVICE";
    public static final String CONF_SERVICE_SETTINGS = "CONF_SERVICE_SETTINGS";
    public static final String CONF_DEBUG = "CONF_DEBUG";
    private Container m_container;
    private IJAPMainView m_mainView;
    private ClipboardCopier m_textCopier;
    static /* synthetic */ Class class$jap$gui$LinkRegistrator;

    public LinkRegistrator(Container container, IJAPMainView iJAPMainView) {
        this.m_container = container;
        this.m_mainView = iJAPMainView;
        this.m_textCopier = new ClipboardCopier(false);
    }

    public void unregisterAll() {
        this.m_textCopier.unregisterAll();
    }

    public void addBrowserInstallationInfo(Container container, GridBagConstraints gridBagConstraints, String string, String string2, boolean bl, int n) {
        this.addBrowserInstallationInfo(container, gridBagConstraints, string, string2, bl, n, null);
    }

    public void addBrowserInstallationInfo(Container container, GridBagConstraints gridBagConstraints, String string, String string2, boolean bl, int n, Object object) {
        GridBagConstraints gridBagConstraints2 = new GridBagConstraints();
        JPanel jPanel = new JPanel(new GridBagLayout());
        gridBagConstraints2.gridx = 0;
        ++gridBagConstraints2.gridy;
        gridBagConstraints2.gridwidth = 1;
        gridBagConstraints2.anchor = 17;
        JLabel jLabel = new JLabel(GUIUtils.loadImageIcon(IMG_ARROW));
        jPanel.add((Component)jLabel, gridBagConstraints2);
        jLabel = bl ? new JLabel(string + " (" + JAPMessages.getString(MSG_RECOMMENDED) + ")") : new JLabel(string);
        this.registerLink(jLabel, string2, n, object);
        ++gridBagConstraints2.gridx;
        jPanel.add((Component)jLabel, gridBagConstraints2);
        jLabel = new JLabel();
        ++gridBagConstraints2.gridx;
        gridBagConstraints2.weightx = 1.0;
        gridBagConstraints2.fill = 2;
        jPanel.add((Component)jLabel, gridBagConstraints2);
        if (n == 1 || n == 3) {
            this.m_textCopier.register(jLabel);
        }
        if (gridBagConstraints != null) {
            ++gridBagConstraints.gridy;
            gridBagConstraints.gridwidth = 4;
            gridBagConstraints.weightx = 1.0;
            gridBagConstraints.anchor = 17;
            gridBagConstraints.fill = 2;
            gridBagConstraints.gridx = 0;
        }
        container.add((Component)jPanel, gridBagConstraints);
        if (gridBagConstraints != null) {
            gridBagConstraints.weightx = 0.0;
        }
    }

    private void registerLink(JLabel jLabel, final String string, final int n, final Object object) {
        jLabel.setForeground(Color.blue);
        if (n == 0) {
            jLabel.setToolTipText(JAPMessages.getString(MSG_CLICK_TO_VIEW_HELP));
        } else if (n == 2) {
            jLabel.setToolTipText(jLabel.getText());
        } else {
            jLabel.setToolTipText(string);
        }
        jLabel.setCursor(Cursor.getPredefinedCursor(12));
        if (n == 1 || n == 3) {
            this.m_textCopier.register(jLabel);
        }
        jLabel.addMouseListener(new MouseAdapter(){

            public void mouseClicked(MouseEvent mouseEvent) {
                if (!SwingUtilities.isLeftMouseButton(mouseEvent)) {
                    return;
                }
                if (n == 0) {
                    JAPHelp.getInstance().setContext(JAPHelpContext.createHelpContext(string, LinkRegistrator.this.m_container));
                    JAPHelp.getInstance().setVisible(true);
                } else if (n == 1) {
                    try {
                        URL uRL = new URL(string);
                        JAPController.getInstance().allowDirectProxyDomain(uRL);
                        AbstractOS.getInstance().openURL(uRL);
                    }
                    catch (MalformedURLException malformedURLException) {}
                } else if (n == 3) {
                    AbstractOS.getInstance().openEMail(string);
                } else if (n == 2) {
                    if (string.equals(LinkRegistrator.CONF_NETWORK)) {
                        LinkRegistrator.this.m_mainView.showConfigDialog("NETWORK_TAB", null);
                    } else if (string.equals(LinkRegistrator.CONF_PAYMENT)) {
                        LinkRegistrator.this.m_mainView.showConfigDialog("PAYMENT_TAB", object);
                    } else if (string.equals(LinkRegistrator.CONF_FILTER)) {
                        LinkRegistrator.this.m_mainView.showConfigDialog("ANON_TAB", Boolean.TRUE);
                    } else if (string.equals(LinkRegistrator.CONF_INFOSERVICE)) {
                        LinkRegistrator.this.m_mainView.showConfigDialog("INFOSERVICE_TAB", null);
                    } else if (string.equals(LinkRegistrator.CONF_SERVICE)) {
                        LinkRegistrator.this.m_mainView.showConfigDialog("ANON_TAB", JAPController.getInstance().getCurrentMixCascade());
                    } else if (string.equals(LinkRegistrator.CONF_INFOSERVICE_SETTINGS)) {
                        LinkRegistrator.this.m_mainView.showConfigDialog("INFOSERVICE_TAB", Boolean.TRUE);
                    } else if (string.equals(LinkRegistrator.CONF_SERVICE_SETTINGS)) {
                        LinkRegistrator.this.m_mainView.showConfigDialog("ANON_TAB", null);
                    } else if (string.equals(LinkRegistrator.CONF_DEBUG)) {
                        LinkRegistrator.this.m_mainView.showConfigDialog("DEBUG_TAB", null);
                    }
                }
            }
        });
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

