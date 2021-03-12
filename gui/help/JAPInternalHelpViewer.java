/*
 * Decompiled with CFR 0.150.
 */
package gui.help;

import anon.platform.AbstractOS;
import anon.util.JAPMessages;
import anon.util.LanguageMapper;
import anon.util.ResourceLoader;
import gui.ClipboardCopier;
import gui.GUIUtils;
import gui.JAPHelpContext;
import gui.dialog.JAPDialog;
import gui.help.JAPHelp;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.net.URL;
import java.util.Locale;
import java.util.Vector;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JEditorPane;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.text.Document;
import logging.LogHolder;
import logging.LogType;

public class JAPInternalHelpViewer
extends JAPDialog {
    private String m_helpPath = " ";
    private LanguageMapper m_language = new LanguageMapper();
    private JComboBox m_comBoxLanguage;
    private HtmlPane m_htmlpaneTheHelpPane;
    private JButton m_closeButton;
    private JButton m_backButton;
    private JButton m_forwardButton;
    private JButton m_homeButton;
    private boolean m_initializing;
    private JAPInternalHelpDelegator m_delegator;

    JAPInternalHelpViewer(Frame frame) {
        super(frame, JAPMessages.getString(JAPHelp.MSG_HELP_WINDOW), false);
        this.setDefaultCloseOperation(1);
        this.m_initializing = true;
        this.m_htmlpaneTheHelpPane = new HtmlPane();
        this.m_htmlpaneTheHelpPane.addPropertyChangeListener(new HelpListener());
        JPanel jPanel = new JPanel(new FlowLayout(0));
        this.m_comBoxLanguage = new JComboBox();
        this.m_backButton = new JButton(GUIUtils.loadImageIcon(JAPHelp.IMG_PREVIOUS, true));
        this.m_backButton.setBackground(Color.gray);
        this.m_backButton.setOpaque(false);
        this.m_backButton.setBorder(new EmptyBorder(0, 0, 0, 0));
        this.m_backButton.setFocusPainted(false);
        this.m_forwardButton = new JButton(GUIUtils.loadImageIcon(JAPHelp.IMG_NEXT, true));
        this.m_forwardButton.setBackground(Color.gray);
        this.m_forwardButton.setOpaque(false);
        this.m_forwardButton.setBorder(new EmptyBorder(0, 0, 0, 0));
        this.m_forwardButton.setFocusPainted(false);
        this.m_homeButton = new JButton(GUIUtils.loadImageIcon(JAPHelp.IMG_HOME, true));
        this.m_homeButton.setBackground(Color.gray);
        this.m_homeButton.setOpaque(false);
        this.m_homeButton.setBorder(new EmptyBorder(0, 0, 0, 0));
        this.m_homeButton.setFocusPainted(false);
        this.m_closeButton = new JButton(JAPMessages.getString(JAPHelp.MSG_CLOSE_BUTTON));
        this.m_forwardButton.setEnabled(false);
        this.m_backButton.setEnabled(false);
        jPanel.add(this.m_homeButton);
        jPanel.add(this.m_backButton);
        jPanel.add(this.m_forwardButton);
        jPanel.add(new JLabel("   "));
        jPanel.add(this.m_comBoxLanguage);
        jPanel.add(new JLabel("   "));
        jPanel.add(this.m_closeButton);
        this.getContentPane().add((Component)this.m_htmlpaneTheHelpPane, "Center");
        this.getContentPane().add((Component)jPanel, "North");
        this.getRootPane().setDefaultButton(this.m_closeButton);
        this.m_closeButton.addActionListener(new HelpListener());
        this.m_backButton.addActionListener(new HelpListener());
        this.m_forwardButton.addActionListener(new HelpListener());
        this.m_homeButton.addActionListener(new HelpListener());
        this.m_comBoxLanguage.addActionListener(new HelpListener());
        int n = 1;
        while (true) {
            block3: {
                try {
                    String string = JAPMessages.getString(JAPHelp.MSG_LANGUAGE_CODE + String.valueOf(n));
                    LanguageMapper languageMapper = new LanguageMapper(string, new Locale(string, ""));
                    this.m_comBoxLanguage.addItem(languageMapper);
                    if ((!this.m_helpPath.equals(" ") || this.m_language.getISOCode().length() != 0) && !languageMapper.getISOCode().equals(JAPMessages.getLocale().getLanguage())) break block3;
                    this.m_helpPath = "help/";
                    this.m_language = languageMapper;
                    this.m_comBoxLanguage.setSelectedIndex(n - 1);
                }
                catch (Exception exception) {
                    break;
                }
            }
            ++n;
        }
        ((JComponent)this.getContentPane()).setPreferredSize(new Dimension(Math.min(Toolkit.getDefaultToolkit().getScreenSize().width - 50, 600), Math.min(Toolkit.getDefaultToolkit().getScreenSize().height - 80, 350)));
        this.pack();
        this.m_initializing = false;
        this.m_delegator = new JAPInternalHelpDelegator(this);
    }

    public URL getContextURL(String string) {
        return ResourceLoader.getResourceURL(this.m_helpPath + this.m_language.getISOCode() + "/" + this.m_helpPath + string + ".html");
    }

    public void loadCurrentContext() {
        this.setVisible(true);
    }

    public void setVisible(boolean bl) {
        boolean bl2 = false;
        if (bl) {
            JAPHelpContext.IHelpContext iHelpContext = this.m_delegator.getHelpContext();
            String string = iHelpContext != null ? iHelpContext.getHelpContext() : "index";
            try {
                URL uRL = new URL(string);
                if (AbstractOS.getInstance().openURL(uRL)) {
                    bl2 = true;
                }
            }
            catch (Exception exception) {
                // empty catch block
            }
            if (!bl2) {
                try {
                    this.m_htmlpaneTheHelpPane.loadContext(this.m_helpPath, this.m_delegator.getHelpContext().getHelpContext(), this.m_language);
                }
                catch (Exception exception) {
                    // empty catch block
                }
            }
        }
        if (!bl2 || !bl) {
            super.setVisible(bl);
        }
    }

    private void homePressed() {
        this.m_htmlpaneTheHelpPane.loadContext(this.m_helpPath, "index", this.m_language);
    }

    private void closePressed() {
        this.setVisible(false);
    }

    private void backPressed() {
        this.m_htmlpaneTheHelpPane.goBack();
        this.checkNavigationButtons();
    }

    private void forwardPressed() {
        this.m_htmlpaneTheHelpPane.goForward();
        this.checkNavigationButtons();
    }

    private void checkNavigationButtons() {
        if (this.m_htmlpaneTheHelpPane.backAllowed()) {
            this.m_backButton.setEnabled(true);
        } else {
            this.m_backButton.setEnabled(false);
        }
        if (this.m_htmlpaneTheHelpPane.forwardAllowed()) {
            this.m_forwardButton.setEnabled(true);
        } else {
            this.m_forwardButton.setEnabled(false);
        }
    }

    JAPHelp getHelp() {
        return this.m_delegator;
    }

    class JAPInternalHelpDelegator
    extends JAPHelp {
        JAPInternalHelpViewer viewer;

        public JAPInternalHelpDelegator(JAPInternalHelpViewer jAPInternalHelpViewer2) {
            this.viewer = jAPInternalHelpViewer2;
        }

        public URL getContextURL(String string) {
            return this.viewer.getContextURL(string);
        }

        public boolean equals(Object object) {
            return this.viewer.equals(object);
        }

        public int hashCode() {
            return this.viewer.hashCode();
        }

        public void loadCurrentContext() {
            this.viewer.loadCurrentContext();
        }

        public void setVisible(boolean bl) {
            this.viewer.setVisible(bl);
        }

        public String toString() {
            return this.viewer.toString();
        }

        protected JAPDialog getOwnDialog() {
            return this.viewer;
        }
    }

    private final class HtmlPane
    extends JScrollPane
    implements HyperlinkListener {
        private JEditorPane html = new JEditorPane("text/html", "<html><body></body></html>");
        private URL url;
        private Cursor cursor;
        private Vector m_history;
        private Vector m_historyViewports;
        private int m_historyPosition;
        private static final String MAILTO = "mailto";

        public HtmlPane() {
            new ClipboardCopier(true).register(this.html);
            this.html.setEditable(false);
            this.html.addHyperlinkListener(this);
            this.m_history = new Vector();
            this.m_historyViewports = new Vector();
            this.m_historyPosition = -1;
            this.getViewport().add(this.html);
            this.cursor = this.html.getCursor();
        }

        public JEditorPane getPane() {
            return this.html;
        }

        public void goBack() {
            --this.m_historyPosition;
            this.loadURL((URL)this.m_history.elementAt(this.m_historyPosition));
        }

        public void goForward() {
            ++this.m_historyPosition;
            this.loadURL((URL)this.m_history.elementAt(this.m_historyPosition));
        }

        private void addToHistory(URL uRL) {
            if (this.m_historyPosition == -1 || !uRL.getFile().equalsIgnoreCase(((URL)this.m_history.elementAt(this.m_historyPosition)).getFile())) {
                this.m_history.insertElementAt(uRL, ++this.m_historyPosition);
            }
        }

        public boolean loadContext(String string, String string2, LanguageMapper languageMapper) {
            URL uRL = ResourceLoader.getResourceURL(string + languageMapper.getISOCode() + "/" + string + string2 + ".html");
            boolean bl = false;
            if (uRL != null) {
                this.linkActivated(uRL);
                bl = true;
            } else {
                LogHolder.log(4, LogType.GUI, "Could not load help context '" + languageMapper.getISOCode() + "/" + string + string2 + "'");
                if (string2 != null) {
                    if (!string2.equals("index")) {
                        bl = this.loadContext(string, "index", languageMapper);
                    } else if (languageMapper.equals(new LanguageMapper("EN"))) {
                        LogHolder.log(3, LogType.GUI, "No index help file for language '" + languageMapper.getISOCode() + "', help files seem to be corrupt!");
                        return true;
                    }
                    if (!bl) {
                        LanguageMapper languageMapper2 = new LanguageMapper("EN");
                        for (int i = 0; i < JAPInternalHelpViewer.this.m_comBoxLanguage.getItemCount(); ++i) {
                            if (!((LanguageMapper)JAPInternalHelpViewer.this.m_comBoxLanguage.getItemAt(i)).equals(languageMapper2) || JAPInternalHelpViewer.this.m_comBoxLanguage.getSelectedIndex() == i) continue;
                            JAPInternalHelpViewer.this.m_comBoxLanguage.setSelectedIndex(i);
                            new HelpListener().actionPerformed(new ActionEvent(JAPInternalHelpViewer.this.m_comBoxLanguage, 0, ""));
                            break;
                        }
                        LogHolder.log(4, LogType.GUI, "No index help file for language '" + languageMapper.getISOCode() + "', switching to '" + languageMapper2.getISOCode() + "'");
                        bl = true;
                    }
                }
            }
            return bl;
        }

        public void hyperlinkUpdate(HyperlinkEvent hyperlinkEvent) {
            if (hyperlinkEvent.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
                this.linkActivated(hyperlinkEvent.getURL());
            } else if (hyperlinkEvent.getEventType() == HyperlinkEvent.EventType.ENTERED) {
                this.html.setCursor(Cursor.getPredefinedCursor(12));
            } else if (hyperlinkEvent.getEventType() == HyperlinkEvent.EventType.EXITED) {
                this.html.setCursor(this.cursor);
            }
        }

        private void linkActivated(URL uRL) {
            this.html.setCursor(Cursor.getPredefinedCursor(3));
            SwingUtilities.invokeLater(new PageLoader(uRL));
            this.addToHistory(uRL);
            this.cleanForwardHistory();
            this.firePropertyChange("CheckButtons", false, true);
        }

        private void cleanForwardHistory() {
            for (int i = this.m_history.size() - 1; i > this.m_historyPosition; --i) {
                this.m_history.removeElementAt(i);
            }
        }

        public boolean backAllowed() {
            return this.m_historyPosition > 0;
        }

        public boolean forwardAllowed() {
            return this.m_history.size() - 1 > this.m_historyPosition;
        }

        private void loadURL(URL uRL) {
            this.html.setCursor(Cursor.getPredefinedCursor(3));
            SwingUtilities.invokeLater(new PageLoader(uRL));
        }

        private final class PageLoader
        implements Runnable {
            PageLoader(URL uRL) {
                HtmlPane.this.url = uRL;
            }

            /*
             * WARNING - Removed try catching itself - possible behaviour change.
             */
            public void run() {
                if (HtmlPane.this.url == null) {
                    HtmlPane.this.html.setCursor(HtmlPane.this.cursor);
                    HtmlPane.this.html.getParent().repaint();
                } else if (HtmlPane.this.url.getProtocol().startsWith("file") || HtmlPane.this.url.getProtocol().startsWith("zip") || HtmlPane.this.url.getProtocol().startsWith("jar") || HtmlPane.this.url.getProtocol().startsWith("systemresource")) {
                    Document document = HtmlPane.this.html.getDocument();
                    try {
                        HtmlPane.this.html.setPage(HtmlPane.this.url);
                    }
                    catch (IOException iOException) {
                        HtmlPane.this.html.setDocument(document);
                        HtmlPane.this.getToolkit().beep();
                    }
                    finally {
                        HtmlPane.this.url = null;
                        SwingUtilities.invokeLater(this);
                    }
                } else {
                    boolean bl = true;
                    bl = HtmlPane.this.url.getProtocol().toLowerCase().startsWith(HtmlPane.MAILTO) ? AbstractOS.getInstance().openEMail(HtmlPane.this.url.toString()) : AbstractOS.getInstance().openURL(HtmlPane.this.url);
                    if (!bl) {
                        HtmlPane.this.html.setCursor(HtmlPane.this.cursor);
                        JAPDialog.showMessageDialog((Component)HtmlPane.this.html.getParent(), JAPMessages.getString(JAPHelp.MSG_ERROR_EXT_URL), (JAPDialog.ILinkedInformation)new ExternalLinkedInformation(HtmlPane.this.url));
                    }
                    if (HtmlPane.this.m_historyPosition > 0) {
                        HtmlPane.this.m_historyPosition--;
                        HtmlPane.this.m_history.removeElementAt(HtmlPane.this.m_history.size() - 1);
                    }
                }
            }

            private class ExternalLinkedInformation
            extends JAPDialog.LinkedInformationAdapter {
                private URL m_url;

                public ExternalLinkedInformation(URL uRL) {
                    this.m_url = uRL;
                }

                public String getMessage() {
                    return this.m_url.toString();
                }

                public int getType() {
                    return 2;
                }

                public boolean isApplicationModalityForced() {
                    return true;
                }
            }
        }
    }

    private class HelpListener
    implements ActionListener,
    PropertyChangeListener {
        private HelpListener() {
        }

        public void actionPerformed(ActionEvent actionEvent) {
            if (actionEvent.getSource() == JAPInternalHelpViewer.this.m_comBoxLanguage && !JAPInternalHelpViewer.this.m_initializing) {
                JAPInternalHelpViewer.this.m_helpPath = "help/";
                JAPInternalHelpViewer.this.m_language = new LanguageMapper(JAPMessages.getString(JAPHelp.MSG_LANGUAGE_CODE + String.valueOf(JAPInternalHelpViewer.this.m_comBoxLanguage.getSelectedIndex() + 1)));
                JAPInternalHelpViewer.this.m_htmlpaneTheHelpPane.loadContext(JAPInternalHelpViewer.this.m_helpPath, JAPInternalHelpViewer.this.m_delegator.getHelpContext().getHelpContext(), JAPInternalHelpViewer.this.m_language);
            } else if (actionEvent.getSource() == JAPInternalHelpViewer.this.m_closeButton) {
                JAPInternalHelpViewer.this.closePressed();
            } else if (actionEvent.getSource() == JAPInternalHelpViewer.this.m_backButton) {
                JAPInternalHelpViewer.this.backPressed();
            } else if (actionEvent.getSource() == JAPInternalHelpViewer.this.m_forwardButton) {
                JAPInternalHelpViewer.this.forwardPressed();
            } else if (actionEvent.getSource() == JAPInternalHelpViewer.this.m_homeButton) {
                JAPInternalHelpViewer.this.homePressed();
            }
        }

        public void propertyChange(PropertyChangeEvent propertyChangeEvent) {
            if (propertyChangeEvent.getSource() == JAPInternalHelpViewer.this.m_htmlpaneTheHelpPane) {
                JAPInternalHelpViewer.this.checkNavigationButtons();
            }
        }
    }
}

