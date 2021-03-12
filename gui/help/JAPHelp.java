/*
 * Decompiled with CFR 0.150.
 */
package gui.help;

import anon.util.JAPMessages;
import gui.JAPHelpContext;
import gui.dialog.JAPDialog;
import gui.help.IHelpModel;
import gui.help.JAPExternalHelpViewer;
import gui.help.JAPInternalHelpViewer;
import java.awt.Component;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URL;
import javax.swing.JButton;
import javax.swing.JMenuItem;
import logging.LogHolder;
import logging.LogType;

public abstract class JAPHelp {
    public static final String INDEX_CONTEXT = "index";
    public static final String IMG_HELP = (class$gui$help$JAPHelp == null ? (class$gui$help$JAPHelp = JAPHelp.class$("gui.help.JAPHelp")) : class$gui$help$JAPHelp).getName() + "_help.gif";
    public static final String MSG_HELP_BUTTON = (class$gui$help$JAPHelp == null ? (class$gui$help$JAPHelp = JAPHelp.class$("gui.help.JAPHelp")) : class$gui$help$JAPHelp).getName() + "_helpButton";
    public static final String MSG_HELP_MENU_ITEM = (class$gui$help$JAPHelp == null ? (class$gui$help$JAPHelp = JAPHelp.class$("gui.help.JAPHelp")) : class$gui$help$JAPHelp).getName() + "_helpMenuItem";
    public static final String MSG_CLOSE_BUTTON = (class$gui$help$JAPHelp == null ? (class$gui$help$JAPHelp = JAPHelp.class$("gui.help.JAPHelp")) : class$gui$help$JAPHelp).getName() + "_closeButton";
    public static final String MSG_HELP_WINDOW = (class$gui$help$JAPHelp == null ? (class$gui$help$JAPHelp = JAPHelp.class$("gui.help.JAPHelp")) : class$gui$help$JAPHelp).getName() + "_helpWindow";
    public static final String MSG_LANGUAGE_CODE = (class$gui$help$JAPHelp == null ? (class$gui$help$JAPHelp = JAPHelp.class$("gui.help.JAPHelp")) : class$gui$help$JAPHelp).getName() + "_languageCode";
    public static final String MSG_ERROR_EXT_URL = (class$gui$help$JAPHelp == null ? (class$gui$help$JAPHelp = JAPHelp.class$("gui.help.JAPHelp")) : class$gui$help$JAPHelp).getName() + "_errorExtURL";
    public static final String IMG_HOME = (class$gui$help$JAPHelp == null ? (class$gui$help$JAPHelp = JAPHelp.class$("gui.help.JAPHelp")) : class$gui$help$JAPHelp).getName() + "_home.gif";
    public static final String IMG_PREVIOUS = (class$gui$help$JAPHelp == null ? (class$gui$help$JAPHelp = JAPHelp.class$("gui.help.JAPHelp")) : class$gui$help$JAPHelp).getName() + "_previous.gif";
    public static final String IMG_NEXT = (class$gui$help$JAPHelp == null ? (class$gui$help$JAPHelp = JAPHelp.class$("gui.help.JAPHelp")) : class$gui$help$JAPHelp).getName() + "_next.gif";
    private JAPHelpContext.IHelpContext m_helpContext;
    protected static JAPHelp ms_theJAPHelp = null;
    static /* synthetic */ Class class$gui$help$JAPHelp;

    public static void init(Frame frame, IHelpModel iHelpModel) {
        if (ms_theJAPHelp == null) {
            ms_theJAPHelp = JAPHelpFactory.createJAPhelp(frame, iHelpModel);
        }
    }

    public static JAPHelp getInstance() {
        return ms_theJAPHelp;
    }

    public static final JButton createHelpButton(JAPHelpContext.IHelpContext iHelpContext) {
        JButton jButton = new JButton(JAPMessages.getString(MSG_HELP_BUTTON));
        jButton.setToolTipText(JAPMessages.getString(MSG_HELP_BUTTON));
        jButton.addActionListener(new HelpContextActionListener(iHelpContext));
        return jButton;
    }

    public static JMenuItem createHelpMenuItem(JAPHelpContext.IHelpContext iHelpContext) {
        JMenuItem jMenuItem = new JMenuItem(JAPMessages.getString(MSG_HELP_MENU_ITEM));
        jMenuItem.addActionListener(new HelpContextActionListener(iHelpContext));
        return jMenuItem;
    }

    public abstract URL getContextURL(String var1);

    public abstract void loadCurrentContext();

    public abstract void setVisible(boolean var1);

    public final void setContext(final String string, final Component component) {
        if (string == null) {
            return;
        }
        this.m_helpContext = new JAPHelpContext.IHelpContext(){

            public String getHelpContext() {
                return string;
            }

            public Component getHelpExtractionDisplayContext() {
                return component;
            }
        };
    }

    public final void setContext(JAPHelpContext.IHelpContext iHelpContext) {
        this.m_helpContext = iHelpContext;
    }

    public final JAPHelpContext.IHelpContext getHelpContext() {
        return this.m_helpContext;
    }

    protected JAPDialog getOwnDialog() {
        return null;
    }

    public static final JAPDialog getHelpDialog() {
        if (ms_theJAPHelp == null) {
            return null;
        }
        return ms_theJAPHelp.getOwnDialog();
    }

    static /* synthetic */ Class class$(String string) {
        try {
            return Class.forName(string);
        }
        catch (ClassNotFoundException classNotFoundException) {
            throw new NoClassDefFoundError(classNotFoundException.getMessage());
        }
    }

    private static class JAPHelpFactory {
        private JAPHelpFactory() {
        }

        private static JAPHelp createJAPhelp(Frame frame, IHelpModel iHelpModel) {
            if (iHelpModel != null) {
                LogHolder.log(7, LogType.GUI, "Creating external help viewer.");
                return new JAPExternalHelpViewer(frame, iHelpModel);
            }
            LogHolder.log(7, LogType.GUI, "Creating internal help viewer.");
            JAPInternalHelpViewer jAPInternalHelpViewer = new JAPInternalHelpViewer(frame);
            return jAPInternalHelpViewer.getHelp();
        }
    }

    static final class HelpContextActionListener
    implements ActionListener {
        private JAPHelpContext.IHelpContext m_helpContext;

        public HelpContextActionListener(JAPHelpContext.IHelpContext iHelpContext) {
            this.m_helpContext = iHelpContext;
        }

        public void actionPerformed(ActionEvent actionEvent) {
            JAPHelp.getInstance().setContext(this.m_helpContext);
            JAPHelp.getInstance().loadCurrentContext();
            if (JAPHelp.getHelpDialog() != null) {
                JAPHelp.getHelpDialog().toFront();
                JAPHelp.getHelpDialog().requestFocus();
            }
        }
    }
}

