/*
 * Decompiled with CFR 0.150.
 */
package jap.pay;

import anon.platform.AbstractOS;
import anon.util.JAPMessages;
import anon.util.Util;
import gui.GUIUtils;
import gui.JAPHtmlMultiLineLabel;
import gui.LinkMouseListener;
import gui.dialog.JAPDialog;
import jap.JAPController;
import jap.JAPUtil;
import jap.pay.wizardnew.PaymentInfoPane;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;
import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import logging.LogHolder;
import logging.LogType;

public class ActivePaymentDetails
extends JAPDialog
implements ActionListener {
    private static final String MSG_HEADING = (class$jap$pay$ActivePaymentDetails == null ? (class$jap$pay$ActivePaymentDetails = ActivePaymentDetails.class$("jap.pay.ActivePaymentDetails")) : class$jap$pay$ActivePaymentDetails).getName() + "_heading";
    private static final String MSG_TITLE = (class$jap$pay$ActivePaymentDetails == null ? (class$jap$pay$ActivePaymentDetails = ActivePaymentDetails.class$("jap.pay.ActivePaymentDetails")) : class$jap$pay$ActivePaymentDetails).getName() + "_title";
    private static final String MSG_CLOSEBUTTON = (class$jap$pay$ActivePaymentDetails == null ? (class$jap$pay$ActivePaymentDetails = ActivePaymentDetails.class$("jap.pay.ActivePaymentDetails")) : class$jap$pay$ActivePaymentDetails).getName() + "_closebutton";
    public static final String MSG_COPYBUTTON = (class$jap$pay$ActivePaymentDetails == null ? (class$jap$pay$ActivePaymentDetails = ActivePaymentDetails.class$("jap.pay.ActivePaymentDetails")) : class$jap$pay$ActivePaymentDetails).getName() + "_copybutton";
    public static final String MSG_PAYBUTTON = (class$jap$pay$ActivePaymentDetails == null ? (class$jap$pay$ActivePaymentDetails = ActivePaymentDetails.class$("jap.pay.ActivePaymentDetails")) : class$jap$pay$ActivePaymentDetails).getName() + "_paybutton";
    private GridBagConstraints m_c;
    private JButton m_closeButton;
    private LinkMouseListener.ILinkCallback m_callback = new LinkMouseListener.ILinkCallback(){

        public void callback(URL uRL) {
            if (uRL == null) {
                return;
            }
            JAPController.getInstance().allowDirectProxyDomain(uRL);
        }
    };
    static /* synthetic */ Class class$jap$pay$ActivePaymentDetails;

    public ActivePaymentDetails(JAPDialog jAPDialog, Vector vector, String string, long l, String string2) {
        super(jAPDialog, JAPMessages.getString(MSG_TITLE));
        try {
            this.setDefaultCloseOperation(2);
            this.buildDialog(vector, string, l, string2);
            this.setResizable(false);
            this.pack();
            this.setVisible(true);
        }
        catch (Exception exception) {
            LogHolder.log(2, LogType.PAY, "Could not create ActivePaymentDetails: ", exception);
        }
    }

    private void buildDialog(Vector vector, String string, long l, String string2) {
        JComponent jComponent;
        this.m_c = new GridBagConstraints();
        this.m_c.anchor = 11;
        this.m_c.insets = new Insets(10, 30, 10, 30);
        this.m_c.gridx = 0;
        this.m_c.gridy = 0;
        this.m_c.weighty = 0.0;
        this.m_c.weightx = 0.0;
        this.getContentPane().setLayout(new GridBagLayout());
        JAPHtmlMultiLineLabel jAPHtmlMultiLineLabel = new JAPHtmlMultiLineLabel("<h3>" + JAPMessages.getString(MSG_HEADING) + "</h3>");
        this.getContentPane().add((Component)jAPHtmlMultiLineLabel, this.m_c);
        ++this.m_c.gridy;
        this.m_c.weightx = 0.0;
        JPanel jPanel = new JPanel();
        jPanel.setLayout(new GridBagLayout());
        Vector<JComponent> vector2 = new Vector<JComponent>();
        Object object = vector.elements();
        while (object.hasMoreElements()) {
            Hashtable hashtable = (Hashtable)object.nextElement();
            ++this.m_c.gridy;
            jComponent = this.buildOptionPanel(hashtable, string, l, string2);
            vector2.addElement(jComponent);
            jPanel.add((Component)jComponent, this.m_c);
        }
        object = GUIUtils.getMaxSize(vector2);
        GUIUtils.setEqualWidths(vector2, (Dimension)object);
        jComponent = new JScrollPane();
        ((JScrollPane)jComponent).setViewportView(jPanel);
        jComponent.setBorder(BorderFactory.createEmptyBorder());
        int n = new Double(((Dimension)object).width).intValue() + 80;
        int n2 = GUIUtils.getTotalSize(vector2).height + 80;
        Window window = GUIUtils.getParentWindow(this.getContentPane());
        int n3 = (int)Math.round((double)GUIUtils.getCurrentScreen(window).getHeight() * 0.8) - 100;
        int n4 = Math.min(n2, n3);
        jComponent.setPreferredSize(new Dimension(n, n4));
        this.getContentPane().add((Component)jComponent, this.m_c);
        jComponent.revalidate();
        this.m_closeButton = new JButton(JAPMessages.getString(MSG_CLOSEBUTTON));
        this.m_closeButton.addActionListener(this);
        ++this.m_c.gridy;
        this.getContentPane().add((Component)this.m_closeButton, this.m_c);
    }

    private JPanel buildOptionPanel(Hashtable hashtable, String string, long l, String string2) {
        JPanel jPanel = new JPanel();
        BoxLayout boxLayout = new BoxLayout(jPanel, 1);
        jPanel.setLayout(boxLayout);
        String string3 = (String)hashtable.get("heading");
        JAPHtmlMultiLineLabel jAPHtmlMultiLineLabel = new JAPHtmlMultiLineLabel("<b>" + string3 + "</b>");
        jPanel.add(jAPHtmlMultiLineLabel);
        jPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        String string4 = (String)hashtable.get("detailedInfo");
        JAPHtmlMultiLineLabel jAPHtmlMultiLineLabel2 = new JAPHtmlMultiLineLabel(string4);
        jAPHtmlMultiLineLabel2.setPreferredWidth(600);
        jAPHtmlMultiLineLabel2.setAlignmentX(0.0f);
        jPanel.add(jAPHtmlMultiLineLabel2);
        jPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        Enumeration enumeration = ((Vector)hashtable.get("extraInfos")).elements();
        while (enumeration.hasMoreElements()) {
            JPanel jPanel2;
            Object object;
            String string5;
            Object object2;
            String string6 = (String)enumeration.nextElement();
            boolean bl = true;
            try {
                new URL(string6);
            }
            catch (MalformedURLException malformedURLException) {
                bl = false;
            }
            if (bl) {
                JComponent jComponent;
                JComponent jComponent2;
                string6 = string6.toUpperCase().indexOf("PAYPAL") >= 0 ? PaymentInfoPane.createPaypalLink(string6, l, string2, string) : PaymentInfoPane.createPaysafecardLink(string6, l, string);
                object2 = string6;
                string5 = (String)hashtable.get("name");
                object = PaymentInfoPane.getMethodImageFilename(string5);
                ImageIcon imageIcon = null;
                if (object != null) {
                    imageIcon = GUIUtils.loadImageIcon((String)object, false, false);
                }
                if (imageIcon != null) {
                    jComponent2 = new JPanel();
                    jComponent2.setLayout(new BoxLayout(jComponent2, 0));
                    jComponent = new JLabel(imageIcon);
                    if (object2 != null) {
                        jComponent.addMouseListener(new LinkMouseListener((String)object2, this.m_callback));
                    }
                    jComponent2.add(jComponent);
                    jComponent2.setAlignmentX(0.0f);
                    jPanel.add(jComponent2);
                    jPanel.add(Box.createRigidArea(new Dimension(0, 5)));
                }
                jPanel2 = new JPanel();
                jComponent2 = new JButton(JAPMessages.getString(MSG_PAYBUTTON));
                ((AbstractButton)jComponent2).addActionListener(new ActionListener((String)object2){
                    private final /* synthetic */ String val$linkToUse;
                    {
                        this.val$linkToUse = string;
                    }

                    public void actionPerformed(ActionEvent actionEvent) {
                        ActivePaymentDetails.this.openURL(this.val$linkToUse);
                    }
                });
                jPanel2.add(jComponent2);
                jComponent = new JButton(JAPMessages.getString(MSG_COPYBUTTON));
                ((AbstractButton)jComponent).addActionListener(new ActionListener((String)object2){
                    private final /* synthetic */ String val$linkToUse;
                    {
                        this.val$linkToUse = string;
                    }

                    public void actionPerformed(ActionEvent actionEvent) {
                        ActivePaymentDetails.this.copyToClipboard(this.val$linkToUse, true);
                    }
                });
                jPanel2.add(jComponent);
                jPanel2.setAlignmentX(0.0f);
                jPanel.add(jPanel2);
            } else {
                string6 = Util.replaceAll(string6, "%t", string);
                string6 = Util.replaceAll(string6, "%a", JAPUtil.formatEuroCentValue(l, true));
                string6 = Util.replaceAll(string6, "%c", "");
                object2 = new JAPHtmlMultiLineLabel(string6);
                ((JComponent)object2).setAlignmentX(0.0f);
                jPanel.add((Component)object2);
                jPanel.add(Box.createRigidArea(new Dimension(0, 5)));
                string5 = string6;
                jPanel2 = new JPanel();
                object = new JButton(JAPMessages.getString(MSG_COPYBUTTON));
                ((AbstractButton)object).addActionListener(new ActionListener(){

                    public void actionPerformed(ActionEvent actionEvent) {
                        ActivePaymentDetails.this.copyToClipboard(string5, false);
                    }
                });
                jPanel2.add((Component)object);
                jPanel2.setAlignmentX(0.0f);
                jPanel.add(jPanel2);
            }
            jPanel.add(Box.createRigidArea(new Dimension(0, 5)));
            jPanel.setSize(jPanel.getPreferredSize().width, jPanel.getPreferredSize().height);
        }
        jPanel.setBorder(BorderFactory.createRaisedBevelBorder());
        return jPanel;
    }

    public void actionPerformed(ActionEvent actionEvent) {
        if (actionEvent.getSource() == this.m_closeButton) {
            this.setVisible(false);
        }
    }

    public void openURL(String string) {
        AbstractOS abstractOS = AbstractOS.getInstance();
        string = this.cleanupLink(string);
        try {
            URL uRL = new URL(string);
            this.m_callback.callback(uRL);
            abstractOS.openURL(uRL);
        }
        catch (MalformedURLException malformedURLException) {
            LogHolder.log(2, LogType.PAY, "Malformed URL");
        }
    }

    private void copyToClipboard(String string, boolean bl) {
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        if (bl) {
            string = this.cleanupLink(string);
            try {
                this.m_callback.callback(new URL(string));
            }
            catch (MalformedURLException malformedURLException) {}
        } else {
            string = this.cleanupText(string);
        }
        StringSelection stringSelection = new StringSelection(string);
        clipboard.setContents(stringSelection, null);
    }

    private String cleanupLink(String string) {
        string = Util.replaceAll(string, "<br>", "");
        string = Util.replaceAll(string, "<p>", "");
        string = Util.replaceAll(string, "<html>", " ");
        string = Util.replaceAll(string, "</html>", " ");
        string = Util.replaceAll(string, "&nbsp;", "%20");
        string = Util.replaceAll(string, " ", "%20");
        string = Util.replaceAll(string, "<font color=blue><u>", "");
        string = Util.replaceAll(string, "</u></font>", "");
        string = string.trim();
        return string;
    }

    private String cleanupText(String string) {
        string = Util.replaceAll(string, "<br>", "\n");
        string = Util.replaceAll(string, "<p>", "\n\n");
        string = Util.replaceAll(string, "&uuml;", "\u00fc");
        string = Util.replaceAll(string, "&Uuml;", "\u00dc");
        string = Util.replaceAll(string, "&auml;", "\u00e4");
        string = Util.replaceAll(string, "&Auml;", "\u00c4");
        string = Util.replaceAll(string, "&ouml;", "\u00f6");
        string = Util.replaceAll(string, "&Ouml;", "\u00d6");
        string = Util.replaceAll(string, "&szlig;", "\u00df");
        string = Util.replaceAll(string, "&nbsp;", " ");
        return string;
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

